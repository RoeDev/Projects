package Agent.ML;

public class Kaiming {
	private double a = Math.sqrt(5);
	private double kaimingVal;
	
	/*
	 * Kaiming is a calculation made for calculating at what range random should be calculated
	 * for initializing weights and biases in.
	 * This calculation is made depending on the in feature amount of a layer
	 */
	// Constructor
	public Kaiming(double fan_In) {
		
		double gain = Math.sqrt(2.0 / (1 + a * a));
//		double gain = Math.sqrt(2);
		kaimingVal = gain / Math.sqrt(fan_In);
	}
	public double getKaimingMin() {
		return -1 * kaimingVal;
	}
	public double getKaimingMax() {
		return kaimingVal;
	}
}
