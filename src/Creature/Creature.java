package Creature;

import java.awt.Color;
import java.util.ArrayList;

import Utilities.Prefs;
import World.TileManager;

public class Creature
{
	// Creature-specific variables 
		public double locationX, locationY, size, diameter, birthDate, age,
			totalEaten, totalDecayed, fitness, forwardVel, rotation, rotationVel,
			leftSensorX, leftSensorY, midSensorX, midSensorY, rightSensorX, rightSensorY, mouthSensorX, mouthSensorY,
			attackDecay, sizeDecay, fitnessDecay, eatRateDecay, forwardDecay, rotationDecay,
			eatRate, decayRate, energyChange;
	
		public int ID, parentID, generation, births, sensorLength, weaponLength,
			leftSensorColor, rightSensorColor, mouthSensorColor,
			realWidth, realHeight;
	
		boolean attack;
		public Color color;
	
	// Neural network variables 
		public double[] defaultSensorValues, sensorInput, inputNeurons, hidLayer1, hidLayer2, outputNeurons;
		double mutateFactor = 0.1;
		double mutateChance = Prefs.MUTATE_CHANCE;
		double superMutateChance = Prefs.SUPER_MUTATE_CHANCE;
		public boolean superMutate = false;
		ArrayList<Axon[][]> brain = new ArrayList<Axon[][]>();
		public Axon[][] inputToLayer1Axons, layer1ToLayer2Axons, layer2ToOutputAxons;
	
	// Constants 
		public final int brainLength = 12;
		// 30 degrees both ways -> pi/6
		double sensorAngle = Math.PI/6.0;
		int startingBirthSize = 250;
	
	// Nearby creature information 
		public Creature nearestCreature = null;
		public double distToNearest, angleToNearest;
		public int colorDifferenceToNearest, numCreaturesWithin10;
	
	
	/** Constructor for a random creature with no parent and default brain **/
	public Creature(int realWidth, int realHeight, int startX, int startY, int ID, int generation)
	{
		parentID = 0;
		this.realWidth = realWidth;
		this.realHeight = realHeight;
		color = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
		size = 150 + (Math.random() - .5) * 50;
		locationX = startX;
		locationY = startY;
		diameter = size/10.0;
		this.ID = ID;
		
		distToNearest = 4.0;
		colorDifferenceToNearest = 200;
		angleToNearest = totalEaten = totalDecayed = fitness = rotation = rotationDecay = births = numCreaturesWithin10 = 0;

		sensorLength = Prefs.p2pw((60 + diameter));
		weaponLength = Prefs.p2pw((30 + diameter));
		this.generation = generation;
		this.brain = null;
		rotation = Math.random() * 2 * Math.PI;
		sensorInput = new double[brainLength];
		updateSensorCoords();
		brainInit();
	}
	
	/** Constructor for a creature birthed from a single parent, with mutation **/
	public Creature(int realWidth, int realHeight, int startX, int startY, int ID, int generation,
			ArrayList<Axon[][]> brain, Color c, int parentID)
	{
		this.parentID = parentID;
		this.realWidth = realWidth;
		this.realHeight = realHeight;
		color = c;
		size = startingBirthSize;
		locationX = startX;
		locationY = startY;
		diameter = size/10.0;
		this.ID = ID;
		
		distToNearest = 4.0;
		colorDifferenceToNearest = 200;
		angleToNearest = totalEaten = totalDecayed = fitness = rotation = rotationDecay = births = numCreaturesWithin10 = 0;
		
		sensorLength = Prefs.p2pw((60 + diameter));
		weaponLength = Prefs.p2pw((30 + diameter));
		this.generation = generation;
		this.brain = brain;
		rotation = Math.random() * 2 * Math.PI;
		sensorInput = new double[brainLength];
		updateSensorCoords();
		brainInit();
	}
	
	public void iterate(double timeInterval)
	{
		updateSensorInput(timeInterval);
		diameter = size / 10.0;
		sensorLength = Prefs.p2pw((60 + diameter));
		for(int i = 0; i < sensorInput.length; i++)
		{
			sensorInput[i] = Math.copySign(Prefs.sigmoid(sensorInput[i]), sensorInput[i]);
		}
		updateBrain();
		applyOutputs(timeInterval);
		keepCreatureInBounds();
	}
	
	public void updateSensorCoords()
	{
		rotation %= (2 * Math.PI);
		while (rotation < 0) rotation += (2 * Math.PI);
		double rightSensorRotation = rotation + sensorAngle;
		double leftSensorRotation = rotation - sensorAngle;
		while (rightSensorRotation < 0) rightSensorRotation += 2*Math.PI;
		while (leftSensorRotation < 0) leftSensorRotation += 2*Math.PI;
		rightSensorRotation %= (2 * Math.PI);
		leftSensorRotation %= (2 * Math.PI);
		//System.out.println("left: " + leftSensorRotation + " mid: " + rotation + " right: " + rightSensorRotation);
		double leftSensorTempAngle = -rightSensorRotation;
		double rightSensorTempAngle = -leftSensorRotation;
		double midSensorTempAngle = -rotation;

		//left sensor, mid sensor, right sensor, mouth
		leftSensorX = locationX + (sensorLength * Math.cos(leftSensorTempAngle));
		leftSensorY = locationY + (sensorLength * Math.sin(leftSensorTempAngle));
		midSensorX = locationX + (weaponLength * Math.cos(midSensorTempAngle));
		midSensorY = locationY + (weaponLength * Math.sin(midSensorTempAngle));
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
		hidLayer2 = new double[brainLength];
		// Forward velocity, rotational velocity, eat, attack, give birth, attack length,
		outputNeurons = new double[brainLength];
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
		else // for when it is a child and has inherited a brain
		{
			inputToLayer1Axons = brain.get(0);
			layer1ToLayer2Axons = brain.get(1);
			layer2ToOutputAxons = brain.get(2);
			if(Math.random() < superMutateChance)
			{
				superMutate = true;
				System.out.println("super mutate! creature: " + ID);
			}
			
			//initialize the mutated axons
			for(int lay1 = 0; lay1 < inputNeurons.length; lay1++)
			{
				for(int lay2 = 0; lay2 < hidLayer1.length; lay2++)
				{
					if(superMutate) inputToLayer1Axons[lay1][lay2].weight += ( (Math.random()*2 - 1) * 20 * mutateFactor);
					else if(Math.random() < mutateChance)
					{
						if(Math.random() * 4 - 2 < inputToLayer1Axons[lay1][lay2].weight) inputToLayer1Axons[lay1][lay2].weight -= Math.random() * mutateFactor;
						else inputToLayer1Axons[lay1][lay2].weight += Math.random() * mutateFactor;
					}
				}
			}
			for(int lay1 = 0; lay1 < hidLayer1.length; lay1++)
			{
				for(int lay2 = 0; lay2 < hidLayer2.length; lay2++)
				{
					if(superMutate) layer1ToLayer2Axons[lay1][lay2].weight += ( (Math.random()*2 - 1) * 20 * mutateFactor);
					else if(Math.random() < mutateChance)
					{
						if(Math.random() * 4 - 2 < layer1ToLayer2Axons[lay1][lay2].weight) layer1ToLayer2Axons[lay1][lay2].weight -= Math.random() * mutateFactor;
						else layer1ToLayer2Axons[lay1][lay2].weight += Math.random() * mutateFactor;
					}
				}
			}
			for(int lay1 = 0; lay1 < hidLayer2.length; lay1++)
			{
				for(int lay2 = 0; lay2 < outputNeurons.length; lay2++)
				{
					if(superMutate) layer2ToOutputAxons[lay1][lay2].weight += ( (Math.random()*2 - 1) * 20 * mutateFactor);
					else if(Math.random() < mutateChance)
					{
						if(Math.random() * 4 - 2 < layer2ToOutputAxons[lay1][lay2].weight) layer2ToOutputAxons[lay1][lay2].weight -= Math.random() * mutateFactor;
						else layer2ToOutputAxons[lay1][lay2].weight += Math.random() * mutateFactor;
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
		for(int i = 0; i < sensorInput.length; i++)
		{
			sensorInput[i] = 0.0;
		}
		updateBrain();
		
	}
	
	public void updateBrain()
	{
		inputNeurons = sensorInput;
		for(int i = 0; i < hidLayer1.length; i++)
		{
			for(int j = 0; j < inputNeurons.length; j++)
			{
				hidLayer1[i] += inputNeurons[j] * inputToLayer1Axons[j][i].weight; 
			}
			hidLayer1[i] = Prefs.sigmoid(hidLayer1[i]);
		}
		for(int i = 0; i < hidLayer2.length; i++)
		{
			for(int j = 0; j < hidLayer1.length; j++)
			{
				hidLayer2[i] += hidLayer1[j] * layer1ToLayer2Axons[j][i].weight; 
			}
			hidLayer2[i] = Prefs.sigmoid(hidLayer2[i]);
		}
		for(int i = 0; i < outputNeurons.length; i++)
		{
			for(int j = 0; j < hidLayer2.length; j++)
			{
				outputNeurons[i] += hidLayer2[j] * layer2ToOutputAxons[j][i].weight; 
			}
			outputNeurons[i] = Prefs.sigmoid(outputNeurons[i]);
		}
	}
	
	public void updateSensorInput(double timeInterval)
	{
		int[] leftTile, midTile, rightTile, mouthTile;
		fitness += timeInterval;
		updateSensorCoords();
		leftTile = TileManager.findTileCoordsAt(leftSensorX, leftSensorY);
		midTile = TileManager.findTileCoordsAt(midSensorX, midSensorY);
		rightTile = TileManager.findTileCoordsAt(rightSensorX, rightSensorY);
		mouthTile = TileManager.findTileCoordsAt(mouthSensorX, mouthSensorY);
		
		leftSensorColor = TileManager.tiles[leftTile[0]][leftTile[1]].colorH;
		rightSensorColor = TileManager.tiles[rightTile[0]][rightTile[1]].colorH;
		mouthSensorColor = TileManager.tiles[mouthTile[0]][mouthTile[1]].colorH;
		
		/**
		 * ------Sensor Inputs------
		 * 0: Left sensor food value
		 * 1: Attack sensor food value
		 * 2: Mouth sensor food value
		 * 3: Right sensor food value
		 * 4: Own size
		 * 5: Distance to nearest creature
		 * 6: Color difference of nearest creature
		 * 7: Change in angle to nearest creature (-180 to +180)
		 * 8: Willingness of nearest creature to birth
		 * 9: Number of creatures within 10 tile radius
		 * 
		 */

		sensorInput[0] = TileManager.tiles[leftTile[0]][leftTile[1]].food / 10.0 - 5.0;
		sensorInput[1] = TileManager.tiles[midTile[0]][midTile[1]].food / 10.0 - 5.0;
		sensorInput[2] = TileManager.tiles[mouthTile[0]][mouthTile[1]].food / 10.0 - 5.0;
		sensorInput[3] = TileManager.tiles[rightTile[0]][rightTile[1]].food / 10.0 - 5.0;
		sensorInput[4] = size / 100.0 - 3.0;
		
		Object[] nearestCreatureData = CreatureManager.findClosestCreatureData(this);
		nearestCreature = (Creature) nearestCreatureData[0];
		numCreaturesWithin10 = (int) nearestCreatureData[1];
		if(nearestCreature != null)
		{
			double tempAngle;
			distToNearest = Prefs.distBtCoords(locationX, locationY, nearestCreature.locationX, nearestCreature.locationY);
			distToNearest /= TileManager.tileSize;
			sensorInput[5] = distToNearest - 4.0;
			
			colorDifferenceToNearest = 0;
			colorDifferenceToNearest += Math.abs(color.getRed() - nearestCreature.color.getRed());
			colorDifferenceToNearest += Math.abs(color.getGreen() - nearestCreature.color.getGreen());
			colorDifferenceToNearest += Math.abs(color.getBlue() - nearestCreature.color.getBlue());
			sensorInput[6] = (colorDifferenceToNearest / 50.0) - 4;
			
			tempAngle = findAngleChange(locationX, locationY, nearestCreature.locationX, nearestCreature.locationY);
			if(tempAngle < 0) tempAngle += 360;
			tempAngle = tempAngle - (rotation / Math.PI * 180.0);
			if(tempAngle > 180) tempAngle -= 360;
			if(tempAngle < -180) tempAngle += 360;
			sensorInput[7] = angleToNearest = tempAngle;
		}
		else
		{
			sensorInput[5] = 100.0;
			sensorInput[6] = 0.0;
			sensorInput[7] = 0.0;
			sensorInput[8] = 0.0;
		}
		sensorInput[9] = numCreaturesWithin10 - 4;
		
	}
	
	public void keepCreatureInBounds()
	{
		locationX = Math.min(locationX, TileManager.tiles[TileManager.tileResL-1][TileManager.tileResW-1].x + TileManager.tileSize);
		locationY = Math.min(locationY, TileManager.tiles[TileManager.tileResL-1][TileManager.tileResW-1].y + TileManager.tileSize);
		locationX = Math.max(locationX, TileManager.tiles[0][0].x);
		locationY = Math.max(locationY, TileManager.tiles[0][0].y);
	}
	
	public void applyOutputs(double timeInterval)
	{
		// Forward velocity, rotational velocity, eat, attack, give birth, attack length,
		forwardVel = outputNeurons[0];
		rotationVel = outputNeurons[1];
		eatRate = 25 * Math.abs(outputNeurons[2]) / (1 + 2*Math.abs(forwardVel));
		if(outputNeurons[3] > 0) attack = true;
		else attack = false;
		weaponLength = (int) ((outputNeurons[5] / 2 + 0.5) * 50 + (diameter));
		
		rotation += rotationVel * timeInterval / 2;
		
		double tempAngle = -(rotation % (2*Math.PI));
		double deltaPos = 100 * forwardVel * timeInterval;
		locationX += (deltaPos * Math.cos(tempAngle));
		locationY += (deltaPos * Math.sin(tempAngle));
		
		double decayModifier = 1.5;
		sizeDecay = (size / 200.0) * decayModifier;
		fitnessDecay = (fitness / 300.0) * decayModifier;
		eatRateDecay = (eatRate / 15.0) * decayModifier;
		rotationDecay = (Math.abs(rotationVel) * timeInterval) * decayModifier;
		forwardDecay = 40 * (Math.abs(forwardVel) * timeInterval) * decayModifier;
		if(attack) attackDecay = 1.0 * decayModifier;
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
		giveBrain.add(layer1ToLayer2Axons);
		giveBrain.add(layer2ToOutputAxons);
		return giveBrain;
	}
	
	private double findStartAngle(double x1, double y1, double x2, double y2) {
        double xDiff = x2 - x1;
        double yDiff = y2 - y1;
        return Math.atan2(-yDiff, xDiff) * 180.0 / Math.PI;
    }
	
    // Returns the angle of this point by adding the angle change to the prevAngle.
    private double findAngleChange(double x1, double y1, double x2, double y2)
    {
        double target = findStartAngle(x1, y1, x2, y2);

        double a = target;
        double b = target + 360;
        double y = target - 360;

        double dir = a;

        if (Math.abs(a)>Math.abs(b))
        {
            dir = b;
            if (Math.abs(b) > Math.abs(y))
            {
                dir = y;
            }
        }
        if(Math.abs(a)>Math.abs(y))
        {
            dir = y;
        }

        return (dir);
    }
}