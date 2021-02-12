package homework5.MultiCircularBufferSimulationFX.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MultiCircularBufferSimulationMain extends Application {

	@Override
	public void start(Stage primaryStage) {
		
		primaryStage.setTitle("Circular Buffer Simulation");
		
		MultiCircularBufferSimController root = new MultiCircularBufferSimController();
		root.parentStage = primaryStage;
		Scene scene = new Scene(root);

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
