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

    //method with N=matrixSize^3 threads
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

    //method with N=matrixSize^2 threads
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

    //method with N=matrixSize threads
    public static int[][] concurrentMultiply3(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        for (int i = 0; i < matrixSize; i++) {

            int finalI = i;
            Future<int[]> future = executor.submit(new Callable<int[]>() {
                @Override
                public int[] call() throws Exception {
                    int sum[] = new int[matrixSize];
                    for (int j = 0; j < matrixSize; j++) {
                        sum[j] = 0;
                        for (int k = 0; k < matrixSize; k++) {
                            sum[j] += matrixA[finalI][k] * matrixB[k][j];
                        }
                    }
                    return sum;
                }
            });
            matrixC[i] = future.get();
        }
        return matrixC;
    }

    //method with 1 thread?
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

    //method with 2 threads
    public static int[][] concurrentMultiply5(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        Future<int[][]> future1 = executor.submit(() -> {
            final int[][] arr = new int[matrixSize / 2][matrixSize];
            for (int i = 0; i < matrixSize / 2; i++) {
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

        Future<int[][]> future2 = executor.submit(() -> {
            final int[][] arr = new int[matrixSize / 2][matrixSize];
            for (int i = matrixSize / 2; i < matrixSize; i++) {
                for (int j = 0; j < matrixSize; j++) {
                    int sum = 0;
                    for (int k = 0; k < matrixSize; k++) {
                        sum += matrixA[i][k] * matrixB[k][j];
                    }
                    arr[i - matrixSize / 2][j] = sum;
                }
            }
            return arr;
        });

        for (int i = 0; i < matrixSize / 2; i++) {
            System.arraycopy(future1.get()[i], 0, matrixC[i], 0, matrixSize);
            System.arraycopy(future2.get()[i], 0, matrixC[matrixSize / 2 + i], 0, matrixSize);
        }

        return matrixC;
    }

    //method with custom thread quantity
    public static int[][] concurrentMultiply6(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        int TM = 2; //threads quantity
        Future<int[][]> future[] = new Future[TM];
        for (int t = 0; t < TM; t++) {
            int finalT = t;
            future[t] = executor.submit(() -> {
                final int[][] arr = new int[matrixSize / TM][matrixSize];
                for (int i = matrixSize / TM * finalT; i < matrixSize / TM * (finalT + 1); i++) {
                    for (int j = 0; j < matrixSize; j++) {
                        int sum = 0;
                        for (int k = 0; k < matrixSize; k++) {
                            sum += matrixA[i][k] * matrixB[k][j];
                        }
                        arr [i - matrixSize / TM * finalT][j] = sum;
                    }
                }
                return arr;
            });
        }

        for (int t = 0; t < TM; t++) {
            for (int i = 0; i < matrixSize / TM; i++) {
                System.arraycopy(future[t].get()[i], 0, matrixC[matrixSize / TM * t + i], 0, matrixSize);
            }
        }

        return matrixC;
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
