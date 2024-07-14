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

    public void setMainApp(GcmsystemdesktopApplication mainApp) {
        this.mainapp = mainApp;
    }

    @FXML
    private void initialize() {
        equipamentoButton.setDisable(true);//disabilita na inicialização pois é já está selecionado
        equipamentoButton.setOnAction(event -> {
            mainapp.showEquipamento();

            equipamentoButton.setDisable(true);
            gcmButton.setDisable(false);
            registroButton.setDisable(false);

        });

        gcmButton.setOnAction(event -> {
            mainapp.showGcm();

            gcmButton.setDisable(true);
            equipamentoButton.setDisable(false);
            registroButton.setDisable(false);
        });

        registroButton.setOnAction(event -> {
            mainapp.showRegistro();
            
            registroButton.setDisable(true);
            equipamentoButton.setDisable(false);
            gcmButton.setDisable(false);
        });
    }
}
