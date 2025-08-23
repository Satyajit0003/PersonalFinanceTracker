package com.user_service.feignService;

import com.user_service.entity.Goal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "GOAL-SERVICE")
public interface GoalService {

    @GetMapping("/goal/user-goals/{userId}")
    Optional<List<Goal>> getGoalsByUserId(@PathVariable String userId);
}
