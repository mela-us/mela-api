package com.hcmus.mela.user.repository;

import com.hcmus.mela.user.model.User;
import com.hcmus.mela.user.model.UserRole;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends MongoRepository<User, UUID> {

    Optional<User> findByUserId(UUID userId);

    boolean existsByUsername(String username);

    @Query("{ 'levelId' : ?0 }")
    @Update("{ '$set' : { 'levelId' : ?1 } }")
    void updateAllByLevelId(UUID levelId, UUID newLevelId);

    List<User> findAllByUserRole(UserRole userRole);

    List<User> findAllByUserRoleAndLevelId(UserRole userRole, UUID levelId);

    int countByCreatedAtBetween(Date start, Date end);

    @Aggregation(pipeline = {
            "{ $match: { 'createdAt' : { $gte : ?0, $lt : ?1 } } }",
            "{ $group: { _id: { year: { $year: '$createdAt' }, month: { $month: '$createdAt' } }, count: { $sum: 1 } } }",
            "{ $project: { year: '$_id.year', month: '$_id.month', count: 1, _id: 0 } }"
    })
    List<MonthlyCount> countByMonthInPeriod(Date start, Date end);

    interface MonthlyCount {
        int getYear();

        int getMonth();

        int getCount();
    }
}
