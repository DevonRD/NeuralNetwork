package io.github.kennytk;

import java.util.ArrayList;

import processing.core.PApplet;

public class World
{
	private PApplet p;

	private int tileResL;
	private int tileResW;
	private Tile[][] tiles;
	private int tileSize;

	private ArrayList<Creature> creatures;

	private int creatureCount;
	private int startNumCreatures;
	private boolean[][] water;
	private double mutateFactor;

	public World(PApplet p, int startNumCreatures, boolean[][] water, double mutateFactor)
	{
		this.p = p;
		
		this.mutateFactor = mutateFactor;

		tileResL = 100;
		tileResW = 100;
		tiles = new Tile[tileResW][tileResL];
		tileSize = 4 * Maths.scaleX(1080) / tileResW;
		
		creatures = new ArrayList<Creature>();
		this.startNumCreatures = startNumCreatures;
		this.water = water;
	}

	public void iterate(double timeInterval)
	{
		updateTiles(timeInterval);
		updateCreatures(timeInterval);
	}

	public void startTiles()
	{
		int count = 0;
		for(int x = 0; x < tileResL; x++)
		{
			for(int y = 0; y < tileResW; y++)
			{
				count++;
				tiles[y][x] = new Tile(Maths.scaleX(50) + x * tileSize, Maths.scaleX(50) + y * tileSize, tileSize, count, x, y,
						water[x][y]);
			}
		}
	}

	public void startCreatures()
	{
		for(int i = 0; i < startNumCreatures; i++)
		{
			creatureCount++;
			creatures.add(new Creature(p, Maths.scaleX(50) + (int) (Math.random() * 4 * Maths.scaleX(1500)),
					Maths.scaleX(50) + (int) (Math.random() * 4 * Maths.scaleX(1500)), creatureCount, 0, mutateFactor));
		}
	}

	public void addCreature()
	{
		creatureCount++;
		creatures.add(new Creature(p, Maths.scaleX(50) + (int) (Math.random() * Maths.scaleX(1500)),
				Maths.scaleX(50) + (int) (Math.random() * Maths.scaleX(1500)), creatureCount, 0, mutateFactor));
	}

	public void addCreature(int x, int y)
	{
		creatureCount++;
		creatures.add(new Creature(p, x, y, creatureCount, 0, mutateFactor));
	}

	public void updateTiles(double timeInterval)
	{
		for(int x = 0; x < tileResL; x++)
		{
			for(int y = 0; y < tileResW; y++)
			{
				tiles[y][x].regen();
			}
		}
	}

	public void updateCreatures(double timeInterval)
	{
		int[] leftTile, midTile, rightTile, mouthTile;
		for(int i = 0; i < creatures.size(); i++)
		{
			creatures.get(i).fitness += timeInterval;
			double[] sensorInput = new double[creatures.get(i).numInputs];
			Creature c = creatures.get(i);
			c.updateSensorCoords();
			leftTile = findTileAt(c.leftSensorX, c.leftSensorY);
			midTile = findTileAt(c.midSensorX, c.midSensorY);
			rightTile = findTileAt(c.rightSensorX, c.rightSensorY);
			mouthTile = findTileAt(c.mouthSensorX, c.mouthSensorY);
			// Left food, left creature, center food, center creature, right food, right creature,
			// mouth food, energy change rate,
			sensorInput[0] = tiles[leftTile[0]][leftTile[1]].food / 100.0;
			if(isCreatureAt(c.leftSensorX, c.leftSensorY))
				sensorInput[1] = 1.0;
			else
				sensorInput[1] = -1.0;
			sensorInput[2] = tiles[midTile[0]][midTile[1]].food / 100.0;
			if(isCreatureAt(c.midSensorX, c.midSensorY))
				sensorInput[3] = 1.0;
			else
				sensorInput[3] = -1.0;
			sensorInput[4] = tiles[rightTile[0]][rightTile[1]].food / 100.0;
			if(isCreatureAt(c.rightSensorX, c.rightSensorY))
				sensorInput[5] = 1.0;
			else
				sensorInput[5] = -1.0;
			sensorInput[6] = tiles[mouthTile[0]][mouthTile[1]].food / 100.0;
			sensorInput[7] = c.size / 300.0;

			creatures.get(i).iterate(sensorInput, timeInterval);
			creatures.get(i).locationX = Math.min(creatures.get(i).locationX, tiles[tileResL - 1][tileResW - 1].x + tileSize);
			creatures.get(i).locationY = Math.min(creatures.get(i).locationY, tiles[tileResL - 1][tileResW - 1].y + tileSize);
			creatures.get(i).locationX = Math.max(creatures.get(i).locationX, tiles[0][0].x);
			creatures.get(i).locationY = Math.max(creatures.get(i).locationY, tiles[0][0].y);
			double eatRequest = creatures.get(i).requestEat(timeInterval);
			int[] foodTile = findTileAt(c.mouthSensorX, c.mouthSensorY);
			creatures.get(i).allowEat(requestEat(foodTile[0], foodTile[1], eatRequest));
			if(creatures.get(i).requestBirth())
			{
				// System.out.println(i + " request birth");
				if(creatures.get(i).size < 300)
				{
					// creatures.get(i).size = 10; // this kills them if they try to birth but don't have enough mass
					// System.out.println("birth failed at " + timeCopy);
				}
				else
				{
					// System.out.println("birth successful at " + timeCopy);
					// births++;
					creatureCount++;
					ArrayList<Axon[][]> creatureBrain = creatures.get(i).giveBirth();
					creatures.add(new Creature((int) creatures.get(i).locationX, (int) creatures.get(i).locationY, creatureCount, 150,
							(creatures.get(i).generation + 1), mutateFactor, creatureBrain));
				}
			}
		}
	}

	public int[] findTileAt(double xCoor, double yCoor)
	{
		int xCoord = (int) xCoor;
		int yCoord = (int) yCoor;
		int[] spot = new int[2];
		for(int x = 0; x < tiles.length; x++)
		{
			for(int y = 0; y < tiles.length; y++)
			{
				if(tiles[y][x].x < xCoord && xCoord <= tiles[y][x].x + tileSize)
				{
					if(tiles[y][x].y < yCoord && yCoord <= tiles[y][x].y + tileSize)
					{
						spot[0] = y;
						spot[1] = x;
					}
				}
			}
		}
		return spot;
	}

	public Tile findTileAt(double xCoor, double yCoor, boolean whatever)
	{
		int xCoord = (int) xCoor;
		int yCoord = (int) yCoor;
		for(int x = 0; x < tiles.length; x++)
		{
			for(int y = 0; y < tiles.length; y++)
			{
				if(tiles[y][x].x < xCoord && xCoord <= tiles[y][x].x + tileSize)
				{
					if(tiles[y][x].y < yCoord && yCoord <= tiles[y][x].y + tileSize)
					{
						return tiles[y][x];
					}
				}
			}
		}
		return null;
	}

	public boolean isCreatureAt(double xCoor, double yCoor)
	{
		boolean isCreature = false;
		int xCoord = (int) xCoor;
		int yCoord = (int) yCoor;
		for(int i = 0; i < creatures.size(); i++)
		{
			if(Math.hypot(xCoord - creatures.get(i).locationX, yCoord - creatures.get(i).locationY) < creatures.get(i).diameter / 2)
			{
				isCreature = true;
			}
		}
		return isCreature;
	}

	public double requestEat(int yIndex, int xIndex, double amount)
	{
		if(tiles[yIndex][xIndex].food < amount)
			return 0;
		tiles[yIndex][xIndex].food -= amount;
		return amount;
	}

	public int getCreatureCount()
	{
		return creatures.size();

	}
}
