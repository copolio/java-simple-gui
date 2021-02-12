package homework2.TwoClock;

import java.util.TimeZone;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class TwoClockController {

    @FXML
    private Pane leftClockPane;

    @FXML
    private Pane rightClockPane;

    @FXML
    private Label lblLeftCity;

    @FXML
    private Label lblRightCity;

    @FXML
    public void initialize() {
    	ClockPane leftClock = new AppleClockPane(TimeZone.getTimeZone("Asia/Seoul"));
    	leftClockPane.getChildren().add(leftClock);
    	leftClock.prefHeightProperty().bind(leftClockPane.heightProperty());
    	leftClock.prefWidthProperty().bind(leftClockPane.widthProperty());
    	
    	EventHandler<ActionEvent> eventHandler = e -> {
			leftClock.setCurrentTime(TimeZone.getTimeZone("Asia/Seoul")); // Set a new clock time
			if (leftClock.hour >= 6 && leftClock.hour < 18)
				leftClock.setBackground(new Background(new BackgroundFill(Color.rgb(240, 240, 240), CornerRadii.EMPTY, Insets.EMPTY)));
			else if (leftClock.hour < 6 || leftClock.hour >= 18)
				leftClock.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));
		};

		// Create an animation for a running clock
		Timeline animation = new Timeline(new KeyFrame(Duration.millis(1000), eventHandler));
		animation.setCycleCount(Timeline.INDEFINITE);
		animation.play(); // Start animation
    	
    	ClockPane rightClock = new AppleClockPane(TimeZone.getTimeZone("America/Argentina/Cordoba"));
    	rightClockPane.getChildren().add(rightClock);
    	rightClock.prefHeightProperty().bind(rightClockPane.heightProperty());
    	rightClock.prefWidthProperty().bind(rightClockPane.widthProperty());
    	
    	EventHandler<ActionEvent> eventHandler1 = e -> {
    		rightClock.setCurrentTime(TimeZone.getTimeZone("America/Argentina/Cordoba")); // Set a new clock time
    		// Change Background Color according to time
			if (rightClock.hour >= 6 && rightClock.hour < 18)
				rightClock.setBackground(new Background(new BackgroundFill(Color.rgb(240, 240, 240), CornerRadii.EMPTY, Insets.EMPTY)));
			else if (rightClock.hour < 6 || rightClock.hour >= 18)
				rightClock.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));
		};

		// Create an animation for a running clock
		Timeline animation1 = new Timeline(new KeyFrame(Duration.millis(1000), eventHandler1));
		animation1.setCycleCount(Timeline.INDEFINITE);
		animation1.play(); // Start animation

    }

}
