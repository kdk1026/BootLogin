package config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import config.mvc.interceptor.api.ApiCookieInterceptor;
import config.mvc.interceptor.api.ApiJwtInterceptor;
import config.mvc.interceptor.api.ApiSessionInterceptor;
import config.mvc.interceptor.web.CookieInterceptor;
import config.mvc.interceptor.web.JwtInterceptor;
import config.mvc.interceptor.web.SessionInterceptor;
import config.mvc.resolver.ParamMapArgResolver;

/**
 * @since 2018. 12. 24.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 24. 김대광	최초작성
 * </pre>
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {
	
	@Autowired
	private Environment env;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(new ParamMapArgResolver());
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		String sProfile = env.getActiveProfiles()[0];
		
		//--------------------------------------------------
		// Web
		//--------------------------------------------------
		registry.addInterceptor(new SessionInterceptor())
					.addPathPatterns("/session/**")
					.excludePathPatterns("/session/login**");
		
		registry.addInterceptor(new CookieInterceptor())
					.addPathPatterns("/cookie/**")
					.excludePathPatterns("/cookie/login**");
		
		registry.addInterceptor(new JwtInterceptor(sProfile))
					.addPathPatterns("/jwt/**")
					.excludePathPatterns("/jwt/login**");

		//--------------------------------------------------
		// Api
		//--------------------------------------------------
		registry.addInterceptor(new ApiSessionInterceptor())
					.addPathPatterns("/api/session/**", "/api/checkin")
					.excludePathPatterns("/api/session/login");
		
		registry.addInterceptor(new ApiCookieInterceptor())
					.addPathPatterns("/api/cookie/**", "/api/checkin")
					.excludePathPatterns("/api/cookie/login");
		
		registry.addInterceptor(new ApiJwtInterceptor(sProfile))
					.addPathPatterns("/api/jwt/**", "/api/checkin")
					.excludePathPatterns("/api/jwt/login", "/api/jwt/refresh");
	}
	
}
