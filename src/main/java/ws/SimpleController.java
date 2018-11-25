package ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class SimpleController {

    private SimpleService simpleService;

    @Autowired
    public SimpleController(SimpleService simpleService) {
        this.simpleService = simpleService;
    }

    @RateLimited
    @RequestMapping("/test")
    public String test() {
        return "";
    }

    @RequestMapping("/unlimited")
    public String unlimited() {
        return "";
    }

    @RequestMapping("/service")
    public String service() {
        return simpleService.useServiceMethod();
    }

    @RequestMapping("/service_unlimited")
    public String serviceUnlimited() {
        return simpleService.useUnlimitedServiceMethod();
    }


}