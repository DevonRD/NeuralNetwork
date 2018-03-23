package io.github.kennytk;

import java.util.ArrayList;

import processing.core.PApplet;

public class Creature
{
	private PApplet p;
	
	double locationX;
	double locationY;
	double size;
	double diameter;
	int ID;
	double totalEaten;
	double totalDecayed;
	int fitness;
	double birthDate;
	double age;
	double forwardVel;
	double rotation;
	double rotationVel;
	int sensorLength;
	int killerLength;
	double[] defaultSensorValues;
	final int numInputs = 8;
	final double ANGLE_CONSTANT = Math.PI/6.0;
	double eatRate, decayRate, energyChange;
	boolean attack;
	int generation;
	double mutateFactor;
	int births = 0;
	ArrayList<Axon[][]> brain = new ArrayList<Axon[][]>();
	
	double leftSensorX, leftSensorY;
	double midSensorX, midSensorY;
	double rightSensorX, rightSensorY;
	double mouthSensorX, mouthSensorY;
	
	double[] inputNeurons;
	double[] hidLayer1;
	double[] hidLayer2;
	double[] outputNeurons;
	
	Axon[][] inputToLayer1Axons;
	Axon[][] layer1ToLayer2Axons;
	Axon[][] layer2ToOutputAxons;
	// 30 degrees both ways   pi/6
		
	public Creature(PApplet p, int startX, int startY, int ID, int generation, double mutateFactor) // no specified size
	{
		this.p = p;
		size = 150 + p.random(-25,25);
		locationX = startX;
		locationY = startY;
		diameter = size/10.0;
		this.ID = ID;
		totalEaten = 0;
		totalDecayed = 0;
		fitness = 0;
		rotation = 0;
		sensorLength = (int) (60 + diameter);
		killerLength = (int) (30 + diameter);
		this.generation = generation;
		this.mutateFactor = mutateFactor;
		this.brain = null;
		updateSensorCoords();
		brainInit();
	}
	public Creature(int startX, int startY, int ID, int size, int generation, double mutateFactor) // specified size
	{
		this.size = size;
		locationX = startX;
		locationY = startY;
		diameter = size/10.0;
		this.ID = ID;
		totalEaten = 0;
		totalDecayed = 0;
		fitness = 0;
		rotation = 0;
		sensorLength = (int) (60 + diameter);
		killerLength = (int) (30 + diameter);
		this.generation = generation;
		this.mutateFactor = mutateFactor;
		this.brain = null;
		updateSensorCoords();
		brainInit();
	}
	public Creature(int startX, int startY, int ID, int size, int generation, double mutateFactor, ArrayList<Axon[][]> brain) // specified size
	{
		this.size = size;
		locationX = startX;
		locationY = startY;
		diameter = size/10.0;
		this.ID = ID;
		totalEaten = 0;
		totalDecayed = 0;
		fitness = 0;
		rotation = 0;
		sensorLength = (int) (60 + diameter);
		killerLength = (int) (30 + diameter);
		this.generation = generation;
		this.brain = brain;
		this.mutateFactor = mutateFactor;
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
		
		// all this is autism. please do not uncomment
//		if(0 <= leftSensorRotation && leftSensorRotation < Math.PI/2.0) leftSensorTempAngle = -leftSensorRotation;
//		else if(Math.PI/2.0 <= leftSensorRotation && leftSensorRotation < Math.PI) leftSensorTempAngle = -(Math.PI - leftSensorRotation);
//		else if(Math.PI <= leftSensorRotation && leftSensorRotation < 3*Math.PI/2.0) leftSensorTempAngle = -(leftSensorRotation - Math.PI);
//		else if(3*Math.PI/2.0 <= leftSensorRotation && leftSensorRotation < 2*Math.PI) leftSensorTempAngle = -(2*Math.PI - leftSensorRotation);
//		else if(-Math.PI/2.0 <= leftSensorRotation && leftSensorRotation < 0) leftSensorTempAngle = -leftSensorRotation;
//		else if(-Math.PI <= leftSensorRotation && leftSensorRotation < -Math.PI/2.0) leftSensorTempAngle = -(-Math.PI - leftSensorRotation);
//		else if(-3*Math.PI/2.0 <= leftSensorRotation && leftSensorRotation < -Math.PI/2.0) leftSensorTempAngle = -(leftSensorRotation + Math.PI);
//		else if(-2*Math.PI <= leftSensorRotation && leftSensorRotation < -3*Math.PI/2.0) leftSensorTempAngle = -(2 * Math.PI + leftSensorRotation);
		
//		if(0 <= leftSensorRotation && leftSensorRotation < Math.PI/2.0) leftSensorTempAngle = -leftSensorRotation;
//		else if(Math.PI/2.0 <= leftSensorRotation && leftSensorRotation < Math.PI) leftSensorTempAngle = -(Math.PI - leftSensorRotation);
//		else if(Math.PI <= leftSensorRotation && leftSensorRotation < 3*Math.PI/2.0) leftSensorTempAngle = -(leftSensorRotation - Math.PI);
//		else if(3*Math.PI/2.0 <= leftSensorRotation && leftSensorRotation < 2*Math.PI) leftSensorTempAngle = -(2*Math.PI - leftSensorRotation);
//		else if(-Math.PI/2.0 <= leftSensorRotation && leftSensorRotation < 0) leftSensorTempAngle = -leftSensorRotation;
//		else if(-Math.PI <= leftSensorRotation && leftSensorRotation < -Math.PI/2.0) leftSensorTempAngle = -(-Math.PI - leftSensorRotation);
//		else if(-3*Math.PI/2.0 <= leftSensorRotation && leftSensorRotation < -Math.PI/2.0) leftSensorTempAngle = -(leftSensorRotation + Math.PI);
//		else if(-2*Math.PI <= leftSensorRotation && leftSensorRotation < -3*Math.PI/2.0) leftSensorTempAngle = -(2 * Math.PI + leftSensorRotation);
		
//		if(0 <= rightSensorRotation && rightSensorRotation < Math.PI/2.0) rightSensorTempAngle = rightSensorRotation;
//		else if(Math.PI/2.0 <= rightSensorRotation && rightSensorRotation < Math.PI) rightSensorTempAngle = rightSensorRotation - Math.PI/2.0;
//		else if(Math.PI <= rightSensorRotation && rightSensorRotation < 3*Math.PI/2.0) rightSensorTempAngle = rightSensorRotation - Math.PI;
//		else if (3*Math.PI/2.0 <= rightSensorRotation && rightSensorRotation < 2*Math.PI) rightSensorTempAngle = rightSensorRotation - 3*Math.PI/2.0;
//		else if(-Math.PI/2.0 <= rightSensorRotation && rightSensorRotation < 0) rightSensorTempAngle = rightSensorRotation;
//		else if(-Math.PI <= rightSensorRotation && rightSensorRotation < -Math.PI/2.0) rightSensorTempAngle = rightSensorRotation - Math.PI/2.0;
//		else if(-3*Math.PI/2.0 <= rightSensorRotation && rightSensorRotation < -Math.PI/2.0) rightSensorTempAngle = rightSensorRotation - Math.PI;
//		else if(-2*Math.PI <= rightSensorRotation && rightSensorRotation < -3*Math.PI/2.0) rightSensorTempAngle = rightSensorRotation - 3*Math.PI/2.0;
		
//		if(0 <= rotation && rotation < Math.PI/2.0) midSensorTempAngle = rotation;
//		else if(Math.PI/2.0 <= rotation && rotation < Math.PI) midSensorTempAngle = rotation - Math.PI/2.0;
//		else if(Math.PI <= rotation && rotation < 3*Math.PI/2.0) midSensorTempAngle = rotation - Math.PI;
//		else if(3*Math.PI/2.0 <= rotation && rotation < 2*Math.PI) midSensorTempAngle = rotation - 3*Math.PI/2.0;
//		else if(-Math.PI/2.0 <= rotation && rotation < 0) midSensorTempAngle = rotation;
//		else if(-Math.PI <= rotation && rotation < -Math.PI/2.0) midSensorTempAngle = rotation - Math.PI/2.0;
//		else if(-3*Math.PI/2.0 <= rotation && rotation < -Math.PI/2.0) midSensorTempAngle = rotation - Math.PI;
//		else if(-2*Math.PI <= rotation && rotation < -3*Math.PI/2.0) midSensorTempAngle = rotation - 3*Math.PI/2.0;
//		System.out.println("(temp) left: " + leftSensorTempAngle + " mid: " + midSensorTempAngle + " right: " + rightSensorTempAngle);

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
			//initialize the slightly mutated axons
			for(int lay1 = 0; lay1 < inputNeurons.length; lay1++)
			{
				for(int lay2 = 0; lay2 < hidLayer1.length; lay2++)
				{
					if(Math.random() < mutateFactor) inputToLayer1Axons[lay1][lay2].weight += ( (Math.random()*2 - 1) * mutateFactor);
				}
			}
			for(int lay1 = 0; lay1 < hidLayer1.length; lay1++)
			{
				for(int lay2 = 0; lay2 < hidLayer2.length; lay2++)
				{
					if(Math.random() < mutateFactor) layer1ToLayer2Axons[lay1][lay2].weight += ( (Math.random()*2 - 1) * mutateFactor);
				}
			}
			for(int lay1 = 0; lay1 < hidLayer2.length; lay1++)
			{
				for(int lay2 = 0; lay2 < outputNeurons.length; lay2++)
				{
					if(Math.random() < mutateFactor) layer2ToOutputAxons[lay1][lay2].weight += ( (Math.random()*2 - 1) * mutateFactor);
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
			sensorValues[i] = Math.copySign(sigmoid(sensorValues[i]), sensorValues[i]);
		}
		updateBrain(sensorValues);
		applyOutputs(timeInterval);
		diameter = size / 10.0;
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
		for(int i = 0; i < hidLayer2.length; i++)
		{
			for(int j = 0; j < hidLayer1.length; j++)
			{
				hidLayer2[i] += hidLayer1[j] * layer1ToLayer2Axons[j][i].weight; 
			}
			hidLayer2[i] = sigmoid(hidLayer2[i]);
		}
		for(int i = 0; i < outputNeurons.length; i++)
		{
			for(int j = 0; j < hidLayer2.length; j++)
			{
				outputNeurons[i] += hidLayer2[j] * layer2ToOutputAxons[j][i].weight; 
			}
			outputNeurons[i] = sigmoid(outputNeurons[i]);
		}
	}
	
	public void applyOutputs(double timeInterval)
	{		
		// Forward velocity, rotational velocity, eat, attack, give birth, attack length,
		forwardVel = outputNeurons[0];
		rotationVel = outputNeurons[1];
		eatRate = 6 * outputNeurons[2];
		if(outputNeurons[3] > 0) attack = true;
		else attack = false;
//		if(Math.random() < outputNeurons[4]) giveBirth(); // this is done somewhere else now
		killerLength = (int) ((outputNeurons[5]/2 + 1) * (50 + diameter));
		
		rotation += rotationVel * timeInterval;
		
		double tempAngle = -(rotation % (2*Math.PI));
		double deltaPos = 100 * forwardVel * timeInterval;
		locationX += (deltaPos * Math.cos(tempAngle));
		locationY += (deltaPos * Math.sin(tempAngle));
		
		decayRate = (size / 100.0) + (fitness / 100.0) + (eatRate / 5.0);
		size -= (decayRate * timeInterval);
		energyChange = eatRate - decayRate;
		totalDecayed += decayRate * timeInterval;
//		System.out.println("mouthx" + mouthSensorX + " mouth y " + mouthSensorY);
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
//		System.out.println(ID + " test " + outputNeurons[4]);
		if(outputNeurons[4] > 0.0) return true;
		else return false;
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
	
	public double sigmoid(double x)
	{
		return (2.0 / (1 + Math.pow(Math.E, -(x / 2.0)))) - 1.0;
	}
	
	
	
	
	
	
	
	
	
}