package br.com.gcmsystem.gcmsystemdesktop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.gcmsystem.gcmsystemdesktop.enums.StatusEnum;
import br.com.gcmsystem.gcmsystemdesktop.enums.UnitEnum;
import br.com.gcmsystem.gcmsystemdesktop.model.GcmModel;
import br.com.gcmsystem.gcmsystemdesktop.repository.GcmRepository;

@Service
public class GcmService {
        
    @Autowired
    private GcmRepository gcmRepository;

    public void save(GcmModel gm) {
        gm.setTransactionPass(new BCryptPasswordEncoder().encode(gm.getTransactionPass()));
        gcmRepository.save(gm);
    }

    public List<GcmModel> findAll() {
        return gcmRepository.findAll();
    }

    public GcmModel findById(Integer id){
        return gcmRepository.findById(id).get();//tratar exceção
    }

    public void deleteById(Integer id){
        gcmRepository.deleteById(id);
    }

    public GcmModel findByNumber(short i){//tratar exceção
        return gcmRepository.findByNumber(i);
    }

    public List<GcmModel> findByStatus(StatusEnum status) {
        return gcmRepository.findByStatus(status); 
    }

    public List<GcmModel> findByUnit(UnitEnum unit) {
        return gcmRepository.findByUnit(unit);
    }
}
