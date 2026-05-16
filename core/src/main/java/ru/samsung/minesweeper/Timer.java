package ru.samsung.minesweeper;

public class Timer {
    private long startTime;
    private long elapsedTime;
    private boolean running;

    public Timer() {
        startTime = 0;
        elapsedTime = 0;
        running = false;
    }

    public void start() {
        startTime = System.currentTimeMillis();
        running = true;
    }

    public void stop() {
        if (running) {
            elapsedTime += System.currentTimeMillis() - startTime;
            running = false;
        }
    }

    public void reset() {
        elapsedTime = 0;
        if (running) {
            startTime = System.currentTimeMillis();
        }
    }

    public void update() {
        if (running) {
            elapsedTime = System.currentTimeMillis() - startTime;
        }
    }

    public String getTimeString() {
        long totalSeconds = elapsedTime / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        long centiseconds = (elapsedTime % 1000) / 10;
        return String.format("%02d:%02d:%02d", minutes, seconds, centiseconds);
    }
}
