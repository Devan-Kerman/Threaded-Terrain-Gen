package net.devtech.benchmarking;

import ee.jjanno.libjsimplex.noise.gpu.SimplexNoiseGpu2D;
import net.devtech.biome.BiomeGen;
import java.util.function.IntConsumer;

public class Bench {
	public static void benchmark(IntConsumer runnable) {
		System.out.println("=== Warmup ==="); // should be enough iterations for T3 compilation
		long time = iter(runnable, 10_000);
		System.out.printf("%dns (%dns/op)\n", time, time / 10_000);
		System.out.println("=== Bench ===");
		time = iter(runnable, 10_000);
		System.out.printf("%dns (%dns/op)\n", time, time / 10_000);
	}

	private static long iter(IntConsumer runnable, int iter) {
		long start = System.nanoTime();
		for (int i = 0; i < iter; i++)
			runnable.accept(i);
		return System.nanoTime() - start;
	}

	public static void main(String[] args) {
		BiomeGen gen = new BiomeGen(7, 100);
		System.out.println("=== CPU biome generation ===");
		benchmark(i -> gen.genCPU(100, 100));
		System.out.println("=== GPU biome generation ===");
		benchmark(i -> gen.genGPU(100, 100));
		System.out.println("=== GPU terrain generation ===");
		SimplexNoiseGpu2D.calculate(0, 0, 100, 100, 10);

		//System.out.println(Arrays.toString(gen.getMap()));
		//benchmark(i -> gen.getBiome(i, i));
	}
}
//204749ns
//335604ns