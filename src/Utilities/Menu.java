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
	List<Double> popHistory;
	ArrayList<Double> UNIV_POP_HISTORY;
	List<List<Integer>> popPropHistory;
	ArrayList<List<Integer>> UNIV_POP_PROP_HISTORY;
	public static Button menuButton;
	public static Button start, killAll, spawn, spawn20, kill, maintainAt, maintainPopNum, findCreatureByID;
	public enum MenuPath
	{
		GENERAL, CREATURE, DATA, TILE;
	}
	public static MenuPath path;
	
	public Menu()
	{
		path = MenuPath.GENERAL;
		popHistory = new ArrayList<Double>();
		UNIV_POP_HISTORY = new ArrayList<Double>();
		popPropHistory = new ArrayList<List<Integer>>();
		UNIV_POP_PROP_HISTORY = new ArrayList<List<Integer>>();
		popHistory.add((double) Variables.START_NUM_CREATURES);
	}
	
	public void menuInit(PApplet p)
	{
		menuButton = new Button(p2pl(2400), 0, p2pl(185), p2pw(120), p);
		start = new Button(p2pl(2400), p2pw(120), p2pl(185), p2pw(120), p);
		killAll = new Button(p2pl(0), 0, p2pl(200), p2pw(120), p);
		spawn = new Button(p2pl(200), 0, p2pl(200), p2pw(120), p);
		spawn20 = new Button(p2pl(400), 0, p2pl(250), p2pw(120), p);
		findCreatureByID = new Button(p2pl(650), 0, p2pl(200), p2pw(120), p);
		maintainAt = new Button(p2pl(850), 0, p2pl(280), p2pw(120), p);
		maintainPopNum = new Button(p2pl(1100), 0, p2pl(140), p2pw(120), p);
	}
	
	public void drawMenu(PApplet p)
	{
		switch(path) // side bar path split
		{
			case GENERAL:
			{
				p.colorMode(PConstants.RGB);
				
				generalMenu(Run.displayTime, Run.creatureDeaths, Run.forcedSpawns, Run.superMutations, p);
				
				drawButtons(p);
				cutGraphs();
				drawGenePoolGraph(p);
				drawPopGraph(p);
				
				break;
			}
			case CREATURE:
			{
				p.colorMode(PConstants.RGB);
				p.fill(60, 120);
				p.rect(p2pl(1600), 0, p2pl(1200), p2pw(2000));
				drawButtons(p);
				p.fill(Run.selectedCreature.color.hashCode());
				//ellipse(p2pl(1700), p2pw(320), p2pw(150), p2pw(150)); // draw the creature
				drawSingleCreature(Run.selectedCreature, p);
				p.fill(255);
				p.textSize(p2pl(65));
				p.text("Creature Data", p2pl(1800), p2pw(100));
				p.textSize(p2pl(30));
				int shift = 45;
				p.text("ID: " + Run.selectedCreature.ID, p2pl(1800), p2pw(160));
				p.text("Age: " + Variables.df.format(Run.selectedCreature.fitness), p2pl(1800), p2pw(210));
				p.text("Generation: " + Run.selectedCreature.generation, p2pl(2100), p2pw(160));
				p.text("Parent: " + Run.selectedCreature.parentID, p2pl(2100), p2pw(210));
				p.text("Total Eaten: " + Variables.df2.format(Run.selectedCreature.totalEaten), p2pl(1620), p2pw(320 + 0 * shift));
				p.text("Total Decayed: " + Variables.df2.format(Run.selectedCreature.totalDecayed), p2pl(1620), p2pw(320 + 1 * shift));
				p.text("Location: ( " + Variables.df2.format(Run.selectedCreature.locationX) + ", " + Variables.df2.format(Run.selectedCreature.locationY) + " )", p2pl(1620), p2pw(320 + 2 * shift));
				p.text("Left XY: ( " + Variables.df2.format(Run.selectedCreature.leftSensorX) + ", " + Variables.df2.format(Run.selectedCreature.leftSensorY) + " )", p2pl(1620), p2pw(320 + 3 * shift));
				p.text("Mid XP: ( " + Variables.df2.format(Run.selectedCreature.midSensorX) + ", " + Variables.df2.format(Run.selectedCreature.midSensorY) + " )", p2pl(1620), p2pw(320 + 4 * shift));
				p.text("Right XY: ( " + Variables.df2.format(Run.selectedCreature.rightSensorX) + ", " + Variables.df2.format(Run.selectedCreature.rightSensorY) + " )", p2pl(1620), p2pw(320 + 5 * shift));
				p.text("Mouth XY: ( " + Variables.df2.format(Run.selectedCreature.mouthSensorX) + ", " + Variables.df2.format(Run.selectedCreature.mouthSensorY) + " )", p2pl(1620), p2pw(320 + 6 * shift));
				p.text("Heading: " + Variables.df2.format((Run.selectedCreature.rotation * 180 / Math.PI)), p2pl(1620), p2pw(320 + 7 * shift));
				p.text("Nearest Creature ID: " + Run.selectedCreature.nearestCreature.ID, p2pl(1620), p2pw(320 + 8 * shift));
				p.text("Nearest ID Distance: " + Variables.df.format(Run.selectedCreature.distToNearest), p2pl(1620), p2pw(320 + 9 * shift));
				p.text("Nearest ID Color Diff: " + Run.selectedCreature.colorDifferenceToNearest, p2pl(1620), p2pw(320 + 10 * shift));
				p.text("Creatures within 10 Tiles: " + Run.selectedCreature.numCreaturesWithin10, p2pl(1620), p2pw(320 + 11 * shift));
				p.text("Size Decay", p2pl(2200), p2pw(320 + 0 * shift));
				p.text("Age Decay", p2pl(2200), p2pw(320 + 1 * shift));
				p.text("Eat Decay", p2pl(2200), p2pw(320 + 2 * shift));
				p.text("Turn Decay", p2pl(2200), p2pw(320 + 3 * shift));
				p.text("Fwd Decay", p2pl(2200), p2pw(320 + 4 * shift));
				p.text("Att Decay", p2pl(2200), p2pw(320 + 5 * shift));
				p.text("Eat Rate", p2pl(2200), p2pw(320 + 6 * shift));
				p.fill(95, 260, 220);
				p.text("Current Size: " + (int)Run.selectedCreature.size, p2pl(2200), p2pw(320 + 10 * shift));
				p.fill(0);
				p.fill(255, 0, 0);
				p.text(Variables.df.format(Run.selectedCreature.sizeDecay), p2pl(2400), p2pw(320 + 0 * shift));
				p.text(Variables.df.format(Run.selectedCreature.fitnessDecay), p2pl(2400), p2pw(320 + 1 * shift));
				p.text(Variables.df.format(Run.selectedCreature.eatRateDecay), p2pl(2400), p2pw(320 + 2 * shift));
				p.text(Variables.df.format(Run.selectedCreature.rotationDecay), p2pl(2400), p2pw(320 + 3 * shift));
				p.text(Variables.df.format(Run.selectedCreature.forwardDecay), p2pl(2400), p2pw(320 + 4 * shift));
				p.text(Variables.df.format(Run.selectedCreature.attackDecay), p2pl(2400), p2pw(320 + 5 * shift));
				p.fill(0, 255, 0);
				p.text(Variables.df.format(Run.selectedCreature.eatRate), p2pl(2400), p2pw(320 + 6 * shift));
				if(Run.selectedCreature.energyChange < 0) p.fill(255, 0, 0);
				else p.fill(0, 255, 0);
				p.text("Energy Change: " + Variables.df.format(Run.selectedCreature.energyChange), p2pl(2200), p2pw(320 + 8 * shift));
				p.fill(255);
				drawCreatureBrain(Run.selectedCreature, p);
				
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
				p.rect(p2pl(1600), 0, p2pl(1200), p2pw(2000));
				p.colorMode(PConstants.HSB, 360, 100, 100);
				p.fill(Run.selectedTile.colorH, Run.selectedTile.colorS, Run.selectedTile.colorV);
				p.rect(p2pl(1620), p2pw(140), p2pl(200), p2pl(200)); // draw the tile
				p.colorMode(PConstants.RGB, 255, 255, 255);
				drawButtons(p);
				p.fill(255, 255, 255);
				p.textSize(p2pl(70));
				p.text("Selected Tile Data", p2pl(1620), p2pw(100));
				p.textSize(p2pl(30));
				p.text("Tile #" + Run.selectedTile.tileNumber, p2pl(1630), p2pw(190));
				p.text("Food: " + Variables.df.format(Run.selectedTile.food), p2pl(1630), p2pw(375));
				p.text("Row and Column: (" + (Run.selectedTile.xIndex+1) + ", " + (Run.selectedTile.yIndex+1) + ")", p2pl(1850), p2pw(200));
				p.text("Regeneration Value: " + Math.round( (Run.selectedTile.regenValue * 1000) ) / 1000.0, p2pl(1850), p2pw(240));
				p.text("HSV: " + Run.selectedTile.colorH + ", " + Run.selectedTile.colorS + ", " + Run.selectedTile.colorV, p2pl(1850), p2pw(280));
				p.text("x Range: " + Run.selectedTile.x + " to " + (Run.selectedTile.x + TileManager.tileSize), p2pl(1850), p2pw(320));
				p.text("y Range: " + Run.selectedTile.y + " to " + (Run.selectedTile.y + TileManager.tileSize), p2pl(1850), p2pw(360));
				p.textSize(p2pl(45));
				p.text("Tile Cooldown Status:  " + Run.selectedTile.deadCooldown + " / " + Variables.TILE_COOLDOWN_THRESH, 
						p2pl(1620), p2pw(500));
				
				break;
			}
		}
	}
	
	public void updateHistoryArrays()
	{
		UNIV_POP_HISTORY.add((double) (CreatureManager.creatures.size())); // for save purposes
		popHistory.add((double) (CreatureManager.creatures.size()));
		ArrayList<Integer> allColors = new ArrayList<Integer>();
		List<Integer> allColorsReal = null;
		for(int i = 0; i < CreatureManager.creatures.size(); i++)
		{
			allColors.add(CreatureManager.creatures.get(i).color.hashCode());
		}
		allColorsReal = allColors.subList(0, allColors.size());
		Collections.sort(allColorsReal);
		allColorsReal.add(CreatureManager.creatures.size());
		popPropHistory.add(allColorsReal);
		UNIV_POP_PROP_HISTORY.add(allColorsReal);
	}
	
	public void generalMenu(double time, int creatureDeaths, int forcedSpawns, int superMutations, PApplet p)
	{
		p.colorMode(PConstants.RGB);
		p.fill(60, 120);
		p.rect(p2pl(1600), 0, p2pl(1200), p2pw(2000));
		p.textSize(p2pl(50));
		p.fill(255);
		p.text("World Time: " + Variables.df.format(time), p2pl(1620), p2pw(100));
		p.textSize(p2pl(30));
		p.fill(255);
		p.text("Starting Creatures: " + CreatureManager.startNumCreatures, p2pl(1620), p2pw(160));
		p.fill(0, 255, 0);
		p.text("Successful Births: " + CreatureManager.births, p2pl(1620), p2pw(200));
		p.fill(255, 0, 0);
		p.text("Number of Deaths: " + creatureDeaths, p2pl(1620), p2pw(240));
		p.fill(255);
		String sign;
		if(CreatureManager.startNumCreatures > CreatureManager.creatures.size()) sign = "-";
		else if(CreatureManager.startNumCreatures == CreatureManager.creatures.size()) sign = "";
		else sign = "+";
		if(sign.equals("+")) p.fill(0, 255, 0);
		else if(sign.equals("-")) p.fill(255, 0, 0);
		else p.fill(95, 260, 220);
		p.text("Total Change: " + sign + Math.abs(CreatureManager.creatures.size() - CreatureManager.startNumCreatures), p2pl(1620), p2pw(280));
		p.fill(255);
		p.text("Living Creatures: " + (CreatureManager.creatures.size()), p2pl(1620), p2pw(320));
		p.text("Total Existed: " + CreatureManager.creatureCount, p2pl(1620), p2pw(360));
		p.text("Forced Spawns: " + forcedSpawns, p2pl(1620), p2pw(440));
		p.text("Super Mutations: " + superMutations, p2pl(1620), p2pw(480));
	}
	
	public void drawButtons(PApplet p)
	{
		p.colorMode(PConstants.RGB);
		p.fill(170, 170, 170);
		p.textSize(p2pl(35));
		
		menuButton.draw();
		start.draw();
		p.fill(255, 255, 255);
		p.text("Menu", menuButton.getTextX(), menuButton.getTextY());
		if(Run.play) p.fill(119, 255, 51);
		else p.fill(255, 51, 51);
		if(Run.play) p.text("On", start.getTextX(), start.getTextY());
		else p.text("Off", start.getTextX(), start.getTextY());
		
		p.textSize(p2pl(30));
		p.colorMode(PConstants.HSB);
		p.fill((int)(p.frameRate / 30.0 * 100), 200, 200);
		p.text("FPS: " + Variables.df.format(p.frameRate), start.getX(), start.getY() + 75);
		
		p.colorMode(PConstants.RGB);
		p.textSize(p2pl(35));
		if(Run.showMenu)
		{
			p.fill(60, 120);
			p.rect(0, 0, p2pl(1600), p2pw(360));
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
	}
	
	public void drawPopGraph(PApplet p)
	{
		p.colorMode(PConstants.RGB);
		p.fill(255, 255, 255);
		p.textSize(p2pl(30));
		p.text("Relative population over time", p2pl(1650), p2pw(1330));
		p.rect(p2pl(1650), p2pw(1340), p2pl(900), p2pw(220));
		p.fill(0, 0, 255);
		int width = 900 / popHistory.size();
//		for(int i = 0; i < popHistory.size(); i++)
//		{
//			double ratio = (popHistory.get(i) / maxObservedCreatures * 200);
//			ellipse(p2pl(1650 + (i)*width), p2pw(1350 + (200 - (int)ratio)), p2pw(5), p2pw(5));
//		}
		if(Run.maintain) p.stroke(0, 255, 0);
		else p.stroke(255, 0, 0);
		p.line(p2pl(1650), p2pw(1350 + (200 - (int)(1.0 * Run.maintainNum / Run.maxObservedCreatures * 200))), 
				p2pl(1650 + 900), p2pw(1350 + (200 - (int)(1.0 * Run.maintainNum / Run.maxObservedCreatures * 200))));
		p.stroke(0);
		
		for(int i = 0; i < popHistory.size()-1; i++)
		{
			double ratio = (popHistory.get(i) / Run.maxObservedCreatures * 200);
			double toRatio = (popHistory.get(i+1) / Run.maxObservedCreatures * 200);
			//ellipse(p2pl(1650 + (i)*width), p2pw(1350 + (200 - (int)ratio)), p2pw(5), p2pw(5));
			p.line(p2pl(1650 + (i)*width), p2pw(1350 + (200 - (int)ratio)), p2pl(1650 + (i+1)*width), p2pw(1350 + (200 - (int)toRatio)));
		}
	}
	
	public void drawGenePoolGraph(PApplet p)
	{
		if(!Run.drawGenePoolGraph) return;
		if(popPropHistory.size() == 0) return;
		if(CreatureManager.creatures.size() == 0) return;
		p.colorMode(PConstants.RGB);
		p.fill(255);
		p.textSize(p2pl(30));
		p.text("Current Gene Pool", p2pl(1650), p2pw(990));
		p.text("Gene Pool History", p2pl(1650), p2pw(1060));
		
		double width = 900.0 / (popPropHistory.get(popPropHistory.size()-1).size()-1.0);
		for(int i = 0; i < popPropHistory.get(popPropHistory.size()-1).size()-1; i++)
		{
			p.fill(popPropHistory.get(popPropHistory.size()-1).get(i));
			p.stroke(popPropHistory.get(popPropHistory.size()-1).get(i));
			p.rect(p2pl(1650) + p2pl(i * width), p2pw(1000), p2pl(width), p2pw(20));
		}
		for(int j = 0; j < popPropHistory.size(); j++)
		{
			double propWidth = 900.0 / popPropHistory.size();
			double upCoord = 1075.0;
			for(int i = 0; i < popPropHistory.get(j).size()-1; i++)
			{
				p.fill(popPropHistory.get(j).get(i));
				p.stroke(popPropHistory.get(j).get(i));
				p.rect(p2pl(1650) + p2pl(j * propWidth), p2pw((int)upCoord), p2pl(propWidth), (int)(200.0 / p2pw(popPropHistory.get(j).get(popPropHistory.get(j).size()-1)) + 1));
				upCoord += 220.0 / popPropHistory.get(j).get(popPropHistory.get(j).size()-1);
			}
		}
		p.stroke(0);
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
		p.line(p2pl(1700), p2pw(150), p2pl(1700) + leftSensorXChange, p2pw(150) + leftSensorYChange);
		p.line(p2pl(1700), p2pw(150), p2pl(1700) + rightSensorXChange, p2pw(150) + rightSensorYChange);
		p.line(p2pl(1700), p2pw(150), p2pl(1700) + killerXChange, p2pw(150) + killerYChange);
		p.stroke(0);
		p.colorMode(PConstants.HSB);
		p.fill(c.leftSensorColor, 120, 120);
		p.ellipse(p2pl(1700) + leftSensorXChange, p2pw(150) + leftSensorYChange, p2pw(15) * 1, p2pw(15) * 1);
		p.fill(c.rightSensorColor, 120, 120);
		p.ellipse(p2pl(1700) + rightSensorXChange, p2pw(150) + rightSensorYChange, p2pw(15) * 1, p2pw(15) * 1);
		p.colorMode(PConstants.RGB);
		p.fill(c.color.hashCode());
		p.ellipse(p2pl(1700), p2pw(150), (int)c.diameter * 1, (int)c.diameter * 1);
		p.fill(255);
	}
	
	public void drawCreatureBrain(Creature c, PApplet p) // top left = 1620, 800
	{
		int verticalSpacing = p2pw(640 / CreatureManager.creatures.get(0).brainLength);
		p.textSize(p2pw(50));
		p.text("Inputs", p2pl(1620), p2pw(900));
		p.text("Hidden Layers", p2pl(1940), p2pw(900));
		p.text("Outputs", p2pl(2400), p2pw(900));
		p.colorMode(PConstants.RGB);
		// Left food, center food, mouth food, right food, size, ......
		p.textSize(p2pw(30));
		p.text("L Food", p2pl(1610), p2pw(950) + verticalSpacing * 0);
		p.text("C Food", p2pl(1610), p2pw(950) + verticalSpacing * 1);
		p.text("M Food", p2pl(1610), p2pw(950) + verticalSpacing * 2);
		p.text("R Food", p2pl(1610), p2pw(950) + verticalSpacing * 3);
		p.text("Size", p2pl(1610), p2pw(950) + verticalSpacing * 4);
		p.text("Cr Dist", p2pl(1610), p2pw(950) + verticalSpacing * 5);
		p.text("Co Diff", p2pl(1610), p2pw(950) + verticalSpacing * 6);
		p.text("# in 10", p2pl(1610), p2pw(950) + verticalSpacing * 7);
		p.text("-----", p2pl(1610), p2pw(950) + verticalSpacing * 8);
		p.text("-----", p2pl(1610), p2pw(950) + verticalSpacing * 9);
		p.text("-----", p2pl(1610), p2pw(950) + verticalSpacing * 10);
		p.text("-----", p2pl(1610), p2pw(950) + verticalSpacing * 11);
		// Forward velocity, rotational velocity, eat, attack, give birth, attack length,
		p.text("For Vel", p2pl(2400), p2pw(950) + verticalSpacing * 0);
		p.text("Rot Vel", p2pl(2400), p2pw(950) + verticalSpacing * 1);
		p.text("Eat Rate", p2pl(2400), p2pw(950) + verticalSpacing * 2);
		if(c.outputNeurons[3] >= 0) p.fill(0, 255, 0);
		else p.fill(255, 0, 0);
		p.text("Attack?", p2pl(2400), p2pw(950) + verticalSpacing * 3);
		if(c.outputNeurons[4] >= 0) p.fill(0, 255, 0);
		else p.fill(255, 0, 0);
		p.text("Birth?", p2pl(2400), p2pw(950) + verticalSpacing * 4);
		p.fill(255);
		p.text("Att Size", p2pl(2400), p2pw(950) + verticalSpacing * 5);
		for(int i = 0; i < c.sensorInput.length; i++)
		{
			p.fill(255);
			p.textSize(p2pw(30));
			//rect(p2pl(1620), p2pw(800 + verticalSpacing * i), p2pl(100), p2pw(50));
			p.text(Variables.df.format(c.sensorInput[i]) + "", p2pl(1700), p2pw(950) + verticalSpacing * i);
		}
		for(int i = 0; i < c.hidLayer1.length; i++)
		{
			p.fill(255);
			p.textSize(p2pw(30));
			//rect(p2pl(1620), p2pw(800 + verticalSpacing * i), p2pl(100), p2pw(50));
			p.text(Variables.df.format(c.hidLayer1[i]) + "", p2pl(1935), p2pw(950) + p2pw(40) * i);
		}
		for(int i = 0; i < c.hidLayer2.length; i++)
		{
			p.fill(255);
			p.textSize(p2pw(30));
			//rect(p2pl(1620), p2pw(800 + verticalSpacing * i), p2pl(100), p2pw(50));
			p.text(Variables.df.format(c.hidLayer2[i]) + "", p2pl(2155), p2pw(950) + p2pw(40) * i);
		}
		for(int i = 0; i < c.outputNeurons.length; i++)
		{
			p.fill(255);
			p.textSize(p2pw(30));
			//rect(p2pl(1620), p2pw(800 + verticalSpacing * i), p2pl(100), p2pw(50));
			p.text(Variables.df.format(c.outputNeurons[i]) + "", p2pl(2510), p2pw(950) + verticalSpacing * i);
		}
		
		for(int i = 0; i < c.inputNeurons.length; i++)
		{
			int color = 0;
			for(int one = 0; one < c.hidLayer1.length; one++)
			{
				color = (int) (sigmoid(c.inputToLayer1Axons[i][one].weight + 1.0) * 255);
				if(color < 0) color = 0;
				if(color > 255) color = 255;
				p.stroke(color);
				p.line(p2pl(1780), p2pw(945) + verticalSpacing * i, p2pl(1920), p2pw(940) + p2pw(40) * one);
			}
		}
		for(int i = 0; i < c.hidLayer1.length; i++)
		{
			int color = 0;
			for(int o = 0; o < c.hidLayer2.length; o++)
			{
				color = (int) (sigmoid(c.layer1ToLayer2Axons[i][o].weight + 1.0) * 255);
				if(color < 0) color = 0;
				if(color > 255) color = 255;
				p.stroke(color);
				p.line(p2pl(2020), p2pw(945) + p2pw(40) * i, p2pl(2140), p2pw(940) + p2pw(40) * o);
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
				p.stroke(color);
				p.line(p2pl(2230), p2pw(945) + p2pw(40) * i, p2pl(2380), p2pw(940) + verticalSpacing * o);
			}
		}
		
		p.stroke(0);
	}
	
	public int p2pl(double frac)
	{
		double returnPixels = 0;
		returnPixels = frac / 2600.0 * Run.appWidth;
		return (int) returnPixels;
	}
	public int p2pw(double frac)
	{
		double returnPixels = 0;
		returnPixels = frac / 1600.0 * Run.appHeight;
		return (int) returnPixels;
	}
	public double sigmoid(double x)
	{
		return (2.0 / (1 + Math.pow(Math.E, -(x / 1.5)))) - 1.0;
	}
}
