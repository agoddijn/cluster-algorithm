import java.awt.geom.Point2D;

public class Poi {
	
	Point2D pos = new Point2D.Double();
	Hub assignedHub = new Hub();
	boolean canBeAssigned = true;
	
	public Poi(double x, double y){
		this.pos = new Point2D.Double(x, y);
	}
	
	public Poi(){
		
	}
	
	public void changeCoord(double x, double y){
		pos.setLocation(x, y);
	}
	
	public void assignHub(Hub hub){
		assignedHub = hub;
	}
	
	public Hub getAssignedHub(){
		return assignedHub;
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
	
	public void changeAssignment(Boolean state){
		canBeAssigned = state;
	}
	
	public boolean assignable(){
		return canBeAssigned;
	}
	
}
