package com.report_service.service;

import com.common_library.dto.EmailDto;
import com.common_library.entity.Account;
import com.common_library.entity.Goal;
import com.common_library.entity.Transaction;
import com.common_library.entity.User;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.report_service.FeignClient.UserService;
import com.report_service.kafka.ReportKafkaProducer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final UserService userService;
    private final Client client;
    private final ReportKafkaProducer reportKafkaProducer;

    public ReportService(UserService userService, Client client, ReportKafkaProducer reportKafkaProducer) {
        this.userService = userService;
        this.client = client;
        this.reportKafkaProducer = reportKafkaProducer;
    }

    @Scheduled(cron = "0 0 9 L * ?")
    //@Scheduled(cron = "0 * * * * ?")
    public void generateReport() {
        List<User> users = userService.allUsers();
        StringBuilder reports = new StringBuilder();

        for (User user : users) {
            if (user.getRole().equals("USER")) {
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

                EmailDto event = new EmailDto(
                        user.getEmail(),
                        "Your Monthly Financial Report",
                        "Dear " + user.getUserName() + ",\n\n" +
                                "Here is your financial report for this month:\n\n" +
                                (response != null ? response.text() : "No report generated.") +
                                "\n\nBest regards,\nFinance App Team"
                );

                reportKafkaProducer.produceReportNotification(event);
            }

        }
    }

    public String generatePrompt(User user) {
        StringBuilder report = new StringBuilder();

        report.append("===== Financial Report for ").append(user.getUserName()).append(" =====%n".formatted());

        // Accounts Section
        report.append("%n-- Accounts --%n");
        double totalBalance = 0.0;
        if (user.getAccounts() != null && !user.getAccounts().isEmpty()) {
            for (Account acc : user.getAccounts()) {
                totalBalance += acc.getBalance();
                report.append("Account ID: ").append(acc.getAccountId())
                        .append(", Type: ").append(acc.getAccountType())
                        .append(", Balance: ").append(acc.getBalance())
                        .append(", Created: ").append(acc.getCreateDate())
                        .append("%n");
            }
            report.append("Total Balance across accounts: ").append(totalBalance).append("%n");
        } else {
            report.append("No accounts found.%n");
        }

        // Transactions Section
        report.append("%n-- Transactions --%n");
        double totalExpenses = 0.0;
        double totalIncome = 0.0;
        Map<String, Double> categoryWise = new HashMap<>();

        if (user.getTransactions() != null && !user.getTransactions().isEmpty()) {
            for (Transaction tx : user.getTransactions()) {
                if ("EXPENSE".equalsIgnoreCase(tx.getTransactionType())) {
                    totalExpenses += tx.getAmount();
                } else if ("INCOME".equalsIgnoreCase(tx.getTransactionType())) {
                    totalIncome += tx.getAmount();
                }

                categoryWise.merge(tx.getCategory(), tx.getAmount(), Double::sum);

                report.append("[").append(tx.getDate()).append("] ")
                        .append(tx.getTransactionType()).append(" - ")
                        .append(tx.getAmount()).append(" (")
                        .append(tx.getCategory()).append(") ")
                        .append("Description: ").append(tx.getDescription())
                        .append("%n");
            }
            report.append("Total Income: ").append(totalIncome).append("%n");
            report.append("Total Expenses: ").append(totalExpenses).append("%n");
        } else {
            report.append("No transactions found.%n");
        }

        // Goals Section
        report.append("%n-- Goals --%n");
        if (user.getGoals() != null && !user.getGoals().isEmpty()) {
            for (Goal goal : user.getGoals()) {
                double progress = (goal.getTargetAmount() > 0)
                        ? (goal.getCurrentAmount() / goal.getTargetAmount()) * 100
                        : 0.0;
                report.append("Goal: ").append(goal.getGoalName())
                        .append(" | Target: ").append(goal.getTargetAmount())
                        .append(" | Current: ").append(goal.getCurrentAmount())
                        .append(" | Status: ").append(goal.getStatus())
                        .append(" | Progress: ").append(String.format("%.2f", progress)).append("%")
                        .append("%n");
            }
        } else {
            report.append("No goals set.%n");
        }

        // Savings Tips Section
        report.append("%n-- Saving Tips --%n");
        report.append(generateSavingTips(totalBalance, totalIncome, totalExpenses, categoryWise, user.getGoals()));

        return report.toString();
    }

    private String generateSavingTips(double totalBalance, double totalIncome, double totalExpenses, Map<String, Double> categoryWise, List<Goal> goals) {

        StringBuilder tips = new StringBuilder();

        // Expense vs Income
        if (totalIncome > 0 && totalExpenses > totalIncome * 0.7) {
            tips.append("⚠️ Your expenses are more than 70% of your income. Consider reducing discretionary spending.%n");
        }

        // High balance suggestion
        if (totalBalance > totalIncome * 3) {
            tips.append("💡 You have a healthy account balance. Consider investing part of it for better returns.%n");
        }

        // Top 1-2 expense categories
        if (!categoryWise.isEmpty()) {
            List<Map.Entry<String, Double>> sorted =
                    categoryWise.entrySet().stream()
                            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                            .limit(2)
                            .collect(Collectors.toList());
            for (Map.Entry<String, Double> entry : sorted) {
                tips.append("🔎 You spent a lot on ").append(entry.getKey())
                        .append(" (").append(entry.getValue()).append("). Try to cut back here.%n");
            }
        }

        // Goals progress
        if (goals != null && !goals.isEmpty()) {
            for (Goal goal : goals) {
                if (goal.getTargetAmount() > 0) {
                    double progress = (goal.getCurrentAmount() / goal.getTargetAmount()) * 100;
                    if (progress < 30) {
                        tips.append("📌 Your goal '").append(goal.getGoalName())
                                .append("' is below 30% progress. Consider allocating more savings to it.%n");
                    }
                }
            }
        }

        if (tips.length() == 0) {
            tips.append("✅ Your finances look balanced. Keep saving consistently!%n");
        }

        return tips.toString();
    }
}
