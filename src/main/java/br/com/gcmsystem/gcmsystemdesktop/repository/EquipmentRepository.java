package br.com.gcmsystem.gcmsystemdesktop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.gcmsystem.gcmsystemdesktop.enums.CategoryEnum;
import br.com.gcmsystem.gcmsystemdesktop.model.EquipmentModel;

public interface EquipmentRepository extends JpaRepository<EquipmentModel, Integer>{

    EquipmentModel findByRegistrationNumber(Integer numRg);
    List<EquipmentModel> findByCategory(CategoryEnum category);   
}