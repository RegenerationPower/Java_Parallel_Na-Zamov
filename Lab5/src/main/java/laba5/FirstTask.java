package laba5;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;

class FirstTask extends RecursiveTask<double[]> {
    private final Functions functions;
    private final double[][] MT;
    private final double[] D;
    private final AtomicReference<double[]> r1Y;
    private final double[] result1;
    private final PrintWriter writer;
    private final Lock lock;
    private final CountDownLatch latch;

    FirstTask(Functions functions, double[][] MT, double[] D, AtomicReference<double[]> r1Y, double[] result1, PrintWriter writer, Lock lock, CountDownLatch latch) {
        this.functions = functions;
        this.MT = MT;
        this.D = D;
        this.r1Y = r1Y;
        this.result1 = result1;
        this.writer = writer;
        this.lock = lock;
        this.latch = latch;
    }

    @Override
    protected double[] compute() {
        double[] result = functions.multiplyVectorByMatrix(D, MT);
        r1Y.set(result);
        lock.lock();
        try {
            System.arraycopy(result, 0, result1, 0, result.length);
            System.out.println("\nResult 1: " + Arrays.toString(result));
            writer.println("\nResult 1: " + Arrays.toString(result1));
        } finally {
            lock.unlock();
        }
        latch.countDown();
        return result;
    }

    public static ForkJoinTask<double[]> createTask(Functions functions, double[][] MT, double[] D, AtomicReference<double[]> r1Y, double[] result1, PrintWriter writer, Lock lock, CountDownLatch latch) {
        return new FirstTask(functions, MT, D, r1Y, result1, writer, lock, latch);
    }
}
