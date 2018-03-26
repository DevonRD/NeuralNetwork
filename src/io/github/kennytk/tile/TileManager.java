package io.github.kennytk.tile;

import io.github.kennytk.IDrawable;
import io.github.kennytk.numbers.Globals;
import io.github.kennytk.numbers.Maths;
import io.github.kennytk.numbers.Statistics;
import io.github.kennytk.numbers.Globals.MenuMode;
import processing.core.PApplet;
import processing.core.PConstants;

public class TileManager implements IDrawable
{
	private static PApplet p;

	private static int horizontalNum;
	private static int verticalNum;
	private static int totalNum;
	private static int tileSize;

	private static Tile[][] tiles;
	private Tile selectedTile;

	public TileManager(PApplet p, int horizontalNum, int verticalNum)
	{
		TileManager.p = p;

		TileManager.horizontalNum = horizontalNum;
		TileManager.verticalNum = verticalNum;

		TileManager.totalNum = horizontalNum * verticalNum;

		tiles = new Tile[horizontalNum][verticalNum];

		tileSize = 4 * Maths.scaleX(1080) / horizontalNum; // TODO: change
		System.out.println(tileSize);
	}

	public void setup()
	{
		for(int x = 0; x < horizontalNum; x++)
		{
			for(int y = 0; y < verticalNum; y++)
			{
				Statistics.tileNum++;
				tiles[x][y] = new Tile(p, tileIndexToPixelsX(x), tileIndexToPixelsY(y), tileSize, Statistics.tileNum, x, y);
			}
		}

		System.out.println("tileManager setup complete - tileNum: " + Statistics.tileNum);
	}

	// TODO: fix the nullPointer in selectTile which crashes it all
	public boolean click(int mX, int mY)
	{
		selectTile(mX, mY);

		if(selectedTile.getXIndex() == -1)
		{
			selectedTile = null;
			Globals.menuMode = MenuMode.MAIN;
			return false;
		}
		else
		{
			Globals.menuMode = MenuMode.TILE;
			return true;
		}
	}

	public int tileIndexToPixelsX(int x)
	{
		return x * tileSize;
	}

	public int tileIndexToPixelsY(int y)
	{
		return y * tileSize;
	}

	public void draw()
	{
		p.pushStyle();
		p.colorMode(PConstants.HSB);
		p.strokeWeight(3);

		for(int x = 0; x < horizontalNum; x++)
		{
			for(int y = 0; y < verticalNum; y++)
			{
				p.stroke(tiles[x][y].getH(), tiles[x][y].getS(), tiles[x][y].getV());
				p.fill(tiles[x][y].getH(), tiles[x][y].getS(), tiles[x][y].getV());
				p.rect(tiles[x][y].getX(), tiles[x][y].getY(), tileSize, tileSize);
			}
		}

		p.fill(0, 0, 0);
		p.popStyle();
	}

	public void menu()
	{
		p.colorMode(PConstants.HSB);

		p.fill(selectedTile.getH(), selectedTile.getS(), selectedTile.getV());

		p.rect(Maths.scaleX(45), Maths.scaleY(220), Maths.scaleX(200), Maths.scaleY(200)); // draw the tile

		p.colorMode(PConstants.RGB);

		p.fill(255, 255, 255);

		p.textSize(Maths.scaleX(Globals.menuTextSize));

		p.text("Selected Tile Data", Maths.scaleX(45), Maths.scaleY(190));

		p.textSize(Maths.scaleX(30));

		p.text(" # " + selectedTile.getID(), Maths.scaleX(45), Maths.scaleY(100));

		p.text(" Food: " + Maths.decimalFormat((selectedTile.getFood())), Maths.scaleX(45), Maths.scaleY(500));

		p.text("Row and Column: (" + (selectedTile.getXIndex() + 1) + ", " + (selectedTile.getYIndex() + 1) + ")", Maths.scaleX(45),
				Maths.scaleY(250));

		p.text("Regeneration Value: " + Math.round((selectedTile.getRegenValue() * 1000)) / 1000.0, Maths.scaleX(45), Maths.scaleY(600));

		p.text("HSV: " + selectedTile.getH() + ", " + selectedTile.getS() + ", " + selectedTile.getV(), Maths.scaleX(45),
				Maths.scaleY(330));

		p.text("x Range: " + selectedTile.getX() + " to " + (selectedTile.getX() + selectedTile.getTileSize()), Maths.scaleX(45),
				Maths.scaleY(370));
		p.text("y Range: " + selectedTile.getY() + " to " + (selectedTile.getY() + selectedTile.getTileSize()), Maths.scaleX(45),
				Maths.scaleY(410));
	}

	public void update(double timeInterval)
	{
		for(int x = 0; x < horizontalNum; x++)
		{
			for(int y = 0; y < verticalNum; y++)
			{
				tiles[x][y].regen();
			}
		}
	}

	public void selectTile(int xP, int yP)
	{
		selectedTile = getTileFromPixels(xP, yP);
	}

	public static Tile getTileFromIndex(double xI, double yI)
	{
		return tiles[(int) xI][(int) yI];
	}

	// takes in a xP and yP in pixels and checks it against tile locations to return a tile
	public static Tile getTileFromPixels(double xP, double yP)
	{
		for(int x = 0; x < horizontalNum; x++)
		{
			for(int y = 0; y < verticalNum; y++)
			{
				if(tiles[x][y].getX() <= xP && xP <= tiles[x][y].getX() + tileSize)
				{
					if(tiles[x][y].getY() <= yP && yP <= tiles[x][y].getY() + tileSize)
					{
						return tiles[x][y];
					}
				}
			}
		}

		// System.out.println("ERROR - getTileFromPixels failed - xP: " + xP + " yP: " + yP);

		// Index -1 as to not conflict with board
		Tile nullTile = new Tile(p, tileSize);

		return nullTile;
	}

	public static double requestEat(int xI, int yI, double amount)
	{
		if(xI == -1 || yI == -1)
		{
			return 0;
		}
		else
		{
			// System.out.println("request eat: " + xI + " y " + yI);
			if(tiles[xI][yI].getFood() < amount)
				return 0;

			tiles[xI][yI].setFood(tiles[xI][yI].getFood() - amount);
			return amount;
		}
	}

	public static int getHorizontalNum()
	{
		return horizontalNum;
	}

	public static int getVerticalNum()
	{
		return verticalNum;
	}

	public static int getTotalNum()
	{
		return totalNum;
	}

	public static int getTileSize()
	{
		return tileSize;
	}
}
