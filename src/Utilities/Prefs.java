package Utilities;

import java.text.DecimalFormat;

import Essentials.Run;

public class Prefs
{
	/** useful notes **/
		// asdf
	
	
	/** computer-based **/
		// pixel distance from left & right side of application to edge of monitor
		// DEFAULT = 68
		public final static int APP_WIDTH_SUBTRACTION_FACTOR = 68;
		
		// pixel distance from top & bottom side of application to edge of monitor
		// DEFAULT = 112
		public final static int APP_HEIGHT_SUBTRACTION_FACTOR = 112;
		
		// default text size for application
		// DEFAULT = 50
		public final static int DEFAULT_TEXTSIZE = 50;
		
		// starting application scale factor (visuals)
		// DEFAULT = 0.26
		public final static double DEFAULT_SCALE_FACTOR = 0.26;
	
	
	/** runtime preferences **/
		// enable or disable various console print statements
		// DEFAULT = false
		public final static boolean DEBUG_PRINTS = false;
		
		// default number to maintain the population
		// DEFAULT = 50
		public final static int START_MAINTAIN_NUM = 50;
		
		// automatically maintain population at the start?
		// DEFAULT = true
		public final static boolean MAINTAIN_DEFAULT = true;
		
		// base mutation chance
		// DEFAULT = 0.5
		public final static double MUTATE_CHANCE = 0.5;
		
		// chance of a large mutation in a creature's genes
		// DEFAULT = 0.05
		public final static double SUPER_MUTATE_CHANCE = 0.05;
		
		// number of default-generated creatures on application start
		// DEFAULT = 50
		public final static int START_NUM_CREATURES = 50;
		
		// amount of time that passes with each code iteration while ON
		// DEFAULT = 0.1
		public final static double GAME_SPEED = 0.1;
		
		// attempted FPS target
		// DEFAULT = 60
		public final static int FRAMERATE = 60;
		
		// should a creature die if they try to give birth without having enough mass?
		// using TRUE could significantly slow the rate of evolution (I think)
		// DEFAULT = false
		public final static boolean KILL_FOR_BIRTH_WITHOUT_MASS = false;
		
		// length & width of map, in tiles
		// don't change unless you adjust other code accordingly
		// DEFAULT = 100
		public final static int MAP_DIMENSIONS = 100;
		
		// how fast should tiles regenerate food?
		// DEFAULT = 0.025
		public final static double TILE_REGEN_RATE = 0.025;
		
		// how many code iterations until a dead tile can regen?
		// DEFAULT = 100
		public final static int TILE_COOLDOWN_THRESH = 100;
		
		// how big should the menu graphs get before throwing out older data?
		// DEFAULT = 150
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
