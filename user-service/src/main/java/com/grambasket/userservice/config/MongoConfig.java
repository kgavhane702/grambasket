package com.grambasket.userservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing // This single annotation enables the automatic handling of @CreatedDate and @LastModifiedDate
public class MongoConfig {
}