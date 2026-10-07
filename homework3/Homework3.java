import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Homework3 {

    public static final int WIDTH = 1600;
    public static final int HEIGHT = 1200;
    public static final int MAX_ITER = 1000;
    public static final int THREADS = 4;

    public static final double X_MIN = -2.2;
    public static final double X_MAX = 1.0;
    public static final double Y_MAX = 1.2;
    public static final double Y_MIN = -1.2;


    static boolean inSet(int px, int py) {
        double cr = X_MIN + (X_MAX - X_MIN) * px / WIDTH;
        double ci = Y_MIN + (Y_MAX - Y_MIN) * py / HEIGHT;
        double zr = 0;
        double zi = 0;
        for (int i = 0; i < MAX_ITER; i++) {
            double newZr = zr * zr - zi * zi + cr;
            zi = 2 * zr * zi + ci;
            zr = newZr;
            if (zr * zr + zi * zi > 4) return false;
        }
        return true;
    }

    static Thread taskThread(int n, boolean[][] result) {
        return new Thread(() -> {
            for (int y = n; y < HEIGHT; y += THREADS) {
                for (int x = 0; x < WIDTH; x++) {
                    result[y][x] = inSet(x, y);
                }
            }
        });
    }

    static void save(boolean[][] result, String name) throws IOException {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                image.setRGB(x, y, result[y][x] ? 0x000000 : 0xFFFFFF);
            }
        }
        ImageIO.write(image, "png", new File(name));
    }

    public static void main(String[] args) throws Exception {
        boolean[][] result = new boolean[HEIGHT][WIDTH];
        Thread[] threads = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) threads[i] = taskThread(i, result);
        for (var t : threads) t.start();
        for (var t : threads) t.join();

        save(result, "mandelbrot.png");
    }
}
