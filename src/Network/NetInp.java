package Network;

import java.io.Serializable;

@SuppressWarnings("serial")
public class NetInp implements Serializable {	
	public long timestamp;
	public double pricediff;
	public double closeprice;
	public double sma;
	public double smadiff;
	public double volume;
	public int futpmove;
	
	public NetInp (long ts) {
		this.timestamp=ts;
	}
	
	public NetInp (long ts, double pd, double cp, double v, int futpmove) {
		this.timestamp=ts;
		this.pricediff=pd;
		this.closeprice=cp;
		this.volume=v;
		this.futpmove=futpmove;
	}
	
	public NetInp (long ts, double pd, double cp, double sma, double smadiff, double v, int futpmove) {
		this.timestamp=ts;
		this.pricediff=pd;
		this.closeprice=cp;
		this.sma=sma;
		this.smadiff=smadiff;
		this.volume=v;
		this.futpmove=futpmove;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof NetInp ) {
			NetInp ot = (NetInp) other;
			if (ot.timestamp==this.timestamp ) return true;
			else return false;
		} else return false;
	}
}