package io.github.kennytk;

public class Maths
{
	public static double sigmoid(double x)
	{
		return (2.0 / (1 + Math.pow(Math.E, -(x / 1.5)))) - 1.0;
	}

	public static int scaleX(double frac)
	{
		double returnPixels = 0;
		returnPixels = frac / 1920.0 * Globals.realWidth;
		return (int) returnPixels;
	}

	public static int scaleY(double frac)
	{
		double returnPixels = 0;
		returnPixels = frac / 1080.0 * Globals.realHeight;
		return (int) returnPixels;
	}
}
