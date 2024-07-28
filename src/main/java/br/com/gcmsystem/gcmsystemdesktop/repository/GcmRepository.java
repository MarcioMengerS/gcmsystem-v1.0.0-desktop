package br.com.gcmsystem.gcmsystemdesktop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.gcmsystem.gcmsystemdesktop.enums.StatusEnum;
import br.com.gcmsystem.gcmsystemdesktop.enums.UnitEnum;
import br.com.gcmsystem.gcmsystemdesktop.model.GcmModel;

public interface GcmRepository extends JpaRepository<GcmModel, Integer>{

    GcmModel findByNumber(Short num);
    List<GcmModel> findByStatus(StatusEnum status);
    List<GcmModel> findByUnit(UnitEnum unit);
}
