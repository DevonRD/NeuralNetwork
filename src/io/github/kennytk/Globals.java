package io.github.kennytk;

public class Globals
{
	public static int realWidth;
	public static int realHeight;
	public static int menuBasePointX = 1200;
	public static double mutationFactor = .05;
	

	static enum TileType
	{
		GRASS, WATER, PLANT
	}

	static enum MenuMode
	{
		MAIN, CREATURE, DATA, TILE;
	}
}
