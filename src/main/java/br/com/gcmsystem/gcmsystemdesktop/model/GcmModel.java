package br.com.gcmsystem.gcmsystemdesktop.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import br.com.gcmsystem.gcmsystemdesktop.enums.StatusEnum;
import br.com.gcmsystem.gcmsystemdesktop.enums.UnitEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "gcm")
public class GcmModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //pessoal
    private String name;
    private String gender;
    private String email;
    private String phone;
    private String cpf;
    private String rg;
    private LocalDate birth;
    private String catCnh;
    private LocalDate validityCnh;
    private String blood;
    //profissional
    private StatusEnum status;
    private UnitEnum unit;//local de trabalho
    private LocalDateTime createdAt; //grava data de criação do objeto GCM
    private LocalDateTime modifyAt; //grava data da última modificação do objeto GCM
    private Short number;
    private String sutache; //nome de guerra
    private String funcionalEmail;
    private LocalDate admissionDate;//Data de admissão na PMPA
    private String matriculation; //matrícula
    private String transactionPass; //senha para transação
    private String tag;
    
    @OneToMany(mappedBy = "gcm")
    private List<RegisterModel> registrations;

}
