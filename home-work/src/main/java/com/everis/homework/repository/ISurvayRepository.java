package com.everis.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.everis.homework.mapper.Survay;

public interface ISurvayRepository extends JpaRepository<Survay, String> {

}
