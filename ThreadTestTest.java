import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThreadTestTest {

    @Test
    void testThreadsStartAndRun() throws InterruptedException {
        ThreadTest thread1 = new ThreadTest();
        ThreadTest thread2 = new ThreadTest();

        thread1.start();
        thread2.start();

        Thread.sleep(500);

        assertTrue(thread1.isAlive(), "Thread 1 should be running.");
        assertTrue(thread2.isAlive(), "Thread 2 should be running.");

        thread1.interrupt();
        thread2.interrupt();

        thread1.join();
        thread2.join();

        assertFalse(thread1.isAlive(), "Thread 1 should have stopped.");
        assertFalse(thread2.isAlive(), "Thread 2 should have stopped.");
    }

    @Test
    void testCounterIncrements() throws InterruptedException {
        ThreadTest thread = new ThreadTest();

        thread.start();
        Thread.sleep(500);

        thread.interrupt();
        thread.join();

        int counterValue = thread.getCounter();
        assertTrue(counterValue > 0, "Counter should have incremented.");
    }

    @Test
    void testThreadInterruption() throws InterruptedException {
        ThreadTest thread = new ThreadTest();

        thread.start();
        Thread.sleep(200);

        thread.interrupt();
        thread.join();

        assertFalse(thread.isAlive(), "Thread should stop after interruption.");
    }
}
