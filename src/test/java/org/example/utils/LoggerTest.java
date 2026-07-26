package org.example.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoggerTest {

    private static final String LOG_FILE = "src/main/java/org/example/data/app.log";
    private Logger logger;
    private File logFile;

    @BeforeEach
    void setUp() {
        logger = Logger.getInstance();
        logFile = new File(LOG_FILE);
        logFile.delete();
    }

    @Test
    void testSingletonPattern() {
        Logger logger1 = Logger.getInstance();
        Logger logger2 = Logger.getInstance();
        assertSame(logger1, logger2, "Logger instances should be the same");
    }

    @Test
    void testLogMessageFormat() throws Exception {
        String testMessage = "Test log message";
        logger.log(testMessage);

        // Give logger time to write
        Thread.sleep(100);

        List<String> lines = Files.readAllLines(logFile.toPath());
        String logLine = lines.get(0);
        assertTrue(logLine.contains("[INFO]"), "Log line should contain INFO level");
        assertTrue(logLine.contains(testMessage), "Log line should contain the log message");
    }

    @Test
    void testErrorMessageFormat() throws Exception {
        String errorMessage = "Test error message";
        logger.error(errorMessage);

        Thread.sleep(100);

        List<String> lines = Files.readAllLines(Path.of(LOG_FILE));
        String logLine = lines.get(0);
        assertTrue(logLine.contains("[ERROR]"), "Log line should contain ERROR level");
        assertTrue(logLine.contains(errorMessage), "Log line should contain the error message");
    }

    @Test
    void testConcurrentLogging() throws Exception {
        int threadCount = 10;
        int messagesPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < messagesPerThread; j++) {
                        logger.log("Thread-" + threadId + " Message-" + j);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        Thread.sleep(500); // Wait for all messages to be written

        List<String> lines = Files.readAllLines(Path.of(LOG_FILE));
        assertEquals(threadCount * messagesPerThread, lines.size(),
                "Log file should contain all messages");
        assertTrue(lines.stream().anyMatch(line -> line.contains("Thread-0 Message-0")),
                "Log should contain messages from all threads");

    }

    @Test
    void testShutdownBehavior() throws Exception {
        logger.log("Message before shutdown");
        logger.shutdown();
        Thread.sleep(100);

        long initialLineCount = Files.readAllLines(Path.of(LOG_FILE)).size();

        logger.log("Message after shutdown");
        Thread.sleep(100);

        long finalLineCount = Files.readAllLines(Path.of(LOG_FILE)).size();
        assertEquals(initialLineCount, finalLineCount,
                "No new messages should be logged after shutdown");
        assertTrue(logFile.exists(), "Log file should still exist after shutdown");
    }

    @Test
    void testLogging() throws Exception {
        String testMessage = "Test log message";
        logger.log(testMessage);

        // Give some time for async logging
        Thread.sleep(100);

        List<String> logLines = Files.readAllLines(new File(LOG_FILE).toPath());
        assertEquals(1, logLines.size(), "Log file should contain one log message");
        String logLine = logLines.get(0);
        assertTrue(logLine.contains(testMessage), "Log line should contain the log message");
    }

    @AfterEach
    void tearDown() {
        logger.shutdown();
        logFile.delete();
    }
}
