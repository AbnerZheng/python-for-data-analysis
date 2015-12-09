import java.text.DecimalFormat;

/** 
 * @date 2015Äê11ÔÂ26ÈÕ 
 * 
 * @author yuhuan
 *  
 */
public class TestClass {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int i = 31;
		int j = 23;
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(6);
		df.setMinimumFractionDigits(6);
		String k = df.format(i / j);
		System.out.println(k);
	}

}
