package io.github.kennytk;

import java.awt.geom.Point2D;

import processing.core.PApplet;
import processing.core.PConstants;

public class TileManager implements IDrawable
{
	private PApplet p;

	private static int horizontalNum;
	private static int verticalNum;
	private static int totalNum;
	private static int tileSize;

	private static Tile[][] tiles;
	private Tile selectedTile;

	public TileManager(PApplet p, int horizontalNum, int verticalNum)
	{
		this.p = p;

		TileManager.horizontalNum = horizontalNum;
		TileManager.verticalNum = verticalNum;
		
		this.totalNum = horizontalNum * verticalNum;

		tiles = new Tile[horizontalNum][verticalNum];
		tileSize = 4 * Maths.scaleX(1080) / horizontalNum; // TODO: change
	}

	public void setup()
	{
		for(int x = 0; x < horizontalNum; x++)
		{
			for(int y = 0; y < verticalNum; y++)
			{
				Statistics.tileNum++;
				tiles[x][y] = new Tile(p, Maths.scaleX(50) + x * tileSize, Maths.scaleX(50) + y * tileSize, tileSize, Statistics.tileNum, x, y);
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

	public void menu()
	{
		p.pushStyle();

		p.colorMode(PConstants.HSB);

		p.fill(selectedTile.getH(), selectedTile.getS(), selectedTile.getV());

		p.rect(Maths.scaleX(45), Maths.scaleY(220), Maths.scaleX(200), Maths.scaleY(200)); // draw the tile

		p.colorMode(PConstants.RGB);

		p.fill(255, 255, 255);

		p.textSize(Maths.scaleX(40));

		p.text("Selected Tile Data", Maths.scaleX(1620), Maths.scaleY(190));

		p.textSize(Maths.scaleX(30));

		p.text(" # " + selectedTile.getID(), Maths.scaleX(1620), Maths.scaleY(250));

		p.text(" Food: " + Maths.decimalFormat((selectedTile.getFood())), Maths.scaleX(1620), Maths.scaleY(400));

		p.text("Row and Column: (" + (selectedTile.getXIndex() + 1) + ", " + (selectedTile.getYIndex() + 1) + ")", Maths.scaleX(1830),
				Maths.scaleY(250));

		p.text("Regeneration Value: " + Math.round((selectedTile.getRegenValue() * 1000)) / 1000.0, Maths.scaleX(1830), Maths.scaleY(290));
		
		p.text("HSV: " + selectedTile.getH() + ", " + selectedTile.getS() + ", " + selectedTile.getV(), Maths.scaleX(1830),
				Maths.scaleY(330));
		
		p.text("x Range: " + selectedTile.getX() + " to " + (selectedTile.getX()+ selectedTile.getTileSize()), Maths.scaleX(1830), Maths.scaleY(370));
		p.text("y Range: " + selectedTile.getY() + " to " + (selectedTile.getY() + selectedTile.getTileSize()), Maths.scaleX(1830), Maths.scaleY(410));

		p.popStyle();
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

	public void selectTile(int yIndex, int xIndex)
	{
		selectedTile = tiles[yIndex][xIndex];
	}

	public static Tile getTile(double xP, double yP)
	{
		for(int x = 0; x < horizontalNum; x++)
		{
			for(int y = 0; y < verticalNum; y++)
			{
				if(tiles[y][x].getX() < xP && yP <= tiles[y][x].getY() + tileSize)
				{
					if(tiles[y][x].getY() < xP && yP <= tiles[y][x].getY() + tileSize)
					{
						return tiles[y][x];
					}
				}
			}
		}
		return null;
	}

	public static Point2D getTilePoint(double xP, double yP)
	{
		xP = (int) xP;
		yP = (int) xP;
		
		Point2D point = null;
		
		for(int x = 0; x < tiles.length; x++)
		{
			for(int y = 0; y < tiles.length; y++)
			{
				//xP, yP and x,y might need to be switched inside if statements
				if(tiles[x][y].getX() < xP && xP <= tiles[x][y].getX() + tileSize)
				{
					if(tiles[x][y].getY() < yP && yP <= tiles[x][y].getY() + tileSize)
					{
						point.setLocation(x,y);
					}
				}
			}
		}
		return point;
	}
	
	public double requestEat(int x, int y, double amount)
	{
		if(tiles[x][y].getFood() < amount)
			return 0;
		
		tiles[x][y].setFood(tiles[x][y].getFood() - amount);
		return amount;
	}
	
	public int getHorizontalNum()
	{
		return horizontalNum;
	}
	
	public int getVerticalNum()
	{
		return verticalNum;
	}
	
	public int getTotalNum()
	{
		return totalNum;
	}
}
