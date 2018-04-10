package Network;

import java.io.*;
import java.util.*;

import MLSTMnetwork.MLSTM.MLSTMnetwork;

public class TrainTest {
	public static void train(File trainDataFile, File saveDestFile) {
		ArrayList<ArrayList<NetInp>> netInpList = new ArrayList<ArrayList<NetInp>>();
		try {
			FileInputStream fin = new FileInputStream(trainDataFile);
			ObjectInputStream ois = new ObjectInputStream(fin);
			netInpList = (ArrayList<ArrayList<NetInp>>) ois.readObject();
			ois.close();
			fin.close();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		final int IP = 1;
		final int BS = 48;
		//int gc = 0; int bc = 0;
		int count = 0;
		double step = 0.001;
		MLSTMnetwork net = new MLSTMnetwork(IP, 3, 50, 2, true);
		
		while (count < 1000) {
			net.resetStates();
			int ListN = (int)(Math.random() * netInpList.size() );
			int startN = (int)(Math.random() * (netInpList.get(ListN).size()-BS-IP) );				
			
			double[][] inputs = new double[BS][IP];
			double[][] expouts = new double[BS][3];
			for (int l=0;l<BS;l++) {
				for (int z=0;z<IP;z++)
					inputs[l][z] = netInpList.get(ListN).get(startN+l+z).pricediff;
				
				if (netInpList.get(ListN).get(startN+l+IP-1).futpmove==1) {
					expouts[l][0] = 1;
					//steps[l] = 0.0003;
				} else if (netInpList.get(ListN).get(startN+l+IP-1).futpmove==0) {
					expouts[l][1] = 1;
					//steps[l] = 0.000015;
				} else if (netInpList.get(ListN).get(startN+l+IP-1).futpmove==-1) {
					expouts[l][2] = 1;
					//steps[l] = 0.0003;
				}
			}
			
			net.trainBPTT(inputs, expouts, step);
			count++;
			System.out.println(count);
		}
		
		if (!new File(saveDestFile.getParent()).exists() )
			new File(saveDestFile.getParent() ).mkdirs();
		net.ws.saveToFile(saveDestFile);
	}
}
