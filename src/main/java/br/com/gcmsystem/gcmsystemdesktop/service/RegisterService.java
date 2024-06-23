package br.com.gcmsystem.gcmsystemdesktop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.gcmsystem.gcmsystemdesktop.model.RegisterModel;
import br.com.gcmsystem.gcmsystemdesktop.repository.RegisterRepository;

@Service
public class RegisterService {
    
    @Autowired
    private RegisterRepository registerRepository;

    public void save(RegisterModel registerModel){
        registerRepository.save(registerModel);
    }

    public Optional<RegisterModel> findById(Integer id) {
        return registerRepository.findById(id);
    }

    public List<RegisterModel> findAll() {
        return registerRepository.findAll();
    }
}
