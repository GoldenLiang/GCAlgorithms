package com.lc.algorithms;

import com.lc.Exception.AllocationException;
import com.lc.model.GCRoots;
import com.lc.model.HeapObject;

/**
 * 分代垃圾回收
 * @author lc
 */
public class GenerationalGC {

	private static final int AGE_MAX = 4;	//晋升年龄
	private HeapObject[] heap = new HeapObject[1000];	//堆
	int old_start = 333; 	//老年代起点，比例设为2：1
	int new_start = 0;	//生成空间的起点
	int survivor1_start = 266;	//survivor1 的起点
	int survivor2_start  = 300;	//survivor2 的起点
	int from_survivor_start = survivor1_start; 	//from_survivor 的起点
	int to_survivor_start = survivor2_start; 	//to_survivor 的起点
	HeapObject[] remebered_set = new HeapObject[100]; 	//记录集
	int index = 0;	//记录集下标
	public int new_free = new_start; //执行生成空间分块开头的指针
	int old_free = old_start; //老年代空间的空闲指针
	int to_survivor_free = survivor1_start; //幸存空间的空闲空间起点
	boolean has_new_obj;	//复制后的对象是否还在新生代空间
	private GCRoots roots;	//根
	
	/**
	 * 写入屏障，用于往记录集里写入对象
	 * @param obj 发出引用的对象
	 * @param new_obj 指针更新后成为引用目标的对象
	 */
	public void write_barrier(HeapObject obj, HeapObject new_obj) {
		//检查三点：
		//1.发出引用的对象是不是老年代对象
		//2.指针更新后的目标对象是不是新生代对象
		//3.发出引用的对象是否还没被标记过
		//满足这三条，即可被记录到记录集里
		if(obj.position > old_start && new_obj.position < old_start && obj.remebered == false) {
			remebered_set[index] = obj;
			index++;
			obj.remebered = true;
			System.out.println("第 " + obj.position + " 个对象已被记录到记录集中。");
			
			obj.forwarding = new_obj.position;
			obj.forwarded = true;
		}
	}
	
	/**
	 * 分配函数
	 * @param size
	 * @return 
	 * @throws AllocationException 
	 */
	public HeapObject new_obj(int size) throws AllocationException {
		if(new_free  + size >= survivor1_start) {
			//如果没有足够大小的分块，执行 minor_gc()
			minor_gc();
			if(new_free  + size >= survivor1_start) {
				try {
					allocation_fail();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			HeapObject obj = new HeapObject(size, new_free);
			new_free += size;
			obj.age = 0;
			obj.remebered = false;
			return obj;
		}
		return null;
	}

	/**
	 * 分配失败函数
	 * @throws AllocationException 
	 */
	private void allocation_fail() throws AllocationException {
		throw new AllocationException("分配分块失败");
	}

	/**
	 * 新生代 GC
	 * @throws AllocationException
	 */
	private void minor_gc() throws AllocationException {
		System.out.println("=========新生代垃圾回收启动=========");
		for(int i = 0; i < roots.getReference().length; i++) {
			//从根复制能找到的新生代对象
			if(roots.getReference()[i].position < old_start) {
				copy(roots.getReference()[i]);
			}
		}
		
		int i = 0; 
		while(i < index) {
			for(HeapObject child : remebered_set) {
				if(child.position < old_start) {
					copy(child);
				}
				if(child.position < old_start) {
					has_new_obj = true;
				}
			}
			
			//如果对象已经不再新生代空间里了
			if(has_new_obj == false) {
				remebered_set[i].remebered = false;
				index--;
			} else {
				i++;
			}
		}
		
		swap(from_survivor_start, to_survivor_start);
		System.out.println("=========新生代垃圾回收结束=========");
	}
	
	/**
	 * 交换 from_survivor 空间与 to_survivor 空间
	 * @param from_survivor_start
	 * @param to_survivor_start
	 */
	private void swap(int from_survivor_start, int to_survivor_start) {
		int temp = to_survivor_start;
		to_survivor_start = from_survivor_start;
		from_survivor_start = temp;
		System.out.println("------->交换from_survivor 与 to_survivor 完成");
	}

	/**
	 * 复制函数
	 * @param obj
	 * @return 复制的对象地址
	 * @throws AllocationException 
	 */
	private int copy(HeapObject obj) throws AllocationException {
		if(obj.forwarded == false) {
			//如果复制对象还年轻
			if(obj.age < AGE_MAX) {
				copy_data(to_survivor_free, obj, obj.size);
				obj.forwarding = to_survivor_free;
				obj.forwarded = true;
				heap[to_survivor_free].age++;
				to_survivor_free += heap[to_survivor_free].size;
				new_free -= obj.size;
						
				//复制子对象
				if(obj.children != null) {
					for(HeapObject child : obj.children) {
						copy(child);
					}
				}
			} else {
				//如果达到了晋升年龄
				promote(obj);
			}
		}
		return obj.forwarding;
	}

	/**
	 * @param to_survivor_free 
	 * @param obj 
	 * @param size 
	 */
	private void copy_data(int to_survivor_free, HeapObject obj, int size) {
		heap[to_survivor_free] = (HeapObject) obj.clone();
		to_survivor_free += obj.size;
		obj.COPIED = true;
		System.out.println("第 " + obj.position + " 号对象已复制完成");
	}
	
	/**
	 * 晋升函数
	 * @param obj
	 * @throws AllocationException
	 */
	private void promote(HeapObject obj) throws AllocationException {
		HeapObject new_obj = allocate_in_old(obj);
		if(new_obj == null) {
			major_gc(roots);
			new_obj = allocate_in_old(obj);
			if(new_obj == null) {
				allocation_fail();
			}	
		}
		
		obj.forwarded = true;
		obj.forwarding = new_obj.position;
		
		for(HeapObject child : obj.children) {
			if(child.position < old_start) {
				remebered_set[index] = new_obj;
				index++;
				new_obj.remebered = true;
			}
		}
	}
	
	/**
	 * 将对象分配到老年代
	 * @param obj
	 * @return
	 */
	private HeapObject allocate_in_old(HeapObject obj) {
		int pos = obj.position;
		heap[old_free] = obj;
		obj.position = old_free;
		old_free += obj.size;
		System.out.println("将 " + pos + " 位置上的对象分配到了 " + obj.position + " 位置上");
		return heap[obj.position];
	}
	
	/**
	 * 老年代 GC
	 * @param roots 
	 */
	private void major_gc(GCRoots roots) {
		//老年代垃圾回收使用 标记-清除
		Mark_Sweep mark_sweep = new Mark_Sweep();
		mark_sweep.mark_phase(roots);
		mark_sweep.sweep_phase();
	}

	public void setRoots(GCRoots roots) {
		this.roots = roots;
	}

	public HeapObject[] getHeap() {
		return heap;
	}
}
