package World;

import Utilities.Variables;

public class Tile
{
	public int x;
	public int y;
	public int xIndex;
	public int yIndex;
	int tileSize;
	int maxFood;
	public double food;
	public int colorH;
	public int colorS;
	public int colorV;
	public double regenValue;
	public int tileNumber;
	boolean water;
	public int deadCooldown, cooldownThreshold;
	
	public Tile(int x, int y, int tileSize, int number, int xIndex, int yIndex, boolean w)
	{
		this.water = w;
		this.x = x;
		this.y = y;
		this.xIndex = xIndex;
		this.yIndex = yIndex;
		this.tileSize = tileSize;
		if(water) maxFood = 0;
		else maxFood = 100;
		if(!water) food = 60 + (int) (Math.random() * 40);
		if(water) colorH = 220;
		else colorH = (int)(food * 0.7 + 50);
		colorS = 80;
		colorV = 45;
		if(water) regenValue = 0;
		else regenValue = 0.025;
		tileNumber = number;
		cooldownThreshold = Variables.TILE_COOLDOWN_THRESH;
	}
	
	public void regenerateTileFood()
	{
		if(water) food = 0;
		if(water) return;
		
		colorH = (int) (food * 0.7 + 50);
		
		deadCooldown++;
		if(deadCooldown < cooldownThreshold) return;
		else deadCooldown = cooldownThreshold;
		
		if(food >= maxFood) return;
		food += regenValue;
		
	}
	
	public void resetCooldown()
	{
		deadCooldown = 0;
	}
}
