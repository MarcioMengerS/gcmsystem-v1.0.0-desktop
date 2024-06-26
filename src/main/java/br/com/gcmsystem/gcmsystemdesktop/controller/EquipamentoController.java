package br.com.gcmsystem.gcmsystemdesktop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.gcmsystem.gcmsystemdesktop.enums.CategoryEnum;
import br.com.gcmsystem.gcmsystemdesktop.model.EquipmentModel;
import br.com.gcmsystem.gcmsystemdesktop.service.EquipmentService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

@Component
public class EquipamentoController {
    private Integer id;
    private EquipmentModel item;

    @Autowired
    private EquipmentService equipmentService;
    
    @FXML
    ComboBox<CategoryEnum> categoryComboBox = new ComboBox<>();
    @FXML
    private TextField idField, modelField, brandField, numField, plateField;
    @FXML
    private Button saveButton, updateButton, deleteButton, clearButton;
    @FXML
    private Label lblTotal, lblWarning;
    @FXML
    private TableView<EquipmentModel> equipmentTableView;
    @FXML
    private TableColumn<EquipmentModel, Integer> idColumn, registerColumn;
    @FXML
    private TableColumn<EquipmentModel, String> modelColumn, brandColumn, plateColumn; //categoryColumn;
    @FXML
    private TableColumn<EquipmentModel, CategoryEnum> categoryColumn;

    public void initialize(){
        // Método usado para carregar e exibir a lista de equipamentos
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id")); //propriedades do objeto EquipmentModel
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        registerColumn.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        plateColumn.setCellValueFactory(new PropertyValueFactory<>("plate"));

        list();
        //Preenchimento nos campos após seleção de itens na lista 
        equipmentTableView.getSelectionModel().selectedItemProperty()
            .addListener((obs, old, newValue)->{
                if(newValue !=null){
                    id = newValue.getId();
                    item = selectedItem();
                    idField.setText(String.valueOf(item.getId()));
                    modelField.setText(item.getModel());
                    brandField.setText(item.getBrand());
                    categoryComboBox.setValue(item.getCategory());
                    numField.setText(String.valueOf(item.getRegistrationNumber()));
                    plateField.setText(item.getPlate());

                    saveButton.setVisible(false);
                    clearButton.setVisible(true);
                    updateButton.setVisible(true);
                    deleteButton.setVisible(true);
                    lblWarning.setVisible(false);
                }
            });
        addButtonToTable();
        categoryComboBox.getItems().setAll(CategoryEnum.values());
    }
    //Adiciona botão na última coluna da lista
    private void addButtonToTable() {
        TableColumn<EquipmentModel, Void> actionColumn = new TableColumn<>();

        Callback<TableColumn<EquipmentModel, Void>, TableCell<EquipmentModel, Void>> cellFactory = new Callback<TableColumn<EquipmentModel, Void>, TableCell<EquipmentModel, Void>>() {
            @Override
            public TableCell<EquipmentModel, Void> call(TableColumn<EquipmentModel, Void> param) {
                final TableCell<EquipmentModel, Void> cell = new TableCell<EquipmentModel, Void>() {
                    private Button btn = new Button("Detalhes");
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            EquipmentModel data = getTableView().getItems().get(getIndex());
                            Alert alert = new Alert(AlertType.INFORMATION);
                            alert.setTitle("Info equipamento");
                            alert.setHeaderText("Informações complementares");
                            alert.setContentText("referencia ID = " + data.getId() );

                            alert.showAndWait(); 
                        });
                    }
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if( empty ) {
                            setGraphic(null);
                        } else {
                            EquipmentModel data = getTableView().getItems().get(getIndex());
                            btn.setText(btn.getText() + " " + data.getId() );
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
        actionColumn.setText("Ação");
        actionColumn.setCellFactory(cellFactory);
        equipmentTableView.getColumns().add(actionColumn);
    }

    public EquipmentModel selectedItem(){
        return equipmentService.findById(id);
    }

    public void list(){
        equipmentTableView.setItems(FXCollections.observableArrayList(equipmentService.findAll()));
        lblTotal.setText(String.valueOf(equipmentTableView.getItems().size()));
    }

    @FXML
    public void save(){
        if(!numField.getText().trim().isEmpty()){
            EquipmentModel equipmentModel = new EquipmentModel();
            equipmentModel.setBrand(brandField.getText());
            equipmentModel.setModel(modelField.getText());
            equipmentModel.setRegistrationNumber(Integer.parseInt(numField.getText()));
            equipmentModel.setPlate(plateField.getText());
            equipmentModel.setCategory(categoryComboBox.getSelectionModel().getSelectedItem());
            // equipmentModel.setCategory(categoryComboBox.getSelectionModel().getSelectedItem().toString());
            equipmentService.save(equipmentModel);
            list();
            clear();
        }else{
            lblWarning.setVisible(true);
        }
    }

    @FXML
    public void update(){
        item.setCategory(categoryComboBox.getValue());
        item.setModel(modelField.getText());
        item.setBrand(brandField.getText());
        item.setRegistrationNumber(Integer.parseInt(numField.getText()));
        item.setPlate(plateField.getText());

        equipmentService.save(item);
        list();
    }

    @FXML
    public void delete(){
        equipmentService.deleteById(id);
        clear();
        list();
    }

    //Método que limpa os campos id, nome, email
    @FXML
    public void clear(){
        idField.clear();
        modelField.clear();
        brandField.clear();
        numField.clear();
        plateField.clear();

        saveButton.setVisible(true);
        clearButton.setVisible(false);
        updateButton.setVisible(false);
        deleteButton.setVisible(false);
    }
}
