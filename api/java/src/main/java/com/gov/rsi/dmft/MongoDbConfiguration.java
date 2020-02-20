package com.gov.rsi.dmft;

import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

import com.gov.rsi.dmft.controllers.DmerController;
import com.mongodb.ConnectionString;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * Configures the MongoDB connection with values from environment variables.
 * The following environment variables are examined:
 * 
 * MONGODB_HOST        defaults to localhost
 * MONGODB_PORT        defaults to 27017
 * MONGODB_DATABASE    defaults to test
 * MONGODB_USER        optional (if specified, both user and password are required)
 * MONGODB_PASSWORD    optional (if specified, both user and password are required)
 */
@Configuration
public class MongoDbConfiguration extends AbstractMongoClientConfiguration {

	private static Logger log = LoggerFactory.getLogger(MongoDbConfiguration.class);

	private Map<String, String> env;
	
	private String host;
	private String port;
	private String database;
	private String user;
	private String password;
	
	@Override
	public String getDatabaseName() {
		if (env == null) {
			load();
		}
		return database;
	}

	@Override
	@Bean
	public MongoClient mongoClient() {
		if (env == null) {
			load();
		}
		
		StringBuilder sb = new StringBuilder("mongodb://");
		if (user != null) {
			sb.append(user).append(':').append(password).append('@');
		}
		sb.append(host).append(':').append(port);
		
		String connection = sb.toString();
		ConnectionString cs = new ConnectionString(sb.toString());
		MongoClient client = MongoClients.create(cs);
		
		return client;
	}
	
	private void load() {
		env = System.getenv();
		host = env.getOrDefault("MONGODB_HOST", "localhost");
		port = env.getOrDefault("MONGODB_PORT", "27017");
		database = env.getOrDefault("MONGODB_DATABASE", "test");
		user = env.get("MONGODB_USER");
		password = env.get("MONGODB_PASSWORD");
		
		user = password == null ? null : user;
		password = user == null ? null : password;
	}
}