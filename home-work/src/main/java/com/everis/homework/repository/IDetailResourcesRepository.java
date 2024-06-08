package com.everis.homework.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.everis.homework.mapper.DetailKeysResources;

/**
 * @author hvergarc
 *
 */
public interface IDetailResourcesRepository extends JpaRepository<DetailKeysResources, Integer> {

	@Transactional
	@Modifying
	@Query("delete FROM DetailKeysResources dr WHERE dr.projectCode = ?1")
	void deleteByProjectCode(String projectCode);
	
	
	@Query("select dr FROM DetailKeysResources dr WHERE dr.projectCode = ?1")
	Optional<List<DetailKeysResources>> findByProjectCode(String projectCode);
}