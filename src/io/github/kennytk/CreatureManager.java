package io.github.kennytk;

import java.util.ArrayList;

import io.github.kennytk.Globals.MenuMode;
import processing.core.PApplet;
import processing.core.PConstants;

public class CreatureManager implements IDrawable
{
	private PApplet p;

	private static ArrayList<Creature> creatures = new ArrayList<Creature>();

	private Creature selectedCreature = null;

	public CreatureManager(PApplet p)
	{
		this.p = p;
	}

	public void setup()
	{
		for(int i = 0; i < Statistics.startNumCreatures; i++)
		{
			Statistics.creatureCount++; // TODO: VVV
			creatures.add(new Creature(p, Maths.scaleX(50) + (int) (Math.random() * 4 * Maths.scaleX(1500)),
					Maths.scaleX(50) + (int) (Math.random() * 4 * Maths.scaleX(1500)), Statistics.creatureCount, 0));
		}
	}

	public void draw()
	{
		p.colorMode(PConstants.RGB);
		p.fill(255);
		for(Creature creature : creatures)
		{
			creature.draw();
		}
	}

	public void menu()
	{
		p.pushStyle();

		p.colorMode(PConstants.RGB);

		p.fill(60, 120);

		p.rect(Maths.scaleX(1600), 0, Maths.scaleX(1200), Maths.scaleY(2000));

		p.fill(255, 255, 255);

		p.ellipse(Maths.scaleX(1700), Maths.scaleY(320), Maths.scaleY(150), Maths.scaleY(150)); // draw the creature
		p.textSize(Maths.scaleX(70));

		p.text("Selected Creature Data", Maths.scaleX(1620), Maths.scaleY(190));
		
		p.textSize(Maths.scaleX(30));
		p.text("ID: " + selectedCreature.getID(), Maths.scaleX(1620), Maths.scaleY(500));
		
		p.text("Current Size: " + (int) selectedCreature.getSize(), Maths.scaleX(1620), Maths.scaleY(530));
		
		p.text("Total Eaten: " + Maths.decimalFormat(selectedCreature.getTotalEaten()), Maths.scaleX(1620), Maths.scaleY(560));
		
		p.text("Total Decayed: " + Maths.decimalFormat(selectedCreature.getTotalDecayed()), Maths.scaleX(1620), Maths.scaleY(590));
		
		p.text("Location: (" + Maths.decimalFormat(selectedCreature.getX()) + ", " + Maths.decimalFormat(selectedCreature.getY()) + " )",
				Maths.scaleX(1620), Maths.scaleY(620));
		
		p.text("Left Sensor: (" + Maths.decimalFormat(selectedCreature.getLeftSensorX()) + ", " + Maths.decimalFormat(selectedCreature.getLeftSensorY()) + " )",
				Maths.scaleX(1620), Maths.scaleY(650));
		
		p.text("Mid Sensor: (" + Maths.decimalFormat(selectedCreature.getMidSensorX()) + ", " + Maths.decimalFormat(selectedCreature.getMidSensorY()) + " )",
				Maths.scaleX(1620), Maths.scaleY(680));
		
		p.text("Right Sensor: (" + Maths.decimalFormat(selectedCreature.getRightSensorX()) + ", " + Maths.decimalFormat(selectedCreature.getRightSensorY()) + " )",
				Maths.scaleX(1620), Maths.scaleY(710));
		
		p.text("Mouth Sensor: (" + Maths.decimalFormat(selectedCreature.getMouthSensorX()) + ", " + Maths.decimalFormat(selectedCreature.getMouthSensorY()) + " )",
				Maths.scaleX(1620), Maths.scaleY(740));
		
		//needs different way to access TileManager to get findTileAt method
		//p.text("Food Under Me: " + Maths.decimalFormat(world.findTileAt(selectedCreature.getMouthSensorX(), selectedCreature.getMouthSensorY(), true).food),
		//		Maths.scaleX(1620), Maths.scaleY(770));
		
		p.text("Heading: " + Maths.decimalFormat((selectedCreature.getRotation() * 180 / Math.PI)), Maths.scaleX(1620), Maths.scaleY(800));
		p.text("Generation: " + selectedCreature.getGeneration(), Maths.scaleX(1620), Maths.scaleY(830));
		
		selectedCreature.drawCreatureBrain();

		p.popStyle();
	}

	public void update(double timeInterval)
	{	
		for(Creature creature : creatures)
		{
			creature.update(timeInterval);
		}
	}

	public void addCreature()
	{
		Statistics.creatureCount++;
		creatures.add(new Creature(p, Maths.scaleX(50) + (int) (Math.random() * Maths.scaleX(1500)),
				Maths.scaleX(50) + (int) (Math.random() * Maths.scaleX(1500)), Statistics.creatureCount, 0));
	}

	public void addCreature(int x, int y)
	{
		Statistics.creatureCount++;
		creatures.add(new Creature(p, x, y, Statistics.creatureCount, 0));
	}

	public static boolean isCreatureAt(double xCoor, double yCoor)
	{
		boolean isCreature = false;
		int xCoord = (int) xCoor;
		int yCoord = (int) yCoor;
		for(Creature creature : creatures)
		{
			if(Math.hypot(xCoord - creature.getX(), yCoord - creature.getY()) < creature.getDiameter() / 2)
			{
				isCreature = true;
			}
		}
		return isCreature;
	}

	public void killAll()
	{
		for(Creature creature : creatures)
		{
			creatures.remove(creature);
		}
	}

	public void checkForDeaths()
	{
		for(Creature creature : creatures)
		{
			if(creature.getSize() < 100)
			{
				if(creature == selectedCreature)
				{
					Menu.setMenuMode(MenuMode.MAIN);
				}
				
				creatures.remove(creature);
				Statistics.creatureDeaths++;
			}
		}
	}

	//TODO: fix this its mega bad and breaks
	public int getCreatureCount()
	{
		Statistics.creatureCount = creatures.size();
		return Statistics.creatureCount;
	}
}
