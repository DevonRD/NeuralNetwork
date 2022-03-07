//import * as manager from './manager.js';

var settings = {
    width_proportion: 0.95,
    height_proportion: 0.9,
    text_size: 50,
    default_scale_factor: 0.26,
    scaleFactor: 0.26,
    framerate: 1,

    start_maintain_num: 50,
    default_maintain: true,
    start_num_creatures: 50,
    game_speed: 0.1,
    world_width: 100,
    world_height: 100,
    kill_birth_without_mass: false,
    tile_regen_rate: 0.025,
    tile_cooldown_threshold: 100,
    tileMaxFood: 100,
    graph_size: 150
    // absent: mutate and super mutate chances
};

var stats = {
    forced_spawns: 0,
    super_mutations: 0,
    dispalyTime: 0,
    creatureCount: 0,
    births: 0,
    deaths: 0,
    maxObservedCreatures: settings.start_num_creatures
};

var modes = {
    spawn_clicking: false,
    show_menu: false,
    show_creature_info: false,
    save_fps: false,
    play: false,
    draw_gene_pool_graph: true
};

var TILESIZE;

var b4x = 0, b4y = 0,
    deltaX = 0, deltaY = 0,
    translateX = 20, translateY = 20;

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
        progress();
    }

    if (!modes.save_fps) {
        if (mouseIsPressed) {
            b4x = mouseX;
            b4y = mouseY;
        }

        push();

        if (selectedCreature != null) {
            settings.scaleFactor = float(2.0);
            translateX = int(-scaleFactor * selectedCreature.locationX + hPix(850));
            translateY = int(-scaleFactor * selectedCreature.locationY + hPix(850));
        }
        translate(translateX, translateY);
        scale(float(settings.scaleFactor));
        colorMode(RGB);
        background(100);
        fill(60);
        rect(wPix(8), 0, wPix(6), hPix(10));
        fill(255, 255, 255);
        textSize(wPix(30));

        // Draw world tiles and creatures
        drawWorld();

        pop();
    }

    //test()
    //menu.drawMenu(this);
};

function progress() {

}

function windowResized() {
    resizeCanvas(0.95 * windowWidth, 0.9 * windowHeight);
}