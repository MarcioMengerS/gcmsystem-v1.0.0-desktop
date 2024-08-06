package br.com.gcmsystem.gcmsystemdesktop;

import java.io.IOException;
import java.util.logging.Logger;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import br.com.gcmsystem.gcmsystemdesktop.controller.HomeController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

@SpringBootApplication
public class GcmsystemdesktopApplication extends Application{
	private static final Logger logger = Logger.getLogger(GcmsystemdesktopApplication.class.getName());
	private double x, y;
	private ConfigurableApplicationContext springContext;
	private Stage primaryStage;
	private BorderPane rootLayout;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void init(){
		springContext = new SpringApplicationBuilder(GcmsystemdesktopApplication.class).run();
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.getIcons().add(new Image("images/gcm.png"));
		this.primaryStage.setTitle("GCMSystem v0.0.1");

		initRootLayout();
		showHome();
		showEquipamento();//inicia na tela de equipamentos
		logger.info("Aplicacao iniciada com sucesso");
	}

	@Override
	public void stop(){
		logger.info("Fechando a aplicacao");
		springContext.close();
	}

	public void initRootLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(GcmsystemdesktopApplication.class.getResource("/fxml/home.fxml"));
			loader.setControllerFactory(springContext::getBean);
			rootLayout = (BorderPane) loader.load();

			//drag it here
			rootLayout.setOnMousePressed(event -> {
				x = event.getSceneX();
				y = event.getSceneY();
			});
			rootLayout.setOnMouseDragged(event -> {
				primaryStage.setX(event.getScreenX() - x);
				primaryStage.setY(event.getScreenY() - y);
			});

			Scene scene = new Scene(rootLayout, 1050,600);// tela do app
			// primaryStage.setResizable(false);// desativa o botão maximizar
			primaryStage.setScene(scene);
			primaryStage.show();
			logger.info("Layout principal inicializado com sucesso");
		} catch (IOException e) {
			logger.severe("Erro ao inicializar o layout principal: " + e.getMessage());
			e.printStackTrace();
		}
    }
	public void showHome() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(GcmsystemdesktopApplication.class.getResource("/fxml/home.fxml"));
			loader.setControllerFactory(springContext::getBean);
			BorderPane homePane = loader.load();
			rootLayout.setLeft(homePane);

			HomeController controller = loader.getController();
			controller.setMainApp(this);
			logger.info("Mostrar HOME inicializado com sucesso");
		} catch (IOException e) {
			logger.severe("Erro ao inicializar mostrar HOME: " + e.getMessage());
			e.printStackTrace();
		}
    }

	public void showEquipamento() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(GcmsystemdesktopApplication.class.getResource("/fxml/equipamento.fxml"));
            loader.setControllerFactory(springContext::getBean);
            BorderPane equipamentoPane = (BorderPane) loader.load();
            rootLayout.setCenter(equipamentoPane);
			logger.info("Mostrar tela EQUIPAMENTO inicializado com sucesso");
        } catch (IOException e) {
			logger.severe("Erro ao inicializar mostrar tela EQUIPAMENTO: " + e.getMessage());
            e.printStackTrace();
        }
	}
	public void showGcm() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(GcmsystemdesktopApplication.class.getResource("/fxml/gcm.fxml"));
            loader.setControllerFactory(springContext::getBean);
            BorderPane gcmPane = loader.load();
            rootLayout.setCenter(gcmPane);
			logger.info("Mostrar tela GCM inicializado com sucesso");
        } catch (IOException e) {
			logger.severe("Erro ao inicializar mostrar tela GCM: " + e.getMessage());
            e.printStackTrace();
        }
	}

	public void showRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(GcmsystemdesktopApplication.class.getResource("/fxml/registro.fxml"));
            loader.setControllerFactory(springContext::getBean);
            BorderPane cautelaPane = loader.load();
            rootLayout.setCenter(cautelaPane);
			logger.info("Mostrar tela CAUTELA inicializado com sucesso");
        } catch (IOException e) {
			logger.severe("Erro ao inicializar mostrar tela CAUTELA: " + e.getMessage());
            e.printStackTrace();
        }
	}

    public void showHistoric() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(GcmsystemdesktopApplication.class.getResource("/fxml/historic.fxml"));
            loader.setControllerFactory(springContext::getBean);
            AnchorPane historicPane = loader.load();
            rootLayout.setCenter(historicPane);
			logger.info("Mostrar tela HISTORICO inicializado com sucesso");
        } catch (IOException e) {
			logger.severe("Erro ao inicializar mostrar tela HISTORICO: " + e.getMessage());
            e.printStackTrace();
        }
	}
}