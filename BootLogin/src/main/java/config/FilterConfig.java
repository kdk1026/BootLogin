package config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import common.servlet.filter.SecurityFilter;

/**
 * @since 2019. 1. 1.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2019. 1. 1. 김대광	최초작성
 * </pre>
 */
@Configuration
public class FilterConfig {

	@Bean
	public FilterRegistrationBean getSecurityFilterRegistrationBean() {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		
		registrationBean.setFilter( new SecurityFilter() );
		registrationBean.addUrlPatterns("/*");
		
		return registrationBean;
	}
	
	
}
