package Network;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class TestNets {
	final static int IP = 1;
	
	public static void testEnsembly (File netsfolder, File trainDataFile) {
		String[] filenames = netsfolder.list();
		File[] netfiles = new File[filenames.length];
		MLSTMnetwork.MLSTM.MLSTMnetwork[] nets = new MLSTMnetwork.MLSTM.MLSTMnetwork[filenames.length];
		for (int i=0;i<filenames.length;i++) {
			filenames[i] = netsfolder.getAbsolutePath() + "\\" + filenames[i];
			netfiles[i] = new File(filenames[i]);
			nets[i] = MLSTMnetwork.MLSTM.loadMLSTMnetworkFromFile(netfiles[i]);
		}
		
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
		
		ArrayList<ArrayList<double[]>> results = new ArrayList<ArrayList<double[]>>();
		for (int k=0;k<nets.length;k++) {
			results.add(new ArrayList<double[]>() );
		}
		
		for (int i=IP;i<netInpList.get(0).size();i++) {
			double inputs[] = new double[IP];
			for (int j=0;j<IP;j++)
				inputs[j] = netInpList.get(0).get(i-IP+j).pricediff;
			
			for (int k=0;k<nets.length;k++) {
				nets[k].input = Arrays.copyOf(inputs, inputs.length);
				nets[k].runNeural();
				results.get(k).add(Arrays.copyOf(nets[k].output, 3) );
			}
		}
		
		ArrayList<double[]> sumres = new ArrayList<double[]>();
		for (int i=0;i<results.get(0).size();i++) {
			double[] outsum = new double[3];
			for (int k=0;k<nets.length;k++)
				for (int z=0;z<3;z++)
					outsum[z]+=results.get(k).get(i)[z];
			
			for (int z=0;z<3;z++)
				outsum[z]/=results.size();
			sumres.add(outsum);
		}
		
		/*for (int i=0;i<sumres.size();i++) {
			for (int z=0;z<3;z++)
				System.out.print(sumres.get(i)[z] + "   ");
			System.out.println("");
		}*/
				
		for (double z=0;z<=0.15;z+=0.01) {
			double money = 10000;
			double realmoney = 10000;
			double buynholdM = 10000;
			boolean instock = false;
			buynholdM *= netInpList.get(0).get( netInpList.get(0).size()-1).closeprice / netInpList.get(0).get(IP).closeprice;
			for (int i=0/*(int)(sumres.size()*0.8)*/;i<sumres.size();i++) {
				if ( (sumres.get(i)[0]-sumres.get(i)[2] > z) && !instock ) {
					money = money / netInpList.get(0).get(i+IP-1).closeprice;
					realmoney = realmoney * 0.9998 / netInpList.get(0).get(i+IP-1).closeprice;
					instock = true;
					//if (z==0.05) System.out.println("Buy at: " + netInpList.get(0).get(i+IP-1).closeprice);
				} else if ( (sumres.get(i)[2]-sumres.get(i)[0] > z) && instock ) {
					money = money * netInpList.get(0).get(i+IP-1).closeprice;
					realmoney = realmoney * 0.9998 * netInpList.get(0).get(i+IP-1).closeprice;
					instock = false;
					//if (z==0.05) System.out.println("Sell at: " + netInpList.get(0).get(i+IP-1).closeprice);
				}
			}		
			System.out.println("z=" + z + " expmoney: " + money + " realexpmoney: " + realmoney + " BnH M: " + buynholdM);
		}
	}
}
