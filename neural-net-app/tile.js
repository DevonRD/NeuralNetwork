class Tile {
    constructor(xIndex, yIndex, tileId, water) {
        this.xIndex = xIndex;
        this.yIndex = yIndex;
        this.tileId = tileId;
        this.isWater = water;
        this.maxH = 120.0;

        this.x = this.xIndex * TILESIZE;
        this.y = this.yIndex * TILESIZE;

        if (this.isWater) {
            this.food = 0;
            this.colorH = 220;
        }
        else {
            this.food = randomBound(settings.tileMaxFood);
            this.colorH = Math.floor(70.0 * this.food / settings.tileMaxFood) + 50;
        }

        this.colorS = 80;
        this.colorV = 45;
        this.deadCooldown = settings.cooldownThreshold;
    }

    progress() {
        if (this.isWater) return;
        this.regenerateTileFood();
    }

    regenerateTileFood() {
        if (this.food >= settings.tileMaxFood) return;
        if (this.deadCooldown < settings.cooldownThreshold) {
            this.deadCooldown++;
            return;
        }
        this.food += settings.tile_regen_rate;
        this.colorH = Math.floor(70.0 * this.food / settings.tileMaxFood) + 50;
    }

    resetCooldown() {
        this.deadCooldown = 0;
    }
}
