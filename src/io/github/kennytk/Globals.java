package io.github.kennytk;

public class Globals
{
	public static int realWidth;
	public static int realHeight;
	public static int menuBasePointX = 1200;
	public static double mutationFactor = .05;

	public static double dragRatio = 1;
	public static double scaleFactor = 0.25;

	static enum TileType
	{
		GRASS, WATER, PLANT
	}

	public static MenuMode menuMode;

	static enum MenuMode
	{
		MAIN, CREATURE, DATA, TILE;
	}
}
