package io.github.kennytk;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;

public class Creature implements IDrawable
{
	private PApplet p;

	private double x, y;

	private double size;
	private double diameter;

	private int ID;

	private double totalEaten, totalDecayed;
	private double fitness;

	private double birthDate;
	private double age;

	private double forwardVel;
	private double rotation;
	private double rotationVel;

	private int sensorLength;
	private int killerLength;

	private double[] defaultSensorValues;
	private final int numInputs = 8;
	private final double ANGLE_CONSTANT = Math.PI / 6.0;

	private double eatRate, decayRate, energyChange;
	private boolean attack;

	private int generation;
	private double mutationFactor;

	private int births = 0;

	private ArrayList<Axon[][]> brain;

	private double leftSensorX, leftSensorY;
	private double midSensorX, midSensorY;
	private double rightSensorX, rightSensorY;
	private double mouthSensorX, mouthSensorY;

	private double[] inputNeurons;
	private double[] hidLayer1;
	private double[] hidLayer2;
	private double[] outputNeurons;

	private Axon[][] inputToLayer1Axons;
	private Axon[][] layer1ToLayer2Axons;
	private Axon[][] layer2ToOutputAxons;
	// 30 degrees both ways pi/6

	public Creature(PApplet p, double x, double y, int ID, int generation) // no specified size
	{
		this.p = p;

		size = 150 + p.random(-25, 25);

		diameter = size / 10.0;

		sensorLength = (int) (60 + diameter);
		killerLength = (int) (30 + diameter);

		commonConstructor(p, x, y, ID, generation, Globals.mutationFactor);
	}

	public Creature(PApplet p, double x, double y, int ID, int size, int generation, double mutationFactor) // specified size
	{
		this.size = size;

		diameter = size / 10.0;

		sensorLength = (int) (60 + diameter);
		killerLength = (int) (30 + diameter);

		commonConstructor(p, x, y, ID, generation, mutationFactor);
	}

	public Creature(PApplet p, double x, double y, int ID, int size, int generation, double mutationFactor, ArrayList<Axon[][]> brain) // specified size
	{
		this.size = size;
		diameter = size / 10.0;

		sensorLength = (int) (60 + diameter);
		killerLength = (int) (30 + diameter);

		this.brain = brain;

		commonConstructor(p, x, y, ID, generation, mutationFactor);
	}

	private void commonConstructor(PApplet p, double x, double y, int ID, int generation, double mutationFactor)
	{
		this.p = p;

		this.x = x;
		this.y = y;

		this.ID = ID;
		this.generation = generation;
		this.mutationFactor = mutationFactor;

		totalEaten = 0;
		totalDecayed = 0;
		fitness = 0;
		rotation = 0;

		updateSensorCoords();
		brainInit();
	}

	public void draw()
	{
		p.stroke(0);

		p.line((int) x, (int) y, (int) leftSensorX, (int) leftSensorY);

		p.line((int) x, (int) y, (int) midSensorX, (int) midSensorY);

		p.stroke(255);

		p.line((int) x, (int) y, (int) rightSensorX, (int) rightSensorY);

		p.stroke(0);

		// if(selectedCreature != null && world.creatures.get(i).ID == selectedID)
		// fill(240, 0, 255);
		// ellipse((int) world.creatures.get(i).x, (int) world.creatures.get(i).y, (int) world.creatures.get(i).diameter,
		// (int) world.creatures.get(i).diameter);

		p.fill(255);

		p.ellipse((int) leftSensorX, (int) leftSensorY, 7, 7);
		p.ellipse((int) rightSensorX, (int) rightSensorY, 7, 7);
		p.ellipse((int) mouthSensorX, (int) mouthSensorY, 7, 7);
	}

	public void update(double timeInterval)
	{
		Point2D leftTile, midTile, rightTile, mouthTile;

		// fix
		setFitness(getFitness() + timeInterval);

		double[] sensorInput = new double[numInputs];

		updateSensorCoords();

		leftTile = getTilePoint(leftSensorX, leftSensorY);

		midTile = getTilePoint(midSensorX, midSensorY);

		rightTile = getTilePoint(rightSensorX, rightSensorY);
		mouthTile = getTilePoint(mouthSensorX, mouthSensorY);

		// Left food, left creature, center food, center creature, right food, right creature,
		// mouth food, energy change rate,

		// all of these casted doubles should just be ints by default
		// (inspect the methods internal cast)

		sensorInput[0] = TileManager.getTileFromPixels(leftTile.getX(), leftTile.getY()).getFood() / 100.0;

		if(CreatureManager.isCreatureAt(leftSensorX, leftSensorY))
			sensorInput[1] = 1.0;
		else
			sensorInput[1] = -1.0;
		sensorInput[2] = TileManager.getTileFromPixels(midTile.getX(), midTile.getY()).getFood() / 100.0;

		if(CreatureManager.isCreatureAt(midSensorX, midSensorY))
			sensorInput[3] = 1.0;
		else
			sensorInput[3] = -1.0;
		sensorInput[4] = TileManager.getTileFromPixels(rightTile.getX(), rightTile.getY()).getFood() / 100.0;
		if(CreatureManager.isCreatureAt(rightSensorX, rightSensorY))
			sensorInput[5] = 1.0;
		else
			sensorInput[5] = -1.0;

		sensorInput[6] = TileManager.getTileFromPixels(mouthTile.getX(), mouthTile.getY()).getFood() / 100.0;
		sensorInput[7] = size / 300.0;

		iterate(sensorInput, timeInterval);

		double eatRequest = requestEat(timeInterval);

		Tile foodTile = TileManager.getTileFromPixels(mouthSensorX, mouthSensorY);

		System.out.println(" mouth X: " + mouthSensorX);
		System.out.println(" mouth Y: " + mouthSensorY);

		allowEat(TileManager.requestEat(foodTile.getXIndex(), foodTile.getYIndex(), eatRequest));

		if(requestBirth())
		{
			// System.out.println(i + " request birth");
			if(size < 300)
			{
				// size = 10; // this kills them if they try to birth but don't have enough mass
				// System.out.println("birth failed at " + timeCopy);
			}
			else
			{
				// System.out.println("birth successful at " + timeCopy);
				// births++;
				ArrayList<Axon[][]> creatureBrain = giveBirth();
				CreatureManager.addCreature(
						new Creature(p, x, y, Statistics.creatureCount, 300, (generation + 1), Globals.mutationFactor, creatureBrain));
			}
		}
	}

	public void updateSensorCoords()
	{
		rotation %= (2 * Math.PI);
		double leftSensorRotation = rotation - ANGLE_CONSTANT;
		double rightSensorRotation = rotation + ANGLE_CONSTANT;
		while(leftSensorRotation < 0)
			leftSensorRotation += 2 * Math.PI;
		while(rightSensorRotation < 0)
			rightSensorRotation += 2 * Math.PI;
		leftSensorRotation %= (2 * Math.PI);
		rightSensorRotation %= (2 * Math.PI);
		// System.out.println("left: " + leftSensorRotation + " mid: " + rotation + " right: " + rightSensorRotation);
		double leftSensorTempAngle = -leftSensorRotation;
		double rightSensorTempAngle = -rightSensorRotation;
		double midSensorTempAngle = -rotation;

		// left sensor, mid sensor, right sensor, mouth
		leftSensorX = x + (sensorLength * Math.cos(leftSensorTempAngle));
		leftSensorY = y + (sensorLength * Math.sin(leftSensorTempAngle));
		midSensorX = x + (killerLength * Math.cos(midSensorTempAngle));
		midSensorY = y + (killerLength * Math.sin(midSensorTempAngle));
		rightSensorX = x + (sensorLength * Math.cos(rightSensorTempAngle));
		rightSensorY = y + (sensorLength * Math.sin(rightSensorTempAngle));
		mouthSensorX = x + (diameter / 2.0 * Math.cos(midSensorTempAngle));
		mouthSensorY = y + (diameter / 2.0 * Math.sin(midSensorTempAngle));
	}

	public void brainInit()
	{
		defaultSensorValues = new double[numInputs];
		// Left food, left creature, right food, right creature, center food, center creature, mouth food,
		// energy change rate,
		inputNeurons = new double[numInputs];
		hidLayer1 = new double[16];
		hidLayer2 = new double[16];
		// Forward velocity, rotational velocity, eat, attack, give birth, attack length,
		outputNeurons = new double[6];
		if(brain == null)
		{
			inputToLayer1Axons = new Axon[inputNeurons.length][hidLayer1.length];
			layer1ToLayer2Axons = new Axon[hidLayer1.length][hidLayer2.length];
			layer2ToOutputAxons = new Axon[hidLayer2.length][outputNeurons.length];
			// initialize the random starting axons
			for(int lay1 = 0; lay1 < inputNeurons.length; lay1++)
			{
				for(int lay2 = 0; lay2 < hidLayer1.length; lay2++)
				{
					inputToLayer1Axons[lay1][lay2] = new Axon();
				}
			}
			for(int lay1 = 0; lay1 < hidLayer1.length; lay1++)
			{
				for(int lay2 = 0; lay2 < hidLayer2.length; lay2++)
				{
					layer1ToLayer2Axons[lay1][lay2] = new Axon();
				}
			}
			for(int lay1 = 0; lay1 < hidLayer2.length; lay1++)
			{
				for(int lay2 = 0; lay2 < outputNeurons.length; lay2++)
				{
					layer2ToOutputAxons[lay1][lay2] = new Axon();
				}
			}
		}
		else
		{
			inputToLayer1Axons = brain.get(0);
			layer1ToLayer2Axons = brain.get(1);
			layer2ToOutputAxons = brain.get(2);
			// initialize the slightly mutated axons
			for(int lay1 = 0; lay1 < inputNeurons.length; lay1++)
			{
				for(int lay2 = 0; lay2 < hidLayer1.length; lay2++)
				{
					if(Math.random() < mutationFactor)
						inputToLayer1Axons[lay1][lay2].weight += ((Math.random() * 2 - 1) * mutationFactor);
				}
			}
			for(int lay1 = 0; lay1 < hidLayer1.length; lay1++)
			{
				for(int lay2 = 0; lay2 < hidLayer2.length; lay2++)
				{
					if(Math.random() < mutationFactor)
						layer1ToLayer2Axons[lay1][lay2].weight += ((Math.random() * 2 - 1) * mutationFactor);
				}
			}
			for(int lay1 = 0; lay1 < hidLayer2.length; lay1++)
			{
				for(int lay2 = 0; lay2 < outputNeurons.length; lay2++)
				{
					if(Math.random() < mutationFactor)
						layer2ToOutputAxons[lay1][lay2].weight += ((Math.random() * 2 - 1) * mutationFactor);
				}
			}
		}
		// done init, fill other layers
		for(int i = 0; i < inputNeurons.length; i++)
		{
			inputNeurons[i] = 0.0;
		}
		updateBrain(defaultSensorValues);

	}

	public void iterate(double[] sensorValues, double timeInterval)
	{
		fitness += timeInterval;
		for(int i = 0; i < sensorValues.length; i++)
		{
			sensorValues[i] = Math.copySign(Maths.netSigmoid(sensorValues[i]), sensorValues[i]);
		}
		updateBrain(sensorValues);
		applyOutputs(timeInterval);
		diameter = size / 10.0;

		x = Math.min(x, TileManager.getTileFromPixels(TileManager.getHorizontalNum() - 1, TileManager.getVerticalNum() - 1).getX()
				+ TileManager.getTileSize());
		y = Math.min(y, TileManager.getTileFromPixels(TileManager.getHorizontalNum() - 1, TileManager.getVerticalNum() - 1).getY()
				+ TileManager.getTileSize());

		x = Math.max(x, TileManager.getTileFromPixels(0, 0).getX());
		y = Math.max(y, TileManager.getTileFromPixels(0, 0).getY());
	}

	public void drawCreatureBrain() // top left = 1620, 800 //change values ugh
	{
		int verticalSpacing = Maths.scaleY(70);
		p.textSize(Maths.scaleY(50));
		p.text("Input", Maths.scaleX(1620), Maths.scaleY(900));
		p.text("Layer 1", Maths.scaleX(1900), Maths.scaleY(900));
		p.text("Layer 2", Maths.scaleX(2120), Maths.scaleY(900));
		p.text("Output", Maths.scaleX(2420), Maths.scaleY(900));
		p.colorMode(PConstants.RGB);
		for(int i = 0; i < inputNeurons.length; i++)
		{
			p.fill(255);
			p.textSize(Maths.scaleY(30));
			// rect(p2pl(1620), p2pw(800 + verticalSpacing * i), p2pl(100), p2pw(50));
			p.text(Maths.decimalFormat(inputNeurons[i]) + "", Maths.scaleX(1620), Maths.scaleY(950) + verticalSpacing * i);
		}
		for(int i = 0; i < hidLayer1.length; i++)
		{
			p.fill(255);
			p.textSize(Maths.scaleY(30));
			// rect(p2pl(1620), p2pw(800 + verticalSpacing * i), p2pl(100), p2pw(50));
			p.text(Maths.decimalFormat(hidLayer1[i]) + "", Maths.scaleX(1900), Maths.scaleY(950) + Maths.scaleY(40) * i);
		}
		for(int i = 0; i < hidLayer2.length; i++)
		{
			p.fill(255);
			p.textSize(Maths.scaleY(30));
			// rect(p2pl(1620), p2pw(800 + verticalSpacing * i), p2pl(100), p2pw(50));
			p.text(Maths.decimalFormat(hidLayer2[i]) + "", Maths.scaleX(2120), Maths.scaleY(950) + Maths.scaleY(40) * i);
		}
		for(int i = 0; i < outputNeurons.length; i++)
		{
			p.fill(255);
			p.textSize(Maths.scaleY(30));
			// rect(p2pl(1620), p2pw(800 + verticalSpacing * i), p2pl(100), p2pw(50));
			p.text(Maths.decimalFormat(outputNeurons[i]) + "", Maths.scaleX(2420), Maths.scaleY(950) + verticalSpacing * i);
		}

		for(int i = 0; i < inputNeurons.length; i++)
		{
			int color = 0;
			for(int one = 0; one < hidLayer1.length; one++)
			{
				color = (int) (Maths.sigmoid(inputToLayer1Axons[i][one].weight + 1.0) * 255);
				if(color < 0)
					color = 0;
				if(color > 255)
					color = 255;
				p.stroke(color);
				p.line(Maths.scaleX(1700), Maths.scaleY(945) + verticalSpacing * i, Maths.scaleX(1890),
						Maths.scaleY(940) + Maths.scaleY(40) * one);
			}
		}
		for(int i = 0; i < hidLayer2.length; i++)
		{
			int color = 0;
			for(int o = 0; o < outputNeurons.length; o++)
			{
				color = (int) (Maths.sigmoid(layer2ToOutputAxons[i][o].weight + 1.0) * 255);
				if(color < 0)
					color = 0;
				if(color > 255)
					color = 255;
				p.stroke(color);
				p.line(Maths.scaleX(2200), Maths.scaleY(945) + Maths.scaleY(40) * i, Maths.scaleX(2410),
						Maths.scaleY(940) + verticalSpacing * o);
			}
		}
		p.stroke(0);
	}

	public void updateBrain(double[] sensorValues)
	{
		inputNeurons = sensorValues;
		for(int i = 0; i < hidLayer1.length; i++)
		{
			for(int j = 0; j < inputNeurons.length; j++)
			{
				hidLayer1[i] += inputNeurons[j] * inputToLayer1Axons[j][i].weight;
			}
			hidLayer1[i] = Maths.netSigmoid(hidLayer1[i]);
		}
		for(int i = 0; i < hidLayer2.length; i++)
		{
			for(int j = 0; j < hidLayer1.length; j++)
			{
				hidLayer2[i] += hidLayer1[j] * layer1ToLayer2Axons[j][i].weight;
			}
			hidLayer2[i] = Maths.netSigmoid(hidLayer2[i]);
		}
		for(int i = 0; i < outputNeurons.length; i++)
		{
			for(int j = 0; j < hidLayer2.length; j++)
			{
				outputNeurons[i] += hidLayer2[j] * layer2ToOutputAxons[j][i].weight;
			}
			outputNeurons[i] = Maths.netSigmoid(outputNeurons[i]);
		}
	}

	public void applyOutputs(double timeInterval)
	{
		// Forward velocity, rotational velocity, eat, attack, give birth, attack length,
		//forwardVel = 0 disables movement
		forwardVel = 0;//outputNeurons[0];
		rotationVel = outputNeurons[1];
		eatRate = 6 * outputNeurons[2];
		if(outputNeurons[3] > 0)
			attack = true;
		else
			attack = false;
		// if(Math.random() < outputNeurons[4]) giveBirth(); // this is done somewhere else now
		killerLength = (int) ((outputNeurons[5] / 2 + 1) * (50 + diameter));

		rotation += rotationVel * timeInterval;

		double tempAngle = -(rotation % (2 * Math.PI));
		double deltaPos = 100 * forwardVel * timeInterval;
		x += (deltaPos * Math.cos(tempAngle));
		y += (deltaPos * Math.sin(tempAngle));

		decayRate = (size / 100.0) + (fitness / 100.0) + (eatRate / 5.0);
		size -= (decayRate * timeInterval);
		energyChange = eatRate - decayRate;

		totalDecayed = getTotalDecayed() + decayRate * timeInterval;

		// System.out.println("mouthx" + mouthSensorX + " mouth y " + mouthSensorY);
	}

	public double requestEat(double timeInterval)
	{
		return eatRate * timeInterval;
	}

	public void allowEat(double amount)
	{
		totalEaten += amount;
		size += amount;
	}

	public boolean requestBirth()
	{
		// System.out.println(ID + " test " + outputNeurons[4]);
		if(outputNeurons[4] > 0.0)
			return true;
		else
			return false;
	}

	public ArrayList<Axon[][]> giveBirth()
	{
		births++;
		size -= 150.0;
		ArrayList<Axon[][]> giveBrain = new ArrayList<Axon[][]>();
		giveBrain.add(inputToLayer1Axons);
		giveBrain.add(layer1ToLayer2Axons);
		giveBrain.add(layer2ToOutputAxons);
		return giveBrain;
	}

	public double getSize()
	{
		return size;
	}

	public double getDiameter()
	{
		return diameter;
	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	public double getTotalDecayed()
	{
		return totalDecayed;
	}

	public int getID()
	{
		return ID;
	}

	public double getTotalEaten()
	{
		return totalEaten;
	}

	public double getFitness()
	{
		return fitness;
	}

	public void setFitness(double fitness)
	{
		this.fitness = fitness;
	}

	public double getBirthDate()
	{
		return birthDate;
	}

	public double getAge()
	{
		return age;
	}

	public double getForwardVel()
	{
		return forwardVel;
	}

	public double getRotation()
	{
		return rotation;
	}

	public double getRotationVel()
	{
		return rotationVel;
	}

	public int getSensorLength()
	{
		return sensorLength;
	}

	public int getKillerLength()
	{
		return killerLength;
	}

	public double[] getDefaultSensorValues()
	{
		return defaultSensorValues;
	}

	public double getEatRate()
	{
		return eatRate;
	}

	public double getDecayRate()
	{
		return decayRate;
	}

	public double getEnergyChange()
	{
		return energyChange;
	}

	public boolean isAttack()
	{
		return attack;
	}

	public int getGeneration()
	{
		return generation;
	}

	public double getMutationFactor()
	{
		return mutationFactor;
	}

	public int getBirths()
	{
		return births;
	}

	public ArrayList<Axon[][]> getBrain()
	{
		return brain;
	}

	public double getLeftSensorX()
	{
		return leftSensorX;
	}

	public double getLeftSensorY()
	{
		return leftSensorY;
	}

	public double getMidSensorX()
	{
		return midSensorX;
	}

	public double getMidSensorY()
	{
		return midSensorY;
	}

	public double getRightSensorX()
	{
		return rightSensorX;
	}

	public double getRightSensorY()
	{
		return rightSensorY;
	}

	public double getMouthSensorX()
	{
		return mouthSensorX;
	}

	public double getMouthSensorY()
	{
		return mouthSensorY;
	}

	public double[] getInputNeurons()
	{
		return inputNeurons;
	}

	public double[] getHidLayer1()
	{
		return hidLayer1;
	}

	public double[] getHidLayer2()
	{
		return hidLayer2;
	}

	public double[] getOutputNeurons()
	{
		return outputNeurons;
	}

	public Axon[][] getInputToLayer1Axons()
	{
		return inputToLayer1Axons;
	}

	public Axon[][] getLayer1ToLayer2Axons()
	{
		return layer1ToLayer2Axons;
	}

	public Axon[][] getLayer2ToOutputAxons()
	{
		return layer2ToOutputAxons;
	}

	// takes in an xP and yP in pixels and checks it against tile locations to return a tile index pair
	private Point2D getTilePoint(double xP, double yP)
	{
		xP = (int) xP;
		yP = (int) xP;

		Point2D point;

		for(int x = 0; x < TileManager.getHorizontalNum(); x++)
		{
			for(int y = 0; y < TileManager.getVerticalNum(); y++)
			{
				if(TileManager.getTileFromIndex(x, y).getX() < xP
						&& xP <= TileManager.getTileFromIndex(x, y).getX() + TileManager.getTileSize())
				{
					if(TileManager.getTileFromIndex(x, y).getY() < yP
							&& yP <= TileManager.getTileFromIndex(x, y).getY() + TileManager.getTileSize())
					{
						point = new Point2D.Double(x, y);
						return point;
					}
				}
			}
		}

		System.out.println("ERROR - COULD NOT FIND TILE POINT");
		return null;
	}
}