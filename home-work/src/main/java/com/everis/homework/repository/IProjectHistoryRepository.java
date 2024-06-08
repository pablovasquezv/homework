package com.everis.homework.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.everis.homework.mapper.ProjectHistory;

public interface IProjectHistoryRepository extends JpaRepository<ProjectHistory, Integer> {

	@Query("select ph FROM ProjectHistory ph WHERE ph.projectCode = ?1")
	Optional<List<ProjectHistory>> findByProjectCode(String projectId);

}
