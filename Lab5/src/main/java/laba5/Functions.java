package laba5;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Scanner;

public class Functions {

    public double[][] multiplyMatrixByMatrix(double[][] a, double[][] b) {
        int rowsA = a.length;
        int colsA = a[0].length;
        int rowsB = b.length;
        int colsB = b[0].length;

        if (colsA != rowsB) {
            throw new IllegalArgumentException("Matrix should have the same length");
        }

        double[][] result = new double[rowsA][colsB];
        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                double sum = 0.0;
                double c = 0.0;
                for (int k = 0; k < colsA; k++) {
                    double y = a[i][k] * b[k][j] - c;
                    double t = sum + y;
                    c = (t - sum) - y;
                    sum = t;
                }
                result[i][j] = sum;
            }
        }
        return result;
    }

    public double[][] subtractMatrix(double[][] a, double[][] b) {
        int rowsA = a.length;
        int colsA = a[0].length;
        int rowsB = b.length;
        int colsB = b[0].length;

        if (rowsA != rowsB || colsA != colsB) {
            throw new IllegalArgumentException("Matrix should have the same length");
        }

        double[][] result = new double[rowsA][colsA];

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsA; j++) {
                result[i][j] = a[i][j] - b[i][j];
            }
        }
        return result;
    }

    public double[][] addMatrixToMatrix(double[][] A, double[][] B) {
        int rows = A.length;
        int cols = A[0].length;
        double[][] result = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = A[i][j] + B[i][j];
            }
        }
        return result;
    }

    public double[] multiplyVectorByScalar(double[] vector, double scalar) {
        double[] result = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            result[i] = vector[i] * scalar;
        }
        return result;
    }

    public double[] multiplyVectorByMatrix(double[] vector, double[][] matrix) {
        if (vector.length != matrix.length) {
            throw new IllegalArgumentException("Vector and matrix should have the same length");
        }
        double[] result = new double[matrix[0].length];
        for (int j = 0; j < matrix[0].length; j++) {
            double sum = 0.0;
            double c = 0.0;
            for (int i = 0; i < vector.length; i++) {
                double y = vector[i] * matrix[i][j] - c;
                double t = sum + y;
                c = (t - sum) - y;
                sum = t;
            }
            result[j] = sum;
        }
        return result;
    }

    public double[] addVectorToVector(double[] vector1, double[] vector2) {
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException("Vectors must be of the same length");
        }

        double[] result = new double[vector1.length];
        for (int i = 0; i < vector1.length; i++) {
            result[i] = vector1[i] + vector2[i];
        }
        return result;
    }

    public double findMaxValue(double[] arr) {
        double maxVal = Double.NEGATIVE_INFINITY;
        for (double val : arr) {
            if (val > maxVal) {
                maxVal = val;
            }
        }
        return maxVal;
    }

    public void writeMatrixToFile(double[][] array, String filename) throws FileNotFoundException {
        String filePath = new File(filename).getAbsolutePath();
        PrintWriter writer = new PrintWriter(filePath);
        for (double[] row : array) {
            for (double elem : row) {
                writer.printf(elem + " ");
            }
            writer.println();
        }
        writer.close();
    }

    public void writeVectorToFile(double[] array, String filename) throws FileNotFoundException {
        String filePath = new File(filename).getAbsolutePath();
        PrintWriter writer = new PrintWriter(filePath);
        for (double elem : array) {
            writer.printf(elem + " ");
        }
        writer.close();
    }

    public void readMatrix(String fileName, double[][] matrix) throws FileNotFoundException {
        File file = new File(fileName);
        Scanner scanner = new Scanner(file);
        scanner.useLocale(Locale.US);

        for (int i = 0; i < matrix.length; i++) {
            String[] values = scanner.nextLine().split(" ");
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = Double.parseDouble(values[j]);
            }
        }
        scanner.close();
    }

    public void readVector(String fileName, double[] vector) throws FileNotFoundException {
        File file = new File(fileName);
        Scanner scanner = new Scanner(file);
        scanner.useLocale(Locale.US);

        for (int i = 0; i < vector.length; i++) {
            vector[i] = scanner.nextDouble();
        }

        scanner.close();
    }

}
