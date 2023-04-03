package laba5;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;

class SecondTask extends RecursiveTask<double[]> {
    private final Functions functions;
    private final double[] B;
    private final double[] D;
    private final AtomicReference<double[]> r1Y;
    private final double[] result2;
    private final PrintWriter writer;
    private final Lock lock;
    private final CountDownLatch latch;

    SecondTask(Functions functions, double[] B, double[] D, AtomicReference<double[]> r1Y, double[] result2, PrintWriter writer, Lock lock, CountDownLatch latch) {
        this.functions = functions;
        this.B = B;
        this.D = D;
        this.r1Y = r1Y;
        this.result2 = result2;
        this.writer = writer;
        this.lock = lock;
        this.latch = latch;
    }

    @Override
    protected double[] compute() {
        double[] r2 = functions.multiplyVectorByScalar(D, functions.findMaxValue(B));
        double[] r;
        lock.lock();
        try {
            r = functions.addVectorToVector(r1Y.get(), r2);
            System.arraycopy(r, 0, result2, 0, r.length);
            System.out.println("\nResult 2: " + Arrays.toString(r));
            writer.println("\nResult 2: " + Arrays.toString(result2));
        } finally {
            lock.unlock();
        }
        latch.countDown();
        return r;
    }

    public static ForkJoinTask<double[]> createTask(Functions functions, double[] B, double[] D, AtomicReference<double[]> r1Y, double[] result2, PrintWriter writer, Lock lock, CountDownLatch latch) {
        return new SecondTask(functions, B, D, r1Y, result2, writer, lock, latch);
    }
}
