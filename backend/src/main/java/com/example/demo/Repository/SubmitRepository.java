package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Model.Submit;

@Repository
public interface SubmitRepository extends JpaRepository<Submit, Long> {
    List<Submit> findByApprovalStatus(String status);
    Submit findByUserIdAndBugId(Long userId, Long bugId); // Add this method
}
