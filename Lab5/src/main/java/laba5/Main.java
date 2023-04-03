package laba5;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    private static final String OUTPUT_FILENAME = "src\\main\\java\\laba5\\output.txt";
    public static void main(String[] args) {
        long startTime = System.nanoTime();
        Functions functions = new Functions();
        try {
            String[] fileNames = {"src/main/java/laba5/inputs/MT.txt", "src/main/java/laba5/inputs/MZ.txt",
                    "src/main/java/laba5/inputs/B.txt", "src/main/java/laba5/inputs/D.txt"};
            String[] fileNames2 = {"src/main/java/laba5/inputs/MT2.txt", "src/main/java/laba5/inputs/MZ2.txt",
                    "src/main/java/laba5/inputs/B2.txt", "src/main/java/laba5/inputs/D2.txt"};

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

            functions.readMatrix("src/main/java/laba5/inputs/MT.txt", MT);
            functions.readMatrix("src/main/java/laba5/inputs/MZ.txt", MZ);
            functions.readVector("src/main/java/laba5/inputs/B.txt", B);
            functions.readVector("src/main/java/laba5/inputs/D.txt", D);
//            functions.readMatrix("src/main/java/laba5/inputs/MT2.txt", MT);
//            functions.readMatrix("src/main/java/laba5/inputs/MZ2.txt", MZ);
//            functions.readVector("src/main/java/laba5/inputs/B2.txt", B);
//            functions.readVector("src/main/java/laba5/inputs/D2.txt", D);

            String outputFilePath = new File(OUTPUT_FILENAME).getAbsolutePath();
            PrintWriter writer = new PrintWriter(outputFilePath);

            AtomicReference<double[]> r1Y = new AtomicReference<>();
            AtomicReference<double[][]> r1MA = new AtomicReference<>();
            double[] result1 = new double[sizeD];
            double[] result2 = new double[sizeD];
            double[][] result3 = new double[sizeMT][sizeMT];
            double[][] result4 = new double[sizeMT][sizeMT];

            CountDownLatch latch = new CountDownLatch(4);
            ExecutorService executor = Executors.newFixedThreadPool(4);
            Lock lock = new ReentrantLock();
            ForkJoinPool pool = new ForkJoinPool();

            FirstTask first = (FirstTask) FirstTask.createTask(functions, MT, D, r1Y, result1, writer, lock, latch);
            result1 = pool.invoke(first);

            SecondTask second = (SecondTask) SecondTask.createTask(functions, B, D, r1Y, result2, writer, lock, latch);
            result2 = pool.invoke(second);

            ThirdTask third = (ThirdTask) ThirdTask.createTask(functions, MT, MZ, r1MA, result3, writer, lock, latch);
            result3 = pool.invoke(third);

            FourthTask fourth = (FourthTask) FourthTask.createTask(functions, MT, MZ, r1MA, result4, writer, lock, latch);
            result4 = pool.invoke(fourth);

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
