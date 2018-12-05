package ws;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class IpChecker {
    private int RPP = 50;                                // Requests per period
    private int PERIOD = 60;                             // Period in seconds
    private int MAX_CACHE_SIZE = 10000;                  // Max number of IP Addresses to store
    private int SECONDS_EXPIRE_AFTER_ACCESS = PERIOD;    // The bucket is deleted after expiration time
    private int REQUEST_COST = 60;                       // Cost of 1 request
    private int TOKENS_IN_BUCKET = RPP * REQUEST_COST;   // Amount of tokens in bucket for 1 IP address

    private final LoadingCache<String, Bucket> bucketCache = CacheBuilder.newBuilder()
            .maximumSize(MAX_CACHE_SIZE)
            .expireAfterAccess(SECONDS_EXPIRE_AFTER_ACCESS, TimeUnit.SECONDS)
            .build(
                    new CacheLoader<String, Bucket>() {
                        @Override
                        public Bucket load(String ip) throws Exception {
                            return new Bucket(TOKENS_IN_BUCKET, PERIOD);
                        }
                    }
            );

    public boolean isLimited (String ipAddress) {
        Bucket bucket;
        try {
            bucket = bucketCache.get(ipAddress);
            if (!bucket.tryConsume(REQUEST_COST)) return true;
            return false;
        } catch (ExecutionException e) {
            return false;
        }
    }

}
