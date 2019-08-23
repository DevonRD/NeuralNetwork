package Essentials;

import processing.core.PApplet;
import java.math.RoundingMode;
import Creature.CreatureManager;
import Utilities.Variables;
import World.TileManager;

public class Manager
{
	PApplet p;
	int appWidth, appHeight;
	
	double timeCopy = 0;
	
	CreatureManager creatureManager;
	TileManager tileManager;
	
	
	public Manager(PApplet p, int startNumCreatures, int width, int height, boolean[][] water, double mutateChance)
	{
		Variables.df.setRoundingMode(RoundingMode.DOWN);
		this.p = p;
		appWidth = width;
		appHeight = height;
		
		tileManager = new TileManager(p, water, width, height);
		creatureManager = new CreatureManager(p, startNumCreatures, mutateChance, width, height);
		
	}
	
	public void iterate(double timeInterval)
	{
		timeCopy += timeInterval;
		
		CreatureManager.iterate(timeInterval);
		TileManager.iterate(timeInterval);
	}
}























