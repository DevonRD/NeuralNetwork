const CONSTANTS = {
    defaultScaleFactor: 0.26,
    startNumCreatures: 50,

    creatureBrainLength: 12,
    creatureBrainLayers: 4,
    creatureSensorAngle: Math.PI / 6.0,
    creatureBirthSize: 250
}

var settings = {
    width_proportion: 0.95,
    height_proportion: 0.9,
    text_size: 50,
    framerate: 60,

    start_maintain_num: 50,
    default_maintain: true,

    gameSpeed: 0.1,
    world_width: 100,
    world_height: 100,
    kill_birth_without_mass: false,
    tile_regen_rate: 0.025,
    tile_cooldown_threshold: 100,
    tileMaxFood: 100.0,
    graph_size: 150
};

var stats = {
    forced_spawns: 0,
    super_mutations: 0,
    dispalyTime: 0,
    creatureCount: 0,
    births: 0,
    deaths: 0,
    rawTime: 0,
    displayTime: 0,
    maxObservedCreatures: CONSTANTS.startNumCreatures
};

var modes = {
    spawnClicking: false,
    show_menu: false,
    show_creature_info: false,
    save_fps: false,
    play: true,
    draw_gene_pool_graph: true
};

var TILESIZE;

var b4x = 0, b4y = 0,
    deltaX = 0, deltaY = 0,
    translateX = 20, translateY = 20,
    scaleFactor = CONSTANTS.defaultScaleFactor;

var selectedCreature = null;

//p5.disableFriendlyErrors = true;

function setup() {
    createCanvas(settings.width_proportion * windowWidth,
        settings.height_proportion * windowHeight);
    frameRate(settings.framerate);
    textSize(settings.text_size);

    TILESIZE = Math.floor(4.0 * hPix(1500) / Math.min(settings.world_width, settings.world_height));

    //loadMap()
    //inst manager
    initWorld();
    //inst menu
    //menu.menuInit

    //noLoop();
};



function draw() {
    //f (Menu.path != Menu.MenuPath.CREATURE) selectedCreature = null;

    if (modes.play) {
        progress(settings.gameSpeed);
    }

    if (!modes.save_fps) {
        if (mouseIsPressed) {
            b4x = mouseX;
            b4y = mouseY;
        }

        push();

        if (selectedCreature != null) {
            scaleFactor = float(2.0);
            translateX = int(-scaleFactor * selectedCreature.locationX + hPix(850));
            translateY = int(-scaleFactor * selectedCreature.locationY + hPix(850));
        }
        translate(translateX, translateY);
        scale(float(scaleFactor));

        // Draw world tiles and creatures
        if (stats.rawTime % 50 === 0) drawWorld();

        pop();
    }

    //test()
    //menu.drawMenu(this);
};

function progress(gameSpeed) {
    stats.rawTime++;
    stats.dispalyTime += gameSpeed;
    progressWorld(gameSpeed);
}

function keyPressed() {
    // reset zoom and camera location
    if (key == 'r') {
        translateX = translateY = 20;
        scaleFactor = CONSTANTS.defaultScaleFactor;
        return;
    }
    // pause and unpause
    else if (key == ' ') {
        modes.play = !modes.play;
    }
    // toggle creature spawning on click
    else if (key == 's') {
        modes.spawnClicking = !modes.spawnClicking;
    }
    // kill all creatures
    else if (key == 'k') {
        killAllCreatures();
    }
}

function mouseDragged() {
    translateX += mouseX - pmouseX;
    translateY += mouseY - pmouseY;
}

function mouseWheel(event) {
    var change = -event.delta > 0 ? 1.05 : -event.delta < 0 ? 1.0 / 1.05 : 1.0;
    scaleFactor *= change;
    if (!(scaleFactor > 3.0) && !(scaleFactor < 0.2)) {
        translateX -= mouseX;
        translateY -= mouseY;
        translateX *= change;
        translateY *= change;
        translateX += mouseX;
        translateY += mouseY;
    }
    if (scaleFactor > 3.0) scaleFactor = 3.0;
    if (scaleFactor < 0.2) scaleFactor = 0.2;
}

function windowResized() {
    resizeCanvas(0.95 * windowWidth, 0.9 * windowHeight);
}