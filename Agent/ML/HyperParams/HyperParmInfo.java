package Agent.ML.HyperParams;

import java.util.List;

public class HyperParmInfo {
	private int index;
	private List<Double> list;
	
	public HyperParmInfo(List<Double> list) {
		this.list = list;
	}
	
	public void reset() {
		index = 0;
	}
	public double getVal() {
		return list.get(index);
	}
	public void step() {
		index++;
	}
	public boolean needReset() {
		return index == list.size();
	}
	public int getIndex() {
		return index;
	}
	@Override
	public String toString() {
		String str = "";
		for(Double param : list) {
			str += param + ", ";
		}
		return str;
	}
}
