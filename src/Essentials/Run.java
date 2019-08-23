package Essentials;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import Creature.Creature;
import Creature.CreatureManager;
import Utilities.Variables;
import Utilities.Menu;
import World.Tile;
import World.TileManager;
import processing.core.PApplet;
import processing.event.MouseEvent;

public class Run extends PApplet
{
	Manager manager;
	Menu menu;
	int rawTime;
	public static double displayTime;
	public static int appWidth, appHeight;
	double timeInterval;
	public static Tile selectedTile;
	public static Creature selectedCreature;
	boolean spawnClicking;
	
	public static boolean play;
	public static boolean showMenu;
	public static boolean maintain;
	public static boolean drawGenePoolGraph;
	public static int maintainNum = Variables.START_MAINTAIN_NUM;
	
	static int startNumCreatures;
	public static int creatureDeaths;
	public static int maxObservedCreatures;
	public static boolean spawnMode;
	double scaleFactor;
	int translateX, translateY;
	int delta;
	int b4x, b4y;
	int deltaX, deltaY;
	
	BufferedImage map;
	public static boolean[][] waterTiles;
	static String[] mapOptions = Variables.MAPS;
	static String fileExt = ".jpg";
	static String selectedMap;
	
	final double MUTATE_CHANCE = Variables.MUTATE_CHANCE;
	public static int forcedSpawns = 0;
	public static int superMutations = 0;
	
	public static void main(String[] args)
	{
		JFrame frame = new JFrame("Input Dialog");
		selectedMap = (String) JOptionPane.showInputDialog(frame, "Select a map to use.", "Map Selector", JOptionPane.QUESTION_MESSAGE, null, mapOptions, mapOptions[0]);
		PApplet.main("Essentials.Run");
	}
	public void settings()
	{
		// appWidth and appHeight are to be pulled from p2p functions
		// in other classes, use height and width otherwise 
		appWidth = displayWidth - 2 * Variables.APP_WIDTH_SUBTRACTION_FACTOR;
		appHeight = displayHeight - 2 * Variables.APP_HEIGHT_SUBTRACTION_FACTOR;
		size(appWidth, appHeight);
	}
	public void setup()
	{
		frameRate(Variables.FRAMERATE);
		textSize(Variables.DEFAULT_TEXTSIZE);
		scaleFactor = Variables.DEFAULT_SCALE_FACTOR;
		
		waterTiles = new boolean[100][100];
		try
		{
			waterTiles = setupWaterMap();
		}
		catch(IOException e)
		{
			System.out.println("catch error in setupMap");
			e.printStackTrace();
		}
		startNumCreatures = Variables.START_NUM_CREATURES;
		timeInterval = Variables.GAME_SPEED;
		manager = new Manager(this, startNumCreatures, width, height, waterTiles, MUTATE_CHANCE);
		menu = new Menu();
		menu.menuInit(this);
		
		selectedTile = null;
		selectedCreature = null;
		
		creatureDeaths = 0;
		maxObservedCreatures = startNumCreatures;
		
		spawnClicking = spawnMode = showMenu = false;
		play = drawGenePoolGraph = true;
		maintain = Variables.MAINTAIN_DEFAULT;
		
		translateX = translateY = 20;
		b4x = b4y = 0;
		deltaX = deltaY = 0;
		displayTime = 0;
	}
	
	public void draw()
	{
		if(Menu.path != Menu.MenuPath.CREATURE) selectedCreature = null;
		if(mousePressed)
		{
			b4x = mouseX;
			b4y = mouseY;
		}
		if(play)
		{	
			rawTime++;
			displayTime += timeInterval;
			manager.iterate(timeInterval); // tiles then creatures
			checkForDeaths();
			
			if(CreatureManager.creatures.size() > maxObservedCreatures) maxObservedCreatures = CreatureManager.creatures.size();
			if(rawTime % 30 == 0)
			{
				menu.updateHistoryArrays();
			}			
			if(maintain)
			{
				while(CreatureManager.creatures.size() < maintainNum)
				{
					forcedSpawns++;
					CreatureManager.addCreature();
				}
			}
			superMutations = 0;
			for(int i = 0; i < CreatureManager.creatures.size(); i++)
			{
				if(CreatureManager.creatures.get(i).superMutate) superMutations++;
			}
		}
		pushMatrix();
		if(selectedCreature != null)
		{
			scaleFactor = 2.0f;
			translateX = (int) (-scaleFactor * selectedCreature.locationX + p2pw(850));
			translateY = (int) (-scaleFactor * selectedCreature.locationY + p2pw(850));
		}
		translate(translateX, translateY);
		scale((float) scaleFactor);
		colorMode(RGB);
		background(100);
		fill(60);
		rect(p2pl(8), 0, p2pl(6), p2pw(10));
		fill(255, 255, 255);
		textSize(p2pl(30));
		drawTiles();
		drawCreatures();
		popMatrix();
		
		menu.drawMenu(this);
	}
	
	public int p2pl(double frac)
	{
		double returnPixels = 0;
		returnPixels = frac / 2600.0 * width;
		return (int) returnPixels;
	}
	public int p2pw(double frac)
	{
		double returnPixels = 0;
		returnPixels = frac / 1600.0 * height;
		return (int) returnPixels;
	}

	public void drawTiles()
	{
		for(int x = 0; x < TileManager.tiles.length; x++)
		{
			for(int y = 0; y < TileManager.tiles.length; y++)
			{
				colorMode(HSB, 360, 100, 100);
				stroke(TileManager.tiles[y][x].colorH, TileManager.tiles[y][x].colorS, TileManager.tiles[y][x].colorV - 10);
				fill(TileManager.tiles[y][x].colorH, TileManager.tiles[y][x].colorS, TileManager.tiles[y][x].colorV);
				rect(TileManager.tiles[y][x].x, TileManager.tiles[y][x].y, TileManager.tileSize, TileManager.tileSize);
				fill(0, 0, 0);
				stroke(0);
			}
		}
		colorMode(RGB, 255, 255, 255);
	}
	
	public void drawCreatures()
	{
		colorMode(RGB);
		fill(255);
		for(int i = 0; i < CreatureManager.creatures.size(); i++)
		{
			Creature c = CreatureManager.creatures.get(i);
			stroke(50);
			line((int)c.locationX, (int)c.locationY, (int)c.leftSensorX, (int)c.leftSensorY);
			if(c.outputNeurons[3] > 0.0) stroke(180, 0, 0);
			line((int)c.locationX, (int)c.locationY, (int)c.midSensorX, (int)c.midSensorY);
			stroke(50);
			line((int)c.locationX, (int)c.locationY, (int)c.rightSensorX, (int)c.rightSensorY);
			stroke(0);
			fill(c.color.hashCode());
			if(selectedCreature != null && CreatureManager.creatures.get(i).ID == selectedCreature.ID)
			{
				stroke(240, 0, 255);
				strokeWeight(7);
			}
			ellipse((int)c.locationX, (int)c.locationY, p2pw(c.diameter), p2pw(c.diameter));
			fill(255);
			stroke(0);
			strokeWeight(1);
			colorMode(HSB, 360, 100, 100);
			fill(c.leftSensorColor, 80, 45);
			ellipse((int)c.leftSensorX, (int)c.leftSensorY, p2pw(15), p2pw(15));
			fill(c.rightSensorColor, 80, 45);
			ellipse((int)c.rightSensorX, (int)c.rightSensorY, p2pw(15), p2pw(15));
			fill(c.mouthSensorColor, 80, 45);
			//ellipse((int)c.mouthSensorX, (int)c.mouthSensorY, p2pw(15), p2pw(15));
			colorMode(RGB, 255, 255, 255);
			fill(0);
		}
	}
	
	public void tileSelected(int yIndex, int xIndex)
	{
		selectedTile = TileManager.tiles[yIndex][xIndex];
	}
	
	public void checkForDeaths()
	{
		for(int i = 0; i < CreatureManager.creatures.size(); i++)
		{
			if(CreatureManager.creatures.get(i).size < 30)
			{
				if(CreatureManager.creatures.get(i) == selectedCreature) Menu.path = Menu.MenuPath.GENERAL;
				CreatureManager.creatures.remove(i);
				creatureDeaths++;
				return;
			}
			if(CreatureManager.creatures.get(i).size < 100)
			{
				if(CreatureManager.creatures.get(i) == selectedCreature) Menu.path = Menu.MenuPath.GENERAL;
				CreatureManager.creatures.remove(i);
				creatureDeaths++;
			}
		}
	}
	
	public void killAll()
	{
		while(!CreatureManager.creatures.isEmpty())
		{
			CreatureManager.creatures.remove(0);
		}
	}
	
	public void keyPressed()
	{
		if(key == 'r')
		{
			translateX = translateY = 20;
			scaleFactor = Variables.DEFAULT_SCALE_FACTOR;
			return;
		}
		else if(key == ' ')
		{
			play = !play;
		}
		else if(key == 's')
		{
			spawnMode = !spawnMode;
		}
		else if(key == 'k')
		{
			killAll();
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
		boolean check = false;
		
		if(showMenu)
		{
			if(mX <= p2pl(1600) && mY >= p2pw(360))
			{
				System.out.println("check menu");
				check = true;
				mX -= translateX;
				mY -= translateY;
				mX /= scaleFactor;
				mY /= scaleFactor;
			}
		}
		else
		{
			if(mX <= p2pl(1600))
			{
				System.out.println("check no menu");
				check = true;
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
		if(showMenu)
		{
			//test for button clicks
			if(Menu.killAll.clicked(mX, mY)) // kill all button
			{
				killAll();
				return;
			}
			if(Menu.spawn.clicked(mX, mY)) // spawn button
			{
				spawnMode = !spawnMode;
				return;
			}
			if(Menu.spawn20.clicked(mX, mY)) // spawn 20 button
			{
				for(int i = 0; i < 20; i++)
				{
					CreatureManager.addCreature();
				}
			}
			if(Menu.maintainAt.clicked(mX, mY)) // maintain at button
			{
				maintain = !maintain;
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
		}
		
		if(check)
		{
			System.out.println("into check if");
			if(spawnMode)
			{
				if(Menu.spawn.clicked(mX, mY)) spawnMode = !spawnMode;
				
				if(TileManager.checkWorldClick(mX, mY)) CreatureManager.addCreature(mX, mY);
				
				return;
			}
			
			System.out.println("not spawn mode");
			// test for creature click
			Creature clickedCreature = CreatureManager.checkCreatureClick(mX, mY);
			if(clickedCreature != null)
			{
				System.out.println("creature click detected");
				selectedCreature = clickedCreature;
				return;
			}
			
			// test for tile click
			Tile clickedTile = TileManager.checkTileClick(mX, mY);
			if(clickedTile != null)
			{
				System.out.println("tile click detected");
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
		Creature creatureSearch = CreatureManager.findCreatureID(frame);
		if(creatureSearch != null) selectedCreature = creatureSearch;
	}
	public boolean[][] setupWaterMap() throws IOException
	{
		boolean[][] results = new boolean[100][100];
		map = ImageIO.read(Run.class.getResource(selectedMap + fileExt));
		System.out.println("start setupWaterMap for map: " + selectedMap + fileExt);
		for(int row = 0; row < 100; row++)
		{
			for(int col = 0; col < 100; col++)
			{
				Color c = new Color(map.getRGB(col, row));
				if(c.getGreen() > 150) results[col][row] = false;
				else if(c.getBlue() > 30) results[col][row] = true;
				else results[col][row] = false;
			}
		}
		System.out.println("done setting up water map");
		return results;
	}
}
