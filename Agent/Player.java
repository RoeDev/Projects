package Agent;

import java.util.ArrayList;

import game.FlappyBird;
import game.ScoreCount;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import model.ScoreBoard;

/**
 * <h1>Player</h1>
 * <p>Human player</p>
 * @author roeec
 *
 */
public class Player extends Agent{
	private Dialog<String> dialog;
	private ScoreBoard scoreBoard;
	private ScoreCount scoreUpdater;
	// Constructor
	public Player(Pane pane, ScoreCount scoreUpdater, ArrayList<Double> tubesPos,ScoreBoard scoreBoard) {
		this.scoreBoard = scoreBoard;
		this.scoreUpdater = scoreUpdater;
		this.pane = pane;
		dialog = new Dialog<String>();
		System.out.println("Player Playing!");
		bird = new FlappyBird(pane, scoreUpdater, tubesPos);
		createKeyListeners();
	}
	
	/**
	 *  The function represents a gameStep
	 *  A gameStep updates the enviorment(bird), and depending the object may do more Things
	 */	
	@Override
	public boolean gameStep() {
		bird.updateState();
		return true;
	}
	
	/**
	 * Create Key listeners so the player can interact with the game by pressing and releasing the SPACE key
	 */
	private void createKeyListeners() {

		pane.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.SPACE)
					bird.onKeyPressed();
			}
		});

		pane.getScene().setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.SPACE)
					bird.onKeyReleased();
			}
		});
	}

	private boolean isDone = false, called = false;
	public boolean getIsDone() {
		return isDone;
	}
	private void openDialogBox() {
		called = true;
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				TextInputDialog textDial = new TextInputDialog("Ex: Joe");
				textDial.setContentText("Nickname:");
				textDial.setHeaderText("GAME OVER\n\nPlease enter your name to submit it to the high scores:");
				textDial.setTitle("Game Over");
				
				final Button ok = (Button) textDial.getDialogPane().lookupButton(ButtonType.OK);
		        ok.addEventFilter(ActionEvent.ACTION, event ->
		            {
		            	System.out.println("OK");
		            	TextField text = textDial.getEditor();
		            	if(text != null && text.getText() != "")
							scoreBoard.addScore(text.getText(), scoreUpdater.getScore());
		            	isDone = true;
		            }
		        );

		        final Button cancel = (Button) textDial.getDialogPane().lookupButton(ButtonType.CANCEL);
		        cancel.addEventFilter(ActionEvent.ACTION, event ->
			        {
			        	System.out.println("CLOSE");
			        	isDone = true;
			        }
		        );
				
				textDial.showAndWait();
				
			}	
		});
	}
	/**
	 * This function makes sure the thread doesn't close, once the while loop finishes, the Thread will close
	 */
	@Override
	public void run() {
		while(!getIsDone()) {
			while(bird.isAlive()) {
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException e) {
					e.printStackTrace(); 
				}
			}
			if(!called)
				openDialogBox();
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
				e.printStackTrace(); 
			}
		}
		System.out.println("out");
	}
}
