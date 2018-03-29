package io.github.kennytk.button;

import io.github.kennytk.IDrawable;
import io.github.kennytk.numbers.Globals;
import processing.core.PApplet;
import processing.core.PConstants;

public class ButtonClick extends ButtonBase implements IDrawable
{
	private PApplet p;
	private String text;
	private int counter;
	private int timeOut;

	public ButtonClick(PApplet p, int x, int y, int width, int height, int timeOut, String text)
	{
		super(p, x, y, width, height);
		this.p = p;
		this.text = text;
		this.timeOut = timeOut;
	}

	public void draw()
	{
		p.strokeWeight(2);
		
		p.colorMode(PConstants.RGB);

		p.textSize(Globals.buttonTextSize);
		
		if(getState())
		{
			p.stroke(242, 38, 19); // red
			p.fill(128, 142, 157); // darker grey
		}
		else
		{
			p.stroke(100, 255, 100); // green
			p.fill(171, 183, 183); // dark grey
		}

		p.rectMode(PConstants.CORNER);

		p.rect(getX(), getY(), getWidth(), getHeight(), Globals.buttonFillet);

		p.rectMode(PConstants.CENTER);

		p.textAlign(PConstants.CENTER, PConstants.CENTER);

		if(getState())
		{
			p.fill(242, 38, 19); // red
			p.text(text, getX() + getWidth() / 2, getY() + getHeight() / 2);
		}
		else
		{
			p.fill(100, 255, 100); // green
			p.text(text, getX() + getWidth() / 2, getY() + getHeight() / 2);
		}

		counter++;
		
		if(counter > timeOut)
		{
			counter = 0;
			deactivate();
		}

		p.rectMode(PConstants.CORNER);
		p.textAlign(PConstants.LEFT, PConstants.TOP);
	}
	
	public void start()
	{
		counter = 0;
		activate();
	}
}
