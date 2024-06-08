package com.everis.homework.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.everis.homework.mapper.HomeworkLog;

/**
 * The Interface IHomeworkLogRepository.
 */
public interface IHomeworkLogRepository extends JpaRepository<HomeworkLog, Integer>{

}