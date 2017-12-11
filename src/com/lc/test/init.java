package com.lc.test;

import java.util.Random;

import org.junit.Test;

import com.lc.algorithms.Mark_Sweep;
import com.lc.model.GCRoots;
import com.lc.model.HeapObject;

public class init {

	@Test
	public void test_mark_sweep() {
		Mark_Sweep mark_sweep = new Mark_Sweep();
		GCRoots root = GCRoots.MethodAreaConstantReferenceObject.setReferenceNumber(2);
		Random random = new Random();
		root.getReference()[0] = new HeapObject(3, random.nextInt(1000));
		root.getReference()[1] = new HeapObject(2, random.nextInt(1000));
		mark_sweep.mark_phase(root);
		mark_sweep.getHeap()[root.getReference()[0].position] = root.getReference()[0];
		mark_sweep.getHeap()[root.getReference()[1].position] = root.getReference()[1];
		mark_sweep.sweep_phase();
	}
	
}
