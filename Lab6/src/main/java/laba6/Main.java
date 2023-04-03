package laba6;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.*;

public class Main {
    private static final String OUTPUT_FILENAME = "src\\main\\java\\laba6\\output.txt";
    public static void main(String[] args) {
        long startTime = System.nanoTime();
        Functions functions = new Functions();
        try {
            String[] fileNames = {"src/main/java/laba6/inputs/MT.txt", "src/main/java/laba6/inputs/MZ.txt",
                    "src/main/java/laba6/inputs/B.txt", "src/main/java/laba6/inputs/D.txt"};
            String[] fileNames2 = {"src/main/java/laba6/inputs/MT2.txt", "src/main/java/laba6/inputs/MZ2.txt",
                    "src/main/java/laba6/inputs/B2.txt", "src/main/java/laba6/inputs/D2.txt"};

            int[] fileLengths = new int[4];
            for (int i = 0; i < fileNames2.length; i++) { // change between fileNames and fileNames2
                try (BufferedReader br = new BufferedReader(new FileReader(fileNames2[i]))) { // change between fileNames and fileNames2
                    String line;
                    int count = 0;
                    while ((line = br.readLine()) != null) {
                        String[] values = line.trim().split("\\s+");
                        count = values.length;
                    }
                    fileLengths[i] = count;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            int sizeMT = fileLengths[0];
            int sizeMZ = fileLengths[1];
            int sizeB = fileLengths[2];
            int sizeD = fileLengths[3];
            double[][] MT = new double[sizeMT][sizeMT];
            double[][] MZ = new double[sizeMZ][sizeMZ];
            double[] B = new double[sizeB];
            double[] D = new double[sizeD];

//            functions.readMatrix("src/main/java/laba6/inputs/MT.txt", MT);
//            functions.readMatrix("src/main/java/laba6/inputs/MZ.txt", MZ);
//            functions.readVector("src/main/java/laba6/inputs/B.txt", B);
//            functions.readVector("src/main/java/laba6/inputs/D.txt", D);
            functions.readMatrix("src/main/java/laba6/inputs/MT2.txt", MT);
            functions.readMatrix("src/main/java/laba6/inputs/MZ2.txt", MZ);
            functions.readVector("src/main/java/laba6/inputs/B2.txt", B);
            functions.readVector("src/main/java/laba6/inputs/D2.txt", D);

            String outputFilePath = new File(OUTPUT_FILENAME).getAbsolutePath();
            PrintWriter writer = new PrintWriter(outputFilePath);

            ExecutorService executor = Executors.newFixedThreadPool(4);
            BlockingQueue<double[]> queue1 = new ArrayBlockingQueue<>(1);
            BlockingQueue<double[][]> queue3 = new ArrayBlockingQueue<>(1);

            Callable<double[]> task1 = () -> {
                double[] r1Y = functions.multiplyVectorByMatrix(D, MT);
                try {
                    queue1.put(r1Y);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
                System.out.println("\nResult 1: " + Arrays.toString(r1Y));
                writer.println("\nResult 1: " + Arrays.toString(r1Y));
                return r1Y;
            };

            Callable<double[]> task2 = () -> {
                double[] r2 = functions.multiplyVectorByScalar(D, functions.findMaxValue(B));
                double[] r;
                double[] r1 = queue1.take();
                r = functions.addVectorToVector(r1, r2);
                System.out.println("\nResult 2: " + Arrays.toString(r));
                writer.println("\nResult 2: " + Arrays.toString(r));
                return r;
            };

            Callable<double[][]> task3 = () -> {
                double[][] r1MA = functions.multiplyMatrixByMatrix(MT, functions.addMatrixToMatrix(MT, MZ));
                try {
                    queue3.put(r1MA);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
                System.out.println("\nResult 3: " + Arrays.deepToString(r1MA));
                writer.println("\nResult 3: " + Arrays.deepToString(r1MA));
                return r1MA;
            };

            Callable<double[][]> task4 = () -> {
                double[][] r2 = functions.multiplyMatrixByMatrix(MZ, MT);
                double[][] r;
                double[][] r3 = queue3.take();
                r = functions.subtractMatrix(r3, r2);
                System.out.println("\nResult 4: " + Arrays.deepToString(r));
                writer.println("\nResult 4: " + Arrays.deepToString(r));
                return r;
            };

            Future<double[]> future1 = executor.submit(task1);
            Future<double[]> future2 = executor.submit(task2);
            Future<double[][]> future3 = executor.submit(task3);
            Future<double[][]> future4 = executor.submit(task4);

            try {
                future1.get();
                future2.get();
                future3.get();
                future4.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } finally {
                executor.shutdown();
            }

            double[] result1 = future1.get();
            double[] result2 = future2.get();
            double[][] result3 = future3.get();
            double[][] result4 = future4.get();

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

        } catch (FileNotFoundException | InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}

