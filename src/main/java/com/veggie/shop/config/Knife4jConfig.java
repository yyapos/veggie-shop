package com.veggie.shop.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j / Swagger API 文档配置
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("蔬菜店管理系统 API")
                        .version("1.0.0")
                        .description("蔬菜店管理系统后端接口文档 — 支持商品管理、销售记账、进货管理、赊账管理、数据看板")
                        .contact(new Contact()
                                .name("Veggie Shop")
                                .email("admin@veggie.shop"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
