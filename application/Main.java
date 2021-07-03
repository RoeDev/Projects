package application;

import java.util.ArrayList;
import java.util.List;

import Agent.Matrix;
import Agent.ML.UnitsTest;
import Agent.ML.HyperParams.Tuner;
import Agent.ML.HyperParams.numberTest;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import view.MainMenuManager;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
//		Matrix a = new Matrix(1, 2);
//		Matrix b = new Matrix(2, 3);
//		System.out.println(a + "\n");
//		System.out.println(b + "\n");
//		Matrix c = a.MultiMatrix(b, 1);
//		System.out.println(c);
		MainMenuManager manager = new MainMenuManager();
		// gameViewManager manager = new gameViewManager(1800, 1200, 1);
		// primaryStage = manager.getGameStage();
		primaryStage = manager.getMainStage();
		primaryStage.setTitle("Flappy Learn V.0");
		primaryStage.getIcons()
				.add(new Image(this.getClass().getResourceAsStream("/LoadingErrorContents/whiteBox.png")));
		// manager.openGame(primaryStage);
		primaryStage.show();
	}

	public static void main(String args[]) {
	      launch(args);
//		   numberTest test = new numberTest();
//		   UnitsTest ut = new UnitsTest();
//		try {
//			Tuner t = new Tuner();
//			List<Double> one = new ArrayList<Double>();
//			one.add(1.0);
//			one.add(2.0);
//			one.add(3.0);
//			t.addParam("test", one);
//			List<Double> two = new ArrayList<Double>();
//			two.add(10.0);
//			two.add(20.0);
//			two.add(30.0);
//			t.addParam("test2", two);
//			List<Double> three = new ArrayList<Double>();
//			three.add(100.0);
//			three.add(200.0);
//			three.add(300.0);
//			t.addParam("test3", three);
//			
//			while (t.isRunning()) {
//				System.out.println("test:  " + t.getParam("test"));
//				System.out.println("test2:\t\t" + t.getParam("test2"));
//				System.out.println("test3:\t\t\t\t" + t.getParam("test3"));
//				t.step();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}