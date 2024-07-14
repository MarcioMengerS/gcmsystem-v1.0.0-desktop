package br.com.gcmsystem.gcmsystemdesktop.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import br.com.gcmsystem.gcmsystemdesktop.enums.CategoryEnum;
import br.com.gcmsystem.gcmsystemdesktop.model.EquipmentModel;
import br.com.gcmsystem.gcmsystemdesktop.service.EquipmentService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

@Controller
public class EquipmentDetailsController implements Initializable{

    @Autowired
    private EquipamentoController equipamentoController;

    private EquipmentModel equipmentModel;

    @Autowired
    private EquipmentService equipmentService;

    @FXML
    private TextField identifierTF;// identificador
    @FXML
    private TextField brandTF;//marca
    @FXML
    private TextField caliberTF;//calibre
    @FXML
    private ComboBox<CategoryEnum> categoryCB;//categoria
    @FXML
    private DatePicker expirationDP;//data validade
    @FXML
    private Text idT;//id
    @FXML
    private TextField locationTF;//localização
    @FXML
    private TextField modelTF;//modelo
    @FXML
    private TextArea moreInformTA;//informações adicionais
    @FXML
    private TextField plateTF;//placa
    @FXML
    private TextField prefixTF;//prefixo
    @FXML
    private HBox radioHB;//radicomunicador
    @FXML
    private TextField registerTF; //registro - arma
    @FXML
    private TextField registrationNumberTF;//patrimonio
    @FXML
    private TextField serieTF;// nº serie
    @FXML
    private TextField sinarmTF;//sinarm
    @FXML
    private TextField sizeTF;// tamanho
    @FXML
    private TextField typeTF;// tipo
    @FXML
    private GridPane vehicleGP, vestGP, gunGP, seriePatrGP, principalGP;//veiculo, colete, arma e Serie/Patrimonio
    @FXML
    private Button updateBTN, saveBTN;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        categoryCB.getItems().setAll(CategoryEnum.values());
        categoryCB.setOnAction(event->{
            CategoryEnum catEnum = categoryCB.getSelectionModel().getSelectedItem();
            selectionCB(catEnum);
        });
    }

    //Inseri campos conform seleção do combobox
    public void selectionCB(CategoryEnum catE){
        switch (catE) {
            case CategoryEnum.ARMA_LETAL:
                seriePatrGP.setVisible(true);
                gunGP.setVisible(true);
                vestGP.setVisible(false);
                radioHB.setVisible(false);
                vehicleGP.setVisible(false);
                break;
            case CategoryEnum.COLETE:
                seriePatrGP.setVisible(true);
                vestGP.setVisible(true);
                gunGP.setVisible(false);
                radioHB.setVisible(false);
                vehicleGP.setVisible(false);
                break;
            case CategoryEnum.RADIO_COMUNICADOR:
                seriePatrGP.setVisible(true);
                radioHB.setVisible(true);
                gunGP.setVisible(false);
                vestGP.setVisible(false);
                vehicleGP.setVisible(false);
                break;
            case CategoryEnum.VEICULO:
                vehicleGP.setVisible(true);
                seriePatrGP.setVisible(false);
                gunGP.setVisible(false);
                vestGP.setVisible(false);
                radioHB.setVisible(false);
                break;
            default:
                seriePatrGP.setVisible(true);
                gunGP.setVisible(false);
                vestGP.setVisible(false);
                radioHB.setVisible(false);
                vehicleGP.setVisible(false);
                break;
        }
    }

    //método utilizado pela classe EquipamentController.java para enviar objeto a ser atualizado na view details-equip.fxml
    public void setEquipmentModel(EquipmentModel eM) {
        saveBTN.setVisible(false);
        updateBTN.setVisible(true);
        this.equipmentModel = eM;
        
        if (eM != null) {
            idT.setText(String.valueOf(equipmentModel.getId()));
            registrationNumberTF.setText(String.valueOf(equipmentModel.getRegistrationNumber()));
            brandTF.setText(equipmentModel.getBrand());
            locationTF.setText(equipmentModel.getLocation());
            modelTF.setText(equipmentModel.getModel());
            serieTF.setText(equipmentModel.getSerie());
            moreInformTA.setText(equipmentModel.getMoreInform());
            categoryCB.getSelectionModel().select(equipmentModel.getCategory());

            switch (equipmentModel.getCategory()) {
                case CategoryEnum.RADIO_COMUNICADOR:
                    radioHB.setVisible(true);
                    identifierTF.setText(String.valueOf(equipmentModel.getIdentifier()));
                    break;
                case CategoryEnum.VEICULO:
                    vehicleGP.setVisible(true);
                    seriePatrGP.setVisible(false);
                    prefixTF.setText(String.valueOf(equipmentModel.getPrefix()));
                    plateTF.setText(equipmentModel.getPlate());
                    break;
                case CategoryEnum.COLETE:
                    vestGP.setVisible(true);
                    sizeTF.setText(equipmentModel.getSize());
                    expirationDP.setValue(equipmentModel.getExpiratioDate());
                    break;
                case CategoryEnum.ARMA_LETAL:
                    gunGP.setVisible(true);
                    typeTF.setText(equipmentModel.getType());
                    caliberTF.setText(equipmentModel.getCaliber());
                    registerTF.setText(String.valueOf(equipmentModel.getRegister()));
                    sinarmTF.setText(equipmentModel.getSinarm());
                    break;
                default:
                    break;
            }
        }
    }

    public void viewEquipment(EquipmentModel eM) {
        setEquipmentModel(eM);
        updateBTN.setVisible(false);

        principalGP.getChildren().forEach(this::disableEditing);
        seriePatrGP.getChildren().forEach(this::disableEditing);
        vehicleGP.getChildren().forEach(this::disableEditing);
        vestGP.getChildren().forEach(this::disableEditing);
        gunGP.getChildren().forEach(this::disableEditing);
        radioHB.getChildren().forEach(this::disableEditing);
        disableEditing(moreInformTA);
        disableEditing(expirationDP);
    }

    private void disableEditing(Node node) {
        if (node instanceof TextField) {
            ((TextField) node).setEditable(false);
            ((TextField) node).setFocusTraversable(false);
            ((TextField) node).setMouseTransparent(true);
            ((TextField) node).getStyleClass().add("no-border");
        } else if (node instanceof TextArea) {
            ((TextArea) node).setEditable(false);
            ((TextArea) node).setFocusTraversable(false);
            ((TextArea) node).setMouseTransparent(true);
            ((TextArea) node).getStyleClass().add("no-border");
        } else if (node instanceof ComboBox) {
            ((ComboBox<?>) node).setEditable(false);
            ((ComboBox<?>) node).setMouseTransparent(true);
            ((ComboBox<?>) node).setFocusTraversable(false);
            ((ComboBox<?>) node).getStyleClass().add("no-border");
        }else if (node instanceof DatePicker) {
            ((DatePicker) node).setEditable(false);
            ((DatePicker) node).setMouseTransparent(true);
            ((DatePicker) node).setFocusTraversable(false);
            ((DatePicker) node).getStyleClass().add("no-border");
        }
    }

    @FXML
    public void update(){
        if(equipmentModel!=null){
             if(!registrationNumberTF.getText().trim().isEmpty())
                equipmentModel.setRegistrationNumber(Integer.parseInt(registrationNumberTF.getText()));
            equipmentModel.setLocation(locationTF.getText());
            equipmentModel.setSerie(serieTF.getText());
            equipmentModel.setMoreInform(moreInformTA.getText());
            equipmentModel.setModel(modelTF.getText());
            equipmentModel.setBrand(brandTF.getText());
            CategoryEnum catSelec = categoryCB.getValue();
            equipmentModel.setCategory(categoryCB.getValue());
  
            equipmentModel = enableFields(catSelec, equipmentModel);
            equipmentService.save(equipmentModel);
            //atualiza lista na pagina principal do equipamento
            equipamentoController.list();
            //fecha a janela após atualização
            Stage stage = (Stage) updateBTN.getScene().getWindow();
            stage.close();
         }
    }

    @FXML
    public void save(){
        EquipmentModel eModel = new EquipmentModel();
        eModel.setLocation(locationTF.getText());
        eModel.setBrand(brandTF.getText());
        eModel.setModel(modelTF.getText());
        eModel.setSerie(serieTF.getText());
        eModel.setMoreInform(moreInformTA.getText());
        if(!registrationNumberTF.getText().trim().isEmpty())
            eModel.setRegistrationNumber(Integer.parseInt(registrationNumberTF.getText()));
        CategoryEnum catSelec = categoryCB.getSelectionModel().getSelectedItem();
        eModel.setCategory(catSelec);
        
        eModel = enableFields(catSelec, eModel);
        equipmentService.save(eModel);
        //atualiza lista na pagina principal do equipamento
        equipamentoController.list();
        //fecha a janela após cadastro
        Stage stage = (Stage) updateBTN.getScene().getWindow();
        stage.close();
    }

    public EquipmentModel enableFields(CategoryEnum catEnum, EquipmentModel equipMo){
        switch (catEnum) {
            case VEICULO:
                equipMo.setPlate(plateTF.getText());
                if(!prefixTF.getText().trim().isEmpty())
                    equipMo.setPrefix(Integer.parseInt(prefixTF.getText()));
                break;
            case COLETE:
                equipMo.setSize(sizeTF.getText());                
                equipMo.setExpiratioDate(expirationDP.getValue());
                break;
            case ARMA_LETAL:
                equipMo.setType(typeTF.getText());
                equipMo.setCaliber(caliberTF.getText());
                equipMo.setSinarm(sinarmTF.getText());
                if(!registerTF.getText().trim().isEmpty())
                    equipMo.setRegister(Integer.parseInt(registerTF.getText()));
                break;
            case RADIO_COMUNICADOR:
                if(!identifierTF.getText().trim().isEmpty())
                    equipMo.setIdentifier(Integer.parseInt(identifierTF.getText()));
                break;
            default:
                break;
        }
        return equipMo;
    }
}