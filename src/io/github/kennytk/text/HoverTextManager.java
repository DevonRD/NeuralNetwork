package io.github.kennytk.text;

public class HoverTextManager
{
	public static HoverTextManager HTM;
	
	private HoverTextManager()
	{
		
	}
	
	public HoverTextManager getInstance()
	{
		if(HTM == null)
		{
			HTM = new HoverTextManager();
		}
		
		return HTM;
	}
}
