package com.example.remittance.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.remittance.common.model.entity.CodeBank;

/**
 * 코드-은행 repository
 * @author evan.m.kim
 */
@Repository
public interface CodeBankRepository extends JpaRepository<CodeBank, String> {
}
