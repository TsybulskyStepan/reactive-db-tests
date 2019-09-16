package com.ww.company.repository;

import com.ww.company.model.Company;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface MongoRepository extends ReactiveCrudRepository<Company, String> {}
