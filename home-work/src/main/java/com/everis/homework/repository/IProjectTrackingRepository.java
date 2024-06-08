package com.everis.homework.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.everis.homework.mapper.ProjectTracking;

/**
 * The Interface IProjectTrackingRepository.
 */
public interface IProjectTrackingRepository extends JpaRepository<ProjectTracking, String> {

    /**
     * Exists by project code.
     *
     * @param projectCode the project code
     * @return true, if successful
     */
    @Query("SELECT CASE WHEN COUNT(pc) > 0 THEN 'true' ELSE 'false' END FROM ProjectTracking pc WHERE pc.projectCode = ?1")
	boolean existsByProjectCode(String projectCode);

    /**
     * Find by id.
     *
     * @param projectCode the project code
     * @return the optional
     */
    @Query("SELECT pc FROM ProjectTracking pc WHERE pc.projectCode = ?1")
	Optional<ProjectTracking> findById(String projectCode);
}