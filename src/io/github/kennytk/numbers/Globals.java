package io.github.kennytk.numbers;

public class Globals
{
	public static int realWidth;
	public static int realHeight;
	
	public static final float innerWidth = 1920;
	public static final float innerHeight = 1080;
	
	public static int menuBasePointX = 1440;
	public static double mutationFactor = .05;

	public static double dragRatio = 1;
	public static float scaleFactor = 1f;
	
	public static int buttonFillet = 6;

	public static enum TileType
	{
		GRASS, WATER, PLANT
	}

	public static MenuMode menuMode;
	public static float menuTextSize;
	public static float buttonTextSize;
	public static float menuTitleSize;

	public static enum MenuMode
	{
		MAIN, CREATURE, DATA, TILE;
	}
}
