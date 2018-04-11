package Essentials;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	Button maintainAt;
	Button maintainPopNum;
	Button findCreatureByID;
	Button showGenePoolHistory;
	int maintainNum = 50;
	boolean maintain;
	boolean play; // pseudo stop boolean
	int startNumCreatures;
	int creatureDeaths;
	List<Double> popHistory;
	ArrayList<Double> UNIV_POP_HISTORY;
	List<List<Integer>> popPropHistory;
	ArrayList<List<Integer>> UNIV_POP_PROP_HISTORY;
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
	boolean drawGenePoolGraph = true;
	static String[] mapOptions = {"map1", "europe", "Large_Island", "Three_Islands", "All_Land", "All_Water"};
	static String fileExt = ".jpg";
	static String selectedMap;
	final double MUTATE_CHANCE = 0.5;
	int forcedSpawns = 0;
	int superMutations = 0;
	
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
		startNumCreatures = 50;
		timeInterval = 0.1;
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
		world = new World(this, startNumCreatures, realWidth, realHeight, water, MUTATE_CHANCE);
		world.startTiles();
		world.startCreatures();
		selectedTile = null;
		selectedCreature = null;
		start = new Button(p2pl(1620), p2pw(20), p2pl(100), p2pw(90)); // +150 for next over
		killAll = new Button(p2pl(1770), p2pw(20), p2pl(170), p2pw(90));
		spawn = new Button(p2pl(1960), p2pw(20), p2pl(160), p2pw(90));
		spawn20 = new Button(p2pl(2140), p2pw(20), p2pl(225), p2pw(90));
		findCreatureByID = new Button(p2pl(2385), p2pw(20), p2pl(185), p2pw(90));
		maintainAt = new Button(p2pl(1620), p2pw(130), p2pl(270), p2pw(90));
		maintainPopNum = new Button(p2pl(1890), p2pw(130), p2pl(100), p2pw(90));
		showGenePoolHistory = new Button(p2pl(1930), p2pw(1035), p2pl(100), p2pw(30));
		play = true;
		creatureDeaths = 0;
		popHistory = new ArrayList<Double>();
		UNIV_POP_HISTORY = new ArrayList<Double>();
		popPropHistory = new ArrayList<List<Integer>>();
		UNIV_POP_PROP_HISTORY = new ArrayList<List<Integer>>();
		popHistory.add((double) startNumCreatures);
		maxObservedCreatures = startNumCreatures;
		spawnClicking = false;
		timeSeconds = 0;
		spawnMode = false;
		scaleFactor = 0.26;
		translateX = 20;
		translateY = 20;
		b4x = 0;
		b4y = 0;
		deltaX = 0;
		deltaY = 0;
		time = 0;
		df = new DecimalFormat("##.##");
		df.setRoundingMode(RoundingMode.DOWN);
		maintain = true;
	}
	
	public void draw()
	{
		if(path != Path.CREATURE) selectedCreature = null;
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
				UNIV_POP_HISTORY.add((double) (world.creatures.size())); // for save purposes
				popHistory.add((double) (world.creatures.size()));
				ArrayList<Integer> allColors = new ArrayList<Integer>();
				List<Integer> allColorsReal = null;
				for(int i = 0; i < world.creatures.size(); i++)
				{
					allColors.add(world.creatures.get(i).color.hashCode());
				}
				allColorsReal = allColors.subList(0, allColors.size());
				Collections.sort(allColorsReal);
				allColorsReal.add(world.creatures.size());
				popPropHistory.add(allColorsReal);
				UNIV_POP_PROP_HISTORY.add(allColorsReal);
			}			
			if(maintain)
			{
				while(world.creatures.size() < maintainNum)
				{
					forcedSpawns++;
					world.addCreature();
				}
			}
			superMutations = 0;
			for(int i = 0; i < world.creatures.size(); i++)
			{
				if(world.creatures.get(i).superMutate) superMutations++;
			}
		}
		pushMatrix();
		if(selectedCreature != null)
		{
			scaleFactor = 2.0f;
			translateX = (int) (-scaleFactor * selectedCreature.locationX + p2pw(850));
			translateY = (int) (-scaleFactor * selectedCreature.locationY + p2pw(850));
		}
		translate(translateX, translateY);
		scale((float) scaleFactor);
		colorMode(RGB);
		background(100);
		fill(60);
		rect(p2pl(8), 0, p2pl(6), p2pw(10));
		fill(255, 255, 255);
		textSize(p2pl(30));
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
				fill(255);
				text("World Time: " + df.format(time), p2pl(1620), p2pw(280));
				textSize(p2pl(30));
				colorMode(HSB);
				fill((int)(frameRate / 30.0 * 100), 200, 200);
				text("FPS: " + df.format(frameRate), p2pl(1620), p2pw(320));
				colorMode(RGB);
				fill(255);
				text("Starting Creatures: " + startNumCreatures, p2pl(1620), p2pw(360));
				fill(0, 255, 0);
				text("Successful Births: " + world.births, p2pl(1620), p2pw(400));
				fill(255, 0, 0);
				text("Number of Deaths: " + creatureDeaths, p2pl(1620), p2pw(440));
				fill(255);
				String sign;
				if(startNumCreatures > world.creatures.size()) sign = "-";
				else if(startNumCreatures == world.creatures.size()) sign = "";
				else sign = "+";
				if(sign.equals("+")) fill(0, 255, 0);
				else if(sign.equals("-")) fill(255, 0, 0);
				else fill(95, 260, 220);
				text("Total Change: " + sign + Math.abs(world.creatures.size() - startNumCreatures), p2pl(1620), p2pw(480));
				fill(255);
				text("Living Creatures: " + (world.creatures.size()), p2pl(1620), p2pw(520));
				text("Total Existed: " + world.creatureCount, p2pl(1620), p2pw(560));
				text("Forced Spawns: " + forcedSpawns, p2pl(1620), p2pw(640));
				text("Super Mutations: " + superMutations, p2pl(1620), p2pw(680));
				
				cutGraphs();
				drawGenePoolGraph();
				drawPopGraph();
				
				break;
			}
			case CREATURE:
			{
				colorMode(RGB);
				fill(60, 120);
				rect(p2pl(1600), 0, p2pl(1200), p2pw(2000));
				drawButtons();
				fill(selectedCreature.color.hashCode());
				//ellipse(p2pl(1700), p2pw(320), p2pw(150), p2pw(150)); // draw the creature
				drawSingleCreature(selectedCreature);
				fill(255);
				textSize(p2pl(65));
				text("Selected Creature Data", p2pl(1800), p2pw(300));
				textSize(p2pl(30));
				int shift = 30;
				text("ID: " + selectedCreature.ID, p2pl(1800), p2pw(350));
				text("Age: " + df.format(selectedCreature.fitness), p2pl(1800), p2pw(400));
				text("Generation: " + selectedCreature.generation, p2pl(2100), p2pw(350));
				text("Parent: " + selectedCreature.parentID, p2pl(2100), p2pw(400));
				text("Total Eaten: " + df.format(selectedCreature.totalEaten), p2pl(1620), p2pw(500 + 0 * shift));
				text("Total Decayed: " + df.format(selectedCreature.totalDecayed), p2pl(1620), p2pw(500 + 1 * shift));
				text("Location: ( " + df.format(selectedCreature.locationX) + ", " + df.format(selectedCreature.locationY) + " )", p2pl(1620), p2pw(500 + 2 * shift));
				text("Left XY: ( " + df.format(selectedCreature.leftSensorX) + ", " + df.format(selectedCreature.leftSensorY) + " )", p2pl(1620), p2pw(500 + 3 * shift));
				text("Mid XP: ( " + df.format(selectedCreature.midSensorX) + ", " + df.format(selectedCreature.midSensorY) + " )", p2pl(1620), p2pw(500 + 4 * shift));
				text("Right XY: ( " + df.format(selectedCreature.rightSensorX) + ", " + df.format(selectedCreature.rightSensorY) + " )", p2pl(1620), p2pw(500 + 5 * shift));
				text("Mouth XY: ( " + df.format(selectedCreature.mouthSensorX) + ", " + df.format(selectedCreature.mouthSensorY) + " )", p2pl(1620), p2pw(500 + 6 * shift));
				text("Heading: " + df.format((selectedCreature.rotation * 180 / Math.PI)), p2pl(1620), p2pw(500 + 7 * shift));
				text("Size Decay", p2pl(2200), p2pw(500 + 0 * shift));
				text("Age Decay", p2pl(2200), p2pw(500 + 1 * shift));
				text("Eat Decay", p2pl(2200), p2pw(500 + 2 * shift));
				text("Turn Decay", p2pl(2200), p2pw(500 + 3 * shift));
				text("Fwd Decay", p2pl(2200), p2pw(500 + 4 * shift));
				text("Att Decay", p2pl(2200), p2pw(500 + 5 * shift));
				text("Eat Rate", p2pl(2200), p2pw(500 + 6 * shift));
				fill(95, 260, 220);
				text("Current Size: " + (int)selectedCreature.size, p2pl(2200), p2pw(500 + 10 * shift));
				fill(0);
				fill(255, 0, 0);
				text(df.format(selectedCreature.sizeDecay), p2pl(2400), p2pw(500 + 0 * shift));
				text(df.format(selectedCreature.fitnessDecay), p2pl(2400), p2pw(500 + 1 * shift));
				text(df.format(selectedCreature.eatRateDecay), p2pl(2400), p2pw(500 + 2 * shift));
				text(df.format(selectedCreature.rotationDecay), p2pl(2400), p2pw(500 + 3 * shift));
				text(df.format(selectedCreature.forwardDecay), p2pl(2400), p2pw(500 + 4 * shift));
				text(df.format(selectedCreature.attackDecay), p2pl(2400), p2pw(500 + 5 * shift));
				fill(0, 255, 0);
				text(df.format(selectedCreature.eatRate), p2pl(2400), p2pw(510 + 6 * shift));
				if(selectedCreature.energyChange < 0) fill(255, 0, 0);
				else fill(0, 255, 0);
				text("Energy Change: " + df.format(selectedCreature.energyChange), p2pl(2200), p2pw(500 + 8 * shift));
				fill(255);
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
		rect(maintainAt.x, maintainAt.y, maintainAt.width, maintainAt.height);
		rect(maintainPopNum.x, maintainPopNum.y, maintainPopNum.width, maintainPopNum.height);
		rect(findCreatureByID.x, findCreatureByID.y, findCreatureByID.width, findCreatureByID.height);
		if(path == Path.GENERAL) rect(showGenePoolHistory.x, showGenePoolHistory.y, showGenePoolHistory.width, showGenePoolHistory.height);
		
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
		if(maintain) fill(119, 255, 51);
		else fill(255, 51, 51);
		text("Maintain At", maintainAt.x + p2pl(20), maintainAt.y + p2pw(60));
		fill(255, 255, 255);
		text(maintainNum, maintainPopNum.x + p2pl(20), maintainPopNum.y + p2pw(60));
		text("Find ID", findCreatureByID.x + p2pl(20), findCreatureByID.y + p2pw(60));
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
				stroke(world.tiles[y][x].colorH, world.tiles[y][x].colorS, world.tiles[y][x].colorV - 30);
				fill(world.tiles[y][x].colorH, world.tiles[y][x].colorS, world.tiles[y][x].colorV);
				rect(world.tiles[y][x].x, world.tiles[y][x].y, world.tileSize, world.tileSize);
				fill(0, 0, 0);
				stroke(0);
			}
		}
	}
	
	public void drawCreatures()
	{
		colorMode(RGB);
		fill(255);
		for(int i = 0; i < world.creatures.size(); i++)
		{
			Creature c = world.creatures.get(i);
			stroke(50);
			line((int)c.locationX, (int)c.locationY, (int)c.leftSensorX, (int)c.leftSensorY);
			if(c.outputNeurons[3] > 0.0) stroke(180, 0, 0);
			line((int)c.locationX, (int)c.locationY, (int)c.midSensorX, (int)c.midSensorY);
			stroke(50);
			line((int)c.locationX, (int)c.locationY, (int)c.rightSensorX, (int)c.rightSensorY);
			stroke(0);
			fill(c.color.hashCode());
			if(selectedCreature != null && world.creatures.get(i).ID == selectedCreature.ID)
			{
				stroke(240, 0, 255);
				strokeWeight(7);
			}
			ellipse((int)c.locationX, (int)c.locationY, p2pw(c.diameter), p2pw(c.diameter));
			fill(255);
			stroke(0);
			strokeWeight(1);
			colorMode(HSB);
			fill(c.leftSensorColor, 120, 120);
			ellipse((int)c.leftSensorX, (int)c.leftSensorY, p2pw(15), p2pw(15));
			fill(c.rightSensorColor, 120, 120);
			ellipse((int)c.rightSensorX, (int)c.rightSensorY, p2pw(15), p2pw(15));
			fill(c.mouthSensorColor, 120, 120);
			//ellipse((int)c.mouthSensorX, (int)c.mouthSensorY, p2pw(15), p2pw(15));
			colorMode(RGB);
			fill(0);
		}
	}
	public void drawSingleCreature(Creature c) // done, not sure how it works, but don't touch!!!!!
	{
		colorMode(RGB);
		fill(c.color.hashCode()); // 1700, 320
		int leftSensorXChange = (int) (c.leftSensorX - c.locationX);
		int leftSensorYChange = (int) (c.leftSensorY - c.locationY);
		int rightSensorXChange = (int) (c.rightSensorX - c.locationX);
		int rightSensorYChange = (int) (c.rightSensorY - c.locationY);
		int killerXChange = (int) (c.midSensorX - c.locationX);
		int killerYChange = (int) (c.midSensorY - c.locationY);
		stroke(50);
		line(1700, 320, 1700 + leftSensorXChange, 320 + leftSensorYChange);
		line(1700, 320, 1700 + rightSensorXChange, 320 + rightSensorYChange);
		line(1700, 320, 1700 + killerXChange, 320+ killerYChange);
		stroke(0);
		colorMode(HSB);
		fill(c.leftSensorColor, 120, 120);
		ellipse(1700 + leftSensorXChange, 320 + leftSensorYChange, p2pw(15) * 1, p2pw(15) * 1);
		fill(c.rightSensorColor, 120, 120);
		ellipse(1700 + rightSensorXChange, 320 + rightSensorYChange, p2pw(15) * 1, p2pw(15) * 1);
		colorMode(RGB);
		fill(c.color.hashCode());
		ellipse(1700, 320, (int)c.diameter * 1, (int)c.diameter * 1);
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
		int verticalSpacing = p2pw(640 / world.creatures.get(0).brainLength);
		textSize(p2pw(50));
		text("Input", p2pl(1620), p2pw(900));
		text("Hidden Layer", p2pl(1950), p2pw(900));
		text("Output", p2pl(2410), p2pw(900));
		colorMode(RGB);
		// Left food, left creature, center food, center creature, right food, right creature, mouth food, size, 
		textSize(p2pw(30));
		text("L Food", p2pl(1620), p2pw(950) + verticalSpacing * 0);
		text("L Crea", p2pl(1620), p2pw(950) + verticalSpacing * 1);
		text("C Food", p2pl(1620), p2pw(950) + verticalSpacing * 2);
		text("C Crea", p2pl(1620), p2pw(950) + verticalSpacing * 3);
		text("R Food", p2pl(1620), p2pw(950) + verticalSpacing * 4);
		text("R Crea", p2pl(1620), p2pw(950) + verticalSpacing * 5);
		text("M Food", p2pl(1620), p2pw(950) + verticalSpacing * 6);
		text("Size", p2pl(1620), p2pw(950) + verticalSpacing * 7);
		text("L Spe", p2pl(1620), p2pw(950) + verticalSpacing * 8);
		text("C Spe", p2pl(1620), p2pw(950) + verticalSpacing * 9);
		text("R Spe", p2pl(1620), p2pw(950) + verticalSpacing * 10);
		text("Tile T", p2pl(1620), p2pw(950) + verticalSpacing * 11);
		// Forward velocity, rotational velocity, eat, attack, give birth, attack length,
		text("For Vel", p2pl(2360), p2pw(950) + verticalSpacing * 0);
		text("Rot Vel", p2pl(2360), p2pw(950) + verticalSpacing * 1);
		text("Eat Rate", p2pl(2360), p2pw(950) + verticalSpacing * 2);
		if(c.outputNeurons[3] >= 0) fill(0, 255, 0);
		else fill(255, 0, 0);
		text("Attack?", p2pl(2360), p2pw(950) + verticalSpacing * 3);
		if(c.outputNeurons[4] >= 0) fill(0, 255, 0);
		else fill(255, 0, 0);
		text("Birth?", p2pl(2360), p2pw(950) + verticalSpacing * 4);
		fill(255);
		text("Att Size", p2pl(2360), p2pw(950) + verticalSpacing * 5);
		for(int i = 0; i < c.inputNeurons.length; i++)
		{
			fill(255);
			textSize(p2pw(30));
			//rect(p2pl(1620), p2pw(800 + verticalSpacing * i), p2pl(100), p2pw(50));
			text(df.format(c.inputNeurons[i]) + "", p2pl(1760), p2pw(950) + verticalSpacing * i);
		}
		for(int i = 0; i < c.hidLayer1.length; i++)
		{
			fill(255);
			textSize(p2pw(30));
			//rect(p2pl(1620), p2pw(800 + verticalSpacing * i), p2pl(100), p2pw(50));
			text(df.format(c.hidLayer1[i]) + "", p2pl(2050), p2pw(950) + verticalSpacing * i);
		}
		for(int i = 0; i < c.outputNeurons.length; i++)
		{
			fill(255);
			textSize(p2pw(30));
			//rect(p2pl(1620), p2pw(800 + verticalSpacing * i), p2pl(100), p2pw(50));
			text(df.format(c.outputNeurons[i]) + "", p2pl(2490), p2pw(950) + verticalSpacing * i);
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
				line(p2pl(1860), p2pw(945) + verticalSpacing * i, p2pl(2020), p2pw(940) + verticalSpacing * one);
			}
		}
		for(int i = 0; i < c.hidLayer1.length; i++)
		{
			int color = 0;
			for(int o = 0; o < c.outputNeurons.length; o++)
			{
				color = (int) (sigmoid(c.layer1ToOutputAxons[i][o].weight + 1.0) * 255);
				if(color < 0) color = 0;
				if(color > 255) color = 255;
				stroke(color);
				line(p2pl(2140), p2pw(945) + verticalSpacing * i, p2pl(2330), p2pw(940) + verticalSpacing * o);
			}
		}
		stroke(0);
	}
	
	public void drawPopGraph()
	{
		colorMode(RGB);
		fill(255, 255, 255);
		textSize(p2pl(30));
		text("Relative population over time", p2pl(1650), p2pw(1330));
		rect(p2pl(1650), p2pw(1340), p2pl(900), p2pw(220));
		fill(0, 0, 255);
		int width = 900 / popHistory.size();
//		for(int i = 0; i < popHistory.size(); i++)
//		{
//			double ratio = (popHistory.get(i) / maxObservedCreatures * 200);
//			ellipse(p2pl(1650 + (i)*width), p2pw(1350 + (200 - (int)ratio)), p2pw(5), p2pw(5));
//		}
		for(int i = 0; i < popHistory.size()-1; i++)
		{
			double ratio = (popHistory.get(i) / maxObservedCreatures * 200);
			double toRatio = (popHistory.get(i+1) / maxObservedCreatures * 200);
			//ellipse(p2pl(1650 + (i)*width), p2pw(1350 + (200 - (int)ratio)), p2pw(5), p2pw(5));
			line(p2pl(1650 + (i)*width), p2pw(1350 + (200 - (int)ratio)), p2pl(1650 + (i+1)*width), p2pw(1350 + (200 - (int)toRatio)));
		}
	}
	
	public void drawGenePoolGraph()
	{
		if(!drawGenePoolGraph) return;
		if(popPropHistory.size() == 0) return;
		if(world.creatures.size() == 0) return;
		colorMode(RGB);
		fill(255);
		textSize(p2pl(30));
		text("Current Gene Pool", p2pl(1650), p2pw(990));
		text("Gene Pool History", p2pl(1650), p2pw(1060));
		
		double width = 900.0 / (popPropHistory.get(popPropHistory.size()-1).size()-1.0);
		for(int i = 0; i < popPropHistory.get(popPropHistory.size()-1).size()-1; i++)
		{
			fill(popPropHistory.get(popPropHistory.size()-1).get(i));
			stroke(popPropHistory.get(popPropHistory.size()-1).get(i));
			rect(p2pl(1650) + p2pl(i * width), p2pw(1000), p2pl(width), p2pw(20));
		}
		for(int j = 0; j < popPropHistory.size(); j++)
		{
			double propWidth = 900.0 / popPropHistory.size();
			double upCoord = 1075.0;
			for(int i = 0; i < popPropHistory.get(j).size()-1; i++)
			{
				fill(popPropHistory.get(j).get(i));
				stroke(popPropHistory.get(j).get(i));
				rect(p2pl(1650) + p2pl(j * propWidth), p2pw((int)upCoord), p2pl(propWidth), (int)(200.0 / p2pw(popPropHistory.get(j).get(popPropHistory.get(j).size()-1)) + 1));
				upCoord += 220.0 / popPropHistory.get(j).get(popPropHistory.get(j).size()-1);
			}
		}
		stroke(0);
	}
	
	public void cutGraphs()
	{
		boolean cutPop = false;
		boolean cutProp = false;
		if(popHistory.size() > 100) cutPop = true;
		if(popPropHistory.size() > 100) cutProp = true;
		
		if(cutPop) popHistory = popHistory.subList(popHistory.size() - 100, popHistory.size() - 1);
		if(cutProp) popPropHistory = popPropHistory.subList(popPropHistory.size() - 100, popPropHistory.size() - 1);
	}
	
	public void keyPressed()
	{
		if(key == 'r')
		{
			translateX = 20;
			translateY = 20;
			scaleFactor = 0.26;
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
	public void mouseDragged(MouseEvent e)
    {
        translateX += mouseX - pmouseX;
        translateY += mouseY - pmouseY;
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
		if(killAll.x < mX && mX <= killAll.x + killAll.width) // kill all button
		{
			if(killAll.y < mY && mY <= killAll.y + killAll.height)
			{
				killAll();
				return;
			}
		}
		if(spawn.x < mX && mX <= spawn.x + spawn.width) // spawn button
		{
			if(spawn.y < mY && mY <= spawn.y + spawn.height)
			{
				spawnMode = !spawnMode;
				return;
			}
		}
		if(spawn20.x < mX && mX <= spawn20.x + spawn20.width) // spawn 20 button
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
		if(maintainAt.x < mX && mX <= maintainAt.x + maintainAt.width) // maintain at button
		{
			if(maintainAt.y < mY && mY <= maintainAt.y + maintainAt.height)
			{
				maintain = !maintain;
				return;
			}
		}
		if(maintainPopNum.x < mX && mX <= maintainPopNum.x + maintainPopNum.width) // maintain number button
		{
			if(maintainPopNum.y < mY && mY <= maintainPopNum.y + maintainPopNum.height)
			{
				if(maintainNum == 1) maintainNum += 4;
				else maintainNum += 5;
				if(maintainNum > 50) maintainNum = 1;
				return;
			}
		}
		if(findCreatureByID.x < mX && mX <= findCreatureByID.x + findCreatureByID.width) // find ID button
		{
			if(findCreatureByID.y < mY && mY <= findCreatureByID.y + findCreatureByID.height)
			{
				thread("findCreatureID");
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
	public void mouseWheel(MouseEvent e)
    {
        float delta = (float) (-e.getCount() > 0 ? 1.05 : -e.getCount() < 0 ? 1.0 / 1.05 : 1.0);
        scaleFactor *= delta;
        if (!(scaleFactor > 3.0) && !(scaleFactor < .2))
        {
            translateX -= mouseX;
            translateY -= mouseY;
            translateX *= delta;
            translateY *= delta;
            translateX += mouseX;
            translateY += mouseY;
        }
        if (scaleFactor > 3.0) scaleFactor = 3.0f;
        if (scaleFactor < .2) scaleFactor = .2f;
    }
	public void findCreatureID()
	{
		int id = Integer.parseInt(JOptionPane.showInputDialog("Enter creature ID:"));
		
		for(int i = 0; i < world.creatures.size(); i++)
		{
			if(id == world.creatures.get(i).ID)
			{
				selectedCreature = world.creatures.get(i);
				path = path.CREATURE;
				return;
			}
		}
		JOptionPane.showMessageDialog(frame, "That creature ID doesn't exist or has died.");
	}
	public boolean[][] setupMap() throws IOException
	{
		boolean[][] results = new boolean[100][100];
		map = ImageIO.read(Run.class.getResource(selectedMap + fileExt));
		System.out.println("right before map setup in setupMap for map: " + selectedMap + fileExt);
		for(int row = 0; row < 100; row++)
		{
			for(int col = 0; col < 100; col++)
			{
				Color c = new Color(map.getRGB(col, row));
				if(c.getGreen() > 150) results[col][row] = false;
				else if(c.getBlue() > 30) results[col][row] = true;
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
































