package br.com.gcmsystem.gcmsystemdesktop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.gcmsystem.gcmsystemdesktop.model.Historic;

public interface HistoricRepository extends JpaRepository<Historic, Integer>{
    List<Historic> findByGcmName(String gcmName);
}
