import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/** 
 * @date 2015年11月26日 
 * 
 * @author yuhuan
 *  
 */
public class CompareFile {
	
	public static String PATH1 = "data/densitydata.txt";
	public static String PATH2 = "data/rtdata.txt";

	public static void main(String[] args) throws IOException {
		new CompareFile().compareFile(PATH1, PATH2);;
	}
	
	/**
	 * @param filePath1
	 * @param filePath2
	 * @throws IOException
	 */
	public void compareFile(String filePath1, String filePath2) throws IOException{
		
		BufferedReader br1 = new BufferedReader(new InputStreamReader(
				new FileInputStream(filePath1)));
		BufferedReader br2 = new BufferedReader(new InputStreamReader(
				new FileInputStream(filePath2)));
		String tag1 = "";
		String tag2 = "";
		int count = 0;
		
		while((tag1 = br1.readLine())!=null && (tag2 = br2.readLine())!=null){
			String[] subTag1 = tag1.split(",");
			String[] subTag2 = tag2.split(",");
			if(subTag1[0].equals(subTag2[0]) && subTag1[1].equals(subTag2[1]) && subTag1[2].equals(subTag2[2])){
				count++;
				System.out.println("第" + count + "行相等");
			}
			else{
				System.out.println("文件第" + count + "行不相等");
				break;
			}
		}
		br1.close();
		br2.close();
	}
}
