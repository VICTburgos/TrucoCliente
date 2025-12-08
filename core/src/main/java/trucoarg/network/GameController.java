package trucoarg.network;

public interface GameController {
    void connect(int numPlayer);
    void start();
    void iniciarPartida(int puntos);
    void repartir(int jugador, int carta);
    void cartaJugada(int jugador, int idCarta);
    void actualizarTurno(int turno);
    void actualizarPuntos(int puntosJ1, int puntosJ2);
    void mostrarVictoria(int ganador);
    void limpiarMesa();
    void nuevaMano();

    void cantoRealizado(String tipoCanto, int jugador, String nombreCanto);
    void respuestaCanto(int jugador, String respuesta, int resultado);
    void jugadorAlMazo(int jugador);
    void actualizarBotones(String botonesVisibles);
    void volverAlMenu();

}
