package org.example.volatileTest;

public class StaticVolatile {
    private static volatile int sharedCounter = 0;

    public static synchronized void incrementCounter() {
        sharedCounter++;  // 항상 메인 메모리에 즉시 반영
    }

    public static int getCounter() {
        return sharedCounter;  // 항상 메인 메모리에서 직접 읽음
    }

    public static void main(String[] args) {
        // 여러 스레드에서 동시에 접근
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1_000_000; i++) {
                incrementCounter();
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1_000_000; i++) {
                incrementCounter();
            }
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Final Counter Value: " + getCounter());
        /*
        원자성 보장 x
        105000 ~ 110000 | 80 ~ 110ms

        synchronized 키워드 추가
        2000000         | 160 ~ 200ms
         */
    }
}