package laba3;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

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
            for (int i = 0; i < fileNames.length; i++) { // change between fileNames and fileNames2
                try (BufferedReader br = new BufferedReader(new FileReader(fileNames[i]))) { // change between fileNames and fileNames2
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

            functions.readMatrix("src/main/java/laba3/inputs/MT.txt", MT);
            functions.readMatrix("src/main/java/laba3/inputs/MZ.txt", MZ);
            functions.readVector("src/main/java/laba3/inputs/B.txt", B);
            functions.readVector("src/main/java/laba3/inputs/D.txt", D);
//            functions.readMatrix("src/main/java/laba3/inputs/MT2.txt", MT);
//            functions.readMatrix("src/main/java/laba3/inputs/MZ2.txt", MZ);
//            functions.readVector("src/main/java/laba3/inputs/B2.txt", B);
//            functions.readVector("src/main/java/laba3/inputs/D2.txt", D);

            String outputFilePath = new File(OUTPUT_FILENAME).getAbsolutePath();
            PrintWriter writer = new PrintWriter(outputFilePath);

            double[] r1Y = new double[sizeD];
            double[] result1 = new double[sizeD];
            double[] result2 = new double[sizeD];
            double[][] r1MA = new double[sizeMT][sizeMT];
            double[][] result3 = new double[sizeMT][sizeMT];
            double[][] result4 = new double[sizeMT][sizeMT];

            Semaphore sem1 = new Semaphore(0);
            Semaphore sem2 = new Semaphore(0);
            Semaphore sem3 = new Semaphore(0);
            Semaphore sem4 = new Semaphore(0);
            CountDownLatch latch = new CountDownLatch(4);
            ExecutorService executor = Executors.newFixedThreadPool(4);

            executor.execute(() -> {
                synchronized(Main.class) {
                    double[] r1Y1 = functions.multiplyVectorByMatrix(D, MT);
                    System.arraycopy(r1Y1, 0, result1, 0, r1Y1.length);
                    System.out.println("\nResult 1: " + Arrays.toString(r1Y1));
                    writer.println("\nResult 1: " + Arrays.toString(result1));
                }
                sem1.release();
                latch.countDown();
            });

            executor.execute(() -> {
                try {
                    sem1.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized(Main.class) {
                    double[] r2 = functions.multiplyVectorByScalar(D, functions.findMaxValue(B));
                    double[] r = functions.addVectorToVector(r1Y, r2);
                    System.arraycopy(r, 0, result2, 0, r.length);
                    System.out.println("\nResult 2: " + Arrays.toString(r));
                    writer.println("\nResult 2: " + Arrays.toString(result2));
                }
                sem2.release();
                latch.countDown();
            });

            executor.execute(() -> {
                synchronized(Main.class) {
                    double[][] r1MA1 = functions.multiplyMatrixByMatrix(MT, functions.addMatrixToMatrix(MT, MZ));
                    for (int i = 0; i < r1MA1.length; i++) {
                        System.arraycopy(r1MA1[i], 0, result3[i], 0, r1MA1[i].length);
                    }
                    System.out.println("\nResult 3: " + Arrays.deepToString(r1MA1));
                    writer.println("\nResult 3: " + Arrays.deepToString(result3));
                }
                sem3.release();
                latch.countDown();
            });

            executor.execute(() -> {
                try {
                    sem3.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized(Main.class) {
                    double[][] r2 = functions.multiplyMatrixByMatrix(MZ, MT);
                    double[][] r = functions.subtractMatrix(r1MA, r2);
                    for (int i = 0; i < r.length; i++) {
                        System.arraycopy(r[i], 0, result4[i], 0, r[i].length);
                    }
                    System.out.println("\nResult 4: " + Arrays.deepToString(r));
                    writer.println("\nResult 4: " + Arrays.deepToString(result4));
                }
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
