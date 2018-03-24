package io.github.kennytk;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import io.github.kennytk.Globals.MenuMode;
import processing.core.PApplet;
import processing.event.MouseEvent;

public class Run extends PApplet
{
	// add pi chart
	// add fps / time chart\
	// add chart selector
	// add family trees fullscreen
	// add backspace button
	// add time interval changer

	public Map map;
	public TileManager tileManager;
	public CreatureManager creatureManager;
	private PopulationGraph populationGraph;

	int rawTime;
	double time;
	double timeInterval;

	Creature selectedCreature;

	ButtonToggle start;
	ButtonClick killAll, spawn, kill, spawnTwenty;

	boolean spawnClicking;
	int timeSeconds;

	int translateX, translateY;

	int delta;

	int b4x, b4y;

	int deltaX, deltaY;

	private static String[] mapOptions = { "map1", "Large_Island", "Three_Islands", "All_Land", "All_Water" };

	private static String fileExt = ".jpg";

	private static String selectedMap;

	public static void main(String[] args)
	{

		// replace with start screen
		JFrame frame = new JFrame("Input Dialog");
		frame.setSize(400, 400);

		// move this line and variables (or start screen equiv. to Map)
		selectedMap = (String) JOptionPane.showInputDialog(frame, "Select a map to use.", "Map Selector", JOptionPane.QUESTION_MESSAGE,
				null, mapOptions, mapOptions[0]);

		// get fileExt from file name when selection map to support .png and .jpg

		PApplet.main("io.github.kennytk.Run");
	}

	public void settings() // w 1920 h 1080
	{
		Globals.realWidth = displayWidth - 136;

		Globals.realHeight = displayHeight - 224;

		size(Globals.realWidth, Globals.realHeight);

		Globals.menuMode = MenuMode.MAIN;

		// startNumCreatures = 100;
		timeInterval = 0.10;
	}

	public void setup()
	{
		frameRate(60);

		map = new Map(selectedMap, fileExt);

		tileManager = new TileManager(this, 100, 100);

		tileManager.setup();

		creatureManager = new CreatureManager(this);

		creatureManager.setup();

		// world = new World(this, Statistics.startNumCreatures, mapData, MUTATE_FACTOR);

		// world.startTiles();

		// world.startCreatures();

		// selectedTile = null;
		// selectedCreature = null;

		start = new ButtonToggle(this, Maths.scaleX(45), Maths.scaleY(20), Maths.scaleX(120), Maths.scaleY(60), "PLAY", "PAUSE"); // +150 for next over
		kill = new ButtonClick(this, Maths.scaleX(175), Maths.scaleY(20), Maths.scaleX(120), Maths.scaleY(60), "KILL");
		killAll = new ButtonClick(this, Maths.scaleX(305), Maths.scaleY(20), Maths.scaleX(120), Maths.scaleY(60), "KILL ALL");
		spawn = new ButtonClick(this, Maths.scaleX(435), Maths.scaleY(20), Maths.scaleX(120), Maths.scaleY(60), "SPAWN");
		spawnTwenty = new ButtonClick(this, Maths.scaleX(565), Maths.scaleY(20), Maths.scaleX(120), Maths.scaleY(60), "SPAWN 20");

		start.activate();

		spawnClicking = false;
		timeSeconds = 0;
		translateX = 0;
		translateY = 0;
		b4x = 0;
		b4y = 0;
		deltaX = 0;
		deltaY = 0;
		time = 0;

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

			iterate(timeInterval); // calculate maths for tiles then creatures

			creatureManager.checkForDeaths();

			if(creatureManager.getCreatureCount() > Statistics.maxObservedCreatures)
				Statistics.maxObservedCreatures = Statistics.creatureCount;

			if(rawTime % 20 == 0) // default 30
			{
				Statistics.popHistory.add((double) (Statistics.creatureCount));
			}
		}

		pushMatrix();

		// I think this is the zooming

		background(200);

		translate(translateX, translateY);
		
		scale((float) Globals.scaleFactor);

		tileManager.draw();
		creatureManager.draw();

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

		switch(Globals.menuMode) // side bar path split
		{
			case MAIN:
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
		/*
		 * @see ButtonToggle for beautification
		 */

		start.draw();
		killAll.draw();
		kill.draw();
		spawn.draw();
		spawnTwenty.draw();
	}

	public void iterate(double timeInterval)
	{
		System.out.println("run iterate start");
		tileManager.update(timeInterval);
		creatureManager.update(timeInterval);
		System.out.println("run iterate end");
	}

	public void keyPressed()
	{
		if(key == 'r')
		{
			translateX = 0;
			translateY = 0;
			Globals.scaleFactor = 0.25;
			return;
		}
		else if(key == ' ')
		{
			start.toggle();
		}
		else if(key == 's')
		{
			// toggle spawn mode
		}
		else if(key == 'k')
		{
			creatureManager.killAll();
		}
	}

	public void mouseDragged()
	{
		deltaX = mouseX - b4x;
		deltaY = mouseY - b4y;
		
		translateX += deltaX / Globals.dragRatio;
		translateY += deltaY / Globals.dragRatio;
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
			
			//mX /= scaleFactor;
			//mY /= scaleFactor;
		}

		// test for button click
		if(start.isClicked(mX, mY))
		{
			start.toggle();
			return;
		}

		if(killAll.isClicked(mX, mY))
		{
			creatureManager.killAll();
			return;
		}

		if(spawn.isClicked(mX, mY))
		{
			// toggle spawn mode
			return;

		}

		if(spawnTwenty.isClicked(mX, mY))
		{
			for(int i = 0; i < 20; i++)
			{
				creatureManager.addCreature();
			}
			return;
		}

		if(check)
		{
			// this is to spawn creatures via the spawnmode button, TODO: bake into button functionality

			// if(spawnMode && check)
			// {
			// if(spawn.getX() < mX && mX <= spawn.getX() + spawn.getWidth()) // start button
			// {
			// if(spawn.getY() < mY && mY <= spawn.getY() + spawn.getHeight())
			// {
			// // toggle spawn mode
			// }
			// }
			// if(world.tiles[0][0].x <= mX && mX <= world.tiles[world.tileResW - 1][world.tileResL - 1].x + world.tileSize
			// && world.tiles[0][0].y <= mY && mY <= world.tiles[world.tileResW - 1][world.tileResL - 1].y + world.tileSize)
			// {
			// if(spawnMode)
			// world.addCreature(mX, mY);
			// }
			// return;
			// }

			if(creatureManager.click(mX, mY))
				return;

			// click tyle
			// this will not work
			if(tileManager.click(mX, mY))
				return;

		}

		Globals.menuMode = MenuMode.MAIN;
	}

	@SuppressWarnings("deprecation")
	public void mouseWheel(MouseEvent e)
	{

		Globals.scaleFactor -= e.getAmount() / 15.0;
		if(Globals.scaleFactor < 0.2)
			Globals.scaleFactor = 0.2;
		if(Globals.scaleFactor > 3.0)
			Globals.scaleFactor = 3.0;
		if(Globals.scaleFactor != 0.2 && Globals.scaleFactor != 3.0)
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
}
