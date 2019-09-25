package Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Creature.Creature;
import Creature.CreatureManager;
import Essentials.Run;
import World.TileManager;
import processing.core.PApplet;
import processing.core.PConstants;

public class Menu
{
	List<Double> populationHistory;
	List<List<Integer>> sortedGeneHistory;
	
	public static Button menuButton, creatureInfo;
	public static Button start, killAll, spawn, spawn20, kill, maintainAt, maintainPopNum, findCreatureByID;
	public enum MenuPath
	{
		GENERAL, CREATURE, DATA, TILE;
	}
	public static MenuPath path;
	
	public Menu()
	{
		path = MenuPath.GENERAL;
		populationHistory = new ArrayList<Double>();
		sortedGeneHistory = new ArrayList<List<Integer>>();
		populationHistory.add((double) Preferences.START_NUM_CREATURES);
	}
	
	public void menuInit(PApplet p)
	{
		menuButton = new Button(Preferences.p2pl(2400), 0, Preferences.p2pl(185), Preferences.p2pw(120), p);
		start = new Button(Preferences.p2pl(2400), Preferences.p2pw(120), Preferences.p2pl(185), Preferences.p2pw(120), p);
		killAll = new Button(Preferences.p2pl(0), 0, Preferences.p2pl(200), Preferences.p2pw(120), p);
		spawn = new Button(Preferences.p2pl(200), 0, Preferences.p2pl(200), Preferences.p2pw(120), p);
		spawn20 = new Button(Preferences.p2pl(400), 0, Preferences.p2pl(250), Preferences.p2pw(120), p);
		findCreatureByID = new Button(Preferences.p2pl(650), 0, Preferences.p2pl(200), Preferences.p2pw(120), p);
		maintainAt = new Button(Preferences.p2pl(850), 0, Preferences.p2pl(280), Preferences.p2pw(120), p);
		maintainPopNum = new Button(Preferences.p2pl(1100), 0, Preferences.p2pl(140), Preferences.p2pw(120), p);
		creatureInfo = new Button(Preferences.p2pl(1650), Preferences.p2pw(250), Preferences.p2pl(280), Preferences.p2pw(120), p);
	}
	
	public void drawMenu(PApplet p)
	{
		switch(path) // side bar path split
		{
			case GENERAL:
			{
				p.colorMode(PConstants.RGB);
				
				generalMenu(Run.displayTime, CreatureManager.creatureDeaths, Run.forcedSpawns, Run.superMutations, p);
				
				drawButtons(p);
				cutGraphs();
				drawGenePoolGraphs(p);
				drawPopulationGraph(p);
				
				break;
			}
			case CREATURE:
			{
				p.colorMode(PConstants.RGB);
				p.fill(60, 120);
				p.rect(Preferences.p2pl(1600), 0, Preferences.p2pl(1200), Preferences.p2pw(2000));
				drawButtons(p);
				p.fill(Run.selectedCreature.color.hashCode());
				drawSingleCreature(Run.selectedCreature, p);
				p.fill(255);
				p.textSize(Preferences.p2pl(65));
				p.text("Creature Data", Preferences.p2pl(1800), Preferences.p2pw(100));
				p.textSize(Preferences.p2pl(30));
				int spacing = 45;
				int shift = 420;
				int shift2 = 340;
				
				p.text("ID: " + Run.selectedCreature.ID, Preferences.p2pl(1800), Preferences.p2pw(160));
				p.text("Age: " + Preferences.formatDecimal.format(Run.selectedCreature.fitness), Preferences.p2pl(1800), Preferences.p2pw(210));
				p.text("Generation: " + Run.selectedCreature.generation, Preferences.p2pl(2100), Preferences.p2pw(160));
				p.text("Parent: " + Run.selectedCreature.parentID, Preferences.p2pl(2100), Preferences.p2pw(210));
				
				p.text("Total Eaten: " + Preferences.formatInteger.format(Run.selectedCreature.totalEaten), Preferences.p2pl(1620), Preferences.p2pw(shift + 0 * spacing));
				p.text("Total Decayed: " + Preferences.formatInteger.format(Run.selectedCreature.totalDecayed), Preferences.p2pl(1620), Preferences.p2pw(shift + 1 * spacing));
				p.text("Location: ( " + Preferences.formatInteger.format(Run.selectedCreature.locationX) + ", " + Preferences.formatInteger.format(Run.selectedCreature.locationY) + " )", Preferences.p2pl(1620), Preferences.p2pw(shift + 2 * spacing));
				p.text("Left XY: ( " + Preferences.formatInteger.format(Run.selectedCreature.leftSensorX) + ", " + Preferences.formatInteger.format(Run.selectedCreature.leftSensorY) + " )", Preferences.p2pl(1620), Preferences.p2pw(shift + 3 * spacing));
				p.text("Mid XP: ( " + Preferences.formatInteger.format(Run.selectedCreature.midSensorX) + ", " + Preferences.formatInteger.format(Run.selectedCreature.midSensorY) + " )", Preferences.p2pl(1620), Preferences.p2pw(shift + 4 * spacing));
				p.text("Right XY: ( " + Preferences.formatInteger.format(Run.selectedCreature.rightSensorX) + ", " + Preferences.formatInteger.format(Run.selectedCreature.rightSensorY) + " )", Preferences.p2pl(1620), Preferences.p2pw(shift + 5 * spacing));
				p.text("Mouth XY: ( " + Preferences.formatInteger.format(Run.selectedCreature.mouthSensorX) + ", " + Preferences.formatInteger.format(Run.selectedCreature.mouthSensorY) + " )", Preferences.p2pl(1620), Preferences.p2pw(shift + 6 * spacing));
				p.text("Heading: " + Preferences.formatInteger.format((Run.selectedCreature.rotation * 180 / Math.PI)), Preferences.p2pl(1620), Preferences.p2pw(shift + 7 * spacing));
				
				p.text("Size Decay", Preferences.p2pl(2200), Preferences.p2pw(shift2 + 0 * spacing));
				p.text("Age Decay", Preferences.p2pl(2200), Preferences.p2pw(shift2 + 1 * spacing));
				p.text("Eat Decay", Preferences.p2pl(2200), Preferences.p2pw(shift2 + 2 * spacing));
				p.text("Turn Decay", Preferences.p2pl(2200), Preferences.p2pw(shift2 + 3 * spacing));
				p.text("Fwd Decay", Preferences.p2pl(2200), Preferences.p2pw(shift2 + 4 * spacing));
				p.text("Att Decay", Preferences.p2pl(2200), Preferences.p2pw(shift2 + 5 * spacing));
				p.text("Eat Rate", Preferences.p2pl(2200), Preferences.p2pw(shift2 + 6 * spacing));
				p.fill(95, 260, 220);
				p.text("Current Size: " + (int)Run.selectedCreature.size, Preferences.p2pl(2200), Preferences.p2pw(shift2 + 10 * spacing));
				p.fill(0);
				p.fill(255, 0, 0);
				p.text(Preferences.formatDecimal.format(Run.selectedCreature.sizeDecay), Preferences.p2pl(2400), Preferences.p2pw(shift2 + 0 * spacing));
				p.text(Preferences.formatDecimal.format(Run.selectedCreature.fitnessDecay), Preferences.p2pl(2400), Preferences.p2pw(shift2 + 1 * spacing));
				p.text(Preferences.formatDecimal.format(Run.selectedCreature.eatRateDecay), Preferences.p2pl(2400), Preferences.p2pw(shift2 + 2 * spacing));
				p.text(Preferences.formatDecimal.format(Run.selectedCreature.rotationDecay), Preferences.p2pl(2400), Preferences.p2pw(shift2 + 3 * spacing));
				p.text(Preferences.formatDecimal.format(Run.selectedCreature.forwardDecay), Preferences.p2pl(2400), Preferences.p2pw(shift2 + 4 * spacing));
				p.text(Preferences.formatDecimal.format(Run.selectedCreature.attackDecay), Preferences.p2pl(2400), Preferences.p2pw(shift2 + 5 * spacing));
				p.fill(0, 255, 0);
				p.text(Preferences.formatDecimal.format(Run.selectedCreature.eatRate), Preferences.p2pl(2400), Preferences.p2pw(shift2 + 6 * spacing));
				if(Run.selectedCreature.energyChange < 0) p.fill(255, 0, 0);
				else p.fill(0, 255, 0);
				p.text("Energy Change: " + Preferences.formatDecimal.format(Run.selectedCreature.energyChange), Preferences.p2pl(2200), Preferences.p2pw(shift2 + 8 * spacing));
				p.fill(255);
				
				if(Run.showCreatureInfo)
				{
					if(Run.selectedCreature != null && Run.selectedCreature.nearestCreature != null)
					{
						p.text("Nearest Creature ID: " + Run.selectedCreature.nearestCreature.ID, Preferences.p2pl(1620), Preferences.p2pw(shift + 8 * spacing));
						p.text("Nearest ID Distance: " + Preferences.formatDecimal.format(Run.selectedCreature.distToNearest), Preferences.p2pl(1620), Preferences.p2pw(shift + 9 * spacing));
						p.text("Nearest ID Color Diff: " + Run.selectedCreature.colorDifferenceToNearest, Preferences.p2pl(1620), Preferences.p2pw(shift + 10 * spacing));
						p.text("Angle change to nearest: " + Run.selectedCreature.angleToNearest, Preferences.p2pl(1620), Preferences.p2pw(shift + 11 * spacing));
						p.text("Creatures within 10 Tiles: " + Run.selectedCreature.numCreaturesWithin10, Preferences.p2pl(1620), Preferences.p2pw(shift + 12 * spacing));
					}
					else
					{
						p.text("Nearest Creature ID: null", Preferences.p2pl(1620), Preferences.p2pw(shift + 8 * spacing));
						p.text("Nearest ID Distance: null", Preferences.p2pl(1620), Preferences.p2pw(shift + 9 * spacing));
						p.text("Nearest ID Color Diff: null", Preferences.p2pl(1620), Preferences.p2pw(shift + 10 * spacing));
						p.text("Angle change to nearest: null", Preferences.p2pl(1620), Preferences.p2pw(shift + 11 * spacing));
						p.text("Creatures within 10 Tiles: 0", Preferences.p2pl(1620), Preferences.p2pw(shift + 12 * spacing));
					}
				}
				else drawCreatureBrain(Run.selectedCreature, p);
				
				break;
			}
			case DATA:
			{
				break;
			}
			case TILE:
			{
				p.colorMode(PConstants.RGB);
				p.fill(60, 120);
				p.rect(Preferences.p2pl(1600), 0, Preferences.p2pl(1200), Preferences.p2pw(2000));
				p.colorMode(PConstants.HSB, 360, 100, 100);
				p.fill(Run.selectedTile.colorH, Run.selectedTile.colorS, Run.selectedTile.colorV);
				p.rect(Preferences.p2pl(1620), Preferences.p2pw(140), Preferences.p2pl(200), Preferences.p2pl(200)); // draw the tile
				p.colorMode(PConstants.RGB, 255, 255, 255);
				drawButtons(p);
				p.fill(255, 255, 255);
				p.textSize(Preferences.p2pl(70));
				p.text("Selected Tile Data", Preferences.p2pl(1620), Preferences.p2pw(100));
				p.textSize(Preferences.p2pl(30));
				p.text("Tile #" + Run.selectedTile.tileNumber, Preferences.p2pl(1630), Preferences.p2pw(190));
				p.text("Food: " + Preferences.formatDecimal.format(Run.selectedTile.food), Preferences.p2pl(1630), Preferences.p2pw(375));
				p.text("Row and Column: (" + (Run.selectedTile.xIndex+1) + ", " + (Run.selectedTile.yIndex+1) + ")", Preferences.p2pl(1850), Preferences.p2pw(200));
				p.text("Regeneration Value: " + Math.round( (Run.selectedTile.regenValue * 1000) ) / 1000.0, Preferences.p2pl(1850), Preferences.p2pw(240));
				p.text("HSV: " + Run.selectedTile.colorH + ", " + Run.selectedTile.colorS + ", " + Run.selectedTile.colorV, Preferences.p2pl(1850), Preferences.p2pw(280));
				p.text("x Range: " + Run.selectedTile.x + " to " + (Run.selectedTile.x + TileManager.tileSize), Preferences.p2pl(1850), Preferences.p2pw(320));
				p.text("y Range: " + Run.selectedTile.y + " to " + (Run.selectedTile.y + TileManager.tileSize), Preferences.p2pl(1850), Preferences.p2pw(360));
				p.textSize(Preferences.p2pl(45));
				p.text("Tile Cooldown Status:  " + Run.selectedTile.deadCooldown + " / " + Preferences.TILE_COOLDOWN_THRESH, Preferences.p2pl(1620), Preferences.p2pw(500));
				
				break;
			}
		}
	}
	
	public void updateHistoryArrays()
	{
		populationHistory.add((double) (CreatureManager.creatures.size()));
		
		ArrayList<Integer> allColors = new ArrayList<Integer>();
		List<Integer> allColorsSorted = null;
		for(int i = 0; i < CreatureManager.creatures.size(); i++)
		{
			allColors.add(CreatureManager.creatures.get(i).color.hashCode());
		}
		allColorsSorted = allColors.subList(0, allColors.size());
		Collections.sort(allColorsSorted);
		
		sortedGeneHistory.add(allColorsSorted);
	}
	
	public void generalMenu(double time, int creatureDeaths, int forcedSpawns, int superMutations, PApplet p)
	{
		int startLabels = 120;
		int generalSpacing = 40;
		
		p.colorMode(PConstants.RGB);
		p.fill(60, 120);
		p.rect(Preferences.p2pl(1600), 0, Preferences.p2pl(1200), Preferences.p2pw(2000));
		p.textSize(Preferences.p2pl(50));
		p.fill(255);
		p.text("World Time: " + Preferences.formatInteger.format(time), Preferences.p2pl(1620), Preferences.p2pw(100));
		p.textSize(Preferences.p2pl(30));
		p.fill(255);
		p.text("Starting Creature Count: " + CreatureManager.startNumCreatures, Preferences.p2pl(1620), Preferences.p2pw(startLabels + 1 * generalSpacing));
		p.text("Current Creature Count: " + (CreatureManager.creatures.size()), Preferences.p2pl(1620), Preferences.p2pw(startLabels + 2 * generalSpacing));
		p.fill(0, 255, 0);
		p.text("Total Creature Births: " + CreatureManager.births, Preferences.p2pl(1620), Preferences.p2pw(startLabels + 5 * generalSpacing));
		p.fill(255, 0, 0);
		p.text("Total Creature Deaths: " + creatureDeaths, Preferences.p2pl(1620), Preferences.p2pw(startLabels + 6 * generalSpacing));
		
		p.fill(255);
		String sign;
		if(CreatureManager.startNumCreatures > CreatureManager.creatures.size()) sign = "-";
		else if(CreatureManager.startNumCreatures == CreatureManager.creatures.size()) sign = "";
		else sign = "+";
		if(sign.equals("+")) p.fill(0, 255, 0);
		else if(sign.equals("-")) p.fill(255, 0, 0);
		else p.fill(95, 260, 220);
		p.text("Total Change: " + sign + Math.abs(CreatureManager.creatures.size() - CreatureManager.startNumCreatures), Preferences.p2pl(1620), Preferences.p2pw(startLabels + 3 * generalSpacing));
		p.fill(255);
		
		p.text("Total Existed: " + CreatureManager.creatureCount, Preferences.p2pl(1620), Preferences.p2pw(startLabels + 8 * generalSpacing));
		p.text("Forced Spawns: " + forcedSpawns, Preferences.p2pl(1620), Preferences.p2pw(startLabels + 9 * generalSpacing));
		p.text("Super Mutations: " + superMutations, Preferences.p2pl(1620), Preferences.p2pw(startLabels + 10 * generalSpacing));
	}
	
	public void drawButtons(PApplet p)
	{
		p.colorMode(PConstants.RGB);
		p.fill(170, 170, 170);
		p.textSize(Preferences.p2pl(35));
		
		menuButton.draw();
		start.draw();
		p.fill(255, 255, 255);
		p.text("Menu", menuButton.getTextX(), menuButton.getTextY());
		if(Run.play) p.fill(119, 255, 51);
		else p.fill(255, 51, 51);
		if(Run.play) p.text("On", start.getTextX(), start.getTextY());
		else p.text("Off", start.getTextX(), start.getTextY());
		
		p.textSize(Preferences.p2pl(30));
		p.colorMode(PConstants.HSB);
		p.fill((int)(p.frameRate / 30.0 * 100), 200, 200);
		p.text("FPS: " + Preferences.formatInteger.format(p.frameRate), start.getX(), start.getY() + 75);
		
		p.colorMode(PConstants.RGB);
		p.textSize(Preferences.p2pl(35));
		if(Run.showMenu)
		{
			p.fill(60, 120);
			p.rect(0, 0, Preferences.p2pl(1600), Preferences.p2pw(360));
			p.fill(170, 170, 170);
			
			killAll.draw();
			spawn.draw();
			spawn20.draw();
			maintainAt.draw();
			maintainPopNum.draw();
			findCreatureByID.draw();
			
			p.fill(255, 255, 255);
			p.text("Kill All", killAll.getTextX(), killAll.getTextY());
			
			if(Run.spawnMode) p.fill(119, 255, 51);
			else p.fill(255, 51, 51);
			p.text("Spawn", spawn.getTextX(), spawn.getTextY());
			
			p.fill(255, 255, 255);
			p.text("Spawn 20", spawn20.getTextX(), spawn20.getTextY());
			if(Run.maintain) p.fill(119, 255, 51);
			else p.fill(255, 51, 51);
			p.text("Maintain At", maintainAt.getTextX(), maintainAt.getTextY());
			p.fill(255, 255, 255);
			p.text(Run.maintainNum, maintainPopNum.getTextX(), maintainPopNum.getTextY());
			p.text("Find ID", findCreatureByID.getTextX(), findCreatureByID.getTextY());
		}
		if(path == MenuPath.CREATURE)
		{
			creatureInfo.draw();
			p.fill(255);
			if(!Run.showCreatureInfo)
			{
				p.text("Brain Info", creatureInfo.getTextX(), creatureInfo.getTextY());
			}
			else p.text("Debug Info", creatureInfo.getTextX(), creatureInfo.getTextY());
		}
	}
	
	public void drawPopulationGraph(PApplet p)
	{
		int horizontalPosition = 1650;
		int verticalPosition = 1350;
		int graphWidth = 900;
		int graphHeight = 200;
		
		p.colorMode(PConstants.RGB);
		p.fill(255);
		p.textSize(Preferences.p2pl(30));
		p.text("Relative Population Over Time", Preferences.p2pl(horizontalPosition), Preferences.p2pw(verticalPosition - 20));
		p.rect(Preferences.p2pl(horizontalPosition), Preferences.p2pw(verticalPosition - 10), Preferences.p2pl(graphWidth), Preferences.p2pw(graphHeight + 20));
		p.fill(0, 0, 255);
		int width = graphWidth / populationHistory.size();
		if(Run.maintain) p.stroke(0, 255, 0);
		else p.stroke(255, 0, 0);
		p.line(Preferences.p2pl(horizontalPosition), Preferences.p2pw(verticalPosition + (graphHeight - (int)(1.0 * Run.maintainNum / CreatureManager.maxObservedCreatures * graphHeight))), 
				Preferences.p2pl(horizontalPosition + graphWidth), Preferences.p2pw(verticalPosition + (graphHeight - (int)(1.0 * Run.maintainNum / CreatureManager.maxObservedCreatures * graphHeight))));
		
		p.stroke(100);
		for(int i = 0; i < populationHistory.size()-1; i++)
		{
			double ratio = (populationHistory.get(i) / CreatureManager.maxObservedCreatures * graphHeight);
			double toRatio = (populationHistory.get(i+1) / CreatureManager.maxObservedCreatures * graphHeight);
			p.line(Preferences.p2pl(horizontalPosition + (i)*width), Preferences.p2pw(verticalPosition + (graphHeight - (int)ratio)), Preferences.p2pl(horizontalPosition + (i+1)*width), Preferences.p2pw(verticalPosition + (graphHeight - (int)toRatio)));
			p.ellipse(Preferences.p2pl(horizontalPosition + (i+1)*width), Preferences.p2pw(verticalPosition + (graphHeight - (int)toRatio)), Preferences.p2pw(5), Preferences.p2pw(5));
		}
	}
	
	public void drawGenePoolGraphs(PApplet p)
	{
		if(!Run.drawGenePoolGraph) return;
		if(sortedGeneHistory.size() == 0) return;
		if(CreatureManager.creatures.size() == 0) return;
		p.colorMode(PConstants.RGB);
		p.fill(255);
		p.textSize(Preferences.p2pl(30));
		double horizontalPosition = 1650.0;
		
		/** v v v  Current gene pool bar  v v v **/
		
		if(Preferences.SHOW_CURRENT_POOL)
		{
			double width = 900.0 / (sortedGeneHistory.get(sortedGeneHistory.size()-1).size()-1.0);
			p.fill(255);
			p.text("Current Gene Pool", Preferences.p2pl(horizontalPosition), Preferences.p2pw(990));
			for(int i = 0; i < sortedGeneHistory.get(sortedGeneHistory.size()-1).size()-1; i++)
			{
				p.fill(sortedGeneHistory.get(sortedGeneHistory.size()-1).get(i));
				p.stroke(sortedGeneHistory.get(sortedGeneHistory.size()-1).get(i));
				p.rect(Preferences.p2pl(horizontalPosition) + Preferences.p2pl(i * width), Preferences.p2pw(1000), Preferences.p2pl(width), Preferences.p2pw(20));
			}
		}
		
		/** v v v  Gene pool history graph  v v v **/
		
		p.fill(255);
		// vertical positioning of the graph
		double verticalPosition = 1080.0;
		
		double graphHeight = 200.0;
		double graphWidth = 900.0;
		
		// pixel length of each entry of the history
		double unitWidth = graphWidth / sortedGeneHistory.size();
		
		p.text("Gene Pool History", Preferences.p2pl(horizontalPosition), Preferences.p2pw(verticalPosition - 15));
		for(int record = 0; record < sortedGeneHistory.size(); record++)
		{
			double entryHeight = graphHeight / sortedGeneHistory.get(record).size();
			for(int entry = 0; entry < sortedGeneHistory.get(record).size(); entry++)
			{
				p.fill(sortedGeneHistory.get(record).get(entry));
				p.stroke(sortedGeneHistory.get(record).get(entry));
				p.rect(Preferences.p2pl(horizontalPosition) + Preferences.p2pl(record * unitWidth), Preferences.p2pw((int)verticalPosition) + Preferences.p2pw(entry * entryHeight), 
						Preferences.p2pl(unitWidth), Preferences.p2pw(entryHeight));
			}
		}
		p.stroke(0);
	}
	
	public void cutGraphs()
	{
		int cutSize = Preferences.DISPLAY_GRAPH_SIZE;
		boolean cutPopulationHistory = false;
		boolean cutGeneHistory = false;
		if(populationHistory.size() > cutSize) cutPopulationHistory = true;
		if(sortedGeneHistory.size() > cutSize) cutGeneHistory = true;
		
		if(cutPopulationHistory) populationHistory = populationHistory.subList(populationHistory.size() - cutSize, populationHistory.size() - 1);
		if(cutGeneHistory) sortedGeneHistory = sortedGeneHistory.subList(sortedGeneHistory.size() - cutSize, sortedGeneHistory.size() - 1);
	}
	
	public void drawSingleCreature(Creature c, PApplet p) // done, not sure how it works, so don't touch!!!!!
	{
		p.colorMode(PConstants.RGB);
		p.fill(c.color.hashCode()); // 1700, 320
		int leftSensorXChange = (int) (c.leftSensorX - c.locationX);
		int leftSensorYChange = (int) (c.leftSensorY - c.locationY);
		int rightSensorXChange = (int) (c.rightSensorX - c.locationX);
		int rightSensorYChange = (int) (c.rightSensorY - c.locationY);
		int killerXChange = (int) (c.midSensorX - c.locationX);
		int killerYChange = (int) (c.midSensorY - c.locationY);
		p.stroke(50);
		p.line(Preferences.p2pl(1700), Preferences.p2pw(150), Preferences.p2pl(1700) + leftSensorXChange, Preferences.p2pw(150) + leftSensorYChange);
		p.line(Preferences.p2pl(1700), Preferences.p2pw(150), Preferences.p2pl(1700) + rightSensorXChange, Preferences.p2pw(150) + rightSensorYChange);
		p.line(Preferences.p2pl(1700), Preferences.p2pw(150), Preferences.p2pl(1700) + killerXChange, Preferences.p2pw(150) + killerYChange);
		p.stroke(0);
		p.colorMode(PConstants.HSB);
		p.fill(c.leftSensorColor, 120, 120);
		p.ellipse(Preferences.p2pl(1700) + leftSensorXChange, Preferences.p2pw(150) + leftSensorYChange, Preferences.p2pw(15) * 1, Preferences.p2pw(15) * 1);
		p.fill(c.rightSensorColor, 120, 120);
		p.ellipse(Preferences.p2pl(1700) + rightSensorXChange, Preferences.p2pw(150) + rightSensorYChange, Preferences.p2pw(15) * 1, Preferences.p2pw(15) * 1);
		p.colorMode(PConstants.RGB);
		p.fill(c.color.hashCode());
		p.ellipse(Preferences.p2pl(1700), Preferences.p2pw(150), (int)c.diameter * 1, (int)c.diameter * 1);
		p.fill(255);
	}
	
	public void drawCreatureBrain(Creature c, PApplet p) // top left = 1620, 800
	{
		int verticalSpacing = Preferences.p2pw(640 / CreatureManager.creatures.get(0).brainLength);
		p.textSize(Preferences.p2pw(50));
		p.text("Inputs", Preferences.p2pl(1620), Preferences.p2pw(900));
		p.text("Hidden Layers", Preferences.p2pl(1940), Preferences.p2pw(900));
		p.text("Outputs", Preferences.p2pl(2400), Preferences.p2pw(900));
		p.colorMode(PConstants.RGB);
		// Left food, center food, mouth food, right food, size, ......
		p.textSize(Preferences.p2pw(30));
		p.text("L Food", Preferences.p2pl(1610), Preferences.p2pw(950) + verticalSpacing * 0);
		p.text("C Food", Preferences.p2pl(1610), Preferences.p2pw(950) + verticalSpacing * 1);
		p.text("M Food", Preferences.p2pl(1610), Preferences.p2pw(950) + verticalSpacing * 2);
		p.text("R Food", Preferences.p2pl(1610), Preferences.p2pw(950) + verticalSpacing * 3);
		p.text("Size", Preferences.p2pl(1610), Preferences.p2pw(950) + verticalSpacing * 4);
		p.text("Cr Dist", Preferences.p2pl(1610), Preferences.p2pw(950) + verticalSpacing * 5);
		p.text("Co Diff", Preferences.p2pl(1610), Preferences.p2pw(950) + verticalSpacing * 6);
		p.text("Cr Angl", Preferences.p2pl(1610), Preferences.p2pw(950) + verticalSpacing * 7);
		p.text("Cr Bir?", Preferences.p2pl(1610), Preferences.p2pw(950) + verticalSpacing * 8);
		p.text("# in 10", Preferences.p2pl(1610), Preferences.p2pw(950) + verticalSpacing * 9);
		p.text("-----", Preferences.p2pl(1610), Preferences.p2pw(950) + verticalSpacing * 10);
		p.text("-----", Preferences.p2pl(1610), Preferences.p2pw(950) + verticalSpacing * 11);
		// Forward velocity, rotational velocity, eat, attack, give birth, attack length,
		p.text("For Vel", Preferences.p2pl(2400), Preferences.p2pw(950) + verticalSpacing * 0);
		p.text("Rot Vel", Preferences.p2pl(2400), Preferences.p2pw(950) + verticalSpacing * 1);
		p.text("Eat Rate", Preferences.p2pl(2400), Preferences.p2pw(950) + verticalSpacing * 2);
		if(c.outputNeurons[3] >= 0) p.fill(0, 255, 0);
		else p.fill(255, 0, 0);
		p.text("Attack?", Preferences.p2pl(2400), Preferences.p2pw(950) + verticalSpacing * 3);
		if(c.outputNeurons[4] >= 0) p.fill(0, 255, 0);
		else p.fill(255, 0, 0);
		p.text("Birth?", Preferences.p2pl(2400), Preferences.p2pw(950) + verticalSpacing * 4);
		p.fill(255);
		p.text("Att Size", Preferences.p2pl(2400), Preferences.p2pw(950) + verticalSpacing * 5);
		for(int i = 0; i < c.sensorInput.length; i++)
		{
			p.fill(255);
			p.textSize(Preferences.p2pw(30));
			//rect(Variables.p2pl(1620), Variables.p2pw(800 + verticalSpacing * i), Variables.p2pl(100), Variables.p2pw(50));
			p.text(Preferences.formatDecimal.format(c.sensorInput[i]) + "", Preferences.p2pl(1710), Preferences.p2pw(950) + verticalSpacing * i);
		}
		for(int i = 0; i < c.hidLayer1.length; i++)
		{
			p.fill(255);
			p.textSize(Preferences.p2pw(30));
			//rect(Variables.p2pl(1620), Variables.p2pw(800 + verticalSpacing * i), Variables.p2pl(100), Variables.p2pw(50));
			p.text(Preferences.formatDecimal.format(c.hidLayer1[i]) + "", Preferences.p2pl(1935), Preferences.p2pw(950) + Preferences.p2pw(40) * i);
		}
		for(int i = 0; i < c.hidLayer2.length; i++)
		{
			p.fill(255);
			p.textSize(Preferences.p2pw(30));
			//rect(Variables.p2pl(1620), Variables.p2pw(800 + verticalSpacing * i), Variables.p2pl(100), Variables.p2pw(50));
			p.text(Preferences.formatDecimal.format(c.hidLayer2[i]) + "", Preferences.p2pl(2155), Preferences.p2pw(950) + Preferences.p2pw(40) * i);
		}
		for(int i = 0; i < c.outputNeurons.length; i++)
		{
			p.fill(255);
			p.textSize(Preferences.p2pw(30));
			//rect(Variables.p2pl(1620), Variables.p2pw(800 + verticalSpacing * i), Variables.p2pl(100), Variables.p2pw(50));
			p.text(Preferences.formatDecimal.format(c.outputNeurons[i]) + "", Preferences.p2pl(2510), Preferences.p2pw(950) + verticalSpacing * i);
		}
		
		for(int i = 0; i < c.inputNeurons.length; i++)
		{
			int color = 0;
			for(int one = 0; one < c.hidLayer1.length; one++)
			{
				color = (int) (Preferences.colorSigmoid(c.inputToLayer1Axons[i][one].weight + 1.0) * 255);
				if(color < 0) color = 0;
				if(color > 255) color = 255;
				p.stroke(color);
				p.line(Preferences.p2pl(1780), Preferences.p2pw(945) + verticalSpacing * i, Preferences.p2pl(1920), Preferences.p2pw(940) + Preferences.p2pw(40) * one);
			}
		}
		for(int i = 0; i < c.hidLayer1.length; i++)
		{
			int color = 0;
			for(int o = 0; o < c.hidLayer2.length; o++)
			{
				color = (int) (Preferences.colorSigmoid(c.layer1ToLayer2Axons[i][o].weight + 1.0) * 255);
				if(color < 0) color = 0;
				if(color > 255) color = 255;
				p.stroke(color);
				p.line(Preferences.p2pl(2020), Preferences.p2pw(945) + Preferences.p2pw(40) * i, Preferences.p2pl(2140), Preferences.p2pw(940) + Preferences.p2pw(40) * o);
			}
		}
		for(int i = 0; i < c.hidLayer2.length; i++)
		{
			int color = 0;
			for(int o = 0; o < c.outputNeurons.length; o++)
			{
				color = (int) (Preferences.colorSigmoid(c.layer2ToOutputAxons[i][o].weight + 1.0) * 255);
				if(color < 0) color = 0;
				if(color > 255) color = 255;
				p.stroke(color);
				p.line(Preferences.p2pl(2230), Preferences.p2pw(945) + Preferences.p2pw(40) * i, Preferences.p2pl(2380), Preferences.p2pw(940) + verticalSpacing * o);
			}
		}
		
		p.stroke(0);
	}
}
