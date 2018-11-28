package response.soft.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(getApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("response.soft.controller"))
                //.paths(PathSelectors.regex("/api/user.*")) //PathSelectors.regex("/anyPath.*"))
                .paths(PathSelectors.any())
                //.paths(PathSelectors.regex("/api/purchase/supplierPayment.*"))
                .build();
    }

    private ApiInfo getApiInfo() {
        Contact contact = new Contact("Response Electronic Ltd", "http://www.response.electronic.com", "salahine.rocky.bgta@gmail.com");
        return new ApiInfoBuilder()
                .title("Response Inventory Api List")
                .description("Start up module api documentation")
                .version("1.0.0")
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
                .contact(contact)
                .build();
    }

}
