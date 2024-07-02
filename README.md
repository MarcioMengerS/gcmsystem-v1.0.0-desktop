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
Para empacotar o código compilado de acordo com o empacotamento escolhido em formato JAR, no terminal bash digitar: <b>mvn package</b>. Nesse processo o MAVEN realiza a compilação, executa os testes, empacota o código, apresenta os resultados e na pasta target cria o arquivo **[gcmsystemdesktop-0-0-1-SNAPSHOT](target/gcmsystemdesktop-0.0.1-SNAPSHOT.jar)**. Outra forma de buildar o projeto é: ./mvn spring-boot:run. Alternativamente, pode construir o arquivo JAR ./mvn clean package e depois executá-lo, da seguinte maneira: java -jar destino/gs-validating-form-input-0.1.0.jar  
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

#### Testar Pacote JAR
Para testar o pacote criado pelo tópico anterior(BUILD), no terminal bash digitar a linha de comando abaixo, mas somente após criação do arquivo.jar:  
**java -jar target/gcmsystemdesktop-0.0.1-SNAPSHOT.jar**
>Dia 29/06/2024 apresentou o seguinte aviso:   
WARNING: Unsupported JavaFX configuration: classes were loaded from 'unnamed module @298e7f7b'  

>Dia 02/07/2024 apresentou o seguinte aviso:  
WARNING: Unsupported JavaFX configuration: classes were loaded from 'unnamed module @54c25dd0'