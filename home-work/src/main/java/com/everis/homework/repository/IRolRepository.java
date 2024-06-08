package com.everis.homework.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.everis.homework.mapper.Rol;

/**
 * 
 * @author sgutierc
 *
 */
public interface IRolRepository extends JpaRepository<Rol, String> {

	@Query("SELECT rc FROM Rol rc WHERE rc.domain=?1")
	Optional<List<Rol>> findByDomain(String domain);

}
