package Network;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;

import MLSTMnetwork.MLSTM;
import MLSTMnetwork.MLSTM.MLSTMnetwork;
import MLSTMnetwork.MLSTM.MLSTMnWeights;

public class TrainMLSTM extends Thread {
	public boolean going = true; 
	
	public File trainDataFile;
	public File saveDestFile;
	
	final int IP = 24;
	final int BS = 24;
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
	
	public TrainMLSTM(File trainDataFile, File saveDestFile) {
		this.trainDataFile = trainDataFile;
		this.saveDestFile = saveDestFile;
		
		net = new MLSTMnetwork(IP, 1, 50, 1, false);
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
			double lastexpcp = 0;
			
			double[][][] chartDataSet = new double[2][netInpList.get(ListN).size()-IP-BS-1][2];
			for (int i=0; i<netInpList.get(ListN).size()-IP-BS-1; i++) {		
				double[][] inputs = new double[BS][IP];
				double[][] expouts = new double[BS][1];
				//double[] steps = new double[BS];
				for (int l=0;l<BS;l++) {
					for (int z=0;z<IP;z++)
						inputs[l][z] = netInpList.get(ListN).get(i+l+z).smadiff;
					
					expouts[l][0] = netInpList.get(ListN).get(i+l+IP).smadiff;
				}
								
				/*if (i>=netInpList.get(ListN).size()*0.8) {
					net.input = Arrays.copyOfRange(net.input, 1, net.input.length+1);	
					net.input[net.input.length-1] = net.output[0];
				} else*/
					net.input = inputs[0];
				/*for (int f=0;f<inputs[0].length;f++)
					System.out.print(inputs[0][f] + ", ");
				System.out.println("");*/
				net.runNeural();
				
				if (i>48)
					if (i<netInpList.get(ListN).size()*0.5) {
						if (i % BS == startN)
							net.trainBPTT(inputs, expouts, step);
						net.calcOutputError(expouts[0]);
						trainerror += net.outputError;
						tec++;
					} else {						
						/*if ( (net.output[0]-net.output[2] > 0.05) && !instock ) {
							money = money / netInpList.get(ListN).get(i).closeprice;
							realmoney = realmoney * 0.9998 / netInpList.get(ListN).get(i).closeprice;
							instock = true;
						} else if ( (net.output[2]-net.output[0] > 0.05) && instock ) {
							money = money * netInpList.get(ListN).get(i).closeprice;
							realmoney = realmoney * 0.9998 * netInpList.get(ListN).get(i).closeprice;
							instock = false;
						}*/
						
						double movesumm = 1;
						MLSTMnetwork tempnet = new MLSTMnetwork(net.ws);
						tempnet.setStates(net.getStates() );
						tempnet.output = Arrays.copyOf(net.output, net.output.length);
						for (int j=0;j<48;j++) {
							movesumm*= (tempnet.output[0]/1000)+1;
							//System.out.print(movesumm + ", ");
							tempnet.input = Arrays.copyOfRange(tempnet.input, 1, tempnet.input.length+1);	
							tempnet.input[tempnet.input.length-1] = tempnet.output[0];
							tempnet.runNeural();
							if (movesumm > 1.0025 && !instock) {
								//System.out.print("buy at : " + netInpList.get(ListN).get(i+IP-1).closeprice );
								money = money / netInpList.get(ListN).get(i+IP-1).closeprice;
								realmoney = realmoney * 0.9998 / netInpList.get(ListN).get(i+IP-1).closeprice;
								instock = true;
								break;
							} else if (1/movesumm > 1.0025 && instock) {
								//System.out.println("; sell at : " + netInpList.get(ListN).get(i+IP-1).closeprice );
								money = money * netInpList.get(ListN).get(i+IP-1).closeprice;
								realmoney = realmoney * 0.9998 * netInpList.get(ListN).get(i+IP-1).closeprice;
								instock = false;
								break;
							}
						}
						//System.out.println("");
						
						validerror += net.calcOutputError(expouts[0]);
						vtec++;
					}
				
				if (i<netInpList.get(ListN).size()*0.5)
					lastexpcp = netInpList.get(ListN).get(i+IP-1).sma * ((net.output[0]/1000)+1);
				else
					lastexpcp = lastexpcp * ((net.output[0]/1000)+1);
				chartDataSet[0][i] = new double[]{i, lastexpcp};
				chartDataSet[1][i] = new double[]{i, netInpList.get(ListN).get(i+IP).sma};			
				
				/*chartDataSet[0][i] = new double[]{i, net.output[0]};
				chartDataSet[1][i] = new double[]{i, expouts[0][0]};*/
				/*chartDataSet[2][i] = new double[]{i, net.output[1]};*/
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
