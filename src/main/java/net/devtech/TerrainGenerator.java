package net.devtech;

import ee.jjanno.libjsimplex.generator.NoiseSurface;
import ee.jjanno.libjsimplex.util.colorizer.ColorMapper;
import net.devtech.biome.BiomeGen;
import net.devtech.terrain.SkeletonKernel;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class TerrainGenerator {
	public static void main(String[] args) throws IOException {
		final int width = 100;
		ColorMapper mapper = new ColorMapper();
		for (int i = 0; i < 20; i++) {
			mapper.addRange(-1 + i * 0.1f, -1 + i * 0.1f + 0.09f, new Color(0, 0, 0), new Color(127, 127, 127), 1);
			mapper.addRange(-1 + i * 0.1f, -1 + i * 0.1f + 0.1f, new Color(127, 127, 127), new Color(255, 255, 255), 1);
		}

		SkeletonKernel kernel = new SkeletonKernel(5, width) {
			@Override
			protected int getBiomeIndex(int biome_id, int x, int y) {
				return biome_id & 1;
			}

			@Override
			protected float heightPostProcess(int x, int y, int biomeIndex, float elevation) {
				if(biomeIndex == 0)
					return elevation*.25f;
				return elevation;
			}
		};

		kernel.set(0, 0);
		kernel.execute(width*width);

		BiomeGen.openBiome(width, kernel.getBiomeMap());
		File temp = File.createTempFile("bruh", ".png");
		ImageIO.write(mapper.getBufferedImage(kernel.getElevation(), width, width), "png", temp);
		Desktop.getDesktop().open(temp);
	}
}
