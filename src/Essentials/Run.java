package Essentials;

import Network.Network;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import processing.core.PApplet;
import processing.event.MouseEvent;

public class Run extends PApplet
{
	public float  zoom;
	public World world;
	int rawTime;
	double time;
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
	
	enum Path
	{
		GENERAL, CREATURE, DATA, TILE;
	}
	Path path;
	 
	public static void main(String[] args)
	{
		PApplet.main("Essentials.Run");
	}
	public void settings() // w 2600 h 1600  8/13 RATIO IS BEST!!!
	{
		realWidth = displayWidth - 136;
		realHeight = displayHeight - 224;
		size(realWidth, realHeight);
		zoom = 1;
		path = Path.GENERAL;
		startNumCreatures = 100;
	}
	public void setup()
	{
		frameRate(60);
		textSize(50);
		world = new World(startNumCreatures, realWidth, realHeight);
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
		scaleFactor = 1;
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
		translate(translateX, translateY);
		scale((float) scaleFactor);
		colorMode(RGB);
		background(100);
		fill(60);
		rect(p2pl(8), 0, p2pl(6), p2pw(10));
		fill(255, 255, 255);
		textSize(p2pl(30));
		text("Code Iterations: " + rawTime, p2pl(650), p2pw(35));
		text("Framerate: " + (int)frameRate, p2pl(50), p2pw(35));
		text("Global Time: " + df.format(time), p2pl(300), p2pw(35));
		drawTiles();
		drawCreatures();
		
		if(play)
		{	
			rawTime++;		
			time = rawTime / 1000.0;
			world.updateTiles();
			world.updateCreatures();
			if(world.creatures.size() > maxObservedCreatures) maxObservedCreatures = world.creatures.size();
			checkForDeaths();
			
			if(rawTime % 30 == 0)
			{
				popHistory.add((double) (world.creatures.size()));
			}
		}
		
		
		switch(path) // side bar path split
		{
			case GENERAL:
			{
				colorMode(RGB);
				fill(60);
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
				
				drawPopGraph();
				
				break;
			}
			case CREATURE:
			{
				colorMode(RGB);
				fill(60);
				rect(p2pl(1600), 0, p2pl(1200), p2pw(2000));
				drawButtons();
				fill(255, 255, 255);
				ellipse(p2pl(1700), p2pw(320), p2pw(150), p2pw(150)); // draw the creature
				textSize(p2pl(70));
				text("Selected Creature Data", p2pl(1620), p2pw(190));
				textSize(p2pl(30));
				text("ID: " + selectedCreature.ID, p2pl(1620), p2pw(500));
				text("Current Size: " + (int)selectedCreature.size, p2pl(1620), p2pw(530));
				text("Total Eaten: " + selectedCreature.totalEaten, p2pl(1620), p2pw(560));
				text("Total Decayed: " + selectedCreature.totalDecayed, p2pl(1620), p2pw(590));
				
				break;
			}
			case DATA:
			{
				break;
			}
			case TILE:
			{
				colorMode(RGB);
				fill(60);
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
				text(" Food: " + selectedTile.food, p2pl(1620), p2pw(400));
				text("Row and Column: (" + (selectedTile.xIndex+1) + ", " + (selectedTile.yIndex+1) + ")", p2pl(1830), p2pw(250));
				text("Regeneration Value: " + Math.round( (selectedTile.regenValue * 1000) ) / 1000.0, p2pl(1830), p2pw(290));
				text("HSV: " + selectedTile.colorH + ", " + selectedTile.colorS + ", " + selectedTile.colorV, p2pl(1830), p2pw(330));
				
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
				rect(world.tiles[y][x].x, world.tiles[y][x].y, world.tileSize-1, world.tileSize-1);
				fill(0, 0, 0);
			}
		}
	}
	
	public void drawCreatures()
	{
		colorMode(RGB);
		fill(255, 255, 255);
		for(int i = 0; i < world.creatures.size(); i++)
		{
			ellipse(world.creatures.get(i).locationX, world.creatures.get(i).locationY, (int)world.creatures.get(i).diameter, (int)world.creatures.get(i).diameter);
		}
	}
	
	public void tileSelected(int yIndex, int xIndex)
	{
		selectedTile = world.tiles[yIndex][xIndex];
	}
	
	public void checkForDeaths()
	{
		for(int i = 0; i < world.creatures.size(); i++)
		{
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
			scaleFactor = 1;
			return;
		}
		else if(key == ' ')
		{
			play = !play;
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
		
		// spawn mode?
		if(spawnMode)
		{
			if(spawn.x < mX && mX <= spawn.x + spawn.width) // start button
			{
				if(spawn.y < mY && mY <= spawn.y + spawn.height)
				{
					spawnMode = !spawnMode;
				}
			}
			if(world.tiles[0][0].x <= mX && mX <= world.tiles[world.tileRes-1][world.tileRes-1].x + world.tileSize &&
				world.tiles[0][0].y <= mY && mY <= world.tiles[world.tileRes-1][world.tileRes-1].y + world.tileSize)
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
		
		
		path = Path.GENERAL;
		
		

	}
	
	@SuppressWarnings("deprecation")
	public void mouseWheel(MouseEvent e)
	{
		scaleFactor -= e.getAmount() / 10.0;
		if(scaleFactor < 0.8) scaleFactor = 0.8;
		if(scaleFactor > 3.0) scaleFactor = 3.0;
		if(scaleFactor != 0.8 && scaleFactor != 3.0)
		{
			translateX += e.getAmount() * mouseX / 10;
			translateY += e.getAmount() * mouseY / 10;
		}
		
	}
}
































