package com.everis.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.everis.homework.mapper.Funding;

public interface IFundingRepository extends JpaRepository<Funding, String> {

}
