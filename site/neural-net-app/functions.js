function wPix(pix) {
    return Math.floor(1.0 * width * pix / 2600.0);
}

function hPix(pix) {
    return Math.floor(1.0 * height * pix / 1600.0);
}

function distanceFormula(x1, y1, x2, y2) {
    return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
}

function randomBound(upperBound) {
    return Math.random() * upperBound;
}

function randomRange(lowerBound, upperBound) {
    var rand = Math.random();
    return rand * lowerBound + (1 - rand) * upperBound;
}

function randCoord() {
    var xCoord = Math.floor(randomRange(
        world[0][0].x, world[settings.world_width - 1][settings.world_height - 1].x));
    var yCoord = Math.floor(randomRange(
        world[0][0].y, world[settings.world_width - 1][settings.world_height - 1].y));
    return [xCoord, yCoord];
}

function sigmoidA(input) {
    return (2.0 / (1.0 + Math.pow(Math.E, -(input / 1.0)))) - 1.0;
}