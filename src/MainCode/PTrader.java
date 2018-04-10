package MainCode;

import java.util.Arrays;
import java.util.LinkedList;

public class PTrader {
	public static LinkedList<String> Log = new LinkedList<String>();
	
	public static void addLogEntry(String entry) {
		String currtime = new java.sql.Timestamp(System.currentTimeMillis()).toString();
		Log.add(currtime + " \"" + entry + "\"");
		Window.MainFrame.LogTextArea.append(currtime + " \"" + entry + "\"\n");
		System.out.println(currtime + " \"" + entry + "\"");
	}
	
	public static void init() {
		Window.MainFrame.createWindow();
	}

	public static void main(String[] args) {
		init();
		
		//Network.TrainTest.train(new java.io.File("data/TrainData/TrainData.jdat"), new java.io.File("data/Networks/TrainTest.jdat") );
		//Network.TestNets.testEnsembly(new java.io.File("data/Networks"), new java.io.File("data/TrainData/TrainData2014.jdat") );
		//MLSTMnetwork.Test.test3();
		
		//System.out.println(Network.TDcreator.createNetInp_histdata(new java.io.File("data/HistData/MinuteBars_EURUSD/DAT_ASCII_EURUSD_M1_2015.csv"), new java.io.File("data/TrainData/EURUSD2015.jdat"), 1800, 60, 1.0025, 12, Integer.MAX_VALUE) );		
	}
}
