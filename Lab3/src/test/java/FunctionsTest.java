import laba3.Functions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class FunctionsTest {
    private final Functions functions = new Functions();

    @Test
    public void testGenerateRandomMatrix() {
        int rows = 5;
        int cols = 4;

        double[][] matrix = functions.generateRandomMatrix(rows, cols);

        Assertions.assertEquals(matrix.length, rows);
        Assertions.assertEquals(matrix[0].length, cols);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Assertions.assertTrue(matrix[i][j] >= 0 && matrix[i][j] <= 1);
            }
        }
    }

    @Test
    public void testGenerateRandomArray() {
        int size = 10;

        double[] array = functions.generateRandomArray(size);

        Assertions.assertEquals(array.length, size);

        for (int i = 0; i < size; i++) {
            Assertions.assertTrue(array[i] >= 0 && array[i] <= 1);
        }
    }

    @Test
    public void testMultiplyMatrixByMatrix() {
        double[][] a = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        double[][] b = {{7.0, 8.0}, {9.0, 10.0}, {11.0, 12.0}};
        double[][] expected = {{58.0, 64.0}, {139.0, 154.0}};

        double[][] result = functions.multiplyMatrixByMatrix(a, b);

        assertArrayEquals(result, expected);
    }

    @Test
    public void testSubtractMatrix() {
        double[][] a = {{1.0, 2.0}, {3.0, 4.0}};
        double[][] b = {{5.0, 6.0}, {7.0, 8.0}};
        double[][] expected = {{-4.0, -4.0}, {-4.0, -4.0}};

        double[][] result = functions.subtractMatrix(a, b);

        assertArrayEquals(result, expected);
    }

    @Test
    public void testAddMatrixToMatrix() {
        double[][] A = {{1, 2}, {3, 4}};
        double[][] B = {{5, 6}, {7, 8}};
        double[][] expected = {{6, 8}, {10, 12}};
        double[][] result = functions.addMatrixToMatrix(A, B);
        assertArrayEquals(expected, result);
    }

    @Test
    public void testMultiplyVectorByScalar() {
        double[] vector = {1, 2, 3};
        double scalar = 2;
        double[] expected = {2, 4, 6};
        double[] result = functions.multiplyVectorByScalar(vector, scalar);
        assertArrayEquals(expected, result);
    }

    @Test
    public void testMultiplyVectorByMatrix() {
        double[] vector = {1, 2};
        double[][] matrix = {{3, 4}, {5, 6}};
        double[] expected = {13, 16};
        double[] result = functions.multiplyVectorByMatrix(vector, matrix);
        assertArrayEquals(expected, result);
    }

    @Test
    public void testAddVectorToVector() {
        double[] vector1 = {1, 2, 3};
        double[] vector2 = {4, 5, 6};
        double[] expected = {5, 7, 9};
        double[] result = functions.addVectorToVector(vector1, vector2);
        Assertions.assertArrayEquals(expected, result);
    }

    @Test
    public void testFindMaxValue() {
        double[] arr = {3, 5, 1, 4, 2};
        double expected = 5;
        double result = functions.findMaxValue(arr);
        Assertions.assertEquals(expected, result);
    }

}
