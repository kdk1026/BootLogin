package config;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;

import config.app.PrimaryDatabaseConfig;
import config.app.TransactionalConfig;

/**
 * @since 2018. 12. 25.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 25. 김대광	최초작성
 * </pre>
 */
@Configuration
@Import({
	PrimaryDatabaseConfig.class, TransactionalConfig.class
})
public class AppConfig {

	@Bean
	public PropertiesFactoryBean common() {
		PropertiesFactoryBean properties = new PropertiesFactoryBean();
		properties.setLocation(new ClassPathResource("properties/common.properties"));
		return properties;
	}
	
}
