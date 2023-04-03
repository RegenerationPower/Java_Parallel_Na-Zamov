package laba5;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;

class FourthTask extends RecursiveTask<double[][]> {
    private final Functions functions;
    private final double[][] MT;
    private final double[][] MZ;
    private final AtomicReference<double[][]> r1MA;
    private final double[][] result4;
    private final PrintWriter writer;
    private final Lock lock;
    private final CountDownLatch latch;

    FourthTask(Functions functions, double[][] MT, double[][] MZ, AtomicReference<double[][]> r1MA, double[][] result4, PrintWriter writer, Lock lock, CountDownLatch latch) {
        this.functions = functions;
        this.MT = MT;
        this.MZ = MZ;
        this.r1MA = r1MA;
        this.result4 = result4;
        this.writer = writer;
        this.lock = lock;
        this.latch = latch;
    }

    @Override
    protected double[][] compute() {
        double[][] r2 = functions.multiplyMatrixByMatrix(MZ, MT);
        double[][] r;
        lock.lock();
        try {
            r = functions.subtractMatrix(r1MA.get(), r2);
            for (int i = 0; i < r.length; i++) {
                System.arraycopy(r[i], 0, result4[i], 0, r[i].length);
            }
            System.out.println("\nResult 4: " + Arrays.deepToString(r));
            writer.println("\nResult 4: " + Arrays.deepToString(result4));
        } finally {
            lock.unlock();
        }
        latch.countDown();
        return r;
    }

    public static ForkJoinTask<double[][]> createTask(Functions functions, double[][] MT, double[][] MZ, AtomicReference<double[][]> r1MA, double[][] result4, PrintWriter writer, Lock lock, CountDownLatch latch) {
        return new FourthTask(functions, MT, MZ, r1MA, result4, writer, lock, latch);
    }
}
