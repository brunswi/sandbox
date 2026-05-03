package com.acs.brunsi.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.acs.brunsi.core.http.HttpBinDelayService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(
    service = Servlet.class,
    property = {
        "sling.servlet.paths=/bin/sandbox/httpbin-delay",
        "sling.servlet.methods=GET"
    }
)
public class HttpBinDelayServlet extends SlingSafeMethodsServlet {

    @Reference
    private transient HttpBinDelayService httpBinDelayService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        try {
            String result = httpBinDelayService.fetch();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(result);
        } catch (IOException e) {
            log.error("httpbin delay call failed", e);
            response.sendError(SlingHttpServletResponse.SC_BAD_GATEWAY, "httpbin delay call failed");
        }
    }
}