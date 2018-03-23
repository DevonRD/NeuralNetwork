package io.github.kennytk;

import io.github.kennytk.Globals.TileType;
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
			food = 50 + (int) (Math.random() * 50);

		if(type == TileType.WATER)
		{
			colorH = 155;
		}
		else
		{
			colorH = (int) food;
		}

		colorS = 100;
		colorV = 100;

		if(type == TileType.WATER)
			regenValue = 0;
		else
			regenValue = Math.random() * 0.01;

		if(regenValue < 0.001 && type == TileType.GRASS)
			regenValue = 0.001;

		this.id = id;
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
