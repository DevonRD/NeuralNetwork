package io.github.kennytk;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import processing.core.PApplet;
import processing.event.MouseEvent;

public class Run extends PApplet
{
	public World world;
	private PopulationGraph populationGraph;

	int rawTime;
	double time;
	double timeInterval;
	Tile selectedTile;
	Creature selectedCreature;

	ButtonToggle start;
	ButtonClick killAll, spawn, kill, spawn20;

	boolean spawnClicking;
	int timeSeconds;

	boolean spawnMode;

	double scaleFactor;

	int translateX, translateY;

	int delta;

	int b4x, b4y;

	int deltaX, deltaY;

	DecimalFormat df;

	BufferedImage map;

	boolean[][] water;

	static String[] mapOptions = { "map1", "Large_Island", "Three_Islands", "All_Land", "All_Water" };

	static String fileExt = ".jpg";

	static String selectedMap;

	final double MUTATE_FACTOR = 0.05;

	private enum Path
	{
		GENERAL, CREATURE, DATA, TILE;
	}

	Path path;

	public static void main(String[] args)
	{
		JFrame frame = new JFrame("Input Dialog");
		selectedMap = (String) JOptionPane.showInputDialog(frame, "Select a map to use.", "Map Selector", JOptionPane.QUESTION_MESSAGE,
				null, mapOptions, mapOptions[0]);
		PApplet.main("io.github.kennytk.Run");
	}

	public void settings() // w 1920 h 1080
	{
		Globals.realWidth = displayWidth - 136;

		Globals.realHeight = displayHeight - 224;

		size(Globals.realWidth, Globals.realHeight);
		path = Path.GENERAL;
		// startNumCreatures = 100;
		timeInterval = 0.10;
	}

	public void setup()
	{
		frameRate(60);

		water = new boolean[100][100];

		try
		{
			water = setupMap();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

		world = new World(this, Statistics.startNumCreatures, water, MUTATE_FACTOR);

		world.startTiles();

		world.startCreatures();

		selectedTile = null;

		selectedCreature = null;

		start = new ButtonToggle(this, Maths.scaleX(45), Maths.scaleY(20), Maths.scaleX(120), Maths.scaleY(60), "PLAY", "PAUSE"); // +150 for next over
		kill = new ButtonClick(this, Maths.scaleX(175), Maths.scaleY(20), Maths.scaleX(120), Maths.scaleY(60), "KILL");
		killAll = new ButtonClick(this, Maths.scaleX(305), Maths.scaleY(20), Maths.scaleX(120), Maths.scaleY(60), "KILL ALL");
		spawn = new ButtonClick(this, Maths.scaleX(435), Maths.scaleY(20), Maths.scaleX(120), Maths.scaleY(60), "SPAWN");
		spawn20 = new ButtonClick(this, Maths.scaleX(565), Maths.scaleY(20), Maths.scaleX(120), Maths.scaleY(60), "SPAWN 20");

		start.activate();

		spawnClicking = false;
		timeSeconds = 0;
		spawnMode = false;
		scaleFactor = 0.25;
		translateX = 0;
		translateY = 0;
		b4x = 0;
		b4y = 0;
		deltaX = 0;
		deltaY = 0;
		time = 0;
		df = new DecimalFormat("##.##");
		df.setRoundingMode(RoundingMode.DOWN);

		populationGraph = new PopulationGraph(this);
	}

	public void draw()
	{
		if(mousePressed)
		{
			b4x = mouseX;
			b4y = mouseY;
		}

		if(start.getState())
		{
			rawTime++;
			time += timeInterval;
			world.iterate(timeInterval); // tiles then creatures
			checkForDeaths();

			if(world.getCreatureCount() > Statistics.maxObservedCreatures)
				Statistics.maxObservedCreatures = world.getCreatureCount();
			if(rawTime % 30 == 0)
			{
				Statistics.popHistory.add((double) (world.getCreatureCount()));
			}
		}

		pushMatrix();

		translate(translateX, translateY);
		scale((float) scaleFactor);
		colorMode(RGB);
		background(100);
		fill(60);
		rect(Maths.scaleX(8), 0, Maths.scaleX(6), Maths.scaleY(10));
		fill(255, 255, 255);
		textSize(Maths.scaleX(30));
		// text("Code Iterations: " + rawTime, p2pl(650), p2pw(35));
		// text("Framerate: " + (int)frameRate, p2pl(50), p2pw(35));
		// text("Global Time: " + df.format(time), p2pl(300), p2pw(35));
		drawTiles();
		drawCreatures();

		popMatrix();

		/*
		 * good increments
		 * 
		 * //////// screen X: 1920
		 * 
		 * 120 = 1920 * 1/16
		 * 240 = 1920 * 1/8
		 * 480 = 1920 * 1/4
		 * 960 = 1920 * 1/2
		 * 
		 * 1200 = 1920 * 5/8
		 * 
		 * //////// screen Y: 1080
		 * 
		 * 135 = 1080 * 1/8
		 * 270 = 1080 * 1/4
		 * 540 = 1080 * 1/2
		 * 
		 * 45 = 1080 * 1/24
		 * 90 = 1080 * 1/12
		 * 180 = 1080 * 1/6
		 * 360 = 1080 * 1/3
		 * 
		 * 810 = 1080 * 3/4
		 * 
		 * //////// menu X: 720
		 * 
		 * 45 = 720 * 1/16
		 * 90 = 720 * 1/8
		 * 180 = 720 * 1/4
		 * 360 = 720 * 1/2
		 * 
		 * 30 = 720 * 1/24
		 * 60 = 720 * 1/12
		 * 120 = 720 * 1/6
		 * 240 = 720 * 1/3
		 * 
		 * 540 = 720 * 3/4
		 * 
		 * //////// menu Y: 1080 (same as screen)
		 * 
		 */

		switch(path) // side bar path split
		{
			case GENERAL:
			{
				pushMenu();

				drawButtons();

				populationGraph.draw();

				popMenu();

				break;
			}

			case CREATURE:
			{
				pushMenu();

				popMenu();

				break;
			}
			case DATA:
			{
				pushMenu();

				popMenu();

				break;
			}
			case TILE:
			{
				pushMenu();

				popMenu();
				break;
			}
		}
	}

	public void pushMenu()
	{
		pushStyle();

		colorMode(RGB);
		fill(60, 120);
		stroke(0);
		strokeWeight(3);

		// x1, y1, x2, y2
		rect(Maths.scaleX(Globals.menuBasePointX), 0, Maths.scaleX(1080), Maths.scaleY(1920));

		popStyle();

		pushMatrix();
		pushStyle();

		translate(Maths.scaleX(1200), 0);
	}

	public void popMenu()
	{
		popMatrix();
		popStyle();
	}

	public void drawButtons()
	{
		// textSize(p2pl(40));

		start.draw();
		killAll.draw();
		kill.draw();
		spawn.draw();
		spawn20.draw();

		// if(play)
		// fill(119, 255, 51);
		// else
		// fill(255, 51, 51);
		//
		// if(play)
		// text("On", start.getX() + p2pl(20), start.getY() + p2pw(60)); // +150 for next over
		// else
		// text("Off", start.getX() + p2pl(20), start.getY() + p2pw(60));

		// fill(255, 255, 255);

		// text("Kill All", killAll.getX() + p2pl(20), killAll.getY() + p2pw(60));

		// if(spawnMode)
		// fill(119, 255, 51);
		// else
		// fill(255, 51, 51);

		// text("Spawn", spawn.getX() + p2pl(20), spawn.getY() + p2pw(60));
		// fill(255, 255, 255);
		// text("Spawn 20", spawn20.getX() + p2pl(20), spawn20.getY() + p2pw(60));
	}

	public void drawCreatures()
	{
		colorMode(RGB);
		fill(255);
		for(int i = 0; i < world.creatures.size(); i++)
		{
			stroke(0);
			line((int) world.creatures.get(i).locationX, (int) world.creatures.get(i).locationY, (int) world.creatures.get(i).leftSensorX,
					(int) world.creatures.get(i).leftSensorY);
			line((int) world.creatures.get(i).locationX, (int) world.creatures.get(i).locationY, (int) world.creatures.get(i).midSensorX,
					(int) world.creatures.get(i).midSensorY);
			stroke(255);
			line((int) world.creatures.get(i).locationX, (int) world.creatures.get(i).locationY, (int) world.creatures.get(i).rightSensorX,
					(int) world.creatures.get(i).rightSensorY);
			stroke(0);
			if(selectedCreature != null && world.creatures.get(i).ID == selectedCreature.ID)
				fill(240, 0, 255);
			ellipse((int) world.creatures.get(i).locationX, (int) world.creatures.get(i).locationY, (int) world.creatures.get(i).diameter,
					(int) world.creatures.get(i).diameter);
			fill(255);
			ellipse((int) world.creatures.get(i).leftSensorX, (int) world.creatures.get(i).leftSensorY, 7, 7);
			ellipse((int) world.creatures.get(i).rightSensorX, (int) world.creatures.get(i).rightSensorY, 7, 7);
			ellipse((int) world.creatures.get(i).mouthSensorX, (int) world.creatures.get(i).mouthSensorY, 7, 7);
		}
	}

	public void drawSingleCreature(Creature c) // not done yet
	{
		colorMode(RGB);
		fill(255);
		for(int i = 0; i < world.getCreatureCount(); i++)
		{
			stroke(0);
			line((int) c.locationX, (int) c.locationY, (int) c.leftSensorX, (int) c.leftSensorY);
			line((int) c.locationX, (int) c.locationY, (int) c.midSensorX, (int) c.midSensorY);
			stroke(255);
			line((int) c.locationX, (int) c.locationY, (int) c.rightSensorX, (int) c.rightSensorY);
			stroke(0);
			fill(240, 0, 255);
			ellipse((int) c.locationX, (int) c.locationY, (int) c.diameter, (int) c.diameter);
			fill(255);
			ellipse((int) c.leftSensorX, (int) c.leftSensorY, 7, 7);
			ellipse((int) c.rightSensorX, (int) c.rightSensorY, 7, 7);
			ellipse((int) c.mouthSensorX, (int) c.mouthSensorY, 7, 7);
		}
		fill(255);
	}

	public void tileSelected(int yIndex, int xIndex)
	{
		selectedTile = world.tiles[yIndex][xIndex];
	}

	public void checkForDeaths()
	{
		for(int i = 0; i < world.getCreatureCount(); i++)
		{
			if(world.creatures.get(i).size < 30)
			{
				if(world.creatures.get(i) == selectedCreature)
					path = Path.GENERAL;
				world.creatures.remove(i);
				Statistics.creatureDeaths++;
				return;
			}
			if(world.creatures.get(i).size < 100)
			{
				if(world.creatures.get(i) == selectedCreature)
					path = Path.GENERAL;
				world.creatures.remove(i);
				Statistics.creatureDeaths++;
			}
		}
	}

	public void killAll()
	{
		while(!world.creatures.isEmpty())
		{
			world.creatures.remove(0);
		}
	}

	public void drawCreatureBrain(Creature c) // top left = 1620, 800
	{
		int verticalSpacing = Maths.scaleY(70);
		textSize(Maths.scaleY(50));
		text("Input", Maths.scaleX(1620), Maths.scaleY(900));
		text("Layer 1", Maths.scaleX(1900), Maths.scaleY(900));
		text("Layer 2", Maths.scaleX(2120), Maths.scaleY(900));
		text("Output", Maths.scaleX(2420), Maths.scaleY(900));
		colorMode(RGB);
		for(int i = 0; i < c.inputNeurons.length; i++)
		{
			fill(255);
			textSize(Maths.scaleY(30));
			// rect(p2pl(1620), p2pw(800 + verticalSpacing * i), p2pl(100), p2pw(50));
			text(df.format(c.inputNeurons[i]) + "", Maths.scaleX(1620), Maths.scaleY(950) + verticalSpacing * i);
		}
		for(int i = 0; i < c.hidLayer1.length; i++)
		{
			fill(255);
			textSize(Maths.scaleY(30));
			// rect(p2pl(1620), p2pw(800 + verticalSpacing * i), p2pl(100), p2pw(50));
			text(df.format(c.hidLayer1[i]) + "", Maths.scaleX(1900), Maths.scaleY(950) + Maths.scaleY(40) * i);
		}
		for(int i = 0; i < c.hidLayer2.length; i++)
		{
			fill(255);
			textSize(Maths.scaleY(30));
			// rect(p2pl(1620), p2pw(800 + verticalSpacing * i), p2pl(100), p2pw(50));
			text(df.format(c.hidLayer2[i]) + "", Maths.scaleX(2120), Maths.scaleY(950) + Maths.scaleY(40) * i);
		}
		for(int i = 0; i < c.outputNeurons.length; i++)
		{
			fill(255);
			textSize(Maths.scaleY(30));
			// rect(p2pl(1620), p2pw(800 + verticalSpacing * i), p2pl(100), p2pw(50));
			text(df.format(c.outputNeurons[i]) + "", Maths.scaleX(2420), Maths.scaleY(950) + verticalSpacing * i);
		}

		for(int i = 0; i < c.inputNeurons.length; i++)
		{
			int color = 0;
			for(int one = 0; one < c.hidLayer1.length; one++)
			{
				color = (int) (Maths.sigmoid(c.inputToLayer1Axons[i][one].weight + 1.0) * 255);
				if(color < 0)
					color = 0;
				if(color > 255)
					color = 255;
				stroke(color);
				line(Maths.scaleX(1700), Maths.scaleY(945) + verticalSpacing * i, Maths.scaleX(1890),
						Maths.scaleY(940) + Maths.scaleY(40) * one);
			}
		}
		for(int i = 0; i < c.hidLayer2.length; i++)
		{
			int color = 0;
			for(int o = 0; o < c.outputNeurons.length; o++)
			{
				color = (int) (Maths.sigmoid(c.layer2ToOutputAxons[i][o].weight + 1.0) * 255);
				if(color < 0)
					color = 0;
				if(color > 255)
					color = 255;
				stroke(color);
				line(Maths.scaleX(2200), Maths.scaleY(945) + Maths.scaleY(40) * i, Maths.scaleX(2410),
						Maths.scaleY(940) + verticalSpacing * o);
			}
		}
		stroke(0);
	}

	public void keyPressed()
	{
		if(key == 'r')
		{
			translateX = 0;
			translateY = 0;
			scaleFactor = 0.25;
			return;
		}
		else if(key == ' ')
		{
			start.toggle();
		}
		else if(key == 's')
		{
			spawnMode = !spawnMode;
		}
		else if(key == 'k')
		{
			killAll();
		}
	}

	public void mouseDragged()
	{
		deltaX = mouseX - b4x;
		deltaY = mouseY - b4y;
		translateX += deltaX / 1.4;
		translateY += deltaY / 1.4;
	}

	public void mouseClicked()
	{
		int mX = mouseX;
		int mY = mouseY;
		boolean check = false;

		if(mX <= Maths.scaleX(1200))
		{
			check = true;
			mX -= translateX;
			mY -= translateY;
			mX /= scaleFactor;
			mY /= scaleFactor;
		}

		// test for button click
		if(start.isClicked(mX, mY))
		{
			start.toggle();
			return;
		}

		if(killAll.isClicked(mX, mY))
		{
			killAll();
			return;
		}

		if(spawn.isClicked(mX, mY))
		{
			spawnMode = !spawnMode;
			return;

		}

		if(spawn20.isClicked(mX, mY))
		{
			for(int i = 0; i < 20; i++)
			{
				world.addCreature();
			}
			return;
		}

		if(check)
		{
			// spawn mode?
			if(spawnMode && check)
			{
				if(spawn.getX() < mX && mX <= spawn.getX() + spawn.getWidth()) // start button
				{
					if(spawn.getY() < mY && mY <= spawn.getY() + spawn.getHeight())
					{
						spawnMode = !spawnMode;
					}
				}
				if(world.tiles[0][0].x <= mX && mX <= world.tiles[world.tileResW - 1][world.tileResL - 1].x + world.tileSize
						&& world.tiles[0][0].y <= mY && mY <= world.tiles[world.tileResW - 1][world.tileResL - 1].y + world.tileSize)
				{
					if(spawnMode)
						world.addCreature(mX, mY);
				}
				return;
			}

			// test for creature click
			for(int i = 0; i < world.creatures.size(); i++)
			{
				if(Math.hypot(mX - world.creatures.get(i).locationX,
						mY - world.creatures.get(i).locationY) < world.creatures.get(i).diameter / 2)
				{
					selectedCreature = world.creatures.get(i);
					path = Path.CREATURE;
					return;
				}
			}

			// test for tile click
			for(int x = 0; x < world.tiles.length; x++)
			{
				for(int y = 0; y < world.tiles.length; y++)
				{
					if(world.tiles[y][x].x < mX && mX <= world.tiles[y][x].x + world.tileSize)
					{
						if(world.tiles[y][x].y < mY && mY <= world.tiles[y][x].y + world.tileSize)
						{
							selectedTile = world.tiles[y][x];
							path = Path.TILE;
							return;
						}
					}
				}
			}
		}

		path = Path.GENERAL;
	}

	@SuppressWarnings("deprecation")
	public void mouseWheel(MouseEvent e)
	{

		scaleFactor -= e.getAmount() / 15.0;
		if(scaleFactor < 0.2)
			scaleFactor = 0.2;
		if(scaleFactor > 3.0)
			scaleFactor = 3.0;
		if(scaleFactor != 0.2 && scaleFactor != 3.0)
		{

			translateX += e.getAmount() * mouseX * 4 / 10;
			translateY += e.getAmount() * mouseY * 4 / 10;
		}
		// else if(scaleFactor != 0.2 && scaleFactor != 3.0 && e.getAmount() > 0)
		// {
		// System.out.println("in");
		// translateX += e.getAmount() * 520 * 4 / 10;
		// translateY += e.getAmount() * 400 * 4 / 10;
		// }

	}

	public boolean[][] setupMap() throws IOException
	{
		boolean[][] results = new boolean[100][100];
		map = ImageIO.read(Run.class.getResource(selectedMap + fileExt));
		for(int row = 0; row < 100; row++)
		{
			for(int col = 0; col < 100; col++)
			{
				Color c = new Color(map.getRGB(col, row));
				if(c.getGreen() > 50)
					results[col][row] = false;
				else if(c.getBlue() > 50)
					results[col][row] = true;
				else
					results[col][row] = false;
			}
		}
		return results;
	}
}
