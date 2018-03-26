package io.github.kennytk.button;

import io.github.kennytk.numbers.Globals;
import io.github.kennytk.numbers.Maths;
import processing.core.PApplet;

public class ButtonBase
{
	private PApplet p;
	
	private int x;
	private int y;
	private int width;
	private int height;
	
	private boolean isActive;
	
	public ButtonBase(PApplet p, int x, int y, int width, int height)
	{
		this.p = p;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
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
	
	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}
	
	public void activate()
	{
		isActive = true;
	}
	
	public void deactivate()
	{
		isActive = false;
	}

	public boolean getState()
	{
		return isActive;
	}
	
	public boolean isClicked(int mX, int mY)
	{
		//remember we translate
		
		if(Maths.scaleX(Globals.menuBasePointX) + x < mX && mX <= Maths.scaleX(Globals.menuBasePointX) + x + width) // start button
		{
			if(y < mY && mY <= y + height)
			{
				return true;
			}
		}
		return false;
	}
}
