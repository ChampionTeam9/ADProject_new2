package com.ad.teamnine.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ad.teamnine.interceptor.SecurityInterceptor;

@Component
public class WebAppConfig implements WebMvcConfigurer{
	@Autowired
	SecurityInterceptor securityInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(securityInterceptor).addPathPatterns("/user/shoppingList/*", "/user/member/*", "/user/myProfile",
				"/recipe/create", "recipe/edit/*", "recipe/delete/*", "/report/*", "/review/*");
	}
}