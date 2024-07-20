package br.com.gcmsystem.gcmsystemdesktop.controller;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.controlsfx.control.textfield.TextFields;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.gcmsystem.gcmsystemdesktop.enums.CategoryEnum;
import br.com.gcmsystem.gcmsystemdesktop.model.EquipmentModel;
import br.com.gcmsystem.gcmsystemdesktop.model.GcmModel;
import br.com.gcmsystem.gcmsystemdesktop.model.RegisterModel;
import br.com.gcmsystem.gcmsystemdesktop.service.EquipmentService;
import br.com.gcmsystem.gcmsystemdesktop.service.GcmService;
import br.com.gcmsystem.gcmsystemdesktop.service.RegisterService;
import br.com.gcmsystem.gcmsystemdesktop.service.HistoricService;
import br.com.gcmsystem.gcmsystemdesktop.util.UsbMonitor;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Callback;

@Component
public class RegisterController implements Initializable{
    @Autowired
    private GcmService gcmService;
    @Autowired
    private EquipmentService equipmentService;
    @Autowired
    private RegisterService registerService;
    @Autowired
    private HistoricService historicService;

    @FXML
    private TextField gcmTF;
    @FXML
    private Text resultRfid;
    @FXML
    private Button loanButton;
    @FXML
    private TextArea noteText;
    @FXML
    private ComboBox<CategoryEnum> equipmCatComboBox;
    @FXML
    private ComboBox<String> equipmNumComboBox;
    @FXML
    private TableView<RegisterModel> registerTableView;
    @FXML
    private TableColumn<RegisterModel, String>statusColumn, timeColumn, noteColumn;
    @FXML
    private TableColumn<RegisterModel, LocalDate> dateColumn;

    @FXML
    private TableColumn<RegisterModel, String>
        gcmNameColumn, gcmEmailColumn,gcmTagColumn,
        equipmentPatrColumn, equipmentCategoryColumn,
        equipmentBrandColumn, equipmentModelColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ///////// início autocomplete gcmTextField ////////////
        ObservableList<String> items = FXCollections.observableArrayList(listAllGcm());
        TextFields.bindAutoCompletion(gcmTF, items);
        /////////// fim autocomplete gcmTextField ////////////

        equipmNumComboBox.setOnAction(event->{
            equipmNumComboBox.getSelectionModel().getSelectedItem();//seleciona equipamento desejado
        });

        equipmCatComboBox.getItems().setAll(CategoryEnum.values());
 
        list();

        onKeyRelesead();
    }

    //Empréstimo de equipamento. Thread para leitura do Crachá
    @FXML
    public void loan() {
        // Desabilita o botão na thread da aplicação
        loanButton.setDisable(true);
        resultRfid.setText("");

        // Executa a operação de forma assíncrona em uma nova thread
        new Thread(() -> {
            String result = UsbMonitor.monitorarUSB(); // Leitura do cartão RFID de forma síncrona

            // Atualiza a interface gráfica na thread da aplicação
            Platform.runLater(() -> {
                associate(result);
                loanButton.setDisable(false); // Reabilita o botão após a operação
            });
        }).start();
    }

    //Realiza emprestimo do equipamento e registra histórico
    public void associate(String result){
        String numberGcm = gcmTF.getText();
        GcmModel gcmModel = gcmService.findByNumber(Short.parseShort(numberGcm));

        //Encontrou GCM? compara as Tags
        if(gcmModel.getTag().equals(result)){
            resultRfid.setText("GCM IDENTIFICADO!");
            RegisterModel registerModel = new RegisterModel();//Instancia objeto cautela
            registerModel.setStatus("Emprestado");//marca status como emprestado na cautela
            registerModel.setNote(noteText.getText()); //registra observação na cautela
            
            registerModel.setGcm(gcmModel);// associa GCM a cautela

            EquipmentModel equipmentModel = new EquipmentModel();//Instancia objeto Equipamento
            String equipmentNum = equipmNumComboBox.getSelectionModel().getSelectedItem(); //pega patrimonio selecionado pelo usuário
            //busca no register patrimonio com esse numero
            Integer e = Integer.valueOf(equipmentNum);// converte em nº patrimonio e prefixo
            CategoryEnum equipmentSelected =equipmCatComboBox.getSelectionModel().getSelectedItem();
            if(equipmentSelected.equals(CategoryEnum.VEICULO)){
                //se categoria for veiculo busca pelo prefixo
                equipmentModel = equipmentService.findByPrefix(e);
            }else if(equipmentSelected.equals(CategoryEnum.CAMERA_CORPORAL)){
                //se categoria for camera corporal busca pelo nº de série
                equipmentModel = equipmentService.findBySerie(equipmentNum);
            }else{
                //senão busca pelo patrimonio
                equipmentModel = equipmentService.findByRegistrationNumber(e);//busca equipamento pelo numero de patrimonio
            }
            registerModel.setEquipment(equipmentModel);//associa Equipamento a cautela

            historicService.createHistoric(registerModel);//registra no historico a cautela
            registerService.save(registerModel);// salva cautela no banco de dados
            clear();
        }else{
            resultRfid.setText("GM NÃO IDENTIFICADO");
        }
        list();
        loanButton.setDisable(false);
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
        addIconToTable();
        registerTableView.setItems(FXCollections.observableArrayList(registerService.findAll()));
    }

    public List<String> listAllGcm(){
        List<GcmModel> gcms = gcmService.findAll();
        ArrayList<String> val = new ArrayList<>();
        gcms.forEach((gm)->{
            val.add(String.valueOf(gm.getNumber()));
        });
        Collections.sort(val);//ordena lista
        return val;
    }

    //Retorna lista de nºs de registro a partir da categoria selecionada no combobox Equipamento/categoria
    // Método devolve a lista de equipamentos disponíveis para emprestimo
    @FXML
    public void searchByCategory(){
        equipmNumComboBox.getItems().clear(); //limpa combobox
        ArrayList<String> valuesSelec = new ArrayList<>();//equipamentos selecionados
        CategoryEnum category = equipmCatComboBox.getSelectionModel().getSelectedItem(); //retorna a categoria selecionada pelo usuário
        List<EquipmentModel> listCategoryAll = equipmentService.findByCategory(category); //Busca todos equipamentos da categoria selecionada
        listCategoryAll.forEach((cat)->{
            if (cat.getCategory().equals(CategoryEnum.VEICULO)) {
                valuesSelec.add(cat.getPrefix().toString());//Adiciona todos prefixo de todos itens da categoria VEICULO
            }else if(cat.getCategory().equals(CategoryEnum.CAMERA_CORPORAL)){
                valuesSelec.add(cat.getSerie().toString());//Adiciona todos nºs de serie de todos itens da categoria CAMERA CORPORAL
            }else{
                valuesSelec.add(cat.getRegistrationNumber().toString());//nº patrimonio de todos itens da categorias restantes
            } 
        });
        //Trecho que remove equipamentos emprestados das opções disponíveis no equipmNumComboBox 
        List<RegisterModel> listRegisterAll =registerService.findAll();//Busca todos as cautelas
        if (listCategoryAll.size()!=0) {//Entra no if se lista de equipamentos da mesma categoria não for vazia
            if(listRegisterAll.size()!=0){//Entra no if se lista de cautelas não for vazia
                for (var i=0; listCategoryAll.size()>i; i++) {
                    for (var z=0; listRegisterAll.size()>z; z++) {
                        if(listRegisterAll.get(z).getEquipment().getId()==listCategoryAll.get(i).getId()){
                            System.out.println("objeto emprestado de numero:"+listCategoryAll.get(i).getRegistrationNumber().toString());
                            //remove somente os equipamentos que foram emprestados
                            valuesSelec.remove(listCategoryAll.get(i).getRegistrationNumber().toString());
                        }
                    }
                }
            }
        }
        equipmNumComboBox.getItems().addAll(valuesSelec);
    }

    //Adiciona icone/botão de devolução na última coluna da Tabela
    private void addIconToTable() {
        TableColumn<RegisterModel, String> editColumn = new TableColumn<>();

        Callback<TableColumn<RegisterModel, String>, TableCell<RegisterModel, String>> cellFactory = (TableColumn<RegisterModel, String> param) -> {
            // make cell containing buttons
            final TableCell<RegisterModel, String> cell = new TableCell<RegisterModel, String>() {
                
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setGraphic(null);
                        setText(null);

                    } else {

                        FontIcon icon = new FontIcon(FontAwesomeRegular.ARROW_ALT_CIRCLE_DOWN);
                        //insere dica de ajuda no ícone 
                        Tooltip tooltip = new Tooltip("Devolver\n material");
                        tooltip.setStyle(
                            "-fx-font: normal bold 10 Langdon; "
                            + "-fx-base: #AE3522; "
                            + "-fx-text-fill: orange;"
                        );
                        Tooltip.install(icon, tooltip);
                        icon.setCursor(javafx.scene.Cursor.HAND);//apresenta como clicavel o icone
                        icon.setIconSize(20);//tamanho icone
                        icon.setIconColor(javafx.scene.paint.Color.BLUE);//cor icone
                        setGraphic(icon);//insere icone na coluna 
                        icon.setOnMouseClicked((MouseEvent event) -> {//aciona com o clic do mouse
                            RegisterModel data = getTableView().getItems().get(getIndex());
                            captureTag(data);
                        });
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);//centraliza itens na célula
            return cell;
        };
        editColumn.setText("Devolve");
        editColumn.setCellFactory(cellFactory);
        registerTableView.getColumns().add(editColumn);
    }
    //lê cartão para devolução
    public void captureTag(RegisterModel register){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Informação");
        alert.setHeaderText("Leitura habilitada");
        alert.setContentText("Aproxime ou encoste o cartão no leitor" );

        alert.show();
        // Executa a operação de forma assíncrona em uma nova thread
        new Thread(() -> {
            String result = UsbMonitor.monitorarUSB(); // Leitura do cartão RFID de forma síncrona

            // Atualiza a interface gráfica na thread da aplicação
            Platform.runLater(() -> {//verifica se tag é a mesma que a do banco
                alert.close();
                if(result.equals(register.getGcm().getTag())){
                    disassociate(register);
                }else{
                    //aviso não coincide regitro
                }
            });
        }).start();
    }
    //desassocia equipamento de GCM
    public void disassociate(RegisterModel registerM){//pegar o register que vem do captureTag e slavar no historic

        registerM.setStatus("Devolvido");
        historicService.createHistoric(registerM);//registra no historico a devolução de material
        registerService.delete(registerM.getId());//desvincula GCM e Equipamento
        list();
    }
    //controla habilitação de combobox e botões
    public void onKeyRelesead(){
        gcmTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null& !newValue.isEmpty()){
                equipmCatComboBox.setDisable(false);
                equipmNumComboBox.setDisable(false);
            }   
            else{
                equipmCatComboBox.setDisable(true);
                equipmNumComboBox.setDisable(true);
            }
        });

        equipmNumComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if(newValue!=null)
                loanButton.setDisable(false);
            else
                loanButton.setDisable(true);
        });
    }

    public void clear(){
        gcmTF.clear();
        equipmCatComboBox.getSelectionModel().clearSelection();
        equipmNumComboBox.getSelectionModel().clearSelection();
        noteText.clear();
        resultRfid.setText("Resultado RFID");
        onKeyRelesead();
    }
}
