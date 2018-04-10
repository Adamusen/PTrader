package Network;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import MLSTMnetwork.MLSTM;
import MLSTMnetwork.MLSTM.MLSTMnetwork;
import MLSTMnetwork.MLSTM.MLSTMnWeights;

public class TrainMLSTM2 extends Thread {
	public boolean going = true; 
	
	public File trainDataFile;
	public File saveDestFile;
	
	final int IP = 72;
	final int BS = 192;
	double step = 0.1;
	int count=0;
	int badcount=0;
	
	double lowesterror=Double.MAX_VALUE;
	double prevvaliderror=Double.MAX_VALUE;
	double validerror=0;
	double trainerror=0;
	double money = 10000;
	double realmoney = 10000;
	double buynholdM = 10000;
	
	MLSTMnetwork net;
	MLSTMnWeights bestnetws;
	
	public TrainMLSTM2(File trainDataFile, File saveDestFile) {
		this.trainDataFile = trainDataFile;
		this.saveDestFile = saveDestFile;
		
		net = new MLSTMnetwork(IP, 3, 50, 2, true);
		net.ws.randomWeights();
		bestnetws = new MLSTMnWeights(net.ws);
	}
	
	public void loadNetfromFile(File srcFile) {
		net = MLSTM.loadMLSTMnetworkFromFile(srcFile);
		bestnetws = new MLSTMnWeights(net.ws);
	}
	
	public void saveWStoFile() {
		if (!new File(saveDestFile.getParent()).exists() )
			new File(saveDestFile.getParent() ).mkdirs();
		bestnetws.saveToFile(saveDestFile);
	}
	
	@SuppressWarnings("unchecked")
	public void run() {
		net.ws = new MLSTMnWeights(bestnetws);
		
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
		
		while (going) {
			net.resetStates();
			
			int ListN = (int)(Math.random() * netInpList.size() );
			int startN = (int)(Math.random() * BS );
			trainerror = 0;
			validerror = 0;
			int tec=0, vtec=0;
			money = 10000; realmoney = 10000;
			boolean instock = false;
			
			double[][][] chartDataSet = new double[3][netInpList.get(ListN).size()-IP-BS-1][2];
			for (int i=0; i<netInpList.get(ListN).size()-IP-BS-1; i++) {		
				double[][] inputs = new double[BS][IP];
				double[][] expouts = new double[BS][3];
				//double[] steps = new double[BS];
				for (int l=0;l<BS;l++) {
					for (int z=0;z<IP;z++)
						inputs[l][z] = netInpList.get(ListN).get(i+l+z).smadiff;
					
					if (netInpList.get(ListN).get(i+l+IP-1).futpmove==1) {
						expouts[l][0] = 1;
						//steps[l] = 0.0003;
					} else if (netInpList.get(ListN).get(i+l+IP-1).futpmove==0) {
						expouts[l][1] = 1;
						//steps[l] = 0.000015;
					} else if (netInpList.get(ListN).get(i+l+IP-1).futpmove==-1) {
						expouts[l][2] = 1;
						//steps[l] = 0.0003;
					}
				}
				
				net.input = inputs[0];
				/*for (int f=0;f<inputs[0].length;f++)
					System.out.print(inputs[0][f] + ", ");
				System.out.println("");*/
				net.runNeural();
				//System.out.println(inputs[0][8] + ", " + inputs[0][9] + ";   " + expouts[0][0] + "_" + expouts[0][1] + "_" +expouts[0][2]);
				
				if (i>48)
					if (i<netInpList.get(ListN).size()*0.8) {
						if (i % BS == startN)
							net.trainBPTT(inputs, expouts, step);
						net.calcOutputError(expouts[0]);
						trainerror += net.outputError;
						tec++;
					} else {						
						if ( (net.output[0]-net.output[2] > 0.2) && !instock ) {
							System.out.println("buy at : " + netInpList.get(ListN).get(i+IP-1).closeprice );
							money = money / netInpList.get(ListN).get(i+IP-1).closeprice;
							realmoney = realmoney * 0.9998 / netInpList.get(ListN).get(i+IP-1).closeprice;
							instock = true;
						} else if ( (net.output[2]-net.output[0] > 0.2) && instock ) {
							System.out.println("sell at : " + netInpList.get(ListN).get(i+IP-1).closeprice );
							money = money * netInpList.get(ListN).get(i+IP-1).closeprice;
							realmoney = realmoney * 0.9998 * netInpList.get(ListN).get(i+IP-1).closeprice;
							instock = false;
						}
						
						validerror += net.calcOutputError(expouts[0]);
						vtec++;
					}
				
				chartDataSet[0][i] = new double[]{i, (net.output[0]-net.output[2])};
				chartDataSet[1][i] = new double[]{i, expouts[0][0]-expouts[0][2]};
				chartDataSet[2][i] = new double[]{i, net.output[1]};
			}
			Window.NTrainer.updateTrainSNetChartDataset(chartDataSet);
			
			trainerror = trainerror / tec;
			validerror = validerror / vtec;
			if (instock) {
				money = money * netInpList.get(ListN).get(netInpList.get(ListN).size()-IP-BS-1).closeprice;
				realmoney = realmoney * 0.9998 * netInpList.get(ListN).get(netInpList.get(ListN).size()-IP-BS-1).closeprice;
			}
			buynholdM = 10000 * netInpList.get(ListN).get(netInpList.get(ListN).size()-IP-BS-1).closeprice / netInpList.get(ListN).get((int)(netInpList.get(ListN).size()*0.8)).closeprice;
			Window.NTrainer.trainSNet_LogTextArea.append("c: " + count + "  validerrmin: " + lowesterror + " validerr: " + validerror + " trainerr: " + trainerror + " expmoney: " + money + " realexpmoney: " + realmoney + " BnH M: " + buynholdM + "\n");
			
			if (validerror < lowesterror) {
				lowesterror = validerror;
				bestnetws = new MLSTMnWeights(net.ws);
				badcount=0;
			}
			
			if (validerror < prevvaliderror) {
				badcount = Math.max(0, badcount--);
			} else badcount++;
			prevvaliderror = validerror;
			
			/*if (badcount >= 20) {
				saveWStoFile();
				going = false;
			}*/
			
			count++;
		}
	}
}
