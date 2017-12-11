package com.lc.model;

/**
 * 堆里的对象
 * @author lc
 */
public class HeapObject {

	/**
	 * 占用堆的大小
	 */
	public int size;
	
	/**
	 * 在堆中的位置
	 */
	public int position;
	
	/**
	 * 标志位
	 */
	public boolean mark;

	/**
	 * 子对象
	 */
	public HeapObject[] children;
	
	public HeapObject(int size, int position) {
		this.size = size;
		this.position = position;
	}
	
}
