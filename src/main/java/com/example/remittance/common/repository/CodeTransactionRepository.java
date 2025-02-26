package com.example.remittance.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.remittance.common.model.entity.CodeTransaction;

/**
 * 코드-거래 유형 repository
 * @author evan.m.kim
 */

@Repository
public interface CodeTransactionRepository extends JpaRepository<CodeTransaction, String> {
}
