import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Runner {

    private static Random random = new Random();

    private static final int NUMBER_OF_TEXTS = 10_000;
    private static final int LENGTH_OF_TEXTS = 100_000;
    private static BlockingQueue<String> queueForA = new ArrayBlockingQueue<>(100);
    private static BlockingQueue<String> queueForB = new ArrayBlockingQueue<>(100);
    private static BlockingQueue<String> queueForC = new ArrayBlockingQueue<>(100);
    private static int sumOfA = 0;
    private static int sumOfB = 0;
    private static int sumOfC = 0;
    private static List<Thread> listOfThreads = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            for (int i = 0; i < NUMBER_OF_TEXTS; i++) {
                try {
                    String text = generateText("abc", LENGTH_OF_TEXTS);
                    queueForA.put(text);
                    queueForB.put(text);
                    queueForC.put(text);
                } catch (InterruptedException e) {
                    return;
                }
            }

        });
        listOfThreads.add(thread);
        Thread countingAThread = new Thread(() -> {
            for (int i = 0; i < NUMBER_OF_TEXTS; i++) {
                countABC('a');
            }
        });
        listOfThreads.add(countingAThread);
        Thread countingBThread = new Thread(() -> {
            for (int i = 0; i < NUMBER_OF_TEXTS; i++) {
                countABC('b');
            }
        });
        listOfThreads.add(countingBThread);
        Thread countingCThread = new Thread(() -> {
            for (int i = 0; i < NUMBER_OF_TEXTS; i++) {
                countABC('c');
            }
        });
        listOfThreads.add(countingCThread);
        startThread(listOfThreads);
        print("A", sumOfA);
        print("B", sumOfB);
        print("C", sumOfC);
    }

    protected static void startThread(List<Thread> threads) throws InterruptedException {
        for (Thread t : threads) {
            t.start();
        }
        for (int i = threads.size() - 1; i >= 0; i--) {
            threads.get(i).join();
        }

    }

    protected static void countABC(char letter) {
        int result = 0;
        try {
            switch (letter) {
                case 'a' -> {
                    String text = queueForA.take();
                    sumOfA += sumOfLetter(text, 'a');

                }
                case 'b' -> {
                    String text = queueForB.take();
                    sumOfB += sumOfLetter(text, 'b');
                }
                case 'c' -> {
                    String text = queueForC.take();
                    sumOfC += sumOfLetter(text, 'c');
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected static void print(String letter, int result) {
        String template = "Всего букв %s: %d\n";
        System.out.printf(template, letter, result);
    }

    public static String generateText(String letters, int length) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    private static int sumOfLetter(String string, char letter) {
        return (int) string.chars()
                .filter(c -> c == letter)
                .count();
    }
}
