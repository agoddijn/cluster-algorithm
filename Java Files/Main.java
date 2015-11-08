import java.sql.*;
import java.util.*;

public class Main {

	final static double THRESHOLD1 = 0.005; // distance hub moves to centre
											// before
	// termination
	final static double MINDISTANCE = 1; // distance from other hub at which hub
											// is
	// deleted
	final static double HUBSIZE = 1000; // average distance between hub and
										// points for it
	// to be a hub
	final static double TIMES = 20; // number of algorithm iterations to test
									// correct hub number

	final static int HUBNUM = 100; // number of hubs according to number of
									// points a higher value causes a higher
									// precision, (more hubs)

	public static void main(String[] args) {
		Map mainMap = new Map();

		// Setting for map
		mainMap.setThreshold1(THRESHOLD1);
		mainMap.setMinDistance(MINDISTANCE);
		mainMap.setHubSize(HUBSIZE);
		mainMap.setTimes(TIMES);
		mainMap.setHubNum(HUBNUM);

		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager
					.getConnection("jdbc:sqlite:PersonalNetwork2.sqlite");
			System.out.println("Opened database successfully");
			c.setAutoCommit(false);

			stmt = c.createStatement();

			String sqldrop = "DROP TABLE HUBSFORTRIPS";
			stmt.execute(sqldrop);

			String sql1 = "CREATE TABLE HUBSFORTRIPS "
					+ "(LONGITUDE INTEGER NOT NULL, "
					+ "LATITUDE INTEGER NOT NULL)";
			stmt.executeUpdate(sql1);

			ResultSet rs = stmt.executeQuery("SELECT * FROM TRIPS;");
			while (rs.next()) {
				double x1 = rs.getDouble("from_latitude");
				double y1 = rs.getDouble("from_longitude");
				double x2 = rs.getDouble("to_latitude");
				double y2 = rs.getDouble("to_longitude");
				mainMap.addPoint(x1, y1);
				mainMap.addPoint(x2, y2);
			}
			List<Hub> hubs = mainMap.findHubs();
			int size = hubs.size();
			for (int i = 0; i < size; i++) {
				Hub curHub = hubs.get(i);
				System.out.println("x: " + curHub.getX() + " y: "
						+ curHub.getY());
				String sql2 = "INSERT INTO HUBSFORTRIPS (LONGITUDE,LATITUDE) "
						+ "VALUES (" + curHub.getX() + ", " + curHub.getY()
						+ ");";
				stmt.executeUpdate(sql2);
			}
			rs.close();
			stmt.close();
			c.commit();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.print("Database succesfuly populated");
	}

}
