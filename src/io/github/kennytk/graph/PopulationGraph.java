package io.github.kennytk.graph;

import io.github.kennytk.IDrawable;
import io.github.kennytk.numbers.Globals;
import io.github.kennytk.numbers.Maths;
import io.github.kennytk.numbers.Statistics;
import processing.core.PApplet;
import processing.core.PConstants;

public class PopulationGraph implements IDrawable
{
	private PApplet p;

	public PopulationGraph(PApplet p)
	{
		this.p = p;
		Statistics.popHistory.add((double) Statistics.startNumCreatures);
	}

	public void draw()
	{
		p.textSize(Globals.menuTextSize);

		p.stroke(0);

		p.textAlign(PConstants.TOP, PConstants.LEFT);
		p.text("Relative population over time", Maths.scaleX(45), Maths.scaleY(800));

		p.stroke(100, 100, 255);
		p.fill(200, 200, 255);

		p.rectMode(PConstants.CORNERS);
		p.rect(Maths.scaleX(45), Maths.scaleY(810), Maths.scaleX(675), Maths.scaleY(1020));

		p.rectMode(PConstants.CORNER);

		p.fill(207, 0, 15);
		p.stroke(207, 0, 15);

		int width = 630 / Statistics.popHistory.size();

		for(int i = 0; i < Statistics.popHistory.size(); i++)
		{
			double ratio = (Statistics.popHistory.get(i) / Statistics.maxObservedCreatures * 200);

			p.ellipseMode(PConstants.CENTER);

			p.ellipse(Maths.scaleX(50 + (i) * width), Maths.scaleY(815 + (202 - (int) ratio)), Maths.scaleY(5), Maths.scaleY(5));

			p.ellipseMode(PConstants.CORNER);
		}
	}
}
