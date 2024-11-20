/**
 * The ThreadTest class extends the {@link Thread} class and demonstrates how to create and run multiple threads in Java.
 * Each thread in this class continuously prints the thread's name along with an incrementing counter every 100 milliseconds.
 */
public class ThreadTest extends Thread {
    public int getCounter() {
        return counter;
    }

    private int counter = 0;

    /**
     * The run method defines the code that will be executed by each thread.
     * It continuously prints the thread's name and the current counter value, then increments the counter.
     * The thread sleeps for 100 milliseconds after each print to simulate periodic work.
     */
    @Override
    public void run() {
        try {
            while (true) {
                System.out.println(getName() + ": " + counter++);
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            System.err.println(getName() + " interrupted: " + e.getMessage());
        }
    }

    /**
     * The main method demonstrates how to create and start multiple threads using the ThreadTest class.
     * It creates three instances of ThreadTest and starts each one.
     * Each thread runs its own instance of the {@link #run()} method.
     */
    public static void main(String[] args) {
        ThreadTest thread1 = new ThreadTest();
        ThreadTest thread2 = new ThreadTest();
        ThreadTest thread3 = new ThreadTest();

        thread1.start();
        thread2.start();
        thread3.start();
    }
}
