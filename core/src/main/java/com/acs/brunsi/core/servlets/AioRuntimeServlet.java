package com.acs.brunsi.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.acs.brunsi.core.http.AioRuntimeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(
    service = Servlet.class,
    property = {
        "sling.servlet.paths=/bin/sandbox/aio-runtime",
        "sling.servlet.methods=GET"
    }
)
public class AioRuntimeServlet extends SlingSafeMethodsServlet {

    @Reference
    private transient AioRuntimeService aioRuntimeService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        try {
            String result = aioRuntimeService.invokeAction();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(result);
        } catch (IOException e) {
            log.error("Failed to invoke AIO action", e);
            response.sendError(SlingHttpServletResponse.SC_BAD_GATEWAY, "Failed to invoke AIO action");
        }
    }
}