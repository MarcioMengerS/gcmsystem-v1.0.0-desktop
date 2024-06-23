package br.com.gcmsystem.gcmsystemdesktop.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import br.com.gcmsystem.gcmsystemdesktop.model.RegisterModel;

public interface RegisterRepository extends JpaRepository<RegisterModel, Integer>{
    
}
