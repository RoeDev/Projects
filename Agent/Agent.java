package Agent;

import game.FlappyBird;
import javafx.scene.layout.Pane;

/**
 * <h1>Agent</h1>
 * Agent is the base class for a bot or human player
 * @author roeec
 *
 */
public abstract class Agent implements Runnable{
	protected FlappyBird bird;
	protected Pane pane;
	
	// The function resets the bird's settings
	public void resetBirdSettings() {
	}
	// The function represents a gameStep
	// A gameStep updates the enviorment(bird), and depending the object may do more Things
	public abstract boolean gameStep();
	
	// This function stops a bird's object and returns to the main menu
	public void stopBird() {	
	}
	// This function tells if a thread is ready for another gameStep call
	public boolean getReadyForNextStep() {
		return true;
	}
}
