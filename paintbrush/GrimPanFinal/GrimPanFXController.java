package homework4.GrimPanFinal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.swing.JColorChooser;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;

import homework4.GrimPanFinal.Utils;
import homework4.GrimPanFinal.ShapeFactory;
import homework4.GrimPanFinal.svg.SVGGrimEllipse;
import homework4.GrimPanFinal.svg.SVGGrimLine;
import homework4.GrimPanFinal.svg.SVGGrimPath;
import homework4.GrimPanFinal.svg.SVGGrimPolygon;
import homework4.GrimPanFinal.svg.SVGGrimPolyline;
import homework4.GrimPanFinal.svg.SVGGrimShape;
import homework4.GrimPanFinal.svg.SaxSVGPathParseHandler;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class GrimPanFXController extends AnchorPane {

	Stage parentStage;
	private GrimPanModel model;
	private ShapeFactory sf;
	
	DoubleProperty widthProp = new SimpleDoubleProperty();
	DoubleProperty heightProp = new SimpleDoubleProperty();
	
	public GrimPanFXController(Stage stage) {
		
		parentStage = stage;
		model = new GrimPanModel();
		sf = ShapeFactory.getInstance(model);

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("grimpan3.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		widthProp.bind(drawPane.widthProperty());
		heightProp.bind(drawPane.heightProperty());
		
		parentStage.widthProperty().addListener((obs, oldVal, newVal) -> {
			System.out.format("drawPane w=%s h=%s\n", drawPane.getWidth(), drawPane.getHeight());
			System.out.format("drawPane Property w=%s h=%s\n", widthProp.get(), heightProp.get());
		});

		parentStage.heightProperty().addListener((obs, oldVal, newVal) -> {
			System.out.format("drawPane w=%s h=%s\n", drawPane.getWidth(), drawPane.getHeight());
			System.out.format("drawPane Property w=%s h=%s\n", widthProp.get(), heightProp.get());
			lblSize.setText("Size: " + (drawPane.getWidth() * drawPane.getHeight()));
		});

		model.shapeList.addListener((ListChangeListener.Change<? extends SVGGrimShape> change) -> {
			System.out.format("Shape Count=%s %n", model.shapeList.size());
			lblCount.setText("Count: " + model.shapeList.size());

		});
		initDrawPane();
	}
	void initDrawPane() {
		model.shapeList.clear();
		model.curDrawShape = null;
		model.polygonPoints.clear();
		clearDrawPane();
		
	}
	
	void clearDrawPane() {
		drawPane.getChildren().clear();
	}
	void redrawDrawPane() {
		clearDrawPane();

		//System.out.println("Shape Count="+model.shapeList.size());
		for (SVGGrimShape gsh:model.shapeList){
			drawPane.getChildren().add(gsh.getShape());
		}
		if (model.curDrawShape!=null && model.curDrawShape.getShape()!=null) {
			drawPane.getChildren().add(model.curDrawShape.getShape());
		}
	}

    @FXML
    private AnchorPane root;

    @FXML
    private Pane drawPane;

	
    @FXML
    private MenuItem menuNew;

    @FXML
    private MenuItem menuOpen;

    @FXML
    private MenuItem menuSave;

    @FXML
    private MenuItem menuSaveAs;

    @FXML
    private MenuItem menuExit;

    @FXML
    private RadioMenuItem menuLine;

    @FXML
    private RadioMenuItem menuPencil;

    @FXML
    private RadioMenuItem menuPolygon;

    @FXML
    private RadioMenuItem menuRegular;

    @FXML
    private RadioMenuItem menuOval;
    
    @FXML
    private RadioMenuItem menuStar;
    
    @FXML
    private RadioMenuItem menuSpiral;
    
    @FXML
    private RadioMenuItem menuHeart;

    @FXML
    private MenuItem menuMove;

    @FXML
    private MenuItem menuDelete;

    @FXML
    private MenuItem menuStrokeWidth;

    @FXML
    private MenuItem menuStrokeColor;

    @FXML
    private MenuItem menuFillColor;

    @FXML
    private CheckMenuItem menuCheckStroke;

    @FXML
    private CheckMenuItem menuCheckFill;

    @FXML
    private MenuItem menuAbout;
    
    @FXML
    private Label lblSize;

    @FXML
    private Label lblCount;


    @FXML
    void handleMenuAbout(ActionEvent event) {

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("About");
		alert.setHeaderText("GrimPan Ver 0.3.1");
		alert.setContentText("Programmed by cskim, ces, hufs.ac.kr");

		alert.showAndWait();
    }

    @FXML
    void handleMenuCheckFill(ActionEvent event) {
		CheckMenuItem checkFill = (CheckMenuItem)event.getSource();
		if (checkFill.isSelected())
			model.setShapeFill(true);
		else
			model.setShapeFill(false);
    }

    @FXML
    void handleMenuCheckStroke(ActionEvent event) {
		CheckMenuItem checkStroke = (CheckMenuItem)event.getSource();
		if (checkStroke.isSelected())
			model.setShapeStroke(true);
		else
			model.setShapeStroke(false);
    }

    @FXML
    void handleMenuDelete(ActionEvent event) {
    	model.setEditState(Utils.EDIT_DELETE);
		if (model.curDrawShape != null){
			model.shapeList.add(model.curDrawShape);
			model.curDrawShape = null;
		}
		redrawDrawPane();
    }

    @FXML
    void handleMenuExit(ActionEvent event) {
		Platform.exit();
    }

    @FXML
    void handleMenuFillColor(ActionEvent event) {
		java.awt.Color awtColor = 
				JColorChooser.showDialog(null, "Choose a color", java.awt.Color.BLACK);
		Color jxColor = Color.BLACK;
		if (awtColor!=null){
			jxColor = new Color(awtColor.getRed()/255.0, awtColor.getGreen()/255.0, awtColor.getBlue()/255.0, 1);
		}
		model.setShapeFillColor(jxColor);
    }

    @FXML
    void handleMenuLine(ActionEvent event) {
		model.setEditState(Utils.SHAPE_LINE);
		redrawDrawPane();
    }

    @FXML
    void handleMenuMove(ActionEvent event) {
		model.setEditState(Utils.EDIT_MOVE);
		if (model.curDrawShape != null){
			model.shapeList.add(model.curDrawShape);
			model.curDrawShape = null;
		}
		redrawDrawPane();
    }

    @FXML
    void handleMenuNew(ActionEvent event) {
		initDrawPane();
    }

	@FXML
	void handleMenuOpen(ActionEvent event) {
		openAction();
	}

    @FXML
    void handleMenuOval(ActionEvent event) {
		model.setEditState(Utils.SHAPE_OVAL);
		redrawDrawPane();
    }

    @FXML
    void handleMenuPencil(ActionEvent event) {
		model.setEditState(Utils.SHAPE_PENCIL);
		redrawDrawPane();
    }

    @FXML
    void handleMenuPolygon(ActionEvent event) {
		model.setEditState(Utils.SHAPE_POLYGON);
		redrawDrawPane();
    }

    @FXML
    void handleMenuRegular(ActionEvent event) {
		model.setEditState(Utils.SHAPE_REGULAR);
		String[] possibleValues = { 
				"3", "4", "5", "6", "7",
				"8", "9", "10", "11", "12"
		};
		List<String> dialogData = Arrays.asList(possibleValues);

		ChoiceDialog<String> dialog = new ChoiceDialog<>(dialogData.get(0), dialogData);
		dialog.setTitle("Regular Polygon");
		dialog.setHeaderText("Number of Points");
		Optional<String> result = dialog.showAndWait();
		String selectedValue = String.valueOf(model.getNPolygon());
		if (result.isPresent()) {
			selectedValue = result.get();
		}

		model.setNPolygon(Integer.parseInt((String)selectedValue));

		redrawDrawPane();
    }
    
    @FXML
    void handleMenuStar(ActionEvent event) {
		model.setEditState(Utils.SHAPE_STAR);
		String[] possibleValues = { 
				"3", "4", "5", "6", "7",
				"8", "9", "10", "11", "12"
		};
		List<String> dialogData = Arrays.asList(possibleValues);

		ChoiceDialog<String> dialog = new ChoiceDialog<>(dialogData.get(0), dialogData);
		dialog.setTitle("Star");
		dialog.setHeaderText("Number of Spikes");
		Optional<String> result = dialog.showAndWait();
		String selectedValue = String.valueOf(model.getNPolygon());
		if (result.isPresent()) {
			selectedValue = result.get();
		}

		model.setNPolygon(Integer.parseInt((String)selectedValue));

		redrawDrawPane();
    }
    
    @FXML
    void handleMenuSpiral(ActionEvent event) {
		model.setEditState(Utils.SHAPE_SPIRAL);

		redrawDrawPane();
    }
    
    @FXML
    void handleMenuHeart(ActionEvent event) {
		model.setEditState(Utils.SHAPE_HEART);

		redrawDrawPane();
    }

	@FXML
	void handleMenuSave(ActionEvent event) {
		saveAction();
	}

    @FXML
    void handleMenuStrokeColor(ActionEvent event) {
		java.awt.Color awtColor = 
				JColorChooser.showDialog(null, "Choose a color", java.awt.Color.BLACK);
		Color jxColor = Color.BLACK;
		if (awtColor!=null){
			jxColor = new Color(awtColor.getRed()/255.0, awtColor.getGreen()/255.0, awtColor.getBlue()/255.0, 1);
		}
		model.setShapeStrokeColor(jxColor);
    }

    @FXML
    void handleMenuStrokeWidth(ActionEvent event) {
		TextInputDialog dialog = new TextInputDialog("10");
		dialog.setTitle("Set Stroke Width");
		dialog.setHeaderText("Enter Stroke Width Value");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			String inputVal = result.get();
			model.setShapeStrokeWidth(Float.parseFloat(inputVal));
		}

    }

    @FXML
    void handleMenusaveAs(ActionEvent event) {
    	saveAsAction();
    }

    @FXML
    void handleMouseEntered(MouseEvent event) {
    	model.setMouseInside(true);
    }

    @FXML
    void handleMouseExited(MouseEvent event) {
    	model.setMouseInside(false);
    }

    @FXML
    void handleMouseDragged(MouseEvent event) {
		Point2D p1 = new Point2D(Math.max(0, event.getX()), Math.max(0, event.getY()));

		if (event.getButton()==MouseButton.PRIMARY && model.isMouseInside()){
			model.setPrevMousePosition(model.getCurrMousePosition());
			model.setCurrMousePosition(p1);

			switch (model.getEditState()){
			case Utils.SHAPE_LINE:
				model.curDrawShape = new SVGGrimLine((Line)(sf.createMousePointedLine()));
				break;
			case Utils.SHAPE_PENCIL:
				((Path)model.curDrawShape.getShape()).getElements().add(new LineTo(p1.getX(), p1.getY()));
				break;
			case Utils.SHAPE_POLYGON:
				break;
			case Utils.SHAPE_REGULAR:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createRegularPolygon(model.getNPolygon())));
				break;
			case Utils.SHAPE_OVAL:
				model.curDrawShape = new SVGGrimEllipse((Ellipse)(sf.createMousePointedEllipse()));
				break;
			case Utils.EDIT_MOVE:
				moveShapeByMouse();
				break;
			case Utils.EDIT_DELETE:
				break;
			case Utils.SHAPE_STAR:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createStar(model.getNPolygon())));
				break;
			case Utils.SHAPE_HEART:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createHeart()));
				break;
			case Utils.SHAPE_SPIRAL:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createSpiral()));
				break;

			}
		}
		redrawDrawPane();
    }

    @FXML
    void handleMousePressed(MouseEvent event) {
		//System.out.format("drawPane w=%s h=%s\n", drawPane.getWidth(), drawPane.getHeight());
		//System.out.format("drawPane Property w=%s h=%s\n", widthProp.get(), heightProp.get());
		Point2D p1 = new Point2D(Math.max(0, event.getX()), Math.max(0, event.getY()));

		if (event.getButton()==MouseButton.PRIMARY && model.isMouseInside()){
			model.setStartMousePosition(p1);
			model.setCurrMousePosition(p1);
			model.setPrevMousePosition(p1);				
			switch (model.getEditState()){
			case Utils.SHAPE_LINE:
				model.curDrawShape = new SVGGrimLine((Line)(sf.createMousePointedLine()));
				break;
			case Utils.SHAPE_PENCIL:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createPaintedShape(new Path(new MoveTo(p1.getX(), p1.getY())))));
				break;
			case Utils.SHAPE_POLYGON:
				model.polygonPoints.add(model.getCurrMousePosition());
				if (event.isShiftDown()) {
					//((Path)model.curDrawShape).getElements().add(new ClosePath());
					model.curDrawShape = new SVGGrimPolygon((Polygon)(sf.createPolygonFromClickedPoints()));
					model.polygonPoints.clear();
					model.shapeList.add(model.curDrawShape);
					model.curDrawShape = null;
				}
				else {
					model.curDrawShape = new SVGGrimPolyline((Polyline)(sf.createPolylineFromClickedPoints()));
				}
				break;
			case Utils.SHAPE_REGULAR:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createRegularPolygon(model.getNPolygon())));
				break;
			case Utils.SHAPE_OVAL:
				model.curDrawShape = new SVGGrimEllipse((Ellipse)(sf.createMousePointedEllipse()));
				break;
			case Utils.EDIT_MOVE:
				model.getSelectedShape();
				break;
			case Utils.EDIT_DELETE:
				model.getSelectedShape();
				break;
			case Utils.SHAPE_STAR:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createStar(model.getNPolygon())));
				break;
			case Utils.SHAPE_HEART:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createHeart()));
				break;
			case Utils.SHAPE_SPIRAL:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createSpiral()));
				break;
			}
		}
		redrawDrawPane();
    }

    @FXML
    void handleMouseReleased(MouseEvent event) {
		Point2D p1 = new Point2D(Math.max(0, event.getX()), Math.max(0, event.getY()));
		//System.out.println("Mouse Released at "+p1);

		if (event.getButton()==MouseButton.PRIMARY){
			model.setPrevMousePosition(model.getCurrMousePosition());
			model.setCurrMousePosition(p1);

			switch (model.getEditState()){
			case Utils.SHAPE_LINE:
				model.curDrawShape = new SVGGrimLine((Line)(sf.createMousePointedLine()));
				if (model.curDrawShape != null){
					model.shapeList.add(model.curDrawShape);
					model.curDrawShape = null;
				}
				break;
			case Utils.SHAPE_PENCIL:
				((Path)model.curDrawShape.getShape()).getElements().add(new LineTo(p1.getX(), p1.getY()));
				if (model.curDrawShape != null){
					model.shapeList.add(model.curDrawShape);
					model.curDrawShape = null;
				}
				break;
			case Utils.SHAPE_POLYGON:
				break;
			case Utils.SHAPE_REGULAR:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createRegularPolygon(model.getNPolygon())));
				if (model.curDrawShape != null){
					model.shapeList.add(model.curDrawShape);
					model.curDrawShape = null;
				}
				break;
			case Utils.SHAPE_OVAL:
				model.curDrawShape = new SVGGrimEllipse((Ellipse)(sf.createMousePointedEllipse()));
				if (model.curDrawShape != null){
					model.shapeList.add(model.curDrawShape);
					model.curDrawShape = null;
				}
				break;
			case Utils.EDIT_MOVE:
				if (model.getSelectedShapeIndex()!=-1) {
					endShapeMove();
				}
				break;
			case Utils.EDIT_DELETE:
				if (model.getSelectedShapeIndex()!=-1) {
					deleteShape();
				}
				break;
			case Utils.SHAPE_STAR:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createStar(model.getNPolygon())));
				if (model.curDrawShape != null){
					model.shapeList.add(model.curDrawShape);
					model.curDrawShape = null;
				}
				break;
			case Utils.SHAPE_HEART:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createHeart()));
				if (model.curDrawShape != null){
					model.shapeList.add(model.curDrawShape);
					model.curDrawShape = null;
				}
				break;
			case Utils.SHAPE_SPIRAL:
				model.curDrawShape = new SVGGrimPath((Path)(sf.createSpiral()));
				if (model.curDrawShape != null){
					model.shapeList.add(model.curDrawShape);
					model.curDrawShape = null;
				}
				break;
			}
		}
    }
	void openAction() {

		FileChooser fileChooser1 = new FileChooser();
		fileChooser1.setTitle("Open Saved GrimPan");
		fileChooser1.setInitialDirectory(new File(Utils.DEFAULT_DIR));
		fileChooser1.getExtensionFilters().add(new ExtensionFilter("SVG File (*.svg)", "*.svg", "*.SVG"));
		File selFile = fileChooser1.showOpenDialog(this.parentStage);

		if (selFile == null) return;

		String fileName = selFile.getName();

		model.setSaveFile(selFile);
		readShapeFromSVGSaveFile(selFile);
		this.parentStage.setTitle("GrimPan - "+fileName);
		redrawDrawPane();
	}

	void readShapeFromSVGSaveFile(File saveFile) {

		SaxSVGPathParseHandler saxTreeHandler = new SaxSVGPathParseHandler(); 

		try {
			SAXParserFactory saxf = SAXParserFactory.newInstance();

			SAXParser saxParser = saxf.newSAXParser();
			saxParser.parse(new InputSource(new FileInputStream(saveFile)), saxTreeHandler);
		}
		catch(Exception e){
			e.printStackTrace();
		}

		this.initDrawPane();

		ObservableList<SVGGrimShape> gshapeList = saxTreeHandler.getPathList();
		for (SVGGrimShape gsh:gshapeList) {
			model.shapeList.add(gsh);
		}
	}
	void saveAction() {
		if (model.getSaveFile()==null){
			model.setSaveFile(new File(Utils.DEFAULT_DIR+"noname.svg"));
			this.parentStage.setTitle("GrimPan - "+Utils.DEFAULT_DIR+"noname.svg");
		}
		File selFile = model.getSaveFile();
		saveGrimPanSVGShapes(selFile);	
	}

	void saveAsAction() {
		FileChooser fileChooser2 = new FileChooser();
		fileChooser2.setTitle("Save As ...");
		fileChooser2.setInitialDirectory(new File(Utils.DEFAULT_DIR));
		fileChooser2.getExtensionFilters().add(new ExtensionFilter("SVG File (*.svg)", "*.svg", "*.SVG"));
		File selFile = fileChooser2.showSaveDialog(this.parentStage);

		model.setSaveFile(selFile);
		this.parentStage.setTitle("GrimPan - "+selFile.getName());

		saveGrimPanSVGShapes(selFile);	

	}

	void saveGrimPanSVGShapes(File saveFile){
		PrintWriter svgout = null;
		try {
			svgout = new PrintWriter(saveFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		svgout.println("<?xml version='1.0' encoding='utf-8' standalone='no'?>");
		//svgout.println("<!DOCTYPE svg PUBLIC '-//W3C//DTD SVG 1.0//EN' 'http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd'>");
		svgout.print("<svg xmlns:svg='http://www.w3.org/2000/svg' ");
		svgout.println("     xmlns='http://www.w3.org/2000/svg' ");
		svgout.print("width='"+this.getWidth()+"' ");
		svgout.print("height='"+this.getHeight()+"' ");
		svgout.println("overflow='visible' xml:space='preserve'>");
		for (SVGGrimShape gs:model.shapeList){
			svgout.println("    "+gs.getSVGShapeString());
		}
		svgout.println("</svg>");
		svgout.close();
	}

	private void moveShapeByMouse(){
		int selIndex = model.getSelectedShapeIndex();
		Shape shape = null;
		if (selIndex != -1){
			shape = model.shapeList.get(selIndex).getShape();
			double dx = model.getCurrMousePosition().getX() - model.getPrevMousePosition().getX();
			double dy = model.getCurrMousePosition().getY() - model.getPrevMousePosition().getY();

			ShapeFactory.translateShape(shape, dx, dy);
		}
	}
	private void endShapeMove(){
		int selIndex = model.getSelectedShapeIndex();
		Shape shape = null;
		if (selIndex != -1){
			shape = model.shapeList.get(selIndex).getShape();
			Color scolor = (Color)shape.getStroke();
			Color fcolor = (Color)shape.getFill();
			if (shape.getStroke()!=Color.TRANSPARENT){
				shape.setStroke(new Color (scolor.getRed(), scolor.getGreen(), scolor.getBlue(), 1));
			}
			if (shape.getFill()!=Color.TRANSPARENT){
				shape.setFill(new Color (fcolor.getRed(), fcolor.getGreen(), fcolor.getBlue(), 1));
			}
			double dx = model.getCurrMousePosition().getX() - model.getPrevMousePosition().getX();
			double dy = model.getCurrMousePosition().getY() - model.getPrevMousePosition().getY();

			ShapeFactory.translateShape(shape, dx, dy);
		}
	}
	private void deleteShape() {
		int selIndex = model.getSelectedShapeIndex();
		Shape shape = null;
		if (selIndex != -1){
			shape = model.shapeList.get(selIndex).getShape();
			if (shape.getStroke()!=Color.TRANSPARENT){
				shape.setStroke(Color.TRANSPARENT);
				shape.setStrokeWidth(0);
			}
			if (shape.getFill()!=Color.TRANSPARENT){
				shape.setFill(Color.TRANSPARENT);
			}
			double dx = model.getCurrMousePosition().getX() - model.getPrevMousePosition().getX();
			double dy = model.getCurrMousePosition().getY() - model.getPrevMousePosition().getY();

			ShapeFactory.translateShape(shape, dx, dy);
			model.shapeList.remove(selIndex);
			lblCount.setText("Count: " + model.shapeList.size());
		}
	}

}
