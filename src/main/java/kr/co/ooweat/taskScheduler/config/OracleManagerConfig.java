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

import javax.sql.DataSource;

@Configuration
@MapperScan(value = "kr.co.ooweat.taskScheduler.mappers.oracle.manager", sqlSessionFactoryRef = "oracleManagerSqlSessionFactory")
public class OracleManagerConfig {

    @Bean(name = "oracleManager")
    @ConfigurationProperties(prefix = "spring.oracle.datasource.manager")
    public DataSource oraclemanagerDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "oracleManagerSqlSessionFactory")
    public SqlSessionFactory oraclemanagerSqlSessionFactory(@Qualifier("oracleManager") DataSource oracleManagerDataSource, ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(oracleManagerDataSource);
        sqlSessionFactoryBean.setTypeAliasesPackage("kr.co.ooweat.taskScheduler.model");
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:mappers/oracle/manager/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name = "oracleManagerSessionTemplate")
    public SqlSessionTemplate oracleManagerSqlSessionTemplate(@Qualifier("oracleManagerSqlSessionFactory") SqlSessionFactory oracleManagerSqlSessionFactory){
        return new SqlSessionTemplate(oracleManagerSqlSessionFactory);
    }
}
