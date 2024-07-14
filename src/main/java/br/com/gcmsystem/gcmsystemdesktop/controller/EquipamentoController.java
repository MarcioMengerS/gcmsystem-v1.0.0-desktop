package br.com.gcmsystem.gcmsystemdesktop.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
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
    private RadioButton serieRB, registrationNumberRB, prefixRB;
    @FXML
    private HBox searchHB;
    @FXML
    private ComboBox<CategoryEnum> categoryComboBox, filterCatCB;
    @FXML
    private TextField idField, modelField, brandField, numField, plateField, searchTF;
    @FXML
    private Button saveButton;
    @FXML
    private Label lblTotal, lblWarning;
    @FXML
    private TableView<EquipmentModel> equipmentTableView;
    @FXML
    private TableColumn<EquipmentModel, Integer> registerColumn;
    @FXML
    private TableColumn<EquipmentModel, String> modelColumn, prefixColumn, serieColumn;
    @FXML
    private TableColumn<EquipmentModel, CategoryEnum> categoryColumn;
    @FXML
    private FontIcon clearField;

    public void initialize(){
        // Método usado para carregar e exibir a lista de equipamentos
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        prefixColumn.setCellValueFactory(new PropertyValueFactory<>("prefix"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        registerColumn.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        serieColumn.setCellValueFactory(new PropertyValueFactory<>("serie"));

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
        filterCatCB.getItems().setAll(CategoryEnum.values());
        // Configurar evento de busca
        searchTF.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTable(newValue.toLowerCase());
        });

        //Quando uma tecla geradora de caracteres é digitada mostra icone "X" no campo de pesquisa
        searchTF.setOnKeyTyped(event ->insertIconX());
    }

    public void insertIconX(){

        if(!searchTF.getText().isEmpty()){
            if(searchHB.getChildren().size()<=1){//Se houver somente o campo de pesquisa no hbox cria icone X
                FontIcon iconClear = new FontIcon(FontAwesomeSolid.TIMES);
                iconClear.setIconColor(Color.GRAY);
                iconClear.setCursor(Cursor.HAND);
                searchHB.getChildren().addAll(iconClear);//insere icone X no Hbox
                iconClear.setOnMouseClicked(event ->{//clicar no X limpa o campo de pesquisa e remove o icone do hbox
                    searchTF.clear();
                    searchHB.getChildren().removeLast();
                });
            }
        }else{
            if(searchHB.getChildren().size()>1){
                searchHB.getChildren().removeLast(); 
            }
        }
    }

    private void filterTable(String filter) {
        if (filter == null || filter.isEmpty()) {
            equipmentTableView.setItems(FXCollections.observableArrayList(equipmentService.findAll()));
        } else if(serieRB.isSelected()){
            equipmentTableView.setItems(FXCollections.observableArrayList(
                    equipmentService.findAll().stream()
                        .filter(equipment -> equipment.getSerie().toLowerCase().contains(filter))
                        .collect(Collectors.toList())
            ));

        } else if(prefixRB.isSelected()){
            List<EquipmentModel> filteredList = equipmentService.findAll().stream()
                    .filter(equipment -> {
                        Integer prefix = equipment.getPrefix();
                        return prefix != null && prefix.toString().toLowerCase().contains(filter);
                    })
                    .collect(Collectors.toList());
            equipmentTableView.setItems(FXCollections.observableArrayList(filteredList));

        }else if(registrationNumberRB.isSelected()){
            List<EquipmentModel> filteredList = equipmentService.findAll().stream()
                    .filter(equipment -> {
                        Integer regNum = equipment.getRegistrationNumber();
                        return regNum != null && regNum.toString().toLowerCase().contains(filter);
                    })
                    .collect(Collectors.toList());
            equipmentTableView.setItems(FXCollections.observableArrayList(filteredList));
        }
    }

    public void filterTableCategory(){
        CategoryEnum categoryEnum = filterCatCB.getSelectionModel().getSelectedItem();
        List<CategoryEnum> enumList = Arrays.asList(CategoryEnum.values());
        List<String> stringList = new ArrayList<>();
        for (CategoryEnum e : enumList) {
            
            System.out.println(categoryEnum.toString());
            stringList.add(e.name());
            System.out.println(stringList.get(0));

        }
        equipmentTableView.setItems(FXCollections.observableArrayList(equipmentService.findByCategory(categoryEnum)));
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
                        //insere dica de ajuda no ícone 
                        Tooltip tooltipPen = new Tooltip("Editar");
                        tooltipPen.setStyle(
                            "-fx-font: normal bold 10 Langdon; "
                            + "-fx-base: #AE3522; "
                            + "-fx-text-fill: orange;"
                        );
                        Tooltip.install(iconPen, tooltipPen);

                        FontIcon iconTrash = new FontIcon(FontAwesomeSolid.TRASH);
                        iconTrash.setCursor(javafx.scene.Cursor.HAND);//apresenta como clicavel o icone
                        iconTrash.setIconSize(18);//tamanho icone
                        iconTrash.setIconColor(javafx.scene.paint.Color.RED);//cor icone
                        //insere dica de ajuda no ícone 
                        Tooltip tooltipTrash = new Tooltip("Excluir");
                        tooltipTrash.setStyle(
                            "-fx-font: normal bold 10 Langdon; "
                            + "-fx-base: #AE3522; "
                            + "-fx-text-fill: orange;"
                        );
                        Tooltip.install(iconTrash, tooltipTrash);

                        FontIcon iconEye = new FontIcon(FontAwesomeSolid.EYE);
                        iconEye.setCursor(javafx.scene.Cursor.HAND);//apresenta como clicavel o icone
                        iconEye.setIconSize(18);//tamanho icone
                        iconEye.setIconColor(javafx.scene.paint.Color.GREEN);//cor icone
                        Tooltip tooltipEye = new Tooltip("Ver");
                        tooltipEye.setStyle(
                            "-fx-font: normal bold 10 Langdon; "
                            + "-fx-base: #AE3522; "
                            + "-fx-text-fill: orange;"
                        );
                        Tooltip.install(iconEye, tooltipEye);
                        
                        //Hbox que contém os três icones dentro da célula da coluna ações
                        HBox boxIcon = new HBox(iconEye, iconPen,iconTrash);
                        boxIcon.setStyle("-fx-alignment:center");
                        HBox.setMargin(iconEye, new Insets(2, 5, 0, 0));
                        HBox.setMargin(iconPen, new Insets(2, 5, 0, 5));
                        HBox.setMargin(iconTrash, new Insets(2, 0, 0, 5));

                        setGraphic(boxIcon);//insere icone na célula
                        iconEye.setOnMouseClicked((MouseEvent event)->{
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/details-equip.fxml"));
                                loader.setControllerFactory(context::getBean);  // Usa o contexto do Spring para criar o controlador
                                Parent parent = loader.load();
                
                                EquipmentDetailsController equipmentDetailsController = loader.getController();
                                // Envia objeto 'data' para a classe EquipmentDetailsController
                                equipmentDetailsController.viewEquipment(data);
                
                                Stage stage = new Stage();
                                Scene scene = new Scene(parent);
                                // Carrega o arquivo CSS
                                String css = getClass().getResource("/css/style.css").toExternalForm();
                                scene.getStylesheets().add(css);

                                stage.setScene(scene);
                                stage.initStyle(StageStyle.UTILITY);
                                stage.show();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        });
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
        actionColumn.setMinWidth(90);//largura minima da coluna
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
