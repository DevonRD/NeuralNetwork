package io.github.kennytk.button;

import io.github.kennytk.IDrawable;
import io.github.kennytk.numbers.Globals;
import processing.core.PApplet;
import processing.core.PConstants;

public class ButtonClickSmall extends ButtonBase implements IDrawable
{
	private PApplet p;
	private int counter;
	private int timeOut;
	
	public ButtonClickSmall(PApplet p, int x, int y, int width, int height, int timeOut)
	{
		super(p, x, y, width, height);
		
		this.p = p;
		this.timeOut = timeOut;
	}

	public void draw()
	{
		p.strokeWeight(3);
		
		p.colorMode(PConstants.RGB);
		
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
