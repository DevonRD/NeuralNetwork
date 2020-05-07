package Creature;

import java.awt.Frame;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import Essentials.Run;
import Utilities.Menu;
import Utilities.Prefs;
import World.TileManager;
import processing.core.PApplet;
import processing.core.PConstants;

public class CreatureManager
{
	static int appWidth = Run.appWidth;
	static int appHeight = Run.appHeight;
	
	public static ArrayList<Creature> creatures = new ArrayList<Creature>();
	
	public static int creatureCount;
	public static int startNumCreatures = Run.startNumCreatures;
	public static double mutateChance = Prefs.MUTATE_CHANCE;
	public static int births = 0;
	public static int creatureDeaths = 0;
	public static int maxObservedCreatures = startNumCreatures;
	
	/** STATIC CLASS **/
	
	public static void iterate(double timeInterval)
	{
		for(int i = 0; i < creatures.size(); i++)
		{			
			creatures.get(i).iterate(timeInterval);
			
			double eatRequest = creatures.get(i).requestEat(timeInterval);
			int[] foodTile = TileManager.findTileCoordsAt(creatures.get(i).mouthSensorX, creatures.get(i).mouthSensorY);
			double allowEatAmt = requestEat(foodTile[0], foodTile[1], eatRequest);
			
			creatures.get(i).allowEat(allowEatAmt);
			if(allowEatAmt == 0.0)  TileManager.tiles[foodTile[0]][foodTile[1]].resetCooldown();
			
			if(creatures.get(i).requestBirth())
			{
				if(creatures.get(i).size < 400)
				{
					if(Prefs.KILL_FOR_BIRTH_WITHOUT_MASS) creatures.get(i).size = 10;
				}
				else
				{
					births++;
					creatureCount++;
					ArrayList<Axon[][]> creatureBrain = creatures.get(i).giveBirth();
					creatures.add(new Creature(appWidth, appHeight, (int)creatures.get(i).locationX, (int)creatures.get(i).locationY,
							creatureCount, ( creatures.get(i).generation+1 ), creatureBrain, creatures.get(i).color, creatures.get(i).ID));
					if(findCreatureID(creatureCount).superMutate) Run.superMutations++;
				}
			}
		}
		checkForDeaths();
		if(creatures.size() > maxObservedCreatures) maxObservedCreatures = creatures.size();
	}
	
	public static void startCreatures()
	{
		for(int i = 0; i < startNumCreatures; i++)
		{
			int testX = TileManager.randXInMap();
			int testY = TileManager.randYInMap();
			int[] tileTest = TileManager.findTileCoordsAt(testX, testY);
			if(!Run.waterTiles[tileTest[1]][tileTest[0]])
			{
				creatureCount++;
				creatures.add(new Creature(appWidth, appHeight, testX, testY, creatureCount, 0));
			}
			else i--;
		}
	}
	
	// Add a creature in a random place with a random brain, on land only
	public static void addCreature()
	{
		boolean done = false;
		while (!done)
		{
			int testX = TileManager.randXInMap();
			int testY = TileManager.randYInMap();
			int[] tileTest = TileManager.findTileCoordsAt(testX, testY);
			if(!Run.waterTiles[tileTest[1]][tileTest[0]])
			{
				creatureCount++;
				creatures.add(new Creature(appWidth, appHeight, testX, testY, creatureCount, 0));
				done = true;
			}
		}
	}
	
	// Add a creature with a random brain at a specific location
	public static void addCreature(int x, int y)
	{
		creatureCount++;
		creatures.add(new Creature(appWidth, appHeight, x, y, creatureCount, 0));
	}
	
	public static void spawnNumCreatures(int number)
	{
		for(int i = 0; i < number; i++)
		{
			addCreature();
		}
	}
	
	public static double requestEat(int yIndex, int xIndex, double amount)
	{
		if(TileManager.tiles[yIndex][xIndex].food < amount) return 0.0;
		TileManager.tiles[yIndex][xIndex].food -= amount;
		return amount;
	}
	
	public static void checkForDeaths()
	{
		if(creatures.size() == 0) return;
		for(int i = 0; i < creatures.size(); i++)
		{
			if(creatures.get(i).size < 100)
			{
				if(Run.selectedCreature != null && creatures.get(i).ID == Run.selectedCreature.ID)
				{
					Menu.path = Menu.MenuPath.GENERAL;
					Run.selectedCreature = null;
				}
				creatures.remove(i);
				creatureDeaths++;
			}
		}
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
	
	// To search for a creature when you are unsure of its existence
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
	
	// For use if you know the creature already exists, does not select
	public static Creature findCreatureID(int ID)
	{
		for(int i = 0; i < creatures.size(); i++)
		{
			if(ID == creatures.get(i).ID)
			{
				return creatures.get(i);
			}
		}
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
			double dist = Prefs.distBtCoords(from.locationX, from.locationY, 
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
	
	public static void maintain()
	{
		while(creatures.size() < Run.maintainNum)
		{
			Run.forcedSpawns++;
			addCreature();
		}
	}
	
	public static void drawCreatures(PApplet p)
	{
		p.colorMode(PConstants.RGB);
		p.fill(255);
		for(int i = 0; i < creatures.size(); i++)
		{
			Creature c = creatures.get(i);
			p.stroke(50);
			p.line((int)c.locationX, (int)c.locationY, (int)c.leftSensorX, (int)c.leftSensorY);
			if(c.outputNeurons[3] > 0.0) p.stroke(180, 0, 0);
			p.line((int)c.locationX, (int)c.locationY, (int)c.midSensorX, (int)c.midSensorY);
			p.stroke(50);
			p.line((int)c.locationX, (int)c.locationY, (int)c.rightSensorX, (int)c.rightSensorY);
			p.stroke(0);
			p.fill(c.color.hashCode());
			if(Run.selectedCreature != null && creatures.get(i).ID == Run.selectedCreature.ID)
			{
				p.stroke(240, 0, 255);
				p.strokeWeight(7);
			}
			p.ellipse((int)c.locationX, (int)c.locationY, Prefs.p2pw(c.diameter), Prefs.p2pw(c.diameter));
			p.fill(255);
			p.stroke(0);
			p.strokeWeight(1);
			p.colorMode(PConstants.HSB, 360, 100, 100);
			p.fill(c.leftSensorColor, 80, 45);
			p.ellipse((int)c.leftSensorX, (int)c.leftSensorY, Prefs.p2pw(15), Prefs.p2pw(15));
			p.fill(c.rightSensorColor, 80, 45);
			p.ellipse((int)c.rightSensorX, (int)c.rightSensorY, Prefs.p2pw(15), Prefs.p2pw(15));
			p.fill(c.mouthSensorColor, 80, 45);
			//ellipse((int)c.mouthSensorX, (int)c.mouthSensorY, p2pw(15), p2pw(15));
			p.colorMode(PConstants.RGB, 255, 255, 255);
			p.fill(0);
		}
	}
	
	public static void killAll()
	{
		while(!creatures.isEmpty())
		{
			creatures.remove(0);
		}
	}
}
