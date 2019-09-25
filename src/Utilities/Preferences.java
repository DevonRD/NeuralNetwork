package Utilities;

import java.text.DecimalFormat;

import Essentials.Run;

public class Preferences
{
	/** useful notes **/
	// asdf
	
	
	/** computer-based **/
	// pixel distance from left & right side of application to edge of monitor
	public final static int APP_WIDTH_SUBTRACTION_FACTOR = 68;
	
	// pixel distance from top & bottom side of application to edge of monitor
	public final static int APP_HEIGHT_SUBTRACTION_FACTOR = 112;
	
	// default text size for application
	public final static int DEFAULT_TEXTSIZE = 50;
	
	// starting application scale factor (visuals)
	public final static double DEFAULT_SCALE_FACTOR = 0.26;
	
	
	/** runtime preferences **/
	// default number to maintain the population
	public final static int START_MAINTAIN_NUM = 50;
	
	// automatically maintain population at the start?
	public final static boolean MAINTAIN_DEFAULT = true;
	
	// base mutation chance
	public final static double MUTATE_CHANCE = 0.5;
	
	// chance of a large mutation in a creature's genes
	public final static double SUPER_MUTATE_CHANCE = 0.05;
	
	// number of default-generated creatures on application start
	public final static int START_NUM_CREATURES = 50;
	
	// amount of time that passes with each code iteration while ON
	public final static double GAME_SPEED = 0.1;
	
	// attempted FPS target
	public final static int FRAMERATE = 60;
	
	// should a creature die if they try to give birth without having enough mass?
	// using TRUE could significantly slow the rate of evolution (I think)
	public final static boolean KILL_FOR_BIRTH_WITHOUT_MASS = false;
	
	// length & width of map, in tiles
	public final static int MAP_DIMENSIONS = 100;
	
	// how many code iterations until a dead tile can regen? default = 100
	public final static int TILE_COOLDOWN_THRESH = 100;
	
	// should the current gene pool be displayed as a bar above the gene pool history graph?
	// disabled by default because it's kind of pointless
	public final static boolean SHOW_CURRENT_POOL = false;
	
	// how big should the menu graphs get before throwing out older data?
	public final static int DISPLAY_GRAPH_SIZE = 150;
	
	
	/** functions, don't edit **/
	// general sigmoid function for neural network
	public static double sigmoid(double x)
	{
		return (2.0 / (1 + Math.pow(Math.E, -(x / 1.0)))) - 1.0;
	}
	
	// slightly modified sigmoid function for reproduction color transformation (Menu)
	public static double colorSigmoid(double x)
	{
		return (2.0 / (1 + Math.pow(Math.E, -(x / 1.5)))) - 1.0;
	}
	
	// distance formula
	public static double distBtCoords(double x1, double y1, double x2, double y2)
	{
		return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}
	
	// conversion from pixels to proportion of the sceren lengthwise
	public static int p2pl(double frac)
	{
		return (int) (frac / 2600.0 * Run.appWidth);
	}
	
	// conversion from pixels to proportion of the sceren widthwise (height)
	public static int p2pw(double frac)
	{
		return (int) (frac / 1600.0 * Run.appHeight);
	}
	
	
	/** universal **/
	// UI number rounding
	public static DecimalFormat formatDecimal = new DecimalFormat("##.##");
	public static DecimalFormat formatInteger = new DecimalFormat("##");
	
	// ordered list of the available maps
	public final static String[] MAPS = 
		{"map1", "europe", "Large_Island", "Three_Islands", "All_Land", "All_Water"};
	
}
