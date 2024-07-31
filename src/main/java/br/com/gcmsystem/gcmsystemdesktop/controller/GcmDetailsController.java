package br.com.gcmsystem.gcmsystemdesktop.controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ResourceBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import br.com.gcmsystem.gcmsystemdesktop.enums.GenderEnum;
import br.com.gcmsystem.gcmsystemdesktop.enums.StatusEnum;
import br.com.gcmsystem.gcmsystemdesktop.enums.UnitEnum;
import br.com.gcmsystem.gcmsystemdesktop.model.GcmModel;
import br.com.gcmsystem.gcmsystemdesktop.service.GcmService;
import br.com.gcmsystem.gcmsystemdesktop.util.UsbMonitor;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

@Controller
public class GcmDetailsController implements Initializable{

    @Autowired
    private GcmController gcmController;
    @Autowired
    private GcmService gcmService;
    @FXML
    private PasswordField passPF;
    @FXML
    private TextField numberTF, nameTF, funionalEmailTF, bloodTF, cpfTF, sutacheTF;
    @FXML
    private TextField phoneTF, catCnhTF, emailTF, matriculationTF, rgTF, tagTF, numWeaponTF;
    @FXML
    private DatePicker valCnhDP, birthDP, admissDP, emissWeaponDP, valWeaponDP;
    @FXML
    private ComboBox<StatusEnum> statusCB ;
    @FXML
    private ComboBox<UnitEnum> unitCB;
    @FXML
    private ComboBox<GenderEnum> genderCB;
    @FXML
    private Button saveBTN, cardBTN, cancelBTN, updateBTN;
    @FXML
    private Label idL, noticeL;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statusCB.getItems().setAll(StatusEnum.values());
        unitCB.getItems().setAll(UnitEnum.values());
        genderCB.getItems().setAll(GenderEnum.values());     
    }

    @FXML
    public void cancel(){
        Stage stage = (Stage) cancelBTN.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void save(){
        GcmModel gModel = new GcmModel();
        //converte texto me Short antes de salvar
        try {
            String num = numberTF.getText();
            short number = Short.parseShort(num);
            gModel.setNumber(number);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        gModel.setName(nameTF.getText());
        gModel.setFuncionalEmail(funionalEmailTF.getText());
        gModel.setBlood(bloodTF.getText());
        gModel.setCpf(cpfTF.getText());
        gModel.setSutache(sutacheTF.getText());
        gModel.setPhone(phoneTF.getText());
        gModel.setGender(genderCB.getSelectionModel().getSelectedItem());
        gModel.setCatCnh(catCnhTF.getText());
        gModel.setValidityCnh(valCnhDP.getValue());
        gModel.setBirth(birthDP.getValue());
        gModel.setAdmissionDate(admissDP.getValue());
        gModel.setEmail(emailTF.getText());
        gModel.setMatriculation(matriculationTF.getText());
        gModel.setRg(rgTF.getText());
        StatusEnum statusSel = statusCB.getSelectionModel().getSelectedItem();
        gModel.setStatus(statusSel);
        UnitEnum unitSel = unitCB.getSelectionModel().getSelectedItem();  
        gModel.setUnit(unitSel);
        gModel.setTag(tagTF.getText());
        gModel.setTransactionPass(passPF.getText());
        gModel.setCreatedAt(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).withNano(0));
        gModel.setEmissionCarryWeapon(emissWeaponDP.getValue());
        gModel.setValidityCarryWeapon(valWeaponDP.getValue());
        gModel.setNumberCarryWeapon(numWeaponTF.getText());
 
        //Depois de setar todos atributos salva objeto GCM no banco
        gcmService.save(gModel);
        //atualiza lista na pagina principal do equipamento
        gcmController.list();
        //fecha a janela após cadastro
        Stage stage = (Stage) saveBTN.getScene().getWindow();
        stage.close();
    }
    //Aciona leitor de Cartão RFID
    @FXML
    public void searchTag() {
        // Desabilita o botão na thread da aplicação
        cardBTN.setDisable(true);
        tagTF.clear();
        noticeL.setText("");
        noticeL.setStyle("");

        // Executa a operação de forma assíncrona em uma nova thread
        new Thread(() -> {
            String result = UsbMonitor.monitorarUSB(); // Leitura do cartão RFID de forma síncrona

            // Atualiza a interface gráfica na thread da aplicação
            Platform.runLater(() -> {
                cardBTN.setDisable(false); // Reabilita o botão após a operação
                //identifica o crachá se houver 8 caracteres copiou com sucesso
                noticeL.setVisible(true);
                if(result.length()==8){
                    tagTF.setText(result);
                    noticeL.setStyle("-fx-background-color:green");
                    noticeL.setText("Cartão identificado");
                }else{
                    noticeL.setStyle("-fx-background-color:orange");
                    noticeL.setText(result);
                    tagTF.setText("");
                }
            });
        }).start();
    }
    //Método chamado na linha 105 da classe GcmController.java
    //e pela propria classe
    public void getGcm(GcmModel gM){
        idL.setText(gM.getId().toString());
        nameTF.setText(gM.getName());
        numberTF.setText(gM.getNumber().toString());
        bloodTF.setText(gM.getBlood());
        funionalEmailTF.setText(gM.getFuncionalEmail());
        cpfTF.setText(gM.getCpf());
        sutacheTF.setText(gM.getSutache());
        phoneTF.setText(gM.getPhone());
        genderCB.getSelectionModel().select(gM.getGender());
        catCnhTF.setText(gM.getCatCnh());
        emailTF.setText(gM.getEmail());
        matriculationTF.setText(gM.getMatriculation());
        rgTF.setText(gM.getRg());
        passPF.setText(gM.getTransactionPass());
        statusCB.getSelectionModel().select(gM.getStatus());
        unitCB.getSelectionModel().select(gM.getUnit());
        valCnhDP.setValue(gM.getValidityCnh());
        birthDP.setValue(gM.getBirth());
        admissDP.setValue(gM.getAdmissionDate());
        emissWeaponDP.setValue(gM.getEmissionCarryWeapon());
        valWeaponDP.setValue(gM.getValidityCarryWeapon());
        numWeaponTF.setText(gM.getNumberCarryWeapon());
        if(!gM.getTag().equals(null)&!gM.getTag().isEmpty()){//se houver tag cadastrada mostra aviso na cor verde
            BackgroundFill backgroundFill =
            new BackgroundFill(
                    Color.valueOf("#8fff49"),//verde agua
                    new CornerRadii(7),
                    new Insets(2,25,2,25)
                    );
            Background background = new Background(backgroundFill);
            noticeL.setBackground(background);
            noticeL.setAlignment(Pos.CENTER);
            noticeL.setFont(Font.font("Arial",FontWeight.BOLD, 10));
            noticeL.setText("CADASTRADO");
            noticeL.setTextFill(javafx.scene.paint.Color.DARKGREEN);
            noticeL.setVisible(true);
        }
        tagTF.setText(gM.getTag());
        updateBTN.setVisible(true);
        saveBTN.setVisible(false);
    }

    @FXML
    public void update(){
        GcmModel gcmModel = new GcmModel();
        gcmModel = gcmService.findById(Integer.parseInt(idL.getText()));//exceção caso não retorne o id
        gcmModel.setName(nameTF.getText());
        gcmModel.setGender(genderCB.getValue());
        gcmModel.setEmail(emailTF.getText());
        gcmModel.setPhone(phoneTF.getText());
        gcmModel.setCpf(cpfTF.getText());//Execção para caso o cpf já exista no banco
        gcmModel.setRg(rgTF.getText());
        gcmModel.setBirth(birthDP.getValue());
        gcmModel.setCatCnh(catCnhTF.getText());
        gcmModel.setValidityCnh(valCnhDP.getValue());
        gcmModel.setBlood(bloodTF.getText());

        gcmModel.setStatus(statusCB.getValue());
        gcmModel.setUnit(unitCB.getValue());
        gcmModel.setModifyAt(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).withNano(0));
        gcmModel.setNumber(Short.parseShort(numberTF.getText()));
        gcmModel.setSutache(sutacheTF.getText());
        gcmModel.setFuncionalEmail(funionalEmailTF.getText());
        gcmModel.setAdmissionDate(admissDP.getValue());
        gcmModel.setMatriculation(matriculationTF.getText());
        gcmModel.setTransactionPass(passPF.getText());
        gcmModel.setTag(tagTF.getText());
        gcmModel.setEmissionCarryWeapon(emissWeaponDP.getValue());
        gcmModel.setValidityCarryWeapon(valWeaponDP.getValue());
        gcmModel.setNumberCarryWeapon(numWeaponTF.getText());
        
        gcmService.save(gcmModel);
        
        gcmController.list();
        //fecha a janela após cadastro
        Stage stage = (Stage) saveBTN.getScene().getWindow();
        stage.close();
    }
}