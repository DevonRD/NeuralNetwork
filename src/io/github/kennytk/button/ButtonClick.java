package io.github.kennytk.button;

import io.github.kennytk.IDrawable;
import processing.core.PApplet;
import processing.core.PConstants;

public class ButtonClick extends ButtonBase implements IDrawable
{
	private PApplet p;
	private String text;
	private int counter;
	
	public ButtonClick(PApplet p, int x, int y, int width, int height, String text)
	{
		super(p, x, y, width, height);
		this.p = p;
		this.text = text;
	}
	
	public void draw()
	{
		p.colorMode(PConstants.RGB);
		p.fill(170, 170, 170);
		
		p.textSize(20);
		
		p.rect(getX(), getY(), getWidth(), getHeight());
		
		p.rectMode(PConstants.CORNERS);
		p.textAlign(PConstants.CENTER, PConstants.CENTER);
		
		if(getState())
		{
			p.fill(119, 255, 51); //on color
			p.text(text, getX() + getWidth() / 2, getY() + getHeight() / 2);
			counter++;
		}
		else
		{
			p.fill(255, 51, 51); //off color
			p.text(text, getX() + getWidth() / 2, getY() + getHeight() / 2);
		}
		
		if(counter > 3000)
		{
			counter = 0;
			deactivate();
		}
		
		p.rectMode(PConstants.CORNER);
		p.textAlign(PConstants.LEFT, PConstants.TOP);
	}
}
