package br.com.gcmsystem.gcmsystemdesktop.controller;

import org.springframework.stereotype.Component;

import br.com.gcmsystem.gcmsystemdesktop.GcmsystemdesktopApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

@Component
public class HomeController {

    private GcmsystemdesktopApplication mainapp;

    @FXML
    private Button equipamentoButton;
    @FXML
    private Button gcmButton;
    @FXML
    private Button registroButton;
    @FXML
    private Button historicButton;

    public void setMainApp(GcmsystemdesktopApplication mainApp) {
        this.mainapp = mainApp;
    }

    @FXML
    private void initialize() {
        equipamentoButton.setDisable(true);//disabilita na inicialização pois já está selecionado
        equipamentoButton.setOnAction(event -> {
            mainapp.showEquipamento();

            equipamentoButton.setDisable(true);
            gcmButton.setDisable(false);
            registroButton.setDisable(false);
            historicButton.setDisable(false);
        });

        gcmButton.setOnAction(event -> {
            mainapp.showGcm();

            gcmButton.setDisable(true);
            equipamentoButton.setDisable(false);
            registroButton.setDisable(false);
            historicButton.setDisable(false);
        });

        registroButton.setOnAction(event -> {
            mainapp.showRegistro();
            
            registroButton.setDisable(true);
            equipamentoButton.setDisable(false);
            gcmButton.setDisable(false);
            historicButton.setDisable(false);
        });

        historicButton.setOnAction(event -> {
            mainapp.showHistoric();
            
            historicButton.setDisable(true);
            equipamentoButton.setDisable(false);
            gcmButton.setDisable(false);
            registroButton.setDisable(false);
        });
    }
}
