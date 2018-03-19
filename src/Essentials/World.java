package Essentials;

import Network.Network;
import processing.core.PApplet;

import java.util.ArrayList;

public class World
{
	int tileResL;
	int tileResW;
	Tile[][] tiles;
	int tileSize;
	ArrayList<Creature> creatures;
	int creatureCount;
	int startNumCreatures;
	int realWidth, realHeight;
	private PApplet p;
	
	public World(PApplet p, int startNumCreatures, int width, int height)
	{
		this.p = p;
		realWidth = width;
		realHeight = height;
		tileResL = 100;
		tileResW = 100;
		tiles = new Tile[tileResW][tileResL];
		tileSize = 4 * p2pw(1500) / tileResW;
		creatures = new ArrayList<Creature>();
		this.startNumCreatures = startNumCreatures;
	}
	
	public void startTiles()
	{
		int count = 0;
		for(int x = 0; x < tileResL; x++)
		{
			for(int y = 0; y < tileResW; y++)
			{
				count++;
				boolean water = false;
				if(Math.random() < 0.05) water = true;
				tiles[y][x] = new Tile(p2pw(50) + x * tileSize, p2pw(50) + y * tileSize, tileSize, count, x, y, water);
			}
		}
	}
	
	public void startCreatures()
	{
		for(int i = 0; i < startNumCreatures; i++)
		{
			creatureCount++;
			creatures.add(new Creature(p2pw(50) + (int)(Math.random() * 4 * p2pw(1500)), p2pw(50) + (int)(Math.random() * 4 * p2pw(1500)), creatureCount));
		}
	}
	
	public void addCreature()
	{
		creatureCount++;
		creatures.add(new Creature(p2pw(50) + (int)(Math.random() * p2pw(1500)), p2pw(50) + (int)(Math.random() * p2pw(1500)), creatureCount));
	}
	
	public void addCreature(int x, int y)
	{
		creatureCount++;
		creatures.add(new Creature(x, y, creatureCount));
	}
	
	public void updateTiles()
	{
		for(int x = 0; x < tileResL; x++)
		{
			for(int y = 0; y < tileResW; y++)
			{
				tiles[y][x].testRegen();
			}
		}
	}
	
	public void updateCreatures()
	{
		for(int i = 0; i < creatures.size(); i++) // random eating, moving, decaying, reproducing
		{
			int food = 0;
			if(Math.random() < 0.6) food = requestEat(creatures.get(i).locationX, creatures.get(i).locationY);
			creatures.get(i).size += food;
			creatures.get(i).totalEaten += food;
			creatures.get(i).updateSize();
			
			if(Math.random() < .666)
			{
				if(Math.random() < .5) creatures.get(i).locationX += 3;
				else creatures.get(i).locationX -= 3;
				if(Math.random() < .5) creatures.get(i).locationY += 3;
				else creatures.get(i).locationY -= 3;
			}
			
			if(creatures.get(i).locationX > tiles[tileResW-1][tileResL-1].x + tileSize) creatures.get(i).locationX = tiles[tileResW-1][tileResL-1].x + tileSize;
			if(creatures.get(i).locationX < p2pw(50)) creatures.get(i).locationX = p2pw(50);
			if(creatures.get(i).locationY > tiles[tileResW-1][tileResL-1].y + tileSize) creatures.get(i).locationY = tiles[tileResW-1][tileResL-1].y + tileSize;
			if(creatures.get(i).locationY < p2pw(50)) creatures.get(i).locationY = p2pw(50);
			
			if(Math.random() < (creatures.get(i).size / 800))
			{
				creatures.get(i).size--;
				creatures.get(i).totalDecayed++;
			}
			
			if(creatures.get(i).checkReproduce())
			{
				creatureCount++;
				creatures.add(new Creature(creatures.get(i).locationX, creatures.get(i).locationY, creatureCount, 150));
			}
		}
		
	}
	
	public int requestEat(int xCor, int yCor)
	{
		for(int x = 0; x < tiles.length; x++)
		{
			for(int y = 0; y < tiles.length; y++)
			{
				if (tiles[y][x].x < xCor && xCor <= tiles[y][x].x + tileSize)
				{
					if (tiles[y][x].y < yCor && yCor <= tiles[y][x].y + tileSize)
					{
						if(tiles[y][x].food < 1) return 0;
						tiles[y][x].food--;
						return 1;
					}
				}
			}
		}
		return 0;
	}
	
	public int p2pl(double frac)
	{
		double returnPixels = 0;
		returnPixels = frac / 2600.0 * realWidth;
		return (int) returnPixels;
	}
	public int p2pw(double frac)
	{
		double returnPixels = 0;
		returnPixels = frac / 1600.0 * realHeight;
		return (int) returnPixels;
	}
	
}

























