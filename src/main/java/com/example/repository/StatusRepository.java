package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.Status;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {

	@Modifying
	@Transactional
	@Query("update Status s set s.success_x2j = ?1, s.fail_x2j = ?2, s.success_j2x = ?3, s.fail_j2x = ?4 where s.seq = 1")
	public void updateStatusValue(int success_x2j, int fail_x2j, int successj2x, int fail_j2x);
	
}
