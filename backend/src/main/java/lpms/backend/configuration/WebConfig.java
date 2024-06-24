package lpms.backend.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static lpms.backend.utils.ProjectConstans.USER_DIR;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // "/image/**" URL 패턴을 "file:image/" 경로로 매핑
        registry.addResourceHandler("/image/**")
                .addResourceLocations("file:"+USER_DIR+"/");
    }
}