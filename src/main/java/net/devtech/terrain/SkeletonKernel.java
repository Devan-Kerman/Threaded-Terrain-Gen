package net.devtech.terrain;

import net.devtech.biome.BiomeGen;

import static ee.jjanno.libjsimplex.noise.cpu.SimplexNoiseCpu.noise;

public abstract class SkeletonKernel extends BiomeGen {
	private final float[] elevation;

	public SkeletonKernel(int power, int regionSize) {
		super(power, regionSize);
		elevation = new float[regionSize * regionSize];
	}

	@Override
	public void run() {
		int i = getGlobalId();
		int x = i % regionSize;
		int y = i / regionSize;

		int biome_id = biome(i, x, y);
		biome_id = compute(biome_id, x, y);
		float terrain = terrain(x, y);
		terrain = heightPostProcess(x, y, biome_id, terrain);
		elevation[i] = terrain;
	}


	protected abstract int compute(int biome_id, int x, int y);

	protected abstract float heightPostProcess(int x, int y, int biomeIndex, float elevation);

	private float terrain(int x, int y) {
		float accumulator = 0;
		for (int j = 0; j < 6; j++) {
			float power = pow(2, j);
			float newFrequency = (float) 0.003 * power;

			accumulator += noise(currX * power + newFrequency * x, currY * power + newFrequency * y);
		}

		return accumulator;
	}

	public float[] getElevation() {
		return elevation;
	}
}
