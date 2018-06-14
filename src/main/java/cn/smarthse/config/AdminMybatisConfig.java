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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;

//管理端数据库配置
//@Configuration
//@MapperScan(basePackages = "cn.smarthse.admin.dao", sqlSessionFactoryRef = "adminSqlSessionFactory")
public class AdminMybatisConfig {

	private final Log log = LogFactory.getLog(getClass());

	@Value("${spring.datasource.driver-class-name}")
	private String driverClassName;

	@Value("${spring.datasource.master.url}")
	private String url_master;
	@Value("${spring.datasource.master.username}")
	private String username_master;
	@Value("${spring.datasource.master.password}")
	private String password_master;

	// 定义管理端数据源，druid数据库连接池
	@Bean(name = "admin")
	public DataSource admin() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(this.driverClassName);
		dataSource.setUrl(this.url_master);
		dataSource.setUsername(this.username_master);
		dataSource.setPassword(this.password_master);
		log.info("step1：定义管理端数据源，druid数据库连接池: " + dataSource.getUrl());
		return dataSource;
	}

	// step2：定义SqlSessionFactory
	@Bean(name = "adminSqlSessionFactory")
	// @DependsOn(value = "defaultDataSource")
	@ConfigurationProperties(prefix = "mybatis.readwrite")
	public SqlSessionFactory adminSqlSessionFactory() throws Exception {
		log.info("step2：定义管理端SqlSessionFactory");
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		// 数据源
		bean.setDataSource(admin());
		// 扫描mybatis mapper文件
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		bean.setMapperLocations(resolver.getResources("classpath*:mapper/readwrite/**/*Mapper.xml"));

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
	@Bean(name = "adminSqlSession")
	// @DependsOn(value = "defaultSqlSessionFactory")
	public SqlSession adminSqlSession() throws Exception {
		log.info("step3：定义管理端sqlSession");
		SqlSessionTemplate a = new SqlSessionTemplate(adminSqlSessionFactory());
		return a;
	}

	// <!-- step4：定义事务管理器-->
	@Bean
	// @DependsOn(value = "defaultDataSource")
	public PlatformTransactionManager adminTransactionManager() {
		log.info("step4：定义管理端事务管理器transactionManager");
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(admin());
		return transactionManager;
	}

}
