import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;


public class KMeans 
{
	private static int itemnum; 
	private static int K;
	private static ArrayList<Point> points = new ArrayList<Point>();
	private static ArrayList<Cluster> clusters = new ArrayList<Cluster>();
	private static double lastE = Double.MAX_VALUE;
	private static double curE = 0.0;
	
	private ArrayList<Integer> user = new ArrayList<Integer>() ;
	
	public KMeans(float[] URR){
		try
		{
			itemnum = URR.length;
			K = 2;

			for (int t=0;t<URR.length;t++){
				double x = URR[t];
				Point apoint = new Point(x);			//construct points array
				points.add(apoint);
			}
			
			for(int i = 0;i < K;i++)					//initialize K clusters and centroid
			{
				Cluster acluster = new Cluster(i, points.get(i));
				clusters.add(acluster);
			}
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
	}
	
	/*
	 * calculate which cluster each of the items belongs to
	 */
	public void calBelongs()
	{
		curE = 0;
		for(int i = 0;i < points.size();i++)
		{
			double distance = Double.MAX_VALUE;
			int clusternum = 0;
			for(int j = 0;j < clusters.size();j++)
			{
				if(distance > clusters.get(j).calDistance(points.get(i)))
				{
					distance = clusters.get(j).calDistance(points.get(i));
					clusternum = j;
				}
			}
			curE += distance;
			int oldcluster = points.get(i).getClusternum();
			points.get(i).setClusternum(clusternum);
			user.add(i, new Integer(clusternum));
			clusters.get(oldcluster).removePoint(points.get(i));
			clusters.get(clusternum).addPoint(points.get(i));
		}
	}
	
	/*
	 * recalculate the centroid of each cluster
	 */
	public static void calAllCentroids()
	{
		for(int i = 0;i < clusters.size();i++)
		{
			clusters.get(i).calcentroid();
		}
	}
	
	public static void printPoints()
	{
		for(int i = 0;i < points.size();i++)
		{
			System.out.println(points.get(i).toString());
		}
	}
	
	public static void printClusters()
	{
		for(int i = 0;i < clusters.size();i++)
		{
			System.out.println(clusters.get(i).toString());
		}
	}
	
	public static void printBelongs()
	{
		for(int i = 0;i < clusters.size();i++)
			clusters.get(i).printElements();
	}
	
	public  void cluster(){
		while(true){
			calBelongs();
			calAllCentroids();
			if(curE == lastE)
				break;
			lastE = curE;
		}
	}
	
	public int getSmallerClusterNum(){
		int min = clusters.get(0).getClusterSize();
		int index=0;
		for(int i=0;i<clusters.size();i++){
			if (min>clusters.get(i).getClusterSize()){
				min = clusters.get(i).getClusterSize();
				index = i;
			}
		}
		return index;
	}
	
	public ArrayList<Integer> getUnreliableUserList(){
		int smallerClusterNum = getSmallerClusterNum();
		ArrayList<Integer> unreliableUser = new ArrayList<Integer>() ;
		for(int i=0; i<itemnum; i++){
			if(user.get(i)==smallerClusterNum){
				unreliableUser.add(i);
			}
		}
		return unreliableUser;
	}
	
	/*public static void main(String[] args)
	{
		float[] URR = new float[339] ;
		int u=0;
		String fileroute = "src/URRCluster_d0.1r0.03.txt";
		try{
			FileReader fr = new FileReader(fileroute);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while(line != null){
				URR[u]=Float.parseFloat(line);
				u++;
				line = br.readLine();
			}
			fr.close();
			br.close();
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
		
		KMeans kMeans = new KMeans(URR);
		kMeans.cluster();
		System.out.println(kMeans.getUnreliableUserList());

	}*/

}
