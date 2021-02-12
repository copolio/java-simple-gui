package homework5.MultiCircularBufferSimulationFX.gui;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Iterator;

import homework5.MultiCircularBufferSimulationFX.CircularBuffer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MultiCircularBufferSimController extends AnchorPane {

	private final static int DEFAULT_BUFFER_COUNT = 10;
	
	public Stage parentStage = null;

	volatile CircularBuffer<String> cirbuf = null;
	volatile CircularBuffer<String> cirbuf1 = null;
	volatile Iterator<BigInteger> fibGen = null;
	BufferShape[] bufShapes = null;
	BufferShape[] bufShapes1 = null;
	
	//volatile IntegerProperty bufUseCount = new SimpleIntegerProperty(0);
	

    @FXML
    private AnchorPane root;

    @FXML
    private TextField tfBufSize;

    @FXML
    private Label lblCount;
    
    @FXML
    private Label lblCount1;

    @FXML
    private Button btnStart;

    @FXML
    private Pane drawPane;

    @FXML
    void handleBtnStart(ActionEvent event) {
    	if (cirbuf==null) {
    		initCircularBuffer(DEFAULT_BUFFER_COUNT);
    	}
    	if (cirbuf1==null) {
    		initCircularBuffer(DEFAULT_BUFFER_COUNT);
    	}
		Thread prod = new Thread(new ProducerTask());
		Thread cons = new Thread(new ConsumerTask());
		prod.start();
		cons.start();
		Thread prod2 = new Thread(new ProducerTask());
		Thread cons2 = new Thread(new ConsumerTask());
		prod2.start();
		cons2.start();
		Thread prod3 = new Thread(new ProducerTask());
		Thread cons3 = new Thread(new ConsumerTask());
		prod3.start();
		cons3.start();
		Thread prod4 = new Thread(new ProducerTask());
		Thread cons4 = new Thread(new ConsumerTask());
		prod4.start();
		cons4.start();
    }

    @FXML
    void handleBufSizeIn(ActionEvent event) {
    	int siz  = Integer.parseInt(tfBufSize.getText());
		initCircularBuffer(siz);
    }
    
	public MultiCircularBufferSimController() {

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cirbuf.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		initialize();
	}

	private void initialize() {
		fibGen = new BigFibonacciIterator();
	}
	void initCircularBuffer(int siz) {
    	//bufUseCount = new SimpleIntegerProperty(0);
		cirbuf =  new CircularBuffer<>(siz);
		bufShapes = new BufferShape[cirbuf.getBufSize()];
		
		cirbuf1 =  new CircularBuffer<>(siz);
		bufShapes1 = new BufferShape[cirbuf1.getBufSize()];
		buildCircularBufferShape();
	}
	void buildCircularBufferShape() {
		drawPane.getChildren().clear();
		
		double cx = drawPane.getWidth()/4;
		double cy = drawPane.getHeight()/4;
		System.out.println("cx="+cx+" cy="+cy);

		double outrad = cx*0.4;
		
		int siz = cirbuf.getBufSize(); 
		int siz1 = cirbuf1.getBufSize(); 
		double angl = 2*Math.PI/siz;
		double ang2 = 2*Math.PI/siz1;
		
		for (int i=0; i<siz; ++i) {
			bufShapes[i] = new BufferShape();
			bufShapes[i].setBufPath(cx, cy, outrad, angl);
			bufShapes[i].setText(String.valueOf(i), cx, cy, outrad, angl);
			bufShapes[i].setRot(i*Math.toDegrees(angl), cx, cy);
			bufShapes[i].setBackground(Color.SNOW);
			drawPane.getChildren().add(bufShapes[i]);
		}
		for (int i=0; i<siz1; ++i) {
			bufShapes1[i] = new BufferShape();
			bufShapes1[i].setBufPath(cx*3, cy, outrad, ang2);
			bufShapes1[i].setText(String.valueOf(i), cx*3, cy, outrad, ang2);
			bufShapes1[i].setRot(i*Math.toDegrees(ang2), cx*3, cy);
			bufShapes1[i].setBackground(Color.SNOW);
			drawPane.getChildren().add(bufShapes1[i]);
		}

	}
	
	void setBufferShapeColor() {
		int siz = cirbuf.getBufSize();
		int front = cirbuf.getFront();
		int rear = cirbuf.getRear();
		int siz1 = cirbuf1.getBufSize();
		int front1 = cirbuf1.getFront();
		int rear1 = cirbuf1.getRear();
		for (int i=0; i<siz; ++i) {
			bufShapes[i].setBackground(Color.SNOW);
		}
		int bp = front;
		for (int count=1; count <= cirbuf.getOccupiedBufferCount(); ++count) {
			bufShapes[bp].setBackground(Color.CYAN);
			bp = (bp + 1) % siz;
		}
		if (front!=rear ) {
			bufShapes[front].setBackground(Color.GREEN);
			bufShapes[rear].setBackground(Color.BLUE);
		}
		else if (cirbuf.getOccupiedBufferCount()>0) {
			bufShapes[front].setBackground(Color.GREEN);
		}
		else {
			bufShapes[front].setBackground(Color.CYAN);
		}
		for (int i=0; i<siz1; ++i) {
			bufShapes1[i].setBackground(Color.SNOW);
		}
		int bp1 = front1;
		for (int count=1; count <= cirbuf1.getOccupiedBufferCount(); ++count) {
			bufShapes1[bp1].setBackground(Color.CYAN);
			bp1 = (bp1 + 1) % siz1;
		}
		if (front1!=rear1 ) {
			bufShapes1[front1].setBackground(Color.GREEN);
			bufShapes1[rear1].setBackground(Color.BLUE);
		}
		else if (cirbuf1.getOccupiedBufferCount()>0) {
			bufShapes1[front1].setBackground(Color.GREEN);
		}
		else {
			bufShapes1[front1].setBackground(Color.CYAN);
		}
		
	}
	private class ProducerTask implements Runnable {
		public void run() {
			try {
				while (true) {
					//System.out.println("Producer writes " + i);
					double bufN = Math.random();
					if (bufN > 0.5 && cirbuf.getOccupiedBufferCount() < cirbuf.getBufSize()) {
						cirbuf.write(String.valueOf(fibGen.next())); // Add a value to the buffer
						Platform.runLater(()->{
							setBufferShapeColor();
							lblCount.setText(String.valueOf(cirbuf.getOccupiedBufferCount()));
							double ratio = (double)cirbuf.getOccupiedBufferCount()/cirbuf.getBufSize();
							Utils.setBackground(lblCount, Utils.getRatioColor(ratio));
							//System.out.println("lblCount Style = "+lblCount.getStyle());
						});
						// Put the thread into sleep
						Thread.sleep((int)(Math.random() * 1000));
					}
					else {
						cirbuf1.write(String.valueOf(fibGen.next())); // Add a value to the buffer
						Platform.runLater(()->{
							setBufferShapeColor();
							lblCount1.setText(String.valueOf(cirbuf1.getOccupiedBufferCount()));
							double ratio = (double)cirbuf1.getOccupiedBufferCount()/cirbuf1.getBufSize();
							Utils.setBackground(lblCount1, Utils.getRatioColor(ratio));
							//System.out.println("lblCount Style = "+lblCount.getStyle());
						});
						// Put the thread into sleep
						Thread.sleep((int)(Math.random() * 1000));
					}
				}
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

	// A task for reading and deleting an int from the buffer
	private class ConsumerTask implements Runnable {
		public void run() {
			try {
				while (true) {
					double bufN = Math.random();
					if (bufN > 0.5 && cirbuf.getOccupiedBufferCount() > 0) {
						String sval = cirbuf.read();
						System.out.println("\t\t\tConsumer reads " + sval);
						Platform.runLater(()->{
							setBufferShapeColor();
							lblCount.setText(String.valueOf(cirbuf.getOccupiedBufferCount()));
							double ratio = (double)cirbuf.getOccupiedBufferCount()/cirbuf.getBufSize();
							Utils.setBackground(lblCount, Utils.getRatioColor(ratio));
							//System.out.println("lblCount Style = "+lblCount.getStyle());
						});
						// Put the thread into sleep
						Thread.sleep((int)(Math.random() * 1000));
					}
					else {
						String sval = cirbuf1.read();
						System.out.println("\t\t\tConsumer reads " + sval);
						Platform.runLater(()->{
							setBufferShapeColor();
							lblCount1.setText(String.valueOf(cirbuf1.getOccupiedBufferCount()));
							double ratio = (double)cirbuf1.getOccupiedBufferCount()/cirbuf1.getBufSize();
							Utils.setBackground(lblCount1, Utils.getRatioColor(ratio));
							//System.out.println("lblCount Style = "+lblCount.getStyle());
						});
						// Put the thread into sleep
						Thread.sleep((int)(Math.random() * 1000));
					}
				}
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

}
