package cn.smarthse.config;

import java.util.Date;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;

//业务数据库配置
@Configuration
@MapperScan(basePackages = "cn.smarthse.business.dao", sqlSessionFactoryRef = "defaultSqlSessionFactory")
public class BusinessMybatisConfig {

	private final Log log = LogFactory.getLog(getClass());

	@Value("${spring.datasource.driver-class-name}")
	private String driverClassName;

	@Value("${spring.datasource.url}")
	private String url;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;

	// <!-- step1：定义数据源，druid数据库连接池 -->
	@Bean(name = "defaultDataSource")
	@Primary
	public DataSource druidDataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(this.driverClassName);
		dataSource.setUrl(this.url);
		dataSource.setUsername(this.username);
		dataSource.setPassword(this.password);
		log.info("step1：定义数据源，druid数据库连接池: " + dataSource.getUrl());
		return dataSource;
	}

	// step2：定义SqlSessionFactory
	/**
	 * 1、当使用MapperScannerConfigurer时不需要configLocation定义(
	 * 1：mapperLocations可以批量载入mapper
	 * ，但是MapperScannerConfigurer扫描mapper后不会将已存在的mapper加入到Configuration中
	 * 2：也可以定义configLocation文件，在里面设置settings和typeAliases等来覆写默认的配置
	 * 3：如果使用了configLocation文件
	 * ，并且里面也定义了mappers，那么在MapperScannerConfigurer扫描动作中就不会加入已经存在的mapper了
	 * （同mapperLocations情况一样） 4：综上所述：建议采用扫描来完成mapper的注册，
	 * 并且在sqlSessionFactory的bean配置中不需要设置mapperLocations，
	 * 如果设置了configLocation文件，那么configLocation文件里也不需要再设置mapper了
	 * 
	 */
	@Primary
	@Bean(name = "defaultSqlSessionFactory")
	//@DependsOn(value = "defaultDataSource")
	public SqlSessionFactory defaultSqlSessionFactory() throws Exception {
		log.info("step2：定义SqlSessionFactory");
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		// 1.数据源
		bean.setDataSource(druidDataSource());

		// 扫描mybatis mapper文件
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		// bean.setConfigLocation(resolver.getResource("classpath:mybatis/sqlMapConfig.xml"));
		bean.setMapperLocations(resolver.getResources("classpath*:mapper/default/**/*Mapper.xml"));

		// 2.配置文件
		log.debug("step2.1：读取配置前：" + new Date());
		org.apache.ibatis.session.Configuration mybatisConfig = bean.getObject().getConfiguration();
		log.debug("step2.1：读取配置后：" + new Date());
		mybatisConfig.setMapUnderscoreToCamelCase(true);
		mybatisConfig.setJdbcTypeForNull(JdbcType.NULL);
		// 全局的映射器启用或禁用缓存
		mybatisConfig.setCacheEnabled(true);
		// 允许 JDBC 支持生成的键。需要适合的驱动。如果设置为 true 则这个设置强制生成的键被使用，尽管一些驱动拒绝兼容但仍然有效
		mybatisConfig.setUseGeneratedKeys(true);
		// 配置默认的执行器。SIMPLE 执行器没有什么特别之处。REUSE 执行器重用预处理语句。BATCH 执行器重用语句和批量更新
		mybatisConfig.setDefaultExecutorType(ExecutorType.REUSE);
		// 全局启用或禁用延迟加载。当禁用时，所有关联对象都会即时加载。
		mybatisConfig.setLazyLoadingEnabled(true);
		// 设置超时时间，它决定驱动等待一个数据库响应的时间
		mybatisConfig.setDefaultStatementTimeout(25000);

		// 分页插件
		Interceptor pageHelper = new com.github.pagehelper.PageHelper();
		Properties p = new Properties();
		p.setProperty("dialect", "mysql");
		p.setProperty("offsetAsPageNum", "true");
		p.setProperty("rowBoundsWithCount", "true");
		p.setProperty("autoRuntimeDialect", "true");
		pageHelper.setProperties(p);
		mybatisConfig.addInterceptor(pageHelper);

		return bean.getObject();

	}

	// <!-- step3：定义sqlSession -->
	@Scope("prototype")
	@Bean(name = "defaultSqlSession")
	//@DependsOn(value = "defaultSqlSessionFactory")
	public SqlSession sqlSession() throws Exception {
		log.info("step3：定义sqlSession");
		SqlSessionTemplate a = new SqlSessionTemplate(defaultSqlSessionFactory());
		return a;
	}

	// <!-- step4：定义事务管理器-->
	@Bean
	//@DependsOn(value = "defaultDataSource")
	public PlatformTransactionManager dataSourceTransactionManager() {
		log.info("step4：定义事务管理器transactionManager");
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(druidDataSource());
		return transactionManager;
	}

}
