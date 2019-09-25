package World;

import Essentials.Run;
import Utilities.Menu;
import Utilities.Preferences;
import processing.core.PApplet;
import processing.core.PConstants;

public class TileManager
{
	int appWidth = Run.appWidth;
	static int appHeight = Run.appHeight;
	
	public static int tileResL = Preferences.MAP_DIMENSIONS;
	public static int tileResW = Preferences.MAP_DIMENSIONS;
	
	public static Tile[][] tiles = new Tile[tileResW][tileResL];
	
	static boolean[][] water = Run.waterTiles;
	public static int tileSize = 4 * Preferences.p2pw(1500) / tileResW;
	
	public TileManager(PApplet p, boolean[][] water, int width, int height)
	{
						
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
	
	public static void startTiles()
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
	
	public static void drawTiles(PApplet p)
	{
		for(int x = 0; x < TileManager.tiles.length; x++)
		{
			for(int y = 0; y < TileManager.tiles.length; y++)
			{
				p.colorMode(PConstants.HSB, 360, 100, 100);
				p.stroke(TileManager.tiles[y][x].colorH, TileManager.tiles[y][x].colorS, TileManager.tiles[y][x].colorV - 10);
				p.fill(TileManager.tiles[y][x].colorH, TileManager.tiles[y][x].colorS, TileManager.tiles[y][x].colorV);
				p.rect(TileManager.tiles[y][x].x, TileManager.tiles[y][x].y, TileManager.tileSize, TileManager.tileSize);
				p.fill(0, 0, 0);
				p.stroke(0);
			}
		}
		p.colorMode(PConstants.RGB, 255, 255, 255);
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
	
}
