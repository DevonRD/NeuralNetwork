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
            this.maxFood = 0;
            this.colorH = 220;
        }
        else {
            this.food = settings.tileMaxFood;
            this.maxFood = settings.tileMaxFood;
            this.colorH = int(this.maxH * this.food / this.maxFood);
        }

        this.colorS = 80;
        this.colorV = 45;
        this.deadCooldown = settings.cooldownThreshold;
    }

    progress() {
        if (!this.isWater) this.maxFood = settings.tileMaxFood;
        this.regenerateTileFood();
    }

    regenerateTileFood() {
        if (this.isWater || this.food >= this.maxFood) return;
        if (this.deadCooldown < settings.cooldownThreshold) {
            this.deadCooldown++;
            return;
        }

        this.food = Math.min(this.food + settings.regenValue, this.maxFood);
        this.colorH = int(this.maxH * this.food / this.maxFood);
    }

    resetCooldown() {
        this.deadCooldown = 0;
    }
}
