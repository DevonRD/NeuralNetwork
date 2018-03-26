package io.github.kennytk.tile;

import io.github.kennytk.IDrawable;
import io.github.kennytk.numbers.Globals.TileType;
import processing.core.PApplet;

public class Tile implements IDrawable
{
	// TODO: add dormant boolean
	private PApplet p;

	private int x;
	private int y;

	private int xIndex;
	private int yIndex;

	private int tileSize;

	private int maxFood;
	private double food;

	private int colorH;
	private int colorS;
	private int colorV;

	private double regenValue;

	private int id;

	private TileType type;

	public Tile(PApplet p, int x, int y, int tileSize, int id, int xIndex, int yIndex)
	{
		this.p = p;

		// make water tiles extend tile
		// make grass tiles extend tile
		// bush/tree
		type = Map.getMapData()[xIndex][yIndex];

		this.x = x;
		this.y = y;

		this.xIndex = xIndex;
		this.yIndex = yIndex;

		this.tileSize = tileSize;

		// TODO: combine statements
		if(type == TileType.WATER)
			maxFood = 0;
		else
			maxFood = 100;

		if(type == TileType.GRASS)
			food = 50 + p.random(0,50);

		if(type == TileType.WATER)
		{
			colorH = (int) (226.0 / 360.0 * 255.0);
			colorS = (int) (78.0 / 100.0 * 255.0);
			colorV = (int) (57.0 / 100.0 * 255.0);
		}
		else
		{
			colorH = (int) (food / 1.5 + 30);
			colorS = 120;
			colorV = 120;
		}

		if(type == TileType.WATER)
			regenValue = 0;
		else
			regenValue = Math.random() * 0.01;

		if(regenValue < 0.001 && type == TileType.GRASS)
			regenValue = 0.001;

		this.id = id;
	}

	/*
	 * null tile to be used exclusively inside the getTileFromPixels
	 * method so that eating doesn't throw a null pointer exception
	 * when creatures eat outside of the board
	 */
	public Tile(PApplet p, int tileSize)
	{
		this.p = p;

		type = TileType.WATER;

		this.x = -1;
		this.y = -1;

		this.xIndex = -1;
		this.yIndex = -1;

		this.tileSize = tileSize;

		food = 0;
		maxFood = 0;
		colorH = 155;
		colorS = 100;
		colorV = 100;
		regenValue = 0;

		this.id = -1;
	}

	public void draw()
	{
	}

	public void regen()
	{
		if(type == TileType.WATER)
			return;

		food += regenValue;

		if(food > maxFood)
			food = maxFood;

		colorH = (int) food;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public int getXIndex()
	{
		return xIndex;
	}

	public int getYIndex()
	{
		return yIndex;
	}

	public double getRegenValue()
	{
		return regenValue;
	}

	public int getH()
	{
		return colorH;
	}

	public int getS()
	{
		return colorS;
	}

	public int getV()
	{
		return colorV;
	}

	public int getID()
	{
		return id;
	}

	public int getTileSize()
	{
		return tileSize;
	}

	public double getFood()
	{
		return food;
	}

	public void setFood(double food)
	{
		this.food = food;
	}

}
