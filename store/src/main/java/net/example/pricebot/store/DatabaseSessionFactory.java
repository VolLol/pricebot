package net.example.pricebot.store;

import net.example.pricebot.store.mappers.GoodsHistoryPriceMapper;
import net.example.pricebot.store.mappers.GoodsInfoMapper;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;

public class DatabaseSessionFactory {
    private final SqlSessionFactory session;

    public DatabaseSessionFactory(DataSource dataSource) {
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(GoodsInfoMapper.class);
        configuration.addMapper(GoodsHistoryPriceMapper.class);
        session = new SqlSessionFactoryBuilder().build(configuration);
    }

    public SqlSessionFactory getSession() {
        return session;
    }
}
