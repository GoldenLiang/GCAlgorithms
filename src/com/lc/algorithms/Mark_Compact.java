package com.lc.algorithms;

import com.lc.model.GCRoots;
import com.lc.model.HeapObject;

/**
 * 标记-压缩算法
 * @author lc
 */
public class Mark_Compact {

	private HeapObject[] heap = new HeapObject[1000];	//堆

	/**
	 * 标记根及子对象
	 * @param roots
	 */
	public void mark_phase(GCRoots roots) {
		for(HeapObject r : roots.getReference()) {
			mark(r);
		}
	}
	
	/**
	 * 标记函数
	 * @param obj
	 */
	private void mark(HeapObject obj) {
		if(obj.mark == false) {
			obj.mark = true;
			System.out.println("------>已标记第 " + obj.position + " 个位置对象");
			if(obj.children != null) {
				for(HeapObject child : obj.children) {
					mark(child);
				}
			}
		}
		System.out.println("----->标记完成");
		System.out.println();
	}

	/**
	 * 设定 forwarding 指针 第一次搜索堆
	 */
	public void set_forwaring_ptr() {
		int scan = 0; //scan 指针
		int new_address = 0; //更新后的地址
		while(scan < heap.length) {
			if(heap[scan] != null) {
				if(heap[scan].mark == true) {
					adjust_ptr(scan, new_address);
					new_address += heap[scan].size;
				}
				scan += heap[scan].size;
			} else {
				scan++;
			}
		}
	}

	/**
	 * 更新指针 第二次搜索堆
	 * @param scan 
	 * @param new_address
	 */
	private void adjust_ptr(int scan, int new_address) {
		heap[scan].forwarding = new_address;
		System.out.println(scan + " 的新地址为 " + new_address);
		new_address += heap[scan].size;
	}
	
	/**
	 * 移动对象（严格上说是复制） 第三次搜索堆
	 */
	public void move_obj() {
		int scan = 0;
		while(scan < heap.length) {
			if(heap[scan] != null) {
				if(heap[scan].mark == true) {
					int new_address = heap[scan].forwarding;
					heap[new_address ] = heap[scan];
					heap[new_address].forwarding = Integer.MAX_VALUE;
					heap[new_address].mark = false;
					scan += heap[scan].size;
					heap[scan] = null;
					System.out.println("==========>将第 " + scan + " 个位置复制到第 " + 
							new_address + " 个位置");
				} else {
					//回收
					scan += heap[scan].size;
					heap[scan] = null;
					System.out.println("已回收第 " + scan + " 个位置");
				}
			} else {
				scan++;
			}
		}
	}

	public HeapObject[] getHeap() {
		return heap;
	}

}
