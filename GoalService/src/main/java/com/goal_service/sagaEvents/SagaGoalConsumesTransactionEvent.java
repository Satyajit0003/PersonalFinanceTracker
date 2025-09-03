package com.goal_service.sagaEvents;

import com.common_library.dto.EmailDto;
import com.common_library.entity.User;
import com.common_library.enums.TransactionStatus;
import com.common_library.event.GoalEvent;
import com.goal_service.entity.Goal;
import com.goal_service.feignService.UserService;
import com.goal_service.kafka.GoalKafkaProducer;
import com.goal_service.repository.GoalRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class SagaGoalConsumesTransactionEvent {

    private final GoalRepository goalRepository;
    private final GoalKafkaProducer goalKafkaProducer;
    private final UserService userService;

    public SagaGoalConsumesTransactionEvent(GoalRepository goalRepository, GoalKafkaProducer goalKafkaProducer, UserService userService) {
        this.goalRepository = goalRepository;
        this.goalKafkaProducer = goalKafkaProducer;
        this.userService = userService;
    }

    @KafkaListener(topics = "goal-complete-event", groupId = "group-1")
    @CachePut(value = "goal", key = "#event.goalId")
    public void consume(GoalEvent event) {
        Goal goal = goalRepository.findById(event.getGoalId()).orElse(null);
        if(event.getStatus().equals(TransactionStatus.SUCCESS)) {
            goal.setCurrentAmount(goal.getCurrentAmount() + event.getAmount());
            if(goal.getCurrentAmount() == goal.getTargetAmount()) {
                User user = userService.singleUser(goal.getUserId()).orElse(null);
                String email = "";
                if(user != null) {
                    email = user.getEmail();
                }
                EmailDto emailDto  = new EmailDto(
                        email,
                        "Goal Achieved",
                        "Congratulations! You have achieved your goal: " + goal.getGoalName()
                );
                goalKafkaProducer.produceGoalNotification(emailDto);
            }
            goalRepository.save(goal);

        }
    }
}
