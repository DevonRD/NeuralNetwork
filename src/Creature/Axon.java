package Creature;

public class Axon
{
	public double weight;
	
	public Axon()
	{
		this.weight = Math.random() * 2.0 - 1;
	}
	public Axon(double weight)
	{
		this.weight = weight;
	}
}