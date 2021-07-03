package subScenes;

import java.util.HashMap;
import java.util.Map;

import Agent.ML.Sequential;
import Agent.ML.serializeModels;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import view.MainMenuManager;
/**
 * <h1>LoadModelScene</h1>
 * <p>This subscene allows the user to load in a trained Neural Network.\n
 * It uses serialization to get neural network saves </p>
 *
 * @author roeec
 *
 */
public class LoadModelScene {
	private Scene scene;
	private MainMenuManager menuView;
	private Pane pane;
	private Map<String, Sequential> map;
	private VBox buttons;
	private ListView listView;
	private serializeModels sModels;
	// Constructor
	public LoadModelScene(MainMenuManager menuView, serializeModels sModels) {
		this.sModels = sModels;
		this.map = sModels.getMap();
		System.out.println("cmap" + map);
		this.menuView = menuView;
		listView = new ListView();
		createListView();
		buttons = new VBox();
		createButton();
		pane = new HBox(listView,buttons);
		scene = new Scene(pane);
		
		
	}
	
	// Reloads map entrys from file
	private void reload() {
		sModels.load();
		map = sModels.getMap();
	}
	
	// Creates the buttons that are on the scene
	private void createButton() {
		Button save = new Button("Save And Exit");
		save.setOnAction(event -> {
            ObservableList selectedIndices = listView.getSelectionModel().getSelectedIndices();
            if(selectedIndices.size() > 0) {
	            Integer index = (Integer) selectedIndices.get(0);
	            Sequential model = map.get(listView.getItems().get(index));
	            if(model != null)
	            	menuView.setModel(model);
            }
            menuView.showMenuViewWithModel();
        });
		Button remove = new Button("Remove");
		remove.setOnAction(event -> {
            ObservableList selectedIndices = listView.getSelectionModel().getSelectedIndices();
            if(selectedIndices.size() > 0) {
            	
	            Integer index = (Integer) selectedIndices.get(0);
	            sModels.removeModel((String) listView.getItems().get(index));
	            reload();
	    		createListView();
            }
        });
		buttons.getChildren().add(save);
		buttons.getChildren().add(remove);
	}
	
	// goes over the map containing read from the file, and adds it to the list view to show it to the user
	private void createListView() {
		listView.getItems().clear();
		for(String key : map.keySet()) {
			listView.getItems().add(key);
		}
	}
	// Shows the LoadModel scene
	public void showScene(Stage stage) {
		reload();
		createListView();
		System.out.println("SCene" + scene);
		stage.setScene(scene);
	}
}
