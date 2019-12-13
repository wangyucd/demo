### bootstrap

#### 1.构造器阶段

  ~~~
   org.springframework.boot.SpringApplication#SpringApplication(org.springframework.core.io.ResourceLoader, java.lang.Class<?>... primarySources)
   
   
   this.resourceLoader = resourceLoader;
   Assert.notNull(primarySources, "PrimarySources must not be null");
   this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));
   this.webApplicationType = WebApplicationType.deduceFromClasspath();
   setInitializers((Collection) getSpringFactoriesInstances(ApplicationContextInitializer.class));
   setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));
   this.mainApplicationClass = deduceMainApplicationClass();
  ~~~
  1.1  WebApplicationType.deduceFromClasspath();
  ~~~
  通过Class.forName(基础类型，缓存，内部类)判断：
  org.springframework.web.reactive.DispatcherHandler->REACTIVE
  "javax.servlet.Servlet",
			"org.springframework.web.context.ConfigurableWebApplicationContext"->SERVLET
  ~~~

  2.2 setInitializers((Collection) getSpringFactoriesInstances(ApplicationContextInitializer.class));
  ~~~
  关键方法：org.springframework.core.io.support.SpringFactoriesLoader#loadSpringFactories
  
  读取META-INF/spring.factories
  jar:file:/C:/Users/shuangyu/.m2/repository/org/springframework/boot/spring-boot/2.2.2.RELEASE/spring-boot-2.2.2.RELEASE.jar!/META-INF/spring.factories
  
  org.springframework.context.ApplicationContextInitializer
  
  LinkedMultiValueMap 保存多值map 例如key为     org.springframework.boot.diagnostics.FailureAnalyzer value为多个，org.springframework.boot.diagnostics.analyzer.BeanCurrentlyInCreationFailureAnalyzer
  
  keySet为 13个
  [org.springframework.boot.diagnostics.FailureAnalyzer, org.springframework.boot.env.EnvironmentPostProcessor, org.springframework.boot.SpringApplicationRunListener, org.springframework.context.ApplicationContextInitializer, org.springframework.boot.env.PropertySourceLoader, org.springframework.context.ApplicationListener, org.springframework.boot.diagnostics.FailureAnalysisReporter, org.springframework.boot.SpringBootExceptionReporter, org.springframework.boot.autoconfigure.AutoConfigurationImportFilter, org.springframework.boot.autoconfigure.AutoConfigurationImportListener, org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider, org.springframework.boot.autoconfigure.EnableAutoConfiguration, org.springframework.beans.BeanInfoFactory]
  
  获取7个初始化器
  ~~~


    2.2.1 org.springframework.boot.SpringApplication#createSpringFactoriesInstances
~~~
实例化工厂类，再依赖于PriorityOrdered 进行排序
~~~
  2.3 org.springframework.boot.SpringApplication#setListeners
~~~
ApplicationListener 的实现类，11个监听器
~~~

  2.4 org.springframework.boot.SpringApplication#deduceMainApplicationClass
~~~
循环当前堆栈，找到main方法所在的类名，forname获取Class对象
~~~



#### 2.运行阶段

~~~

public ConfigurableApplicationContext run(String... args) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		ConfigurableApplicationContext context = null;
		Collection<SpringBootExceptionReporter> exceptionReporters = new ArrayList<>();
		configureHeadlessProperty();		
		
		listeners.starting();
		try {
			ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
			ConfigurableEnvironment environment = prepareEnvironment(listeners, applicationArguments);
			configureIgnoreBeanInfo(environment);
			Banner printedBanner = printBanner(environment);
			context = createApplicationContext();
			exceptionReporters = getSpringFactoriesInstances(SpringBootExceptionReporter.class,
					new Class[] { ConfigurableApplicationContext.class }, context);
			prepareContext(context, environment, listeners, applicationArguments, printedBanner);
			refreshContext(context);
			afterRefresh(context, applicationArguments);
			stopWatch.stop();
			if (this.logStartupInfo) {
				new StartupInfoLogger(this.mainApplicationClass).logStarted(getApplicationLog(), stopWatch);
			}
			listeners.started(context);
			callRunners(context, applicationArguments);
		}
		catch (Throwable ex) {
			handleRunFailure(context, ex, exceptionReporters, listeners);
			throw new IllegalStateException(ex);
		}

		try {
			listeners.running(context);
		}
		catch (Throwable ex) {
			handleRunFailure(context, ex, exceptionReporters, null);
			throw new IllegalStateException(ex);
		}
		return context;
	}
~~~



  2.1 	listeners.starting();

~~~
		实际获取的是工厂类
		org.springframework.boot.SpringApplicationRunListener=\
        org.springframework.boot.context.event.EventPublishingRunListener
       
        EventPublishingRunListener 持有构造器节点的11个ApplicationListener
        SpringApplicationRunListeners 持有SpringApplicationRunListener列表，且提供处理方法循环调用
		SpringApplicationRunListeners listeners = getRunListeners(args);
		广播事件 ApplicationStartingEvent
		具体实现：AbstractApplicationEventMulticaster#supportsEvent(org.springframework.context.ApplicationListener<?>, org.springframework.core.ResolvableType, java.lang.Class<?>)
		
		首先匹配的是
		1.LoggingApplicationListener 
		由于默认依赖logback，实际执行org.springframework.boot.logging.logback.LogbackLoggingSystem#beforeInitialize
		
		2.BackgroundPreinitializer
		对于一些耗时的任务使用一个后台线程尽早触发它们开始执行初始化
		
			runSafely(new ConversionServiceInitializer());
			runSafely(new ValidationInitializer());
			runSafely(new MessageConverterInitializer());
			runSafely(new JacksonInitializer());
			runSafely(new CharsetInitializer());
					
		
		3.DelegatingApplicationListener
		监听应用事件，并将这些应用事件广播给环境属性context.listener.classes指定的那些监听器(较优雅，不用添加spring.factories)。
		
		4.LiquibaseServiceLocatorApplicationListener
		
		如果存在liquibase.servicelocator.ServiceLocator）
        则使用springboot相关的版本进行替代
		

		执行方法：org.springframework.context.event.SimpleApplicationEventMulticaster#invokeListener
		实际是执行：
		org.springframework.context.ApplicationListener#onApplicationEvent
		
~~~



  2.2 org.springframework.boot.SpringApplication#prepareEnvironment

~~~
	//get StandardServletEnvironment
	先调用StandardServletEnvironment 
	再调用StandardEnvironment 获取getSystemProperties，getSystemEnvironment
	
	ConfigurableEnvironment environment = getOrCreateEnvironment();
	//初始化ConversionService
	configureEnvironment(environment, applicationArguments.getSourceArgs());
	ConfigurationPropertySources.attach(environment);
	listeners.environmentPrepared(environment);
	bindToSpringApplication(environment);
	if (!this.isCustomEnvironment) {
		environment = new EnvironmentConverter(getClassLoader()).convertEnvironmentIfNecessary(environment,
	deduceEnvironmentClass());
	}
	ConfigurationPropertySources.attach(environment);
~~~



​    2.2.1  listeners.environmentPrepared(environment); 环境准备事件触发

~~~
调用 org.springframework.boot.context.event.EventPublishingRunListener#environmentPrepared

[org.springframework.boot.context.config.ConfigFileApplicationListener@5158b42f, 
org.springframework.boot.context.config.AnsiOutputApplicationListener@595b007d, org.springframework.boot.context.logging.LoggingApplicationListener@72d1ad2e, org.springframework.boot.context.logging.ClasspathLoggingApplicationListener@2d7275fc, org.springframework.boot.autoconfigure.BackgroundPreinitializer@399f45b1, org.springframework.boot.context.config.DelegatingApplicationListener@38c6f217, org.springframework.boot.context.FileEncodingApplicationListener@3a93b025]
~~~



​    2.2.1.1 ConfigFileApplicationListener

~~~

org.springframework.boot.env.EnvironmentPostProcessor=\
org.springframework.boot.cloud.CloudFoundryVcapEnvironmentPostProcessor,\
org.springframework.boot.env.SpringApplicationJsonEnvironmentPostProcessor,\
org.springframework.boot.env.SystemEnvironmentPropertySourceEnvironmentPostProcessor,\
org.springframework.boot.reactor.DebugAgentEnvironmentPostProcessor
将自身ConfigFileApplicationListener也加入其中。
依次处理5个EnvironmentPostProcessor



org.springframework.boot.context.config.ConfigFileApplicationListener@235834f2, org.springframework.boot.reactor.DebugAgentEnvironmentPostProcessor@6babf3bf]

~~~

​    2.2.1.1.1 ConfigFileApplicationListener

~~~


[org.springframework.boot.env.SystemEnvironmentPropertySourceEnvironmentPostProcessor@4de4b452, org.springframework.boot.env.SpringApplicationJsonEnvironmentPostProcessor@50b5ac82, org.springframework.boot.cloud.CloudFoundryVcapEnvironmentPostProcessor@101952da, 

添加RandomValuePropertySource到环境中。
重新load执行方法  new Loader(environment, resourceLoader).load();

PropertySourceLoader的实现类包括：
[org.springframework.boot.env.PropertiesPropertySourceLoader, 后缀xml，properties
org.springframework.boot.env.YamlPropertySourceLoader, 后缀yaml，yml]

搜索classpath下的 根路径或者config路径。spring.config.name可以指定名称,默认application
~~~

​    2.2.1.1.2 DebugAgentEnvironmentPostProcessor

~~~
reactor.tools.agent.ReactorDebugAgent  reactor debug的时候需要用到的
~~~

  2.2.1.2 AnsiOutputApplicationListener

```

```

2.2.1.3 FileEncodingApplicationListener

~~~
spring.mandatory-file-encoding和系统属性file.encoding匹配，不一致则抛出异常
~~~



2.3 Banner printedBanner = printBanner(environment);

~~~
打印应用程序banner，可以是图片，字符等，输入到console log  
~~~



2.4 context = createApplicationContext();

```
根据应用类型，实例化对应的context
org.springframework.context.annotation.AnnotationConfigApplicationContext
org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext
org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebServerApplicationContext
```



2.5  exceptionReporters = getSpringFactoriesInstances(SpringBootExceptionReporter.class,      new Class[] { ConfigurableApplicationContext.class }, context);

~~~
org.springframework.boot.SpringBootExceptionReporter=\
org.springframework.boot.diagnostics.FailureAnalyzers
~~~

2.6  private void prepareContext 准备context

```
context.getBeanFactory().setConversionService(ApplicationConversionService.getSharedInstance());
实际来源于
new ApplicationConversionService()

applyInitializers(ConfigurableApplicationContext context) 应用7个初始化器
```





#### 附录

~~~java

 {org.springframework.boot.diagnostics.FailureAnalyzer=[org.springframework.boot.diagnostics.analyzer.BeanCurrentlyInCreationFailureAnalyzer, org.springframework.boot.diagnostics.analyzer.BeanDefinitionOverrideFailureAnalyzer, org.springframework.boot.diagnostics.analyzer.BeanNotOfRequiredTypeFailureAnalyzer, org.springframework.boot.diagnostics.analyzer.BindFailureAnalyzer, org.springframework.boot.diagnostics.analyzer.BindValidationFailureAnalyzer, org.springframework.boot.diagnostics.analyzer.UnboundConfigurationPropertyFailureAnalyzer, org.springframework.boot.diagnostics.analyzer.ConnectorStartFailureAnalyzer, org.springframework.boot.diagnostics.analyzer.NoSuchMethodFailureAnalyzer, org.springframework.boot.diagnostics.analyzer.NoUniqueBeanDefinitionFailureAnalyzer, org.springframework.boot.diagnostics.analyzer.PortInUseFailureAnalyzer, org.springframework.boot.diagnostics.analyzer.ValidationExceptionFailureAnalyzer, org.springframework.boot.diagnostics.analyzer.InvalidConfigurationPropertyNameFailureAnalyzer, org.springframework.boot.diagnostics.analyzer.InvalidConfigurationPropertyValueFailureAnalyzer, org.springframework.boot.autoconfigure.diagnostics.analyzer.NoSuchBeanDefinitionFailureAnalyzer, org.springframework.boot.autoconfigure.flyway.FlywayMigrationScriptMissingFailureAnalyzer, org.springframework.boot.autoconfigure.jdbc.DataSourceBeanCreationFailureAnalyzer, org.springframework.boot.autoconfigure.jdbc.HikariDriverConfigurationFailureAnalyzer, org.springframework.boot.autoconfigure.session.NonUniqueSessionRepositoryFailureAnalyzer], org.springframework.boot.env.EnvironmentPostProcessor=[org.springframework.boot.cloud.CloudFoundryVcapEnvironmentPostProcessor, org.springframework.boot.env.SpringApplicationJsonEnvironmentPostProcessor, org.springframework.boot.env.SystemEnvironmentPropertySourceEnvironmentPostProcessor, org.springframework.boot.reactor.DebugAgentEnvironmentPostProcessor], org.springframework.boot.SpringApplicationRunListener=[org.springframework.boot.context.event.EventPublishingRunListener], org.springframework.context.ApplicationContextInitializer=[org.springframework.boot.context.ConfigurationWarningsApplicationContextInitializer, org.springframework.boot.context.ContextIdApplicationContextInitializer, org.springframework.boot.context.config.DelegatingApplicationContextInitializer, org.springframework.boot.rsocket.context.RSocketPortInfoApplicationContextInitializer, org.springframework.boot.web.context.ServerPortInfoApplicationContextInitializer, org.springframework.boot.autoconfigure.SharedMetadataReaderFactoryContextInitializer, org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener], org.springframework.boot.env.PropertySourceLoader=[org.springframework.boot.env.PropertiesPropertySourceLoader, org.springframework.boot.env.YamlPropertySourceLoader], org.springframework.context.ApplicationListener=[org.springframework.boot.ClearCachesApplicationListener, org.springframework.boot.builder.ParentContextCloserApplicationListener, org.springframework.boot.cloud.CloudFoundryVcapEnvironmentPostProcessor, org.springframework.boot.context.FileEncodingApplicationListener, org.springframework.boot.context.config.AnsiOutputApplicationListener, org.springframework.boot.context.config.ConfigFileApplicationListener, org.springframework.boot.context.config.DelegatingApplicationListener, org.springframework.boot.context.logging.ClasspathLoggingApplicationListener, org.springframework.boot.context.logging.LoggingApplicationListener, org.springframework.boot.liquibase.LiquibaseServiceLocatorApplicationListener, org.springframework.boot.autoconfigure.BackgroundPreinitializer], org.springframework.boot.diagnostics.FailureAnalysisReporter=[org.springframework.boot.diagnostics.LoggingFailureAnalysisReporter], org.springframework.boot.SpringBootExceptionReporter=[org.springframework.boot.diagnostics.FailureAnalyzers], org.springframework.boot.autoconfigure.AutoConfigurationImportFilter=[org.springframework.boot.autoconfigure.condition.OnBeanCondition, org.springframework.boot.autoconfigure.condition.OnClassCondition, org.springframework.boot.autoconfigure.condition.OnWebApplicationCondition], org.springframework.boot.autoconfigure.AutoConfigurationImportListener=[org.springframework.boot.autoconfigure.condition.ConditionEvaluationReportAutoConfigurationImportListener], org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider=[org.springframework.boot.autoconfigure.freemarker.FreeMarkerTemplateAvailabilityProvider, org.springframework.boot.autoconfigure.mustache.MustacheTemplateAvailabilityProvider, org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAvailabilityProvider, org.springframework.boot.autoconfigure.thymeleaf.ThymeleafTemplateAvailabilityProvider, org.springframework.boot.autoconfigure.web.servlet.JspTemplateAvailabilityProvider], org.springframework.boot.autoconfigure.EnableAutoConfiguration=[org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration, org.springframework.boot.autoconfigure.aop.AopAutoConfiguration, org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration, org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration, org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration, org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration, org.springframework.boot.autoconfigure.cloud.CloudServiceConnectorsAutoConfiguration, org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration, org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration, org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration, org.springframework.boot.autoconfigure.couchbase.CouchbaseAutoConfiguration, org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration, org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration, org.springframework.boot.autoconfigure.data.cassandra.CassandraReactiveDataAutoConfiguration, org.springframework.boot.autoconfigure.data.cassandra.CassandraReactiveRepositoriesAutoConfiguration, org.springframework.boot.autoconfigure.data.cassandra.CassandraRepositoriesAutoConfiguration, org.springframework.boot.autoconfigure.data.couchbase.CouchbaseDataAutoConfiguration, org.springframework.boot.autoconfigure.data.couchbase.CouchbaseReactiveDataAutoConfiguration, org.springframework.boot.autoconfigure.data.couchbase.CouchbaseReactiveRepositoriesAutoConfiguration, org.springframework.boot.autoconfigure.data.couchbase.CouchbaseRepositoriesAutoConfiguration, org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration, org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration, org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration, org.springframework.boot.autoconfigure.data.elasticsearch.ReactiveElasticsearchRepositoriesAutoConfiguration, org.springframework.boot.autoconfigure.data.elasticsearch.ReactiveRestClientAutoConfiguration, org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration, org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration, org.springframework.boot.autoconfigure.data.ldap.LdapRepositoriesAutoConfiguration, org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration, org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration, org.springframework.boot.autoconfigure.data.mongo.MongoReactiveRepositoriesAutoConfiguration, org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration, org.springframework.boot.autoconfigure.data.neo4j.Neo4jDataAutoConfiguration, org.springframework.boot.autoconfigure.data.neo4j.Neo4jRepositoriesAutoConfiguration, org.springframework.boot.autoconfigure.data.solr.SolrRepositoriesAutoConfiguration, org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration, org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration, org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration, org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration, org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration, org.springframework.boot.autoconfigure.elasticsearch.jest.JestAutoConfiguration, org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientAutoConfiguration, org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration, org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration, org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration, org.springframework.boot.autoconfigure.h2.H2ConsoleAutoConfiguration, org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration, org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration, org.springframework.boot.autoconfigure.hazelcast.HazelcastJpaDependencyAutoConfiguration, org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration, org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration, org.springframework.boot.autoconfigure.influx.InfluxDbAutoConfiguration, org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration, org.springframework.boot.autoconfigure.integration.IntegrationAutoConfiguration, org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration, org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration, org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration, org.springframework.boot.autoconfigure.jdbc.JndiDataSourceAutoConfiguration, org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration, org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration, org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration, org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration, org.springframework.boot.autoconfigure.jms.JndiConnectionFactoryAutoConfiguration, org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration, org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration, org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAutoConfiguration, org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration, org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration, org.springframework.boot.autoconfigure.jsonb.JsonbAutoConfiguration, org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration, org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapAutoConfiguration, org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration, org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration, org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration, org.springframework.boot.autoconfigure.mail.MailSenderValidatorAutoConfiguration, org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration, org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration, org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration, org.springframework.boot.autoconfigure.mustache.MustacheAutoConfiguration, org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration, org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration, org.springframework.boot.autoconfigure.rsocket.RSocketMessagingAutoConfiguration, org.springframework.boot.autoconfigure.rsocket.RSocketRequesterAutoConfiguration, org.springframework.boot.autoconfigure.rsocket.RSocketServerAutoConfiguration, org.springframework.boot.autoconfigure.rsocket.RSocketStrategiesAutoConfiguration, org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration, org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration, org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration, org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration, org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration, org.springframework.boot.autoconfigure.security.rsocket.RSocketSecurityAutoConfiguration, org.springframework.boot.autoconfigure.security.saml2.Saml2RelyingPartyAutoConfiguration, org.springframework.boot.autoconfigure.sendgrid.SendGridAutoConfiguration, org.springframework.boot.autoconfigure.session.SessionAutoConfiguration, org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration, org.springframework.boot.autoconfigure.security.oauth2.client.reactive.ReactiveOAuth2ClientAutoConfiguration, org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration, org.springframework.boot.autoconfigure.security.oauth2.resource.reactive.ReactiveOAuth2ResourceServerAutoConfiguration, org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration, org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration, org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration, org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration, org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration, org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration, org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration, org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration, org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration, org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration, org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerFactoryAutoConfiguration, org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration, org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration, org.springframework.boot.autoconfigure.web.reactive.function.client.ClientHttpConnectorAutoConfiguration, org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration, org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration, org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration, org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration, org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration, org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration, org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration, org.springframework.boot.autoconfigure.websocket.reactive.WebSocketReactiveAutoConfiguration, org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration, org.springframework.boot.autoconfigure.websocket.servlet.WebSocketMessagingAutoConfiguration, org.springframework.boot.autoconfigure.webservices.WebServicesAutoConfiguration, org.springframework.boot.autoconfigure.webservices.client.WebServiceTemplateAutoConfiguration, org.mybatis.spring.boot.autoconfigure.MybatisLanguageDriverAutoConfiguration, org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration], org.springframework.beans.BeanInfoFactory=[org.springframework.beans.ExtendedBeanInfoFactory]}
~~~
