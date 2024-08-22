package org.example.volatileTest;

public class TaskRunner {

    private static int number;
    private static boolean ready;

    private static class Reader extends Thread {

        @Override
        public void run() {
            while (!ready) {
                Thread.yield();
            }

            System.out.println(number);
        }
    }

    public static void main(String[] args) {
        new Reader().start();
        number = 42;
        ready = true;
    }
    /*
    무한 루프에 빠지거나 0이 찍힐 것으로 예상되지만, 실제로는 42가 찍히고 금방 종료된다.
    다음과 같은 최적화가 적용되었을 수 있다.
    - The processor may flush its write buffer in an order other than the program order.
    - The processor may apply an out-of-order execution technique.
    - The JIT compiler may optimize via reordering.
     */
}
