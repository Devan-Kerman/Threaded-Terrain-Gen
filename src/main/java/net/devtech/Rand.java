package net.devtech;


/**
 * static splittable random
 */
public class Rand {
	private static final long GOLDEN_GAMMA = 0x9e3779b97f4a7c15L;

	private Rand() {/*static class*/}

	public static int next(long[] seed) {
		long z = seed[0] += GOLDEN_GAMMA;
		z = (z ^ (z >>> 33)) * 0x62a9d9ed799705f5L;
		return (int) (((z ^ (z >>> 28)) * 0xcb24d0a5c88c35b3L) >>> 32);
	}

	public static long nextSeed(long seed) {
		return seed + GOLDEN_GAMMA;
	}

	public static int next(long seed) {
		long z = seed + GOLDEN_GAMMA;
		z = (z ^ (z >>> 33)) * 0x62a9d9ed799705f5L;
		return (int) (((z ^ (z >>> 28)) * 0xcb24d0a5c88c35b3L) >>> 32);
	}


	public static long seed(int x, int y) {
		return (long) x << 32 | y & 0xFFFFFFFFL;
	}
}
