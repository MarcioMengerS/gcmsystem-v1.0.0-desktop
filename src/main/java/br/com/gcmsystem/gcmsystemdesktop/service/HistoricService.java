package br.com.gcmsystem.gcmsystemdesktop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.gcmsystem.gcmsystemdesktop.model.RegisterModel;
import br.com.gcmsystem.gcmsystemdesktop.model.Historic;
import br.com.gcmsystem.gcmsystemdesktop.repository.HistoricRepository;

@Service
public class HistoricService {
    @Autowired
    private HistoricRepository historicRepository;

    public void createHistoric(RegisterModel register) {
        Historic historic = new Historic();
        historic.setDate(register.getDate());
        historic.setTime(register.getTime());
        historic.setStatus(register.getStatus());
        historic.setNote(register.getNote());
        //Salvando informações individualizadas do GCM
        historic.setGcmId(register.getGcm().getId());
        historic.setGcmNumber(register.getGcm().getNumber());
        historic.setGcmName(register.getGcm().getName());
        historic.setGcmEmail(register.getGcm().getEmail());
        historic.setGcmTag(register.getGcm().getTag());
        //Salvando informações individualizadas do Equipamento
        historic.setEquipId(register.getEquipment().getId());
        historic.setEquipModel(register.getEquipment().getModel());
        historic.setEquipBrand(register.getEquipment().getBrand());
        historic.setEquipCategory(register.getEquipment().getCategory());
        historic.setEquipRegistrationNumber(register.getEquipment().getRegistrationNumber());
        historic.setEquipPlate(register.getEquipment().getPlate());

        historicRepository.save(historic);
    }
}