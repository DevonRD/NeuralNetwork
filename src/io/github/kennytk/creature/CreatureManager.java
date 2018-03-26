package io.github.kennytk.creature;

import java.util.ArrayList;

import io.github.kennytk.IDrawable;
import io.github.kennytk.Menu;
import io.github.kennytk.numbers.Globals;
import io.github.kennytk.numbers.Maths;
import io.github.kennytk.numbers.Statistics;
import io.github.kennytk.numbers.Globals.MenuMode;
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
			Statistics.creatureCount++; // TODO: VVV 500 is temp to get them out of the corner
			creatures.add(new Creature(p, 500 + (int) (Math.random() * Maths.scaleX(1200)),
					500 + (int) (Math.random() * Maths.scaleY(1080)), Statistics.creatureCount, 0));
		}

		System.out.println("creatureManager setup complete");
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

		p.text("Left Sensor: (" + Maths.decimalFormat(selectedCreature.getLeftSensorX()) + ", "
				+ Maths.decimalFormat(selectedCreature.getLeftSensorY()) + " )", Maths.scaleX(1620), Maths.scaleY(650));

		p.text("Mid Sensor: (" + Maths.decimalFormat(selectedCreature.getMidSensorX()) + ", "
				+ Maths.decimalFormat(selectedCreature.getMidSensorY()) + " )", Maths.scaleX(1620), Maths.scaleY(680));

		p.text("Right Sensor: (" + Maths.decimalFormat(selectedCreature.getRightSensorX()) + ", "
				+ Maths.decimalFormat(selectedCreature.getRightSensorY()) + " )", Maths.scaleX(1620), Maths.scaleY(710));

		p.text("Mouth Sensor: (" + Maths.decimalFormat(selectedCreature.getMouthSensorX()) + ", "
				+ Maths.decimalFormat(selectedCreature.getMouthSensorY()) + " )", Maths.scaleX(1620), Maths.scaleY(740));

		// needs different way to access TileManager to get findTileAt method
		// p.text("Food Under Me: " + Maths.decimalFormat(world.findTileAt(selectedCreature.getMouthSensorX(), selectedCreature.getMouthSensorY(), true).food),
		// Maths.scaleX(1620), Maths.scaleY(770));

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

	public boolean click(double mX, double mY)
	{
		for(Creature creature : creatures)
		{
			if(Math.hypot(mX - creature.getX(), mY - creature.getY()) < creature.getDiameter() / 2)
			{
				selectedCreature = creature;
				Globals.menuMode = MenuMode.CREATURE;
				return true;
			}
		}

		return false;
	}

	public static void addCreature(Creature creature)
	{
		Statistics.creatureCount++;
		creatures.add(creature);
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

	private ArrayList<Creature> toKill = new ArrayList<>();

	public void killAll()
	{
		Menu.setMenuMode(MenuMode.MAIN);

		toKill.addAll(creatures);
		Statistics.creatureDeaths += creatures.size();

		creatures.removeAll(toKill);
		toKill.clear();
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

				toKill.add(creature);
				Statistics.creatureDeaths++;
			}
		}
		creatures.removeAll(toKill);
		toKill.clear();
	}

	// TODO: fix this its mega bad and breaks
	public int getCreatureCount()
	{
		Statistics.creatureCount = creatures.size();
		return Statistics.creatureCount;
	}

	// method is currently broken do not use
	// @SuppressWarnings("unused")
	// private Point2D getTilePoint(double xP, double yP)
	// {
	// xP = (int) xP;
	// yP = (int) xP;
	//
	// Point2D point = null;
	//
	// for(int x = 0; x < TileManager.getHorizontalNum(); x++)
	// {
	// for(int y = 0; y < TileManager.getVerticalNum(); y++)
	// {
	// // xP, yP and x,y might need to be switched inside if statements
	// if(TileManager.getTileFromPixels(x, y).getX() < xP
	// && xP <= TileManager.getTileFromPixels(x, y).getX() + TileManager.getTileSize())
	// {
	// if(TileManager.getTileFromPixels(x, y).getY() < yP
	// && yP <= TileManager.getTileFromPixels(x, y).getY() + TileManager.getTileSize())
	// {
	// point.setLocation(x, y);
	// return point;
	// }
	// }
	// }
	// }
	//
	// System.out.println("ERROR - COULD NOT FIND TILE POINT");
	// return null;
	// }
}
