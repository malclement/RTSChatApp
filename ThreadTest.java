public class ThreadTest extends Thread {
    public int getCounter() {
        return counter;
    }

    private int counter = 0;

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

    public static void main(String[] args) {
        ThreadTest thread1 = new ThreadTest();
        ThreadTest thread2 = new ThreadTest();
        ThreadTest thread3 = new ThreadTest();

        thread1.start();
        thread2.start();
        thread3.start();
    }
}
