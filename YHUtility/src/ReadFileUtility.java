import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

/** 
 * @date 2015��11��20�� 
 * 
 * @author yuhuan
 *  
 */
public class ReadFileUtility {
	
	public static String PATH = "data/rtdata.txt";
	
	/**
	 * FileInputStream���ļ����ж�ȡ����
	 * @param path
	 * @throws IOException
	 */
	public void FileInputStreamUtility(String path) throws IOException{
        File file=new File(path);
        if(!file.exists()||file.isDirectory()){
        	System.out.println("�ļ�������");
        }
        else{
        	 FileInputStream fis=new FileInputStream(file);
             byte[] buf = new byte[1024];
             StringBuffer sb=new StringBuffer();
             while((fis.read(buf))!=-1){
                 sb.append(new String(buf));    
                 buf=new byte[1024];//�������ɣ�������ϴζ�ȡ�������ظ�
             }
             fis.close();
        }
	}
	
	/**
	 * BufferedReader���ļ����ж�ȡ����
	 * @param path
	 * @throws IOException
	 */
	public void BufferedReaderUtility(String path) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(path)));
        String tag = "";
        int count = 0;
        
        while (((tag = br.readLine()) != null)&& count < 5000) {
        	count++;
            System.out.println("��������Ϊ��"+tag);
        }
        br.close();
	}
	
	/**
	 * ���ļ�β����ʼ��ȡ����
	 * @param path
	 * @throws IOException
	 */
	public void BufferedReaderEndUtility(String path) throws IOException{
		File file = new File(path);
		if(file.exists()){
			RandomAccessFile raf = new RandomAccessFile(file,"r");
			long len = raf.length();
			if(len == 0){
				System.out.println("�ļ�Ϊ��");
			}
			else{
				long pos = len - 1;
				System.out.println("pos="+pos);
				int count = 0;
				String line = null;
				while(pos > 0 && count < 1){
					pos--;
					raf.seek(pos);
					if(raf.readByte() == '\n'){
						System.out.println("pos="+pos);
						count++;
						break;
					}
					if(pos == 0){
						raf.seek(0);
					}
					byte[] bytes = new byte[(int)(len - pos)];
					raf.read(bytes);
					line = new String(bytes);
					//System.out.println(line);
				}
				System.out.println(line);
				raf.close();
			}
		}
		else{
			System.out.println("�ļ������ڻ��ߴ����쳣");
		}
	}
	
	public static void main(String[] args) throws IOException {
		ReadFileUtility reader = new ReadFileUtility();
		//reader.BufferedReaderEndUtility(PATH);
		reader.BufferedReaderUtility(PATH);
	}
}
