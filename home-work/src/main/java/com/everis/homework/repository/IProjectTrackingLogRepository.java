package com.everis.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.everis.homework.mapper.ProjectTrackingLog;

public interface IProjectTrackingLogRepository extends JpaRepository<ProjectTrackingLog, Integer> {

}
