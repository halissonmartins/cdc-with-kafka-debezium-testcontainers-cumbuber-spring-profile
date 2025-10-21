package br.com.halisson;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceConfig {
	
	/**
	 * To prevent this erro:
	 * Consider marking one of the beans as @Primary, updating the consumer to accept multiple beans, or using @Qualifier to identify the bean that should be consumed
	 * 
	 * @return
	 */
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

}

