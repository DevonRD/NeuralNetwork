package io.github.kennytk.button;

import io.github.kennytk.IDrawable;
import io.github.kennytk.numbers.Maths;
import processing.core.PApplet;
import processing.core.PConstants;

public class ButtonRightTriangle extends ButtonClickSmall implements IDrawable
{
	private PApplet p;

	public ButtonRightTriangle(PApplet p, int x, int y, int width, int height, int timeOut)
	{
		super(p, x, y, width, height, timeOut);
		this.p = p;
	}

	private int buffer = 20;
	
	public void draw()
	{
		super.draw();
		
		p.strokeCap(PConstants.ROUND);
		
		p.fill(100);
		
		p.beginShape(PConstants.TRIANGLE);
		p.vertex(getX() + Maths.scaleX(buffer), getY() + getHeight() - Maths.scaleY(buffer));
		p.vertex(getX() + Maths.scaleX(buffer), getY() + Maths.scaleY(buffer));
		p.vertex(getX() + getWidth() - Maths.scaleX(10), getY() + (getHeight() / 2));
		p.endShape(PConstants.CLOSE);

		//p.triangle(getX() + Maths.scaleX(buffer), getY() + getHeight() - Maths.scaleY(buffer), getX() + Maths.scaleX(buffer), getY() + Maths.scaleY(buffer),
		//		getX() + getWidth() - Maths.scaleX(10), getY() + (getHeight() / 2));
	}

}
