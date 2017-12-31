package com.lc.algorithms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.lc.Exception.AllocationException;
import com.lc.model.GCRoots;
import com.lc.model.HeapObject;

/**
 * 标记-清除算法
 * @author lc
 */
public class Mark_Sweep {

	private List<HeapObject> freeList = new ArrayList<>(); //空闲列表，用于找出清楚对象后的分块
	private HeapObject[] heap = new HeapObject[1000];	//堆

	/**
	 * 标记函数
	 */
	public void mark_phase(GCRoots roots) {
		//首先标记根指向的对象
		for(HeapObject r : roots.getReference())
			mark(r);
	}
	
	private void mark(HeapObject obj) {
		if(obj.mark == false) {
			obj.mark = true;
			System.out.println("==》已标记第 " + obj.position + " 个位置。");
			if(obj.children != null) {
				//递归标记子对象
				for(HeapObject child : obj.children)
					mark(child);
			}
		}
		System.out.println("=====标记完成=====");
	}
	
	
	/**
	 * 清除函数
	 */
	public void sweep_phase() {
		int sweeping = 0;
		while(sweeping < heap.length - 1) {
			HeapObject current = heap[sweeping];
			if(current != null && current.mark == true) {
				//如果是活动对象，将活动对象标志位重置为 false
				(heap[sweeping]).mark = false;
			} else {
				//如果分块连续，合并两个分块
				if(current != null) {
					//如果是非活动对象，添加至空闲列表
					freeList.add(heap[sweeping]);
					System.out.println("==》已清除第 " + current.position + " 个位置。");
					current = new HeapObject(current.size, current.position);
				}
			}
			sweeping++;
		}
		System.out.println("=====清除完成=====");
	}
	
	/**
	 * 分配函数
	 * @param size 
	 * @return
	 * @throws AllocationException 
	 */
	public HeapObject new_obj(int size) throws AllocationException {
		HeapObject chunk = pickup_chunk(size, freeList);
		if(chunk != null) {
			return chunk;
		} else {
			allocation_fail();
			throw new RuntimeException("分配分块失败");
		}
	}
	
	/**
	 * 使用 best-fit 找到第一个适合的分块
	 * @param size 需要大小
	 * @param freeList 空闲链表
	 * @return 
	 */
	public HeapObject pickup_chunk(int size, List<HeapObject> freeList) {
		Iterator<HeapObject> it = freeList.iterator();
		HeapObject freeObject = it.next();
		while(freeObject != null) {
			if(it.next().size >= size) {
				return it.next();
			}
			freeObject = it.next();
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
	 * 传入对象是否是垃圾
	 */
	public boolean isGarbage(Object obj) {
		for(GCRoots root : GCRoots.values()) {
			if(obj == root)
				return false;
			
			//递归遍历子对象
			for(HeapObject child : root.getReference()) {
				if(obj == child)
					return true;
			}
		}
		return true;
	}
	
	public HeapObject[] getHeap() {
		return heap;
	}
}
