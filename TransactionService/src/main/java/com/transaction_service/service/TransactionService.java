package com.transaction_service.service;

import com.transaction_service.dto.TransactionDto;
import com.transaction_service.entity.Transaction;

import java.util.List;

public interface TransactionService {

    Transaction createTransaction(TransactionDto transactionDto);

    Transaction updateTransaction(TransactionDto transactionDto, String transactionId);

    void deleteTransaction(String transactionId);

    Transaction getTransactionById(String transactionId);

    List<Transaction> getAllTransactions();

    List<Transaction> getTransactionsByUserId(String userId);

    List<Transaction> getTransactionsByAccountId(String accountId);

    List<Transaction> getTransactionsByDate(String userId,String date);

    List<Transaction> getTransactionsByCategory(String userId,String category);

    List<Transaction> getTransactionsByType(String userId, String type);

}
