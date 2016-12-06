package com.romcharm.repositories;

import com.romcharm.domain.Family;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamiliesRespository extends MongoRepository<Family, String> {
    @Override
    Family findOne(String familyName);

    @Override
    Family save(Family family);
}
