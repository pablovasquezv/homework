package com.everis.homework.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.everis.homework.mapper.Homework;

/**
 * @author gsaravia
 *
 */
public interface IHomeworkRepository extends JpaRepository<Homework, Integer> {

	@Query("select hk from Homework hk where hk.userName=?1")
	Optional<Homework> findByUser(String username);

}