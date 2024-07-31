# GCMSYSTEM
## Sistema de gerenciamento voltado a Guarda Civil Metropolitana
### Versão 1.0.0
Manter frequência de BUILD para acompanhar se o código está rodando perfeitamente também é um dos objetivos.  
### Bibliotecas Importadas
#### Manuais
Pacotes adicionados manualmente:
* java.time.LocalDate - manipular data
* java.time.LocalDateTime - manipular data e hora

### Build
Para empacotar o código compilado de acordo com o empacotamento escolhido em formato JAR, no terminal bash digitar: **mvn package**. Nesse processo o MAVEN realiza a compilação, executa os testes, empacota o código, apresenta os resultados e na pasta target cria o arquivo [gcmsystemdesktop-0-0-1-SNAPSHOT](target/gcmsystemdesktop-0.0.1-SNAPSHOT.jar). Outra forma de buildar o projeto é: ./mvn spring-boot:run. Alternativamente, pode construir o arquivo JAR ./mvn clean package e depois executá-lo, da seguinte maneira: java -jar destino/gs-validating-form-input-0.1.0.jar  
Resultado do building realizado com sucesso:  
 
**Utilizado o comando "mvn clean package" no build abaixo:**  
>BUILD SUCCESS 
>Total time:  31.449 s  
>Finished at: 2024-06-29T12:07:24-03:00  
>Tests run: 1, Failures: 0, Errors: 0, Skipped: 0 

>BUILD SUCCESS 
>Total time:  36.879 s  
>Finished at: 2024-07-02T15:29:24-03:00  
>Tests run: 1, Failures: 0, Errors: 0, Skipped: 0  

>BUILD SUCCESS 
>Total time:  41.223 s 
>Finished at: 2024-07-14T10:37:40-03:00 
>Tests run: 1, Failures: 0, Errors: 0, Skipped: 0  

### Tarefas:
- ~~funcionalidade: atualização de agente da Guarda Municipal - 25/07/2024~~
- ~~funcionalidade: filtros de pesquisas de Guardas Municipais - 28/07/2024~~
- funcionalidade: filtrar equipamento por localização - 28/07/2024
- funcionalidade: inserir histórico de cautelas na guia equipamento do GCM - 28/07/2024
- ~~Inserir classe enum para escolha de gênero: MASCULINO e FEMININO - 31/07/2024~~

#### Testar Pacote JAR
Para testar o pacote criado pelo tópico anterior(BUILD), no terminal bash digitar a linha de comando abaixo, mas somente após criação do arquivo.jar:  
**java -jar target/gcmsystemdesktop-0.0.1-SNAPSHOT.jar**
>Dia 29/06/2024 apresentou o seguinte aviso:   
WARNING: Unsupported JavaFX configuration: classes were loaded from 'unnamed module @298e7f7b'  

>Dia 02/07/2024 apresentou o seguinte aviso:  
WARNING: Unsupported JavaFX configuration: classes were loaded from 'unnamed module @54c25dd0'

>Dia 14/07/2024 apresentou o seguinte aviso:  
WARNING: Unsupported JavaFX configuration: classes were loaded from 'unnamed module @54c25dd0'  

Ao executar aplicativo, usar o comando Maven para garantir que todas as dependências, incluindo o ControlsFX, sejam carregadas corretamente:   
    1. Limpe e Compile: **mvn clean install**  
    2. Execute o Projeto JavaFX:  **mvn javafx:run** 

>[INFO] BUILD SUCCESS  
[INFO] Total time:  55.722 s  
[INFO] Finished at: 2024-07-14T16:55:09-03:00 

>[INFO] BUILD SUCCESS  
[INFO] Total time:  05:17 h  
[INFO] Finished at: 2024-07-20T17:05:34-03:00

>[INFO] BUILD SUCCESS  
[INFO] Total time:  57.516 s  
[INFO] Finished at: 2024-07-25T19:49:03-03:00  

>[INFO] BUILD SUCCESS  
[INFO] Total time:  50.930 s  
[INFO] Finished at: 2024-07-28T08:14:24-03:00

>[INFO] BUILD SUCCESS  
[INFO] Total time:  48.102 s  
[INFO] Finished at: 2024-07-28T17:26:28-03:00  

### Desenvolvimento, alterações e configurações decorridas da atualização do sistema  
**Data:** 28/07/2024  
**Arquivo(s) modificados:**  
`GcmController.java e gcm.xml`  
&nbsp;&nbsp;&nbsp;&nbsp; Retirada de buttons, TextFields, Label e seus respectivos métodos(addListener(), selectedItem(), save(), update(), delete, clear(),   
searchTag()). Além de variável do tipo inteiro e do tipo GcmModel (id, item).  
`GcmDetailsController.java e details-gcm.fxml`  
&nbsp;&nbsp;&nbsp;&nbsp;Adicionado buttons, TextFields, Label, DatePickers. Modificados os métodos save(), getGcm(). Criado método update().  
`GcmModel.java`  
&nbsp;&nbsp;&nbsp;&nbsp;Adicionada as variáveis tag, emissionCarryWeapon, validityCarryWeapon, numberCarryWeapon dos tipos String e LocalDate.  
`GcmService.java`  
&nbsp;&nbsp;&nbsp;&nbsp; Modificado nome da variável user do tipo GcmModel para gm no metodo save().  
`application.properties`  
&nbsp;&nbsp;&nbsp;&nbsp; Modificado parâmetro de configuração do fuso horário do Banco de dados de UTC para America/Sao_Paulo.  

---  
**Data:** 28/07/2024    
**Arquivo(s) modificados:**  
`GcmController.java`  
&nbsp;&nbsp;&nbsp;&nbsp; Inserido método InsertIconX(), enumInString(), filterRadioButton, filterCombobox() e eventos no método initialize().
`GcmService.java e GcmRepository.java`  
&nbsp;&nbsp;&nbsp;&nbsp; Criação dos métodos findByStatus() e findByUnit().

---  
**Data:** 31/07/2024    
**Arquivo(s) modificados:**  
`GcmModel.java, GcmDetailsController.java e details-gcm.fxml`  
&nbsp;&nbsp;&nbsp;&nbsp;  Modificação de variáveis trabalhar com enum (GenderEnum.java)
`GenderEnum.java`  
&nbsp;&nbsp;&nbsp;&nbsp; Criação de Enum para Gênero
`UnitEnum.java`  
&nbsp;&nbsp;&nbsp;&nbsp; Organização das siglas da classe UnitEnum.java