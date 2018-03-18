package Essentials;

import Network.Network;

public class Creature
{
	int locationX;
	int locationY;
	double size;
	double diameter;
	int ID;
	int totalEaten;
	int totalDecayed;
	int fitness;
	
	public Creature(int startX, int startY, int ID) // no specified size
	{
		size = 200 + (Math.random() - .5) * 50;
		locationX = startX;
		locationY = startY;
		diameter = size/10;
		this.ID = ID;
		totalEaten = 0;
		totalDecayed = 0;
		fitness = 0;
	}
	public Creature(int startX, int startY, int ID, int size) // specified size
	{
		this.size = size;
		locationX = startX;
		locationY = startY;
		diameter = size/10;
		this.ID = ID;
		totalEaten = 0;
		totalDecayed = 0;
		fitness = 0;
	}
	
	public void updateSize()
	{
		diameter = size / 15.0;
	}
	
	public boolean checkReproduce()
	{
		if(size > 300)
		{
			size -= 150;
			return true;
		}
		return false;
	}
	
}