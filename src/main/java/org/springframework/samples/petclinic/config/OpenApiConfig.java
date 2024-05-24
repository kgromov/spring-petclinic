package org.springframework.samples.petclinic.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.SpecVersion;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.swagger.v3.oas.models.SpecVersion.V31;

@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI petClinicOpenApi() {
		return new OpenAPI()
			.info(
				new Info()
					.title("Pet Clinic API")
					.description("OpenAPI documentation for Pet clinic project")
					.version("v0.0.1")
					.license(new License().name("Apache 2.0"))
			)
			.externalDocs(
				new ExternalDocumentation()
					.description("Petclinic community")
					.url("https://spring-petclinic.github.io/")
			)
			.specVersion(V31);
	}
}
