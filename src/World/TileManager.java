package World;

import Utilities.Menu;
import Utilities.Variables;
import processing.core.PApplet;

public class TileManager
{
	int appWidth, appHeight;
	
	public static Tile[][] tiles;
	boolean[][] water;
	public static int tileResL;
	public static int tileResW;
	public static int tileSize;
	
	
	
	
	
	public TileManager(PApplet p, boolean[][] water, int width, int height)
	{
		tileResL = Variables.MAP_DIMENSIONS;
		tileResW = Variables.MAP_DIMENSIONS;
		
		appWidth = width;
		appHeight = height;
		
		tiles = new Tile[tileResW][tileResL];
		tileSize = 4 * p2pw(1500) / tileResW;
		
		this.water = water;
		
		startTiles();
	}
	
	public static void iterate(double timeInterval)
	{
		for(int x = 0; x < tileResL; x++)
		{
			for(int y = 0; y < tileResW; y++)
			{
				tiles[y][x].regenerateTileFood();
			}
		}
	}
	
	public void startTiles()
	{
		int count = 0;
		for(int x = 0; x < tileResL; x++)
		{
			for(int y = 0; y < tileResW; y++)
			{
				count++;
				tiles[y][x] = new Tile(x * tileSize, y * tileSize, tileSize, count, x, y, water[x][y]);
			}
		}
	}
	
	public static int[] findTileAt(double xCoord, double yCoord)
	{
		int[] spot = new int[2];
		spot[0] = ((int) yCoord) / tileSize;
		spot[1] = ((int) xCoord) / tileSize;
		if(spot[0] > tileResW - 1) spot[0] = tileResW - 1;
		if(spot[1] > tileResL - 1) spot[1] = tileResL - 1;
		if(spot[0] < 0) spot[0] = 0;
		if(spot[1] < 0) spot[1] = 0;
		
		return spot;
	}
	
	public Tile findTileAt(double xCoord, double yCoord, boolean whatever)
	{
		int spotY = ((int) yCoord) / tileSize;
		int spotX = ((int) xCoord) / tileSize;
		if(spotY > tileResW - 1) spotY = 0;
		if(spotX > tileResL - 1) spotX = 0;
		spotY = Math.max(0, spotY);
		spotX = Math.max(0, spotX);
		
		return tiles[spotY][spotX];
	}
	
	public static Tile checkTileClick(int mX, int mY)
	{
		for(int x = 0; x < tiles.length; x++)
		{
			for(int y = 0; y < tiles.length; y++)
			{
				if (tiles[y][x].x < mX && mX <= tiles[y][x].x + tileSize)
				{
					if (tiles[y][x].y < mY && mY <= tiles[y][x].y + tileSize)
					{
						Menu.path = Menu.MenuPath.TILE;
						return tiles[y][x];
					}
				}
			}
		}
		return null;
	}
	
	public static boolean checkWorldClick(int mX, int mY)
	{
		if(TileManager.tiles[0][0].x <= mX && mX <= TileManager.tiles[TileManager.tileResW-1][TileManager.tileResL-1].x + TileManager.tileSize &&
		TileManager.tiles[0][0].y <= mY && mY <= TileManager.tiles[TileManager.tileResW-1][TileManager.tileResL-1].y + TileManager.tileSize)
		{
			return true;
		}
		else return false;
	}
	
	public static int randXInMap()
	{
		return TileManager.tiles[0][0].x + (int)(Math.random() * (TileManager.tiles[TileManager.tileResL-1][TileManager.tileResW-1].x - TileManager.tiles[0][0].x));
	}
	
	public static int randYInMap()
	{
		return TileManager.tiles[0][0].y + (int)(Math.random() * (TileManager.tiles[TileManager.tileResL-1][TileManager.tileResW-1].y - TileManager.tiles[0][0].y));
	}
	
	public int p2pw(double frac)
	{
		double returnPixels = 0;
		returnPixels = frac / 1600.0 * appHeight;
		return (int) returnPixels;
	}
	
}
