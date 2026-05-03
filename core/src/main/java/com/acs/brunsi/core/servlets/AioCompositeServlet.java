package com.acs.brunsi.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.acs.brunsi.core.http.AioCompositeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(
    service = Servlet.class,
    property = {
        "sling.servlet.paths=/bin/sandbox/aio-composite",
        "sling.servlet.methods=GET"
    }
)
public class AioCompositeServlet extends SlingSafeMethodsServlet {

    @Reference
    private transient AioCompositeService aioCompositeService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            String result = "b".equalsIgnoreCase(action)
                ? aioCompositeService.invokeActionB()
                : aioCompositeService.invokeActionA();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(result);
        } catch (IOException e) {
            log.error("AIO composite action '{}' failed", action, e);
            response.sendError(SlingHttpServletResponse.SC_BAD_GATEWAY, "AIO composite action failed");
        }
    }
}
