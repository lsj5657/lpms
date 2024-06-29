package lpms.backend.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static lpms.backend.utils.ProjectConstans.ROOT_DIR;

/**
 * Web configuration class to customize the Spring MVC configuration.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Add resource handlers to serve static resources.
     *
     * @param registry ResourceHandlerRegistry to add the resource handler to
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map the "/image/**" URL pattern to the directory specified by USER_DIR
        registry.addResourceHandler("/image/**")
                .addResourceLocations("file:"+ ROOT_DIR +"/");
    }
}