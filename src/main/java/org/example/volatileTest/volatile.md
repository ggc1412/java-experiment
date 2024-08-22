스레드에서는 Task를 수행하는 동안 성능 향상을 위해서 Main 메모리에서 읽은 값을 CPU 캐시에 저장하여 사용한다. 멀티 스레드 환경에서 하나의 스레드는 read만 하는데 다른 스레드에서 write를 하는 경우, 값 불일치 문제가 발생할 수 있다.

```java
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
```

하지만 실제로 테스트 했을 때 volatile 키워드가 없어도 무한루프에 빠지지 않는다.
이는 최근의 JVM에서 최적화로 인해 volatile 키워드가 없이도 올바르게 동작할 수 있도록 하였기 때문이다.

알아본 최적화 사항은 다음과 같다.

1. JMM(Java Memory Model)의 발전
   - Java 5 이후로 JMM이 크게 개선되어 멀티스레딩 환경에서의 메모리 가시성과 순서 보장을 강화하였다.
2. JIT(Just-In-Time) 컴파일러의 최적화
   - 현대 JVM의 JIT 컴파일러는 코드 실행 패턴을 분석하고 최적화한다. 특정 상황에서는 메모리 배리어를 자동으로 삽입한다.
3. 하드웨어 레벨의 메모리 모델 개선
   - 최신 CPU 아키텍처는 메모리 일관성 모델을 제공한다. 이는 소프트웨어 레벨의 동기화 없이도 일부 동시성 문제를 해결할 수 있게 한다.
4. Happens-Before 관계의 확장:
   - JVM 구현이 발전하면서, 일부 동작에 대해 추가적인 Happens-Before 관계가 정의되었다. 이로 인해 명시적인 volatile 선언 없이도 메모리 가시성이 보장되는 경우가 늘어났다.

최적화가 되었다고는 하지만 volatile을 사용하는 것이 JMM에 의해 명시적으로 보장된 동작을 얻을 수 있으므로 가장 안전하고 명확한 방법이다.

- [Java Volatile Keyword](https://jenkov.com/tutorials/java-concurrency/volatile.html)
- [Guide to the Volatile Keyword in Java | Baeldung](https://www.baeldung.com/java-volatile)

> **JIT(Just-In-Time)**
초기 JVM은 인터프리터 방식망 이용하여 한줄 한줄 읽기 때문에 실행속도가 느린 단점이 있었지만, JIT 컴파일러 방식을 도입해 속도를 보완하였다.
JIT는 실행 시점에서 인터프리트 방식으로 기계어 코드를 생성하면서 그 코드를 캐싱하여, 같은 함수가 여러 번 불릴 때 매번 기계어 코드를 생성하는 것을 방지한다.
JIT 컴파일러는 바이트코드를 읽어 빠른 속도로 기계어를 생성할 수 있다. 이런 기계어 변환은 코드가 실행되는 과정에 실시간으로 일어나며(그래서 Just-In-Time이다), 전체 코드의 필요한 부분만 변환한다. 기계어로 변환된 코드는 캐시에 저장되기 때문에 재사용시 컴파일을 다시 할 필요가 없다.
>