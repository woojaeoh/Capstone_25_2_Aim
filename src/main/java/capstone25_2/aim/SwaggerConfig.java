package capstone25_2.aim;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Capstone 25-2 Analyst Report API")
                        .description("""
                                AI 기반 리포트 분석 시스템의 백엔드 API 문서입니다.  
                                <br>
                                프론트엔드 및 AI 팀은 아래 엔드포인트를 테스트할 수 있습니다.
                                """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Backend Team - Woojae")
                                .email("backend@capstone25-2.com")));
    }
}
