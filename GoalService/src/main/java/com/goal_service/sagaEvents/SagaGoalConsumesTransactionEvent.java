package com.goal_service.sagaEvents;

import com.common_library.enums.TransactionStatus;
import com.common_library.event.GoalEvent;
import com.goal_service.entity.Goal;
import com.goal_service.repository.GoalRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class SagaGoalConsumesTransactionEvent {

    private final GoalRepository goalRepository;

    public SagaGoalConsumesTransactionEvent(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    @KafkaListener(topics = "goal-complete-event", groupId = "group-1")
    public void consume(GoalEvent event) {
        Goal goal = goalRepository.findById(event.getGoalId()).orElse(null);
        if(event.getStatus().equals(TransactionStatus.SUCCESS)) {
            goal.setCurrentAmount(goal.getCurrentAmount() + event.getAmount());
            goalRepository.save(goal);
        }
    }
}
