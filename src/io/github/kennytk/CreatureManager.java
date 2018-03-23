package io.github.kennytk;

import java.util.ArrayList;

import processing.core.PApplet;

public class CreatureManager implements IDrawable
{
	private PApplet p;

	private ArrayList<Creature> creatures = new ArrayList<Creature>();
	
	private Creature selectedCreature = null;

	public CreatureManager(PApplet p)
	{
		this.p = p;
	}

	public void setup()
	{
		for(int i = 0; i < Statistics.startNumCreatures; i++)
		{
			Statistics.creatureCount++; //TODO: VVV
			creatures.add(new Creature(p, Maths.scaleX(50) + (int) (Math.random() * 4 * Maths.scaleX(1500)),
					Maths.scaleX(50) + (int) (Math.random() * 4 * Maths.scaleX(1500)), Statistics.creatureCount, 0, Globals.mutationFactor));
		}
	}

	public void draw()
	{
		p.pushStyle();

		p.colorMode(p.RGB);

		p.fill(60, 120);

		p.rect(Maths.scaleX(1600), 0, Maths.scaleX(1200), Maths.scaleY(2000));

		p.fill(255, 255, 255);

		p.ellipse(Maths.scaleX(1700), Maths.scaleY(320), Maths.scaleY(150), Maths.scaleY(150)); // draw the creature
		p.textSize(Maths.scaleX(70));

		p.text("Selected Creature Data", Maths.scaleX(1620), Maths.scaleY(190));
		p.textSize(Maths.scaleX(30));
		p.text("ID: " + selectedCreature.ID, Maths.scaleX(1620), Maths.scaleY(500));
		p.text("Current Size: " + (int) selectedCreature.size, Maths.scaleX(1620), Maths.scaleY(530));
		p.text("Total Eaten: " + df.format(selectedCreature.totalEaten), Maths.scaleX(1620), Maths.scaleY(560));
		p.text("Total Decayed: " + df.format(selectedCreature.totalDecayed), Maths.scaleX(1620), Maths.scaleY(590));
		p.text("Location: (" + df.format(selectedCreature.locationX) + ", " + df.format(selectedCreature.locationY) + " )",
				Maths.scaleX(1620), Maths.scaleY(620));
		p.text("Left Sensor: (" + df.format(selectedCreature.leftSensorX) + ", " + df.format(selectedCreature.leftSensorY) + " )",
				Maths.scaleX(1620), Maths.scaleY(650));
		p.text("Mid Sensor: (" + df.format(selectedCreature.midSensorX) + ", " + df.format(selectedCreature.midSensorY) + " )",
				Maths.scaleX(1620), Maths.scaleY(680));
		p.text("Right Sensor: (" + df.format(selectedCreature.rightSensorX) + ", " + df.format(selectedCreature.rightSensorY) + " )",
				Maths.scaleX(1620), Maths.scaleY(710));
		p.text("Mouth Sensor: (" + df.format(selectedCreature.mouthSensorX) + ", " + df.format(selectedCreature.mouthSensorY) + " )",
				Maths.scaleX(1620), Maths.scaleY(740));
		p.text("Food Under Me: " + df.format(world.findTileAt(selectedCreature.mouthSensorX, selectedCreature.mouthSensorY, true).food),
				Maths.scaleX(1620), Maths.scaleY(770));
		p.text("Heading: " + df.format((selectedCreature.rotation * 180 / Math.PI)), Maths.scaleX(1620), Maths.scaleY(800));
		p.text("Generation: " + selectedCreature.generation, Maths.scaleX(1620), Maths.scaleY(830));
		p.drawCreatureBrain(selectedCreature);

		p.popStyle();
	}
	
	public void update(double timeInterval)
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
	

	public void addCreature()
	{
		Statistics.creatureCount++;
		creatures.add(new Creature(p, Maths.scaleX(50) + (int) (Math.random() * Maths.scaleX(1500)),
				Maths.scaleX(50) + (int) (Math.random() * Maths.scaleX(1500)), Statistics.creatureCount, 0, Globals.mutationFactor));
	}

	public void addCreature(int x, int y)
	{
		Statistics.creatureCount++;
		creatures.add(new Creature(p, x, y, Statistics.creatureCount, 0, Globals.mutationFactor));
	}
	
	public int getCreatureCount()
	{
		return creatures.size();
	}
}
