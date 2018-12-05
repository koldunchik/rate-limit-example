package ws;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

public class Utils {
    public static String getIpFromRequest(HttpServletRequest req) {
        String ipAddress = "";
        if (req != null) {
            ipAddress = req.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null || "".equals(ipAddress)) {
                ipAddress = req.getRemoteAddr();
            }
        }
        return ipAddress;
    }

    public static long currentTimeSeconds() {
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
    }
}
