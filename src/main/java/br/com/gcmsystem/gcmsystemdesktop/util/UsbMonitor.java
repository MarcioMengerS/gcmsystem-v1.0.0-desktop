package br.com.gcmsystem.gcmsystemdesktop.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public class UsbMonitor {
    private static SerialPort usbPort;
    @SuppressWarnings("unused")
    private static boolean deviceConnected = false;
    private static boolean leituraHabilitada = false;
    private static boolean leituraConcluida = true;
    private static ScheduledExecutorService scheduler;
    private static final Object lock = new Object();
    private static String readData= "";
    // Método assíncrono
    public static Future<String> monitorarUSBAsync() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        return executor.submit(() -> monitorarUSB());
    }
  
    // Método síncrono
    public static String monitorarUSB() {

        if (!leituraConcluida) {
            System.out.println("Leitura já está em andamento.");
            return "Leitura já está em andamento.";
        }

        leituraHabilitada = true;
        leituraConcluida = false;
        scheduler = Executors.newScheduledThreadPool(1);

        new Thread(() -> {
            while (!leituraConcluida) {
                if (usbPort == null || !usbPort.isOpen()) {
                    SerialPort[] portNames = SerialPort.getCommPorts();
                    for (SerialPort portName : portNames) {
                        if (portName.getProductID() == 29987) {//numero do productId do Esp32
                            usbPort = SerialPort.getCommPort(portName.getSystemPortName());
                            System.out.println("Serial Number: " + portName.getSerialNumber());
                            usbPort.setBaudRate(9600);
                            usbPort.setNumDataBits(8);
                            usbPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
                            usbPort.setParity(SerialPort.NO_PARITY);
                        }
                    }

                    if (usbPort.openPort()) {
                        System.out.println("Porta serial aberta com sucesso.");
                        deviceConnected = true;

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

                                } else if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                                    if (leituraHabilitada) {
                                        System.out.println("Módulo disponível para leitura");
                                        byte[] readBuffer = new byte[usbPort.bytesAvailable()];

                                        System.out.println("Bytes no Buffer: " + readBuffer.length);
                                        if (readBuffer.length == 10) {
                                            int numRead = usbPort.readBytes(readBuffer, 8);
                                            readData = new String(readBuffer).trim();
                                            System.out.println("Leitura de " + numRead + " bytes: " + readData);

                                            leituraHabilitada = false;
                                            leituraConcluida = true;
                                            usbPort.closePort();
                                            System.out.println("Leitura concluída e porta serial fechada.");
                                            scheduler.shutdownNow();
                                            synchronized (lock) {
                                                lock.notify();
                                            }
                                        }
                                    }
                                }
                            }
                        });

                        scheduler.schedule(() -> {
                            if (leituraHabilitada) {
                                System.out.println("Tempo limite de 5 segundos atingido. Encerrando leitura.");
                                readData ="Tempo excedido (5s.)";
                                leituraHabilitada = false;
                                leituraConcluida = true;
                                if (usbPort.isOpen()) {
                                    usbPort.closePort();
                                }
                                System.out.println("Porta serial fechada após timeout.");
                                synchronized (lock) {
                                    lock.notify();
                                }
                            }
                        }, 5, TimeUnit.SECONDS); //definido tempo de 5 segundos de leitura do cratão
                    } else {
                        System.err.println("Falha ao abrir a porta serial.");
                        usbPort.closePort();
                        deviceConnected = false;
                        leituraConcluida = true;
                        readData ="Leitor desconectado";
                        synchronized (lock) {
                            lock.notify();
                        }
                    }
                }

                try {
                    System.out.println(".");
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("Monitoramento USB encerrado.");
        }).start();

        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

        String resultadoFinal = readData;
        readData = "";//limpa variavel garantir proxima chamada
        return resultadoFinal;
    }
}