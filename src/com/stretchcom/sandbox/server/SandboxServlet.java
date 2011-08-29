package com.stretchcom.sandbox.server;

import java.io.IOException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class SandboxServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world");
	}
}
