package io.github.kennytk.tile;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import io.github.kennytk.Run;
import io.github.kennytk.numbers.Globals.TileType;

public class Map
{
	private static BufferedImage image;
	private static TileType[][] mapData;
	
	public Map(String selectedMap, String fileExt)
	{
		try
		{
			image = ImageIO.read(Run.class.getResource(selectedMap + fileExt));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

		mapData = new TileType[image.getWidth()][image.getHeight()];

		for(int row = 0; row < image.getWidth(); row++)
		{
			for(int col = 0; col < image.getHeight(); col++)
			{
				Color c = new Color(image.getRGB(row, col));

				if(c.getGreen() > 50)
				{
					mapData[row][col] = TileType.GRASS;
				}
				else if(c.getBlue() > 50)
				{
					mapData[row][col] = TileType.WATER;
				}
				else
				{
					mapData[row][col] = TileType.WATER;
				}
			}
		}

	}

	public void setup()
	{
	}
	
	public static TileType[][] getMapData()
	{
		return mapData;
	}
}
