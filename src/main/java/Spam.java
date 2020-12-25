import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class Spam {
    private static Logger log = Logger.getLogger(Spam.class.getName());
    private final String[] messages;
    private final int[] delays;
    private final Thread[] threads;

    public Spam(final String[] messages, final int[] delays) {
        this.messages = messages;
        this.delays = delays;
        threads = new Thread[messages.length];
    }

    private void joinThreads() {
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                log.info(e.toString());
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void delay(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            log.info(e.toString());
            Thread.currentThread().interrupt();
        }
    }

    private void createThreads() {
        for (int i = 0; i < messages.length; i++) {
            threads[i] = new Worker(messages[i], delays[i]);
        }
    }

    public void start() {
        for (Thread thread : threads) {
            thread.start();
        }
    }

    public void stop() {
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

    private static class Worker extends Thread {
        private final String message;
        private final int delay;

        Worker(String message, int delay) {
            this.message = message;
            this.delay = delay;
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                System.out.print(message + System.lineSeparator());
                delay(delay);
            }
        }
    }

    public static void main(final String[] args) {
        Spam spam = new Spam(
                new String[] {"@SPAM DETECTED@", "piu piu"},
                new int[] {250, 500});
        spam.createThreads();
        spam.start();

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            int data;
            while ((data = reader.read()) != -1) {
                if (data == '\n' || data == '\r') {
                    spam.stop();
                }
            }
        } catch (IOException e) {
            log.severe(e.toString());
        }
        spam.joinThreads();
    }
}
