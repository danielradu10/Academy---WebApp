import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun api(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("Academia API")
            .pathsToMatch("/academia/**")
            .pathsToExclude("/actuator/**", "/swagger-ui/**", "/v3/api-docs", "/swagger-resources/**", "/error")
            .build()
    }
}
