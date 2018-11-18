# Rate Limit Example #

50 requests per 60 seconds using Token Bucket Algorithm

For each IP address:
* 3000 tokens are available for 60 seconds window
* 1 request costs 60 tokens
* 50 new tokens are issued every second

## Notes

Bucket4j is used for Token Bucket Algorithm implementation

Google Guava is used for high performance thread-safe caching


## How to run ##
**mvn clean package**

**java -jar test-1.jar**


## How to check ##
* Make first requests with Apache Benchmark

**ab -n 50 http://localhost:8080/test**

* Open URL in browser

**http://localhost:8080/test**


## HTTP codes ##
200 - ok

502 - rate limit
