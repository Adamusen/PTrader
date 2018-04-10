package Network;

import java.io.*;
import java.util.*;
import java.text.*;

public class TDcreator {
	public static String createNetInp_histdata(File srcFile, File destFile, int wt, int wp, double pdiff, int noftws, int nofitems) {
		final int pdscaler = 1000;
		final int smaperiod = 8;
		int pc=0, nc=0, c=0;
		String result = "Invalid source or destination File!";
		
		FileReader fr = null;
		BufferedReader br = null;
		FileOutputStream fout = null;
		ObjectOutputStream oos = null;
		
		try {
			fr = new FileReader(srcFile);
			br = new BufferedReader(fr);
			
			ArrayList<ArrayList<NetInp>> netInpList = new ArrayList<ArrayList<NetInp>>();
			
			ArrayList<Long> tsList = new ArrayList<Long>();
			ArrayList<Double> cpList = new ArrayList<Double>();
			ArrayList<Double> vList = new ArrayList<Double>();
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd hhmmss"); 
			
			String[] ld = br.readLine().split(";");
			while (ld!=null) {
				tsList.add(df.parse(ld[0]).getTime()/1000);
				cpList.add(Double.parseDouble(ld[4]) );
				vList.add(Double.parseDouble(ld[5]) );
				
				String s = null;
				s = br.readLine();
				if (s==null) break;
				ld = s.split(";");
			}
			br.close();
			fr.close();
			
			for (int i=0;i<wt/wp;i++) {
				int ListN = netInpList.size();
				netInpList.add(new ArrayList<NetInp>() );
				
				double lastcp = 0;
				for (int j=1+ListN*(wp/60);j<tsList.size();j+=wt/60) {
					long ts = tsList.get(j);
					double cp = cpList.get(j);
					double pd = 0;
					if (lastcp!=0)
						pd = (cp/lastcp-1)*pdscaler;
					lastcp = cp;
					double v = vList.get(j);
					
					netInpList.get(ListN).add(new NetInp(ts, pd, cp, v, 0) );
				}
				
				for (int k=0;k<netInpList.get(ListN).size();k++) {
					int fpm = 0;
					for (int z=k+1;z<Math.min(k+noftws, netInpList.get(ListN).size() );z++)
						if (netInpList.get(ListN).get(z).closeprice/netInpList.get(ListN).get(k).closeprice > pdiff) {
							fpm=1;
							pc++;
							break;
						} else if (netInpList.get(ListN).get(k).closeprice/netInpList.get(ListN).get(z).closeprice > pdiff) {
							fpm=-1;
							nc++;
							break;
						}
					c++;			
					netInpList.get(ListN).get(k).futpmove = fpm;
					
					int avgcount=0;
					double avgsumm=0;
					for (int l=k;l>=Math.max(0, k-smaperiod+1);l--) {
						avgsumm+=netInpList.get(ListN).get(l).closeprice;
						avgcount++;
					}
					netInpList.get(ListN).get(k).sma = avgsumm / (double)avgcount;
					
					if (k>0)
						netInpList.get(ListN).get(k).smadiff = (netInpList.get(ListN).get(k-1).sma/netInpList.get(ListN).get(k).sma-1)*pdscaler;
				}
			}	
			
			for (int i=0;i<netInpList.size();i++)
				while (netInpList.get(i).size()>nofitems)
					netInpList.get(i).remove(0);
			
			File fileout = destFile;
			if (!new File(fileout.getParent()).exists() )
				new File(fileout.getParent() ).mkdirs();
			
			fout = new FileOutputStream(fileout, false);
			oos = new ObjectOutputStream(fout);
			oos.writeObject(netInpList);
			oos.close();
			fout.close();
			
			result = "Success.   positives: " + pc + " neutrals: " + (c-pc-nc) + " negatives: " + nc;
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		return result;
	}
}
