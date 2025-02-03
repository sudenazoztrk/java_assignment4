/**
 * The Point class represents a point in a map with its distance from a starting point,
 * its start and end points, and an ID to uniquely identify it. It also keeps track of
 * the previous point in a path to facilitate pathfinding.
 */
public class Point {
    private final int distance; // distance from the starting point
    private final String startPoint;
    private final String endPoint;
    private final int ID; // unique identifier for the point
    private Point previous; // previous point to track the path

    /**
     * Constructs a new Point with the specified distance, start point, end point, and ID.
     *
     * @param distance   the distance from the starting point
     * @param startPoint the name of the start point
     * @param endPoint   the name of the end point
     * @param ID         the unique identifier for the point
     */
    public Point(int distance, String startPoint, String endPoint, int ID) {
        this.distance = distance;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.ID = ID;
        this.previous = null;
    }

    /**
     * Returns the distance from the starting point.
     *
     * @return the distance from the starting point
     */
    public int getDistance() {
        return distance;
    }

    /**
     * Returns the name of the start point.
     *
     * @return the name of the start point
     */
    public String getStartPoint() {
        return startPoint;
    }

    /**
     * Returns the name of the end point.
     *
     * @return the name of the end point
     */
    public String getEndPoint() {
        return endPoint;
    }

    /**
     * Returns the unique identifier for the point.
     *
     * @return the unique identifier for the point
     */
    public int getID() {
        return ID;
    }

    /**
     * Returns the previous point in the path.
     *
     * @return the previous point in the path
     */
    public Point getPrevious() {
        return previous;
    }

    /**
     * Sets the previous point in the path.
     *
     * @param previous the previous point to set
     */
    public void setPrevious(Point previous) {
        this.previous = previous;
    }


}
