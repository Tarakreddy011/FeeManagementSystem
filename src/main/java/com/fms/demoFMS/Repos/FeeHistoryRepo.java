package com.fms.demoFMS.Repos;

import com.fms.demoFMS.Entity.FeeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeeHistoryRepo extends JpaRepository<FeeHistory, Integer> {
}
