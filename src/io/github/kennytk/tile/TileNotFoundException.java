package io.github.kennytk.tile;

public class TileNotFoundException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TileNotFoundException(String message)
	{
		super(message);
	}

	public TileNotFoundException(String message, Throwable throwable)
	{
		super(message, throwable);
	}
}
