package ws;

import java.util.concurrent.atomic.AtomicLong;

public class Bucket {
    private final long capacity;
    private final int refillPeriod;

    private AtomicLong availableTokens;
    private long lastRefill;

    public Bucket(long capacity, int refillPeriod) {
        this.capacity = capacity;
        this.refillPeriod = refillPeriod;
        availableTokens = new AtomicLong(capacity);
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
        long currentTimeSeconds = Utils.currentTimeSeconds();
        long secondsSinceLastRefill = currentTimeSeconds - lastRefill;
        if (secondsSinceLastRefill < refillPeriod) return;

        long existingSize = availableTokens.get();
        if (availableTokens.compareAndSet(existingSize, capacity)) {
            lastRefill = Utils.currentTimeSeconds();
        }
    }

}
