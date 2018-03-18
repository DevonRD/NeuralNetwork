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
	
	public Tile(int x, int y, int tileSize, int number, int xIndex, int yIndex)
	{
		this.x = x;
		this.y = y;
		this.xIndex = xIndex;
		this.yIndex = yIndex;
		this.tileSize = tileSize;
		maxFood = 100;
		food = 50 + (int) (Math.random() * 50);
		colorH = food;
		colorS = 80;
		colorV = 100;
		regenValue = Math.random() * 0.05;
		if (regenValue < 0.01) regenValue = 0.01;
		tileNumber = number;
		
	}
	
	public void testRegen()
	{
		if(Math.random() < regenValue) food++;
		if(food > 100) food = 100;
		colorH = food;
	}
}
