package net.devtech.biome;

import com.aparapi.Kernel;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static net.devtech.Rand.*;

public abstract class BiomeGen extends Kernel {
	protected final int power;
	protected final int size;
	protected final int mod;
	protected final int[] biomeMap;
	protected final int regionSize;

	protected int currX;
	protected int currY;

	public BiomeGen(int power, int regionSize) {
		this.power = power;
		this.size = 1 << power;
		this.mod = size - 1;
		this.regionSize = regionSize;
		biomeMap = new int[regionSize * regionSize];
	}

	private int computeBiome(int x, int y) {
		int rx = x >> power;
		int ry = y >> power;

		int current = distance(rx, ry, x, y);
		int next = 0;

		int offsetx = 0;
		int offsety = 0;
		next = distance(rx - 1, ry, x, y);
		if (next < current) {
			offsetx = -1;
			offsety = 0;
			current = next;
		}
		next = distance(rx, ry + 1, x, y);
		if (next < current) {
			offsetx = 0;
			offsety = 1;
			current = next;
		}
		next = distance(rx + 1, ry, x, y);
		if (next < current) {
			offsetx = 1;
			offsety = 0;
			current = next;
		}
		next = distance(rx, ry - 1, x, y);
		if (next < current) {
			offsetx = 0;
			offsety = -1;
			current = next;
		}
		next = distance(rx - 1, ry - 1, x, y);
		if (next < current) {
			offsetx = -1;
			offsety = -1;
			current = next;
		}
		next = distance(rx + 1, ry + 1, x, y);
		if (next < current) {
			offsetx = 1;
			offsety = 1;
			current = next;
		}
		next = distance(rx + 1, ry - 1, x, y);
		if (next < current) {
			offsetx = 1;
			offsety = -1;
			current = next;
		}
		if (distance(rx - 1, ry + 1, x, y) < current) {
			offsetx = -1;
			offsety = 1;
		}

		return next(seed(rx + offsetx, ry + offsety));
	}

	private int distance(int rx, int ry, int x, int y) {
		long arr = seed(rx, ry);
		int ox = rx * size + (next(arr) & mod) - x;
		int oy = ry * size + (next(nextSeed(arr)) & mod) - y;
		return ox * ox + oy * oy;
	}

	protected int biome(int index, int x, int y) {
		return biomeMap[index] = computeBiome(currX + x, currY + y);
	}

	public void set(int x, int y) {
		currX = x;
		currY = y;
	}

	public int[] getBiomeMap() {
		return biomeMap;
	}

	public int[] genCPU(int x, int y) {
		for (int i = 0; i < regionSize; i++)
			for (int i1 = 0; i1 < regionSize; i1++)
				biomeMap[i * regionSize + i1] = computeBiome(x + i, y + i1);
		return biomeMap;
	}

	public int[] genGPU(int x, int y) {
		currX = x;
		currY = y;
		execute(regionSize*regionSize);
		return biomeMap;
	}

	public static void main(String[] args) throws IOException {
		final int size = 1024;
		BiomeGen gen = null; //new SkeletonKernel(7, size);
		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR);
		int[] map = gen.genGPU(size, size);
		for (int i = 0; i < size; i++)
			for (int i1 = 0; i1 < size; i1++)
				image.setRGB(i, i1, new Color(map[i * size + i1] & 255, map[i * size + i1] & 255, map[i * size + i1] & 255, ((map[i * size + i1] & 255) == 0) ? 0 : 255).getRGB());

		File temp = File.createTempFile("bruh", ".png");
		ImageIO.write(image, "png", temp);
		Desktop.getDesktop().open(temp);
	}

	public static void openBiome(int size, int[] map) throws IOException {
		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR);
		for (int i = 0; i < size; i++)
			for (int i1 = 0; i1 < size; i1++)
				image.setRGB(i, i1, new Color(map[i * size + i1] & 255, map[i * size + i1] & 255, map[i * size + i1] & 255, ((map[i * size + i1] & 255) == 0) ? 0 : 255).getRGB());

		File temp = File.createTempFile("bruh", ".png");
		ImageIO.write(image, "png", temp);
		Desktop.getDesktop().open(temp);
	}
}
