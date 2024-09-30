package com.is4tech.invoicemanagement.config;

import com.is4tech.invoicemanagement.filter.CustomAuditFilter;
import com.is4tech.invoicemanagement.interceptor.AuditInterceptor;
import com.is4tech.invoicemanagement.service.AuditService;
import com.is4tech.invoicemanagement.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {

    private final AuditService auditService;
    private final JwtUtil jwtUtil;

    @Bean
    public AuditInterceptor auditInterceptor() {
        return new AuditInterceptor(auditService, jwtUtil);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(auditInterceptor())
                .addPathPatterns("/invoice-management/v0.1/**")
                .excludePathPatterns("/invoice-management/v0.1/auth/signup", "/invoice-management/v0.1/auth/login");
    }

    @Bean
    public FilterRegistrationBean<CustomAuditFilter> contentCachingFilter() {
        FilterRegistrationBean<CustomAuditFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CustomAuditFilter());
        registrationBean.addUrlPatterns("/invoice-management/v0.1/*");
        return registrationBean;
    }
}
