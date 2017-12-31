package com.lc.model;

/**
 * 堆里的对象
 * @author lc
 */
public class HeapObject implements Cloneable {

	/**
	 * 复制是否完成
	 */
	public boolean COPIED = false;

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

	/**
	 * forwarding 指针
	 */
	public int forwarding = Integer.MAX_VALUE;

	/**
	 * 分代回收算法中，是否被记录到记录集
	 */
	public boolean remebered;
	
	/**
	 * 分代回收算法中，对象的年龄
	 */
	public int age;

	/**
	 * 分代回收算法中，对象是否有指向对象
	 */
	public boolean forwarded;
	
	public HeapObject(int size, int position) {
		this.size = size;
		this.position = position;
	}
	
	@Override
	public Object clone() {
		HeapObject obj = null;
		try {
			obj = (HeapObject) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return obj;
	}
}
