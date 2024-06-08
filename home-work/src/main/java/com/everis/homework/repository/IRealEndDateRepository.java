package com.everis.homework.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.everis.homework.mapper.RealEndDateLog;

public interface IRealEndDateRepository extends JpaRepository<RealEndDateLog, Integer> {

	@Query("select re FROM RealEndDateLog re WHERE re.projectCode = ?1")
	Optional<List<RealEndDateLog>> findByProjectCode(String projectCode);
}
