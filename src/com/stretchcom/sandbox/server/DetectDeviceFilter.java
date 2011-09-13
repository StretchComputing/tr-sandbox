package com.stretchcom.sandbox.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.handinteractive.mobile.UAgentInfo;

public class DetectDeviceFilter implements Filter {

    private static final String MOBILE_DIR = "/mobile";
    private static final String DESKTOP_DIR = "/desktop";
    private static final Logger log = Logger.getLogger(SandboxApplication.class.getName());

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
        ServletException {

        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String uri = httpRequest.getRequestURI();
            log.info("Before URI: " + uri);

            if (!uri.startsWith(MOBILE_DIR) && !uri.startsWith(DESKTOP_DIR)) {
                UAgentInfo agent_info = new UAgentInfo(httpRequest.getHeader("user-agent"), httpRequest.getHeader("accept"));
                if (agent_info.detectTierIphone() || agent_info.detectTierTablet()) {
                    uri = MOBILE_DIR + uri;
                } else {
                    uri = DESKTOP_DIR + uri;
                }
                log.info("After URI: " + uri);
                RequestDispatcher rd = httpRequest.getRequestDispatcher(uri);
                rd.forward(request, response);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig fc) throws ServletException {
    }

}
