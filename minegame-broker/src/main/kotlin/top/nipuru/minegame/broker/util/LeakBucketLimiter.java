package top.nipuru.minegame.broker.util;

public class LeakBucketLimiter {

    private final double leaksPerSecond;

    private final double capacity;

    private double water = 0;

    private long lastOutTime = System.currentTimeMillis();

    public LeakBucketLimiter(double leaksPerSecond, int capacity) {
        this.leaksPerSecond = leaksPerSecond;
        this.capacity = capacity;
    }

    public synchronized boolean isLimit() {
        if (water == 0) {
            lastOutTime = System.currentTimeMillis();
            water+=1;
            return false;
        }
        // 执行漏水
        double waterLeaked = ((System.currentTimeMillis() - lastOutTime) * leaksPerSecond / 1000);
        double waterLeft = water - waterLeaked;
        water = Math.max(0, waterLeft);
        lastOutTime = System.currentTimeMillis();
        if (water < capacity) {
            water+=1;
            return false;
        } else {
            return true;
        }

    }
}
