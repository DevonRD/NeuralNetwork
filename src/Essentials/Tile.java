package Essentials;

import Network.Network;

public class Tile
{
	int x;
	int y;
	int xIndex;
	int yIndex;
	int tileSize;
	int maxFood;
	int food;
	int colorH;
	int colorS;
	int colorV;
	double regenValue;
	int tileNumber;
	boolean water;
	
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
		if(!water) food = 50 + (int) (Math.random() * 50);
		if(water) colorH = 155;
		else colorH = food;
		colorS = 100;
		colorV = 100;
		if(water) regenValue = 0;
		else regenValue = Math.random() * 0.05;
		if (regenValue < 0.01 && !water) regenValue = 0.01;
		tileNumber = number;
		
	}
	
	public void testRegen()
	{
		if(water) return;
		if(Math.random() < regenValue) food++;
		if(food > 100) food = 100;
		colorH = food;
	}
}
