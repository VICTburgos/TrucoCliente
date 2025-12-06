package trucoarg.personajesDosJugadores;

import trucoarg.personajesSolitario.CartaSolitario;
import trucoarg.personajesSolitario.MazoSolitario;
import trucoarg.utiles.ColisionesDosJugadores;

public class JuegoTruco {

    private JugadorBase jugador1;
    private JugadorBase jugador2;
    private final MazoSolitario mazo;
    private final ColisionesDosJugadores colisiones;
    private boolean jugador1EsMano = false;

    private final Truco gestorTruco = new Truco();
    private final Envido gestorEnvido = new Envido();

    private boolean envidoYaResuelto = false;

    // 🆕 PUNTOS PARA GANAR
    private int puntosParaGanar = 15;

    private int manoOriginal;
    private int turnoActual = 1;
    private int tiradaActual = 1;
    private int rondasGanadasJ1 = 0;
    private int rondasGanadasJ2 = 0;
    private boolean manoTerminada = false;
    private CartaSolitario cartaJugadaJ1 = null;
    private CartaSolitario cartaJugadaJ2 = null;

    private int puntosJ1 = 0;
    private int puntosJ2 = 0;

    // 🆕 CONSTRUCTORES
    public JuegoTruco(int puntosParaGanar) {
        this.puntosParaGanar = puntosParaGanar;
        mazo = new MazoSolitario();
        colisiones = new ColisionesDosJugadores();
        iniciarNuevaMano();
        System.out.println("🎮 Juego iniciado a " + puntosParaGanar + " puntos");
    }

    public JuegoTruco() {
        this(15); // Por defecto 15 puntos
    }

    public void iniciarNuevaMano() {
        System.out.println("\n===== iniciarNuevaMano() llamado =====");

        mazo.reiniciarMazo();
        colisiones.reset();

        jugador1EsMano = !jugador1EsMano;
        manoOriginal = jugador1EsMano ? 1 : 2;

        jugador1 = new JugadorBase( 1, jugador1EsMano);
        jugador2 = new JugadorBase( 2, !jugador1EsMano);

        manoTerminada = false;
        tiradaActual = 1;
        rondasGanadasJ1 = 0;
        rondasGanadasJ2 = 0;
        turnoActual = manoOriginal;
        cartaJugadaJ1 = null;
        cartaJugadaJ2 = null;

        System.out.println("Mano original: J" + manoOriginal);

        gestorTruco.reset();
        gestorEnvido.reset();
        envidoYaResuelto = false;
    }

    public boolean jugarCarta(int jugador, CartaSolitario carta) {
        System.out.println("\n=== jugarCarta() llamado ===");
        System.out.println("Jugador que intenta jugar: J" + jugador);
        System.out.println("Turno actual: J" + turnoActual);
        System.out.println("Mano terminada: " + manoTerminada);
        System.out.println("Carta ya jugada: " + carta.getYaJugadas());
        System.out.println("Hay canto pendiente: " + hayCantoPendiente());

        if (manoTerminada) {
            System.out.println("❌ RECHAZADO: Mano terminada");
            return false;
        }

        if (jugador != turnoActual) {
            System.out.println("❌ RECHAZADO: No es turno de J" + jugador + " (turno actual: J" + turnoActual + ")");
            return false;
        }

        if (carta.getYaJugadas()) {
            System.out.println("❌ RECHAZADO: Carta ya fue jugada");
            return false;
        }

        carta.setYaJugadas(true);

        if (jugador == 1 && cartaJugadaJ1 == null) {
            cartaJugadaJ1 = carta;
            turnoActual = 2;
            System.out.println("✅ J1 jugó carta (nivel=" + carta.getNIVEL() + "). Nuevo turno: J2");
            return true;
        }

        if (jugador == 2 && cartaJugadaJ2 == null) {
            cartaJugadaJ2 = carta;
            turnoActual = 1;
            System.out.println("✅ J2 jugó carta (nivel=" + carta.getNIVEL() + "). Nuevo turno: J1");
            return true;
        }

        System.out.println("❌ RECHAZADO: Condición desconocida");
        return false;
    }

    public int procesarTirada() {
        if (manoTerminada) return -1;
        if (cartaJugadaJ1 == null || cartaJugadaJ2 == null) return -1;

        System.out.println("\n--- procesarTirada() Ronda " + tiradaActual + " ---");

        int resultado;
        if (cartaJugadaJ1.getNIVEL() > cartaJugadaJ2.getNIVEL())
            resultado = 1;
        else if (cartaJugadaJ2.getNIVEL() > cartaJugadaJ1.getNIVEL())
            resultado = 2;
        else
            resultado = 0;

        if (resultado == 1) {
            System.out.println("Gana tirada J1");
            rondasGanadasJ1++;
            turnoActual = 1;
        } else if (resultado == 2) {
            System.out.println("Gana tirada J2");
            rondasGanadasJ2++;
            turnoActual = 2;
        } else {
            System.out.println("Parda en la tirada");
            turnoActual = manoOriginal;
        }

        int ganadorMano = verificarFinDeMano();
        if (ganadorMano != -1) {
            manoTerminada = true;

            int puntosTruco = gestorTruco.getPuntos();
            if (ganadorMano == 1)
                puntosJ1 += puntosTruco;
            else
                puntosJ2 += puntosTruco;

            System.out.println("\n***** FIN DE MANO *****");
            System.out.println("Ganador de la mano: J" + ganadorMano);
            System.out.println("Puntos -> J1: " + puntosJ1 + " | J2: " + puntosJ2);

            // 🆕 VERIFICAR VICTORIA
            if (hayGanador()) {
                System.out.println("🏆 ¡HAY UN GANADOR! J" + getGanadorFinal());
            }

            return ganadorMano;
        }

        tiradaActual++;
        cartaJugadaJ1 = null;
        cartaJugadaJ2 = null;

        System.out.println("Siguiente tirada: " + tiradaActual + ". Turno: J" + turnoActual);
        return resultado;
    }

    private int verificarFinDeMano() {
        if (rondasGanadasJ1 == 2) return 1;
        if (rondasGanadasJ2 == 2) return 2;

        if (tiradaActual == 3) {
            if (rondasGanadasJ1 > rondasGanadasJ2) return 1;
            if (rondasGanadasJ2 > rondasGanadasJ1) return 2;
            return manoOriginal;
        }

        return -1;
    }

    public boolean cantar(int jugador, String canto) {
        if (manoTerminada) return false;

        if (jugador != turnoActual) {
            System.out.println("No es turno de J" + jugador);
            return false;
        }

        return gestorTruco.cantar(jugador, canto);
    }

    public boolean cantarEnvido(int jugador, String tipoEnvido) {
        if (manoTerminada) return false;

        if (tiradaActual > 1) {
            System.out.println("El envido solo se puede cantar en la primera tirada");
            return false;
        }

        if (envidoYaResuelto) {
            System.out.println("El envido ya fue resuelto en esta mano");
            return false;
        }

        if (gestorEnvido.estaEsperandoRespuesta()) {
            int jugadorQueDebeResponder = gestorEnvido.getJugadorQueDebeResponder();
            if (jugador != jugadorQueDebeResponder) {
                System.out.println("No puedes subir el envido, no es tu turno para responder");
                return false;
            }
        } else {
            if (jugador != turnoActual) {
                System.out.println("No es turno de J" + jugador);
                return false;
            }
        }

        return gestorEnvido.cantar(jugador, tipoEnvido);
    }

    public int responderCanto(int jugador, boolean quiero) {
        int resultado = gestorTruco.responder(jugador, quiero);

        if (resultado > 0) {
            manoTerminada = true;

            if (resultado == 1)
                puntosJ1 += gestorTruco.getPuntos();
            else
                puntosJ2 += gestorTruco.getPuntos();

            // 🆕 VERIFICAR VICTORIA
            if (hayGanador()) {
                System.out.println("🏆 ¡HAY UN GANADOR! J" + getGanadorFinal());
            }
        }

        return resultado;
    }

    public int responderEnvido(int jugador, boolean quiero) {
        int resultado = gestorEnvido.responder(jugador, quiero);

        if (resultado == 0) {
            // Envido aceptado: se deben comparar los envidos
            int ganador = gestorEnvido.getJugadorQueCanto();
            int puntosEnvido = gestorEnvido.getPuntos();

            if (ganador == 1) {
                puntosJ1 += puntosEnvido;
                System.out.println("J1 suma " + puntosEnvido + " puntos por envido QUERIDO");
            } else {
                puntosJ2 += puntosEnvido;
                System.out.println("J2 suma " + puntosEnvido + " puntos por envido QUERIDO");
            }

            gestorEnvido.reset();
            envidoYaResuelto = true;

            // 🆕 VERIFICAR VICTORIA
            if (hayGanador()) {
                System.out.println("🏆 ¡HAY UN GANADOR! J" + getGanadorFinal());
            }

        } else if (resultado > 0) {
            // Envido rechazado (NO QUIERO)
            int puntosRechazo = gestorEnvido.getPuntosRechazo();

            if (resultado == 1) {
                puntosJ1 += puntosRechazo;
                System.out.println("J1 suma " + puntosRechazo + " punto (envido NO QUERIDO)");
            } else {
                puntosJ2 += puntosRechazo;
                System.out.println("J2 suma " + puntosRechazo + " punto (envido NO QUERIDO)");
            }

            gestorEnvido.reset();
            envidoYaResuelto = true;

            if (hayGanador()) {
                System.out.println("🏆 ¡HAY UN GANADOR! J" + getGanadorFinal());
            }
        }

        return resultado;
    }

    public boolean hayGanador() {
        return puntosJ1 >= puntosParaGanar || puntosJ2 >= puntosParaGanar;
    }

    public void terminarManoAlMazo() {
        manoTerminada = true;
    }

    // 🆕 MÉTODOS PARA AGREGAR PUNTOS DIRECTAMENTE
    public void agregarPuntosJ1(int puntos) {
        puntosJ1 += puntos;
    }

    public void agregarPuntosJ2(int puntos) {
        puntosJ2 += puntos;
    }

    public int getGanadorFinal() {
        if (puntosJ1 >= puntosParaGanar) return 1;
        if (puntosJ2 >= puntosParaGanar) return 2;
        return -1;
    }

    public int getPuntosParaGanar() {
        return puntosParaGanar;
    }

    public boolean hayCantoPendiente() {
        return gestorTruco.estaEsperandoRespuesta() || gestorEnvido.estaEsperandoRespuesta();
    }

    public int getJugadorQueDebeResponder() {
        if (gestorTruco.estaEsperandoRespuesta()) {
            return gestorTruco.getJugadorQueDebeResponder();
        }
        if (gestorEnvido.estaEsperandoRespuesta()) {
            return gestorEnvido.getJugadorQueDebeResponder();
        }
        return -1;
    }

    public boolean isManoTerminada() {
        return manoTerminada;
    }

    public int getTurnoActual() {
        return turnoActual;
    }

    public int getTiradaActual() {
        return tiradaActual;
    }

    public int getPuntosJ1() {
        return puntosJ1;
    }

    public int getPuntosJ2() {
        return puntosJ2;
    }

    public JugadorBase getJugador1() {
        return jugador1;
    }

    public JugadorBase getJugador2() {
        return jugador2;
    }

    public void reiniciarManoSiCorresponde() {
        if (manoTerminada) {
            iniciarNuevaMano();
        }
    }

    public boolean puedeJugar(int jugador) {
        if (manoTerminada) return false;
        return turnoActual == jugador;
    }

    public Truco getGestorTruco() {
        return gestorTruco;
    }

    public Envido getGestorEnvido() {
        return gestorEnvido;
    }

    public boolean isEnvidoYaResuelto() {
        return envidoYaResuelto;
    }
}
