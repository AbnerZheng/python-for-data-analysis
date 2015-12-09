
public class Point 
{
	private double coordinatex;
	private int clusternum;
	
	public Point(double x)
	{
		this.coordinatex = x;
	}
	
	public Point(double x, int clusternum)
	{
		this.coordinatex = x;
		this.clusternum = clusternum;
	}
	
	public double getX()
	{
		return this.coordinatex;
	}
	
	public int getClusternum()
	{
		return this.clusternum;
	}
	
	public void setClusternum(int clusternum)
	{
		this.clusternum = clusternum;
	}
	
	public String toString()
	{
		return "Point" + "(" + coordinatex + ") belongs to cluster " + clusternum;
	}
	
}
