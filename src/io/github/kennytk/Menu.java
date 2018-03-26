package io.github.kennytk;

import io.github.kennytk.numbers.Globals.MenuMode;
import io.github.kennytk.numbers.Maths;
import io.github.kennytk.numbers.Statistics;
import processing.core.PApplet;

public class Menu implements IDrawable
{
	private PApplet p;
	private static MenuMode menuMode;
	
	private double time, fps;

	public Menu(PApplet p)
	{
		this.p = p;
		time = 0;
		fps = 0;
	}

	@Override
	public void draw()
	{
		p.fill(255, 255, 255);
		p.stroke(255, 255, 255);
		p.textSize(Maths.scaleX(20));

		p.text("Starting Creatures: " + Statistics.startNumCreatures, Maths.scaleX(45), Maths.scaleY(180)); // increments of 30 are good for menu text

		p.text("Living Creatures: " + (Statistics.creatureCount), Maths.scaleX(45), Maths.scaleY(210));

		String sign;

		if(Statistics.startNumCreatures > Statistics.creatureCount)
			sign = "-";
		else
			sign = "+";

		p.text("Total Change: " + sign + Math.abs(Statistics.creatureCount - Statistics.startNumCreatures), Maths.scaleX(45),
				Maths.scaleY(240));
		p.text("Number of Deaths: " + Statistics.creatureDeaths, Maths.scaleX(45), Maths.scaleY(270));
		p.text("Total Existed Creatures: " + Statistics.creatureCount, Maths.scaleX(45), Maths.scaleY(300));

		p.text("World Time: " + Maths.decimalFormat(time), Maths.scaleX(45), Maths.scaleY(330)); // df.format(time)

		p.text("FPS: " + Maths.decimalFormat(fps), Maths.scaleX(45), Maths.scaleY(360));// frameRate

		p.text("Successful Births: " + Statistics.creatureBirths, Maths.scaleX(45), Maths.scaleY(390));

		// text("Code Iterations: " + rawTime, p2pl(650), p2pw(35));
		// text("Framerate: " + (int)frameRate, p2pl(50), p2pw(35));
		// text("Global Time: " + df.format(time), p2pl(300), p2pw(35));
	}
	
	public static void setMenuMode(MenuMode menuMode)
	{
		Menu.menuMode = menuMode;
	}
	
	public MenuMode getMenuMode()
	{
		return menuMode;
	}
	
	public void setTime(double time)
	{
		this.time = time;
	}
	
	public void setFPS(double fps)
	{
		this.fps = fps;
	}
}
