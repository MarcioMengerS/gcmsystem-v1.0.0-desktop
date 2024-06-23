package br.com.gcmsystem.gcmsystemdesktop.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
@Table(name = "register")
public class RegisterModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private LocalDate date;
    private LocalTime time;
    private String status; //EMPRESTADO ou DEVOLVIDO
    private String note;  //observações

    @ManyToOne
    @JoinColumn(name = "gcm_id")
    private GcmModel gcm;

    @ManyToOne
    @JoinColumn(name = "equipment_id")
    private EquipmentModel equipment;

    public RegisterModel(){
        this.time = LocalTime.now(ZoneId.of("America/Sao_Paulo")).withNano(0);
        this.date = LocalDate.now();
    }
}
