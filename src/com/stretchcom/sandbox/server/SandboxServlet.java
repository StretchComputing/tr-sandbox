package com.stretchcom.sandbox.server;

import java.io.IOException;
import javax.servlet.http.*;

import com.handinteractive.mobile.UAgentInfo;

@SuppressWarnings("serial")
public class SandboxServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        UAgentInfo agent_info = new UAgentInfo(req.getHeader("user-agent"), req.getHeader("accept"));
        if (agent_info.detectTierIphone() || agent_info.detectTierTablet()) {
            resp.getWriter().println("Mobile Device");
        } else {
            resp.getWriter().println("Desktop Device");
        }
//        resp.getWriter().println(isMobile(req) ? "mobile" : "not mobile");
        resp.getWriter().println();
        resp.getWriter().println("User Agent: " + req.getHeader("user-agent"));
        resp.getWriter().println();
        resp.getWriter().println("Present HTTP Headers");
        resp.getWriter().println("--------------------");
        for (Object o : java.util.Collections.list(req.getHeaderNames())) {
            resp.getWriter().println(o);
        }
    }

    private static final String[] UA_PREFIXES = { "w3c ", "w3c-", "acs-", "alav", "alca", "amoi", "audi", "avan",
        "benq", "bird", "blac", "blaz", "brew", "cell", "cldc", "cmd-", "dang", "doco", "eric", "hipt", "htc_", "inno",
        "ipaq", "ipod", "jigs", "kddi", "keji", "leno", "lg-c", "lg-d", "lg-g", "lge-", "lg/u", "maui", "maxo", "midp",
        "mits", "mmef", "mobi", "mot-", "moto", "mwbp", "nec-", "newt", "noki", "palm", "pana", "pant", "phil", "play",
        "port", "prox", "qwap", "sage", "sams", "sany", "sch-", "sec-", "send", "seri", "sgh-", "shar", "sie-", "siem",
        "smal", "smar", "sony", "sph-", "symb", "t-mo", "teli", "tim-", "tosh", "tsm-", "upg1", "upsi", "vk-v", "voda",
        "wap-", "wapa", "wapi", "wapp", "wapr", "webc", "winw", "winw", "xda ", "xda-" };

    private static final String[] UA_CONTAINS = { "android", "blackberry", "hiptop", "ipod", "lge vx", "midp", "maemo",
        "mmp", "netfront", "nintendo DS", "novarra", "openweb", "opera mobi", "opera mini", "palm", "psp", "phone",
        "smartphone", "symbian", "up.browser", "up.link", "wap", "windows ce" };

    private boolean isMobile(HttpServletRequest req) {
        String user_agent = req.getHeader("user-agent").toLowerCase();
        if (req.getHeader("x_wap_profile") != null || req.getHeader("profile") != null) {
            return true;
        }

        // The following code retrieves the UAheader and attempts to match one
        // of the common UA string prefixes (primarily identifying operators and
        // handset manufacturers).
        String user_agent_prefix = user_agent.substring(0, 4);
        for (String s : UA_PREFIXES) {
            if (s.equals(user_agent_prefix)) {
                return true;
            }
        }

        // The following code attempts to match the term wap within the
        // http_accept header, which identifies acceptable content types.
        String accept = req.getHeader("accept").toLowerCase();
        if (accept.contains("wap")) {
            return true;
        }

        // The following code, attempts to match common strings identifying
        // mobile platforms and operating systems.
        for (String s : UA_CONTAINS) {
            if (user_agent.contains(s)) {
                return true;
            }
        }

        return false;
    }

    //
    // // Finally, this cod retrieves all headers and attempts to match a common
    // string identifying Opera Mini.
    //
    // if (isset($_SERVER["ALL_HTTP"]) &&
    // strpos(strtolower($_SERVER["ALL_HTTP"]), "operamini") !== false) {
    // return true;
    // }
    // return false;
    // }
}
