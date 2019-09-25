package Essentials;

import processing.core.PApplet;
import java.math.RoundingMode;
import Creature.CreatureManager;
import Utilities.Preferences;
import World.TileManager;

public class Manager
{
	int appWidth = Run.appWidth;
	int appHeight = Run.appHeight;
			
	public Manager()
	{
		Preferences.formatDecimal.setRoundingMode(RoundingMode.DOWN);
		
		TileManager.startTiles();
		CreatureManager.startCreatures();
	}
	
	public void drawWorld(PApplet p)
	{
		TileManager.drawTiles(p);
		CreatureManager.drawCreatures(p);
	}
	
	public void iterate(double timeInterval)
	{		
		CreatureManager.iterate(timeInterval);
		TileManager.iterate(timeInterval);
		
		if(Run.maintain) CreatureManager.maintain();
	}
}