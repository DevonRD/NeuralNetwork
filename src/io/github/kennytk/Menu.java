package io.github.kennytk;

import processing.core.PApplet;

public class Menu implements IDrawable
{
	private PApplet p;

	public Menu(PApplet p)
	{
		this.p = p;
	}

	@Override
	public void draw()
	{
		p.fill(255, 255, 255);
		p.stroke(255, 255, 255);
		p.textSize(Maths.scaleX(20));

		p.text("Starting Creatures: " + Statistics.startNumCreatures, Maths.scaleX(45), Maths.scaleY(180)); // increments of 30 are good for menu text

		text("Living Creatures: " + (world.creatures.size()), Maths.scaleX(45), Maths.scaleY(210));

		String sign;

		if(Statistics.startNumCreatures > world.creatures.size())
			sign = "-";
		else
			sign = "+";

		p.text("Total Change: " + sign + Math.abs(world.creatures.size() - Statistics.startNumCreatures), Maths.scaleX(45),
				Maths.scaleY(240));
		p.text("Number of Deaths: " + Statistics.creatureDeaths, Maths.scaleX(45), Maths.scaleY(270));
		p.text("Total Existed Creatures: " + world.creatureCount, Maths.scaleX(45), Maths.scaleY(300));
		p.text("World Time: " + df.format(time), Maths.scaleX(45), Maths.scaleY(330));
		p.text("FPS: " + frameRate, Maths.scaleX(45), Maths.scaleY(360));
		p.text("Successful Births: " + world.births, Maths.scaleX(45), Maths.scaleY(390));
	}
}
