package br.com.gcmsystem.gcmsystemdesktop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.gcmsystem.gcmsystemdesktop.enums.CategoryEnum;
import br.com.gcmsystem.gcmsystemdesktop.enums.LocationEnum;
import br.com.gcmsystem.gcmsystemdesktop.model.EquipmentModel;

public interface EquipmentRepository extends JpaRepository<EquipmentModel, Integer>{

    EquipmentModel findByRegistrationNumber(Integer numRg);
    List<EquipmentModel> findByCategory(CategoryEnum category);
    Optional<EquipmentModel> findBySerie(String serie);
    EquipmentModel findByPrefix(Integer prefix);
    List<EquipmentModel> findByLocation(LocationEnum location);   
}