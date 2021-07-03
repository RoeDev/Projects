package Agent.ML;

public class Pair<T,S> implements Comparable{
	private T var1;
	private S var2;
	public Pair(T var1, S var2) {
		this.var1 = var1;
		this.var2 = var2;
	}
	public T getVar1() {
		return var1;
	}
	public void setVar1(T var1) {
		this.var1 = var1;
	}
	public S getVar2() {
		return var2;
	}
	public void setVar2(S var2) {
		this.var2 = var2;
	}
	
	public String toString() {
		return "(" + var1 + "," + var2 +")";
	}
	
	
	@Override
	public int compareTo(Object obj) {
		if (this == obj)
			return 0;
		if (getClass() != obj.getClass())
			return -1;
		Pair<T,S> other = (Pair<T,S>) obj;
		if((double) var1 - (double) other.getVar1() > 0)
			return 1;
		return 0;
	}
	
	
}
