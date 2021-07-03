package game;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import defaultSettings.GameSettings;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * <h1>Score Count</h1>
 * <p>Score Count is a HUD (Heads Up Display) for the score.
 * In Addition, for a Machine learning agent, it also shows the amount of times learned and max score</p>
 * @author roeec
 */
public class ScoreCount {
	private AnchorPane pane;
	private int scoreCnt;
	private Text maxScoreText;
	private int maxScore;
	private Text score = new Text("0");
	private int FONT_SIZE = 250;
	private String FONT_PATH = "src\\model\\recources\\FlappyBirdFont.ttf";
	private int LearningAmount;
	/**
	 * Constructor
	 * @param pane - game pane
	 */
	public ScoreCount(AnchorPane pane) {
		this.pane = pane;
		setSettings();
		pane.getChildren().add(score);
		
	}
	/**
	 * Set default position
	 */
	private void setLayoutDefault() {
		score.setLayoutX(GameSettings.WIDTH / 2);
		score.setLayoutY(GameSettings.HEIGHT / 4);
	}
	/**
	 * Reset Score count
	 */
	public void resetScoreCount() {
		setLayoutDefault();
		scoreCnt = 0;
		score.setText(0 + "");
	}
	/**
	 * set default font and position
	 */
	private void setSettings() {
		setLayoutDefault();
		try {
			score.setFont(Font.loadFont(new FileInputStream(FONT_PATH), FONT_SIZE));
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: loading in Font! (ScoreCount.java)");
			score.setFont(Font.font("Verdana",FONT_SIZE));
		}
		
	}
	/**
	 * update max score count to new score
	 */
	public void updateMaxScoreCount() {
		if(scoreCnt > maxScore) {
			maxScore = scoreCnt;
			maxScoreText.setText("Max Score:" + maxScore + "\nTimes Learned: " + LearningAmount);
		}
	}
	/**
	 * This function is used for Machine Learning agents, it adds a max score and times learned HUD
	 */
	public void addMaxScoreCounter() {
		maxScoreText = new Text("Max Score:" + 0 + "\nTimes Learned: " + LearningAmount);
		try {
			maxScoreText.setFont(Font.loadFont(new FileInputStream(FONT_PATH), FONT_SIZE / 4));
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: loading in Font! (ScoreCount.java)");
			maxScoreText.setFont(Font.font("Verdana",FONT_SIZE / 4));
		}
		maxScoreText.setLayoutX(GameSettings.WIDTH / 20);
		maxScoreText.setLayoutY(GameSettings.HEIGHT * 7 / 8);
		pane.getChildren().add(maxScoreText);
	}
	/**
	 * updates the learning amount text in the HUD for machine learning agent
	 * @param amount
	 */
	public void setLearningAmount(int amount) {
		LearningAmount = amount;
		maxScoreText.setText("Max Score:" + maxScore + "\nTimes Learned: " + LearningAmount);
	}
	/**
	 * Updates the score to score + 1
	 */
	public void updateScore() {
		scoreCnt++;
		score.setText(scoreCnt + "");
	}
	/**
	 * @return = returns the current score
	 */
	public int getScore() {
		return scoreCnt;
	}
}
