package laba5;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;

class ThirdTask extends RecursiveTask<double[][]> {
    private final Functions functions;
    private final double[][] MT;
    private final double[][] MZ;
    private final AtomicReference<double[][]> r1MA;
    private final double[][] result3;
    private final PrintWriter writer;
    private final Lock lock;
    private final CountDownLatch latch;

    ThirdTask(Functions functions, double[][] MT, double[][] MZ, AtomicReference<double[][]> r1MA, double[][] result3, PrintWriter writer, Lock lock, CountDownLatch latch) {
        this.functions = functions;
        this.MT = MT;
        this.MZ = MZ;
        this.r1MA = r1MA;
        this.result3 = result3;
        this.writer = writer;
        this.lock = lock;
        this.latch = latch;
    }

    @Override
    protected double[][] compute() {
        double[][] result = functions.multiplyMatrixByMatrix(MT, functions.addMatrixToMatrix(MT, MZ));
        r1MA.set(result);
        lock.lock();
        try {
            for (int i = 0; i < result.length; i++) {
                System.arraycopy(result[i], 0, result3[i], 0, result[i].length);
            }
            System.out.println("\nResult 3: " + Arrays.deepToString(result));
            writer.println("\nResult 3: " + Arrays.deepToString(result3));
        } finally {
            lock.unlock();
        }
        latch.countDown();
        return result;
    }

    public static ForkJoinTask<double[][]> createTask(Functions functions, double[][] MT, double[][] MZ, AtomicReference<double[][]> r1MA, double[][] result3, PrintWriter writer, Lock lock, CountDownLatch latch) {
        return new ThirdTask(functions, MT, MZ, r1MA, result3, writer, lock, latch);
    }
}
