package org.springframework.samples.petclinic.config;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.util.List;

@Profile("play")
@Configuration
public class DataSourceDecoratorConfig {
	private static final Logger log = LoggerFactory.getLogger(DataSourceDecoratorConfig.class);

	@Bean
	public QueryExecutionListener queryExecutionListener(@Value("${decorator.datasource.datasource-proxy.slow-query.threshold}") Long threshold) {
		return new QueryExecutionListener() {
			@Override
			public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
				log.info("Connection Id: {} for the query: {}", execInfo.getConnectionId(), execInfo.getStatement());
			}

			@Override
			public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
				if (execInfo.getElapsedTime() > threshold) {
					log.warn("Slow query detected - {} ms taken to process: {}", execInfo.getElapsedTime(), execInfo.getStatement());
				}

			}
		};
	}

	//	@Bean(name = "dataSource")
	DataSource getDataSource(DataSource dataSource) {
		return ProxyDataSourceBuilder.create(dataSource)
			.beforeQuery((execInfo, queryForList) -> {
				log.info("Connection Id: {} for the query: {}", execInfo.getConnectionId(), execInfo.getStatement());
			})
			.afterQuery((execInfo, queryInfoList) -> {
				if (execInfo.getElapsedTime() > 2000) {
					log.info("HIGH TIME - {} ms taken to process: {}", execInfo.getElapsedTime(), execInfo.getStatement());
				}
			})
			.build();
	}
}
