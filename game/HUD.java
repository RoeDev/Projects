package game;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Agent.ML.Exceptions.HudException;
import defaultSettings.GameSettings;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
/**
 * <h1>HUD</h1>
 * <p>The HUD (Heads Up Display) displays info on screen.</p>
 * @author roeec
 *
 */
public class HUD {
	private AnchorPane pane;
	private Map<String, Integer> map;
	private List<Object> values;
	private List<String> names;
	private int FONT_SIZE = 30;
	private String FONT_PATH = "src\\model\\recources\\FlappyFont.ttf";
	private int x = (int) (GameSettings.WIDTH * 7 / 10);
	private int y = (int) (GameSettings.HEIGHT * 2 / 10);
	
	private Text HUD;
	private double loss;
	/**
	 * Constructor
	 * @param pane - pane that HUD will be added to
	 */
	public HUD(AnchorPane pane) {
		this.pane = pane;
		map = new HashMap<String, Integer>();
		values = new ArrayList<Object>();
		names = new ArrayList<String>();
		InitializeParams();
		pane.getChildren().add(HUD);
	}
	/**
	 * Setting HUD font
	 */
	private void setHUDFont() {
		try {
			HUD.setFont(Font.loadFont(new FileInputStream(FONT_PATH), FONT_SIZE));
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: loading in Font! (ScoreCount.java)");
			HUD.setFont(Font.font("Verdana",FONT_SIZE));
		}
		
	}
	/**
	 * Initializer HUD size and position
	 */
	private void InitializeParams() {
		HUD = new Text("loss: " + loss);
		HUD.setLayoutX(x);
		HUD.setLayoutY(y);
		HUD.resize(500, 400);;
	}
	/**
	 * Adding HUD item to display
	 * @param name - name displayed for item
	 * @param val  - item's info that will be displayed
	 * @throws HudException
	 */
	public void addHudParam(String name, Object val) throws HudException {
		if(map.get(name) != null)
			throw new HudException("Param '" + name + "' Already exists!");
		values.add(val);
		names.add(name);
		map.put(name, values.size() - 1);
	}
	/**
	 * Updating a HUD item that was added with addHudParam(). 
	 * This will only update the stored HUD value and not the On screen text
	 * @param name - name of item that will be updated
	 * @param val - new value
	 * @throws HudException
	 */
	public void updateHudParam(String name, Object val) throws HudException {
		Integer index = map.get(name);
		if(index == null)
			throw new HudException("Invalid name param!" + name);
		values.set(index, val);
	}
	/**
	 * Updating the on Screen Text
	 */
	public void updateHUD() {
		HUD.setText(stringUpdate());
	}
	/**
	 * builds a String from all the stored items(name and value)
	 * @return
	 */
	private String stringUpdate() {
		String str = "";
		for(int i = 0; i < names.size(); i++)
			str += names.get(i) +":\n" + values.get(i) + "\n";
		return str;
	}
}
