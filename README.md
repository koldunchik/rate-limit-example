# Rate Limit Example #

50 requests per 60 seconds

For each IP address:
* 3000 tokens are available initially
* 1 request costs 60 tokens
* 50 new tokens are issued every second

## Notes

Google Guava is used for high performance thread-safe caching
with access expiration

## How to run ##
`mvn clean package`

`java -jar test-1.jar`


## How to check ##
Make first requests with Apache Benchmark

`ab -n 50 http://localhost:8080/test`

Open URL in browser

`http://localhost:8080/test`


## HTTP codes ##
200 - ok

502 - rate limit
