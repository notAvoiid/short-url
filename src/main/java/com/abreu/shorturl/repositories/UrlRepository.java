package com.abreu.shorturl.repositories;

import com.abreu.shorturl.models.Url;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UrlRepository extends MongoRepository<Url, String> {
}
