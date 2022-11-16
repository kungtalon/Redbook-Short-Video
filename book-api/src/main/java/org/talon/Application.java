package org.talon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.talon.userverification.EmailSender;
import org.talon.userverification.TwilioSMSSender;
import tk.mybatis.spring.annotation.MapperScan;

import javax.validation.constraints.Email;

/**
 * Created by Zelong
 * On 2022/10/28
 **/

@SpringBootApplication
@MapperScan(basePackages = "org.talon.mapper")
@ComponentScan(basePackages = {"org.talon", "org.n3r.idworker"})
public class Application extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(this.getClass());
    }
}
