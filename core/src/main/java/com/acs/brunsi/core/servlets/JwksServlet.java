package com.acs.brunsi.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.acs.brunsi.core.http.JwksService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(
    service = Servlet.class,
    property = {
        "sling.servlet.paths=/bin/sandbox/jwks",
        "sling.servlet.methods=GET"
    }
)
public class JwksServlet extends SlingSafeMethodsServlet {

    @Reference
    private transient JwksService jwksService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        try {
            String jwks = jwksService.fetchJwks();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jwks);
        } catch (IOException e) {
            log.error("Failed to fetch JWKS", e);
            response.sendError(SlingHttpServletResponse.SC_BAD_GATEWAY, "Failed to fetch JWKS");
        }
    }
}