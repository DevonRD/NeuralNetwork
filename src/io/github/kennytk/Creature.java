package io.github.kennytk;

import java.util.ArrayList;

import processing.core.PApplet;

public class Creature
{
	private PApplet p;

	private double locationX;
	private double locationY;
	
	private double size;
	private double diameter;
	
	private int ID;
	
	private double totalEaten;
	private double totalDecayed;
	private int fitness;
	
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
	private ArrayList<Axon[][]> brain = new ArrayList<Axon[][]>();

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

	public Creature(PApplet p, int x, int y, int ID, int generation, double mutationFactor) // no specified size
	{
		this.p = p;
		
		size = 150 + p.random(-25, 25);
		
		diameter = size / 10.0;
		
		sensorLength = (int) (60 + diameter);
		killerLength = (int) (30 + diameter);
		
		commonConstructor(p, x, y, ID, generation, mutationFactor);
	}

	public Creature(PApplet p, int x, int y, int ID, int size, int generation, double mutationFactor) // specified size
	{
		this.size = size;

		diameter = size / 10.0;

		sensorLength = (int) (60 + diameter);
		killerLength = (int) (30 + diameter);
		
		commonConstructor(p, x, y, ID, generation, mutationFactor);
	}

	public Creature(PApplet p, int x, int y, int ID, int size, int generation, double mutationFactor, ArrayList<Axon[][]> brain) // specified size
	{
		this.size = size;
		diameter = size / 10.0;
		
		sensorLength = (int) (60 + diameter);
		killerLength = (int) (30 + diameter);
		
		this.brain = brain;
		
		commonConstructor(p, x, y, ID, generation, mutationFactor);
	}
	
	private void commonConstructor(PApplet p, int x, int y, int ID, int generation, double mutationFactor)
	{
		this.p = p;
		
		locationX = x;
		locationY = y;
		
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
		leftSensorX = locationX + (sensorLength * Math.cos(leftSensorTempAngle));
		leftSensorY = locationY + (sensorLength * Math.sin(leftSensorTempAngle));
		midSensorX = locationX + (killerLength * Math.cos(midSensorTempAngle));
		midSensorY = locationY + (killerLength * Math.sin(midSensorTempAngle));
		rightSensorX = locationX + (sensorLength * Math.cos(rightSensorTempAngle));
		rightSensorY = locationY + (sensorLength * Math.sin(rightSensorTempAngle));
		mouthSensorX = locationX + (diameter / 2.0 * Math.cos(midSensorTempAngle));
		mouthSensorY = locationY + (diameter / 2.0 * Math.sin(midSensorTempAngle));
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
					if(Math.random() < mutateFactor)
						inputToLayer1Axons[lay1][lay2].weight += ((Math.random() * 2 - 1) * mutateFactor);
				}
			}
			for(int lay1 = 0; lay1 < hidLayer1.length; lay1++)
			{
				for(int lay2 = 0; lay2 < hidLayer2.length; lay2++)
				{
					if(Math.random() < mutateFactor)
						layer1ToLayer2Axons[lay1][lay2].weight += ((Math.random() * 2 - 1) * mutateFactor);
				}
			}
			for(int lay1 = 0; lay1 < hidLayer2.length; lay1++)
			{
				for(int lay2 = 0; lay2 < outputNeurons.length; lay2++)
				{
					if(Math.random() < mutateFactor)
						layer2ToOutputAxons[lay1][lay2].weight += ((Math.random() * 2 - 1) * mutateFactor);
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
		forwardVel = outputNeurons[0];
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
		locationX += (deltaPos * Math.cos(tempAngle));
		locationY += (deltaPos * Math.sin(tempAngle));

		decayRate = (size / 100.0) + (fitness / 100.0) + (eatRate / 5.0);
		size -= (decayRate * timeInterval);
		energyChange = eatRate - decayRate;
		totalDecayed += decayRate * timeInterval;
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
}