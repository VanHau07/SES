package vn.sesgroup.hddt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
//https://stackjava.com/spring-boot/spring-boot-tuy-chinh-trang-whitelabel-error-page.html
//https://stackjava.com/spring/spring-mvc-exception-handling-xu-ly-exception-trong-spring-mvc.html
//@EnableAutoConfiguration(exclude = {
//	ErrorMvcAutoConfiguration.class
//})


@SpringBootApplication(scanBasePackages = "vn.sesgroup.hddt")

public class EInvoiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EInvoiceApplication.class, args);
	}

}
