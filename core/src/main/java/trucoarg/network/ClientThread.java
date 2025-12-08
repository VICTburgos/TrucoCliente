package trucoarg.network;


import com.badlogic.gdx.Gdx;

import java.io.IOException;
import java.net.*;

public class ClientThread extends Thread {

    private DatagramSocket socket;
    private int serverPort = 5555;
    private String ipServerStr = "255.255.255.255";
    private InetAddress ipServer;
    private boolean end = false;
    public GameController gameController;

    public ClientThread(GameController gameController) {
        try {
            this.gameController = gameController;
            ipServer = InetAddress.getByName(ipServerStr);
            socket = new DatagramSocket();
        } catch (SocketException | UnknownHostException e) {
//            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        do {
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
            try {
                socket.receive(packet);
                processMessage(packet);
            } catch (IOException e) {
//                throw new RuntimeException(e);
            }
        } while(!end);
    }

    private void processMessage(DatagramPacket packet) {
        String message = (new String(packet.getData())).trim();
        String[] parts = message.split(":");

        System.out.println("📨 Mensaje recibido: " + message);

        switch(parts[0]) {
            case "AlreadyConnected":
                System.out.println("Ya estas conectado");
                break;

            case "Connected":
                System.out.println("Conectado al servidor");
                this.ipServer = packet.getAddress();
                gameController.connect(Integer.parseInt(parts[1]));
                break;

            case "Full":
                System.out.println("Servidor lleno");
                this.end = true;
                break;

            case "Start":
                this.gameController.start();
                break;

            case "Iniciar_Partida":
                int puntos = Integer.parseInt(parts[1]);
                Gdx.app.postRunnable(() -> gameController.iniciarPartida(puntos));
                break;

            case "Repartir":
                int jugador = Integer.parseInt(parts[1]);
                int carta = Integer.parseInt(parts[2]);
                Gdx.app.postRunnable(() -> gameController.repartir(jugador, carta));
                break;

            case "CartaJugada":
                int jugadorQueJugo = Integer.parseInt(parts[1]);
                int idCartaJugada = Integer.parseInt(parts[2]);
                System.out.println(" ClientThread recibió: CartaJugada:" + jugadorQueJugo + ":" + idCartaJugada);
                Gdx.app.postRunnable(() -> gameController.cartaJugada(jugadorQueJugo, idCartaJugada));
                break;

            case "Turno":
                int turnoJugador = Integer.parseInt(parts[1]);
                Gdx.app.postRunnable(() -> gameController.actualizarTurno(turnoJugador));
                break;

            case "Puntos":
                int puntosJ1 = Integer.parseInt(parts[1]);
                int puntosJ2 = Integer.parseInt(parts[2]);
                Gdx.app.postRunnable(() -> gameController.actualizarPuntos(puntosJ1, puntosJ2));
                break;

            case "Victoria":
                int ganador = Integer.parseInt(parts[1]);
                Gdx.app.postRunnable(() -> gameController.mostrarVictoria(ganador));
                break;

            case "LimpiarMesa":
                System.out.println("🗑 Limpiando mesa para siguiente tirada");
                Gdx.app.postRunnable(() -> gameController.limpiarMesa());
                break;

            case "NuevaMano":
                System.out.println(" Nueva mano - Limpiando TODO");
                Gdx.app.postRunnable(() -> gameController.nuevaMano());
                break;

            case "CantoRealizado":
                String tipoCanto = parts[1]; // "truco" o "envido"
                int jugadorQueCanto = Integer.parseInt(parts[2]);
                String nombreCanto = parts[3];
                System.out.println(" Canto realizado: " + tipoCanto + " - J" + jugadorQueCanto + " - " + nombreCanto);
                Gdx.app.postRunnable(() ->
                    gameController.cantoRealizado(tipoCanto, jugadorQueCanto, nombreCanto));
                break;

            case "RespuestaCanto":
                int jugadorQueResponde = Integer.parseInt(parts[1]);
                String respuesta = parts[2]; // "quiero" o "noquiero"
                int resultado = Integer.parseInt(parts[3]);
                System.out.println(" Respuesta a canto: J" + jugadorQueResponde + " - " + respuesta + " - resultado=" + resultado);
                Gdx.app.postRunnable(() ->
                    gameController.respuestaCanto(jugadorQueResponde, respuesta, resultado));
                break;

            case "JugadorAlMazo":
                int jugadorAlMazo = Integer.parseInt(parts[1]);
                System.out.println(" J" + jugadorAlMazo + " se fue al mazo");
                Gdx.app.postRunnable(() ->
                    gameController.jugadorAlMazo(jugadorAlMazo));
                break;

            case "ActualizarBotones":
                String botonesVisibles = parts[1];
                System.out.println(" Recibido estado de botones: " + botonesVisibles);
                Gdx.app.postRunnable(() -> gameController.actualizarBotones(botonesVisibles));
                break;
            case "Disconnect":
                Gdx.app.postRunnable(() -> gameController.volverAlMenu());
                break;

            default:
                System.out.println(" Mensaje desconocido: " + parts[0]);
                break;
        }
    }

    public void sendMessage(String message) {
        byte[] byteMessage = message.getBytes();
        DatagramPacket packet = new DatagramPacket(byteMessage, byteMessage.length, ipServer, serverPort);
        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void terminate() {
        this.end = true;
        socket.close();
        this.interrupt();
    }
}
