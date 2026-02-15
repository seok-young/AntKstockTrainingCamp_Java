package com.example.AntKstockCamp.repository;

import com.example.AntKstockCamp.domain.Entity.AccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountHistoryRepository extends JpaRepository<AccountHistory, Long> {
}
