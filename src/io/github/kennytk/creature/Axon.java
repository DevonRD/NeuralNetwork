package io.github.kennytk.creature;

public class Axon
{
	double weight;

	public Axon()
	{
		this.weight = Math.random() * 4.0 - 2;
	}

	public Axon(double weight)
	{
		this.weight = weight;
	}
}
