package com.romcharm.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import java.util.Collections;

@Configuration
public class MongoConfig extends AbstractMongoConfiguration {

    @Value("${MONGODB_PORT_27017_TCP_ADDR}")
    private String host;

    @Value("${MONGODB_PORT_27017_TCP_PORT}")
    private int port;

    @Value("${MONGODB_USERNAME}")
    private String username;

    @Value("${MONGODB_DBNAME}")
    private String mongoDatabaseName;

    @Value("${MONGODB_PASSWORD}")
    private String password;

    @Override
    protected String getDatabaseName() {
        return mongoDatabaseName;
    }

    @Override
    public Mongo mongo() throws Exception {
        return new MongoClient(new ServerAddress(host, port),
                Collections.singletonList(MongoCredential.createCredential(username, "admin", password.toCharArray())));
    }
}
