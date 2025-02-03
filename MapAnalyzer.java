import java.util.Locale;

/**
 * This class reads datas from a file,
 * analyzes the map to find the shortest path, generates a barely connected map, and
 * performs an analysis comparing the original map and the barely connected map.
 */

public class MapAnalyzer {
    /**
     * The main method that drives the program. It sets the default locale, reads the input file,
     * clears the output file, and then performs the map analysis.
     *
     * @param args Command line arguments where args[0] is the input file path and args[1] is the output file path.
     */
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        String[] items = FileInput.readFile(args[0], true, true);  // assuming FileInput class handles file reading

        //Clear the output file
        FileOutput.writeToFile(args[1], "", false, false);  // assuming FileOutput class handles file writing

        Map map = new Map();
        map.ReadData(items);
        map.findShortestPath(items,args);
        map.barelyConnectedMap(items,args);
        map.Analysis(args);


    }

}