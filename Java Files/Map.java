import java.util.*;
import java.awt.geom.Point2D;

public class Map {

	double THRESHOLD1 = 0.005; // distance hub moves to centre before
										// termination
	double MINDISTANCE = 0.01; // distance from other hub at which hub is
										// deleted
	double HUBSIZE = 20; // average distance between hub and points for it
								// to be a hub
	double EXCEPTION = 1; // if a point is this much further from hub than
								// others, it is unassigned from hub
	double TIMES = 20;

	int HUBNUM = 1; // number of hubs according to number of points

	List<Poi> points = new ArrayList<Poi>();
	List<Hub> hubs = new ArrayList<Hub>();
	
	public void setThreshold1(double newThreshold){
		this.THRESHOLD1 = newThreshold; 
	}
	
	public void setMinDistance(double newMinDist){
		this.MINDISTANCE = newMinDist; 
	}
	
	public void setHubSize(double newHubSize){
		this.HUBSIZE = newHubSize; 
	}
	
	public void setTimes(double newTimes){
		this.TIMES = newTimes; 
	}
	
	public void setHubNum(int newHubNum){
		this.HUBNUM = newHubNum; 
	}

	public Map(List<Poi> poi) {
		this.points = poi;
	}

	public Map() {

	}

	// returns list of points
	public List<Poi> getPoints(){
		return points;
	}
	
	// returns list of hubs
		public List<Hub> getHubs(){
			return hubs;
		}
	
	// Adds POI to Map
	public void addPoint(double x, double y) {
		Poi p = new Poi(x, y);
		points.add(p);
	}

	// Removes POI from Map
	public void removePoint(double x, double y) {
		Poi p = new Poi(x, y);
		points.remove(p);
	}

	// Move existing POI
	public void movePoint(Poi p, double x, double y) {
		points.get(points.indexOf(p)).changeCoord(x, y);
	}

	// Returns the list of the hubs
	// ensures the correct number of hubs is displayed at the end
	public List<Hub> findHubs() {
		double[] probs = new double[100];
		int correctHubNum = 0;
		double probTemp = 0;
		boolean done = false;
		for (int i = 0; i < TIMES; i++) {
			mainAlgorithm();
			probs[hubs.size()] += 1;
			hubs.clear();
			makeAllAssignable();
		}
		for (int i = 0; i < 100; i++) {
			if (i == 0) {
				probTemp = probs[i];
				correctHubNum = i;
			} else if (probs[i] > probTemp) {
				probTemp = probs[i];
				correctHubNum = i;
			}
		}
		while (!done) {
			mainAlgorithm();
			if (hubs.size() == correctHubNum) {
				done = true;
			} else {
				hubs.clear();
				makeAllAssignable();
			}
		}
		makeAllAssignable();
		return hubs;
	}

	// Generates randomly located hubs
	public void generateHubs(int number) {
		Random rand = new Random(System.currentTimeMillis());
		double xMax = findExtreme("xMax");
		double xMin = findExtreme("xMin");
		double yMax = findExtreme("yMax");
		double yMin = findExtreme("yMin");

		for (int i = 0; i < number; i++) {
			Hub h = new Hub((rand.nextInt((int) (xMax - xMin)) + xMin),
					(rand.nextInt((int) (yMax - yMin)) + yMin));
			hubs.add(h);
		}
	}

	// Find the extremes of the map
	public double findExtreme(String which) {
		int size = points.size();
		double toReturn = 0.0;
		double curVal = 0.0;
		Poi curPoint;
		for (int i = 0; i < size; i++) {
			curPoint = points.get(i);
			if (which.startsWith("x")) {
				curVal = curPoint.getX();
			} else if (which.startsWith("y")) {
				curVal = curPoint.getY();
			}
			if (i == 0) {
				toReturn = curVal;
			} else {
				if (which.endsWith("Max")) {
					if (curVal > toReturn) {
						toReturn = curVal;
					}
				} else if (which.endsWith("Min")) {
					if (curVal < toReturn) {
						toReturn = curVal;
					}
				}
			}
		}
		return toReturn;
	}

	// The main algorithm
	public List<Hub> mainAlgorithm() {
		removeDouble();
		int sizePoints = points.size();
		boolean cont = true;
		if (hubs.isEmpty()) {
			generateHubs(points.size() * HUBNUM);
		}
		while (cont) {
			for (int i = 0; i < sizePoints; i++) {
				Poi curPoint = points.get(i);
				curPoint.assignHub(getClosestHub(curPoint));
			}
			trimHubs(); // deletes hubs with only one point
			cont = moveHubsToCentre();
		}
		trimHubs2(); // deletes hubs that are too close to eachother
		trimHubs3(); // deletes hubs that are too far away from anything
		return hubs;
	}

	// Returns the hub closest to the given POI and assigns the point to the hub
	public Hub getClosestHub(Poi p) {
		Point2D posPoi = p.getPos();
		double sizeHubs = hubs.size();
		double distance = 0.0;
		Hub selectedHub = new Hub();
		for (int i = 0; i < sizeHubs; i++) {
			Hub curHub = hubs.get(i);
			Point2D posHub = curHub.getPos();
			if (i == 0) {
				distance = posPoi.distance(posHub);
				selectedHub = curHub;
			} else {
				double distTemp = posPoi.distance(posHub);
				if (distTemp < distance) {
					distance = distTemp;
					selectedHub = curHub;
				}
			}
		}
		setHubAssignment(p, selectedHub);
		return selectedHub;
	}

	// Assigns the POI's to the appropriate hubs
	public void setHubAssignment(Poi p, Hub hub) {
		if (p.assignable()) {
			hub.assignPoi(p);
		}
	}

	// Delete all hubs without x
	public void trimHubs() {
		int size = hubs.size();
		for (int i = 0; i < size; i++) {
			Hub curHub = hubs.get(i);
			List<Poi> pois = curHub.getAssigned();
			if (pois.size() < 2) {
				hubs.remove(curHub);
				if (pois.size() == 1) {
					pois.get(0).changeAssignment(false);
				}
				i--;
				size--;
			}
		}
	}

	// Move hubs to the centre of assigned POI's
	public boolean moveHubsToCentre() {
		int size = hubs.size();
		boolean cont = false;
		for (int i = 0; i < size; i++) {
			Hub curHub = hubs.get(i);
			if (moveHubToCentre(curHub)) {
				cont = true;
			}
		}
		return cont;
	}

	// Move single hub to centre of its assigned POI's
	public boolean moveHubToCentre(Hub hub) {
		Point2D centre = findCentre(hub);
		if (hub.getPos().distance(centre) < THRESHOLD1) {
			return false;
		}
		hub.changeCoord(centre);
		return true;
	}

	// Find position of centre of assigned x's
	public Point2D findCentre(Hub hub) {
		List<Poi> pois = hub.getAssigned();
		double avgX = 0.0;
		double avgY = 0.0;
		double size = hub.getAssigned().size();
		for (int i = 0; i < size; i++) {
			Poi curPoint = pois.get(i);
			avgX += curPoint.getX();
			avgY += curPoint.getY();
		}
		return new Point2D.Double((avgX / size), (avgY / size));
	}

	// Delete hubs that are within certain distance from eachother
	public void trimHubs2() {
		double distance = 0;
		int size = hubs.size();
		for (int i = 0; i < size; i++) {
			Hub curHub = hubs.get(i);
			for (int j = i + 1; j < size; j++) {
				Hub tempHub = hubs.get(j);
				distance = curHub.getPos().distance(tempHub.getPos());
				if (distance < MINDISTANCE) {
					hubs.remove(tempHub);
					size--;
					j--;
				}
			}
		}
	}

	// Delete Hubs that are too far away from anything
	public void trimHubs3() {
		int size = hubs.size();
		for (int i = 0; i < size; i++) {
			Hub curHub = hubs.get(i);
			double avDist = avgDist(curHub);
			if (avDist > HUBSIZE) {
				hubs.remove(curHub);
				i--;
				size--;
			}
		}
	}

	// Remove points from hubs if they are too far away
	public void trimPoints() {
		int size = hubs.size();
		for (int i = 0; i < size; i++) {
			Hub curHub = hubs.get(i);
			List<Poi> pois = curHub.getAssigned();
			double avDist = avgDist(curHub);
			int size2 = pois.size();
			for (int j = 0; j < size2; j++) {
				Poi curPoi = pois.get(j);
				if (curHub.getPos().distance(curPoi.getPos()) > (avDist + EXCEPTION)) {
					// curPoi.changeAssignment(false);
					curHub.removePoi(curPoi);
					size2--;
					j--;
				}
			}
		}
	}

	// get the average distance of points from given hub
	public double avgDist(Hub hub) {
		double avgDist = 0.0;
		List<Poi> pois = hub.getAssigned();
		int size = pois.size();
		for (int j = 0; j < size; j++) {
			avgDist += hub.getPos().distance(pois.get(j).getPos());
		}
		avgDist /= size;
		return avgDist;
	}

	// clear all hubs
	public void clearHubs() {
		hubs.clear();
	}

	// make all points assignable
	public void makeAllAssignable() {
		int size = points.size();
		for (int i = 0; i < size; i++) {
			Poi curPoint = points.get(i);
			curPoint.changeAssignment(true);
		}
	}
	
	// remove all pois that have the same coordinates
	public void removeDouble(){
		int size = points.size();
		for(int i = 0; i < size; i++){
			Poi curPoint = points.get(i);
			for(int j = i+1; j < size; j++){
				Poi tempPoint = points.get(j);
				if(curPoint.getX() == tempPoint.getX() && curPoint.getY() == tempPoint.getY()){
					points.remove(j);
					j--;
					size--;
				}
			}
		}
	}
}
