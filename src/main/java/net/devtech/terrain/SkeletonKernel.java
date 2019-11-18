package net.devtech.terrain;

import net.devtech.biome.BiomeGen;

public abstract class SkeletonKernel extends BiomeGen {
	private float[] elevation;

	public SkeletonKernel(int power, int regionSize) {
		super(power, regionSize);
		for (int i = 0; i < 512; i++) {
			permMod12[i] = (short) (perm[i] % 12);
		}

		elevation = new float[regionSize * regionSize];
	}

	@Override
	public void run() {
		int i = getGlobalId();
		int x = i % regionSize;
		int y = i / regionSize;

		int biome_id = biome(i, x, y);
		biome_id = getBiomeIndex(biome_id, x, y);
		float terrain = terrain(x, y, 6, .003f, .5f);
		terrain = heightPostProcess(x, y, biome_id, terrain);
		elevation[i] = terrain;
	}

	/**
	 * convert a random biome id into a processable biome id
	 * @param biome_id a random int
	 * @param x
	 * @param y
	 * @return
	 */
	protected abstract int getBiomeIndex(int biome_id, int x, int y);

	protected abstract float heightPostProcess(int x, int y, int biomeIndex, float elevation);

	private float terrain(int x, int y, int octaves, float frequency, float persistence) {
		float accumulator = 0;

		for (int j = 0; j < octaves; j++) {
			float power = pow(2, j);
			float newFrequency = frequency * power;

			accumulator += noise(currX * power + newFrequency * x, currY * power + newFrequency * y, generateWeights(octaves, persistence), j);
		}

		return accumulator;
	}

	private float noise(float xin, float yin, float[] weights, int index) {
		float n0 = 0f, n1 = 0f, n2 = 0f;
		int i1 = 0, j1 = 0;
		float s = (xin + yin) * 0.3660254037844386f;
		int i = fastfloor(xin + s);
		int j = fastfloor(yin + s);
		float t = (i + j) * 0.21132486540518713f;
		float X0 = i - t;
		float Y0 = j - t;
		float x0 = xin - X0;
		float y0 = yin - Y0;

		if (x0 > y0) {
			i1 = 1;
			j1 = 0;
		} else {
			i1 = 0;
			j1 = 1;
		}
		float x1 = x0 - i1 + 0.21132486540518713f;
		float y1 = y0 - j1 + 0.21132486540518713f;
		float x2 = x0 - 1.0f + 2.0f * 0.21132486540518713f;
		float y2 = y0 - 1.0f + 2.0f * 0.21132486540518713f;
		int ii = i & 255;
		int jj = j & 255;
		int gi0 = permMod12[ii + intNoise(jj)];
		int gi1 = permMod12[ii + i1 + intNoise(jj + j1)];
		int gi2 = permMod12[ii + 1 + intNoise(jj + 1)];
		float t0 = 0.5f - x0 * x0 - y0 * y0;
		if (t0 < 0) n0 = 0.0f;
		else {
			t0 *= t0;
			n0 = t0 * t0 * dot(grad3[gi0 * 2], grad3[gi0 * 2 + 1], x0, y0);
		}
		float t1 = 0.5f - x1 * x1 - y1 * y1;
		if (t1 < 0) n1 = 0.0f;
		else {
			t1 *= t1;
			n1 = t1 * t1 * dot(grad3[gi1 * 2], grad3[gi1 * 2 + 1], x1, y1);
		}
		float t2 = 0.5f - x2 * x2 - y2 * y2;
		if (t2 < 0) n2 = 0.0f;
		else {
			t2 *= t2;
			n2 = t2 * t2 * dot(grad3[gi2 * 2], grad3[gi2 * 2 + 1], x2, y2);
		}
		return 70.0f * (n0 + n1 + n2) * weights[index];
	}

	private int intNoise(int n) {
		n = ((n + 463856334) >> 13) ^ (n + 575656768);
		return ((n * (n * n * 60493 + 19990303) + 1376312589) & 0x7fffffff & 255);
	}

	private float grad3[] = {1, 1, -1, 1, 1, -1, -1, -1, 1, 0, -1, 0, 1, 0, -1, 0, 0, 1, 0, -1, 0, 1, 0, -1};

	private short permMod12[] = new short[512];

	private int fastfloor(float x) {
		int xi = (int) x;
		return x < xi ? xi - 1 : xi;
	}

	private float dot(float gx, float gy, float x, float y) {
		return gx * x + gy * y;
	}

	private static short perm[] = {151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180, 151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180};

	private static float[] generateWeights(int octaves, double persistence) {
		float[] weights = new float[octaves];
		float totalWeight = 0;
		float lastWeight = 1;
		for (int i = 0; i < octaves; i++) {
			totalWeight += lastWeight;
			weights[i] = lastWeight;
			lastWeight *= persistence;
		}
		getBiomeIndex(weights, totalWeight);
		return weights;
	}

	private static void getBiomeIndex(float[] weights, float totalWeight) {
		for (int i = 0; i < weights.length; i++) {
			weights[i] = weights[i] / totalWeight;
		}
	}

	public float[] getElevation() {
		return elevation;
	}
}
