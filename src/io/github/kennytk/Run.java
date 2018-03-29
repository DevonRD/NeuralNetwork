package io.github.kennytk;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import io.github.kennytk.button.ButtonClick;
import io.github.kennytk.button.ButtonRightTriangle;
import io.github.kennytk.button.ButtonToggle;
import io.github.kennytk.creature.CreatureManager;
import io.github.kennytk.graph.PopulationGraph;
import io.github.kennytk.numbers.Globals;
import io.github.kennytk.numbers.Globals.MenuMode;
import io.github.kennytk.numbers.Maths;
import io.github.kennytk.numbers.Statistics;
import io.github.kennytk.tile.Map;
import io.github.kennytk.tile.TileManager;
import processing.core.PApplet;
import processing.event.MouseEvent;

public class Run extends PApplet
{
	// create play buttons along bottom
	// remove crappy buttons
	// make creature interaction menu
	// add all cases to top of thing to switch between
	// create data scroll bar
	// make mouse following for tile and for creature
	// make gant or whatever its called chart
	// make pi chart

	// add fps / time chart\
	// add chart selector
	// add family trees fullscreen
	// add backspace button
	// add time interval changer

	public Map map;
	public TileManager tileManager;
	public CreatureManager creatureManager;
	public PopulationGraph populationGraph;
	public Menu menu;

	int rawTime;
	double time;
	double timeInterval;

	ButtonToggle startToggle, spawnToggle;
	ButtonClick killAll, kill, spawnX;
	ButtonRightTriangle menuRight;

	boolean spawnClicking;
	int timeSeconds;

	int translateX, translateY;

	int delta;

	private static String[] mapOptions = { "map1", "map2", "Large_Island", "Three_Islands", "All_Land", "All_Water" };

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

		Globals.menuTextSize = Maths.scaleY(30);
		Globals.buttonTextSize = Maths.scaleY(30);
		Globals.menuTitleSize = Maths.scaleY(40);

		Globals.menuMode = MenuMode.MAIN;

		// startNumCreatures = 100;
		timeInterval = 0.10;
	}

	public void setup()
	{
		frameRate(60);

		map = new Map(selectedMap, fileExt);

		tileManager = new TileManager(this, map.getWidthIndex(), map.getHeightIndex());

		tileManager.setup();

		creatureManager = new CreatureManager(this);

		creatureManager.setup();

		menu = new Menu(this);

		startToggle = new ButtonToggle(this, Maths.scaleX(20), Maths.scaleY(60), Maths.scaleX(120), Maths.scaleY(60), "Pause", "Play");

		spawnToggle = new ButtonToggle(this, Maths.scaleX(150), Maths.scaleY(60), Maths.scaleX(120), Maths.scaleY(60), "Spawn", "Spawn");

		kill = new ButtonClick(this, Maths.scaleX(150), Maths.scaleY(60), Maths.scaleX(120), Maths.scaleY(60), 30, "Kill"); // should be moved to creature

		killAll = new ButtonClick(this, Maths.scaleX(180), Maths.scaleY(60), Maths.scaleX(160), Maths.scaleY(60), 30, "Kill All");
		spawnX = new ButtonClick(this, Maths.scaleX(180), Maths.scaleY(140), Maths.scaleX(160), Maths.scaleY(60), 30, "Spawn:");

		menuRight = new ButtonRightTriangle(this, 20, 20, 100, 100, 100);

		spawnClicking = false;
		timeSeconds = 0;
		translateX = 0;
		translateY = 0;
		time = 0;

		populationGraph = new PopulationGraph(this);
	}

	public void draw()
	{
		if(startToggle.getState())
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

				menu.setTime(time);
				menu.setFPS(frameRate);
				menu.draw();

				popMenu();

				break;
			}

			case CREATURE:
			{
				pushMenu();

				creatureManager.menu();

				popMenu();

				break;
			}
			case DATA:
			{
				pushMenu();

				populationGraph.draw();

				popMenu();

				break;
			}
			case TILE:
			{
				pushMenu();

				tileManager.menu();

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

		// x1, y1, width, height
		rect(Maths.scaleX(Globals.menuBasePointX), 0, Maths.scaleX(720), Maths.scaleY(1080));

		popStyle();

		pushMatrix();
		pushStyle();

		translate(Maths.scaleX(Globals.menuBasePointX), 0);

		// TODO: create menu switcher
		textSize(Globals.menuTitleSize);
		text("Main Menu", Maths.scaleX(20), Maths.scaleY(40));

		menuRight.draw();
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

		// startToggle.draw();
		killAll.draw();
		// kill.draw();
		// spawnToggle.draw();
		spawnX.draw();
	}

	public void iterate(double timeInterval)
	{
		// System.out.println("run iterate start");
		tileManager.update(timeInterval);
		creatureManager.update(timeInterval);
		// System.out.println("run iterate end");
	}

	public void keyPressed()
	{
		if(key == 'r')
		{
			translateX = 0;
			translateY = 0;
			Globals.scaleFactor = 1f;
			return;
		}
		else if(key == ' ')
		{
			startToggle.toggle();
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

	public void mouseDragged(MouseEvent e)
	{
		translateX += mouseX - pmouseX;
		translateY += mouseY - pmouseY;
	}

	public void mouseClicked()
	{
		int mX = mouseX;
		int mY = mouseY;

		// System.out.println(mX + " : " + mY);

		boolean isOutsideMenu = false;

		if(mX <= Maths.scaleX(1200))
		{
			isOutsideMenu = true;

			mX -= translateX;
			mY -= translateY;

			mX /= Globals.scaleFactor;
			mY /= Globals.scaleFactor;
		}
		else
		{
			isOutsideMenu = false;
		}

		// test for button click
		if(startToggle.isClicked(mX, mY))
		{
			startToggle.toggle();
			return;
		}

		if(killAll.isClicked(mX, mY))
		{
			killAll.start();
			creatureManager.killAll();
			return;
		}

		if(spawnToggle.isClicked(mX, mY))
		{
			spawnToggle.toggle();
			return;
		}

		if(spawnX.isClicked(mX, mY))
		{
			spawnX.start();
			for(int i = 0; i < 20; i++)
			{
				creatureManager.addCreature();
			}
			return;
		}

		if(isOutsideMenu)
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

			// click tile
			// this will not work
			if(tileManager.click(mX, mY))
				return;

		}

		Globals.menuMode = MenuMode.MAIN;
	}

	public void mouseWheel(MouseEvent e)
	{
		float delta = (float) (-e.getCount() > 0 ? 1.05 : -e.getCount() < 0 ? 1.0 / 1.05 : 1.0);

		Globals.scaleFactor *= delta;

		if(!(Globals.scaleFactor > 3.0) && !(Globals.scaleFactor < .2))
		{
			translateX -= mouseX;
			translateY -= mouseY;

			translateX *= delta;
			translateY *= delta;

			translateX += mouseX;
			translateY += mouseY;

		}
		if(Globals.scaleFactor > 3.0)
			Globals.scaleFactor = 3.0f;

		if(Globals.scaleFactor < .2)
			Globals.scaleFactor = .2f;
	}
}
