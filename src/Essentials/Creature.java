package Essentials;

import java.awt.Color;
import java.util.ArrayList;

public class Creature
{
	double locationX;
	double locationY;
	double size;
	double diameter;
	int ID;
	double totalEaten;
	double totalDecayed;
	double fitness;
	double birthDate;
	double age;
	double forwardVel;
	double rotation;
	double rotationVel;
	int sensorLength;
	int killerLength;
	double[] defaultSensorValues;
	final int brainLength = 12;
	final double ANGLE_CONSTANT = Math.PI/6.0;
	double eatRate, decayRate, energyChange;
	boolean attack;
	int generation;
	double mutateChance;
	double mutateFactor = 0.1;
	double superMutateChance = 0.05;
	boolean superMutate = false;
	int births = 0;
	int realWidth, realHeight;
	Color color; // genes
	int leftSensorColor, rightSensorColor, mouthSensorColor;
	ArrayList<Axon[][]> brain = new ArrayList<Axon[][]>();
	double attackDecay, sizeDecay, fitnessDecay, eatRateDecay, forwardDecay, rotationDecay = 0;
	
	double leftSensorX, leftSensorY;
	double midSensorX, midSensorY;
	double rightSensorX, rightSensorY;
	double mouthSensorX, mouthSensorY;
	
	double[] inputNeurons;
	double[] hidLayer1;
	double[] outputNeurons;
	Axon[][] inputToLayer1Axons;
	Axon[][] layer1ToOutputAxons;
	String parentID;
	// 30 degrees both ways   pi/6
		
	public Creature(int realWidth, int realHeight, int startX, int startY, int ID, int generation, double mutateChance) // no specified size
	{
		parentID = " -- ";
		this.realWidth = realWidth;
		this.realHeight = realHeight;
		color = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
		size = 150 + (Math.random() - .5) * 50;
		locationX = startX;
		locationY = startY;
		diameter = size/10.0;
		this.ID = ID;
		totalEaten = 0;
		totalDecayed = 0;
		fitness = 0;
		rotation = 0;
		sensorLength = p2pw((40 + diameter));
		killerLength = p2pw((30 + diameter));
		this.generation = generation;
		this.mutateChance = mutateChance;
		this.brain = null;
		rotation = Math.random() * 2 * Math.PI;
		updateSensorCoords();
		brainInit();
	}
	public Creature(int realWidth, int realHeight, int startX, int startY, int ID, int size, int generation, double mutateChance, ArrayList<Axon[][]> brain, Color c, String parentID) // specified size
	{
		this.parentID = parentID;
		this.realWidth = realWidth;
		this.realHeight = realHeight;
		color = c;
		//color = new Color(c.getRed() + (int)(Math.random() * 4 - 2), c.getGreen() + (int)(Math.random() * 4 - 2), c.getBlue() + (int)(Math.random() * 4 - 2));
		this.size = size;
		locationX = startX;
		locationY = startY;
		diameter = size/10.0;
		this.ID = ID;
		totalEaten = 0;
		totalDecayed = 0;
		fitness = 0;
		rotation = 0;
		sensorLength = p2pw((40 + diameter));
		killerLength = p2pw((30 + diameter));
		this.generation = generation;
		this.brain = brain;
		this.mutateChance = mutateChance;
		rotation = Math.random() * 2 * Math.PI;
		updateSensorCoords();
		brainInit();
	}
	public void updateSensorCoords()
	{
		rotation %= (2 * Math.PI);
		double leftSensorRotation = rotation - ANGLE_CONSTANT;
		double rightSensorRotation = rotation + ANGLE_CONSTANT;
		while (leftSensorRotation < 0) leftSensorRotation += 2*Math.PI;
		while (rightSensorRotation < 0) rightSensorRotation += 2*Math.PI;
		leftSensorRotation %= (2 * Math.PI);
		rightSensorRotation %= (2 * Math.PI);
//		System.out.println("left: " + leftSensorRotation + " mid: " + rotation + " right: " + rightSensorRotation);
		double leftSensorTempAngle = -leftSensorRotation;
		double rightSensorTempAngle = -rightSensorRotation;
		double midSensorTempAngle = -rotation;

		//left sensor, mid sensor, right sensor, mouth
		leftSensorX = locationX + (sensorLength * Math.cos(leftSensorTempAngle));
		leftSensorY = locationY + (sensorLength * Math.sin(leftSensorTempAngle));
		midSensorX = locationX + (killerLength * Math.cos(midSensorTempAngle));
		midSensorY = locationY + (killerLength * Math.sin(midSensorTempAngle));
		rightSensorX = locationX + (sensorLength * Math.cos(rightSensorTempAngle));
		rightSensorY = locationY + (sensorLength * Math.sin(rightSensorTempAngle));
		mouthSensorX = locationX + (diameter/2.0 * Math.cos(midSensorTempAngle));
		mouthSensorY = locationY + (diameter/2.0 * Math.sin(midSensorTempAngle));
	}
	public void brainInit()
	{
		defaultSensorValues = new double[brainLength];
		// Left food, left creature, center food, center creature, right food, right creature, mouth food, size, 
		inputNeurons = new double[brainLength];
		hidLayer1 = new double[brainLength];
		// Forward velocity, rotational velocity, eat, attack, give birth, attack length,
		outputNeurons = new double[brainLength];
		if(brain == null)
		{
			inputToLayer1Axons = new Axon[inputNeurons.length][hidLayer1.length];
			layer1ToOutputAxons = new Axon[hidLayer1.length][outputNeurons.length];
			
			//initialize the random starting axons
			for(int lay1 = 0; lay1 < inputNeurons.length; lay1++)
			{
				for(int lay2 = 0; lay2 < hidLayer1.length; lay2++)
				{
					inputToLayer1Axons[lay1][lay2] = new Axon();
				}
			}
			for(int lay1 = 0; lay1 < hidLayer1.length; lay1++)
			{
				for(int lay2 = 0; lay2 < outputNeurons.length; lay2++)
				{
					layer1ToOutputAxons[lay1][lay2] = new Axon();
				}
			}
		}
		else // for when it is a child and has inherited a brain
		{
			inputToLayer1Axons = brain.get(0);
			layer1ToOutputAxons = brain.get(1);
			if(Math.random() < superMutateChance)
			{
				superMutate = true;
				System.out.println("super mutate! creature: " + ID);
			}
			
			//initialize the mutated axons
			int smc = 1;
			if(superMutate) smc = 5;
			for(int lay1 = 0; lay1 < brainLength; lay1++) // input to layer 1
			{
				for(int lay2 = 0; lay2 < brainLength; lay2++)
				{
					if(Math.random() < mutateChance || superMutate)
					{
						if(Math.random() * 4 - 2 < inputToLayer1Axons[lay1][lay2].weight) inputToLayer1Axons[lay1][lay2].weight -= mutateFactor * smc;
						else inputToLayer1Axons[lay1][lay2].weight += mutateFactor * smc;
					}
				}
			}
			for(int lay1 = 0; lay1 < brainLength; lay1++) // layer 1 to output
			{
				for(int lay2 = 0; lay2 < brainLength; lay2++)
				{
					if(Math.random() < mutateChance || superMutate)
					{
						if(Math.random() * 4 - 2 < layer1ToOutputAxons[lay1][lay2].weight) layer1ToOutputAxons[lay1][lay2].weight -= mutateFactor * smc;
						else layer1ToOutputAxons[lay1][lay2].weight += mutateFactor * smc;
					}
				}
			}
			if(superMutate)
			{
				int colorR = color.getRed();
				int colorG = color.getGreen();
				int colorB = color.getBlue();
				int colorChange = 30;
				if(Math.random() < 0.5)
				{
					colorR += colorChange;
					colorR = Math.min(255, colorR);
				}
				else
				{
					colorR -= colorChange;
					colorR = Math.max(0, colorR);
				}
				if(Math.random() < 0.5)
				{
					colorG += colorChange;
					colorG = Math.min(255, colorG);
				}
				else
				{
					colorG -= colorChange;
					colorG = Math.max(0, colorG);
				}
				if(Math.random() < 0.5)
				{
					colorB += colorChange;
					colorB = Math.min(255, colorB);
				}
				else
				{
					colorB -= colorChange;
					colorB = Math.max(0, colorB);
				}
				color = new Color(colorR, colorG, colorB);
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
		diameter = size / 10.0;
		sensorLength = p2pw((30 + diameter));
		for(int i = 0; i < sensorValues.length; i++)
		{
			sensorValues[i] = Math.copySign(sigmoid(sensorValues[i]), sensorValues[i]);
		}
		updateBrain(sensorValues);
		applyOutputs(timeInterval);
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
			hidLayer1[i] = sigmoid(hidLayer1[i]);
		}
		for(int i = 0; i < outputNeurons.length; i++)
		{
			for(int j = 0; j < hidLayer1.length; j++)
			{
				outputNeurons[i] += hidLayer1[j] * layer1ToOutputAxons[j][i].weight; 
			}
			outputNeurons[i] = sigmoid2(outputNeurons[i]);
		}
	}
	
	public void applyOutputs(double timeInterval)
	{		
		// Forward velocity, rotational velocity, eat, attack, give birth, attack length,
		forwardVel = outputNeurons[0];
		rotationVel = outputNeurons[1];
		eatRate = 25 * Math.abs(outputNeurons[2]) / (1 + 2*Math.abs(forwardVel));
		if(outputNeurons[3] > 0) attack = true;
		else attack = false;
		killerLength = (int) ((outputNeurons[5] / 2 + 0.5) * 50 + (diameter));
		
		rotation += rotationVel * timeInterval / 2;
		
		double tempAngle = -(rotation % (2*Math.PI));
		double deltaPos = 100 * forwardVel * timeInterval;
		locationX += (deltaPos * Math.cos(tempAngle));
		locationY += (deltaPos * Math.sin(tempAngle));
		
		double decayModifier = 1;
		sizeDecay = (size / 150.0) * decayModifier;
		fitnessDecay = (fitness / 500.0) * decayModifier;
		eatRateDecay = (eatRate / 15.0) * decayModifier;
		rotationDecay = (Math.abs(rotationVel) * timeInterval) * decayModifier;
		forwardDecay = 2 * (Math.abs(forwardVel) * timeInterval) * decayModifier;
		if(attack) attackDecay = 0.1 * decayModifier;
		else attackDecay = 0;
		
		decayRate = (sizeDecay + fitnessDecay + eatRateDecay + rotationDecay + forwardDecay + attackDecay);
		size -= (decayRate * timeInterval);
		energyChange = eatRate - decayRate;
		totalDecayed += (decayRate * timeInterval);
	}
	
	public double requestEat(double timeInterval)
	{
		return eatRate * timeInterval;
	}
	
	public void allowEat(double amount)
	{
		if(amount == 0.0) energyChange -= eatRate;
		totalEaten += amount;
		size += amount;
	}
	
	public boolean requestBirth()
	{
		if(outputNeurons[4] > 0.0) return true;
		else return false;
	}
	
	public ArrayList<Axon[][]> giveBirth()
	{
		births++;
		size -= 150.0;
		ArrayList<Axon[][]> giveBrain = new ArrayList<Axon[][]>();
		giveBrain.add(inputToLayer1Axons);
		giveBrain.add(layer1ToOutputAxons);
		return giveBrain;
	}
	
	public double sigmoid(double x)
	{
		return (1.0 / (1 + Math.pow(Math.E, -(x / 1.0))));
	}
	public double sigmoid2(double x)
	{
		return (2.0 / (1 + Math.pow(Math.E, -(x / 1.5))) - 1.0);
	}
	public int p2pl(double frac)
	{
		double returnPixels = 0;
		returnPixels = frac / 2600.0 * realWidth;
		return (int) returnPixels;
	}
	public int p2pw(double frac)
	{
		double returnPixels = 0;
		returnPixels = frac / 1600.0 * realHeight;
		return (int) returnPixels;
	}
	
	
	
	
	
	
	
}