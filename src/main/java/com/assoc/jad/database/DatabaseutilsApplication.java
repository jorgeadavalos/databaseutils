package com.assoc.jad.database;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.assoc.jad.filetransfer.servlet.DocServlet;

@SpringBootApplication
public class DatabaseutilsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DatabaseutilsApplication.class, args);
	}

    @Bean
    ServletRegistrationBean<DocServlet> docServlet() {
		   DocServlet servlet = new DocServlet();
			ServletRegistrationBean<DocServlet> registration = new ServletRegistrationBean<DocServlet>(servlet, "/docservlet/*");
			registration.setName("docservlet");
			registration.setLoadOnStartup(1);
			return registration;
		}
}
