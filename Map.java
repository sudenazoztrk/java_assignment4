import java.util.*;

public class Map {
    private Point startPoint; //first start point
    private String endPoint; //target point
    private final ArrayList<Point> possiblePointsList = new ArrayList<>(); //possible roads of selected point, this list used in fastest road calculation
    private final ArrayList<Point> pointsList = new ArrayList<>(); // this list used in fastest road calculation
    private final HashMap<String,Point> pointList2 = new HashMap<>(); // this map used in fastest road calculation
    private final LinkedHashMap<String, Point> fastestWays = new LinkedHashMap<>();
    private Point selectedPoint;
    private final ArrayList<String> allPoints = new ArrayList<>(); //list to store all points, sorted alphabetically
    private String[] items2Array;
    private int totalMaterial = 0;
    private int barelyConnectedMapMaterial = 0;
    private int fastestRoadLength = 0;
    private int barelyConnectedRoadLength = 0;

    /**
     * Reads data from the input items, initializes the start and end points,
     * and calculates the total material required.
     *
     * @param items An array of strings where each string represents a road with
     *              start point, end point, distance, and ID separated by tabs.
     */
    public void ReadData(String[] items) {
        for (String line : items) {
            String[] parts = line.split("\t");
            startPoint = new Point(0, "", parts[0], 0);
            selectedPoint = startPoint;
            endPoint = parts[1];
            break; // only the first line is used to initialize start and end points
        }

        for(int i = 1;i<items.length;i++){ //calculation of total material in roads
            String[] parts = items[i].split("\t");
            totalMaterial += Integer.parseInt(parts[2]);
        }
    }

    /**
     * Finds possible roads from the currently selected point and populates the
     * possiblePointsList. It also collects all unique points for further use.
     *
     * @param items An array of strings where each string represents a road with
     *              start point, end point, distance, and ID separated by tabs.
     */
    public void FindRoads(String[] items) {
        possiblePointsList.clear();
        for (int i = 1; i < items.length; i++) {
            String[] parts = items[i].split("\t");
            if (parts[0].equals(selectedPoint.getEndPoint())) {
                String startPoint2 = parts[0];
                String endPoint2 = parts[1];
                int distance = Integer.parseInt(parts[2]) + selectedPoint.getDistance();
                int id = Integer.parseInt(parts[3]);
                Point point = new Point(distance, startPoint2, endPoint2, id);
                point.setPrevious(selectedPoint);
                possiblePointsList.add(point);
            }
            if (parts[1].equals(selectedPoint.getEndPoint())) {
                String startPoint2 = parts[1];
                String endPoint2 = parts[0];
                int distance = Integer.parseInt(parts[2]) + selectedPoint.getDistance();
                int id = Integer.parseInt(parts[3]);
                Point point = new Point(distance, startPoint2, endPoint2, id);
                point.setPrevious(selectedPoint);
                possiblePointsList.add(point);
            }

            String startPoint3 = parts[0];
            String endPoint3 = parts[1];
            if(!allPoints.contains(startPoint3)){ //collect all of the unique points, we will use this part to barely connected map
                allPoints.add(startPoint3);
            }
            if(!allPoints.contains(endPoint3)){
                allPoints.add(endPoint3);
            }

        }
        Collections.sort(allPoints);  // sort all points alphabetically
    }

    /**
     * Finds the shortest path from the start point to the end point.
     *
     * @param items An array of strings where each string represents a road with
     *              start point, end point, distance, and ID separated by tabs.
     * @param args  Command line arguments passed to the program, used for output file name.
     */
    public void findShortestPath(String[] items,String[] args) {
        pointsList.add(startPoint);

        while (!pointsList.isEmpty()) { //the loop will continue until fastest road is completely found
            selectedPoint = getShortestDistancePoint();

            if (selectedPoint.getEndPoint().equals(endPoint)) {
                printPath(selectedPoint,items,args);
                return;
            }

            FindRoads(items);

            for (Point point : possiblePointsList) {
                if (!pointList2.containsKey(point.getEndPoint()) ||
                        pointList2.get(point.getEndPoint()).getDistance() > point.getDistance()) {
                    pointList2.put(point.getEndPoint(), point);
                    pointsList.add(point);
                }
            }
        }
    }

    /**
     * Retrieves the point with the shortest distance from the pointsList.
     *
     * @return The point with the shortest distance.
     */
    public Point getShortestDistancePoint() {
        Point shortestDistancePoint = pointsList.get(0);
        for (Point point : pointsList) {
            if (point.getDistance() < shortestDistancePoint.getDistance()) {
                shortestDistancePoint = point;
            }
            else if(point.getDistance()== shortestDistancePoint.getDistance()){
                if(point.getID() < shortestDistancePoint.getID()){
                    shortestDistancePoint = point;
                }
            }
        }
        pointsList.remove(shortestDistancePoint);
        return shortestDistancePoint;
    }

    /**
     * Prints the path from start point to the given end point and writes it to a file.
     *
     * @param endPoint2 The endpoint of the path to be printed.
     * @param items     An array of strings where each string represents a road with
     *                  start point, end point, distance, and ID separated by tabs.
     * @param args      Command line arguments passed to the program, used for output file name.
     */
    public void printPath(Point endPoint2,String[] items,String[] args) {
        //start from endpoint and go back until start point, using this logic we are creating path of fastest road

        Point current = endPoint2;
        while (current != null) {
            fastestWays.put(current.getEndPoint(), current);
            current = current.getPrevious();
        }

        FileOutput.writeToFile(args[1],"Fastest Route from " + startPoint.getEndPoint() + " to " + endPoint + " (" + endPoint2.getDistance() + " KM):",true,true);

        ArrayList<String> path = new ArrayList<>(fastestWays.keySet());
        Collections.reverse(path);

        int b = 0;
        while (b < path.size() - 1) {
            for (String item : items) {
                String[] parts = item.split("\t");
                if ((parts[0].equals(path.get(b)) && parts[1].equals(path.get(b + 1))) ||
                        (parts[1].equals(path.get(b)) && parts[0].equals(path.get(b + 1)))) {
                    FileOutput.writeToFile(args[1],item,true,true);
                    fastestRoadLength += Integer.parseInt(parts[2]);
                    b++;
                    break;
                }
            }
        }
    }

    /**
     * Checks if a point exists in the possiblePointsList2.
     *
     * @param point              The point to be checked.
     * @param possiblePointsList2 The list of possible points.
     * @return True if the point exists in the list, otherwise false.
     */
    public boolean checkPossibleRoads(Point point, ArrayList<Point> possiblePointsList2){
        //this method using for barely connected map
        for(Point point2: possiblePointsList2){
            if(point2.getEndPoint().equals(point.getEndPoint()) && point2.getStartPoint().equals(point.getStartPoint())){
                return true;
            }
        }
        return false;
    }

    /**
     * Finds possible roads based on the barely connected map and populates the possiblePointsList2.
     *
     * @param barelyConnectedMap  The barely connected map as a HashMap.
     * @param possiblePointsList2 The list of possible points.
     * @param items               An array of strings where each string represents a road with
     *                            start point, end point, distance, and ID separated by tabs.
     */
    public void findPossibleRoads(HashMap<String,Point> barelyConnectedMap,ArrayList<Point> possiblePointsList2,String[] items){
        possiblePointsList2.clear();
        for ( int i = 1; i < items.length; i++){
            String[] parts = items[i].split("\t");
            for(Point selectedPoint: barelyConnectedMap.values()){
                if(parts[0].equals(selectedPoint.getEndPoint())){
                    String startPoint2 = parts[0];
                    String endPoint2 = parts[1];
                    int distance = Integer.parseInt(parts[2]);
                    int id = Integer.parseInt(parts[3]);
                    Point point = new Point(distance, startPoint2, endPoint2, id);
                    if(!barelyConnectedMap.containsKey(point.getEndPoint()) && !checkPossibleRoads(point,possiblePointsList2)) {
                        possiblePointsList2.add(point);
                    }
                }
                if(parts[1].equals(selectedPoint.getEndPoint())){
                    String startPoint2 = parts[1];
                    String endPoint2 = parts[0];
                    int distance = Integer.parseInt(parts[2]);
                    int id = Integer.parseInt(parts[3]);
                    Point point = new Point(distance, startPoint2, endPoint2, id);
                    if(!barelyConnectedMap.containsKey(point.getEndPoint())&& !checkPossibleRoads(point,possiblePointsList2)) {
                        possiblePointsList2.add(point);
                    }
                }
            }
        }
        Collections.sort(possiblePointsList2, new Comparator<Point>() {
            @Override
            public int compare(Point p1, Point p2) {
                int distanceComparison = Integer.compare(p1.getDistance(), p2.getDistance());
                if (distanceComparison == 0) {
                    return Integer.compare(p1.getID(), p2.getID());
                } else {
                    return distanceComparison;
                }
            }
        });

    }

    /**
     * Generates a barely connected map from the given points and writes the results to a file.
     *
     * @param items An array of strings where each string represents a road with
     *              start point, end point, distance, and ID separated by tabs.
     * @param args  Command line arguments passed to the program, used for output file name.
     */
    public void barelyConnectedMap(String[] items,String[] args){
        LinkedHashMap<String,Point> barelyConnectedMap = new LinkedHashMap<>();
        ArrayList<Point> possiblePointsList2 = new ArrayList<>();
        ArrayList<Point> usedRoads = new ArrayList<>();

        Point newPoint = new Point(0,"", allPoints.get(0),0 ); // select the first point in the alphabetically sorted list
        barelyConnectedMap.put(newPoint.getEndPoint(),newPoint);

        while(barelyConnectedMap.size() < allPoints.size()){ // loop until all points are selected
            findPossibleRoads(barelyConnectedMap,possiblePointsList2,items);

            newPoint = possiblePointsList2.get(0);
            barelyConnectedMap.put(newPoint.getEndPoint(), newPoint);
            Point Road = new Point(0,newPoint.getStartPoint(),newPoint.getEndPoint(), 0);
            usedRoads.add(Road);
        }

        ArrayList<Point> orderedMap = new ArrayList<>();

        int k = 0;
        while(k < usedRoads.size() ){
            outerLoop:
            for(Point Road: usedRoads){
                for(String item: items){
                    String[] parts = item.split("\t");
                    if(parts[0].equals(Road.getStartPoint()) && parts[1].equals(Road.getEndPoint())){
                        int distance = Integer.parseInt(parts[2]);
                        int id = Integer.parseInt(parts[3]);
                        Point point = new Point(distance,parts[0],parts[1],id);
                        orderedMap.add(point);
                        k++;
                        continue outerLoop;
                    }else if(parts[1].equals(Road.getStartPoint()) && parts[0].equals(Road.getEndPoint())){
                        int distance = Integer.parseInt(parts[2]);
                        int id = Integer.parseInt(parts[3]);
                        Point point = new Point(distance,parts[0],parts[1],id);
                        orderedMap.add(point);
                        k++;
                        continue outerLoop;
                    }
                }
            }
        }

        Collections.sort(orderedMap, new Comparator<Point>() {
            @Override
            public int compare(Point p1, Point p2) {
                int distanceComparison = Integer.compare(p1.getDistance(), p2.getDistance());
                if (distanceComparison == 0) {
                    return Integer.compare(p1.getID(), p2.getID());
                } else {
                    return distanceComparison;
                }
            }
        });

        ArrayList<String> items2 = new ArrayList<>();
        items2.add("sude\tnaz");  //random line for array, just for make similar to input file
        FileOutput.writeToFile(args[1],"Roads of Barely Connected Map is:",true,true);

        for (Point point: orderedMap){
            String item = point.getStartPoint()+"\t"+point.getEndPoint()+"\t"+point.getDistance()+"\t"+point.getID();
            FileOutput.writeToFile(args[1],item,true,true);
            items2.add(item);
            barelyConnectedMapMaterial += point.getDistance();
        }

        items2Array = items2.toArray(new String[0]);

        secondFastestRoad(args);
    }

    /**
     * Finds the fastest route from the start point to the end point using the
     * barely connected map and writes the results to a file.
     *
     * @param args Command line arguments passed to the program, used for output file name.
     */
    public void secondFastestRoad(String[] args){
        pointList2.clear();
        pointsList.clear();
        fastestWays.clear();
        possiblePointsList.clear();

        pointsList.add(startPoint);

        while (!pointsList.isEmpty()) {
            selectedPoint = getShortestDistancePoint();

            if (selectedPoint.getEndPoint().equals(endPoint)) {
                printSecondPath(selectedPoint,items2Array,args);
                return;
            }

            FindRoads(items2Array);

            for (Point point : possiblePointsList) {
                if (!pointList2.containsKey(point.getEndPoint()) ||
                        pointList2.get(point.getEndPoint()).getDistance() > point.getDistance()) {
                    pointList2.put(point.getEndPoint(), point);
                    pointsList.add(point);
                }
            }
        }

    }

    /**
     * Prints the second fastest path from start point to the given end point and writes it to a file.
     *
     * @param endPoint2 The endpoint of the path to be printed.
     * @param items     An array of strings where each string represents a road with
     *                  start point, end point, distance, and ID separated by tabs.
     * @param args      Command line arguments passed to the program, used for output file name.
     */

    public void printSecondPath(Point endPoint2,String[] items,String[] args){
        Point current = endPoint2;
        while (current != null) {
            fastestWays.put(current.getEndPoint(), current);
            current = current.getPrevious();
        }

        FileOutput.writeToFile(args[1],"Fastest Route from " + startPoint.getEndPoint() + " to " + endPoint + " on Barely Connected Map (" + endPoint2.getDistance() + " KM):",true,true);

        ArrayList<String> path = new ArrayList<>(fastestWays.keySet());
        Collections.reverse(path);

        int b = 0;
        while (b < path.size() - 1) {
            for (String item : items) {
                String[] parts = item.split("\t");
                if ((parts[0].equals(path.get(b)) && parts[1].equals(path.get(b + 1))) ||
                        (parts[1].equals(path.get(b)) && parts[0].equals(path.get(b + 1)))) {
                    FileOutput.writeToFile(args[1],item,true,true);
                    barelyConnectedRoadLength += Integer.parseInt(parts[2]);
                    b++;
                    break;
                }
            }
        }
    }

    /**
     * Performs an analysis comparing the barely connected map with the original map
     * and writes the results to a file.
     *
     * @param args Command line arguments passed to the program, used for output file name.
     */
    public void Analysis(String[] args){
        double ratio1 = (double) barelyConnectedMapMaterial / totalMaterial;
        double ratio2 = (double) barelyConnectedRoadLength / fastestRoadLength;
        FileOutput.writeToFile(args[1],"Analysis:",true,true);
        FileOutput.writeToFile(args[1],String.format(Locale.US, "Ratio of Construction Material Usage Between Barely Connected and Original Map: %.2f",ratio1),true,true);
        FileOutput.writeToFile(args[1],String.format(Locale.US, "Ratio of Fastest Route Between Barely Connected and Original Map: %.2f",ratio2),true,false);
    }
}
