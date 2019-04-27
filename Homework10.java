import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Deque;
import java.util.LinkedList;

public class Homework10 {
    static double[][] matrix;
    static String[][] conversionMatrix;
    static int[][] path;
    static int n; // Represents how many nodes there are
    static int INF = Integer.MAX_VALUE; // Artificial infinity

    static void readInput(File inputFile) { // Reads input file and creates the matrix
        try {
            BufferedReader bufferedreader = new BufferedReader(new FileReader(inputFile));
            String st;
            boolean hasReadLineOne = false;

            while ((st = bufferedreader.readLine()) != null) {
                String[] stringArray = st.split(" "); // stringArray is the line x of the input file as an array separated by spaces
                if (!hasReadLineOne) {
                    hasReadLineOne = !hasReadLineOne;
                    n = Integer.parseInt(stringArray[0]) + 1;
                    matrix = new double[n][n];
                    conversionMatrix = new String[n][n];

                    for (int i = 0; i < n; i++) { // Initialize the matrix to be int max
                        for (int j = 0; j < n; j++) {
                            matrix[i][j] = INF;
                        }
                    }
                } else {
                    addStringToMatrix(stringArray);
                    addStringToConversionMatrix(stringArray);
                }
            }
        } catch (Exception err) {
            System.out.println(err);
            System.exit(-1);
        }
    }

    static void createOutput(String outputFile, boolean hasNegativeCycle, Object[] negativeCyclePath) {
        try {
            PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
            System.out.println("Writing to " + outputFile);

            if (!hasNegativeCycle) {
                writer.println("no");
            } else {
                writer.println("yes");
                String[] output = new String[negativeCyclePath.length - 1];
                double netGain = 1;

                for (int i = 0; i < negativeCyclePath.length - 1; i++) {
                    int productOne = (int)negativeCyclePath[i];
                    int productTwo = (int)negativeCyclePath[i + 1];
                    double logOfAOverB = matrix[productOne][productTwo];
                    double BOverA = Math.round(1 / Math.pow(10, logOfAOverB) * 100)/ 100.0;
                    String conversion = conversionMatrix[productOne][productTwo];

                    output[i] = productOne + " " + productTwo + " " + conversion;
                    writer.println(output[i]);
                    netGain *= BOverA;
                }

                DecimalFormat df = new DecimalFormat("#.#####");

                String conversionMessage = "1 kg of product "
                        + negativeCyclePath[0]
                        + " gets "
                        + df.format(netGain)
                        + " kg of product "
                        + negativeCyclePath[0]
                        + " from the above sequence.";
                writer.println(conversionMessage);
            }
            writer.close();
        } catch (Exception err) {
            System.out.println(err);
            System.exit(-1);
        }
    }

    static void addStringToMatrix(String[] stringArray) {
        int productOne = Integer.parseInt(stringArray[0]);
        int productTwo = Integer.parseInt(stringArray[1]);
        double productOneConversion = Double.parseDouble(stringArray[2]);
        double productTwoConversion = Double.parseDouble(stringArray[3]);
        double weight = Math.log10(productOneConversion / productTwoConversion);

        matrix[productOne][productTwo] = weight;
    }

    static void addStringToConversionMatrix(String[] stringArray) {
        int productOne = Integer.parseInt(stringArray[0]);
        int productTwo = Integer.parseInt(stringArray[1]);
        String conversion = stringArray[2] + " " + stringArray[3];

        conversionMatrix[productOne][productTwo] = conversion;
    }

    static boolean hasNegativeCycle() {
        double dist[][] = new double[n][n];
        path = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dist[i][j] = matrix[i][j]; // dist[i[[j] is the weight of edge u -> v
                if (matrix[i][j] != INF && i != j) {
                    path[i][j] = i;
                } else {
                    path[i][j] = -1;
                }
            }
        }

        for (int k = 0; k < n; k++) { // Floyd Warshall
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        path[i][j] = path[k][j];
                    }
                }
            }
        }

        for (int i = 0; i < n; i++) { // Checks diagonals for a negative value
            if (dist[i][i] < 0) {
                return true;
            }
        }

        return false;
    }

    static Object[] getNegativeCyclePath(int[][] path, int start) { // Returns the negative cycle
        Deque<Integer> stack = new LinkedList<>(); // Will contain all the nodes on the path
        int end = start;

        stack.addFirst(end);
        while (true) {
            end = path[start][end]; // Traversing the path
            stack.addFirst(end);
            if (end == start) { // Breaks out of the loop when it finds itself
                break;
            }
        }

        return stack.toArray();
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Enter: java Homework7 input1.txt output1.txt");
            System.exit(-1);
        }

        readInput(new File(args[0]));
        createOutput(args[1], hasNegativeCycle(), getNegativeCyclePath(path, path[1][1]));
    }
}
