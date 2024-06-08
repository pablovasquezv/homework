package com.everis.homework.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.everis.homework.mapper.Profile;

public interface IProfileRepository extends JpaRepository<Profile, String> {

	
    @Query("SELECT pf FROM Profile pf WHERE pf.user = ?1")
	Optional<Profile> findById(String username);
    
    
}
