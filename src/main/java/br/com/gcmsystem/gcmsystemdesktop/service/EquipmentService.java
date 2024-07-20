package br.com.gcmsystem.gcmsystemdesktop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.gcmsystem.gcmsystemdesktop.enums.CategoryEnum;
import br.com.gcmsystem.gcmsystemdesktop.model.EquipmentModel;
import br.com.gcmsystem.gcmsystemdesktop.repository.EquipmentRepository;

@Service
public class EquipmentService {
    
    @Autowired
    private EquipmentRepository equipmentRepository;

    public void save(EquipmentModel equ) {
        equipmentRepository.save(equ);
    }

    public List<EquipmentModel> findAll() {
        return equipmentRepository.findAll();
    }

    public EquipmentModel findById(Integer id){
        return equipmentRepository.findById(id).get();//tratar exceção obj. nulo
    }

    public void deleteById(Integer id){
        equipmentRepository.delete(equipmentRepository.findById(id).orElseThrow());
    }

    public List<EquipmentModel> findByCategory(CategoryEnum category){
        return equipmentRepository.findByCategory(category);
    }

    public EquipmentModel findBySerie(String serie){
        return equipmentRepository.findBySerie(serie).orElseThrow();
    }

    public EquipmentModel findByRegistrationNumber(Integer rNum){
        return equipmentRepository.findByRegistrationNumber(rNum);//tratar exceçao obj. nulo
    }

    public EquipmentModel findByPrefix(Integer prefix){
        return equipmentRepository.findByPrefix(prefix);//tratar exceçao obj. nulo
    }
}