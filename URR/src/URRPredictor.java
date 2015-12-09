import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;


public class URRPredictor {
	ArrayList<Map> upccList;
	ArrayList<Map> ipccList;
	float[] URR_L1AVG;
	float[] URR_L2AVG;
	//float[] URR_imean;
	//float[] URR_umean;
	ArrayList unreliableUserList;
	HashMap userLocationMap; 

	
	public void run8Methods(float[][] originalMatrix, float[][] randomedMatrix, float random, int topK, int topR,double lambda){
		float[][] originalMatrixT = UtilityFunctions.matrixTransfer(originalMatrix);
		float[][] randomedMatrixT = UtilityFunctions.matrixTransfer(randomedMatrix);
		
		//calculate the mean failure rate of services that each user called
		float[] umean = UtilityFunctions.getUMean(randomedMatrix);
		
		//calculate the mean failure rate of users that called the same service
		float[] imean = UtilityFunctions.getUMean(randomedMatrixT);
			
		//URR_UPCC
		//float PUR = (float)0.05;
		//int number = (int)(originalMatrix.length * (float)random);
		System.out.println("Caculating URR_L1AVG: " + new Time(System.currentTimeMillis()));
		getURR_L1AVG(randomedMatrix, (float) 0.1, 1000, imean); 
		System.out.println("Identify unreliable user" + new Time(System.currentTimeMillis()));
		getUnreliableUserList(URR_L1AVG, topR);
		
		float[] URR_umean = getURR_umean(randomedMatrix, URR_L1AVG);
		System.out.println("calculating L1_UPCC: " + new Time(System.currentTimeMillis()));
		float[][] predictedMatrixURR_UPCC = URR_UPCC(originalMatrix, randomedMatrix,
				URR_umean, topK);
		double mae = UtilityFunctions.MAE(originalMatrix, randomedMatrix, predictedMatrixURR_UPCC);
		double rmse = UtilityFunctions.RMSE(originalMatrix, randomedMatrix, predictedMatrixURR_UPCC);
		UtilityFunctions.writeFile("result.txt", "L1_UPCC:\t" + mae + "\t" + rmse + "\r\n");
		
		//URR_IPCC
		
		//threashold = UtilityFunctions.getMean(URR);
		float[] URR_imean = getURR_imean(randomedMatrix, URR_L1AVG);		
		//ipccList = calculateURRipccList(originalMatrixT, randomedMatrixT, imean, true, false, 20, 100);
		System.out.println("calculating L1_IPCC: " + new Time(System.currentTimeMillis()));
		float[][] predictedMatrixURR_IPCC = URR_IPCC(originalMatrix, randomedMatrix, URR_imean, topK);
		//getUnreliableUserList(URR, number);
		//float[][] predictedMatrixURR_IPCC = URR_UPCC(originalMatrixT, randomedMatrixT,
		//				URR_umean, 100, false);
		mae = UtilityFunctions.MAE(originalMatrix, randomedMatrix, predictedMatrixURR_IPCC);
		rmse = UtilityFunctions.RMSE(originalMatrix, randomedMatrix, predictedMatrixURR_IPCC);
		UtilityFunctions.writeFile("result.txt", "L1_IPCC:\t" + mae + "\t" + rmse + "\r\n");
		
		System.out.println("calculating L1_UIPCC: " + new Time(System.currentTimeMillis()));
		float[][] predictedMatrixURR_IPCCT = UtilityFunctions.matrixTransfer(predictedMatrixURR_IPCC);
		double[] mae_urr_uipcc = new double[11]; 
		double[] rmse_urr_uipcc = new double[11]; 
		for (int i = 0; i < 11; i++) {
			double lambda2 = (double)i/10.0;
			float[][] predictedMatrixURR_UIPCC = UIPCC(predictedMatrixURR_UPCC, predictedMatrixURR_IPCCT, lambda2);
			mae_urr_uipcc[i] =   UtilityFunctions.MAE(originalMatrix, randomedMatrix, predictedMatrixURR_UIPCC);
			rmse_urr_uipcc[i] = UtilityFunctions.RMSE(originalMatrix, randomedMatrix, predictedMatrixURR_UIPCC);
//			System.out.println("UIPCC:" + i + "\t" + mae2[i] + "\t" + rmse2[i]);
		}
		
		double smallMAE = 100;
		double smallRMSE = 100;
		for (int i = 0; i < 11; i++) {
			if(mae_urr_uipcc[i] < smallMAE) smallMAE = mae_urr_uipcc[i];
			if(rmse_urr_uipcc[i] < smallRMSE) smallRMSE = rmse_urr_uipcc[i];
		}		
		UtilityFunctions.writeFile("result.txt", "L1_UIPCC:\t" + smallMAE + "\t" + smallRMSE + "\r\n");
		
		System.out.println("Caculating URR_L2AVG: " + new Time(System.currentTimeMillis()));
		getURR_L2AVG(randomedMatrix, (float) 0.1, 1000, imean); 
		System.out.println("Identify unreliable user" + new Time(System.currentTimeMillis()));
		unreliableUserList.clear();
		getUnreliableUserList(URR_L2AVG, topR);
		
		URR_umean = getURR_umean(randomedMatrix, URR_L2AVG);
		System.out.println("calculating L2_UPCC: " + new Time(System.currentTimeMillis()));
		float[][] predictedMatrixL2_UPCC = URR_UPCC(originalMatrix, randomedMatrix,
				URR_umean, topK);
		mae = UtilityFunctions.MAE(originalMatrix, randomedMatrix, predictedMatrixL2_UPCC);
		rmse = UtilityFunctions.RMSE(originalMatrix, randomedMatrix, predictedMatrixL2_UPCC);
		UtilityFunctions.writeFile("result.txt", "L2_UPCC:\t" + mae + "\t" + rmse + "\r\n");
		
		//URR_IPCC
		
		//threashold = UtilityFunctions.getMean(URR);
		URR_imean = getURR_imean(randomedMatrix, URR_L2AVG);		
		//ipccList = calculateURRipccList(originalMatrixT, randomedMatrixT, imean, true, false, 20, 100);
		System.out.println("calculating L2_IPCC: " + new Time(System.currentTimeMillis()));
		float[][] predictedMatrixL2_IPCC = URR_IPCC(originalMatrix, randomedMatrix, URR_imean, topK);
		
		mae = UtilityFunctions.MAE(originalMatrix, randomedMatrix, predictedMatrixL2_IPCC);
		rmse = UtilityFunctions.RMSE(originalMatrix, randomedMatrix, predictedMatrixL2_IPCC);
		UtilityFunctions.writeFile("result.txt", "L2_IPCC:\t" + mae + "\t" + rmse + "\r\n");
		
		System.out.println("calculating L2_UIPCC: " + new Time(System.currentTimeMillis()));
		float[][] predictedMatrixL2_IPCCT = UtilityFunctions.matrixTransfer(predictedMatrixL2_IPCC);
		mae_urr_uipcc = new double[11]; 
		rmse_urr_uipcc = new double[11]; 
		for (int i = 0; i < 11; i++) {
			double lambda2 = (double)i/10.0;
			float[][] predictedMatrixL2_UIPCC = UIPCC(predictedMatrixL2_UPCC, predictedMatrixL2_IPCCT, lambda2);
			mae_urr_uipcc[i] =   UtilityFunctions.MAE(originalMatrix, randomedMatrix, predictedMatrixL2_UIPCC);
			rmse_urr_uipcc[i] = UtilityFunctions.RMSE(originalMatrix, randomedMatrix, predictedMatrixL2_UIPCC);
//			System.out.println("UIPCC:" + i + "\t" + mae2[i] + "\t" + rmse2[i]);
		}
		
		smallMAE = 100;
		smallRMSE = 100;
		for (int i = 0; i < 11; i++) {
			if(mae_urr_uipcc[i] < smallMAE) smallMAE = mae_urr_uipcc[i];
			if(rmse_urr_uipcc[i] < smallRMSE) smallRMSE = rmse_urr_uipcc[i];
		}		
		UtilityFunctions.writeFile("result.txt", "L2_UIPCC:\t" + smallMAE + "\t" + smallRMSE + "\r\n");
		
		String fileName = "WSDream-QoSDataset2/" + "userlist.txt";
		userLocationMap = UtilityFunctions.getUserLocationMap(fileName);
	}
	
	

	public float[][] UIPCC(float[][] predictedMatrixUPCC, float[][] predictedMatrixIPCC, double lambda){
		float[][]result = new float[predictedMatrixUPCC.length][predictedMatrixUPCC[0].length];
		for (int i = 0; i < predictedMatrixUPCC.length; i++) {
			for (int j = 0; j < predictedMatrixUPCC[0].length; j++) {
				result[i][j] = (float)(lambda * predictedMatrixUPCC[i][j] + (1 - lambda) * predictedMatrixIPCC[j][i]);
			}
		}
		return result;
	}
	
	public float[][] URR_UPCC(float[][] originalMatrix, float[][] randomedMatrix, float[] umean, int topK) {
		float[][] predictedMatrix = new float[randomedMatrix.length][randomedMatrix[0].length];
	
		for (int i = 0; i < originalMatrix.length; i++) {
//			System.out.println(i);
			// get the pcc values of the current user with all other users. 
			HashMap pcc = new HashMap();
			
			for (int j = 0; j < originalMatrix.length; j++) {
				// the same user. 
				if(i == j) continue;
				
				// the user has no ratings, no similarity computation. 
				if(umean[i] == -2 || umean[j] == -2) continue; 
				
				if (unreliableUserList.contains(j))
					continue;
				
				double pccValue = 0;
				pccValue = getPCC(randomedMatrix[i], randomedMatrix[j], umean[i], umean[j]);

				
				// find similar users, 
				if(pccValue>0)	pcc.put(j, pccValue);
			}
//			System.out.println(i + "\t" + pcc.size());
			Map sortedPcc = sortByValue(pcc, topK);
			
			// predict values for each items in the line. 
			for (int j = 0; j < originalMatrix[0].length; j++) {
				// not removed entry, no need to predict. 
				if(randomedMatrix[i][j] != -2) continue; 
				
				// no original value for making evaluation, no need to predict. 
				if(originalMatrix[i][j] < 0) continue;
				
				int k = 0;
				double pccAll = 0; 
				double predictedValue = 0;
				Iterator it = sortedPcc.entrySet().iterator();
				while(k < topK && it.hasNext()){
					
					Map.Entry en = (Map.Entry)it.next();
					int userID = (Integer)en.getKey();
					
					if(unreliableUserList.contains(userID))
						continue;
					// if the similar user does not use this item previously, can not be used. 
					if(randomedMatrix[userID][j] == -2 || randomedMatrix[userID][j] == -1) continue;
										
					double userPCCValue = (Double)en.getValue();
					pccAll += userPCCValue;
					k++;
					
					predictedValue += (userPCCValue) * (originalMatrix[userID][j] - umean[userID]);
				}
				
				// no similar users, use umean. 
				if(pccAll == 0) {
					predictedValue = umean[i];
				} else{ 
					predictedValue = predictedValue/pccAll + umean[i];
				}
				
				
				// will become worst, no need. 
				if(predictedValue <= 0) predictedValue = 0;
				
				predictedMatrix[i][j] = (float)predictedValue;
			}
		}
	
		return predictedMatrix;
	}	
	
	public float[][] URR_IPCC(float[][] originalMatrix, float[][] randomedMatrix, float[] imean, int topK) {
		float[][] predictedMatrix = new float[randomedMatrix.length][randomedMatrix[0].length];
		float[][] randomedMatrixT = UtilityFunctions.matrixTransfer(randomedMatrix);
		
		for (int j = 0; j < originalMatrix[0].length; j++) {
 
			HashMap pcc = new HashMap();
			//get similar services of service j
			for (int i = 0; i < originalMatrix[0].length; i++) {
				// the same user. 
				if(j == i) continue;
				
				// the user has no ratings, no similarity computation. 
				if(imean[i] == -2 || imean[j] == -2) continue; 
				
				double pccValue = 0;
				
				pccValue = getURR_IPCC(randomedMatrixT[j], randomedMatrixT[i], imean[j], imean[i]);

				
				// find similar users, 
				if(pccValue>0)	pcc.put(i, pccValue);
			}
//			System.out.println(i + "\t" + pcc.size());
			Map sortedPcc = sortByValue(pcc, topK);
			
			
			 
			for (int i = 0; i < originalMatrix.length; i++) {
				// not removed entry, no need to predict. 
				if(randomedMatrix[i][j] != -2) continue; 
				
				// no original value for making evaluation, no need to predict. 
				if(originalMatrix[i][j] < 0) continue;
				
								
				int k = 0;
				double pccAll = 0; 
				double predictedValue = 0;
				Iterator it = sortedPcc.entrySet().iterator();
				while(it.hasNext()){
					
					Map.Entry en = (Map.Entry)it.next();
					int serviceID = (Integer)en.getKey();
					
					// if the similar user does not use this item previously, can not be used. 
					if(randomedMatrix[i][serviceID] == -2 || randomedMatrix[i][serviceID] == -1) continue;
					
										
					double userPCCValue = (Double)en.getValue();
					pccAll += userPCCValue;
					
					predictedValue += (userPCCValue) * (originalMatrix[i][serviceID] - imean[serviceID]);
				}
				
				// no similar users, use umean. 
				if(pccAll == 0) {
					predictedValue = imean[j];
				} else{ 
					predictedValue = predictedValue/pccAll + imean[j];
				}
				
				
				// will become worst, no need. 
				if(predictedValue <= 0) predictedValue = 0;
				
				predictedMatrix[i][j] = (float)predictedValue;
			}
				
		}
		
		
		return predictedMatrix;
	}
	
	
	/*public ArrayList<Map> calculateURRipccList(float[][] originalMatrix, float[][] removedMatrix, float[] umean, boolean isPCC, 
			boolean isSW, int swThreshold, int topK) {
		
		ArrayList<Map> pccList = new ArrayList<Map>();
		for (int i = 0; i < originalMatrix.length; i++) {
//			System.out.println(i);
			// get the pcc values of the current user with all other users. 
			HashMap pcc = new HashMap();
			
			for (int j = 0; j < originalMatrix.length; j++) {
				// the same user. 
				if(i == j) continue;
				
				// the user has no ratings, no similarity computation. 
				if(umean[i] == -2 || umean[j] == -2) continue; 
				
				double pccValue = 0;
				if(isPCC)
					pccValue = getURR_IPCC(removedMatrix[i], removedMatrix[j], umean[i], umean[j]);
				else
					pccValue = getVSS(removedMatrix[i], removedMatrix[j], isSW, swThreshold);
				
				// find similar users, 
				if(pccValue>0)	pcc.put(j, pccValue);
			}
//			System.out.println(i + "\t" + pcc.size());
			Map sortedPcc = sortByValue(pcc, topK);

			
		return pccList;	
	}*/
	
	public ArrayList<Map> calculateURRupccList(float[][] originalMatrix, float[][] removedMatrix, float[] umean, boolean isPCC, 
			boolean isSW, int swThreshold, int topK) {
		
		ArrayList<Map> pccList = new ArrayList<Map>();
		for (int i = 0; i < originalMatrix.length; i++) {
//			System.out.println(i);
			// get the pcc values of the current user with all other users. 
			HashMap pcc = new HashMap();
			
			for (int j = 0; j < originalMatrix.length; j++) {
				// the same user. 
				if(i == j) continue;
				
				// the user has no ratings, no similarity computation. 
				if(umean[i] == -2 || umean[j] == -2) continue; 
				
				if (unreliableUserList.contains(j))
					continue;
				
				double pccValue = 0;
				if(isPCC)
					pccValue = getPCC(removedMatrix[i], removedMatrix[j], umean[i], umean[j]);
				else
					pccValue = getVSS(removedMatrix[i], removedMatrix[j], isSW, swThreshold);
				
				// find similar users, 
				if(pccValue>0)	pcc.put(j, pccValue);
			}
//			System.out.println(i + "\t" + pcc.size());
			Map sortedPcc = sortByValue(pcc, topK);
			
			pccList.add(sortedPcc);
		}
			
		return pccList;	
	}
	
	/**
	 * 
	 * @param originalMatrix
	 * @param removedMatrix
	 * @param isSW
	 * @param swThreshold
	 */
	public float[][] UPCC(float[][] originalMatrix, float[][] removedMatrix, float[] umean, int topK, boolean isUPCC){
		
		float[][] predictedMatrix = new float[originalMatrix.length][originalMatrix[0].length];
		
		for (int i = 0; i < originalMatrix.length; i++) {
//			System.out.println(i);
			// get the pcc values of the current user with all other users. 
			HashMap pcc = new HashMap();
			
			for (int j = 0; j < originalMatrix.length; j++) {
				// the same user. 
				if(i == j) continue;
				
				// the user has no ratings, no similarity computation. 
				if(umean[i] == -2 || umean[j] == -2) continue; 
				
				double pccValue = 0;
				
					pccValue = getPCC(removedMatrix[i], removedMatrix[j], umean[i], umean[j]);

				
				// find similar users, 
				if(pccValue>0)	pcc.put(j, pccValue);
			}
//			System.out.println(i + "\t" + pcc.size());
			Map sortedPcc = sortByValue(pcc, topK);
			
			// predict values for each items in the line. 
			for (int j = 0; j < originalMatrix[0].length; j++) {
				// not removed entry, no need to predict. 
				if(removedMatrix[i][j] != -2) continue; 
				
				// no original value for making evaluation, no need to predict. 
				if(originalMatrix[i][j] < 0) continue;
				
				int k = 0;
				double pccAll = 0; 
				double predictedValue = 0;
				Iterator it = sortedPcc.entrySet().iterator();
				while(k < topK && it.hasNext()){
					
					Map.Entry en = (Map.Entry)it.next();
					int userID = (Integer)en.getKey();
					
					// if the similar user does not use this item previously, can not be used. 
					if(removedMatrix[userID][j] == -2 || removedMatrix[userID][j] == -1) continue;
					
					double userPCCValue = (Double)en.getValue();
					pccAll += userPCCValue;
					k++;
					
					predictedValue += (userPCCValue) * (originalMatrix[userID][j] - umean[userID]);
				}
				
				// no similar users, use umean. 
				if(pccAll == 0) {
					predictedValue = umean[i];
				} else{ 
					predictedValue = predictedValue/pccAll + umean[i];
				}
				
				
				// will become worst, no need. 
				if(predictedValue <= 0) predictedValue = 0;
				
				
//				if(predictedValue >= 1) predictedValue = 1;
				
//				System.out.println(predictedValue + "\t" + valueMatrix[i][j]);
				
				predictedMatrix[i][j] = (float)predictedValue;
			}
		}
		return predictedMatrix;
	}
	
	
	
	public Map sortByValue(Map map, int topK) {
		List list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
						.compareTo(((Map.Entry) (o2)).getValue());
			}
		});
		Collections.reverse(list);
		// logger.info(list);
		Map result = new LinkedHashMap();
		Iterator it = list.iterator();
		while (it.hasNext() && result.size() < topK) {
			Map.Entry entry = (Map.Entry) it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
	
	
	
	
	public float[][] UMEAN(float[][] originalMatrix, float[][] removedMatrix, float[] umean) {
		float[][] predictedMatrix = new float[originalMatrix.length][originalMatrix[0].length];
		for (int i = 0; i < originalMatrix.length; i++) {
			for (int j = 0; j < originalMatrix[0].length; j++) {
				// predict the remove entry and the original entry is not null. 
				if(removedMatrix[i][j] == -2 && originalMatrix[i][j] != -1) {
					if(umean[i] == -2) predictedMatrix[i][j] = -2;
					else predictedMatrix[i][j] = umean[i];
				}
			}
		}
		return predictedMatrix;
	}
	
	
	/**
	 * two vectors, and two means of the vectors.
	 * isWS: whether enable the significant weight. 
	 * swPercent: the threshold of the significant weight.  
	 *  
	 * @return
	 * -2 indicates this pcc value has problem and can't be used.
	 */
	public double getPCC(float[] u1, float[] u2, double mean1, double mean2){
		// get the index of the common rated items.
		Vector<Integer> commonRatedKey = new Vector<Integer>();
		
		for (int i = 0; i < u1.length; i++) {
			if(u1[i] >= 0 && u2[i] >= 0) {
				commonRatedKey.add(i);
			}
		}
		
		// no common rate items. 
		if(commonRatedKey.size() == 0 || commonRatedKey.size() == 1) 
			return -2;
		
		
		double pcc = 0;
		double upperAll = 0;
		double downAll1 = 0;
		double downAll2 = 0;

		for (int i = 0; i < commonRatedKey.size(); i++) {
			int key = commonRatedKey.get(i);
			double value1 = u1[key];
			double value2 = u2[key];
			
			double temp1 = value1 - mean1;
			double temp2 = value2 - mean2;
			
			if(temp1 < 0.00001 && temp1 > 0) temp1 = 0.00001;
			if(temp2 < 0.00001 && temp2 > 0) temp2 = 0.00001;

			if(temp1 > -0.00001 && temp1 < 0) temp1 = -0.00001;
			if(temp2 > -0.00001 && temp2 < 0) temp2 = -0.00001;
			
			upperAll += temp1 * temp2;
			downAll1 += temp1 * temp1;
			downAll2 += temp2 * temp2;
		}
		
		double downValue = Math.sqrt(downAll1 * downAll2);
		
		if(downValue == 0) 
			return -2;
		
		pcc = upperAll / downValue;
		
		//use significant weight to avoid the over estimation problem.
		// 10 is a parameter, which can be set.
		/*if(isSW && commonRatedKey.size() < swThreshold) {
			pcc = pcc * commonRatedKey.size() / swThreshold;
		}*/
		return pcc;
	}
	
	public double getURR_IPCC(float[] u1, float[] u2, double mean1, double mean2){
		// get the index of the common rated items.
		Vector<Integer> commonRatedKey = new Vector<Integer>();
		
		//remove unreliable user
		for (int i = 0; i < u1.length; i++) {
			if (unreliableUserList.contains(i))
				continue;
			if(u1[i] >= 0 && u2[i] >= 0 ) {
				commonRatedKey.add(i);
			}
		}
		
		// no common rate items. 
		if(commonRatedKey.size() == 0 || commonRatedKey.size() == 1) 
			return -2;
		
		
		double pcc = 0;
		double upperAll = 0;
		double downAll1 = 0;
		double downAll2 = 0;

		for (int i = 0; i < commonRatedKey.size(); i++) {
			int key = commonRatedKey.get(i);
			double value1 = u1[key];
			double value2 = u2[key];
			
			double temp1 = value1 - mean1;
			double temp2 = value2 - mean2;
			
			if(temp1 < 0.00001 && temp1 > 0) temp1 = 0.00001;
			if(temp2 < 0.00001 && temp2 > 0) temp2 = 0.00001;

			if(temp1 > -0.00001 && temp1 < 0) temp1 = -0.00001;
			if(temp2 > -0.00001 && temp2 < 0) temp2 = -0.00001;
			
			upperAll += temp1 * temp2;
			downAll1 += temp1 * temp1;
			downAll2 += temp2 * temp2;
		}
		
		double downValue = Math.sqrt(downAll1 * downAll2);
		
		if(downValue == 0) 
			return -2;
		
		pcc = upperAll / downValue;
		
		//use significant weight to avoid the over estimation problem.
		// 10 is a parameter, which can be set.
		/*if(isSW && commonRatedKey.size() < swThreshold) {
			pcc = pcc * commonRatedKey.size() / swThreshold;
		}*/
		return pcc;
	}
	
	
	public double getPCC(double[] u1, double[] u2, double mean1, double mean2, boolean isSW, int swThreshold){
		// get the index of the common rated items.
		Vector<Integer> commonRatedKey = new Vector<Integer>();
		
		for (int i = 0; i < u1.length; i++) {
			if(u1[i] >= 0 && u2[i] >= 0) {
				commonRatedKey.add(i);
			}
		}
		
		// no common rate items. 
		if(commonRatedKey.size() == 0 || commonRatedKey.size() == 1) 
			return -2;
		
		
		double pcc = 0;
		double upperAll = 0;
		double downAll1 = 0;
		double downAll2 = 0;

		for (int i = 0; i < commonRatedKey.size(); i++) {
			int key = commonRatedKey.get(i);
			double value1 = u1[key];
			double value2 = u2[key];
			
			double temp1 = value1 - mean1;
			double temp2 = value2 - mean2;
			
			if(temp1 < 0.00001 && temp1 > 0) temp1 = 0.00001;
			if(temp2 < 0.00001 && temp2 > 0) temp2 = 0.00001;

			if(temp1 > -0.00001 && temp1 < 0) temp1 = -0.00001;
			if(temp2 > -0.00001 && temp2 < 0) temp2 = -0.00001;
			
			upperAll += temp1 * temp2;
			downAll1 += temp1 * temp1;
			downAll2 += temp2 * temp2;
		}
		
		double downValue = Math.sqrt(downAll1 * downAll2);
		
		if(downValue == 0) 
			return -2;
		
		pcc = upperAll / downValue;
		
		//use significant weight to avoid the over estimation problem.
		// 10 is a parameter, which can be set.
		if(isSW && commonRatedKey.size() < swThreshold) {
			pcc = pcc * commonRatedKey.size() / swThreshold;
		}
		return pcc;
	}
	
	public double getVSS(float[] u1, float[] u2, boolean isSW, int swThreshold){
		// get the index of the common rated items.
		Vector<Integer> commonRatedKey = new Vector<Integer>();
		
		for (int i = 0; i < u1.length; i++) {
			if(u1[i] >= 0 && u2[i] >= 0) {
				commonRatedKey.add(i);
			}
		}
		
		// no common rate items. 
		if(commonRatedKey.size() == 0 || commonRatedKey.size() == 1) 
			return -2;
		
		
		double vss = 0;
		double upperAll = 0;
		double downAll1 = 0;
		double downAll2 = 0;

		for (int i = 0; i < commonRatedKey.size(); i++) {
			int key = commonRatedKey.get(i);
			double value1 = u1[key];
			double value2 = u2[key];
			
			if(value1 < 0.00001 && value1 > 0) value1 = 0.00001;
			if(value1 > -0.00001 && value1 < 0) value1 = -0.00001;
			
			if(value2 < 0.00001 && value2 > 0) value2 = 0.00001;
			if(value2 > -0.00001 && value2 < 0) value2 = -0.00001;
			
			upperAll += value1 * value2;
			downAll1 += value1 * value1;
			downAll2 += value2 * value2;
		}
		
		double downValue = Math.sqrt(downAll1 * downAll2);
		
		if(downValue == 0) 
			return -2;
		
		vss = upperAll / downValue;
		
		//use significant weight to avoid the over estimation problem.
		// 10 is a parameter, which can be set.
		if(isSW && commonRatedKey.size() < swThreshold) {
			vss = vss * commonRatedKey.size() / swThreshold;
		}
		return vss;
	}
	
	
	public double getVSS(double[] u1, double[] u2, boolean isSW, int swThreshold){
		// get the index of the common rated items.
		Vector<Integer> commonRatedKey = new Vector<Integer>();
		
		for (int i = 0; i < u1.length; i++) {
			if(u1[i] >= 0 && u2[i] >= 0) {
				commonRatedKey.add(i);
			}
		}
		
		// no common rate items. 
		if(commonRatedKey.size() == 0 || commonRatedKey.size() == 1) 
			return -2;
		
		
		double vss = 0;
		double upperAll = 0;
		double downAll1 = 0;
		double downAll2 = 0;

		for (int i = 0; i < commonRatedKey.size(); i++) {
			int key = commonRatedKey.get(i);
			double value1 = u1[key];
			double value2 = u2[key];
			
			if(value1 < 0.00001 && value1 > 0) value1 = 0.00001;
			if(value1 > -0.00001 && value1 < 0) value1 = -0.00001;
			
			if(value2 < 0.00001 && value2 > 0) value2 = 0.00001;
			if(value2 > -0.00001 && value2 < 0) value2 = -0.00001;
			
			upperAll += value1 * value2;
			downAll1 += value1 * value1;
			downAll2 += value2 * value2;
		}
		
		double downValue = Math.sqrt(downAll1 * downAll2);
		
		if(downValue == 0) 
			return -2;
		
		vss = upperAll / downValue;
		
		//use significant weight to avoid the over estimation problem.
		// 10 is a parameter, which can be set.
		if(isSW && commonRatedKey.size() < swThreshold) {
			vss = vss * commonRatedKey.size() / swThreshold;
		}
		return vss;
	}

	public void getClusterURR(float[][] randomedMatrix, double d, int loopNum, float[] imean) {
		
	}

	
	public float[] getURR_L1AVG(float[][] randomedMatrix, double d, int loopNum, float[] imean) {
		int userNumber = randomedMatrix.length;
		URR_L1AVG = new float[userNumber];
		int serviceNumber = randomedMatrix[0].length;
		float[] rating = new float[serviceNumber];
		float[] last_rating = new float[serviceNumber];
		int iteration = 0;
		
		for (int i = 0; i < imean.length; i++) {
			rating[i] = imean[i];
		}
		
		Vector<Integer> unpredictedList = new Vector<Integer>();
		
		for (int i = 0; i < userNumber; i++) {
			//URR[i] = random.nextFloat();
			URR_L1AVG[i] = (float)1.0;
		}
		
		for (int i = 0; i < serviceNumber; i++) {
			unpredictedList.add(i);
			last_rating[i] = -3;
		}
			
		do {
			
			//calculating URR
			for (int i = 0; i < userNumber; i++) {
				float tmpURR = 0;
				int Oj = 0;
				Vector<Integer> ratedServiceKey = new Vector<Integer>();
				
				for (int j =0; j < serviceNumber; j++) {
					if (randomedMatrix[i][j] > 0) {
						ratedServiceKey.add(j);
					}
				}
				Oj = ratedServiceKey.size();
				for (int k = 0; k < Oj; k++) {
					tmpURR += Math.abs(randomedMatrix[i][k] - rating[k]);
				}
				if (Oj > 0) {
					tmpURR = (float)1 - (float)d * tmpURR / (float)Oj;
					URR_L1AVG[i] = tmpURR;
				}			
			}
					
			//calculating ratings
			for (int i = 0; i < serviceNumber; i++) {
				float tmprating = 0;
				int Mj = 0;
				Vector<Integer> commonRatedKey = new Vector<Integer>();
							
				for (int j = 0; j < userNumber; j++) {
					if (randomedMatrix[j][i] > 0) {
						commonRatedKey.add(j);
					}
				}
				Mj = commonRatedKey.size();
				
				for (int k = 0; k < Mj; k++) {
					int index = commonRatedKey.get(k);
					tmprating += randomedMatrix[index][i] * URR_L1AVG[index];
				}
				
				if (Mj > 0) {
					rating[i] =  tmprating / Mj;
				}
				else {
					rating[i] = -2;
				}		
			}
		
			for (int i = 0; i < unpredictedList.size(); i++) {
				int index = unpredictedList.get(i);
				if (rating[index] == -2){
					unpredictedList.remove(i);
					continue;
				}				
				if (Math.abs(rating[index] - last_rating[index]) < 0.0001) {
					unpredictedList.remove(i);
				} else {
					last_rating[index] = rating[index];
				}
			}
			

			iteration ++;
		} while ((iteration < loopNum) && unpredictedList.size() > 0);
				
		return rating;
	}
	
	public float[] getURR_L2AVG(float[][] randomedMatrix, double d, int loopNum, float[] imean) {
		int userNumber = randomedMatrix.length;
		URR_L2AVG = new float[userNumber];
		int serviceNumber = randomedMatrix[0].length;
		float[] rating = new float[serviceNumber];
		float[] last_rating = new float[serviceNumber];
		int iteration = 0;
		
		for (int i = 0; i < imean.length; i++) {
			rating[i] = imean[i];
		}
		
		Vector<Integer> unpredictedList = new Vector<Integer>();
		
		for (int i = 0; i < userNumber; i++) {
			//URR[i] = random.nextFloat();
			URR_L2AVG[i] = (float)1.0;
		}
		
		for (int i = 0; i < serviceNumber; i++) {
			unpredictedList.add(i);
			last_rating[i] = -3;
		}
			
		do {
			
			//calculating URR
			for (int i = 0; i < userNumber; i++) {
				float tmpURR = 0;
				int Oj = 0;
				Vector<Integer> ratedServiceKey = new Vector<Integer>();
				
				for (int j =0; j < serviceNumber; j++) {
					if (randomedMatrix[i][j] > 0) {
						ratedServiceKey.add(j);
					}
				}
				Oj = ratedServiceKey.size();
				float diff = 0;
				for (int k = 0; k < Oj; k++) {
					diff = randomedMatrix[i][k] - rating[k];
					tmpURR += diff * diff;
				}
				if (Oj > 0) {
					tmpURR = (float)1 - (float)d * tmpURR / ((float)2.0 *(float)Oj);
					URR_L2AVG[i] = tmpURR;
				}			
			}
					
			//calculating ratings
			for (int i = 0; i < serviceNumber; i++) {
				float tmprating = 0;
				int Mj = 0;
				Vector<Integer> commonRatedKey = new Vector<Integer>();
							
				for (int j = 0; j < userNumber; j++) {
					if (randomedMatrix[j][i] > 0) {
						commonRatedKey.add(j);
					}
				}
				Mj = commonRatedKey.size();
				
				for (int k = 0; k < Mj; k++) {
					int index = commonRatedKey.get(k);
					tmprating += randomedMatrix[index][i] * URR_L2AVG[index];
				}
				
				if (Mj > 0) {
					rating[i] =  tmprating / Mj;
				}
				else {
					rating[i] = -2;
				}		
			}
		
			for (int i = 0; i < unpredictedList.size(); i++) {
				int index = unpredictedList.get(i);
				if (rating[index] == -2) {
					unpredictedList.remove(i);
					continue;
				}
				if (Math.abs(rating[index] - last_rating[index]) < 0.0001) {
					unpredictedList.remove(i);
				} else {
					last_rating[index] = rating[index];
				}
			}
			

			iteration ++;
		} while ((iteration < loopNum) && unpredictedList.size() > 0);
				
		return rating;
	}
	
	public void getUnreliableUserList(float[] URR, float unreliableNum) {
		//Test if the URR algorithm can identify unreliable users. (Yes)
		ArrayList<Float> list = new ArrayList<Float>();
		HashMap map = new HashMap();
		for (int i = 0; i < URR.length; i++) {
			list.add(new Float(URR[i]));	
			map.put(i, URR[i]);
		}
		Collections.sort(list);
		//Collections.reverse(list);
		
		unreliableUserList = new ArrayList();
		for (int i = 0; i < unreliableNum; i++) {
			unreliableUserList.add(UtilityFunctions.getMapKeyByValue(map, list.get(i)));
		}
	}
	
	public float[] getURR_umean(float[][] removedMatrix, float[] URR) {
		float[] umean = new float[removedMatrix.length];
		int[] uNumber = new int[removedMatrix.length];
		
		for (int i = 0; i < removedMatrix.length; i++) {
			if (unreliableUserList.contains(i))
				continue;
			for (int j = 0; j < removedMatrix[0].length; j++) {
				// exclude the null entries (-1) and the removed entries (-2). 
				if(removedMatrix[i][j] < 0) continue;
				
				umean[i] += removedMatrix[i][j] ;
				uNumber[i]++;
			}
		}
		
		for (int i = 0; i < umean.length; i++) {
			if(uNumber[i] ==0) umean[i] = -2;
			else umean[i] = umean[i] * URR[i] / uNumber[i];
		}
		
		return umean;
	}
	public float[] getURR_imean(float[][] removedMatrix, float[] URR) {
		int serviceNum = removedMatrix[0].length;
		float[] imean = new float[serviceNum];
		int[] iNumber = new int[serviceNum];
		
		/*for (int i = 0; i < removedMatrix.length; i++) {
			if (unreliableUserList.contains(i))
				continue;
			for (int j = 0; j < removedMatrix[0].length; j++) {
				imean[j] += removedMatrix[i][j] * URR[i];
				imean[j] ++;
			}
		}*/
		for (int j = 0; j < serviceNum; j ++) {
			for (int i = 0; i < removedMatrix.length; i ++) {
				if(removedMatrix[i][j] < 0) 
					continue;
				if (unreliableUserList.contains(i))
					continue;
				imean[j] += removedMatrix[i][j];
				iNumber[j]++;
			}
		}
		
		for (int i = 0; i < imean.length; i++) {
			if(iNumber[i] ==0) imean[i] = -2;
			else imean[i] /= iNumber[i];
		}
		
		return imean;
	}
	
}