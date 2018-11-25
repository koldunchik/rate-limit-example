package ws;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class Bucket {
    private final long capacity;
    private final int refillPerSecond;

    private AtomicLong availableTokens;
    private long lastRefill;

    public Bucket(long capacity, int refillPerSecond) {
        this.capacity = capacity;
        this.refillPerSecond = refillPerSecond;
        availableTokens = new AtomicLong(0L);
        availableTokens.compareAndSet(0L, capacity);
    }

    public boolean tryConsume(int numTokens) {
        refill();
        if (numTokens < 0 || numTokens >= capacity)
            throw new RuntimeException("Invalid number of tokens");

        while (true) {
            long balance = availableTokens.get();
            if (balance < numTokens) {
                availableTokens.compareAndSet(balance, 0);
                return false;
            }

            if (availableTokens.compareAndSet(balance, balance - numTokens)) {
                return true;
            }
        }

    }

    private void refill() {
        long currentTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        if (currentTimeSeconds <= lastRefill) return;

        long secondsSinceLastRefill = currentTimeSeconds - lastRefill;
        long refill = secondsSinceLastRefill * refillPerSecond;

        long existingSize = availableTokens.get();
        long newSize = Math.min(capacity, existingSize + refill);
        if (availableTokens.compareAndSet(existingSize, newSize)) {
            lastRefill = currentTimeSeconds;
        }
    }

}
