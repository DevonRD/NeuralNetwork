package Essentials;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import processing.core.PApplet;
import processing.event.MouseEvent;

public class Run extends PApplet
{
	public World world;
	int rawTime;
	double time;
	double timeInterval;
	Tile selectedTile;
	Creature selectedCreature;
	Button start;
	Button killAll;
	Button spawn;
	boolean spawnClicking;
	Button spawn20;
	Button kill;
	boolean play; // pseudo stop boolean
	int startNumCreatures;
	int creatureDeaths;
	ArrayList<Double> popHistory;
	int maxObservedCreatures;
	int timeSeconds;
	int realWidth, realHeight;
	boolean spawnMode;
	double scaleFactor;
	int translateX, translateY;
	int delta;
	int b4x, b4y;
	int deltaX, deltaY;
	DecimalFormat df;
	BufferedImage map;
	boolean[][] water;
	static String[] mapOptions = {"map1", "Large_Island", "Three_Islands", "All_Land", "All_Water"};
	static String fileExt = ".jpg";
	static String selectedMap;
	final double MUTATE_FACTOR = 0.05;
	
	enum Path
	{
		GENERAL, CREATURE, DATA, TILE;
	}
	Path path;
	
	public static void main(String[] args)
	{
		JFrame frame = new JFrame("Input Dialog");
		selectedMap = (String) JOptionPane.showInputDialog(frame, "Select a map to use.", "Map Selector", JOptionPane.QUESTION_MESSAGE, null, mapOptions, mapOptions[0]);
		PApplet.main("Essentials.Run");
	}
	public void settings() // w 2600 h 1600  8/13 RATIO IS BEST!!! actually it doesnt matter really
	{
		realWidth = displayWidth - 136;
		realHeight = displayHeight - 224;
		size(realWidth, realHeight);
		path = Path.GENERAL;
		startNumCreatures = 100;
		timeInterval = 0.05;
	}
	public void setup()
	{
		frameRate(60);
		textSize(50);
		water = new boolean[100][100];
		try
		{
			System.out.println("in the trycatch for setupMap");
			water = setupMap();
		}
		catch(IOException e)
		{
			System.out.println("error in attempt to setupMap");
			e.printStackTrace();
		}
		world = new World(this, startNumCreatures, realWidth, realHeight, water, MUTATE_FACTOR);
		world.startTiles();
		world.startCreatures();
		selectedTile = null;
		selectedCreature = null;
		start = new Button(p2pl(1620), p2pw(20), p2pl(100), p2pw(90)); // +150 for next over
		killAll = new Button(p2pl(1770), p2pw(20), p2pl(170), p2pw(90));
		spawn = new Button(p2pl(1960), p2pw(20), p2pl(160), p2pw(90));
		spawn20 = new Button(p2pl(2140), p2pw(20), p2pl(225), p2pw(90));
		play = true;
		creatureDeaths = 0;
		popHistory = new ArrayList<Double>();
		popHistory.add((double) startNumCreatures);
		maxObservedCreatures = startNumCreatures;
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
	}
	
	public void draw()
	{
		if(mousePressed)
		{
			b4x = mouseX;
			b4y = mouseY;
		}
		if(play)
		{	
			rawTime++;
			time += timeInterval;
			world.iterate(timeInterval); // tiles then creatures
			checkForDeaths();
			
			if(world.creatures.size() > maxObservedCreatures) maxObservedCreatures = world.creatures.size();
			if(rawTime % 30 == 0)
			{
				popHistory.add((double) (world.creatures.size()));
			}
		}
		pushMatrix();
		translate(translateX, translateY);
		scale((float) scaleFactor);
		colorMode(RGB);
		background(100);
		fill(60);
		rect(p2pl(8), 0, p2pl(6), p2pw(10));
		fill(255, 255, 255);
		textSize(p2pl(30));
//		text("Code Iterations: " + rawTime, p2pl(650), p2pw(35));
//		text("Framerate: " + (int)frameRate, p2pl(50), p2pw(35));
//		text("Global Time: " + df.format(time), p2pl(300), p2pw(35));
		drawTiles();
		drawCreatures();
		popMatrix();
		
		
		switch(path) // side bar path split
		{
			case GENERAL:
			{
				colorMode(RGB);
				fill(60, 120);
				rect(p2pl(1600), 0, p2pl(1200), p2pw(2000));
				drawButtons();
				textSize(p2pl(40));
				fill(255, 255, 255);
				text("Starting Creatures: " + startNumCreatures, p2pl(1620), p2pw(350));
				text("Living Creatures: " + (world.creatures.size()), p2pl(1620), p2pw(400));
				String sign;
				if(startNumCreatures > world.creatures.size()) sign = "-";
				else sign = "+";
				text("Total Change: " + sign + Math.abs(world.creatures.size() - startNumCreatures), p2pl(1620), p2pw(450));
				text("Number of Deaths: " + creatureDeaths, p2pl(1620), p2pw(550));
				text("Total Existed Creatures: " + world.creatureCount, p2pl(1620), p2pw(600));
				text("World Time: " + df.format(time), p2pl(1620), p2pw(700));
				text("FPS: " + frameRate, p2pl(1620), p2pw(750));
				text("Successful Births: " + world.births, p2pl(1620), p2pw(850));
				
				drawPopGraph();
				
				break;
			}
			case CREATURE:
			{
				colorMode(RGB);
				fill(60, 120);
				rect(p2pl(1600), 0, p2pl(1200), p2pw(2000));
				drawButtons();
				fill(255, 255, 255);
				ellipse(p2pl(1700), p2pw(320), p2pw(150), p2pw(150)); // draw the creature
				textSize(p2pl(70));
				text("Selected Creature Data", p2pl(1620), p2pw(190));
				textSize(p2pl(30));
				text("ID: " + selectedCreature.ID, p2pl(1620), p2pw(500));
				text("Current Size: " + (int)selectedCreature.size, p2pl(1620), p2pw(530));
				text("Total Eaten: " + df.format(selectedCreature.totalEaten), p2pl(1620), p2pw(560));
				text("Total Decayed: " + df.format(selectedCreature.totalDecayed), p2pl(1620), p2pw(590));
				text("Location: (" + df.format(selectedCreature.locationX) + ", " + df.format(selectedCreature.locationY) + " )", p2pl(1620), p2pw(620));
				text("Left Sensor: (" + df.format(selectedCreature.leftSensorX) + ", " + df.format(selectedCreature.leftSensorY) + " )", p2pl(1620), p2pw(650));
				text("Mid Sensor: (" + df.format(selectedCreature.midSensorX) + ", " + df.format(selectedCreature.midSensorY) + " )", p2pl(1620), p2pw(680));
				text("Right Sensor: (" + df.format(selectedCreature.rightSensorX) + ", " + df.format(selectedCreature.rightSensorY) + " )", p2pl(1620), p2pw(710));
				text("Mouth Sensor: (" + df.format(selectedCreature.mouthSensorX) + ", " + df.format(selectedCreature.mouthSensorY) + " )", p2pl(1620), p2pw(740));
				text("Food Under Me: " + df.format(world.findTileAt(selectedCreature.mouthSensorX, selectedCreature.mouthSensorY, true).food), p2pl(1620), p2pw(770));
				text("Heading: " + df.format((selectedCreature.rotation * 180 / Math.PI)), p2pl(1620), p2pw(800));
				text("Generation: " + selectedCreature.generation, p2pl(1620), p2pw(830));
				drawCreatureBrain(selectedCreature);
				break;
			}
			case DATA:
			{
				break;
			}
			case TILE:
			{
				colorMode(RGB);
				fill(60, 120);
				rect(p2pl(1600), 0, p2pl(1200), p2pw(2000));
				colorMode(HSB);
				fill(selectedTile.colorH, selectedTile.colorS, selectedTile.colorV);
				rect(p2pl(1620), p2pw(220), p2pl(200), p2pw(200)); // draw the tile
				colorMode(RGB);
				drawButtons();
				fill(255, 255, 255);
				textSize(p2pl(70));
				text("Selected Tile Data", p2pl(1620), p2pw(190));
				textSize(p2pl(30));
				text(" # " + selectedTile.tileNumber, p2pl(1620), p2pw(250));
				text(" Food: " + df.format(selectedTile.food), p2pl(1620), p2pw(400));
				text("Row and Column: (" + (selectedTile.xIndex+1) + ", " + (selectedTile.yIndex+1) + ")", p2pl(1830), p2pw(250));
				text("Regeneration Value: " + Math.round( (selectedTile.regenValue * 1000) ) / 1000.0, p2pl(1830), p2pw(290));
				text("HSV: " + selectedTile.colorH + ", " + selectedTile.colorS + ", " + selectedTile.colorV, p2pl(1830), p2pw(330));
				text("x Range: " + selectedTile.x + " to " + (selectedTile.x + world.tileSize), p2pl(1830), p2pw(370));
				text("y Range: " + selectedTile.y + " to " + (selectedTile.y + world.tileSize), p2pl(1830), p2pw(410));

				
				break;
			}
		}
	}
	
	public void drawButtons()
	{
		colorMode(RGB);
		fill(170, 170, 170);
		rect(start.x, start.y, start.width, start.height);
		rect(killAll.x, killAll.y, killAll.width, killAll.height);
		rect(spawn.x, spawn.y, spawn.width, spawn.height);
		rect(spawn20.x, spawn20.y, spawn20.width, spawn20.height);
		
		textSize(p2pl(40));
		if(play) fill(119, 255, 51);
		else fill(255, 51, 51);
		if(play) text("On", start.x + p2pl(20), start.y + p2pw(60)); // +150 for next over
		else text("Off", start.x + p2pl(20), start.y + p2pw(60));
		fill(255, 255, 255);
		text("Kill All", killAll.x + p2pl(20), killAll.y + p2pw(60));
		if(spawnMode) fill(119, 255, 51);
		else fill(255, 51, 51);
		text("Spawn", spawn.x + p2pl(20), spawn.y + p2pw(60));
		fill(255, 255, 255);
		text("Spawn 20", spawn20.x + p2pl(20), spawn20.y + p2pw(60));
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

	public void drawTiles()
	{
		for(int x = 0; x < world.tiles.length; x++)
		{
			for(int y = 0; y < world.tiles.length; y++)
			{
				colorMode(HSB);
				fill(world.tiles[y][x].colorH, world.tiles[y][x].colorS, world.tiles[y][x].colorV);
				rect(world.tiles[y][x].x, world.tiles[y][x].y, world.tileSize, world.tileSize);
				fill(0, 0, 0);
			}
		}
	}
	
	public void drawCreatures()
	{
		colorMode(RGB);
		fill(255);
		for(int i = 0; i < world.creatures.size(); i++)
		{
			stroke(0);
			line((int)world.creatures.get(i).locationX, (int)world.creatures.get(i).locationY, (int)world.creatures.get(i).leftSensorX, (int)world.creatures.get(i).leftSensorY);
			line((int)world.creatures.get(i).locationX, (int)world.creatures.get(i).locationY, (int)world.creatures.get(i).midSensorX, (int)world.creatures.get(i).midSensorY);
			stroke(255);
			line((int)world.creatures.get(i).locationX, (int)world.creatures.get(i).locationY, (int)world.creatures.get(i).rightSensorX, (int)world.creatures.get(i).rightSensorY);
			stroke(0);
			if(selectedCreature != null && world.creatures.get(i).ID == selectedCreature.ID) fill(240, 0, 255);
			ellipse((int)world.creatures.get(i).locationX, (int)world.creatures.get(i).locationY, (int)world.creatures.get(i).diameter, (int)world.creatures.get(i).diameter);
			fill(255);
			ellipse((int)world.creatures.get(i).leftSensorX, (int)world.creatures.get(i).leftSensorY, 7, 7);
			ellipse((int)world.creatures.get(i).rightSensorX, (int)world.creatures.get(i).rightSensorY, 7, 7);
			ellipse((int)world.creatures.get(i).mouthSensorX, (int)world.creatures.get(i).mouthSensorY, 7, 7);
		}
	}
	public void drawSingleCreature(Creature c) // not done yet
	{
		colorMode(RGB);
		fill(255);
		for(int i = 0; i < world.creatures.size(); i++)
		{
			stroke(0);
			line((int)c.locationX, (int)c.locationY, (int)c.leftSensorX, (int)c.leftSensorY);
			line((int)c.locationX, (int)c.locationY, (int)c.midSensorX, (int)c.midSensorY);
			stroke(255);
			line((int)c.locationX, (int)c.locationY, (int)c.rightSensorX, (int)c.rightSensorY);
			stroke(0);
			fill(240, 0, 255);
			ellipse((int)c.locationX, (int)c.locationY, (int)c.diameter, (int)c.diameter);
			fill(255);
			ellipse((int)c.leftSensorX, (int)c.leftSensorY, 7, 7);
			ellipse((int)c.rightSensorX, (int)c.rightSensorY, 7, 7);
			ellipse((int)c.mouthSensorX, (int)c.mouthSensorY, 7, 7);
		}
		fill(255);
	}
	
	public void tileSelected(int yIndex, int xIndex)
	{
		selectedTile = world.tiles[yIndex][xIndex];
	}
	
	public void checkForDeaths()
	{
		for(int i = 0; i < world.creatures.size(); i++)
		{
			if(world.creatures.get(i).size < 30)
			{
				if(world.creatures.get(i) == selectedCreature) path = Path.GENERAL;
				world.creatures.remove(i);
				creatureDeaths++;
				return;
			}
			if(world.creatures.get(i).size < 100)
			{
				if(world.creatures.get(i) == selectedCreature) path = Path.GENERAL;
				world.creatures.remove(i);
				creatureDeaths++;
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
		int verticalSpacing = p2pw(70);
		textSize(p2pw(50));
		text("Input", p2pl(1620), p2pw(900));
		text("Layer 1", p2pl(1900), p2pw(900));
		text("Layer 2", p2pl(2120), p2pw(900));
		text("Output", p2pl(2420), p2pw(900));
		colorMode(RGB);
		for(int i = 0; i < c.inputNeurons.length; i++)
		{
			fill(255);
			textSize(p2pw(30));
			//rect(p2pl(1620), p2pw(800 + verticalSpacing * i), p2pl(100), p2pw(50));
			text(df.format(c.inputNeurons[i]) + "", p2pl(1620), p2pw(950) + verticalSpacing * i);
		}
		for(int i = 0; i < c.hidLayer1.length; i++)
		{
			fill(255);
			textSize(p2pw(30));
			//rect(p2pl(1620), p2pw(800 + verticalSpacing * i), p2pl(100), p2pw(50));
			text(df.format(c.hidLayer1[i]) + "", p2pl(1900), p2pw(950) + p2pw(40) * i);
		}
		for(int i = 0; i < c.hidLayer2.length; i++)
		{
			fill(255);
			textSize(p2pw(30));
			//rect(p2pl(1620), p2pw(800 + verticalSpacing * i), p2pl(100), p2pw(50));
			text(df.format(c.hidLayer2[i]) + "", p2pl(2120), p2pw(950) + p2pw(40) * i);
		}
		for(int i = 0; i < c.outputNeurons.length; i++)
		{
			fill(255);
			textSize(p2pw(30));
			//rect(p2pl(1620), p2pw(800 + verticalSpacing * i), p2pl(100), p2pw(50));
			text(df.format(c.outputNeurons[i]) + "", p2pl(2420), p2pw(950) + verticalSpacing * i);
		}
		
		for(int i = 0; i < c.inputNeurons.length; i++)
		{
			int color = 0;
			for(int one = 0; one < c.hidLayer1.length; one++)
			{
				color = (int) (sigmoid(c.inputToLayer1Axons[i][one].weight + 1.0) * 255);
				if(color < 0) color = 0;
				if(color > 255) color = 255;
				stroke(color);
				line(p2pl(1700), p2pw(945) + verticalSpacing * i, p2pl(1890), p2pw(940) + p2pw(40) * one);
			}
		}
		for(int i = 0; i < c.hidLayer2.length; i++)
		{
			int color = 0;
			for(int o = 0; o < c.outputNeurons.length; o++)
			{
				color = (int) (sigmoid(c.layer2ToOutputAxons[i][o].weight + 1.0) * 255);
				if(color < 0) color = 0;
				if(color > 255) color = 255;
				stroke(color);
				line(p2pl(2200), p2pw(945) + p2pw(40) * i, p2pl(2410), p2pw(940) + verticalSpacing * o);
			}
		}
		stroke(0);
	}
	
	public void drawPopGraph()
	{
		colorMode(RGB);
		fill(255, 255, 255);
		textSize(p2pl(30));
		text("Relative population over time", p2pl(1650), p2pw(1310));
		rect(p2pl(1650), p2pw(1340), p2pl(900), p2pw(220));
		fill(0, 0, 255);
		int width = 900 / popHistory.size();
		for(int i = 0; i < popHistory.size(); i++)
		{
			double ratio = (popHistory.get(i) / maxObservedCreatures * 200);
			ellipse(p2pl(1650 + (i)*width), p2pw(1350 + (200 - (int)ratio)), p2pw(5), p2pw(5));
		}
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
			play = !play;
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
		
		if(mX <= p2pl(1600))
		{
			check = true;
			mX -= translateX;
			mY -= translateY;
			mX /= scaleFactor;
			mY /= scaleFactor;
			
		}
		
		//test for button click
		if(start.x < mX && mX <= start.x + start.width) // start button
		{
			if(start.y < mY && mY <= start.y + start.height)
			{
				play = !play;
				return;
			}
		}
		if(killAll.x < mX && mX <= killAll.x + killAll.width) // start button
		{
			if(killAll.y < mY && mY <= killAll.y + killAll.height)
			{
				killAll();
				return;
			}
		}
		if(spawn.x < mX && mX <= spawn.x + spawn.width) // start button
		{
			if(spawn.y < mY && mY <= spawn.y + spawn.height)
			{
				spawnMode = !spawnMode;
				return;
			}
		}
		if(spawn20.x < mX && mX <= spawn20.x + spawn20.width) // start button
		{
			if(spawn20.y < mY && mY <= spawn20.y + spawn20.height)
			{
				for(int i = 0; i < 20; i++)
				{
					world.addCreature();
				}
				return;
			}
		}
		if(check)
		{
			// spawn mode?
			if(spawnMode && check)
			{
				if(spawn.x < mX && mX <= spawn.x + spawn.width) // start button
				{
					if(spawn.y < mY && mY <= spawn.y + spawn.height)
					{
						spawnMode = !spawnMode;
					}
				}
				if(world.tiles[0][0].x <= mX && mX <= world.tiles[world.tileResW-1][world.tileResL-1].x + world.tileSize &&
					world.tiles[0][0].y <= mY && mY <= world.tiles[world.tileResW-1][world.tileResL-1].y + world.tileSize)
				{
					if(spawnMode) world.addCreature(mX, mY);
				}
				return;
			}
			
			// test for creature click
			for(int i = 0; i < world.creatures.size(); i++)
			{
				if (Math.hypot(mX - world.creatures.get(i).locationX, mY - world.creatures.get(i).locationY) < world.creatures.get(i).diameter/2)
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
					if (world.tiles[y][x].x < mX && mX <= world.tiles[y][x].x + world.tileSize)
					{
						if (world.tiles[y][x].y < mY && mY <= world.tiles[y][x].y + world.tileSize)
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
		if(scaleFactor < 0.2) scaleFactor = 0.2;
		if(scaleFactor > 3.0) scaleFactor = 3.0;
		if(scaleFactor != 0.2 && scaleFactor != 3.0)
		{
			
			translateX += e.getAmount() * mouseX * 4 / 10;
			translateY += e.getAmount() * mouseY * 4 / 10;
		}
//		else if(scaleFactor != 0.2 && scaleFactor != 3.0 && e.getAmount() > 0)
//		{
//			System.out.println("in");
//			translateX += e.getAmount() * 520 * 4 / 10;
//			translateY += e.getAmount() * 400 * 4 / 10;
//		}
		
	}
	
	public boolean[][] setupMap() throws IOException
	{
		boolean[][] results = new boolean[100][100];
		map = ImageIO.read(Run.class.getResource(selectedMap + fileExt));
		System.out.println("right before map setup in setupMap");
		for(int row = 0; row < 100; row++)
		{
			for(int col = 0; col < 100; col++)
			{
				Color c = new Color(map.getRGB(col, row));
				if(c.getGreen() > 50) results[col][row] = false;
				else if(c.getBlue() > 50) results[col][row] = true;
				else results[col][row] = false;
			}
		}
		System.out.println("done setting up map");
		return results;
		
	}
	public double sigmoid(double x)
	{
		return (2.0 / (1 + Math.pow(Math.E, -(x / 1.5)))) - 1.0;
	}
}
































