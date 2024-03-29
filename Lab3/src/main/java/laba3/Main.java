package laba3;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    private static final String OUTPUT_FILENAME = "src\\main\\java\\laba3\\output.txt";
    public static void main(String[] args) {
        long startTime = System.nanoTime();
        Functions functions = new Functions();
        try {
            String[] fileNames = {"src/main/java/laba3/inputs/MT.txt", "src/main/java/laba3/inputs/MZ.txt",
                    "src/main/java/laba3/inputs/B.txt", "src/main/java/laba3/inputs/D.txt"};
            String[] fileNames2 = {"src/main/java/laba3/inputs/MT2.txt", "src/main/java/laba3/inputs/MZ2.txt",
                    "src/main/java/laba3/inputs/B2.txt", "src/main/java/laba3/inputs/D2.txt"};

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

//            functions.readMatrix("src/main/java/laba3/inputs/MT.txt", MT);
//            functions.readMatrix("src/main/java/laba3/inputs/MZ.txt", MZ);
//            functions.readVector("src/main/java/laba3/inputs/B.txt", B);
//            functions.readVector("src/main/java/laba3/inputs/D.txt", D);
            functions.readMatrix("src/main/java/laba3/inputs/MT2.txt", MT);
            functions.readMatrix("src/main/java/laba3/inputs/MZ2.txt", MZ);
            functions.readVector("src/main/java/laba3/inputs/B2.txt", B);
            functions.readVector("src/main/java/laba3/inputs/D2.txt", D);

            String outputFilePath = new File(OUTPUT_FILENAME).getAbsolutePath();
            PrintWriter writer = new PrintWriter(outputFilePath);

            AtomicReference<double[]> r1Y = new AtomicReference<>();
            AtomicReference<double[][]> r1MA = new AtomicReference<>();
            double[] result1 = new double[sizeD];
            double[] result2 = new double[sizeD];
            double[][] result3 = new double[sizeMT][sizeMT];
            double[][] result4 = new double[sizeMT][sizeMT];

            Semaphore sem1 = new Semaphore(0);
            Semaphore sem2 = new Semaphore(0);
            Semaphore sem3 = new Semaphore(0);
            Semaphore sem4 = new Semaphore(0);
            CountDownLatch latch = new CountDownLatch(4);
            ExecutorService executor = Executors.newFixedThreadPool(4);

            executor.execute(() -> {
                double[] result = functions.multiplyVectorByMatrix(D, MT);
                r1Y.set(result);
                System.arraycopy(result, 0, result1, 0, result.length);
                System.out.println("\nResult 1: " + Arrays.toString(result));
                writer.println("\nResult 1: " + Arrays.toString(result1));
                sem1.release();
                latch.countDown();
            });

            executor.execute(() -> {
                try {
                    sem1.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                double[] r2 = functions.multiplyVectorByScalar(D, functions.findMaxValue(B));
                double[] r = functions.addVectorToVector(r1Y.get(), r2);
                System.arraycopy(r, 0, result2, 0, r.length);
                System.out.println("\nResult 2: " + Arrays.toString(r));
                writer.println("\nResult 2: " + Arrays.toString(result2));
                sem2.release();
                latch.countDown();
            });

            executor.execute(() -> {
                double[][] result = functions.multiplyMatrixByMatrix(MT, functions.addMatrixToMatrix(MT, MZ));
                r1MA.set(result);
                for (int i = 0; i < result.length; i++) {
                    System.arraycopy(result[i], 0, result3[i], 0, result[i].length);
                }
                System.out.println("\nResult 3: " + Arrays.deepToString(result));
                writer.println("\nResult 3: " + Arrays.deepToString(result3));
                sem3.release();
                latch.countDown();
            });

            executor.execute(() -> {
                try {
                    sem3.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                double[][] r2 = functions.multiplyMatrixByMatrix(MZ, MT);
                double[][] r = functions.subtractMatrix(r1MA.get(), r2);
                for (int i = 0; i < r.length; i++) {
                    System.arraycopy(r[i], 0, result4[i], 0, r[i].length);
                }
                System.out.println("\nResult 4: " + Arrays.deepToString(r));
                writer.println("\nResult 4: " + Arrays.deepToString(result4));
                sem4.release();
                latch.countDown();
            });

            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                executor.shutdown();
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
