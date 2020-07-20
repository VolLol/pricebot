package net.example.pricebot.store;

import org.apache.ibatis.datasource.DataSourceFactory;
import org.apache.ibatis.datasource.pooled.PooledDataSource;

import javax.sql.DataSource;
import java.util.Properties;

public class StoreDataSourceFactory implements DataSourceFactory {
    private Properties properties;


    @Override
    public void setProperties(Properties propertiesIn) {
        properties = propertiesIn;
    }

    @Override
    public DataSource getDataSource() {
        PooledDataSource pooledDataSource = new PooledDataSource();

        pooledDataSource.setDriver(properties.getProperty("driver"));
        pooledDataSource.setUrl(properties.getProperty("url"));
        pooledDataSource.setUsername(properties.getProperty("user"));
        pooledDataSource.setPassword(properties.getProperty("password"));

        return pooledDataSource;
    }
}
