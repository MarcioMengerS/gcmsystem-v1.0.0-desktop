package br.com.gcmsystem.gcmsystemdesktop.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import br.com.gcmsystem.gcmsystemdesktop.enums.CategoryEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
//Classe responsavel pelo armazenamento do histórico de cautelas
@Entity
@Data
@AllArgsConstructor
@Table(name = "historic")
public class Historic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private LocalDate date;
    private LocalTime time;
    private String status;//emprestado ou devolvido
    private String note;
    //Atributos GCM
    private Integer gcmId;
    private Short gcmNumber;
    private String gcmName;
    private String gcmEmail;
    private String gcmTag;
   
    //Atributos Equipamento
    private Integer equipId;
    private String equipModel;
    private String equipBrand;
    private CategoryEnum equipCategory;
    private Integer equipRegistrationNumber;
    private String serie;
    private Integer prefix;
    private String equipPlate;


    public Historic(){
        this.time = LocalTime.now(ZoneId.of("America/Sao_Paulo")).withNano(0);
        this.date = LocalDate.now();
    }
}
