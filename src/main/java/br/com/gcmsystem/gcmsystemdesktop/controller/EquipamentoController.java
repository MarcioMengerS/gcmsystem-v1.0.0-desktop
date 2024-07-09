package br.com.gcmsystem.gcmsystemdesktop.controller;

import java.io.IOException;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import br.com.gcmsystem.gcmsystemdesktop.enums.CategoryEnum;
import br.com.gcmsystem.gcmsystemdesktop.model.EquipmentModel;
import br.com.gcmsystem.gcmsystemdesktop.service.EquipmentService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

@Controller
public class EquipamentoController {
    @Autowired
    private ApplicationContext context;  // Injeta o contexto do Spring
    private Integer id;
    private EquipmentModel item;

    @Autowired
    private EquipmentService equipmentService;
    
    @FXML
    private ComboBox<CategoryEnum> categoryComboBox;
    @FXML
    private TextField idField, modelField, brandField, numField, plateField;
    @FXML
    private Button saveButton;
    @FXML
    private Label lblTotal, lblWarning;
    @FXML
    private TableView<EquipmentModel> equipmentTableView;
    @FXML
    private TableColumn<EquipmentModel, Integer> registerColumn;
    @FXML
    private TableColumn<EquipmentModel, String> modelColumn, prefixColumn, plateColumn;
    @FXML
    private TableColumn<EquipmentModel, CategoryEnum> categoryColumn;

    public void initialize(){
        // Método usado para carregar e exibir a lista de equipamentos
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        prefixColumn.setCellValueFactory(new PropertyValueFactory<>("prefix"));
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

                    lblWarning.setVisible(false);
                }
            });
        saveButton.setOnAction(event->{
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/details-equip.fxml"));
                loader.setControllerFactory(context::getBean);  // Usa o contexto do Spring para criar o controlador
                Parent parent = loader.load();

                Stage stage = new Stage();
                stage.setScene(new Scene(parent));
                stage.initStyle(StageStyle.UTILITY);
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        addIconToTable();
        categoryComboBox.getItems().setAll(CategoryEnum.values());
    }
    //Adiciona botão na última coluna da Tabela
    private void addIconToTable() {
        TableColumn<EquipmentModel, String> actionColumn = new TableColumn<>();

        Callback<TableColumn<EquipmentModel, String>, TableCell<EquipmentModel, String>> cellFactory = (TableColumn<EquipmentModel, String> param) -> {

            final TableCell<EquipmentModel, String> cell = new TableCell<EquipmentModel, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    }else{
                        EquipmentModel data = getTableView().getItems().get(getIndex());
                        
                        FontIcon iconPen = new FontIcon(FontAwesomeSolid.PEN);
                        iconPen.setCursor(javafx.scene.Cursor.HAND);//apresenta como clicavel o icone
                        iconPen.setIconSize(18);//tamanho icone
                        iconPen.setIconColor(javafx.scene.paint.Color.BLUE);//cor icone

                        FontIcon iconTrash = new FontIcon(FontAwesomeSolid.TRASH);
                        iconTrash.setCursor(javafx.scene.Cursor.HAND);//apresenta como clicavel o icone
                        iconTrash.setIconSize(18);//tamanho icone
                        iconTrash.setIconColor(javafx.scene.paint.Color.RED);//cor icone

                        FontIcon iconEye = new FontIcon(FontAwesomeSolid.EYE);
                        iconEye.setCursor(javafx.scene.Cursor.HAND);//apresenta como clicavel o icone
                        iconEye.setIconSize(18);//tamanho icone
                        iconEye.setIconColor(javafx.scene.paint.Color.GREEN);//cor icone

                        HBox boxIcon = new HBox(iconEye, iconPen,iconTrash);
                        boxIcon.setStyle("-fx-alignment:center");
                        HBox.setMargin(iconEye, new Insets(2, 5, 0, 0));
                        HBox.setMargin(iconPen, new Insets(2, 5, 0, 5));
                        HBox.setMargin(iconTrash, new Insets(2, 0, 0, 5));

                        setGraphic(boxIcon);//insere icone na coluna

                        iconPen.setOnMouseClicked((MouseEvent event)->{
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/details-equip.fxml"));
                                loader.setControllerFactory(context::getBean);  // Usa o contexto do Spring para criar o controlador
                                Parent parent = loader.load();
                
                                EquipmentDetailsController equipmentDetailsController = loader.getController();
                                // Envia objeto 'data' para a classe EquipmentDetailsController
                                equipmentDetailsController.setEquipmentModel(data);
                
                                Stage stage = new Stage();
                                stage.setScene(new Scene(parent));
                                stage.initStyle(StageStyle.UTILITY);
                                stage.show();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        });
                        iconTrash.setOnMouseClicked((MouseEvent event)->{
                            delete();
                        });
                    }
                }
            };
            return cell;
        };
        actionColumn.setText("Ações");
        actionColumn.setMinWidth(80);//largura minima da coluna
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

    public void delete(){
        equipmentService.deleteById(id);
        list();
    }
}
