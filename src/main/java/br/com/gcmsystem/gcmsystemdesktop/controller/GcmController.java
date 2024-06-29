package br.com.gcmsystem.gcmsystemdesktop.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.gcmsystem.gcmsystemdesktop.model.GcmModel;
import br.com.gcmsystem.gcmsystemdesktop.service.GcmService;
import br.com.gcmsystem.gcmsystemdesktop.util.UsbMonitor;
import javafx.application.Platform;
import javafx.collections.FXCollections;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

@Component
public class GcmController{

    private Integer id;
    private GcmModel item;

    @Autowired
    private GcmService gcmService;

    @FXML
    private TextField idField, numberField, nameField, emailField, tagField;
    @FXML
    private Button saveButton, updateButton, deleteButton, clearButton, cardButton;
    @FXML
    private Label lblTotal, lblWarning;
    @FXML
    private TableView<GcmModel> gcmTableView;
    @FXML
    private TableColumn<GcmModel, String>numberColumn, nameColumn, emailColumn, tagColumn;

    public void initialize() {
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number")); //id: atributo do objeto GcmModel
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));//name: atributo do objeto GcmModel
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));//email: atributo do objeto GcmModel
        tagColumn.setCellValueFactory(new PropertyValueFactory<>("tag"));//tag: atributo do objeto GcmModel

        // idField.setDisable(true);//desabilita edição campo id
        list();

        //Preenchimento dos campos após seleção de itens na lista 
        gcmTableView.getSelectionModel().selectedItemProperty()
            .addListener((obs, old, newValue)->{
                if(newValue !=null){
                    id = newValue.getId();
                    item = selectedItem();
                    idField.setText(String.valueOf(item.getId()));
                    numberField.setText(item.getNumber().toString());
                    nameField.setText(item.getName());
                    emailField.setText(item.getEmail());
                    tagField.setText(item.getTag());
                    saveButton.setVisible(false);
                    clearButton.setVisible(true);
                    updateButton.setVisible(true);
                    deleteButton.setVisible(true);
                    lblWarning.setVisible(false);
                }
            });  
    }

    public GcmModel selectedItem(){
        return gcmService.findById(id);
    }

    public void list(){
        gcmTableView.setItems(FXCollections.observableArrayList(gcmService.findAll()));
        lblTotal.setText(String.valueOf(gcmTableView.getItems().size()));
    }

    @FXML
    public void save(){
        if(!nameField.getText().trim().isEmpty()){
            GcmModel gcmModel = new GcmModel();
            //converte texto me Short antes de salvar
            try {
                String num = numberField.getText();
                short number = Short.parseShort(num);
                gcmModel.setNumber(number);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            gcmModel.setName(nameField.getText());
            gcmModel.setEmail(emailField.getText());
            gcmModel.setTag(tagField.getText());
            gcmService.save(gcmModel);
            list();
            clear();
        }else {
            lblWarning.setVisible(true);
        }
    }
    @FXML
    public void update(){
        item.setName(nameField.getText());
        item.setEmail(emailField.getText());
        item.setTag(tagField.getText());
        gcmService.save(item);
        clear();
        list();
    }
    @FXML
    public void delete(){
        gcmService.deleteById(id);
        clear();
        list();
    }

    //Método que limpa os campos id, nome, email
    @FXML
    public void clear(){
        idField.clear();
        numberField.clear();
        nameField.clear();
        emailField.clear();
        tagField.clear();

        saveButton.setVisible(true);
        clearButton.setVisible(false);
        updateButton.setVisible(false);
        deleteButton.setVisible(false);
    }
  
    @FXML
    public void searchTag() {
        // Desabilita o botão na thread da aplicação
        cardButton.setDisable(true);
        tagField.clear();

        // Executa a operação de forma assíncrona em uma nova thread
        new Thread(() -> {
            String result = UsbMonitor.monitorarUSB(); // Leitura do cartão RFID de forma síncrona

            // Atualiza a interface gráfica na thread da aplicação
            Platform.runLater(() -> {
                processResult(result);
                cardButton.setDisable(false); // Reabilita o botão após a operação
            });
        }).start();
    }

    public void processResult(String result){
        tagField.setText(result);
        // result = UsbMonitor.monitorarUSB();//le cartão RFID de forma sincrona
        System.out.println(result);
        cardButton.setDisable(false);
    }
}
