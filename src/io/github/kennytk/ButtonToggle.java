package io.github.kennytk;

import processing.core.PApplet;
import processing.core.PConstants;

public class ButtonToggle extends ButtonBase
{
	private PApplet p;
	private boolean isActive;
	private String activeText, inactiveText;

	public ButtonToggle(PApplet p, int x, int y, int width, int height, String activeText, String inactiveText)
	{
		super(p, x, y, width, height);
		this.p = p;
		this.activeText = activeText;
		this.inactiveText = inactiveText;
	}
	
	public void draw()
	{
		p.colorMode(PConstants.RGB);
		p.fill(170, 170, 170);
		
		if(getState());
		
		p.rect(getX(), getY(), getWidth(), getHeight());
		
		if(getState())
		{
			p.fill(119, 255, 51); //on color
			p.text(activeText, getX(), getY() + getHeight() / 2);
		}
		else
		{
			p.fill(255, 51, 51); //off color
			p.text(inactiveText, getX(), getY() + getHeight() / 2);
		}
	}
	
	public void activate()
	{
		isActive = true;
	}
	
	public void deactivate()
	{
		isActive = false;
	}
	
	public void toggle()
	{
		isActive = !isActive;
	}

	public boolean getState()
	{
		return isActive;
	}	
}
