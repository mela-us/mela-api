package com.hcmus.mela.skills.repository;

import com.hcmus.mela.skills.model.UserSkill;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class UserSkillCustomRepositoryImpl implements UserSkillCustomRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public UserSkill updateUserSkill(UserSkill userSkill) {
        Query query = new Query(Criteria.where("_id").is(userSkill.getUserSkillId()));

        Update update = new Update().set("points", userSkill.getPoints());

        return mongoTemplate.findAndModify(query, update, UserSkill.class);
    }
}
