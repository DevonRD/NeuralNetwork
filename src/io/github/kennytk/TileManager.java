package io.github.kennytk;

import processing.core.PApplet;
import processing.core.PConstants;

public class TileManager implements IDrawable
{
	private PApplet p;

	private int horizontalNum;
	private int verticalNum;
	private int tileSize;

	private Tile[][] tiles;

	public TileManager(PApplet p, int horizontalNum, int verticalNum)
	{
		this.p = p;

		this.horizontalNum = horizontalNum;
		this.verticalNum = verticalNum;
		
		tiles = new Tile[horizontalNum][verticalNum];
		tileSize = 4 * Maths.scaleX(1080) / horizontalNum; // TODO: change
	}
	
	public void setup()
	{
		int count = 0;
		for(int x = 0; x < horizontalNum; x++)
		{
			for(int y = 0; y < verticalNum; y++)
			{
				count++;
				tiles[y][x] = new Tile(Maths.scaleX(50) + x * tileSize, Maths.scaleX(50) + y * tileSize, tileSize, count, x, y,
						water[x][y]);
			}
		}
	}

	public void draw()
	{
		for(int x = 0; x < horizontalNum; x++)
		{
			for(int y = 0; y < verticalNum; y++)
			{
				p.colorMode(PConstants.HSB);
				p.fill(tiles[y][x].getH(), tiles[y][x].getS(), tiles[y][x].getV());
				p.rect(tiles[y][x].getX(), tiles[y][x].getY(), tileSize, tileSize);
				p.fill(0, 0, 0);
			}
		}
	}
	
	public void update(double timeInterval)
	{
		for(int x = 0; x < horizontalNum; x++)
		{
			for(int y = 0; y < verticalNum; y++)
			{
				tiles[y][x].regen();
			}
		}
	}
}
