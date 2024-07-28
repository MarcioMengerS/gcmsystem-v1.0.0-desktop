package br.com.gcmsystem.gcmsystemdesktop.controller;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import br.com.gcmsystem.gcmsystemdesktop.model.GcmModel;
import br.com.gcmsystem.gcmsystemdesktop.service.GcmService;
import javafx.collections.FXCollections;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
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

    // private Integer id;
    //private GcmModel item;

    @Autowired
    private GcmService gcmService;

    // @FXML
    // private TextField /*,idField numberField,  emailField,nameField, tagField*/ ;
    @FXML
    private Button /*saveButton, updateButton, deleteButton, clearButton,cardButton,*/  registerButton;
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

        //Quando clica no item na lista automaticamente há o preenchimento dos campos
        // gcmTableView.getSelectionModel().selectedItemProperty()
        //     .addListener((obs, old, newValue)->{
        //         if(newValue !=null){
        //             id = newValue.getId();
        //             item = selectedItem();
        //             idField.setText(String.valueOf(item.getId()));
        //             numberField.setText(item.getNumber().toString());
        //             nameField.setText(item.getName());
        //             emailField.setText(item.getEmail());
        //             tagField.setText(item.getTag());
        //             saveButton.setVisible(false);
        //             clearButton.setVisible(true);
        //             updateButton.setVisible(true);
        //             deleteButton.setVisible(true);
        //             lblWarning.setVisible(false);
        //         }
        //     });
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

    // public GcmModel selectedItem(){
    //     return gcmService.findById(id);
    // }

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

    // @FXML
    // public void save(){
    //     if(!nameField.getText().trim().isEmpty()){
    //         GcmModel gcmModel = new GcmModel();
    //         //converte texto me Short antes de salvar
    //         try {
    //             String num = numberField.getText();
    //             short number = Short.parseShort(num);
    //             gcmModel.setNumber(number);
    //         } catch (NumberFormatException e) {
    //             e.printStackTrace();
    //         }
    //         gcmModel.setName(nameField.getText());
    //         gcmModel.setEmail(emailField.getText());
    //         gcmModel.setTag(tagField.getText());
    //         gcmService.save(gcmModel);
    //         list();
    //         clear();
    //     }else {
    //         lblWarning.setVisible(true);
    //     }
    // }
    // @FXML
    // public void update(){
    //     item.setName(nameField.getText());
    //     item.setEmail(emailField.getText());
    //     item.setTag(tagField.getText());
    //     gcmService.save(item);
    //     clear();
    //     list();
    // }
    // @FXML
    // public void delete(){
    //     gcmService.deleteById(id);
    //     clear();
    //     list();
    // }

    //Método que limpa os campos id, nome, email
    // @FXML
    // public void clear(){
    //     idField.clear();
    //     numberField.clear();
    //     nameField.clear();
    //     emailField.clear();
    //     tagField.clear();

    //     saveButton.setVisible(true);
    //     clearButton.setVisible(false);
    //     updateButton.setVisible(false);
    //     deleteButton.setVisible(false);
    // }
  
    // @FXML
    // public void searchTag() {
    //     // Desabilita o botão na thread da aplicação
    //     cardButton.setDisable(true);
    //     tagField.clear();

    //     // Executa a operação de forma assíncrona em uma nova thread
    //     new Thread(() -> {
    //         String result = UsbMonitor.monitorarUSB(); // Leitura do cartão RFID de forma síncrona

    //         // Atualiza a interface gráfica na thread da aplicação
    //         Platform.runLater(() -> {
    //             tagField.setText(result);
    //             cardButton.setDisable(false); // Reabilita o botão após a operação
    //         });
    //     }).start();
    // }
}