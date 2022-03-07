package Utilities;

import processing.core.PApplet;
import processing.core.PConstants;

public class Button
{
	private int x;
	private int y;
	int width;
	int height;
	int buffer = 10;
	private int textX;
	private int textY;
	PApplet p; // 1/5 2/3 text
	
	public Button(int x, int y, int width, int height, PApplet p)
	{
		this.setX(x + buffer);
		this.setY(y + buffer);
		this.width = width - 2 * buffer;
		this.height = height - 2 * buffer;
		this.p = p;
		setTextX(buffer + this.getX() + Prefs.wPix(20));
		setTextY((int)(buffer + this.getY() + this.height / 2.0));
	}
	public void draw()
	{
		p.colorMode(PConstants.RGB);
		p.fill(170, 170, 170);
		p.textSize(Prefs.wPix(35));
		p.rect(this.getX(), this.getY(), this.width, this.height);
	}

	public boolean clicked(int x, int y)
	{
		if(this.getX() < x && x <= this.getX() + this.width)
		{
			if(this.getY() < y && y <= this.getY() + this.height)
			{
				return true;
			}
		}
		return false;
	}
	public int getTextX()
	{
		return textX;
	}
	public void setTextX(int textX)
	{
		this.textX = textX;
	}
	public int getTextY()
	{
		return textY;
	}
	public void setTextY(int textY)
	{
		this.textY = textY;
	}
	public int getX()
	{
		return x;
	}
	public void setX(int x)
	{
		this.x = x;
	}
	public int getY()
	{
		return y;
	}
	public void setY(int y)
	{
		this.y = y;
	}
}