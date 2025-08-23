package com.report_service.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.report_service.FeignClient.UserService;
import com.report_service.email.EmailEvent;
import com.report_service.entity.Goal;
import com.report_service.entity.Transaction;
import com.report_service.entity.User;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    private final UserService userService;
    private final Client client;

    public ReportService(UserService userService, Client client) {
        this.userService = userService;
        this.client = client;
    }

    @Scheduled(cron = "0 0 9 L * ?")
    public void generateReport() {
        List<User> users = userService.allUsers();
        StringBuilder reports = new StringBuilder();

        for (User user : users) {
            String prompt = generatePrompt(user);

            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash",
                    prompt,
                    null
            );

            if (response != null && response.text() != null) {
                reports.append("Report for user: ").append(user.getUserName()).append("\n")
                        .append(response.text()).append("\n\n");
            }

            EmailEvent event = new EmailEvent(
                    user.getEmail(),
                    "Your Monthly Financial Report",
                    "Dear " + user.getUserName() + ",\n\n" +
                            "Here is your financial report for this month:\n\n" +
                            (response != null ? response.text() : "No report generated.") +
                            "\n\nBest regards,\nFinance App Team"
            );

        }
    }


    public String generatePrompt(User user) {
        List<Transaction> transactions = user.getTransactions();
        List<Goal> goals = user.getGoals();

        String prompt = "Here is the transactions list: " + transactions +
                ". And here is the goals list: " + goals +
                ". Analyze both and generate a report with: " +
                "1) Income vs. expenditure summary, " +
                "2) Spending insights, " +
                "3) Goal progress, " +
                "4) Personalized saving tips.";

        return prompt;
    }
}
