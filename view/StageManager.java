package view;

import javafx.scene.Scene;
import javafx.stage.Stage;
/**
 * This is a base class for view managers
 * @author roeec
 *
 */
public abstract class StageManager {
	
	//javafx Necessities
	protected Scene scene;
	protected Stage stage;
	
	public Stage getMainStage() {
		return stage;
	}
}
