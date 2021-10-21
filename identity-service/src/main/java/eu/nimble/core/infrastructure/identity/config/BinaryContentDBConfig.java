package eu.nimble.core.infrastructure.identity.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;

/**
 * Created by suat on 04-Dec-18.
 */
@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties
@PropertySource("classpath:bootstrap.yml")
@EnableJpaRepositories(
        transactionManagerRef = "binarycontentdbTransactionManager",
        basePackages = {"at.srfg.iot.common.utility.persistence.binary"}
)
@ComponentScan(basePackages = {"eu.nimble.core.infrastructure.identity.config", "at.srfg.iot.common.utility.config"})
public class BinaryContentDBConfig {

    @Autowired
    private BinaryDataSourceFactory binaryDataSourceFactory;

    @Bean(name = "binarycontentdbDataSource")
    public DataSource binaryContentDbdataSource() {
        return binaryDataSourceFactory.createDatasource("binarycontentdb");
    }
}