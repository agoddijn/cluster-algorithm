import java.awt.geom.Point2D;
import java.util.*;


public class Hub {
	
	Point2D pos = new Point2D.Double();
	List<Poi> assigned = new ArrayList<Poi>();

	public Hub(double x, double y){
		this.pos = new Point2D.Double(x, y);
	}
	
	public Hub(){
		
	}
	
	public void changeCoord(Point2D p){
		pos.setLocation(p);
	}
	
	public void assignPoi(Poi poi){
		assigned.add(poi);
	}
	
	public void removePoi(Poi poi){
		assigned.remove(poi);
	}
	
	public List<Poi> getAssigned(){
		return assigned;
	}
	
	public double getX(){
		return pos.getX();
	}
	
	public double getY(){
		return pos.getY();
	}
	
	public Point2D getPos(){
		return pos;
	}
	
}
