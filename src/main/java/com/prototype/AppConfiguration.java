package com.prototype;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import com.prototype.entity.Step1Entity;
import com.prototype.entity.Step2Entity;
import com.prototype.entity.Step3Entity;


@Configuration
public class AppConfiguration {

//	@Bean
//    public ErrorDecoder errorDecoder() {  // tells Feign client how to handle HTTP 500 Error
//        return new CustomErrorDecoder();  // do not throw exception for HTTP 500 errors
//    }
	
	@Bean
	public RestTemplate restTemplate() {
	    return new RestTemplate();
	}
	
	//somehow can't find a default entitymanager factory, so I had to define one
	@Bean(name="entityManagerFactory")
	public SessionFactory sessionFactory() {   //LocalSessionFactoryBean
		
		org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration()
				.setProperty(AvailableSettings.JAKARTA_JDBC_URL, "jdbc:h2:mem:testdb")
				.setProperty(AvailableSettings.JAKARTA_JDBC_DRIVER, "org.h2.Driver")
				.setProperty(AvailableSettings.HBM2DDL_AUTO, "update")
				.addAnnotatedClass(Step1Entity.class)
				.addAnnotatedClass(Step2Entity.class)
				.addAnnotatedClass(Step3Entity.class);
		
		SessionFactory sessionFactory = configuration.buildSessionFactory();
	    
	    return sessionFactory;
	} 
	
	//somehow can't find a default transactionManager, so I had to define one
	@Bean(name = "transactionManager")
	public PlatformTransactionManager dbTransactionManager() {
	    JpaTransactionManager transactionManager = new JpaTransactionManager();
	    transactionManager.setEntityManagerFactory(sessionFactory());  // sessionFactory().getObject()
	    return transactionManager;
	}
}
