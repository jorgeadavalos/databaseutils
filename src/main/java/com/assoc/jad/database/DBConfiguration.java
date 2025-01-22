package com.assoc.jad.database;

//import javax.sql.DataSource;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class DBConfiguration {
//	@Autowired testing desktop github
//	private DataSource dataSource;
//
//    @Bean
//    TransactionManager transactionManager(DataSource datasource) {
//		return new DataSourceTransactionManager(dataSource);
//	}
}
