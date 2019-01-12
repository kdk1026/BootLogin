package config.app;

import javax.sql.DataSource;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.flywaydb.core.Flyway;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

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
@EnableTransactionManagement
@MapperScan(value = "kr.co.test.mapper.account", annotationClass = Mapper.class)
public class PrimaryDatabaseConfig {
	
	private static final String MYBATIS_CONFIG_LOCATION = "mybatis/configuration.xml";
	private static final String ACCOUNT_MAPPER_LOCATION = "classpath:mybatis/mappers/account/*.xml";
	
	@FlywayDataSource
	@Bean(name = "accountDataScource")
	@ConfigurationProperties(prefix = "datasource.account")
    public DataSource accountDataScource() {
		return DataSourceBuilder.create().build();
    }
	
	@Bean
	public PlatformTransactionManager accountTransactionManager(@Qualifier("accountDataScource") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
	
	@Bean(initMethod = "migrate")
	public Flyway flyway(@Qualifier("accountDataScource") DataSource dataSource) {
		Flyway flyway = new Flyway();
		flyway.setLocations("classpath:db/migration/primary");
		flyway.setDataSource(dataSource);
		flyway.setBaselineOnMigrate(true);
		return flyway;
	}
	
	@Bean(name = "accountSqlSessionFactory")
	public SqlSessionFactory accountSqlSessionFactory(@Qualifier("accountDataScource") DataSource dataSource) throws Exception {
		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
		sqlSessionFactory.setDataSource(dataSource);
		sqlSessionFactory.setConfigLocation(new ClassPathResource(MYBATIS_CONFIG_LOCATION));
		sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(ACCOUNT_MAPPER_LOCATION));
		return sqlSessionFactory.getObject();
	}
	
	@Bean(name = "accountSqlSessionTemplate")
	public SqlSessionTemplate accountSqlSessionTemplate(@Qualifier("accountSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
	
}
