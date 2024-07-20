package br.com.gcmsystem.gcmsystemdesktop.controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import br.com.gcmsystem.gcmsystemdesktop.enums.CategoryEnum;
import br.com.gcmsystem.gcmsystemdesktop.model.Historic;
import br.com.gcmsystem.gcmsystemdesktop.service.HistoricService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

@Controller
public class HistoricController implements Initializable{

    @Autowired
    private HistoricService historicService;

    @FXML
    private TableView<Historic> historicTV;
    @FXML
    private TableColumn<Historic, Integer> gcmNumberC;
    @FXML
    private TableColumn<Historic, String> gcmNameC, statusC, infoC;
    @FXML
    private TableColumn<Historic, LocalDate> dateC;
    @FXML
    private TableColumn<Historic, LocalTime> timeC;
    @FXML
    private TableColumn<Historic, CategoryEnum> equipCategoryC;
    @FXML
    private TableColumn<Historic, Integer> equipRegistrationNumberC, prefixC;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        list();
    }

    public void list(){
        statusC.setCellValueFactory(new PropertyValueFactory<>("status"));
        equipCategoryC.setCellValueFactory(new PropertyValueFactory<>("equipCategory"));
        equipRegistrationNumberC.setCellValueFactory(new PropertyValueFactory<>("equipRegistrationNumber"));
        prefixC.setCellValueFactory(new PropertyValueFactory<>("prefix"));
        dateC.setCellValueFactory(new PropertyValueFactory<>("date"));
        timeC.setCellValueFactory(new PropertyValueFactory<>("time"));
        gcmNumberC.setCellValueFactory(new PropertyValueFactory<>("gcmNumber"));
        gcmNameC.setCellValueFactory(new PropertyValueFactory<>("gcmName"));
        
        //Formata data para mostrar na tabela no formato dd/mm/yyyy
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dateC.setCellFactory(new Callback<TableColumn<Historic,LocalDate>,TableCell<Historic,LocalDate>>() {
            @Override
            public TableCell<Historic, LocalDate> call(TableColumn<Historic, LocalDate> param) {
                return new TableCell<Historic, LocalDate>() {
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
        //Modifica cor do atributo STATUS = Devolvido | Emprestado
        statusC.setCellFactory(new Callback<TableColumn<Historic, String>, TableCell<Historic, String>>() {
            @Override
            public TableCell<Historic, String> call(TableColumn<Historic, String> param) {
                return new TableCell<Historic, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item);
                            if ("Emprestado".equals(item)) {
                                setTextFill(javafx.scene.paint.Color.ORANGE);
                            } else if ("Devolvido".equals(item)) {
                                setTextFill(javafx.scene.paint.Color.GREEN);
                            } else {
                                setTextFill(javafx.scene.paint.Color.BLACK);
                            }
                        }
                    }
                };
            }
        });
        //insere icone para ver historico completo
        infoC.setCellFactory(new Callback<TableColumn<Historic, String>, TableCell<Historic, String>> () {
            @Override
            public TableCell<Historic, String> call(TableColumn<Historic, String> param) {
                return new TableCell<Historic, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        }else{                        
                            FontIcon iconEye = new FontIcon(FontAwesomeSolid.EYE);
                            iconEye.setCursor(javafx.scene.Cursor.HAND);//apresenta como clicavel o icone
                            iconEye.setIconSize(18);//tamanho icone
                            iconEye.setIconColor(javafx.scene.paint.Color.GREEN);//cor icone
                            setGraphic(iconEye);//insere icone na célula
                        }
                    }
                };
            }
        });
        historicTV.setItems(FXCollections.observableArrayList(historicService.findAll()));
    }

}
