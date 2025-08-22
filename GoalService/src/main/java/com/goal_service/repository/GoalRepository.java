package com.goal_service.repository;

import com.goal_service.entity.Goal;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GoalRepository extends MongoRepository<Goal, String> {

    List<Goal> findByUserId(String userId);

}
