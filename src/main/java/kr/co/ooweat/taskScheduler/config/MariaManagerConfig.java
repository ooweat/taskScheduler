package kr.co.ooweat.taskScheduler.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
@MapperScan(value = "kr.co.ooweat.taskScheduler.mappers.maria.manager", sqlSessionFactoryRef = "mariaManagerSqlSessionFactory")
public class MariaManagerConfig {

    @Primary
    @Bean(name = "mariaManager")
    @ConfigurationProperties(prefix = "spring.maria.datasource.manager")
    public DataSource mariaManagerDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "mariaManagerSqlSessionFactory")
    public SqlSessionFactory mariaManagerSqlSessionFactory(@Qualifier("mariaManager") DataSource mariaManagerDataSource, ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(mariaManagerDataSource);
        sqlSessionFactoryBean.setTypeAliasesPackage("kr.co.ooweat.taskScheduler.model");
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:mappers/maria/manager/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }

    @Primary
    @Bean(name = "mariaManagerSessionTemplate")
    public SqlSessionTemplate mariaManagerSqlSessionTemplate(@Qualifier("mariaManagerSqlSessionFactory") SqlSessionFactory mariaManagerSqlSessionFactory) {
        return new SqlSessionTemplate(mariaManagerSqlSessionFactory);
    }
}
