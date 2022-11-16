package org.talon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.talon.interceptor.PassportInterceptor;
import org.talon.interceptor.UserTokenInterceptor;

/**
 * Created by Zelong
 * On 2022/10/30
 **/

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public PassportInterceptor passportInterceptor() {
        return new PassportInterceptor();
    }

    @Bean
    public UserTokenInterceptor userTokenInterceptor() {
        return new UserTokenInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passportInterceptor())
                .addPathPatterns("/passport/getVeriCode");

        registry.addInterceptor(userTokenInterceptor())
                .addPathPatterns("/userInfo/modifyImage")
                .addPathPatterns("/userInfo/modifyUserInfo");
    }

}
