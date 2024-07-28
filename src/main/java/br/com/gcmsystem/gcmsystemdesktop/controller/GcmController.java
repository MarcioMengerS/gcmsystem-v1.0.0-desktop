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

import br.com.gcmsystem.gcmsystemdesktop.enums.StatusEnum;
import br.com.gcmsystem.gcmsystemdesktop.enums.UnitEnum;
import br.com.gcmsystem.gcmsystemdesktop.model.GcmModel;
import br.com.gcmsystem.gcmsystemdesktop.service.GcmService;
import javafx.collections.FXCollections;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

@Controller
public class GcmController{

    @Autowired
    private ApplicationContext context;  // Injeta o contexto do Spring
    @Autowired
    private GcmService gcmService;

    @FXML
    private HBox searchHB;
    @FXML
    private RadioButton numberRB, nameRB, sutacheRB;
    @FXML
    private ComboBox<String> statusCB, unitCB;
    @FXML
    private TextField searchTF;
    @FXML
    private Button registerButton;
    @FXML
    private Label lblTotal;
    @FXML
    private TableView<GcmModel> gcmTableView;
    @FXML
    private TableColumn<GcmModel, String>numberColumn, nameColumn, sutacheColumn, tagColumn;

    public void initialize() {
        list();
        //botão que abre tela de cadastro
        registerButton.setOnAction(event->{
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/details-gcm.fxml"));
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
        //Quando uma tecla geradora de caracteres é digitada mostra icone "X" no campo de pesquisa
        searchTF.setOnKeyTyped(event ->insertIconX());

        statusCB.getItems().setAll(enumInString("Status"));
        unitCB.getItems().setAll(enumInString("Unit"));

        // realiza busca conforme informação digitada no searchTF
        searchTF.textProperty().addListener((observable, oldValue, newValue) -> {
            gcmTableView.refresh(); //limpa BackgroundFill que aparecia após seleção
            filterRadioButton(newValue.toLowerCase());
            // if(searchHB.getChildren().size()>1){//Se o campo pesquisar estiver vazio remove icone "X"
            //     searchHB.getChildren().removeLast(); 
            // }
        });
        searchTF.setOnMouseClicked((event)->{
            unitCB.getSelectionModel().selectFirst();
            statusCB.getSelectionModel().selectFirst();
        });

        //busca GCMs através do Combobox Status
        statusCB.setOnAction((event)->{ 
            gcmTableView.refresh(); //limpa BackgroundFill que aparecia após seleção
            filterComboBox(statusCB.getId());
        });
        statusCB.setOnMouseClicked(event ->{
            unitCB.getSelectionModel().selectFirst();
            searchTF.clear();
        });

        //busca GCMs através do Combobox Unidade
        unitCB.setOnAction((event)->{
            gcmTableView.refresh(); //limpa BackgroundFill que aparecia após seleção
            filterComboBox(unitCB.getId());
        });
        unitCB.setOnMouseClicked(event ->{
            statusCB.getSelectionModel().selectFirst();
            searchTF.clear();
        });
        
        //limpa campo de pesquisa searchTF quando clicar em um dos Radios buttons
        numberRB.setOnAction(event-> searchTF.clear());
        nameRB.setOnAction(event-> searchTF.clear());
        sutacheRB.setOnAction(event-> searchTF.clear());

        //Clique duplo na linha da tabela aciona o evento abaixo
        gcmTableView.setOnMouseClicked( event -> {
            if( event.getClickCount() == 2 ) {
                GcmModel gcmSelected=gcmTableView.getSelectionModel().selectedItemProperty().getValue();
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/details-gcm.fxml"));
                    loader.setControllerFactory(context::getBean);  // Usa o contexto do Spring para criar o controlador
                    Parent parent = loader.load();

                    GcmDetailsController gcmDetailsController = loader.getController();
                    gcmDetailsController.getGcm(gcmSelected);
    
                    Stage stage = new Stage();
                    stage.setScene(new Scene(parent));
                    stage.initStyle(StageStyle.UTILITY);
                    stage.show();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void list(){
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number")); //id: atributo do objeto GcmModel
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));//name: atributo do objeto GcmModel
        sutacheColumn.setCellValueFactory(new PropertyValueFactory<>("sutache"));//email: atributo do objeto GcmModel
        tagColumn.setCellValueFactory(new PropertyValueFactory<>("tag"));//tag: atributo do objeto GcmModel
        //Modifica a coluna CARTÃO = CADASTRADO | CADASTRO PENDENTE 
        tagColumn.setCellFactory(new Callback<TableColumn<GcmModel, String>, TableCell<GcmModel, String>>() {
            @Override
            public TableCell<GcmModel, String> call(TableColumn<GcmModel, String> param) {
                return new TableCell<GcmModel, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            if (item.isEmpty()) {
                                setText("");
                            }else{
                                BackgroundFill backgroundFill =
                                new BackgroundFill(
                                        Color.valueOf("#8fff49"),//verde agua
                                        new CornerRadii(7),
                                        new Insets(2,25,2,25)
                                        );
                                Background background = new Background(backgroundFill);
                                setBackground(background);
                                setAlignment(Pos.CENTER);
                                setFont(Font.font("Arial",FontWeight.BOLD, 10));
                                setText("CADASTRADO");
                                setTextFill(javafx.scene.paint.Color.DARKGREEN);
                            }
                        }
                    }
                };
            }
        });

        gcmTableView.setItems(FXCollections.observableArrayList(gcmService.findAll()));
        lblTotal.setText(String.valueOf(gcmTableView.getItems().size()));
    }

    //Insere icone "X" para apagar conteúdo do campo pesquisar
    public void insertIconX(){
        if(!searchTF.getText().isEmpty()){
            if(searchHB.getChildren().size()<=1){//Se houver somente o campo de pesquisa no hbox cria icone X
                FontIcon iconClear = new FontIcon(FontAwesomeSolid.TIMES);
                iconClear.setIconColor(Color.GRAY);
                iconClear.setCursor(Cursor.HAND);
                searchHB.getChildren().addLast(iconClear);//insere icone X no Hbox
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

    //Transformar StatusEnum e UnitEnum em String pois o combobox é do tipo String para aceitar opção "todos"
    public List<String> enumInString(String enumType){
        List<String> listString = new ArrayList<>();
        if(enumType.equals("Status")){
            List<StatusEnum> listStatus = Arrays.asList(StatusEnum.values());
            for (StatusEnum statusEnum : listStatus) {
                listString.add(statusEnum.name()); 
            }
        }else if(enumType.equals("Unit")){
            List<UnitEnum> listUnit = Arrays.asList(UnitEnum.values());
            for (UnitEnum unitEnum : listUnit) {
                listString.add(unitEnum.name());
            }
        }
        listString.addFirst("Todos");
        return listString;
    }

    //filtro de pesquisa dos radios buttons
    private void filterRadioButton(String filterRB) {
        if (filterRB == null || filterRB.isEmpty()) {
            gcmTableView.setItems(FXCollections.observableArrayList(gcmService.findAll()));
        } else if(nameRB.isSelected()){
            gcmTableView.setItems(FXCollections.observableArrayList(
                    gcmService.findAll().stream()
                        .filter(gcm -> gcm.getName().toLowerCase().contains(filterRB))
                        .collect(Collectors.toList())
            ));

        } else if(numberRB.isSelected()){
            List<GcmModel> filteredList = gcmService.findAll().stream()
                    .filter(gcm -> {
                        Short prefix = gcm.getNumber();
                        return prefix != null && prefix.toString().toLowerCase().contains(filterRB);
                    })
                    .collect(Collectors.toList());
            gcmTableView.setItems(FXCollections.observableArrayList(filteredList));

        }else if(sutacheRB.isSelected()){
            gcmTableView.setItems(FXCollections.observableArrayList(
                    gcmService.findAll().stream()
                        .filter(gcm -> gcm.getSutache().toLowerCase().contains(filterRB))
                        .collect(Collectors.toList())
            ));
        }
    }

    //filtro de pesquisa Combobox
    private void filterComboBox(String filterCB) {
        String itemSelect;
        if(filterCB.equals("statusCB")){
            itemSelect = statusCB.getSelectionModel().getSelectedItem();
            List<StatusEnum> listEnum = Arrays.asList(StatusEnum.values());
            for (StatusEnum status : listEnum) {
                if(status.name().equals(itemSelect)){
                    gcmTableView.setItems(FXCollections.observableArrayList(gcmService.findByStatus(status)));
                }
                if(itemSelect.equals("Todos")){
                    gcmTableView.setItems(FXCollections.observableArrayList(gcmService.findAll()));
                }
            }
        }else if(filterCB.equals("unitCB")){
            itemSelect = unitCB.getSelectionModel().getSelectedItem();
            List<UnitEnum> listEnum = Arrays.asList(UnitEnum.values());
            for (UnitEnum unit : listEnum) {
                if(unit.name().equals(itemSelect)){
                    gcmTableView.setItems(FXCollections.observableArrayList(gcmService.findByUnit(unit)));
                }
                if(itemSelect.equals("Todos")){
                    gcmTableView.setItems(FXCollections.observableArrayList(gcmService.findAll()));
                }
            }
        }
    }
}