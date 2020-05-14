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
	public static Button start, killAll, spawn, spawn20, kill, maintainAt, maintainPopNum, findCreatureByID, saveFPS;
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
		populationHistory.add((double) Prefs.START_NUM_CREATURES);
	}
	
	public void menuInit(PApplet p)
	{
		menuButton = new Button(Prefs.p2pl(2400), 0, Prefs.p2pl(185), Prefs.p2pw(120), p);
		start = new Button(Prefs.p2pl(2400), Prefs.p2pw(120), Prefs.p2pl(185), Prefs.p2pw(120), p);
		killAll = new Button(Prefs.p2pl(0), 0, Prefs.p2pl(200), Prefs.p2pw(120), p);
		spawn = new Button(Prefs.p2pl(200), 0, Prefs.p2pl(200), Prefs.p2pw(120), p);
		spawn20 = new Button(Prefs.p2pl(400), 0, Prefs.p2pl(250), Prefs.p2pw(120), p);
		findCreatureByID = new Button(Prefs.p2pl(650), 0, Prefs.p2pl(200), Prefs.p2pw(120), p);
		maintainAt = new Button(Prefs.p2pl(850), 0, Prefs.p2pl(280), Prefs.p2pw(120), p);
		maintainPopNum = new Button(Prefs.p2pl(1100), 0, Prefs.p2pl(140), Prefs.p2pw(120), p);
		creatureInfo = new Button(Prefs.p2pl(1650), Prefs.p2pw(250), Prefs.p2pl(280), Prefs.p2pw(120), p);
		saveFPS = new Button(Prefs.p2pl(1240), 0, Prefs.p2pl(240), Prefs.p2pw(120), p);
	}
	
	public void drawMenu(PApplet p)
	{
		// side bar path split
		switch(path)
		{
			case GENERAL:
			{
				p.colorMode(PConstants.RGB);
				
				generalMenu(Run.getDisplayTime(), CreatureManager.creatureDeaths, Run.forcedSpawns, Run.superMutations, p);
				
				drawButtons(p);
				cutGraphs();
				drawGenePoolHistoryGraph(p);
				drawPopulationGraph(p);
				
				break;
			}
			case CREATURE:
			{
				p.colorMode(PConstants.RGB);
				p.fill(60, 120);
				p.rect(Prefs.p2pl(1600), 0, Prefs.p2pl(1200), Prefs.p2pw(2000));
				drawButtons(p);
				p.fill(Run.selectedCreature.color.hashCode());
				drawSingleCreature(Run.selectedCreature, p);
				p.fill(255);
				p.textSize(Prefs.p2pl(65));
				p.text("Creature Data", Prefs.p2pl(1800), Prefs.p2pw(100));
				p.textSize(Prefs.p2pl(30));
				int spacing = 45;
				int shift = 420;
				int shift2 = 340;
				
				p.text("ID: " + Run.selectedCreature.ID, Prefs.p2pl(1800), Prefs.p2pw(160));
				p.text("Age: " + Prefs.formatInteger.format(Run.selectedCreature.fitness), Prefs.p2pl(1800), Prefs.p2pw(210));
				p.text("Generation: " + Run.selectedCreature.generation, Prefs.p2pl(2100), Prefs.p2pw(160));
				p.text("Parent: " + Run.selectedCreature.parentID, Prefs.p2pl(2100), Prefs.p2pw(210));
				
				p.text("Total Eaten: " + Prefs.formatInteger.format(Run.selectedCreature.totalEaten), Prefs.p2pl(1620), Prefs.p2pw(shift + 0 * spacing));
				p.text("Total Decayed: " + Prefs.formatInteger.format(Run.selectedCreature.totalDecayed), Prefs.p2pl(1620), Prefs.p2pw(shift + 1 * spacing));
				p.text("Location: ( " + Prefs.formatInteger.format(Run.selectedCreature.locationX) + ", " + Prefs.formatInteger.format(Run.selectedCreature.locationY) + " )", Prefs.p2pl(1620), Prefs.p2pw(shift + 2 * spacing));
				p.text("Left XY: ( " + Prefs.formatInteger.format(Run.selectedCreature.leftSensorX) + ", " + Prefs.formatInteger.format(Run.selectedCreature.leftSensorY) + " )", Prefs.p2pl(1620), Prefs.p2pw(shift + 3 * spacing));
				p.text("Mid XP: ( " + Prefs.formatInteger.format(Run.selectedCreature.midSensorX) + ", " + Prefs.formatInteger.format(Run.selectedCreature.midSensorY) + " )", Prefs.p2pl(1620), Prefs.p2pw(shift + 4 * spacing));
				p.text("Right XY: ( " + Prefs.formatInteger.format(Run.selectedCreature.rightSensorX) + ", " + Prefs.formatInteger.format(Run.selectedCreature.rightSensorY) + " )", Prefs.p2pl(1620), Prefs.p2pw(shift + 5 * spacing));
				p.text("Mouth XY: ( " + Prefs.formatInteger.format(Run.selectedCreature.mouthSensorX) + ", " + Prefs.formatInteger.format(Run.selectedCreature.mouthSensorY) + " )", Prefs.p2pl(1620), Prefs.p2pw(shift + 6 * spacing));
				p.text("Heading: " + Prefs.formatInteger.format((Run.selectedCreature.rotation * 180 / Math.PI)), Prefs.p2pl(1620), Prefs.p2pw(shift + 7 * spacing));
				
				p.text("Size Decay", Prefs.p2pl(2200), Prefs.p2pw(shift2 + 0 * spacing));
				p.text("Age Decay", Prefs.p2pl(2200), Prefs.p2pw(shift2 + 1 * spacing));
				p.text("Eat Decay", Prefs.p2pl(2200), Prefs.p2pw(shift2 + 2 * spacing));
				p.text("Turn Decay", Prefs.p2pl(2200), Prefs.p2pw(shift2 + 3 * spacing));
				p.text("Fwd Decay", Prefs.p2pl(2200), Prefs.p2pw(shift2 + 4 * spacing));
				p.text("Att Decay", Prefs.p2pl(2200), Prefs.p2pw(shift2 + 5 * spacing));
				p.text("Eat Rate", Prefs.p2pl(2200), Prefs.p2pw(shift2 + 6 * spacing));
				p.fill(95, 260, 220);
				p.text("Current Size: " + (int)Run.selectedCreature.size, Prefs.p2pl(2200), Prefs.p2pw(shift2 + 10 * spacing));
				p.fill(0);
				p.fill(255, 0, 0);
				p.text(Prefs.formatDecimal.format(Run.selectedCreature.sizeDecay), Prefs.p2pl(2400), Prefs.p2pw(shift2 + 0 * spacing));
				p.text(Prefs.formatDecimal.format(Run.selectedCreature.fitnessDecay), Prefs.p2pl(2400), Prefs.p2pw(shift2 + 1 * spacing));
				p.text(Prefs.formatDecimal.format(Run.selectedCreature.eatRateDecay), Prefs.p2pl(2400), Prefs.p2pw(shift2 + 2 * spacing));
				p.text(Prefs.formatDecimal.format(Run.selectedCreature.rotationDecay), Prefs.p2pl(2400), Prefs.p2pw(shift2 + 3 * spacing));
				p.text(Prefs.formatDecimal.format(Run.selectedCreature.forwardDecay), Prefs.p2pl(2400), Prefs.p2pw(shift2 + 4 * spacing));
				p.text(Prefs.formatDecimal.format(Run.selectedCreature.attackDecay), Prefs.p2pl(2400), Prefs.p2pw(shift2 + 5 * spacing));
				p.fill(0, 255, 0);
				p.text(Prefs.formatDecimal.format(Run.selectedCreature.eatRate), Prefs.p2pl(2400), Prefs.p2pw(shift2 + 6 * spacing));
				if(Run.selectedCreature.energyChange < 0) p.fill(255, 0, 0);
				else p.fill(0, 255, 0);
				p.text("Energy Change: " + Prefs.formatDecimal.format(Run.selectedCreature.energyChange), Prefs.p2pl(2200), Prefs.p2pw(shift2 + 8 * spacing));
				p.fill(255);
				
				if(Run.showCreatureInfo)
				{
					if(Run.selectedCreature != null && Run.selectedCreature.nearestCreature != null)
					{
						p.text("Nearest Creature ID: " + Run.selectedCreature.nearestCreature.ID, Prefs.p2pl(1620), Prefs.p2pw(shift + 8 * spacing));
						p.text("Nearest ID Distance: " + Prefs.formatDecimal.format(Run.selectedCreature.distToNearest), Prefs.p2pl(1620), Prefs.p2pw(shift + 9 * spacing));
						p.text("Nearest ID Color Diff: " + Run.selectedCreature.colorDifferenceToNearest, Prefs.p2pl(1620), Prefs.p2pw(shift + 10 * spacing));
						p.text("Angle change to nearest: " + Run.selectedCreature.angleToNearest, Prefs.p2pl(1620), Prefs.p2pw(shift + 11 * spacing));
						p.text("Creatures within 10 Tiles: " + Run.selectedCreature.numCreaturesWithin10, Prefs.p2pl(1620), Prefs.p2pw(shift + 12 * spacing));
					}
					else
					{
						p.text("Nearest Creature ID: null", Prefs.p2pl(1620), Prefs.p2pw(shift + 8 * spacing));
						p.text("Nearest ID Distance: null", Prefs.p2pl(1620), Prefs.p2pw(shift + 9 * spacing));
						p.text("Nearest ID Color Diff: null", Prefs.p2pl(1620), Prefs.p2pw(shift + 10 * spacing));
						p.text("Angle change to nearest: null", Prefs.p2pl(1620), Prefs.p2pw(shift + 11 * spacing));
						p.text("Creatures within 10 Tiles: 0", Prefs.p2pl(1620), Prefs.p2pw(shift + 12 * spacing));
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
				p.rect(Prefs.p2pl(1600), 0, Prefs.p2pl(1200), Prefs.p2pw(2000));
				p.colorMode(PConstants.HSB, 360, 100, 100);
				p.fill(Run.selectedTile.colorH, Run.selectedTile.colorS, Run.selectedTile.colorV);
				p.rect(Prefs.p2pl(1620), Prefs.p2pw(140), Prefs.p2pl(200), Prefs.p2pl(200)); // draw the tile
				p.colorMode(PConstants.RGB, 255, 255, 255);
				drawButtons(p);
				p.fill(255, 255, 255);
				p.textSize(Prefs.p2pl(70));
				p.text("Selected Tile Data", Prefs.p2pl(1620), Prefs.p2pw(110));
				p.textSize(Prefs.p2pl(30));
				p.text("Tile #" + Run.selectedTile.tileNumber, Prefs.p2pl(1630), Prefs.p2pw(200));
				p.text("Food: " + Prefs.formatDecimal.format(Run.selectedTile.food), Prefs.p2pl(1630), Prefs.p2pw(460));
				p.text("Row and Column: (" + (Run.selectedTile.xIndex+1) + ", " + (Run.selectedTile.yIndex+1) + ")", Prefs.p2pl(1850), Prefs.p2pw(200));
				p.text("Regeneration Value: " + Math.round( (Run.selectedTile.regenValue * 1000) ) / 1000.0, Prefs.p2pl(1850), Prefs.p2pw(250));
				p.text("HSV: " + Run.selectedTile.colorH + ", " + Run.selectedTile.colorS + ", " + Run.selectedTile.colorV, Prefs.p2pl(1850), Prefs.p2pw(300));
				p.text("x Range: " + Run.selectedTile.x + " to " + (Run.selectedTile.x + TileManager.tileSize), Prefs.p2pl(1850), Prefs.p2pw(350));
				p.text("y Range: " + Run.selectedTile.y + " to " + (Run.selectedTile.y + TileManager.tileSize), Prefs.p2pl(1850), Prefs.p2pw(400));
				p.textSize(Prefs.p2pl(45));
				p.text("Tile Cooldown Status:  " + Run.selectedTile.deadCooldown + " / " + Prefs.TILE_COOLDOWN_THRESH, Prefs.p2pl(1620), Prefs.p2pw(600));
				
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
		p.rect(Prefs.p2pl(1600), 0, Prefs.p2pl(1200), Prefs.p2pw(2000));
		p.textSize(Prefs.p2pl(50));
		p.fill(200);
		p.text("World Time: " + Prefs.formatInteger.format(time), Prefs.p2pl(1620), Prefs.p2pw(100));
		p.textSize(Prefs.p2pl(30));
		p.fill(255);
		p.text("Starting Creature Count: " + CreatureManager.startNumCreatures, Prefs.p2pl(1620), Prefs.p2pw(startLabels + 1 * generalSpacing));
		p.text("Current Creature Count: " + (CreatureManager.creatures.size()), Prefs.p2pl(1620), Prefs.p2pw(startLabels + 2 * generalSpacing));
		p.fill(0, 255, 0);
		p.text("Total Creature Births: " + CreatureManager.births, Prefs.p2pl(1620), Prefs.p2pw(startLabels + 5 * generalSpacing));
		p.fill(255, 0, 0);
		p.text("Total Creature Deaths: " + creatureDeaths, Prefs.p2pl(1620), Prefs.p2pw(startLabels + 6 * generalSpacing));
		
		p.fill(255);
		String sign;
		if(CreatureManager.startNumCreatures > CreatureManager.creatures.size()) sign = "-";
		else if(CreatureManager.startNumCreatures == CreatureManager.creatures.size()) sign = "";
		else sign = "+";
		if(sign.equals("+")) p.fill(0, 255, 0);
		else if(sign.equals("-")) p.fill(255, 0, 0);
		else p.fill(95, 260, 220);
		p.text("Total Change: " + sign + Math.abs(CreatureManager.creatures.size() - CreatureManager.startNumCreatures), Prefs.p2pl(1620), Prefs.p2pw(startLabels + 3 * generalSpacing));
		p.fill(255);
		
		p.text("Total Existed: " + CreatureManager.creatureCount, Prefs.p2pl(1620), Prefs.p2pw(startLabels + 8 * generalSpacing));
		p.text("Forced Spawns: " + forcedSpawns, Prefs.p2pl(1620), Prefs.p2pw(startLabels + 9 * generalSpacing));
		p.text("Super Mutations: " + superMutations, Prefs.p2pl(1620), Prefs.p2pw(startLabels + 10 * generalSpacing));
	}
	
	public void drawButtons(PApplet p)
	{
		p.colorMode(PConstants.RGB);
		p.fill(170, 170, 170);
		p.textSize(Prefs.p2pl(35));
		
		menuButton.draw();
		start.draw();
		p.fill(255, 255, 255);
		p.text("Menu", menuButton.getTextX(), menuButton.getTextY());
		if(Run.play) p.fill(119, 255, 51);
		else p.fill(255, 51, 51);
		if(Run.play) p.text("On", start.getTextX(), start.getTextY());
		else p.text("Off", start.getTextX(), start.getTextY());
		
		p.textSize(Prefs.p2pl(30));
		p.colorMode(PConstants.HSB);
		p.fill((int)(p.frameRate / 30.0 * 100), 200, 200);
		p.text("FPS: " + Prefs.formatInteger.format(p.frameRate), start.getX(), start.getY() + 75);
		
		p.colorMode(PConstants.RGB);
		p.textSize(Prefs.p2pl(35));
		if(Run.showMenu)
		{
			p.fill(60, 120);
			p.rect(0, 0, Prefs.p2pl(1600), Prefs.p2pw(360));
			p.fill(170, 170, 170);
			
			killAll.draw();
			spawn.draw();
			spawn20.draw();
			maintainAt.draw();
			maintainPopNum.draw();
			findCreatureByID.draw();
			saveFPS.draw();
			
			p.fill(255, 255, 255);
			p.text("Kill All", killAll.getTextX(), killAll.getTextY());
			
			if(Run.spawnMode) p.fill(119, 255, 51);
			else p.fill(255, 51, 51);
			p.text("Spawn", spawn.getTextX(), spawn.getTextY());
			
			p.fill(255, 255, 255);
			p.text("Spawn 20", spawn20.getTextX(), spawn20.getTextY());
			
			if(Run.maintainPop) p.fill(119, 255, 51);
			else p.fill(255, 51, 51);
			p.text("Maintain At", maintainAt.getTextX(), maintainAt.getTextY());
						
			p.fill(255, 255, 255);
			p.text(Run.maintainNum, maintainPopNum.getTextX(), maintainPopNum.getTextY());
			p.text("Find ID", findCreatureByID.getTextX(), findCreatureByID.getTextY());
			
			if(Run.saveFPS) p.fill(119, 255, 51);
			else p.fill(255, 51, 51);
			p.text("FPS Saver", saveFPS.getTextX(), saveFPS.getTextY());
			p.fill(255, 255, 255);
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
		p.textSize(Prefs.p2pl(30));
		p.text("Relative Population Over Time", Prefs.p2pl(horizontalPosition), Prefs.p2pw(verticalPosition - 20));
		p.rect(Prefs.p2pl(horizontalPosition), Prefs.p2pw(verticalPosition - 10), Prefs.p2pl(graphWidth), Prefs.p2pw(graphHeight + 20));
		p.fill(0, 0, 255);
		int width = graphWidth / populationHistory.size();
		if(Run.maintainPop) p.stroke(0, 255, 0);
		else p.stroke(255, 0, 0);
		p.line(Prefs.p2pl(horizontalPosition), Prefs.p2pw(verticalPosition + (graphHeight - (int)(1.0 * Run.maintainNum / CreatureManager.maxObservedCreatures * graphHeight))), 
				Prefs.p2pl(horizontalPosition + graphWidth), Prefs.p2pw(verticalPosition + (graphHeight - (int)(1.0 * Run.maintainNum / CreatureManager.maxObservedCreatures * graphHeight))));
		
		p.stroke(100);
		for(int i = 0; i < populationHistory.size()-1; i++)
		{
			double ratio = (populationHistory.get(i) / CreatureManager.maxObservedCreatures * graphHeight);
			double toRatio = (populationHistory.get(i+1) / CreatureManager.maxObservedCreatures * graphHeight);
			p.line(Prefs.p2pl(horizontalPosition + (i)*width), Prefs.p2pw(verticalPosition + (graphHeight - (int)ratio)), Prefs.p2pl(horizontalPosition + (i+1)*width), Prefs.p2pw(verticalPosition + (graphHeight - (int)toRatio)));
			p.ellipse(Prefs.p2pl(horizontalPosition + (i+1)*width), Prefs.p2pw(verticalPosition + (graphHeight - (int)toRatio)), Prefs.p2pw(5), Prefs.p2pw(5));
		}
	}
	
	public void drawGenePoolHistoryGraph(PApplet p)
	{
		if(!Run.drawGenePoolGraph) return;
		if(sortedGeneHistory.size() == 0) return;
		if(CreatureManager.creatures.size() == 0) return;
		p.colorMode(PConstants.RGB);
		p.fill(255);
		p.textSize(Prefs.p2pl(30));
		double horizontalPosition = 1650.0;
		
		p.fill(255);
		// vertical positioning of the graph
		double verticalPosition = 1080.0;
		
		double graphHeight = 200.0;
		double graphWidth = 900.0;
		
		// pixel length of each entry of the history
		double unitWidth = graphWidth / sortedGeneHistory.size();
		
		p.text("Gene Pool History", Prefs.p2pl(horizontalPosition), Prefs.p2pw(verticalPosition - 15));
		for(int record = 0; record < sortedGeneHistory.size(); record++)
		{
			double entryHeight = graphHeight / sortedGeneHistory.get(record).size();
			for(int entry = 0; entry < sortedGeneHistory.get(record).size(); entry++)
			{
				p.fill(sortedGeneHistory.get(record).get(entry));
				p.stroke(sortedGeneHistory.get(record).get(entry));
				p.rect(Prefs.p2pl(horizontalPosition) + Prefs.p2pl(record * unitWidth), Prefs.p2pw((int)verticalPosition) + Prefs.p2pw(entry * entryHeight), 
						Prefs.p2pl(unitWidth), Prefs.p2pw(entryHeight));
			}
		}
		p.stroke(0);
	}
	
	public void cutGraphs()
	{
		int cutSize = Prefs.DISPLAY_GRAPH_SIZE;
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
		p.line(Prefs.p2pl(1700), Prefs.p2pw(150), Prefs.p2pl(1700) + leftSensorXChange, Prefs.p2pw(150) + leftSensorYChange);
		p.line(Prefs.p2pl(1700), Prefs.p2pw(150), Prefs.p2pl(1700) + rightSensorXChange, Prefs.p2pw(150) + rightSensorYChange);
		p.line(Prefs.p2pl(1700), Prefs.p2pw(150), Prefs.p2pl(1700) + killerXChange, Prefs.p2pw(150) + killerYChange);
		p.stroke(0);
		p.colorMode(PConstants.HSB);
		p.fill(c.leftSensorColor, 120, 120);
		p.ellipse(Prefs.p2pl(1700) + leftSensorXChange, Prefs.p2pw(150) + leftSensorYChange, Prefs.p2pw(15) * 1, Prefs.p2pw(15) * 1);
		p.fill(c.rightSensorColor, 120, 120);
		p.ellipse(Prefs.p2pl(1700) + rightSensorXChange, Prefs.p2pw(150) + rightSensorYChange, Prefs.p2pw(15) * 1, Prefs.p2pw(15) * 1);
		p.colorMode(PConstants.RGB);
		p.fill(c.color.hashCode());
		p.ellipse(Prefs.p2pl(1700), Prefs.p2pw(150), (int)c.diameter * 1, (int)c.diameter * 1);
		p.fill(255);
	}
	
	public void drawCreatureBrain(Creature c, PApplet p) // top left = 1620, 800
	{
		int verticalSpacing = Prefs.p2pw(640 / CreatureManager.creatures.get(0).brainLength);
		p.textSize(Prefs.p2pw(50));
		p.text("Inputs", Prefs.p2pl(1620), Prefs.p2pw(900));
		p.text("Hidden Layers", Prefs.p2pl(1940), Prefs.p2pw(900));
		p.text("Outputs", Prefs.p2pl(2400), Prefs.p2pw(900));
		p.colorMode(PConstants.RGB);
		// Left food, center food, mouth food, right food, size, ......
		p.textSize(Prefs.p2pw(30));
		p.text("L Food", Prefs.p2pl(1610), Prefs.p2pw(950) + verticalSpacing * 0);
		p.text("C Food", Prefs.p2pl(1610), Prefs.p2pw(950) + verticalSpacing * 1);
		p.text("M Food", Prefs.p2pl(1610), Prefs.p2pw(950) + verticalSpacing * 2);
		p.text("R Food", Prefs.p2pl(1610), Prefs.p2pw(950) + verticalSpacing * 3);
		p.text("Size", Prefs.p2pl(1610), Prefs.p2pw(950) + verticalSpacing * 4);
		p.text("Cr Dist", Prefs.p2pl(1610), Prefs.p2pw(950) + verticalSpacing * 5);
		p.text("Co Diff", Prefs.p2pl(1610), Prefs.p2pw(950) + verticalSpacing * 6);
		p.text("Cr Angl", Prefs.p2pl(1610), Prefs.p2pw(950) + verticalSpacing * 7);
		p.text("Cr Bir?", Prefs.p2pl(1610), Prefs.p2pw(950) + verticalSpacing * 8);
		p.text("# in 10", Prefs.p2pl(1610), Prefs.p2pw(950) + verticalSpacing * 9);
		p.text("-----", Prefs.p2pl(1610), Prefs.p2pw(950) + verticalSpacing * 10);
		p.text("-----", Prefs.p2pl(1610), Prefs.p2pw(950) + verticalSpacing * 11);
		// Forward velocity, rotational velocity, eat, attack, give birth, attack length,
		p.text("For Vel", Prefs.p2pl(2400), Prefs.p2pw(950) + verticalSpacing * 0);
		p.text("Rot Vel", Prefs.p2pl(2400), Prefs.p2pw(950) + verticalSpacing * 1);
		p.text("Eat Rate", Prefs.p2pl(2400), Prefs.p2pw(950) + verticalSpacing * 2);
		if(c.outputNeurons[3] >= 0) p.fill(0, 255, 0);
		else p.fill(255, 0, 0);
		p.text("Attack?", Prefs.p2pl(2400), Prefs.p2pw(950) + verticalSpacing * 3);
		if(c.outputNeurons[4] >= 0) p.fill(0, 255, 0);
		else p.fill(255, 0, 0);
		p.text("Birth?", Prefs.p2pl(2400), Prefs.p2pw(950) + verticalSpacing * 4);
		p.fill(255);
		p.text("Att Size", Prefs.p2pl(2400), Prefs.p2pw(950) + verticalSpacing * 5);
		for(int i = 0; i < c.sensorInput.length; i++)
		{
			p.fill(255);
			p.textSize(Prefs.p2pw(30));
			//rect(Variables.p2pl(1620), Variables.p2pw(800 + verticalSpacing * i), Variables.p2pl(100), Variables.p2pw(50));
			p.text(Prefs.formatDecimal.format(c.sensorInput[i]) + "", Prefs.p2pl(1710), Prefs.p2pw(950) + verticalSpacing * i);
		}
		for(int i = 0; i < c.hidLayer1.length; i++)
		{
			p.fill(255);
			p.textSize(Prefs.p2pw(30));
			//rect(Variables.p2pl(1620), Variables.p2pw(800 + verticalSpacing * i), Variables.p2pl(100), Variables.p2pw(50));
			p.text(Prefs.formatDecimal.format(c.hidLayer1[i]) + "", Prefs.p2pl(1935), Prefs.p2pw(950) + Prefs.p2pw(40) * i);
		}
		for(int i = 0; i < c.hidLayer2.length; i++)
		{
			p.fill(255);
			p.textSize(Prefs.p2pw(30));
			//rect(Variables.p2pl(1620), Variables.p2pw(800 + verticalSpacing * i), Variables.p2pl(100), Variables.p2pw(50));
			p.text(Prefs.formatDecimal.format(c.hidLayer2[i]) + "", Prefs.p2pl(2155), Prefs.p2pw(950) + Prefs.p2pw(40) * i);
		}
		for(int i = 0; i < c.outputNeurons.length; i++)
		{
			p.fill(255);
			p.textSize(Prefs.p2pw(30));
			//rect(Variables.p2pl(1620), Variables.p2pw(800 + verticalSpacing * i), Variables.p2pl(100), Variables.p2pw(50));
			p.text(Prefs.formatDecimal.format(c.outputNeurons[i]) + "", Prefs.p2pl(2510), Prefs.p2pw(950) + verticalSpacing * i);
		}
		
		for(int i = 0; i < c.inputNeurons.length; i++)
		{
			int color = 0;
			for(int one = 0; one < c.hidLayer1.length; one++)
			{
				color = (int) (Prefs.colorSigmoid(c.inputToLayer1Axons[i][one].weight + 1.0) * 255);
				if(color < 0) color = 0;
				if(color > 255) color = 255;
				p.stroke(color);
				p.line(Prefs.p2pl(1780), Prefs.p2pw(945) + verticalSpacing * i, Prefs.p2pl(1920), Prefs.p2pw(940) + Prefs.p2pw(40) * one);
			}
		}
		for(int i = 0; i < c.hidLayer1.length; i++)
		{
			int color = 0;
			for(int o = 0; o < c.hidLayer2.length; o++)
			{
				color = (int) (Prefs.colorSigmoid(c.layer1ToLayer2Axons[i][o].weight + 1.0) * 255);
				if(color < 0) color = 0;
				if(color > 255) color = 255;
				p.stroke(color);
				p.line(Prefs.p2pl(2020), Prefs.p2pw(945) + Prefs.p2pw(40) * i, Prefs.p2pl(2140), Prefs.p2pw(940) + Prefs.p2pw(40) * o);
			}
		}
		for(int i = 0; i < c.hidLayer2.length; i++)
		{
			int color = 0;
			for(int o = 0; o < c.outputNeurons.length; o++)
			{
				color = (int) (Prefs.colorSigmoid(c.layer2ToOutputAxons[i][o].weight + 1.0) * 255);
				if(color < 0) color = 0;
				if(color > 255) color = 255;
				p.stroke(color);
				p.line(Prefs.p2pl(2230), Prefs.p2pw(945) + Prefs.p2pw(40) * i, Prefs.p2pl(2380), Prefs.p2pw(940) + verticalSpacing * o);
			}
		}
		
		p.stroke(0);
	}
}
