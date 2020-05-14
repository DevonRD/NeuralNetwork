package Essentials;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.commons.lang3.time.StopWatch;

import Creature.Creature;
import Creature.CreatureManager;
import Utilities.Prefs;
import Utilities.Menu;
import World.Tile;
import World.TileManager;
import processing.core.PApplet;
import processing.event.MouseEvent;

public class Run extends PApplet
{
	Manager manager;
	Menu menu;
	
	// Time variables
		int rawTime;
		double timeInterval;
		static double displayTime;
		StopWatch stopwatch;
	
	// General variables
		double scaleFactor;
		int translateX, translateY, delta, b4x, b4y, deltaX, deltaY;
		public static int appWidth, appHeight, maintainNum, startNumCreatures, forcedSpawns, superMutations;
		public static boolean play, showMenu, maintainPop, drawGenePoolGraph, showCreatureInfo, spawnMode, spawnClicking, saveFPS;
	
	// Currently selected
		public static Tile selectedTile;
		public static Creature selectedCreature;
	
	// Map variables
	// Note: To add more maps, simply add the .jpg file to the "Maps" folder.
		BufferedImage map;
		public static boolean[][] waterTiles;
		static File[] mapOptions;
		static File selectedMap;
	
	public static void main(String[] args)
	{
		PApplet.main("Essentials.Run");
	}
	
	public void settings()
	{
		// appWidth and appHeight are used in p2p functions (see Utilities.Prefs)
		// in other classes, use height and width 
		appWidth = displayWidth - 2 * Prefs.APP_WIDTH_SUBTRACTION_FACTOR;
		appHeight = displayHeight - 2 * Prefs.APP_HEIGHT_SUBTRACTION_FACTOR;
		size(appWidth, appHeight);
	}
	
	public void setup()
	{
		stopwatch = new StopWatch();
		frameRate(Prefs.FRAMERATE);
		textSize(Prefs.DEFAULT_TEXTSIZE);
		
		timeInterval = Prefs.GAME_SPEED;
		maintainPop = Prefs.MAINTAIN_DEFAULT;
		maintainNum = Prefs.START_MAINTAIN_NUM;
		scaleFactor = Prefs.DEFAULT_SCALE_FACTOR;
		startNumCreatures = Prefs.START_NUM_CREATURES;
		
		loadMap();
		
		manager = new Manager();
		menu = new Menu();
		menu.menuInit(this);
		
		selectedTile = null;
		selectedCreature = null;
		
		spawnClicking = spawnMode = showMenu = showCreatureInfo = saveFPS = false;
		play = drawGenePoolGraph = true;
		
		forcedSpawns = superMutations = 0;
		translateX = translateY = 20;
		displayTime = b4x = b4y = deltaX = deltaY = 0;
	}
	
	public void draw()
	{
		if(Menu.path != Menu.MenuPath.CREATURE) selectedCreature = null;
		
		if(saveFPS)
		{
			if(play)
			{	
				progress();			
			}
			menu.drawMenu(this);
		}
		else
		{
			if(mousePressed)
			{
				b4x = mouseX;
				b4y = mouseY;
			}
			
			if(play)
			{	
				progress();			
			}
			
			pushMatrix();
			if(selectedCreature != null)
			{
				scaleFactor = 2.0f;
				translateX = (int) (-scaleFactor * selectedCreature.locationX + Prefs.p2pw(850));
				translateY = (int) (-scaleFactor * selectedCreature.locationY + Prefs.p2pw(850));
			}
			translate(translateX, translateY);
			scale((float) scaleFactor);
			
			colorMode(RGB);
			background(100);
			fill(60);
			rect(Prefs.p2pl(8), 0, Prefs.p2pl(6), Prefs.p2pw(10));
			fill(255, 255, 255);
			textSize(Prefs.p2pl(30));
			manager.drawWorld(this);
			popMatrix();
			
			menu.drawMenu(this);
		}
	}
	
	public void progress()
	{
		rawTime++;
		displayTime += timeInterval;
		manager.iterate(timeInterval);
		
		if(rawTime % 60 == 0)
		{
			menu.updateHistoryArrays();
		}	
	}
	
	public void keyPressed()
	{
		// reset zoom and camera location
		if(key == 'r')
		{
			translateX = translateY = 20;
			scaleFactor = Prefs.DEFAULT_SCALE_FACTOR;
			return;
		}
		// pause and unpause
		else if(key == ' ')
		{
			play = !play;
		}
		// toggle creature spawning on click
		else if(key == 's')
		{
			spawnMode = !spawnMode;
		}
		// kill all creatures
		else if(key == 'k')
		{
			CreatureManager.killAll();
		}
	}
	
	public void mouseDragged(MouseEvent e)
    {
        translateX += mouseX - pmouseX;
        translateY += mouseY - pmouseY;
    }
	
	public void mouseClicked()
	{
		int mX = mouseX;
		int mY = mouseY;
		
		// clicked on world, not a menu, test for creature, tile, etc click
		boolean checkWorldClick = false;
		
		if(showMenu)
		{
			if(mX <= Prefs.p2pl(1600) && mY >= Prefs.p2pw(360))
			{
				if(Prefs.DEBUG_PRINTS) System.out.println("check menu");
				checkWorldClick = true;
				mX -= translateX;
				mY -= translateY;
				mX /= scaleFactor;
				mY /= scaleFactor;
			}
		}
		else
		{
			if(mX <= Prefs.p2pl(1600))
			{
				if(Prefs.DEBUG_PRINTS) System.out.println("check no menu");
				checkWorldClick = true;
				mX -= translateX;
				mY -= translateY;
				mX /= scaleFactor;
				mY /= scaleFactor;
			}
		}
		
		if(Menu.menuButton.clicked(mX, mY))
		{
			showMenu = !showMenu;
			return;
		}
		if(Menu.start.clicked(mX, mY)) // start button
		{
			play = !play;
			return;
		}
		if(selectedCreature != null && Menu.creatureInfo.clicked(mX, mY))
		{
			showCreatureInfo = !showCreatureInfo;
			return;
		}
		if(showMenu)
		{
			//test for button clicks
			if(Menu.killAll.clicked(mX, mY)) // kill all button
			{
				selectedCreature = null;
				Menu.path = Menu.MenuPath.GENERAL;
				Manager.killAll();
				return;
			}
			if(Menu.spawn.clicked(mX, mY)) // spawn button
			{
				spawnMode = !spawnMode;
				return;
			}
			if(Menu.spawn20.clicked(mX, mY)) // spawn 20 button
			{
				CreatureManager.spawnNumCreatures(20);
			}
			if(Menu.maintainAt.clicked(mX, mY)) // maintain at button
			{
				maintainPop = !maintainPop;
				return;
			}
			if(Menu.maintainPopNum.clicked(mX, mY)) // maintain number button
			{
				if(maintainNum == 1) maintainNum += 4;
				else maintainNum += 5;
				if(maintainNum > 50) maintainNum = 1;
				return;
			}
			if(Menu.findCreatureByID.clicked(mX, mY)) // find ID button
			{
				thread("findCreatureID");
				return;
			}
			if(Menu.saveFPS.clicked(mX, mY)) // FPS saver button
			{
				saveFPS = !saveFPS;
				return;
			}
		}
		
		if(checkWorldClick)
		{
			if(Prefs.DEBUG_PRINTS) System.out.println("checkWorldClick, spawnMode?");
			if(spawnMode)
			{
				if(Menu.spawn.clicked(mX, mY)) spawnMode = !spawnMode;
				
				if(TileManager.checkWorldClick(mX, mY)) CreatureManager.addCreature(mX, mY);
				
				return;
			}
			
			if(Prefs.DEBUG_PRINTS) System.out.println("not spawnMode");
			// test for creature click
			Creature clickedCreature = CreatureManager.checkCreatureClick(mX, mY);
			if(clickedCreature != null)
			{
				if(Prefs.DEBUG_PRINTS) System.out.println("creature click");
				selectedCreature = clickedCreature;
				return;
			}
			
			// test for tile click
			Tile clickedTile = TileManager.checkTileClick(mX, mY);
			if(clickedTile != null)
			{
				if(Prefs.DEBUG_PRINTS) System.out.println("tile click");
				selectedTile = clickedTile;
				return;
			}
			
		}
		Menu.path = Menu.MenuPath.GENERAL;
	}
	
	public void mouseWheel(MouseEvent e)
    {
        float delta = (float) (-e.getCount() > 0 ? 1.05 : -e.getCount() < 0 ? 1.0 / 1.05 : 1.0);
        scaleFactor *= delta;
        if (!(scaleFactor > 3.0) && !(scaleFactor < .2))
        {
            translateX -= mouseX;
            translateY -= mouseY;
            translateX *= delta;
            translateY *= delta;
            translateX += mouseX;
            translateY += mouseY;
        }
        if (scaleFactor > 3.0) scaleFactor = 3.0f;
        if (scaleFactor < .2) scaleFactor = .2f;
    }
	
	public void findCreatureID()
	{
		Creature tempSelectedCreature = CreatureManager.findCreatureID(frame);
		if(tempSelectedCreature != null) selectedCreature = tempSelectedCreature;
	}
	
	public void loadMap()
	{
		File mapFolder = new File("Maps");
		mapOptions = mapFolder.listFiles();
		System.out.println(mapOptions[0]);
		JFrame frame = new JFrame("Input Dialog");
		selectedMap = (File) JOptionPane.showInputDialog(frame, "Select a map to use.", "Map Selector", 
				JOptionPane.QUESTION_MESSAGE, null, mapOptions, mapOptions[0]);
		
		System.out.print("Generating map... ");
		stopwatch.start();
		try
		{
			waterTiles = new boolean[100][100];
			waterTiles = setupWaterMap();
		}
		catch(IOException e)
		{
			System.out.println("ERROR: Failed to generate selected map: see Run -> loadMap()");
			e.printStackTrace();
		}
		stopwatch.stop();
		System.out.println("Done. (" + stopwatch.getTime() + " ms)");
		stopwatch.reset();
	}
	
	public boolean[][] setupWaterMap() throws IOException
	{
		boolean[][] results = new boolean[100][100];
		int numLandTiles = 0;
		map = ImageIO.read(selectedMap);
		if(Prefs.DEBUG_PRINTS) System.out.println("start setupWaterMap for map: " + selectedMap.getName());
		for(int row = 0; row < 100; row++)
		{
			for(int col = 0; col < 100; col++)
			{
				Color c = new Color(map.getRGB(col, row));
				if(c.getGreen() > 150) results[col][row] = false;
				else if(c.getBlue() > 30) results[col][row] = true;
				else results[col][row] = false;
				if(results[col][row] == false) numLandTiles++;
			}
		}
		if(numLandTiles < 1)
		{
			JOptionPane.showMessageDialog(frame, "Error: You cannot load a map with no land tiles.");
			System.exit(1);
		}
		return results;
	}
	
	public static double getDisplayTime()
	{
		return displayTime;
	}
}
