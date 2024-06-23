package br.com.gcmsystem.gcmsystemdesktop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.gcmsystem.gcmsystemdesktop.model.GcmModel;

public interface GcmRepository extends JpaRepository<GcmModel, Integer>{

    GcmModel findByNumber(Short num);
}
