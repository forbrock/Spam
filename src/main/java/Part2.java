import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public class Part2 {
    private static Logger log = Logger.getLogger(Part2.class.getName());
    private static final InputStream STDIN = System.in;
    private static final byte[] BYTES = System.lineSeparator().getBytes();

    public static class CustomInputStream extends InputStream {
        private InputStream inputStream;
        private boolean needDelay = true;

        public CustomInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public int read() throws IOException {
            if (needDelay) {
                Spam.delay(2000);
            }
            int data = inputStream.read();
            needDelay = false;
            if (data == -1) {
                return -1;
            }
            return data;
        }
    }

    public static void main(final String[] args) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(BYTES);
        InputStream customInStream = new CustomInputStream(inputStream);
        try {
            System.setIn(customInStream);

            Thread t = new Thread(() -> Spam.main(null));
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                log.info(e.toString());
                Thread.currentThread().interrupt();
            }
        } finally {
            System.setIn(STDIN);
        }
    }
}
