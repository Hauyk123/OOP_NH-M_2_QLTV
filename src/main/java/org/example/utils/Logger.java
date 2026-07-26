package org.example.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/*
 * Xử lí đa luồng cho logger
 * Sử dụng BlockingQueue để lưu trữ các log message
 * Sử dụng một luồng riêng để ghi log vào file
 * Đảm bảo rằng các log message được ghi theo thứ tự
 */
/**
 * Thread-safe singleton logger implementation using BlockingQueue Uses a
 * dedicated thread for writing logs to avoid blocking main operations
 */
public class Logger {

    private static final Logger instance = new Logger();
    // Hàng đợi thread-safe giữa luồng chính và luồng ghi log
    private final BlockingQueue<LogMessage> messageQueue;
    private final String LOG_FILE;
    private volatile boolean isRunning;
    //	Chạy nền, lấy log từ hàng đợi và ghi vào file
    private Thread loggingThread;

    private static class LogMessage {

        final String message;
        final LogLevel level;
        final LocalDateTime timestamp;

        LogMessage(String message, LogLevel level) {
            this.message = message;
            this.level = level;
            this.timestamp = LocalDateTime.now();
        }
    }

    private enum LogLevel {
        INFO, ERROR, WARN
    }

    private Logger() {
        messageQueue = new LinkedBlockingQueue<>();
        LOG_FILE = "src/main/java/org/example/data/app.log";

        // Create log file if it doesn't exist
        try {
            File logFile = new File(LOG_FILE);
            if (!logFile.exists()) {
                logFile.getParentFile().mkdirs(); // Create parent directories if needed
                logFile.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Failed to create log file: " + e.getMessage());
        }

        isRunning = true;
        startLoggingThread();
    }

    public static Logger getInstance() {
        return instance;
    }

    private void startLoggingThread() {
        loggingThread = new Thread(() -> {
            while (isRunning || !messageQueue.isEmpty()) {
                try {
                    LogMessage logMessage = messageQueue.take();
                    writeToFile(logMessage);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        //	Đảm bảo logger không ngăn chương trình thoát
        loggingThread.setDaemon(true);
        loggingThread.start();
    }

    private void writeToFile(LogMessage msg) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println(String.format("%s - [%s] %s",
                    msg.timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    msg.level,
                    msg.message));
        } catch (IOException e) {
            System.err.println("Failed to write log: " + e.getMessage());
        }
    }

    public void log(String message) {
        if (isRunning) {
            try {
                LogMessage logMessage = new LogMessage(message, LogLevel.INFO);
                messageQueue.put(logMessage);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void error(String message) {
        if (isRunning) {
            try {
                LogMessage logMessage = new LogMessage(message, LogLevel.ERROR);
                messageQueue.put(logMessage);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void shutdown() {
        isRunning = false;
        if (loggingThread != null) {
            loggingThread.interrupt();
            try {
                loggingThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
