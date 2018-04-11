package Essentials;

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
	@SuppressWarnings("unused")
	private PApplet p;
	boolean[][] water;
	double mutateChance;
	int births = 0;
	double timeCopy = 0;
	
	public World(PApplet p, int startNumCreatures, int width, int height, boolean[][] water, double mutateChance)
	{
		this.mutateChance = mutateChance;
		this.p = p;
		realWidth = width;
		realHeight = height;
		tileResL = 100;
		tileResW = 100;
		tiles = new Tile[tileResW][tileResL];
		tileSize = 4 * p2pw(1500) / tileResW;
		creatures = new ArrayList<Creature>();
		this.startNumCreatures = startNumCreatures;
		this.water = water;
	}
	
	public void iterate(double timeInterval)
	{
		timeCopy += timeInterval;
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
				tiles[y][x] = new Tile(x * tileSize, y * tileSize, tileSize, count, x, y, water[x][y]);
			}
		}
	}
	public void startCreatures()
	{
		for(int i = 0; i < startNumCreatures; i++)
		{
			creatureCount++;
			creatures.add(new Creature(realWidth, realHeight, p2pw(50) + (int)(Math.random() * 4 * p2pw(1500)), p2pw(50) + (int)(Math.random() * 4 * p2pw(1500)), creatureCount, 0, mutateChance));
		}
	}
	public void addCreature()
	{
		creatureCount++;
		creatures.add(new Creature(realWidth, realHeight, tiles[0][0].x + (int)(Math.random() * (tiles[tileResL-1][tileResW-1].x - tiles[0][0].x)), tiles[0][0].y + (int)(Math.random() * (tiles[tileResL-1][tileResW-1].y - tiles[0][0].y)), creatureCount, 0, mutateChance));
	}
	public void addCreature(int x, int y)
	{
		creatureCount++;
		creatures.add(new Creature(realWidth, realHeight, x, y, creatureCount, 0, mutateChance));
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
			double[] sensorInput = new double[creatures.get(i).brainLength];
			Creature c = creatures.get(i);
			c.updateSensorCoords();
			leftTile = findTileAt(c.leftSensorX, c.leftSensorY);
			midTile = findTileAt(c.midSensorX, c.midSensorY);
			rightTile = findTileAt(c.rightSensorX, c.rightSensorY);
			mouthTile = findTileAt(c.mouthSensorX, c.mouthSensorY);
			creatures.get(i).leftSensorColor = tiles[leftTile[0]][leftTile[1]].colorH;
			creatures.get(i).rightSensorColor = tiles[rightTile[0]][rightTile[1]].colorH;
			creatures.get(i).mouthSensorColor = tiles[mouthTile[0]][mouthTile[1]].colorH;
			// Left food, left creature, center food, center creature, right food, right creature,
			// mouth food, energy change rate, 
			sensorInput[0] = tiles[leftTile[0]][leftTile[1]].food / 25.0 - 2.0;
			if(isCreatureAt(c.leftSensorX, c.leftSensorY)) sensorInput[1] = 1.0;
			else sensorInput[1] = -1.0;
			sensorInput[2] = tiles[midTile[0]][midTile[1]].food / 25.0 - 2.0;
			if(isCreatureAt(c.midSensorX, c.midSensorY)) sensorInput[3] = 1.0;
			else sensorInput[3] = -1.0;
			sensorInput[4] = tiles[rightTile[0]][rightTile[1]].food / 25.0 - 2.0;
			if(isCreatureAt(c.rightSensorX, c.rightSensorY)) sensorInput[5] = 1.0;
			else sensorInput[5] = -1.0;
			sensorInput[6] = tiles[mouthTile[0]][mouthTile[1]].food / 25.0 - 2.0;
			sensorInput[7] = c.size / 100.0 - 3.0;
			
			creatures.get(i).iterate(sensorInput, timeInterval);
			creatures.get(i).locationX = Math.min(creatures.get(i).locationX, tiles[tileResL-1][tileResW-1].x + tileSize);
			creatures.get(i).locationY = Math.min(creatures.get(i).locationY, tiles[tileResL-1][tileResW-1].y + tileSize);
			creatures.get(i).locationX = Math.max(creatures.get(i).locationX, tiles[0][0].x);
			creatures.get(i).locationY = Math.max(creatures.get(i).locationY, tiles[0][0].y);
			double eatRequest = creatures.get(i).requestEat(timeInterval);
			int[] foodTile = findTileAt(c.mouthSensorX, c.mouthSensorY);
			creatures.get(i).allowEat(requestEat(foodTile[0], foodTile[1], eatRequest));
			if(creatures.get(i).requestBirth())
			{
				if(creatures.get(i).size < 400)
				{
					//creatures.get(i).size = 10; // this kills them if they try to birth but don't have enough mass
				}
				else
				{
					births++;
					creatureCount++;
					ArrayList<Axon[][]> creatureBrain = creatures.get(i).giveBirth();
					creatures.add(new Creature(realWidth, realHeight, (int)creatures.get(i).locationX, (int)creatures.get(i).locationY, creatureCount, 250, ( creatures.get(i).generation+1 ), mutateChance, creatureBrain, creatures.get(i).color, creatures.get(i).ID + ""));
				}
			}
		}
	}
	public int[] findTileAt(double xCoord, double yCoord)
	{
		int[] spot = new int[2];
		spot[0] = ((int) yCoord) / tileSize;
		spot[1] = ((int) xCoord) / tileSize;
		if(spot[0] > tileResW - 1) spot[0] = 0;
		if(spot[1] > tileResL - 1) spot[1] = 0;
		spot[0] = Math.max(0, spot[0]);
		spot[1] = Math.max(0, spot[1]);
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
	public boolean isCreatureAt(double xCoor, double yCoor)
	{
		boolean isCreature = false;
		int xCoord = (int) xCoor;
		int yCoord = (int) yCoor;
		for(int i = 0; i < creatures.size(); i++)
		{
			if (Math.hypot(xCoord - creatures.get(i).locationX, yCoord - creatures.get(i).locationY) < creatures.get(i).diameter/2)
			{
				isCreature = true;
			}
		}
		return isCreature;
	}
	
	public double requestEat(int yIndex, int xIndex, double amount)
	{
		if(tiles[yIndex][xIndex].food < amount) return 0.0;
		tiles[yIndex][xIndex].food -= amount;
		return amount;
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

























