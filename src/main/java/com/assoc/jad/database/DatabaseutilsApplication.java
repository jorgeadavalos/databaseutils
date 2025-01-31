package com.assoc.jad.database;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

import com.assoc.jad.filetransfer.servlet.DocServlet;

@SpringBootApplication
public class DatabaseutilsApplication {
	@Value("${my.DYNSERVERPORT}") 		private int DYNSERVERPORT;

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
	@Bean
    ApplicationListener<ServletWebServerInitializedEvent> webServer() {
        return (event) -> {
            int port = event.getWebServer().getPort();
            DYNSERVERPORT = port;
            System.out.println("Web server started on port: " + port);
        };
    }
}
