package io.github.kennytk;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Maths
{
	private static DecimalFormat df = new DecimalFormat("##.##");
	
	public static double sigmoid(double x)
	{
		return (2.0 / (1 + Math.pow(Math.E, -(x / 1.5)))) - 1.0;
	}

	public static double netSigmoid(double x)
	{
		return (2.0 / (1 + Math.pow(Math.E, -(x / 2.0)))) - 1.0;
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
	
	public static String decimalFormat(double num)
	{
		df.setRoundingMode(RoundingMode.DOWN);
		return df.format(num);
	}
}
