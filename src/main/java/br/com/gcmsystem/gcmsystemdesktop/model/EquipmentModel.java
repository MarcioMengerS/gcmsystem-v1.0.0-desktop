package br.com.gcmsystem.gcmsystemdesktop.model;

import java.util.List;

import br.com.gcmsystem.gcmsystemdesktop.enums.CategoryEnum;
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
@Table(name = "equipment")
public class EquipmentModel {
        
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String model;
    private String brand;
    // private String category;
    private CategoryEnum category;
    private Integer registrationNumber; //nºsérie, prefixo, patrimônio
    private String plate; //Placa Veículo

    @OneToMany(mappedBy = "equipment")
    private List<RegisterModel> registrations;
}
