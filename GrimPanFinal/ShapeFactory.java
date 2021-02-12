package homework4.GrimPanFinal;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.shape.VLineTo;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class ShapeFactory {

	private volatile static ShapeFactory uniqueSFInstance;
	
	GrimPanModel model = null;

	private ShapeFactory(GrimPanModel model) {
		this.model = model;
	}
	public static ShapeFactory getInstance(GrimPanModel model) {
		if (uniqueSFInstance == null) {
			synchronized (GrimPanModel.class) {
				if (uniqueSFInstance == null) {
					uniqueSFInstance = new ShapeFactory(model);
				}
			}
		}
		return uniqueSFInstance;
	}
	public Shape createPaintedShape(Shape shape) {

		if (model.isShapeFill()){
			shape.setFill(model.getShapeFillColor());
		}
		else {
			shape.setFill(Color.TRANSPARENT);
		}

		if (model.isShapeStroke()){
			shape.setStrokeWidth(model.getShapeStrokeWidth());
			shape.setStroke(model.getShapeStrokeColor());
		}
		else {
			shape.setStroke(Color.TRANSPARENT);
		}
		return shape;
	}
	public Ellipse createPaintedEllipse() {
		Ellipse shape = new Ellipse();

		if (model.isShapeFill()){
			shape.setFill(model.getShapeFillColor());
		}
		else {
			shape.setFill(Color.TRANSPARENT);
		}
		if (model.isShapeStroke()){
			shape.setStrokeWidth(model.getShapeStrokeWidth());
			shape.setStroke(model.getShapeStrokeColor());
		}
		else {
			shape.setStroke(Color.TRANSPARENT);
		}
		return shape;
	}
	public Line createPaintedLine() {
		Line shape = new Line();

		shape.setStrokeWidth(model.getShapeStrokeWidth());
		shape.setStroke(model.getShapeStrokeColor());
		return shape;
	}
	public Path createPaintedPath() {
		Path shape = new Path();

		if (model.isShapeFill()){
			shape.setFill(model.getShapeFillColor());
		}
		else {
			shape.setFill(Color.TRANSPARENT);
		}

		if (model.isShapeStroke()){
			shape.setStrokeWidth(model.getShapeStrokeWidth());
			shape.setStroke(model.getShapeStrokeColor());
		}
		else {
			shape.setStroke(Color.TRANSPARENT);
		}
		return shape;
	}
	public Shape createMousePointedLine() {
		Point2D pstart = model.getStartMousePosition();
		Point2D pend = model.getCurrMousePosition();
		return createPaintedShape(new Line(pstart.getX(), pstart.getY(), pend.getX(), pend.getY()));

	}

	public Shape createPolygonFromClickedPoints(){
		ArrayList<Point2D> points = model.polygonPoints;
		Polygon result = new Polygon();
		if (points != null && points.size() > 2) {
			for (int i=0; i<points.size(); ++i){
				result.getPoints().add(points.get(i).getX());
				result.getPoints().add(points.get(i).getY());
			}
		}
		return createPaintedShape(result);
	}
	public Shape createPolylineFromClickedPoints(){
		ArrayList<Point2D> points = model.polygonPoints;
		Polyline result = new Polyline();
		if (points != null && points.size() > 0) {
			for (int i=0; i<points.size(); ++i){
				result.getPoints().add(points.get(i).getX());
				result.getPoints().add(points.get(i).getY());
			}
		}
		return createPaintedShape(result);
	}
	public Shape createMousePointedEllipse(){

		Point2D topleft = model.getStartMousePosition();
		Point2D pcurr = model.getCurrMousePosition();

		if (pcurr.distance(topleft) <= Utils.MINPOLYDIST)
			return null;
		double radiusX = (pcurr.getX() - topleft.getX()) / 2;
		double radiusY = (pcurr.getY() - topleft.getY()) / 2;
		double centerX = topleft.getX() + radiusX;
		double centerY = topleft.getY() + radiusY;
		return createPaintedShape(new Ellipse(centerX, centerY, radiusX, radiusY));
	}
	public Shape createRegularPolygon(int nvertex){
		Point2D center = model.getStartMousePosition();
		Point2D pi = model.getCurrMousePosition();
		if (pi.distance(center) <= Utils.MINPOLYDIST)
			return new Path();

		double nangle = 360.0/nvertex; // 360/n degree
		Rotate rot = new Rotate(nangle);

		Point2D[] polyPoints = new Point2D[nvertex];
		polyPoints[0] = rot.transform(pi.getX()-center.getX(), pi.getY()-center.getY()); 
		for (int i=1; i<polyPoints.length; ++i){
			polyPoints[i] = rot.transform(polyPoints[i-1]);
		}

		Translate tra = new Translate(center.getX(), center.getY());
		//polyPoints[0] = new Point2D(pi.getX(), pi.getY()); 
		for (int i=0; i<polyPoints.length; ++i){
			polyPoints[i] = tra.transform(polyPoints[i]);
		}
		Path polygonPath = new Path();
		polygonPath.getElements().add(new MoveTo(polyPoints[0].getX(), polyPoints[0].getY()));
		for (int i=1; i<polyPoints.length; ++i){
			polygonPath.getElements().add(new LineTo(polyPoints[i].getX(), polyPoints[i].getY()));
		}
		//polygonPath.getElements().add(new LineTo(polyPoints[0].getX(), polyPoints[0].getY()));
		polygonPath.getElements().add(new ClosePath());

		return createPaintedShape(polygonPath);

	}
	public Shape createStar(int nvertex){
		Point2D center = model.getStartMousePosition();
		Point2D pi = model.getCurrMousePosition();

		Path polygonPath = new Path();
		polygonPath.getElements().add(new MoveTo(center.getX(), center.getY()));

		double rot=Math.PI/2*3;
	    double x=center.getX();
	    double y=center.getY();
	    double step=Math.PI/nvertex;
	    double outerRadius = center.getY() - pi.getY();
	    double innerRadius = outerRadius / 2.0;

	    polygonPath.getElements().add(new MoveTo(center.getX(), center.getY() - outerRadius));
	    for(int i=0;i<nvertex;i++){
	        x=center.getX()+Math.cos(rot)*outerRadius;
	        y=center.getY()+Math.sin(rot)*outerRadius;
	        polygonPath.getElements().add(new LineTo(x, y));
	        rot+=step;

	        x=center.getX()+Math.cos(rot)*innerRadius;
	        y=center.getY()+Math.sin(rot)*innerRadius;
	        polygonPath.getElements().add(new LineTo(x, y));
	        rot+=step;
	    }
	    polygonPath.getElements().add(new LineTo(center.getX(), center.getY()-outerRadius));
		polygonPath.getElements().add(new ClosePath());
	      
	    return createPaintedShape(polygonPath);

		
	}
	public Shape createSpiral(){
		Point2D topleft = model.getStartMousePosition();
		Point2D pcurr = model.getCurrMousePosition();

		if (pcurr.distance(topleft) <= Utils.MINPOLYDIST)
			return null;
		double radiusX = (pcurr.getX() - topleft.getX()) / 2;
		double radiusY = (pcurr.getY() - topleft.getY()) / 2;
		double centerX = topleft.getX() + radiusX;
		double centerY = topleft.getY() + radiusY;
		
		double size = Math.abs(topleft.getX() - pcurr.getX());
		
		Path polygonPath = new Path();
		polygonPath.getElements().add(new MoveTo(centerX, centerY));
		
		double STEPS_PER_ROTATION = 60;
	    double increment = 2*Math.PI/STEPS_PER_ROTATION;       
	    double theta = increment;

	    while( theta < size*Math.PI) {
	    	double newX = centerX + theta * Math.cos(theta); 
	    	double newY = centerY + theta * Math.sin(theta);
	    	ArcTo arc = new ArcTo();
	    	arc.setX(newX);
	    	arc.setY(newY);
	    	polygonPath.getElements().add(arc);
	    	polygonPath.getElements().add(arc);
	    	theta = theta + increment;
	    }
		
		return createPaintedShape(polygonPath);

	}
	public Shape createHeart(){
		Point2D start = model.getStartMousePosition();
		Point2D end = model.getCurrMousePosition();
		
		SVGPath svg = new SVGPath();
		svg.setContent("m297.29747,550.86823c-13.77504,-15.43632 -48.17067,-45.52968 -76.4347,-66.87411c-83.7441,-63.24184 -95.14169,-72.39422 -129.14353,-103.70324c-62.68453,-57.72017 -89.30562,-115.71002 -89.21439,-194.33964c0.04451,-38.38385 2.66077,-53.17195 13.40988,-75.79726c18.2367,-38.38571 45.10027,-66.90931 79.44532,-84.35452c24.3254,-12.35578 36.32265,-17.84526 76.94443,-18.06984c42.49329,-0.23483 51.43863,4.71973 76.43471,18.45184c30.42451,16.71432 61.7399,52.43571 68.21323,77.81059l3.9981,15.6724l9.85963,-21.58451c55.71617,-121.97293 233.59836,-120.14805 295.50229,3.03159c19.63767,39.07605 21.79364,122.51317 4.38012,169.51287c-22.71527,61.30937 -65.38001,108.05053 -164.00634,179.67658c-64.68082,46.97364 -137.88474,118.04586 -142.98067,128.02803c-5.91548,11.58753 -0.28216,1.8159 -26.40808,-27.46078z");
		double size = Math.abs(start.getX() - end.getX());
		svg.setScaleX(size / svg.boundsInLocalProperty().get().getWidth());
		svg.setScaleY(size / svg.boundsInLocalProperty().get().getHeight());
		Path path = (Path) (Shape.subtract(svg, new Rectangle(0, 0)));

		return createPaintedShape(path);


	}


	static public void translateShape(Shape shape, double dx, double dy) {

		if (shape instanceof Line) {
			Line line = (Line) shape;
			line.setStartX(line.getStartX()+dx);
			line.setStartY(line.getStartY()+dy);
			line.setEndX(line.getEndX()+dx);
			line.setEndY(line.getEndY()+dy);
		}
		else if (shape instanceof Ellipse) {
			Ellipse ellipse = (Ellipse) shape;
			ellipse.setCenterX(ellipse.getCenterX()+dx);
			ellipse.setCenterY(ellipse.getCenterY()+dy);
		}
		else if (shape instanceof Path) {
			Path path =(Path) shape;
			for (PathElement el:path.getElements()) {
				if (el instanceof MoveTo) {
					MoveTo pel = (MoveTo)el;
					pel.setX(pel.getX() + dx);
					pel.setY(pel.getY() + dy);
				}
				else if (el instanceof LineTo) {
					LineTo pel = (LineTo)el;
					pel.setX(pel.getX() + dx);
					pel.setY(pel.getY() + dy);
				}
				else if (el instanceof ArcTo) {
					ArcTo pel = (ArcTo)el;
					pel.setX(pel.getX() + dx);
					pel.setY(pel.getY() + dy);
				}
				else if (el instanceof HLineTo) {
					HLineTo pel = (HLineTo)el;
					pel.setX(pel.getX() + dx);
				}
				else if (el instanceof VLineTo) {
					VLineTo pel = (VLineTo)el;
					pel.setY(pel.getY() + dy);
				}
				else if (el instanceof CubicCurveTo) {
					CubicCurveTo pel = (CubicCurveTo)el;
					pel.setX(pel.getX() + dx);
					pel.setY(pel.getY() + dy);
					pel.setControlX1(pel.getControlX1() + dx);
					pel.setControlY1(pel.getControlY1() + dy);
					pel.setControlX2(pel.getControlX2() + dx);
					pel.setControlY2(pel.getControlY2() + dy);
				}
				else if (el instanceof QuadCurveTo) {
					QuadCurveTo pel = (QuadCurveTo)el;
					pel.setX(pel.getX() + dx);
					pel.setY(pel.getY() + dy);
					pel.setControlX(pel.getControlX() + dx);
					pel.setControlY(pel.getControlY() + dy);
				}
			}
		}
		else if (shape instanceof Polygon) {
			Polygon pol =(Polygon) shape;
			ObservableList<Double> points = pol.getPoints();
			for (int i=0; i<points.size(); i+=2) {
				points.set(i, points.get(i)+dx);
				points.set(i+1, points.get(i+1)+dy);
			}
		}
		else if (shape instanceof Polyline) {
			Polyline pol =(Polyline) shape;
			ObservableList<Double> points = pol.getPoints();
			for (int i=0; i<points.size(); i+=2) {
				points.set(i, points.get(i)+dx);
				points.set(i+1, points.get(i+1)+dy);
			}
		}
	}
}
