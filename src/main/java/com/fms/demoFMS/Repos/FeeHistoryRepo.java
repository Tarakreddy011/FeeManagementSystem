package com.fms.demoFMS.Repos;

import com.fms.demoFMS.Entity.FeeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeHistoryRepo extends JpaRepository<FeeHistory, Integer> {

    @Query("SELECT COALESCE(SUM(f.amount), 0.0) FROM FeeHistory f")
    Double getTotalFeesCollected();

    List<FeeHistory> findTop5ByOrderByFeepaymentIdDesc();

    List<FeeHistory> findByStudent_StudentidOrderByFeepaymentIdDesc(Integer studentid);
}
