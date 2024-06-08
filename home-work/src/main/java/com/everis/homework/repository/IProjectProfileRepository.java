package com.everis.homework.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import com.everis.homework.mapper.ProjectProfile;
import com.everis.homework.mapper.ProjectTracking;

public interface IProjectProfileRepository extends JpaRepository<ProjectProfile, Integer>{

	@Transactional
	@Modifying
	@Query("delete FROM ProjectProfile pp WHERE pp.projectCode = ?1")
	void deleteByProjectCode(String projectCode);
	
    @Query("SELECT pp FROM ProjectProfile pp WHERE pp.projectCode = ?1")
	Optional<ProjectTracking> findById(String projectCode);
}
