package ru.javaops.masterjava.matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {

                int sum = 0;
                List<Future<Integer>> list = new ArrayList<>();
                for (int k = 0; k < matrixSize; k++) {

                    int finalI = i;
                    int finalK = k;
                    int finalJ = j;
                    Future<Integer> future = executor.submit(() -> matrixA[finalI][finalK] * matrixB[finalK][finalJ]);
                    list.add(future);
                }

                for (Future<Integer> f : list) {
                    sum += f.get();
                }
                matrixC[i][j] = sum;
            }
        }
        return matrixC;
    }

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply2(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {

                int finalI = i;
                int finalJ = j;
                Future<Integer> future = executor.submit(() -> {
                    int sum = 0;
                    for (int k = 0; k < matrixSize; k++) {
                        sum += matrixA[finalI][k] * matrixB[k][finalJ];
                    }
                    return sum;
                });

                matrixC[i][j] = future.get();
            }
        }
        return matrixC;
    }

    public static int[][] concurrentMultiply3(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) {

            int finalI = i;
            Future<Integer[]> future = executor.submit(new Callable<Integer[]>() {
                @Override
                public Integer[] call() throws Exception {
                    Integer sum[] = new Integer[matrixSize];
                    for (int j = 0; j < matrixSize; j++) {
                        sum[j] = 0;
                        for (int k = 0; k < matrixSize; k++) {
                            sum[j] += matrixA[finalI][k] * matrixB[k][j];
                        }
                    }
                    return sum;
                }
            });

            Integer arr[] = future.get();
            for (int j = 0; j < matrixSize; j++) {
                matrixC[i][j] = arr[j];
            }
        }
        return matrixC;
    }

    public static int[][] concurrentMultiply4(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;

        Future<int[][]> future = executor.submit(() -> {

            final int[][] arr = new int[matrixSize][matrixSize];

            for (int i = 0; i < matrixSize; i++) {
                for (int j = 0; j < matrixSize; j++) {
                    int sum = 0;
                    for (int k = 0; k < matrixSize; k++) {
                        sum += matrixA[i][k] * matrixB[k][j];
                    }
                    arr[i][j] = sum;
                }
            }
            return arr;
        });

        return future.get();
    }


    // TODO optimize by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                int sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += matrixA[i][k] * matrixB[k][j];
                }
                matrixC[i][j] = sum;
            }
        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
