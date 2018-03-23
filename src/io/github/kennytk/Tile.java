package io.github.kennytk;

import processing.core.PApplet;
import processing.core.PConstants;

public class Tile implements IDrawable
{
	// TODO: add dormant boolean
	private PApplet p;

	private int x;
	private int y;
	private int xIndex;
	private int yIndex;
	private int tileSize;
	private int maxFood;
	private double food;
	private int colorH;
	private int colorS;
	private int colorV;
	private double regenValue;
	private int tileNumber;
	private boolean water;

	public Tile(PApplet p, int x, int y, int tileSize, int number, int xIndex, int yIndex, boolean w)
	{
		this.p = p;
		this.water = w;
		this.x = x;
		this.y = y;
		this.xIndex = xIndex;
		this.yIndex = yIndex;
		this.tileSize = tileSize;
		if(water)
			maxFood = 0;
		else
			maxFood = 100;
		if(!water)
			food = 50 + (int) (Math.random() * 50);
		if(water)
			colorH = 155;
		else
			colorH = (int) food;
		colorS = 100;
		colorV = 100;
		if(water)
			regenValue = 0;
		else
			regenValue = Math.random() * 0.01;
		if(regenValue < 0.001 && !water)
			regenValue = 0.001;
		tileNumber = number;
	}

	public void draw()
	{
		p.pushStyle();

		p.colorMode(PConstants.HSB);
		p.fill(selectedTile.colorH, selectedTile.colorS, selectedTile.colorV);
		p.rect(Maths.scaleX(1620), Maths.scaleY(220), Maths.scaleX(200), Maths.scaleY(200)); // draw the tile
		p.colorMode(PConstants.RGB);
		p.drawButtons();
		p.fill(255, 255, 255);
		p.textSize(Maths.scaleX(70));
		p.text("Selected Tile Data", Maths.scaleX(1620), Maths.scaleY(190));
		p.textSize(Maths.scaleX(30));
		p.text(" # " + selectedTile.tileNumber, Maths.scaleX(1620), Maths.scaleY(250));
		p.text(" Food: " + df.format(selectedTile.food), Maths.scaleX(1620), Maths.scaleY(400));
		p.text("Row and Column: (" + (selectedTile.xIndex + 1) + ", " + (selectedTile.yIndex + 1) + ")", Maths.scaleX(1830),
				Maths.scaleY(250));
		p.text("Regeneration Value: " + Math.round((selectedTile.regenValue * 1000)) / 1000.0, Maths.scaleX(1830), Maths.scaleY(290));
		p.text("HSV: " + selectedTile.colorH + ", " + selectedTile.colorS + ", " + selectedTile.colorV, Maths.scaleX(1830),
				Maths.scaleY(330));
		p.text("x Range: " + selectedTile.x + " to " + (selectedTile.x + world.tileSize), Maths.scaleX(1830), Maths.scaleY(370));
		p.text("y Range: " + selectedTile.y + " to " + (selectedTile.y + world.tileSize), Maths.scaleX(1830), Maths.scaleY(410));

		p.popStyle();
	}

	public void regen()
	{
		if(water)
			return;
		food += regenValue;
		if(food > maxFood)
			food = maxFood;
		colorH = (int) food;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getH()
	{
		return colorH;
	}
	
	public int getS()
	{
		return colorS;
	}
	
	public int getV()
	{
		return colorV;
	}
	
}
