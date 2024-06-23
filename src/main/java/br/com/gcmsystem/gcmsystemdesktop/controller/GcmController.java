package br.com.gcmsystem.gcmsystemdesktop.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import br.com.gcmsystem.gcmsystemdesktop.model.GcmModel;
import br.com.gcmsystem.gcmsystemdesktop.service.GcmService;
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
    private SerialPort usbPort;
    // private char[]tag = new char[10];
    private Integer id;
    private GcmModel item;
    private String statusTagField;
    private String statusTagButton = "Ligar Leitor";
    private volatile boolean deviceConnected = false;
    private volatile boolean leituraHabilitada = false;
    private String tagCaptured;
    

    @Autowired
    private GcmService gcmService;

    @FXML
    private TextField idField, numberField, nameField, emailField, searchComField, tagField;
    @FXML
    private Button saveButton, updateButton, deleteButton, clearButton, tagButton, stopCom;
    @FXML
    private Label lblTotal, lblWarning;
    
    @FXML
    private TableView<GcmModel> gcmTableView;

    @FXML
    private TableColumn<GcmModel, String>numberColumn, nomeColumn, emailColumn, tagColumn;

    public void initialize() {
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number")); //id: atributo do objeto GcmModel
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("name"));//name: atributo do objeto GcmModel
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
        tagButton.setText(statusTagButton);
        tagButton.setOnAction(event-> toogleLeitura());
        monitorarUSB();
        searchComField.setText(statusTagField);        
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
            gcmModel.setTag(tagCaptured);
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
        item.setTag(tagCaptured);
        gcmService.save(item);
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

    public void monitorarUSB() {
        new Thread(() -> {
            while (true) {
                if (usbPort == null || !usbPort.isOpen()) {
                    SerialPort[] portNames = SerialPort.getCommPorts();//retorna lista de portas disponíveis
                    for(SerialPort portName: portNames){
                        if(portName.getProductID()==29987){//Nº Product ID do ESP32
  
                            usbPort = SerialPort.getCommPort(portName.getSystemPortName()); //identifica e configura a porta
                            System.out.println("Serial Number: "+portName.getSerialNumber());
                            usbPort.setBaudRate(9600);
                            usbPort.setNumDataBits(8);
                            usbPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
                            usbPort.setParity(SerialPort.NO_PARITY);
                        }
                    }

                    if (usbPort.openPort()) {
                        System.out.println("Porta serial aberta com sucesso.");
                        deviceConnected =true;
                        Platform.runLater(()->{
                            tagButton.setDisable(false);
                            searchComField.setText(usbPort.getSystemPortName());
                            statusTagField=usbPort.getSystemPortName();
                        });

                        usbPort.addDataListener(new SerialPortDataListener() {
                            @Override
                            public int getListeningEvents() {
                                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE | SerialPort.LISTENING_EVENT_PORT_DISCONNECTED;
                            }

                            @Override
                            public void serialEvent(SerialPortEvent event) {
                                if (event.getEventType() == SerialPort.LISTENING_EVENT_PORT_DISCONNECTED) {
                                    System.out.println("Módulo ESP32+RFID desconectado.");
                                    deviceConnected = false;
                                    usbPort.closePort();
                                    Platform.runLater(()-> {
                                        tagButton.setDisable(true);
                                        searchComField.setText("Sem conexão");
                                    });
                                } else if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                                    if(leituraHabilitada){
                                        System.out.println("Módulo disponível para leitura");
                                        byte[] readBuffer = new byte[usbPort.bytesAvailable()];

                                        System.out.println("Bytes no Buffer: "+readBuffer.length);
                                        if (readBuffer.length==10) {
                                            int numRead= usbPort.readBytes(readBuffer, 10);
                                            System.out.println("Leitura de " + numRead + " bytes: " + new String(readBuffer));
                                            tagCaptured = new String(readBuffer).trim();
                                            Platform.runLater(()->tagField.setText(tagCaptured));

                                        }else{usbPort.closePort();}
                                        
                                    }
                                }
                            }
                        });
                    } else {
                        System.err.println("Falha ao abrir a porta serial.");
                        deviceConnected = false;
                        Platform.runLater(()-> tagButton.setDisable(true));
                    }
                }

                try {
                    Thread.sleep(2000); // Verifica a cada 2 segundos
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void toogleLeitura() {
        if (deviceConnected) {
            leituraHabilitada = !leituraHabilitada; // Alterna o estado de leitura
            if (leituraHabilitada) {
                System.out.println("Leitura do cartão RFID habilitada.");
                statusTagButton ="Desligar Leitor";
                tagButton.setText(statusTagButton);
            } else {
                System.out.println("Leitura do cartão RFID desabilitada.");
                statusTagButton = "Ligar Leitor";
                tagButton.setText(statusTagButton);
            }
        } else {
            System.out.println("Dispositivo não está conectado.");
        }
    }
    //     // Lê cartão
    //     usbPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
    //     InputStream in = usbPort.getInputStream();
    //     try {
    //         for (int j =0; j<10;++j){
    //             tag[j]=(char)in.read();
    //             in.close();
    //         }
    //         System.out.println(new String(tag)); //resultado da leitura
    //     } catch (Exception e) {
    //         e.printStackTrace(); 
    //     }
    //     usbPort.closePort();
    // }
}
