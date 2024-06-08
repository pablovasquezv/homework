package com.everis.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.everis.homework.mapper.ContigencyDocument;

public interface IContigencyDocRepository  extends JpaRepository<ContigencyDocument, String> {

}
