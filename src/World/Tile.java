package World;

import Utilities.Prefs;

public class Tile
{
	public int x, y, xIndex, yIndex, tileSize, maxFood, tileNumber, deadCooldown, cooldownThreshold,
		colorH, colorS, colorV;

	public double food, regenValue;
	
	boolean water;
	
	
	public Tile(int x, int y, int tileSize, int tileNumber, int xIndex, int yIndex, boolean water)
	{
		this.water = water;
		this.x = x;
		this.y = y;
		this.xIndex = xIndex;
		this.yIndex = yIndex;
		this.tileNumber = tileNumber;
		this.tileSize = tileSize;
		
		if(water) maxFood = 0;
		else maxFood = 100;
		if(!water) food = 60 + (int) (Math.random() * 40);
		if(water) colorH = 220;
		else colorH = (int)(food * 0.7 + 50);
		colorS = 80;
		colorV = 45;
		
		if(water) regenValue = 0;
		else regenValue = Prefs.TILE_REGEN_RATE;
		cooldownThreshold = Prefs.TILE_COOLDOWN_THRESH;
	}
	
	public void regenerateTileFood()
	{
		if(water) food = 0;
		if(water) return;
		
		colorH = (int) (food * 0.7 + 50);
		
		deadCooldown++;
		if(deadCooldown < cooldownThreshold) return;
		else deadCooldown = cooldownThreshold;
		
		if(food >= maxFood)
		{
			food = maxFood;
			return;
		}
		food += regenValue;
	}
	
	public void resetCooldown()
	{
		deadCooldown = 0;
	}
}
