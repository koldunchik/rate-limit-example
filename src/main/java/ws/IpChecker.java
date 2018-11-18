package ws;

import com.github.bucket4j.Bandwidth;
import com.github.bucket4j.Bucket;
import com.github.bucket4j.Bucket4j;
import com.github.bucket4j.Refill;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class IpChecker {
    private int RPP = 50;                                // Requests per period
    private int PERIOD = 60;                             // Period in seconds
    private int MAX_CACHE_SIZE = 10000;                  // Max number of IP Addresses to store
    private int SECONDS_EXPIRE_AFTER_ACCESS = PERIOD;    // The bucket is deleted after expiration time
    private int TOKENS_IN_BUCKET = RPP * PERIOD;         // Amount of tokens in bucket for 1 IP address
    private int REQUEST_COST = TOKENS_IN_BUCKET / RPP;   // Cost of 1 request
    private int REFILL = TOKENS_IN_BUCKET / PERIOD;      // Number of tokens added to bucket every second

    private final LoadingCache<String, Bucket> bucketCache = CacheBuilder.newBuilder()
            .maximumSize(MAX_CACHE_SIZE)
            .expireAfterAccess(SECONDS_EXPIRE_AFTER_ACCESS, TimeUnit.SECONDS)
            .build(
                    new CacheLoader<String, Bucket>() {
                        @Override
                        public Bucket load(String ip) throws Exception {
                            return createNewBucket();
                        }
                    }
            );

    private Bucket createNewBucket() {
        Refill refill = Refill.smooth(REFILL, Duration.ofSeconds(1));
        Bandwidth limit = Bandwidth.classic(TOKENS_IN_BUCKET, refill);
        return Bucket4j.builder().addLimit(limit).build();
    }

    public boolean isLimited (String ipAddress) {
        Bucket bucket = null;
        try {
            bucket = bucketCache.get(ipAddress);
            if (!bucket.tryConsume(REQUEST_COST)) return true;
            return false;
        } catch (ExecutionException e) {
            return false;
        }
    }

}
