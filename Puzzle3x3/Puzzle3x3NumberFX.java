package homework1.Puzzle3x3;

import java.util.Date;
import java.util.Random;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;


public class Puzzle3x3NumberFX extends Application {
	
		static final int MAX_VALUE = 1000;
		private GridPane root;
		private Button[] btnPuzzle = null;
		
		private Button btnStart;
		
		private Button btn00;
		private Button btn01;
		private Button btn02;
		private Button btn10;
		private Button btn11;
		private Button btn12;
		private Button btn20;
		private Button btn21;
		private Button btn22;

		private ImageView imageIconWhite = null;
		private int[] permu;
		private int[][] canMove;
		private final int TRYMAX = 100;
		private int whitePos;

		
		@Override
		public void start(Stage primaryStage) throws Exception {

			primaryStage.setTitle("Puzzle 3x3");
			primaryStage.setMaxHeight(300);
			primaryStage.setMaxWidth(280);
			
			root = new GridPane();
			root.setPrefHeight(280);
			root.setPrefWidth(280);
			
			btnStart = new Button("Start");
			btnStart.setMaxSize(280, 19);
			btnStart.setOnAction(e->startNewGame());
			root.add(btnStart, 0, 3, 3, 1);
			
			btn00 = new Button("1");
			btn01 = new Button("2");
			btn02 = new Button("3");
			btn10 = new Button("4");
			btn11 = new Button("5");
			btn12 = new Button("6");
			btn20 = new Button("7");
			btn21 = new Button("8");
			btn22 = new Button("");
			
			btn00.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			btn01.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			btn02.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			btn10.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			btn11.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			btn12.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			btn20.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			btn21.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			btn22.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			
	        int numRows = 3 ;
	        int numColumns = 3 ;
	        for (int row = 0 ; row < numRows ; row++ ){
	            RowConstraints rc = new RowConstraints();
	            rc.setFillHeight(true);
	            rc.setVgrow(Priority.ALWAYS);
	            root.getRowConstraints().add(rc);
	        }
	        for (int col = 0 ; col < numColumns; col++ ) {
	            ColumnConstraints cc = new ColumnConstraints();
	            cc.setFillWidth(true);
	            cc.setHgrow(Priority.ALWAYS);
	            root.getColumnConstraints().add(cc);
	        }

	        btn00.setOnAction(e -> doButtonAction(0));
	        btn01.setOnAction(e -> doButtonAction(1));
	        btn02.setOnAction(e -> doButtonAction(2));
	        btn10.setOnAction(e -> doButtonAction(3));
	        btn11.setOnAction(e -> doButtonAction(4));
	        btn12.setOnAction(e -> doButtonAction(5));
	        btn20.setOnAction(e -> doButtonAction(6));
	        btn21.setOnAction(e -> doButtonAction(7));
	        btn22.setOnAction(e -> doButtonAction(8));
	        
			root.addRow(0, btn00, btn01, btn02);
			root.addRow(1, btn10, btn11, btn12);
			root.addRow(2, btn20, btn21, btn22);
			
			btnPuzzle = new Button[] {
					btn00, btn01, btn02,
					btn10, btn11, btn12,
					btn20, btn21, btn22
			};
			permu = new int [] { 0, 1, 2, 3, 4, 5, 6, 7, 8};
			canMove = new int [][] {
					/* 0 */{1, 3},
					/* 1 */{0, 2, 4},
					/* 2 */{1, 5},
					/* 3 */{0, 4, 6},
					/* 4 */{1, 3, 5, 7},
					/* 5 */{2, 4, 8},
					/* 6 */{3, 7},
					/* 7 */{4, 6, 8},
					/* 8 */{5,7}
			};
			setDisableAll();
			setButtonImage();
			Scene scene = new Scene(root);

			primaryStage.setScene(scene);
			primaryStage.show();
		}
		
	    private void startNewGame() {
	    	int st = 8;
			int to = 0;
			long seed = new Date().getTime();
			Random ran = new Random(seed);


			for (int tryCount = 1; tryCount < TRYMAX; ++ tryCount) {
				to = canMove[st][ran.nextInt(canMove[st].length)];
				swapPermute(st, to);
				st = to;
			}
	    	setButtonImage();
			setEnableAll();
			
			whitePos = st;
			setWhitePosition(whitePos);
	    	
	    }
		private void doButtonAction(int btn) {
			setWhitePosition(btn);
		}
		private void setWhitePosition(int btn) {
			if (!isEnabled(btn))
				return;
			
			swapPermute(btn, whitePos);
			whitePos = btn;
			setButtonImage();
			if (isEndCondition()) {
				setDisableAll();
				return;
			}

			setButtonWhite(btnPuzzle[btn]);
			
		}
		private void setButtonWhite(Button btn) {
			btn.setGraphic(imageIconWhite);
		}
		private void swapPermute(int i, int j) {
			int temp = permu[i];
			permu[i] = permu[j];
			permu[j] = temp;
		}
		private void setDisableAll() {
			for (int i=0; i<btnPuzzle.length; ++i){
				btnPuzzle[i].setDisable(true);
			}
		}
		private void setEnableAll() {
			for (int i=0; i<btnPuzzle.length; ++i){
				btnPuzzle[i].setDisable(false);
			}
		}
		private void setButtonImage() {
			for (int i=0; i<btnPuzzle.length; ++i){
				int lnum = permu[i]+1;
				if (lnum == 9){
					btnPuzzle[i].setText("");
				} 
				else{ 
					btnPuzzle[i].setText(String.valueOf(permu[i]+1));
				}
			}
		}
		private boolean isEndCondition() {
			for (int i=0; i<9; ++i)
				if (permu[i] != i) return false;
			return true;
		}
		private boolean isEnabled(int btn){
			boolean[] result = new boolean[9];
			for (int i=0; i<9; ++i){
				result[i] = false;
			}
			for (int c=0; c<canMove[whitePos].length; ++c){
				int d = canMove[whitePos][c];
				result[d] = true;
			}
			return result[btn];
		}
		public static void main(String[] args) {
			launch(args);
		}

	}