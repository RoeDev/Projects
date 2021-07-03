package model;
/**
 * Person Object for scoreBoard, it holds a name and that name's score
 * @author roeec
 *
 */
public class PersonScore {
	private String name;
	private double score;
	
	public PersonScore(double score,String name) {
		this.name = name;
		this.score = score;
	}
	public String getName() {
		return name;
	}
	public double getScore() {
		return score;
	}
	@Override
	public String toString() {
		return "[name=" + name + ", score=" + score + "]";
	}
	
}
