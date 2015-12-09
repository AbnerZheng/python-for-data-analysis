import java.sql.Time;


public class Tester {

	/**
	 * @param args
	 */
	Predictor predictor;
	
	public static void preProcess() {
		String prefix = "WSDream-QoSDataset2/";
		String matrix = "tpMatrix"; 
		int userNumber = 339; 
		int itemNumber = 5825;
		float[][] removedMatrix;
		float[][] randomedMatrix;
		URRPredictor predictor = new URRPredictor();
		
		
		float[][] originalMatrix = UtilityFunctions.readMatrix(prefix + matrix + ".txt", userNumber, itemNumber);

		originalMatrix = UtilityFunctions.readMatrix(prefix + matrix + ".txt", userNumber, itemNumber);
		float density = (float)0.1;
		float random = (float)0.005;
		
		//UtilityFunctions.writeMatrix(removedMatrix, "removed/rtMatrix" + density);
		for(int i = 1; i <=5; i++ ) {
			removedMatrix = UtilityFunctions.removeEntry(originalMatrix, density, "randomed//" + matrix + "30");			
			randomedMatrix = UtilityFunctions.randomEntry(removedMatrix, random);
			UtilityFunctions.writeMatrix(randomedMatrix, "randomed/rtMatrix" + density + "_" + random + "case" + i);
		}
		
	}
	public void test(float[][] originalMatrix, float density, float random, String matrix,int iteration, int topK, float factord, float lambda) {
		double[][] mae_rmse;
		int methodNum = 3;
		double[] MAE = new double[methodNum];
		double[] RMSE = new double[methodNum];
		float[][] removedMatrix;
		float[][] randomedMatrix;
		
		
		for(int i = 0; i < iteration; i++ ){
			System.out.println("Iteration " + i + ":");
			removedMatrix = UtilityFunctions.removeEntry(originalMatrix, density, "randomed//" + matrix + "30");
			randomedMatrix = UtilityFunctions.randomEntry(removedMatrix, random);
			
			mae_rmse = predictor.run8Methods(originalMatrix, randomedMatrix, random, topK, density,factord);
			
			for(int j = 0; j < methodNum; j ++) {
				MAE[j] += mae_rmse[j][0];
				RMSE[j] += mae_rmse[j][1];
			}
			//System.out.println("UIPCC:\t" + mae_rmse[0][0] + "\t" + mae_rmse[0][1]);
			//System.out.println("RAP:\t" + mae_rmse[1][0] + "\t" + mae_rmse[1][1]);
			//System.out.println("RAPC:\t" + mae_rmse[2][0] + "\t" + mae_rmse[2][1]);
			
		}
		for(int i = 0; i < methodNum; i++ ) {
			MAE[i] = MAE[i]/(double)iteration;
			RMSE[i] = RMSE[i]/(double)iteration;
		}
		//randomedMatrix= UtilityFunctions.readMatrix("randomed/rtMatrix" + density + "_" + random, userNumber, itemNumber);
		
		UtilityFunctions.writeFile("result.txt", "Density = " + density + "\tRandom = " + random +"\tfactor d= " + factord +  "\tTime: "
		+ new Time(System.currentTimeMillis())+ "\r\n");
		UtilityFunctions.writeFile("result.txt", "UIPCC:\t" + MAE[0] + "\t" + RMSE[0] + "\r\n");
		UtilityFunctions.writeFile("result.txt", "RAP:\t" + MAE[1] + "\t" + RMSE[1] + "\r\n");
		UtilityFunctions.writeFile("result.txt", "RAPC:\t" + MAE[2] + "\t" + RMSE[2] + "\r\n");
		//UtilityFunctions.writeFile("result.txt", "UMEAN:\t" + MAE[0] + "\t" + RMSE[0] + "\r\n");
		//UtilityFunctions.writeFile("result.txt", "IMEAN:\t" + MAE[1] + "\t" + RMSE[1] + "\r\n");
		//UtilityFunctions.writeFile("result.txt", "UPCC:\t" + MAE[2] + "\t" + RMSE[2] + "\r\n");
		//UtilityFunctions.writeFile("result.txt", "IPCC:\t" + MAE[3] + "\t" + RMSE[3] + "\r\n");
	}
	public static void main(String[] args) {
		//Tester.preProcess();
		Tester tester = new Tester();
		String prefix = "WSDream-QoSDataset2/";
		String matrix = "rtMatrix"; 
		int userNumber = 339; 
		int itemNumber = 5825;
		int iteration = 5;

		Predictor predictor = new Predictor();
		tester.predictor = predictor;
		float density = (float)0.1;
		float random = (float)0.03;
		int topK = 35;
		
		float[][] originalMatrix = UtilityFunctions.readMatrix(prefix + matrix + ".txt", userNumber, itemNumber);
		String userLoactionFileName = "userlist.txt";
		predictor.initUserLocationMap(prefix + userLoactionFileName);
		
		
		//topK = 45;
		//tester.test(originalMatrix,density,random,matrix,iteration,topK);
		
		float factord = (float)0.8;
		float lambda = (float) 0.2;
		//for (int i = 4; i < 11; i++) {
		//	lambda = (float) ((float)i/10.0);
			tester.test(originalMatrix,density,random,matrix,iteration,topK,factord,lambda);
		//}
		
		
		

	}

}
