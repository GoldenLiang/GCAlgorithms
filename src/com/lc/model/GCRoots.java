package com.lc.model;

/**
 * 可做为根的对象
 * @author lc
 */
public enum GCRoots {

	VMStackReferenceObject,	//虚拟机栈变量引用对象
	MethodAreaStaticReferenceObject, //方法区静态变量引用对象
	MethodAreaConstantReferenceObject, //方法区常量引用对象
	NaticeMethodReferenceObject;	//本地方法引用对象
	
	public int referenceNumber; //引用对象个数
	private HeapObject[] reference; //引用对象

	public GCRoots setReferenceNumber(int referenceNumber) {
		this.referenceNumber = referenceNumber;
		return this;
	}
	
	/**
	 * 使用单例确保正确初始化
	 * @return
	 */
	public HeapObject[] getReference() {
		if(reference == null)
			reference = new HeapObject[referenceNumber];
		return reference;
	}
}
