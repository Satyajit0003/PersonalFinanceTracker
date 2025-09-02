package com.transaction_service.service;

import com.common_library.enums.TransactionStatus;
import com.transaction_service.dto.TransactionDto;
import com.transaction_service.entity.Transaction;

import java.util.List;

public interface TransactionService {

    String createTransaction(TransactionDto transactionDto);

    void updateTransactionStatus(String transactionId, TransactionStatus status);

    void deleteTransaction(String transactionId);

    Transaction getTransactionById(String transactionId);

    List<Transaction> getAllTransactions();

    List<Transaction> getTransactionsByUserId(String userId);

    List<Transaction> getTransactionsByAccountId(String accountId);

    List<Transaction> getTransactionsByDate(String userId,String date);

    List<Transaction> getTransactionsByCategory(String userId,String category);


}
