import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/** 
 * @date 2015年11月24日 
 * 
 * @author yuhuan
 *  
 */
public class BigFileDensity {
	
	public static String outFile = "data/densitydata.txt";
	public static String PATH = "data/rtdata.txt";
	public static LinkedBlockingQueue<List<String>> listQueue = new LinkedBlockingQueue<List<String>>();
	public static double density = 0.1;
	public static int oneTimeNum = 100000;

	public static void main(String[] args) throws Exception {
		new BigFileDensity().start();	
	}
	
	public void start() throws Exception{
		ExecutorService exe = Executors.newSingleThreadExecutor();
		exe.execute(new Writer(outFile));
		densityData();
	}
	
	/**
	 * 总共有：186855000行      最大的时间片编号为：653     最大用户编号为：624      最大的服务编号为：999
	 * @throws IOException
	 */
	public void densityData() throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(
	              new FileInputStream(PATH)));
        String tag = "";
        Random random = new Random();
    	List<String> listData = new LinkedList<String>();
        int count = 0;
        int residue = 0;
        int times = 0;
        
 	    while ((tag = br.readLine()) != null) {
 	    	double value = random.nextDouble();
 		    String[] subTags = tag.split(",");
 		    String line = "";
 	    	if(value < density){
 	    		line = subTags[0] + "," + subTags[1] + "," + subTags[2] + "," + subTags[3] + "\n";
 	    	}
 	    	else{
 	    		line = subTags[0] + "," + subTags[1] + "," + subTags[2] + "," + "-2" + "\n";
 	    	}
 		    listData.add(line);
 		    count++;
 		    //每个listData放oneTimeNum(100000)条数据
 		    if(listData.size() == oneTimeNum){
 		    	listQueue.add(listData);
 		    	times++;
 		    	//clean listData
 	 		    listData = new LinkedList<String>();
 	 		    System.out.println("打印次数"+times);
 		    }
 		    //剩余的不足oneTimeNum条的数据
 		    if(count > 186800000){
 		    	residue++;
 		    }
 		    if(residue == 55000){
 		    	listQueue.add(listData);
 	 		    System.out.println("residueTimeNum"+residue);
 		    }
        }
 	    br.close();
	}
	
	/**
	 * 写文件线程
	 * @author huan
	 *
	 */
    class Writer implements Runnable{
    	
		private File file;
        private BufferedWriter writer;
		
		 public Writer(String path) throws FileNotFoundException{
			 file = new File(path);
			 writer = new BufferedWriter(new OutputStreamWriter(
	                    new FileOutputStream(file, true)));
		 }
		 
		@Override
		public void run(){
			while(true){
				List<String> lines = null;
				try {
					lines = listQueue.take();
					try{
						for(String line : lines){	
							writer.append(line);
						}
						writer.flush();
						lines.clear();
					}catch(IOException ioe){
						ioe.printStackTrace();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
