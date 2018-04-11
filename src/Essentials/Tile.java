package Essentials;

public class Tile
{
	int x;
	int y;
	int xIndex;
	int yIndex;
	int tileSize;
	int maxFood;
	double food;
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
		if(!water) food = 60 + (int) (Math.random() * 40);
		if(water) colorH = 155;
		else colorH = (int) food;
		colorS = 120;
		colorV = 120;
		if(water) regenValue = 0;
		//else regenValue = Math.random() * 0.1;
		else regenValue = 0.025;
		//if (regenValue < 0.04 && !water) regenValue = 0.04;
		tileNumber = number;
		
	}
	
	public void regen()
	{
		if(water) food = 0;
		if(water) return;
		if(food >= maxFood) return;
		food += regenValue;
		colorH = (int) (food / 1.5 + 30);
	}
}
