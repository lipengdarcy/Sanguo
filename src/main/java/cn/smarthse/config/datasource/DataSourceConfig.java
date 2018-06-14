package cn.smarthse.config.datasource;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;

@Configuration
@MapperScan(basePackages = "cn.smarthse.admin.dao", sqlSessionFactoryRef = "adminSqlSessionFactory")
public class DataSourceConfig {

	private final Log log = LogFactory.getLog(getClass());

	@Value("${spring.datasource.driver-class-name}")
	private String driverClassName;

	@Value("${spring.datasource.master.url}")
	private String url_master;
	@Value("${spring.datasource.master.username}")
	private String username_master;
	@Value("${spring.datasource.master.password}")
	private String password_master;

	@Value("${spring.datasource.slave1.url}")
	private String url_slave1;
	@Value("${spring.datasource.slave1.username}")
	private String username_slave1;
	@Value("${spring.datasource.slave1.password}")
	private String password_slave1;

	@Value("${spring.datasource.slave2.url}")
	private String url_slave2;
	@Value("${spring.datasource.slave2.username}")
	private String username_slave2;
	@Value("${spring.datasource.slave2.password}")
	private String password_slave2;

	@Value("${spring.datasource.slave3.url}")
	private String url_slave3;
	@Value("${spring.datasource.slave3.username}")
	private String username_slave3;
	@Value("${spring.datasource.slave3.password}")
	private String password_slave3;

	@Bean("master")
	// @Primary
	public DataSource master() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(this.driverClassName);
		dataSource.setUrl(this.url_master);
		dataSource.setUsername(this.username_master);
		dataSource.setPassword(this.password_master);
		log.info("step1.2：定义业务主数据源（读写）: " + dataSource.getUrl());
		return dataSource;
		// return DataSourceBuilder.create().build();
	}

	/**
	 * Slave alpha data source.
	 *
	 * @return the data source
	 */
	@Bean("slaveAlpha")
	public DataSource slaveAlpha() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(this.driverClassName);
		dataSource.setUrl(this.url_slave1);
		dataSource.setUsername(this.username_slave1);
		dataSource.setPassword(this.password_slave1);
		log.info("step1.3：定义业务从数据源1（读）: " + dataSource.getUrl());
		return dataSource;
	}

	/**
	 * Slave beta data source.
	 *
	 * @return the data source
	 */
	@Bean("slaveBeta")
	// @ConfigurationProperties(prefix = "spring.datasource.slave-beta")
	public DataSource slaveBeta() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(this.driverClassName);
		dataSource.setUrl(this.url_slave2);
		dataSource.setUsername(this.username_slave2);
		dataSource.setPassword(this.password_slave2);
		log.info("step1.4：定义业务从数据源2（读）: " + dataSource.getUrl());
		return dataSource;
	}

	/**
	 * Slave gamma data source.
	 *
	 * @return the data source
	 */
	@Bean("slaveGamma")
	public DataSource slaveGamma() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(this.driverClassName);
		dataSource.setUrl(this.url_slave3);
		dataSource.setUsername(this.username_slave3);
		dataSource.setPassword(this.password_slave3);
		log.info("step1.5：定义业务从数据源3（读）: " + dataSource.getUrl());
		return dataSource;
	}

	/**
	 * Dynamic data source.
	 *
	 * @return the data source
	 */
	@Bean("dynamicDataSource")
	public DataSource dynamicDataSource() {
		DynamicRoutingDataSource dynamicRoutingDataSource = new DynamicRoutingDataSource();
		Map<Object, Object> dataSourceMap = new HashMap<>(4);
		dataSourceMap.put(DataSourceKey.master.name(), master());
		dataSourceMap.put(DataSourceKey.slaveAlpha.name(), slaveAlpha());
		dataSourceMap.put(DataSourceKey.slaveBeta.name(), slaveBeta());
		dataSourceMap.put(DataSourceKey.slaveGamma.name(), slaveGamma());

		// 将 master 数据源作为默认指定的数据源
		dynamicRoutingDataSource.setDefaultTargetDataSource(master());
		// 将 master 和 slave 数据源作为指定的数据源
		dynamicRoutingDataSource.setTargetDataSources(dataSourceMap);

		// 将数据源的 key 放到数据源上下文的 key 集合中，用于切换时判断数据源是否有效
		DynamicDataSourceContextHolder.dataSourceKeys.addAll(dataSourceMap.keySet());

		// 将 Slave 数据源的 key 放在集合中，用于轮循
		DynamicDataSourceContextHolder.slaveDataSourceKeys.addAll(dataSourceMap.keySet());
		DynamicDataSourceContextHolder.slaveDataSourceKeys.remove(DataSourceKey.master.name());
		log.info("DataSourceConfig dynamicDataSource：定义动态数据源（统一的数据源入口）");
		return dynamicRoutingDataSource;
	}

	/**
	 * 配置 SqlSessionFactory2:业务端，主从数据库
	 */
	@Bean(name = "adminSqlSessionFactory")
	// @DependsOn(value = "dynamicDataSource")
	// @ConfigurationProperties(prefix = "mybatis.readwrite")
	public SqlSessionFactory sqlSessionFactory() throws Exception {

		log.info("step2.2：定义业务端SqlSessionFactory");
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		// 配置数据源，此处配置为关键配置，如果没有将 dynamicDataSource 作为数据源则不能实现切换
		bean.setDataSource(dynamicDataSource());

		// 扫描mybatis mapper文件
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		bean.setTypeAliasesPackage("cn.smarthse.dao");
		bean.setMapperLocations(resolver.getResources("classpath*:mapper/readwrite/**/*Mapper.xml"));
		// 配置文件
		log.debug("读取配置前：" + new Date());
		org.apache.ibatis.session.Configuration mybatisConfig = bean.getObject().getConfiguration();
		log.debug("读取配置后：" + new Date());
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
		log.info("DataSourceConfig dynamicDataSource：定义动态数据源的SqlSessionFactoryBean");
		return bean.getObject();
	}

	// 定义sqlSession
	@Scope("prototype")
	@Bean(name = "multiSqlSession")
	// @DependsOn(value = "defaultSqlSessionFactory")
	public SqlSession sqlSession() throws Exception {
		SqlSessionTemplate a = new SqlSessionTemplate(sqlSessionFactory());
		log.info("DataSourceConfig dynamicDataSource：定义动态数据源的sqlSession");
		return a;
	}

	// 定义事务管理器
	@Bean
	// @DependsOn(value = "defaultDataSource")
	public PlatformTransactionManager dataSourceTransactionManager() {
		log.info("step4：定义事务管理器transactionManager");
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dynamicDataSource());
		return transactionManager;
	}

}