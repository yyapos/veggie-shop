package com.veggie.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 蔬菜店管理系统 - 启动类
 */
@SpringBootApplication
public class VeggieShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(VeggieShopApplication.class, args);
        System.out.println("""

                ============================================
                🥬  蔬菜店管理系统启动成功！
                📖  API文档: http://localhost:8080/doc.html
                ============================================
                """);
    }
}
