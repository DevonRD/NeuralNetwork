/**
 * manager.js - manages tile and creature updates between draws
 */

var tileIdCount = 0;
var world;
var water;

function forEachTile(applyFunc) {
    for (var y = 0; y < settings.world_height; y++) {
        for (var x = 0; x < settings.world_width; x++) {
            var output = applyFunc(y, x);
            if (output != null) return output;
        }
    }
}

// ============================
//   INITIALIZATION FUNCTIONS 
// ============================

function initWorld() {
    initTiles();
    // creatures init
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

// =========================
//   PROGRESSION FUNCTIONS 
// =========================

function progressWorld() {
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
    // Draw Tiles
    colorMode(HSB, 360, 100, 100);
    forEachTile(drawTile);
    colorMode(RGB, 255, 255, 255);

    // draw creatures
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

function randomRange(upperBound) {
    return Math.random() * upperBound;
}

function test() {
    if (mouseIsPressed) {
        fill(0);
    } else {
        fill(255);
    }
    ellipse(mouseX, mouseY, 80, 80);
}