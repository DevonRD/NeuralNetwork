/**
 * manager.js - manages tile and creature updates between draws
 */

var tileIdCount = 0;
var creatureIdCount = 0;
var water;

var world;
var creatures = new Array();

function forEachTile(applyFunc) {
    for (var y = 0; y < settings.world_height; y++) {
        for (var x = 0; x < settings.world_width; x++) {
            var output = applyFunc(y, x);
            if (output != null) return output;
        }
    }
}

function forEachCreature(applyFunc) {
    for (var creature = 0; creature < creatures.length; creature++) {
        var output = applyFunc(creature);
        if (output != null) return output;
    }
}

function killAllCreatures() {
    creatures = new Array();
}

// ============================
//   INITIALIZATION FUNCTIONS 
// ============================

function initWorld() {
    initTiles();
    initCreatures();
}

function initTiles() {
    world = new Array(settings.world_height);
    for (var y = 0; y < settings.world_height; y++) {
        world[y] = new Array(settings.world_width);
    }

    forEachTile(createTile);
}

function createTile(yIndex, xIndex) {
    world[yIndex][xIndex] = new Tile(xIndex, yIndex, tileIdCount, false);
    // TODO: FIX AUTOMATIC WATER DETERMINATION IN CONSTRUCTOR ABOVE
    tileIdCount++;
    return null;
}

function initCreatures() {
    for (var i = 0; i < CONSTANTS.startNumCreatures; i++) {
        creatures.push(new Creature());
    }
}

// =========================
//   PROGRESSION FUNCTIONS 
// =========================

function progressWorld(gameSpeed) {
    forEachTile(progressTile);
    // progress creatures

}

function progressTile(yIndex, xIndex) {
    world[yIndex][xIndex].progress();
    return null;
}

// =====================
//   DRAWING FUNCTIONS 
// =====================

function drawWorld() {
    colorMode(RGB);
    background(100);
    fill(60);
    rect(wPix(8), 0, wPix(6), hPix(10));
    fill(255, 255, 255);
    textSize(wPix(30));

    // Draw tiles
    colorMode(HSB, 360, 100, 100);
    forEachTile(drawTile);
    colorMode(RGB, 255, 255, 255);

    // Draw creatures
    forEachCreature(drawCreature);
}

function drawTile(yIndex, xIndex) {
    var tile = world[yIndex][xIndex];
    stroke(tile.colorH, tile.colorS, tile.colorV - 10);
    fill(tile.colorH, tile.colorS, tile.colorV);
    rect(tile.x, tile.y, TILESIZE, TILESIZE);
    fill(0, 0, 0);
    stroke(0);
    // TODO: SEND FILL AND STROKE BACK TO DRAWWORLD?
    return null;
}

function drawCreature(creatureIndex) {
    var c = creatures[creatureIndex];
    colorMode(RGB);
    fill(255);
    stroke(50);
    line(Math.floor(c.locationX), Math.floor(c.locationY), Math.floor(c.leftSensorX), Math.floor(c.leftSensorY));
    if (c.attack) stroke(180, 0, 0);
    line(Math.floor(c.locationX), Math.floor(c.locationY), Math.floor(c.midSensorX), Math.floor(c.midSensorY));
    stroke(50);
    line(Math.floor(c.locationX), Math.floor(c.locationY), Math.floor(c.rightSensorX), Math.floor(c.rightSensorY));
    stroke(0);
    fill(c.colorR, c.colorG, c.colorB);
    if (selectedCreature !== null && c.id === selectedCreature.id) {
        stroke(240, 0, 255);
        strokeWeight(7);
    }
    ellipse(Math.floor(c.locationX), Math.floor(c.locationY), hPix(c.diameter), hPix(c.diameter));
    fill(255);
    stroke(0);
    strokeWeight(1);
    colorMode(HSB, 360, 100, 100);
    fill(c.leftSensorColor, 80, 45);
    ellipse(Math.floor(c.leftSensorX), Math.floor(c.leftSensorY), hPix(15), hPix(15));
    fill(c.rightSensorColor, 80, 45);
    ellipse(Math.floor(c.rightSensorX), Math.floor(c.rightSensorY), hPix(15), hPix(15));
    fill(c.mouthSensorColor, 80, 45);
    // OLD
    //ellipse((int)c.mouthSensorX, (int)c.mouthSensorY, p2pw(15), p2pw(15));
    colorMode(RGB, 255, 255, 255);
    fill(0);
}

// =======================
//   SEARCHING FUNCTIONS 
// =======================

function findTileIndex(yCoord, xCoord) {
    var yIndex = Math.floor(yCoord / TILESIZE);
    var xIndex = Math.floor(xCoord / TILESIZE);
    yIndex = Math.min(yIndex, settings.world_height - 1);
    xIndex = Math.min(xIndex, settings.world_width - 1);
    yIndex = Math.max(yIndex, 0);
    xIndex = Math.max(xIndex, 0);
    return [yIndex, xIndex];
}

function findTile(yCoord, xCoord) {
    indices = findTileIndex(yCoord, xCoord);
    return world[indices[0]][indices[1]];
}

function checkTileClick(mY, mX) {
    var yIndex = Math.floor(mY / TILESIZE);
    var xIndex = Math.floor(mX / TILESIZE);
    if (yIndex < 0 || settings.world_height <= yIndex) return null;
    if (xIndex < 0 || settings.world_width <= xIndex) return null;
    // TODO: SET MENU PATH TO TILE MODE
    return world[yIndex][xIndex];
}
