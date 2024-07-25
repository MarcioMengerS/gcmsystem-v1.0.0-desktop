package br.com.gcmsystem.gcmsystemdesktop.model;

import java.time.LocalDate;
import java.util.List;

import br.com.gcmsystem.gcmsystemdesktop.enums.CategoryEnum;
import br.com.gcmsystem.gcmsystemdesktop.enums.LocationEnum;
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
    private CategoryEnum category; //categoria
    private LocationEnum location;//local do equipamento
    private String brand;//marca
    private String model;//modelo
    private String serie;//nºsérie
    private Integer registrationNumber; //patrimônio
    private String moreInform; //mais informações
    //Veiculos
    private Integer prefix; //prefixo
    private String plate; //placa veículo
    //Coletes
    private String size; //tamanho - Transformar em ENUM
    private LocalDate expiratioDate;//validade
    //Armas
    private String type; //tipo - Transformar em ENUM
    private String caliber;//calibre
    private Integer register; //registro
    private String sinarm; //sinarm
    //Radiocomunicador
    private Integer identifier; //identificador

    @OneToMany(mappedBy = "equipment")
    private List<RegisterModel> registrations;
}
