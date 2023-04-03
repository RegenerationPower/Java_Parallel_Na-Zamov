package laba1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    private static final String INPUT_FILENAME = "src\\main\\java\\laba1\\input.txt";
    private static final String OUTPUT_FILENAME = "src\\main\\java\\laba1\\output.txt";
    private static volatile double[] r1Y;
    private static volatile double[][] r1MA;
    public static void main(String[] args) {
        long startTime = System.nanoTime();
        Functions functions = new Functions();
        try {
            String inputFilePath = new File(INPUT_FILENAME).getAbsolutePath();
            Scanner scanner = new Scanner(new File(inputFilePath));
            int sizeMT = scanner.nextInt();
            int sizeMZ = scanner.nextInt();
            int sizeB = scanner.nextInt();
            int sizeD = scanner.nextInt();
            scanner.close();

//            double[][] MT = functions.generateRandomMatrix(sizeMT, sizeMT);
//            double[][] MZ = functions.generateRandomMatrix(sizeMZ, sizeMZ);
//            double[] B = functions.generateRandomArray(sizeB);
//            double[] D = functions.generateRandomArray(sizeD);

            double[][] MT = new double[sizeMT][sizeMT];
            double[][] MZ = new double[sizeMZ][sizeMZ];
            double[] B = new double[sizeB];
            double[] D = new double[sizeD];

            functions.readMatrix("src/main/java/laba1/inputs/MT.txt", MT);
            functions.readMatrix("src/main/java/laba1/inputs/MZ.txt", MZ);
            functions.readVector("src/main/java/laba1/inputs/B.txt", B);
            functions.readVector("src/main/java/laba1/inputs/D.txt", D);
//            functions.readMatrix("src/main/java/laba1/inputs/MT2.txt", MT);
//            functions.readMatrix("src/main/java/laba1/inputs/MZ2.txt", MZ);
//            functions.readVector("src/main/java/laba1/inputs/B2.txt", B);
//            functions.readVector("src/main/java/laba1/inputs/D2.txt", D);

//            functions.writeMatrixToFile(MT, "src/main/java/laba1/inputs/MT.txt");
//            functions.writeMatrixToFile(MZ, "src/main/java/laba1/inputs/MZ.txt");
//            functions.writeVectorToFile(B, "src/main/java/laba1/inputs/B.txt");
//            functions.writeVectorToFile(D, "src/main/java/laba1/inputs/D.txt");
//            functions.writeMatrixToFile(MT, "src/main/java/laba1/inputs/MT2.txt");
//            functions.writeMatrixToFile(MZ, "src/main/java/laba1/inputs/MZ2.txt");
//            functions.writeVectorToFile(B, "src/main/java/laba1/inputs/B2.txt");
//            functions.writeVectorToFile(D, "src/main/java/laba1/inputs/D2.txt");

            String outputFilePath = new File(OUTPUT_FILENAME).getAbsolutePath();
            PrintWriter writer = new PrintWriter(outputFilePath);

            r1Y = new double[sizeD];
            double[] result1 = new double[sizeD];
            double[] result2 = new double[sizeD];
            r1MA = new double[sizeMT][sizeMT];
            double[][] result3 = new double[sizeMT][sizeMT];
            double[][] result4 = new double[sizeMT][sizeMT];

            Thread t1 = new Thread(new Runnable() {
                public void run() {
                    synchronized(Main.class) {
                        r1Y = functions.multiplyVectorByMatrix(D, MT);
                        System.arraycopy(r1Y, 0, result1, 0, r1Y.length);
                        System.out.println("\nResult 1: " + Arrays.toString(r1Y));
                        writer.println("\nResult 1: " + Arrays.toString(result1));
                    }
                }
            });

            Thread t2 = new Thread(new Runnable() {
                public void run() {
                    synchronized(Main.class) {
                        double[] r2 = functions.multiplyVectorByScalar(D, functions.findMaxValue(B));
                        double[] r = functions.addVectorToVector(r1Y, r2);
                        System.out.println("sdffds" + Arrays.toString(r1Y));
                        System.arraycopy(r, 0, result2, 0, r.length);
                        System.out.println("\nResult 2: " + Arrays.toString(r));
                        writer.println("\nResult 2: " + Arrays.toString(result2));
                    }
                }
            });

            Thread t3 = new Thread(new Runnable() {
                public void run() {
                    synchronized(Main.class) {
                        r1MA = functions.multiplyMatrixByMatrix(MT, functions.addMatrixToMatrix(MT, MZ));
                        for (int i = 0; i < r1MA.length; i++) {
                            System.arraycopy(r1MA[i], 0, result3[i], 0, r1MA[i].length);
                        }
                        System.out.println("\nResult 3: " + Arrays.deepToString(r1MA));
                        writer.println("\nResult 3: " + Arrays.deepToString(result3));
                    }
                }
            });

            Thread t4 = new Thread(new Runnable() {
                public void run() {
                    synchronized(Main.class) {
                        double[][] r2 = functions.multiplyMatrixByMatrix(MZ, MT);
                        double[][] r = functions.subtractMatrix(r1MA, r2);
                        for (int i = 0; i < r.length; i++) {
                            System.arraycopy(r[i], 0, result4[i], 0, r[i].length);
                        }
                        System.out.println("\nResult 4: " + Arrays.deepToString(r));
                        writer.println("\nResult 4: " + Arrays.deepToString(result4));
                    }
                }
            });

            t1.start();
            t2.start();
            t3.start();
            t4.start();

            try {
                t1.join();
                t2.join();
                t3.join();
                t4.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            double[] Y = new double[result1.length];
            for (int i = 0; i < Y.length; i++) {
                Y[i] = result1[i] + result2[i];
            }

            double[][] MA = new double[result3.length][result3[0].length];
            for (int i = 0; i < MA.length; i++) {
                for (int j = 0; j < MA[0].length; j++) {
                    MA[i][j] = result3[i][j] + result4[i][j];
                }
            }

            System.out.println("\nFinal results: \nY=" + Arrays.toString(Y) + "\n\nMA=" + Arrays.deepToString(MA));
            writer.println("\nFinal results: \nY=" + Arrays.toString(Y) + "\n\nMA=" + Arrays.deepToString(MA));

            long endTime = System.nanoTime();
            long resultTime = (endTime - startTime);
            System.out.println("\nProgram duration: " + resultTime + " ns");
            writer.println("\nProgram duration: " + resultTime + " ns");
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
