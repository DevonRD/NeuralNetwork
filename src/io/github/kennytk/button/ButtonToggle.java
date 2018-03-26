package io.github.kennytk.button;

import io.github.kennytk.IDrawable;
import processing.core.PApplet;
import processing.core.PConstants;

public class ButtonToggle extends ButtonBase implements IDrawable
{
	private PApplet p;
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

		p.textSize(20);

		p.fill(170, 170, 170);

		if(getState())
			;

		p.rectMode(PConstants.CORNER);

		p.rect(getX(), getY(), getWidth(), getHeight());

		p.rectMode(PConstants.CENTER);
		p.textAlign(PConstants.CENTER, PConstants.CENTER);

		if(getState())
		{
			p.fill(119, 255, 51); // on color
			p.text(activeText, getX() + getWidth() / 2, getY() + getHeight() / 2);
		}
		else
		{
			p.fill(255, 51, 51); // off color
			p.text(inactiveText, getX() + getWidth() / 2, getY() + getHeight() / 2);
		}

		p.rectMode(PConstants.CORNER);
		p.textAlign(PConstants.LEFT, PConstants.TOP);
	}

	public void toggle()
	{
		if(getState())
			deactivate();
		else
			activate();
	}
	
	/*
	 * 
	 * 
		// if(play)
		// fill(119, 255, 51);
		// else
		// fill(255, 51, 51);
		//
		// if(play)
		// text("On", start.getX() + p2pl(20), start.getY() + p2pw(60)); // +150 for next over
		// else
		// text("Off", start.getX() + p2pl(20), start.getY() + p2pw(60));

		// fill(255, 255, 255);

		// text("Kill All", killAll.getX() + p2pl(20), killAll.getY() + p2pw(60));

		// if(spawnMode)
		// fill(119, 255, 51);
		// else
		// fill(255, 51, 51);

		// text("Spawn", spawn.getX() + p2pl(20), spawn.getY() + p2pw(60));
		// fill(255, 255, 255);
		// text("Spawn 20", spawn20.getX() + p2pl(20), spawn20.getY() + p2pw(60));
	 * 
	 */
}
