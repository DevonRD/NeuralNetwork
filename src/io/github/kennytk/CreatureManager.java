package io.github.kennytk;

import processing.core.PApplet;

public class CreatureManager
{
	private PApplet p;
	
	public CreatureManager(PApplet p)
	{
		this.p = p;
	}
	
	public void draw()
	{
		p.pushStyle();

		p.colorMode(p.RGB);

		p.fill(60, 120);

		p.rect(Maths.scaleX(1600), 0, Maths.scaleX(1200), Maths.scaleY(2000));

		p.fill(255, 255, 255);

		p.ellipse(Maths.scaleX(1700), Maths.scaleY(320), Maths.scaleY(150), Maths.scaleY(150)); // draw the creature
		p.textSize(Maths.scaleX(70));

		p.text("Selected Creature Data", Maths.scaleX(1620), Maths.scaleY(190));
		p.textSize(Maths.scaleX(30));
		p.text("ID: " + selectedCreature.ID, Maths.scaleX(1620), Maths.scaleY(500));
		p.text("Current Size: " + (int) selectedCreature.size, Maths.scaleX(1620), Maths.scaleY(530));
		p.text("Total Eaten: " + df.format(selectedCreature.totalEaten), Maths.scaleX(1620), Maths.scaleY(560));
		p.text("Total Decayed: " + df.format(selectedCreature.totalDecayed), Maths.scaleX(1620), Maths.scaleY(590));
		p.text("Location: (" + df.format(selectedCreature.locationX) + ", " + df.format(selectedCreature.locationY) + " )",
				Maths.scaleX(1620), Maths.scaleY(620));
		p.text("Left Sensor: (" + df.format(selectedCreature.leftSensorX) + ", " + df.format(selectedCreature.leftSensorY) + " )",
				Maths.scaleX(1620), Maths.scaleY(650));
		p.text("Mid Sensor: (" + df.format(selectedCreature.midSensorX) + ", " + df.format(selectedCreature.midSensorY) + " )",
				Maths.scaleX(1620), Maths.scaleY(680));
		p.text("Right Sensor: (" + df.format(selectedCreature.rightSensorX) + ", " + df.format(selectedCreature.rightSensorY) + " )",
				Maths.scaleX(1620), Maths.scaleY(710));
		p.text("Mouth Sensor: (" + df.format(selectedCreature.mouthSensorX) + ", " + df.format(selectedCreature.mouthSensorY) + " )",
				Maths.scaleX(1620), Maths.scaleY(740));
		p.text("Food Under Me: "
				+ df.format(world.findTileAt(selectedCreature.mouthSensorX, selectedCreature.mouthSensorY, true).food),
				Maths.scaleX(1620), Maths.scaleY(770));
		p.text("Heading: " + df.format((selectedCreature.rotation * 180 / Math.PI)), Maths.scaleX(1620), Maths.scaleY(800));
		p.text("Generation: " + selectedCreature.generation, Maths.scaleX(1620), Maths.scaleY(830));
		p.drawCreatureBrain(selectedCreature);

		p.popStyle();
	}
}
