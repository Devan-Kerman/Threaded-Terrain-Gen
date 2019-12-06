package net.devtech;

import net.devtech.terrain.SkeletonKernel;

public class BasicKernel extends SkeletonKernel {

	public BasicKernel(int power, int regionSize) {
		super(power, regionSize);
	}

	@Override
	protected int compute(int biome_id, int x, int y) {
		return biome_id & 1;
	}

	@Override
	protected float heightPostProcess(int x, int y, int biomeIndex, float elevation) {
		if(biomeIndex == 0)
			return elevation*.25f;
		return elevation;
	}
}
