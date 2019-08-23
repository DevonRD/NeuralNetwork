package Creature;

import java.awt.Frame;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import Essentials.Run;
import Utilities.Menu;
import Utilities.Variables;
import World.TileManager;
import processing.core.PApplet;

public class CreatureManager
{
	PApplet p;
	static int appWidth;
	static int appHeight;
	
	public static ArrayList<Creature> creatures;
	
	public static int creatureCount;
	public static int startNumCreatures;
	public static double mutateChance;
	public static int births = 0;
	
	public CreatureManager(PApplet p, int startNumCreatures, double mutateChance, int width, int height)
	{
		this.p = p;
		CreatureManager.mutateChance = mutateChance;
		
		appWidth = width;
		appHeight = height;
		
		creatures = new ArrayList<Creature>();
		CreatureManager.startNumCreatures = startNumCreatures;
		
		startCreatures();
	}
	
	public static void iterate(double timeInterval)
	{
		for(int i = 0; i < creatures.size(); i++)
		{			
			creatures.get(i).iterate(timeInterval);
			
			double eatRequest = creatures.get(i).requestEat(timeInterval);
			int[] foodTile = TileManager.findTileAt(creatures.get(i).mouthSensorX, creatures.get(i).mouthSensorY);
			double allowEatAmt = requestEat(foodTile[0], foodTile[1], eatRequest);
			
			creatures.get(i).allowEat(allowEatAmt);
			if(allowEatAmt == 0.0)  TileManager.tiles[foodTile[0]][foodTile[1]].resetCooldown();
			
			if(creatures.get(i).requestBirth())
			{
				if(creatures.get(i).size < 400)
				{
					if(Variables.KILL_FOR_BIRTH_WITHOUT_MASS) creatures.get(i).size = 10;
				}
				else
				{
					births++;
					creatureCount++;
					ArrayList<Axon[][]> creatureBrain = creatures.get(i).giveBirth();
					creatures.add(new Creature(appWidth, appHeight, (int)creatures.get(i).locationX, (int)creatures.get(i).locationY, creatureCount, 250, ( creatures.get(i).generation+1 ), mutateChance, creatureBrain, creatures.get(i).color, creatures.get(i).ID + ""));
				}
			}
		}
	}
	
	public void startCreatures()
	{
		for(int i = 0; i < startNumCreatures; i++)
		{
			int testX = TileManager.randXInMap();
			int testY = TileManager.randYInMap();
			int[] tileTest = TileManager.findTileAt(testX, testY);
			if(!Run.waterTiles[tileTest[1]][tileTest[0]])
			{
				creatureCount++;
				creatures.add(new Creature(appWidth, appHeight, testX, testY, creatureCount, 0, mutateChance));
			}
			else i--;
		}
	}
	
	public static void addCreature()
	{
		boolean done = false;
		while (!done)
		{
			int testX = TileManager.randXInMap();
			int testY = TileManager.randYInMap();
			int[] tileTest = TileManager.findTileAt(testX, testY);
			if(!Run.waterTiles[tileTest[1]][tileTest[0]])
			{
				creatureCount++;
				creatures.add(new Creature(appWidth, appHeight, testX, testY, creatureCount, 0, mutateChance));
				done = true;
			}
		}
	}
	public static void addCreature(int x, int y)
	{
		creatureCount++;
		creatures.add(new Creature(appWidth, appHeight, x, y, creatureCount, 0, mutateChance));
	}
	
	public static double requestEat(int yIndex, int xIndex, double amount)
	{
		if(TileManager.tiles[yIndex][xIndex].food < amount) return 0.0;
		TileManager.tiles[yIndex][xIndex].food -= amount;
		return amount;
	}
	
	
	
	
	public static boolean isCreatureAt(double xCoor, double yCoor)
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
	
	public void creatureMenu()
	{
		
	}
	
	public static Creature checkCreatureClick(int x, int y)
	{
		for(int i = 0; i < creatures.size(); i++)
		{
			if (Math.hypot(x - creatures.get(i).locationX, y - creatures.get(i).locationY) < creatures.get(i).diameter/2)
			{
				Menu.path = Menu.MenuPath.CREATURE;
				return creatures.get(i);
			}
		}
		return null;
	}
	
	public static Creature findCreatureID(Frame frame)
	{
		int id = Integer.parseInt(JOptionPane.showInputDialog("Enter creature ID:"));
		
		for(int i = 0; i < creatures.size(); i++)
		{
			if(id == creatures.get(i).ID)
			{
				Menu.path = Menu.MenuPath.CREATURE;
				return creatures.get(i);
			}
		}
		JOptionPane.showMessageDialog(frame, "That creature ID doesn't exist or has died.");
		return null;
	}
	
	public static Object[] findClosestCreatureData(Creature from)
	{
		Object[] data = new Object[2];
		Creature nearestCreature = null;
		double closestDist = 999999.9;
		int within10 = 0;
		
		for(int i = 0; i < creatures.size(); i++)
		{
			double dist = Variables.distBtCoords(from.locationX, from.locationY, 
					creatures.get(i).locationX, creatures.get(i).locationY);
			if(dist < closestDist && dist > 0.01)
			{
				closestDist = dist;
				nearestCreature = creatures.get(i);
			}
			if(dist / TileManager.tileSize < 10.0 && dist > 0.01)
			{
				within10++;
			}
		}
		
		data[0] = nearestCreature;
		data[1] = within10;
		return data;
	}
	
	public int p2pl(double frac)
	{
		double returnPixels = 0;
		returnPixels = frac / 2600.0 * appWidth;
		return (int) returnPixels;
	}
	public int p2pw(double frac)
	{
		double returnPixels = 0;
		returnPixels = frac / 1600.0 * appHeight;
		return (int) returnPixels;
	}
}
