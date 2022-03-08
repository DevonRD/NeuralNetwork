class Neuron {
    constructor(axons = null) {
        this.value = 0.0;

        if (axons !== null) {
            this.axons = axons;
        }
        else {
            this.axons = new Array(CONSTANTS.creatureBrainLength);
            for (var axon = 0; axon < CONSTANTS.creatureBrainLength; axon++) {
                this.axons[axon] = Math.random() * 2.0 - 1;
            }
        }
    }

    getFeedForward(targetIndex) {
        return this.value * this.axons[targetIndex];
    }

    resetValue() {
        this.value = 0.0;
    }

    addValue(value) {
        this.value += value;
    }
}

class Brain {
    // New brain from scratch
    constructor(brain1 = null, brain2 = null) {
        this.neurons = new Array(CONSTANTS.creatureBrainLength);
        for (var i = 0; i < CONSTANTS.creatureBrainLength; i++) {
            this.neurons[i] = new Array(CONSTANTS.creatureBrainLayers);
            for (var j = 0; j < CONSTANTS.creatureBrainLayers; j++) {
                if (brain1 !== null && brain2 !== null) {
                    // For gene ramdomization via reproduction
                    var axons1 = brain1.neurons[i][j].axons;
                    var axons2 = brain2.neurons[i][j].axons;
                    var mixedAxons = new Array(CONSTANTS.creatureBrainLength);
                    for (var axon = 0; axon < CONSTANTS.creatureBrainLength; axon++) {
                        mixedAxons[axon] = randomRange(axons1[axon], axons2[axon]);
                    }
                    this.neurons[i][j] = new Neuron(mixedAxons);
                }
                else {
                    // For creating a random brain from scratch
                    this.neurons[i][j] = new Neuron();
                }
            }
        }
    }

    updateInputs(inputs) {
        for (var i = 0; i < CONSTANTS.creatureBrainLength; i++) {
            this.neurons[i][0] = inputs[i];
        }
    }

    getOutputs() {
        var outputs = new Array(CONSTANTS.creatureBrainLength);
        for (var i = 0; i < CONSTANTS.creatureBrainLength; i++) {
            outputs[i] = this.neurons[i][CONSTANTS.creatureBrainLayers - 1];
        }
        return outputs;
    }

    updateBrain(inputs) {
        this.updateInputs(inputs);

        // Update neuron values
        for (var layer = 0; layer < CONSTANTS.creatureBrainLayers - 1; layer++) {
            // Zero-out target layer
            for (var target = 0; target < CONSTANTS.creatureBrainLength; target++) {
                this.neurons[target][layer + 1].resetValue();
            }
            // Feed forward
            for (var neuron = 0; neuron < CONSTANTS.creatureBrainLength; neuron++) {
                for (var target = 0; target < CONSTANTS.creatureBrainLength; target++) {
                    this.neurons[target][layer + 1].addValue(this.neurons[neuron][layer].getFeedForward(target));
                }
            }
        }

        return this.getOutputs();
    }
}

class Creature {
    constructor(parent1 = null, parent2 = null) {
        if (parent1 !== null && parent2 !== null) {
            this.parentIds = [parent1.id, parent2.id];
            this.generation = Math.max(parent1.generation, parent2.generation) + 1;
            this.colorR = Math.floor(0.5 * parent1.colorR + 0.5 * parent2.colorR);
            this.colorG = Math.floor(0.5 * parent1.colorG + 0.5 * parent2.colorG);
            this.colorB = Math.floor(0.5 * parent1.colorB + 0.5 * parent2.colorB);

            this.locationX = parent1.locationX;
            this.locationY = parent2.locationY;
            this.energy = CONSTANTS.creatureBirthSize;

            this.diameter = this.energy / 8.0;
            this.initVars();
            this.updateSensorCoords();

            this.brain = new Brain(parent1.brain, parent2.brain);
        }
        else {
            this.parentIds = [-1, -1];
            this.generation = 0;
            this.colorR = Math.floor(randomBound(255));
            this.colorG = Math.floor(randomBound(255));
            this.colorB = Math.floor(randomBound(255));

            var randPos = randCoord();
            this.locationX = randPos[0];
            this.locationY = randPos[1];
            // TODO: IMPLEMENT WATER CHECKING ON SPAWN HERE!!!
            this.energy = 125.0 + randomBound(50);

            this.diameter = this.energy / 8.0;
            this.initVars();
            this.updateSensorCoords();

            this.brain = new Brain();
        }
    }

    progress(timeInterval) {
        var sensorInputs = this.updateSensorInputs(timeInterval);
        this.diameter = this.energy / 8.0;
        this.sensorLength = hPix(60 + this.diameter);

        for (var i = 0; i < sensorInputs.length; i++) {
            var sign = Math.sign(sensorInputs[i]);
            sensorInputs[i] = sign * Math.abs(sigmoidA(sensorInputs[i]));
        }

        this.brain.updateBrain(sensorInputs);
        this.applyOutputs(timeInterval);
        this.keepCreatureInBounds();
    }

    updateSensorCoords() {
        this.rotation %= (2 * Math.PI);
        while (this.rotation < 0) this.rotation += (2 * Math.PI);
        var rightSensorRotation = this.rotation + CONSTANTS.creatureSensorAngle;
        var leftSensorRotation = this.rotation - CONSTANTS.creatureSensorAngle;
        while (rightSensorRotation < 0) rightSensorRotation += 2 * Math.PI;
        while (leftSensorRotation < 0) leftSensorRotation += 2 * Math.PI;
        rightSensorRotation %= (2 * Math.PI);
        leftSensorRotation %= (2 * Math.PI);

        var leftSensorTempAngle = -rightSensorRotation;
        var rightSensorTempAngle = -leftSensorRotation;
        var midSensorTempAngle = -this.rotation;

        //left sensor, mid sensor, right sensor, mouth
        this.leftSensorX = this.locationX + (this.sensorLength * Math.cos(leftSensorTempAngle));
        this.leftSensorY = this.locationY + (this.sensorLength * Math.sin(leftSensorTempAngle));
        this.midSensorX = this.locationX + (this.weaponLength * Math.cos(midSensorTempAngle));
        this.midSensorY = this.locationY + (this.weaponLength * Math.sin(midSensorTempAngle));
        this.rightSensorX = this.locationX + (this.sensorLength * Math.cos(rightSensorTempAngle));
        this.rightSensorY = this.locationY + (this.sensorLength * Math.sin(rightSensorTempAngle));
        this.mouthSensorX = this.locationX + (this.diameter / 2.0 * Math.cos(midSensorTempAngle));
        this.mouthSensorY = this.locationY + (this.diameter / 2.0 * Math.sin(midSensorTempAngle));
    }

    updateSensorInputs(timeInterval) {
        var leftTile, midTile, rightTile, mouthTile;
        this.fitness += timeInterval;
        this.updateSensorCoords();
        leftTile = findTileIndex(this.leftSensorX, this.leftSensorY);
        midTile = findTileIndex(this.midSensorX, this.midSensorY);
        rightTile = findTileIndex(this.rightSensorX, this.rightSensorY);
        mouthTile = findTileIndex(this.mouthSensorX, this.mouthSensorY);

        this.leftSensorColor = world[leftTile[0]][leftTile[1]].colorH;
        this.rightSensorColor = world[rightTile[0]][rightTile[1]].colorH;
        this.mouthSensorColor = world[mouthTile[0]][mouthTile[1]].colorH;

        /**
         * ------Sensor Inputs------
         * 0: Left sensor food value
         * 1: Attack sensor food value
         * 2: Mouth sensor food value
         * 3: Right sensor food value
         * 4: Own size (energy)
         * 5: Distance to nearest creature
         * 6: Color difference of nearest creature
         * 7: Change in angle to nearest creature (-180 to +180)
         * 8: Willingness of nearest creature to birth
         * 9: Number of creatures within 10 tile radius
         * 10:
         * 11: CONSTANT 1
         */

        var sensorInputs = new Array(CONSTANTS.creatureBrainLength);
        for (var i = 0; i < CONSTANTS.creatureBrainLength; i++) {
            sensorInputs[i] = 0.0;
        }
        sensorInputs[0] = world[leftTile[0]][leftTile[1]].food / 10.0 - 5.0;
        sensorInputs[1] = world[midTile[0]][midTile[1]].food / 10.0 - 5.0;
        sensorInputs[2] = world[mouthTile[0]][mouthTile[1]].food / 10.0 - 5.0;
        sensorInputs[3] = world[rightTile[0]][rightTile[1]].food / 10.0 - 5.0;
        sensorInputs[4] = this.energy / 100.0 - 3.0;

        /* TODO: IMPLEMENT FINDING NEAREST CREATURE
        Object[] nearestCreatureData = CreatureManager.findClosestCreatureData(this);
        nearestCreature = (Creature) nearestCreatureData[0];
        numCreaturesWithin10 = (int) nearestCreatureData[1]; */
        var nearestCreature = null;
        if (nearestCreature !== null) {
            this.nearestDist = distanceFormula(this.locationX, this.locationY, nearestCreature.locationX, nearestCreature.locationY);
            this.nearestDist /= TILESIZE;
            sensorInputs[5] = this.nearestDist - 4.0;

            this.nearestColorDiff = 0;
            this.nearestColorDiff += Math.abs(this.colorR - nearestCreature.colorR);
            this.nearestColorDiff += Math.abs(this.colorG - nearestCreature.colorG);
            this.nearestColorDiff += Math.abs(this.colorB - nearestCreature.colorB);
            sensorInputs[6] = (this.nearestColorDiff / 50.0) - 4;

            var tempAngle = findAngleChange(locationX, locationY, nearestCreature.locationX, nearestCreature.locationY);
            if (tempAngle < 0) tempAngle += 360;
            tempAngle -= (rotation / Math.PI * 180.0);
            if (tempAngle > 180) tempAngle -= 360;
            if (tempAngle < -180) tempAngle += 360;
            this.nearestAngle = tempAngle;
            sensorInputs[7] = tempAngle;
        }
        else {
            sensorInputs[5] = 100.0;
        }
        // PLACE BACK IN AFTER ^^^ IMPL
        //sensorInputs[9] = numCreaturesWithin10 - 4;
        sensorInputs[9] = 2.0;

        sensorInputs[11] = 1.0;
        return sensorInputs;
    }

    applyOutputs(timeInterval) {
        var outputs = this.brain.getOutputs();
        // Forward velocity, rotational velocity, eat, attack, give birth, attack length,
        this.forwardVel = outputs[0];
        this.rotationVel = outputs[1];
        this.eatRate = 25 * Math.abs(outputs[2]) / (1 + 2 * Math.abs(this.forwardVel));
        if (outputs[3] > 0) this.attack = true;
        else this.attack = false;
        this.weaponLength = Math.floor((outputs[5] / 2 + 0.5) * 50 + this.diameter);

        this.rotation += this.rotationVel * timeInterval / 2;

        var tempAngle = -(this.rotation % (2 * Math.PI));
        var deltaPos = 100 * this.forwardVel * timeInterval;
        this.locationX += (deltaPos * Math.cos(tempAngle));
        this.locationY += (deltaPos * Math.sin(tempAngle));

        var decayModifier = 1.5;
        this.energyDecay = (this.energy / 200.0) * decayModifier;
        this.ageDecay = (this.age / 300.0) * decayModifier;
        this.eatRateDecay = (this.eatRate / 15.0) * decayModifier;
        this.rotationDecay = (Math.abs(this.rotationVel) * timeInterval) * decayModifier;
        this.forwardDecay = 40 * (Math.abs(this.forwardVel) * timeInterval) * decayModifier;
        if (this.attack) this.attackDecay = 1.0 * decayModifier;
        else this.attackDecay = 0;

        this.decayRate = (this.energyDecay + this.ageDecay + this.eatRateDecay + this.rotationDecay + this.forwardDecay + this.attackDecay);
        this.energy -= (this.decayRate * timeInterval);
        this.energyChange = this.eatRate - this.decayRate;
        this.totalDecayed += (this.decayRate * timeInterval);
    }

    keepCreatureInBounds() {
        this.locationX = Math.min(this.locationX, world[settings.world_height - 1][settings.world_width - 1].x + TILESIZE);
        this.locationY = Math.min(this.locationY, world[settings.world_height - 1][settings.world_width - 1].y + TILESIZE);
        this.locationX = Math.max(this.locationX, world[0][0].x);
        this.locationY = Math.max(this.locationY, world[0][0].y);
    }

    initVars() {
        this.id = creatureIdCount;
        creatureIdCount++;

        // TODO: PROPER INITIALIZATION OF THESE VARIABLES?
        this.nearestDist = 0;
        this.nearestColorDiff = 0;

        this.nearestCreature = null;
        this.nearestAngle = 0;
        this.totalEaten = 0;
        this.totalDecayed = 0;
        this.age = 0;
        this.rotation = 0;
        this.rotationDecay = 0;
        this.births = 0;
        this.numCreaturesWithin10 = 0;

        this.sensorLength = hPix(60 + this.diameter);
        this.weaponLength = hPix(30 + this.diameter);
        this.rotation = Math.random() * 2 * Math.PI;
        //this.brain = null;

        this.attack = false;
    }
}

// =====================
//   UTILITY FUNCTIONS 
// =====================

function findStartAngle(x1, y1, x2, y2) {
    var xDiff = x2 - x1;
    var yDiff = y2 - y1;
    return Math.atan2(-yDiff, xDiff) * 180.0 / Math.PI;
}

// Returns the angle of this point by adding the angle change to the prevAngle.
function findAngleChange(x1, y1, x2, y2) {
    var target = findStartAngle(x1, y1, x2, y2);

    var a = target;
    var b = target + 360;
    var y = target - 360;
    var dir = a;

    if (Math.abs(a) > Math.abs(b)) {
        dir = b;
        if (Math.abs(b) > Math.abs(y)) {
            dir = y;
        }
    }
    if (Math.abs(a) > Math.abs(y)) {
        dir = y;
    }

    return dir;
}