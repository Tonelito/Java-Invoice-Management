package com.is4tech.invoicemanagement.interceptor;

import com.is4tech.invoicemanagement.annotation.AuditEntity;
import com.is4tech.invoicemanagement.dto.AuditDto;
import com.is4tech.invoicemanagement.service.AuditService;
import com.is4tech.invoicemanagement.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

public class AuditInterceptor implements HandlerInterceptor {
    private final AuditService auditService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuditInterceptor(AuditService auditService, JwtUtil jwtUtil) {
        this.auditService = auditService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            Method method = ((HandlerMethod) handler).getMethod();
            AuditEntity auditEntity = method.getAnnotation(AuditEntity.class);

            if (auditEntity != null) {
                String entityName = auditEntity.value();
                String httpMethod = request.getMethod();

                if (request instanceof ContentCachingRequestWrapper) {
                    String requestBody = getRequestBody((ContentCachingRequestWrapper) request);
                    System.out.println("Request Body in Interceptor: " + requestBody);

                    request.setAttribute("startTime", System.currentTimeMillis());

                    AuditDto auditDto = new AuditDto();
                    auditDto.setEntity(entityName);
                    auditDto.setDatetime(LocalDateTime.now());
                    auditDto.setRequest(requestBody);
                    auditDto.setOperation(httpMethod);
                    auditDto.setUserId(getUserIdFromRequest(request));
                    request.setAttribute("auditDto", auditDto);
                }
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (handler instanceof HandlerMethod) {
            AuditDto auditDto = (AuditDto) request.getAttribute("auditDto");

            if (auditDto != null) {
                int statusCode = response.getStatus();
                auditDto.setResponse(String.valueOf(statusCode));

                long responseTime = System.currentTimeMillis() - (Long) request.getAttribute("startTime");
                auditDto.setResponseTime((float) responseTime / 1000);

                auditService.logAudit(auditDto);
            }
        }
    }

    private String getRequestBody(ContentCachingRequestWrapper request) throws UnsupportedEncodingException {
        byte[] buf = request.getContentAsByteArray();
        return new String(buf, 0, buf.length, request.getCharacterEncoding());
    }

    private Integer getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return jwtUtil.extractUserId(token).intValue();
        }
        return null;
    }
}
