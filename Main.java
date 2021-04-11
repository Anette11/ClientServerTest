package clientservertest;

public class Main {
    private static void start() {
        new Thread(new Client()).start();
        new Thread(new Server()).start();
    }

    public static void main(String[] args) {
        start();
    }
}