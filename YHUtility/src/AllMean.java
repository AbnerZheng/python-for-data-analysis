import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

/** 
 * @date 2015��11��27�� 
 * 
 * @author yuhuan
 *  
 */
public class AllMean {
	
	public String PATH = "data/rtdata.txt";
	public String USERMEAN = "data/usermean.txt";
	public String TIMESLOTSMEAN = "data/timeslotsmean.txt";
	public String SERVICEMEAN = "data/servicemean.txt";
	//����û��ķ���ʱ��
	public Map<String, Double> userMap1= new HashMap<String,Double>();
	//����û��ķ�������
	public Map<String, Integer> userMap2  = new HashMap<String,Integer>();
	//����ʱ��Ƭ�ϵķ���ʱ��
	public Map<String, Double> timeMap1= new HashMap<String,Double>();
	//���ʱ��Ƭ�ϵķ�������
	public Map<String, Integer> timeMap2  = new HashMap<String,Integer>();
	//���ڷ����ϵķ���ʱ��
	public Map<String, Double> serviceMap1= new HashMap<String,Double>();
	//���ʱ �ϵķ�������
	public Map<String, Integer> serviceMap2  = new HashMap<String,Integer>();
	
	public static void main(String[] args) throws IOException{
		AllMean means = new AllMean();
		//�����û���ƽ����Ӧʱ��
		//means.initUserMaps();
		//means.userMean();
		//����ʱ��Ƭ�ϵ�ƽ����Ӧʱ��
		means.initTimeMaps();
		means.timeSlotsMean();
		//��������ƽ����Ӧʱ��
		means.initSerivceMaps();
		means.serviceMean();
	}
	
	/**
	 * ���û���ƽ����Ӧʱ��
	 * �ܹ��У�186855000��      ����ʱ��Ƭ���Ϊ��653     ����û����Ϊ��624      ���ķ�����Ϊ��999
	 * @throws IOException 
	 */
	public void initUserMaps(){
		for(int i = 0; i < 625 ; i++){
			String userID = Integer.toString(i);
			userMap1.put(userID, 0.0);
			userMap2.put(userID, 0);
		}
		System.out.println("Map��ʼ�����");
	}
	
	public void initTimeMaps(){
		for(int i = 0; i < 654 ; i++){
			String timeID = Integer.toString(i);
			timeMap1.put(timeID, 0.0);
			timeMap2.put(timeID, 0);
		}
		System.out.println("Map��ʼ�����");
	}
	
	public void initSerivceMaps(){
		for(int i = 0; i < 1000 ; i++){
			String serviceID = Integer.toString(i);
			serviceMap1.put(serviceID, 0.0);
			serviceMap2.put(serviceID, 0);
		}
		System.out.println("Map��ʼ�����");
	}
	
	public void userMean() throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(PATH)));
		String tag = "";
		int count = 0;
		File file = new File(USERMEAN);
		if(file.exists()){
			file.delete();
		}
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file, true)));
		
		while((tag = br.readLine()) != null){
			String[] subTag = tag.split(",");
			double temp = Double.parseDouble(subTag[3]);
			String id = subTag[1];
			double sum = userMap1.get(id) + temp;
			userMap1.put(id, sum);
			int sNum = userMap2.get(id) + 1;
			userMap2.put(id, sNum);
			count++;
			System.out.println("���ڴ�����к�Ϊ��" + count);
		}
		
		//�����������ݣ�0��624���û������Щ�²����ڵ�
		for(int i = 0; i < 625 ; i++){
			String userID = Integer.toString(i);
			if(userMap2.get(userID) <= 0){
				 userMap1.remove(userID);
				 userMap2.remove(userID);
			}
			else {
				double sum = userMap1.get(userID);
				int num = userMap2.get(userID);
				double mean = sum / num;
				System.out.println("�û�" + userID + "��ƽ����Ӧʱ��Ϊ"+ mean);
				String line = userID + "," + mean + "\n";
				out.append(line);
				out.flush();
			}
		}
		br.close();
		out.close();
	}
	
    public void timeSlotsMean() throws IOException{
    	
    	BufferedReader br = new BufferedReader(new InputStreamReader(
    			new FileInputStream(PATH)));
		String tag = "";
		int count = 0;
		File file = new File(TIMESLOTSMEAN);
		if(file.exists()){
			file.delete();
		}
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
	            new FileOutputStream(file, true)));
		
		while((tag = br.readLine()) != null){
			String[] subTag = tag.split(",");
			double temp = Double.parseDouble(subTag[3]);
			String id = subTag[0];
			double sum = timeMap1.get(id) + temp;
			timeMap1.put(id, sum);
			int sNum = timeMap2.get(id) + 1;
			timeMap2.put(id, sNum);
			count++;
			System.out.println("���ڴ�����к�Ϊ��" + count);
		}
		
		for(int i = 0; i < 654 ; i++){
			String timeID = Integer.toString(i);
			if(timeMap2.get(timeID) <= 0){
				 timeMap1.remove(timeID);
				 timeMap2.remove(timeID);
			}
			else {
				double sum = timeMap1.get(timeID);
				int num = timeMap2.get(timeID);
				double mean = sum / num;
				System.out.println("ʱ��Ƭ" + timeID + "��ƽ����Ӧʱ��Ϊ"+ mean);
				String line = timeID + "," + mean + "\n";
				out.append(line);
				out.flush();
			}
		}
		timeMap1.clear();
		timeMap2.clear();
		br.close();
		out.close();
	}
    
    public void serviceMean() throws IOException{
    	BufferedReader br = new BufferedReader(new InputStreamReader(
    			new FileInputStream(PATH)));
		String tag = "";
		int count = 0;
		File file = new File(SERVICEMEAN);
		if(file.exists()){
			file.delete();
		}
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
	            new FileOutputStream(file, true)));
		
		while((tag = br.readLine()) != null){
			String[] subTag = tag.split(",");
			double temp = Double.parseDouble(subTag[3]);
			String id = subTag[2];
			double sum = serviceMap1.get(id) + temp;
			serviceMap1.put(id, sum);
			int sNum = serviceMap2.get(id) + 1;
			serviceMap2.put(id, sNum);
			count++;
			System.out.println("���ڴ�����к�Ϊ��" + count);
		}
		
		for(int i = 0; i < 1000 ; i++){
			String serviceID = Integer.toString(i);
			if(serviceMap2.get(serviceID) <= 0){
				 serviceMap1.remove(serviceID);
				 serviceMap2.remove(serviceID);
			}
			else {
				double sum = serviceMap1.get(serviceID);
				int num = serviceMap2.get(serviceID);
				double mean = sum / num;
				System.out.println("����" + serviceID + "��ƽ����Ӧʱ��Ϊ"+ mean);
				String line = serviceID + "," + mean + "\n";
				out.append(line);
				out.flush();
			}
		}
		serviceMap1.clear();
		serviceMap2.clear();
		br.close();
		out.close();
	}
}

