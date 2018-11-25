package ws;

import org.springframework.stereotype.Service;

@Service("simpleService")
public class SimpleService
{
    @RateLimited
    public String useServiceMethod() {
        return "Service method was used";
    }

    public String useUnlimitedServiceMethod() {
        return "Unlimited service method was used";
    }


}