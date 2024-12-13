package org.zerock.restqrpayment_2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // CSS 파일 매핑
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");

        // JavaScript 파일 매핑
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");

        // 이미지 파일 매핑 (필요한 경우)
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");

        // 기타 정적 리소스 매핑
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}
