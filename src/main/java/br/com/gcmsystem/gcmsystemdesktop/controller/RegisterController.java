package br.com.gcmsystem.gcmsystemdesktop.controller;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.gcmsystem.gcmsystemdesktop.enums.CategoryEnum;
import br.com.gcmsystem.gcmsystemdesktop.model.EquipmentModel;
import br.com.gcmsystem.gcmsystemdesktop.model.GcmModel;
import br.com.gcmsystem.gcmsystemdesktop.model.RegisterModel;
import br.com.gcmsystem.gcmsystemdesktop.service.EquipmentService;
import br.com.gcmsystem.gcmsystemdesktop.service.GcmService;
import br.com.gcmsystem.gcmsystemdesktop.service.RegisterService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

@Component
public class RegisterController implements Initializable{    

    @Autowired
    private GcmService gcmService;
    @Autowired
    private EquipmentService equipmentService;
    @Autowired
    private RegisterService registerService;

    @FXML
    private TextArea noteText;
    @FXML
    private ComboBox<CategoryEnum> equipmCatComboBox;
    @FXML
    private ComboBox<String> gcmComboBox, equipmNumComboBox;
    @FXML
    private TableView<RegisterModel> registerTableView;
    @FXML
    private TableColumn<RegisterModel, String>statusColumn, timeColumn, noteColumn;
    @FXML
    private TableColumn<RegisterModel, LocalDate> dateColumn;

    @FXML
    private TableColumn<RegisterModel, String>
        gcmNumberColumn, gcmNameColumn, gcmEmailColumn,gcmTagColumn,
        equipmentPlateColumn,equipmentPatrColumn,equipmentBrandColumn, equipmentModelColumn, equipmentCategoryColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gcmComboBox.getItems().addAll(listAllGcm());
        gcmComboBox.setOnAction(event->{
            gcmComboBox.getSelectionModel().getSelectedItem();//seleciona GCM desejado
        });

        equipmNumComboBox.setOnAction(event->{
            equipmNumComboBox.getSelectionModel().getSelectedItem();//seleciona equipamento desejado
        });

        equipmCatComboBox.getItems().setAll(CategoryEnum.values());
 
        list();
    }

    @FXML
    public void loan(){
        RegisterModel registerModel = new RegisterModel();
        registerModel.setStatus("Emprestado");
        registerModel.setNote(noteText.getText());

        String numberGcm = gcmComboBox.getSelectionModel().getSelectedItem();
        GcmModel gcmModel = gcmService.findByNumber(Short.parseShort(numberGcm));
        registerModel.setGcm(gcmModel);

        EquipmentModel equipmentModel = new EquipmentModel();
        String equipmentNum = equipmNumComboBox.getSelectionModel().getSelectedItem();
        Integer i = Integer.valueOf(equipmentNum);
        equipmentModel = equipmentService.findByRegistrationNumber(i);
        registerModel.setEquipment(equipmentModel);

        registerService.save(registerModel);
        list();
    }
    
    @FXML
    public void findById(){
        Optional<RegisterModel> register = registerService.findById(4);

        System.out.println(register.get().getEquipment().getBrand());
    }

    public void list(){
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        noteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));//coluna observação

        //Formatar data
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        dateColumn.setCellFactory(new Callback<TableColumn<RegisterModel,LocalDate>,TableCell<RegisterModel,LocalDate>>() {

            @Override
            public TableCell<RegisterModel, LocalDate> call(TableColumn<RegisterModel, LocalDate> param) {
                return new TableCell<RegisterModel, LocalDate>() {
                    @Override
                    protected void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(formatter.format(item));
                        }
                    }
                };
            }
        });

        // Insere coluna nome em GCM
        gcmNameColumn.setCellValueFactory(cellData -> {
            GcmModel gcm = cellData.getValue().getGcm();
            if (gcm != null) {
                return new SimpleStringProperty(gcm.getName());
            } else {
                return new SimpleStringProperty("");
            }
        });
        //Insere coluna email em GCM
        gcmEmailColumn.setCellValueFactory(cellData -> {
            GcmModel gcm = cellData.getValue().getGcm();
            if (gcm != null) {
                return new SimpleStringProperty(gcm.getEmail());
            } else {
                return new SimpleStringProperty("");
            }
        });
        //Insere coluna tag em GCM
        gcmTagColumn.setCellValueFactory(cellData -> {
            GcmModel gcm = cellData.getValue().getGcm();
            if (gcm != null) {
                return new SimpleStringProperty(gcm.getTag());
            } else {
                return new SimpleStringProperty("");
            }
        });
        //Insere coluna Patrimônio em equipamento
        equipmentPatrColumn.setCellValueFactory(cellData -> {
            EquipmentModel equipment = cellData.getValue().getEquipment();
            if (equipment != null && equipment.getRegistrationNumber() != null) {
                return new SimpleStringProperty(String.valueOf(equipment.getRegistrationNumber()));
            } else {
                return new SimpleStringProperty("");
            }
        });
        //Insere coluna categoria em equipamento
        equipmentCategoryColumn.setCellValueFactory(cellData -> {
            EquipmentModel equipment = cellData.getValue().getEquipment();
            if (equipment != null && equipment.getCategory() != null) {
                return new SimpleStringProperty(equipment.getCategory().name());
            } else {
                return new SimpleStringProperty("");
            }
        });
        //Insere coluna Marca em equipamento
        equipmentBrandColumn.setCellValueFactory(cellData -> {
            EquipmentModel equipment = cellData.getValue().getEquipment();
            if (equipment != null) {
                return new SimpleStringProperty(equipment.getBrand());
            } else {
                return new SimpleStringProperty("");
            }
        });
        //Insere coluna Modelo em equipamento
        equipmentModelColumn.setCellValueFactory(cellData -> {
            EquipmentModel equipment = cellData.getValue().getEquipment();
            if (equipment != null) {
                return new SimpleStringProperty(equipment.getModel());
            } else {
                return new SimpleStringProperty("");
            }
        });
        registerTableView.setItems(FXCollections.observableArrayList(registerService.findAll()));
    }

    public List<String> listAllGcm(){
        List<GcmModel> gcms = gcmService.findAll();
        ArrayList<String> val = new ArrayList<>();
        gcms.forEach((gm)->{
            // numberGcms.add(gm.getNumber());
            val.add(String.valueOf(gm.getNumber()));
        });
        return val;
    }

    // public List<String> listAllNumRegEquip(){
    //     ArrayList<String> numReg = new ArrayList<String>();
    //     List<EquipmentModel>equipments = equipmentService.findAll();
    //     equipments.forEach((eq)-> numReg.add(eq.getRegistrationNumber().toString()));
    //     return numReg;
    // }

    //Retorna lista de nºs de registro a partir da categoria selecionada no combobox Equipamento/categoria
    //Apresenta resultado no combobox Patrimônio/Série
    @FXML
    public void searchByCategory(){
        equipmNumComboBox.getItems().clear();
        CategoryEnum category = equipmCatComboBox.getSelectionModel().getSelectedItem();
        ArrayList<String> values = new ArrayList<>();
        List<EquipmentModel> catList = equipmentService.findByCategory(category);
        catList.forEach((cat)->{
            values.add(cat.getRegistrationNumber().toString());
        });
        equipmNumComboBox.getItems().addAll(values);
        // return values;
    }
}
