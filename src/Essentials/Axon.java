package Essentials;

public class Axon
{
	double weight;
	
	public Axon()
	{
		this.weight = Math.random() * 2.0 - 1;
	}
	public Axon(double weight)
	{
		this.weight = weight;
	}
}