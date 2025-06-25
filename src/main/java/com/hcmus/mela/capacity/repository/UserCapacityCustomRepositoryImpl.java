package com.hcmus.mela.capacity.repository;

import com.hcmus.mela.capacity.model.UserCapacity;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class UserCapacityCustomRepositoryImpl implements UserCapacityCustomRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public UserCapacity updateUserCapacity(UserCapacity userCapacity) {
        Query query = new Query(Criteria.where("_id").is(userCapacity.getUserCapacityId()));

        Update update = new Update().set("excellence", userCapacity.getExcellence());

        return mongoTemplate.findAndModify(query, update, UserCapacity.class);
    }
}
