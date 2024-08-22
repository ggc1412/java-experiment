package org.example.volatileTest;

import static java.lang.Thread.sleep;

public class Volatile {
    private static volatile boolean flag = false;

    private static int sharedValue = 0;

    public static void main(String[] args) throws InterruptedException {
        Thread writerThread = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                sharedValue++;
                flag = true;
                System.out.println("Writer: Set flag to true. sharedValue = " + sharedValue);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread readerThread = new Thread(() -> {
            int localValue = 0;
            while (!flag) {
                // flag에 volatile 키워드가 없다면
                // writerThread에서 변경한 것을 알지 못하고 무한 루프에 빠지게 된다.
            }
            localValue = sharedValue;
            System.out.println("Reader: Detected flag change. sharedValue = " + localValue);
        });

        writerThread.start();
        readerThread.start();

        writerThread.join();
        readerThread.join();
    }
}