package com.example.remittance.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.remittance.common.model.entity.TransactionFee;

/**
 * 거래-수수료 repository
 * @author evan.m.kim
 */

@Repository
public interface TransactionFeeRepository extends JpaRepository<TransactionFee, Long> {
}
