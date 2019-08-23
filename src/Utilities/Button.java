package Utilities;

import Essentials.Run;
import processing.core.PApplet;

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
		setTextX(buffer + this.getX() + p2pl(20));
		setTextY((int)(buffer + this.getY() + this.height / 2.0));
	}
	public void draw()
	{
		p.rect(this.getX(), this.getY(), this.width, this.height);
	}
	public int p2pl(double frac)
	{
		double returnPixels = 0;
		returnPixels = frac / 2600.0 * Run.appWidth;
		return (int) returnPixels;
	}
	public int p2pw(double frac)
	{
		double returnPixels = 0;
		returnPixels = frac / 1600.0 * Run.appHeight;
		return (int) returnPixels;
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