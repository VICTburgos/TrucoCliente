package trucoarg.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import trucoarg.elementos.Imagen;
import trucoarg.network.ClientThread;
import trucoarg.network.GameController;
import trucoarg.personajesDosJugadores.JugadorBase;
import trucoarg.personajesSolitario.CartaSolitario;
import trucoarg.ui.Boton;
import trucoarg.ui.EntradaDosJugadores;
import trucoarg.utiles.CartasFinales;
import trucoarg.utiles.Configuracion;
import trucoarg.utiles.Recursos;
import trucoarg.utiles.Render;

import java.util.ArrayList;
import java.util.List;

/**
 * Pantalla del juego de Truco para 2 jugadores en modo red.
 * El SERVIDOR maneja toda la lógica del juego.
 * El CLIENTE solo dibuja y envía inputs del usuario.
 */
public class PantallaDosJugadores implements Screen, GameController {

    // ========== GRÁFICOS ==========
    private Imagen fondo;
    private SpriteBatch batch;

    private BitmapFont fuente;
    private BitmapFont fuenteVictoria;
    private BitmapFont fuenteCanto;

    // ========== JUGADORES ==========
    private JugadorBase jugador1;
    private JugadorBase jugador2;
    private int miNumeroJugador = 0; // 1 o 2 (asignado por el servidor)


    // ========== ESTADO DEL JUEGO (actualizado por el servidor) ==========
    private int puntosJ1 = 0;
    private int puntosJ2 = 0;
    private int puntosParaGanar = 15;
    private int turnoActual = 1;
    private boolean juegoTerminado = false;

    // ========== CARTAS JUGADAS EN LA MESA ==========
    private final List<CartaSolitario> jugadasJ1 = new ArrayList<>();
    private final List<CartaSolitario> jugadasJ2 = new ArrayList<>();
    private final Vector2[] posicionesJugadasJ1 = new Vector2[3];
    private final Vector2[] posicionesJugadasJ2 = new Vector2[3];

    // ========== BOTONES ==========
    private Boton btnTruco;
    private Boton btnRetruco;
    private Boton btnValeCuatro;
    private Boton btnEnvido;
    private Boton btnRealEnvido;
    private Boton btnFaltaEnvido;
    private Boton btnQuiero;
    private Boton btnNoQuiero;
    private Boton btnIrAlMazo;

    // ========== MENSAJES TEMPORALES ==========
    private String mensajeTemporal = "";
    private float tiempoMensajeTemporal = 0f;
    private static final float DURACION_MENSAJE_TEMPORAL = 4f;


    // ========== CONTROL DE VICTORIA ==========
    private float tiempoVictoria = 0f;
    private static final float TIEMPO_MOSTRAR_VICTORIA = 3f;

    // ========== REFERENCIA AL CONTROLADOR DE RED ==========
    private GameController gameController;
    private ClientThread clientThread;
    // 🆕 Agregar esta variable al inicio de la clase
    private String tipoCantoPendiente = null; // "truco" o "envido"
    private List<CartaPendiente> cartasPendientesBuffer = null;
    private boolean cartasPendientesAplicadas = false;

    // Clase auxiliar para guardar cartas en el buffer
    private static class CartaPendiente {
        int jugador;
        int idCarta;

        CartaPendiente(int jugador, int idCarta) {
            this.jugador = jugador;
            this.idCarta = idCarta;
        }
    }
    public void setCartasPendientes(List<PantallaSeleccionPuntos.CartaPendiente> cartas) {
        this.cartasPendientesBuffer = new ArrayList<>();
        for (PantallaSeleccionPuntos.CartaPendiente cp : cartas) {
            this.cartasPendientesBuffer.add(new CartaPendiente(cp.jugador, cp.idCarta));
        }
        System.out.println("📦 " + cartasPendientesBuffer.size() + " cartas pendientes recibidas");
    }

    // ========== CONSTRUCTOR ==========
    public PantallaDosJugadores(int puntosParaGanar, GameController gameController) {
        this.puntosParaGanar = puntosParaGanar;
        this.gameController = gameController;

        // ✅ NUEVO: Obtener el clientThread del gameController
        if (gameController instanceof PantallaSeleccionPuntos) {
            this.clientThread = ((PantallaSeleccionPuntos) gameController).clientThread;
            System.out.println("✅ ClientThread obtenido correctamente");
        }
        System.out.println("✅ JuegoTruco local inicializado");
    }

    @Override
    public void show() {
        // Configurar gráficos
        fondo = new Imagen(Recursos.FONDODOSJUGADORES);
        fondo.dimensionarImg(Configuracion.ANCHO, Configuracion.ALTO);
        batch = Render.batch;

        // Crear jugadores SIN cartas
        jugador1 = new JugadorBase(1, true);
        jugador2 = new JugadorBase(2, false);

        // Configurar posiciones en la mesa
        configurarPosicionesMesa();

        // ✅ Crear botones ANTES de aplicar cartas
        crearBotones();

        // Crear fuentes
        fuente = new BitmapFont();
        fuente.getData().setScale(2f);
        fuente.setColor(Color.WHITE);

        fuenteVictoria = new BitmapFont();
        fuenteVictoria.getData().setScale(4f);
        fuenteVictoria.setColor(Color.YELLOW);

        fuenteCanto = new BitmapFont();
        fuenteCanto.getData().setScale(5f);
        fuenteCanto.setColor(new Color(1f, 0.8f, 0.2f, 1f));

        // ✅ Aplicar cartas pendientes
        if (cartasPendientesBuffer != null && !cartasPendientesAplicadas) {
            System.out.println("📦 Aplicando " + cartasPendientesBuffer.size() + " cartas pendientes en show()");
            for (CartaPendiente cp : cartasPendientesBuffer) {
                System.out.println("   → Aplicando carta J" + cp.jugador + " ID:" + cp.idCarta);
                repartir(cp.jugador, cp.idCarta);
            }
            cartasPendientesBuffer.clear();
            cartasPendientesAplicadas = true;
            System.out.println("✅ Cartas aplicadas exitosamente");
        }

        // ✅ CRÍTICO: Actualizar botones DESPUÉS de que todo esté inicializado
        System.out.println("🎮 Llamando actualizarEstadoBotones() desde show()");
        actualizarEstadoBotones();
        if (clientThread != null) {
            clientThread.sendMessage("SolicitarBotones:" + miNumeroJugador);
            System.out.println("📤 Cliente solicita estado de botones");
        }
    }

    private void crearBotones() {
        float btnAncho = 150;
        float btnAlto = 50;
        float margen = 20;
        float separacion = 10;

        Color azulArg = new Color(0.4f, 0.6f, 0.85f, 0.9f);
        Color violeta = new Color(0.6f, 0.3f, 0.8f, 0.9f);
        Color blanco = Color.WHITE;
        Color borde = new Color(0.2f, 0.4f, 0.6f, 1f);
        Color verde = new Color(0.2f, 0.7f, 0.3f, 0.9f);
        Color rojo = new Color(0.8f, 0.2f, 0.2f, 0.9f);
        Color naranja = new Color(0.9f, 0.5f, 0.1f, 0.9f);

        // Botones de TRUCO
        float trucoPosY = Configuracion.ALTO / 2f + 100;
        btnTruco = new Boton("TRUCO", margen, trucoPosY, btnAncho, btnAlto);
        btnRetruco = new Boton("RETRUCO", margen, trucoPosY - btnAlto - separacion, btnAncho, btnAlto);
        btnValeCuatro = new Boton("VALE 4", margen, trucoPosY - (btnAlto + separacion) * 2, btnAncho, btnAlto);

        btnTruco.setColor(azulArg, blanco, borde);
        btnRetruco.setColor(azulArg, blanco, borde);
        btnValeCuatro.setColor(azulArg, blanco, borde);

        // Botones de ENVIDO
        float envidoPosY = Configuracion.ALTO / 2f - 50;
        btnEnvido = new Boton("ENVIDO", margen, envidoPosY, btnAncho, btnAlto);
        btnRealEnvido = new Boton("REAL ENVIDO", margen, envidoPosY - btnAlto - separacion, btnAncho, btnAlto);
        btnFaltaEnvido = new Boton("FALTA ENVIDO", margen, envidoPosY - (btnAlto + separacion) * 2, btnAncho, btnAlto);

        btnEnvido.setColor(violeta, blanco, borde);
        btnRealEnvido.setColor(violeta, blanco, borde);
        btnFaltaEnvido.setColor(violeta, blanco, borde);

        // Botones de RESPUESTA
        float respuestaPosY = Configuracion.ALTO / 2f + 50;
        btnQuiero = new Boton("QUIERO", Configuracion.ANCHO - btnAncho - margen, respuestaPosY, btnAncho, btnAlto);
        btnNoQuiero = new Boton("NO QUIERO", Configuracion.ANCHO - btnAncho - margen, respuestaPosY - btnAlto - separacion, btnAncho, btnAlto);

        btnQuiero.setColor(verde, blanco, borde);
        btnNoQuiero.setColor(rojo, blanco, borde);

        // Botón IR AL MAZO
        float mazoPosY = 100;
        btnIrAlMazo = new Boton("IR AL MAZO", margen, mazoPosY, btnAncho, btnAlto);
        btnIrAlMazo.setColor(naranja, blanco, borde);



        System.out.println("✅ Botones creados correctamente");
    }
    private void ocultarTodosLosBotones() {
        btnTruco.setVisible(false);
        btnRetruco.setVisible(false);
        btnValeCuatro.setVisible(false);
        btnEnvido.setVisible(false);
        btnRealEnvido.setVisible(false);
        btnFaltaEnvido.setVisible(false);
        btnQuiero.setVisible(false);
        btnNoQuiero.setVisible(false);
        btnIrAlMazo.setVisible(false);
    }
    private void actualizarEstadoBotones() {
        // Solo ocultar todo por defecto
        // El servidor dirá qué mostrar
        ocultarTodosLosBotones();
    }

    // ========== CONFIGURACIÓN DE POSICIONES ==========
    private void configurarPosicionesMesa() {
        float cx = Configuracion.ANCHO / 2f;
        float cy = Configuracion.ALTO / 2f;

        // Posiciones para las cartas jugadas por J1 (abajo en la mesa)
        posicionesJugadasJ1[0] = new Vector2(cx - 300, cy - 120);
        posicionesJugadasJ1[1] = new Vector2(cx - 50, cy - 120);
        posicionesJugadasJ1[2] = new Vector2(cx + 200, cy - 120);

        // Posiciones para las cartas jugadas por J2 (arriba en la mesa)
        posicionesJugadasJ2[0] = new Vector2(cx - 300, cy + 40);
        posicionesJugadasJ2[1] = new Vector2(cx - 50, cy + 40);
        posicionesJugadasJ2[2] = new Vector2(cx + 200, cy + 40);
    }

    private void posicionarCartasJugadorAbajo(List<CartaSolitario> mano) {
        float x = Configuracion.ANCHO / 2f - 300;
        float y = Configuracion.ALTO - 650;
        float dx = 250;

        System.out.println("🎯 Posicionando " + mano.size() + " cartas ABAJO");

        for (int i = 0; i < mano.size(); i++) {
            CartaSolitario c = mano.get(i);
            c.setSize(100, 200);
            c.setPosicion(new Vector2(x + i * dx, y));
            c.setYaJugadas(false);

            System.out.println("   Carta " + i + ": pos=(" + (x + i * dx) + "," + y + ")");
        }
    }

    private void posicionarCartasJugadorArriba(List<CartaSolitario> mano) {
        float x = Configuracion.ANCHO / 2f - 300;
        float y = Configuracion.ALTO - 220;
        float dx = 250;

        System.out.println("🎯 Posicionando " + mano.size() + " cartas ARRIBA");

        for (int i = 0; i < mano.size(); i++) {
            CartaSolitario c = mano.get(i);
            c.setSize(100, 200);
            c.setPosicion(new Vector2(x + i * dx, y));
            c.setYaJugadas(false);

            System.out.println("   Carta " + i + ": pos=(" + (x + i * dx) + "," + y + ")");
        }
    }

    // ========== MENSAJES TEMPORALES ==========
    private void mostrarMensajeTemporal(String mensaje) {
        mensajeTemporal = mensaje;
        tiempoMensajeTemporal = DURACION_MENSAJE_TEMPORAL;
    }

    @Override
    public void render(float delta) {
        // Salir al menú con ESC
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            volverAlMenuConMusica();
            return;
        }

        // Actualizar temporizador de mensajes temporales
        if (tiempoMensajeTemporal > 0) {
            tiempoMensajeTemporal -= delta;
            if (tiempoMensajeTemporal <= 0) {
                mensajeTemporal = "";
            }
        }

        // Actualizar temporizador de victoria
        if (juegoTerminado) {
            tiempoVictoria += delta;
            if (tiempoVictoria >= TIEMPO_MOSTRAR_VICTORIA) {
                volverAlMenuConMusica();
                return;
            }
        }

        Render.limpiarPantalla(0, 0, 0);
        batch.begin();
        fondo.dibujar();

        // Dibujar cartas en las manos
        if (miNumeroJugador == 1) {
            // Soy J1: dibujar mis cartas (J1) abajo
            for (CartaSolitario c : jugador1.getMano()) {
                c.dibujar(batch);
            }
            // Dibujar cartas del oponente (J2) arriba (dorso)
            for (CartaSolitario c : jugador2.getMano()) {
                c.dibujar(batch);
            }
        } else if (miNumeroJugador == 2) {
            // Soy J2: dibujar mis cartas (J2) abajo
            for (CartaSolitario c : jugador2.getMano()) {
                c.dibujar(batch);
            }
            // Dibujar cartas del oponente (J1) arriba (dorso)
            for (CartaSolitario c : jugador1.getMano()) {
                c.dibujar(batch);
            }
        }

        // ✅✅✅ CRÍTICO: DIBUJAR CARTAS JUGADAS EN LA MESA ✅✅✅
        for (CartaSolitario c : jugadasJ1) {
            c.dibujar(batch);
        }
        for (CartaSolitario c : jugadasJ2) {
            c.dibujar(batch);
        }

        // Dibujar indicador de turno
        String textoTurno = (turnoActual == miNumeroJugador) ? "TU TURNO" : "Turno del J" + turnoActual;
        Color colorTurno = (turnoActual == miNumeroJugador) ? Color.GREEN : Color.RED;
        fuente.setColor(colorTurno);
        fuente.draw(batch, textoTurno, Configuracion.ANCHO - 200, Configuracion.ALTO - 50);
        fuente.setColor(Color.WHITE);  // Restaurar color

        // Dibujar puntos
        fuente.draw(batch, "J1: " + puntosJ1 + " pts", 50, Configuracion.ALTO - 50);
        fuente.draw(batch, "J2: " + puntosJ2 + " pts", 50, Configuracion.ALTO - 100);
        fuente.draw(batch, "ESC para salir", 50, 650);


        // Dibujar mensaje temporal
        if (!mensajeTemporal.isEmpty()) {
            com.badlogic.gdx.graphics.g2d.GlyphLayout layout =
                new com.badlogic.gdx.graphics.g2d.GlyphLayout(fuenteCanto, mensajeTemporal);
            float anchoTexto = layout.width;
            float altoTexto = layout.height;

            fuenteCanto.draw(batch, mensajeTemporal,
                Configuracion.ANCHO / 2f - anchoTexto / 2f,
                Configuracion.ALTO / 2f + altoTexto / 2f);
        }

        // Dibujar mensaje de victoria
        if (juegoTerminado) {
            int ganador = (puntosJ1 >= puntosParaGanar) ? 1 : 2;
            String msgVictoria = "¡GANÓ JUGADOR " + ganador + "!";
            String msgPuntos = puntosJ1 + " - " + puntosJ2;

            fuenteVictoria.draw(batch, msgVictoria,
                Configuracion.ANCHO / 2f - 300,
                Configuracion.ALTO / 2f + 50);

            fuente.draw(batch, msgPuntos,
                Configuracion.ANCHO / 2f - 100,
                Configuracion.ALTO / 2f - 20);

            fuente.draw(batch, "Volviendo al menú...",
                Configuracion.ANCHO / 2f - 150,
                Configuracion.ALTO / 2f - 80);
        }

        // Dibujar botones
        if (btnTruco != null) btnTruco.dibujar(batch);
        if (btnRetruco != null) btnRetruco.dibujar(batch);
        if (btnValeCuatro != null) btnValeCuatro.dibujar(batch);
        if (btnEnvido != null) btnEnvido.dibujar(batch);
        if (btnRealEnvido != null) btnRealEnvido.dibujar(batch);
        if (btnFaltaEnvido != null) btnFaltaEnvido.dibujar(batch);
        if (btnQuiero != null) btnQuiero.dibujar(batch);
        if (btnNoQuiero != null) btnNoQuiero.dibujar(batch);
        if (btnIrAlMazo != null) btnIrAlMazo.dibujar(batch);

        batch.end();
    }


    public void setMiNumeroJugador(int numero) {
        this.miNumeroJugador = numero;
        System.out.println("🎮 Mi número de jugador establecido: " + miNumeroJugador);

        // ❌ NO actualizar botones aquí
        // El servidor lo hará cuando corresponda
    }

    public void jugarCarta(CartaSolitario carta, int jugador) {
        if (juegoTerminado) return;

        if (jugador != miNumeroJugador) {
            System.out.println("❌ No puedes jugar cartas del oponente");
            return;
        }

        if (turnoActual != miNumeroJugador) {
            System.out.println("❌ No es tu turno");
            mostrarMensajeTemporal("¡No es tu turno!");
            return;
        }

        System.out.println("🎴 Enviando al servidor: JugarCarta:" + jugador + ":" + carta.getId());

        // ✅ Enviar al servidor usando clientThread
        if (clientThread != null) {
            clientThread.sendMessage("JugarCarta:" + jugador + ":" + carta.getId());
            System.out.println("📤 Mensaje enviado al servidor");
        } else {
            System.err.println("❌ ERROR: clientThread es null");
        }
    }

    private boolean verificarPuedoCantar() {
        if (turnoActual != miNumeroJugador) {
            mostrarMensajeTemporal("No es tu turno");
            return false;
        }
        return true;
    }

    public void procesarClickBoton(Boton boton) {
        if (juegoTerminado) return;

        System.out.println("🖱️ Click en botón: " + boton.getTexto());

        // ✅ SOLO enviar al servidor - SIN validaciones locales

        if (boton == btnIrAlMazo) {
            enviarMensajeAlServidor("IrAlMazo:" + miNumeroJugador);
            return;
        }

        if (boton == btnQuiero) {
            enviarMensajeAlServidor("ResponderCanto:" + miNumeroJugador + ":quiero");
            return;
        }

        if (boton == btnNoQuiero) {
            enviarMensajeAlServidor("ResponderCanto:" + miNumeroJugador + ":noquiero");
            return;
        }

        if (boton == btnTruco) {
            enviarMensajeAlServidor("CantarTruco:" + miNumeroJugador + ":truco");
            return;
        }

        if (boton == btnRetruco) {
            enviarMensajeAlServidor("CantarTruco:" + miNumeroJugador + ":retruco");
            return;
        }

        if (boton == btnValeCuatro) {
            enviarMensajeAlServidor("CantarTruco:" + miNumeroJugador + ":vale cuatro");
            return;
        }

        if (boton == btnEnvido) {
            enviarMensajeAlServidor("CantarEnvido:" + miNumeroJugador + ":envido");
            return;
        }

        if (boton == btnRealEnvido) {
            enviarMensajeAlServidor("CantarEnvido:" + miNumeroJugador + ":real envido");
            return;
        }

        if (boton == btnFaltaEnvido) {
            enviarMensajeAlServidor("CantarEnvido:" + miNumeroJugador + ":falta envido");
            return;
        }
    }

    // Método auxiliar
    private void enviarMensajeAlServidor(String mensaje) {
        if (clientThread != null) {
            clientThread.sendMessage(mensaje);
            System.out.println("📤 Enviado: " + mensaje);
        } else {
            System.err.println("❌ ERROR: clientThread es null");
        }
    }



    public Boton[] getBotones() {
        return new Boton[]{
            btnTruco, btnRetruco, btnValeCuatro,
            btnEnvido, btnRealEnvido, btnFaltaEnvido,
            btnQuiero, btnNoQuiero, btnIrAlMazo
        };
    }

    // ========== IMPLEMENTACIÓN DE GameController (mensajes del servidor) ==========

    /**
     * Llamado cuando el servidor confirma la conexión.
     * @param numPlayer Número de jugador asignado (1 o 2)
     */
    @Override
    public void connect(int numPlayer) {
        System.out.println("✅ Conectado como jugador " + numPlayer);
        this.miNumeroJugador = numPlayer;
    }

    /**
     * Llamado cuando el servidor indica que la partida comienza.
     */
    @Override
    public void start() {
        System.out.println("🎮 Partida iniciada");
    }

    /**
     * Llamado cuando el servidor envía los puntos para ganar.
     * @param puntos Puntos necesarios para ganar
     */
    @Override
    public void iniciarPartida(int puntos) {
        System.out.println("🎯 Iniciando partida a " + puntos + " puntos");
        this.puntosParaGanar = puntos;
    }
    private void posicionarCartasOponenteArriba(List<CartaSolitario> mano) {
        float x = Configuracion.ANCHO / 2f - 300;
        float y = Configuracion.ALTO - 220;
        float dx = 250;

        System.out.println("🎯 Posicionando " + mano.size() + " cartas OPONENTE (dorso)");

        for (int i = 0; i < mano.size(); i++) {
            CartaSolitario c = mano.get(i);
            c.setSize(100, 200);
            c.setPosicion(new Vector2(x + i * dx, y));
            c.setYaJugadas(false);

            // ✅ Cambiar la textura al dorso
            c.mostrarDorso();

            System.out.println("   Carta " + i + " OPONENTE: pos=(" + (x + i * dx) + "," + y + ") [DORSO]");
        }
    }

    @Override
    public void repartir(int jugador, int idCarta) {
        System.out.println("📨 Recibiendo carta ID:" + idCarta + " para jugador " + jugador);

        // Crear la carta visual basándose en el ID recibido
        CartaSolitario carta = crearCartaPorId(idCarta);
        if (carta == null) {
            System.err.println("❌ Error al crear carta con ID: " + idCarta);
            return;
        }

        // Agregar la carta a la mano del jugador correspondiente
        if (jugador == 1) {
            jugador1.getMano().add(carta);
            System.out.println("✅ Carta agregada a J1. Total: " + jugador1.getMano().size());
        } else if (jugador == 2) {
            jugador2.getMano().add(carta);
            System.out.println("✅ Carta agregada a J2. Total: " + jugador2.getMano().size());
        } else {
            System.err.println("❌ Número de jugador inválido: " + jugador);
            return;
        }

        //  NUEVO: Posicionar solo las cartas del jugador actual
        if (miNumeroJugador == 1) {
            // Soy J1: mis cartas abajo, las del oponente arriba (dorso)
            posicionarCartasJugadorAbajo(jugador1.getMano());
            posicionarCartasOponenteArriba(jugador2.getMano());
        } else if (miNumeroJugador == 2) {
            // Soy J2: mis cartas abajo, las del oponente arriba (dorso)
            posicionarCartasJugadorAbajo(jugador2.getMano());
            posicionarCartasOponenteArriba(jugador1.getMano());
        }

        // Cuando ambos jugadores tienen 3 cartas, activar el InputProcessor
        if (jugador1.getMano().size() == 3 && jugador2.getMano().size() == 3) {
            System.out.println("✅ Todas las cartas recibidas - Activando InputProcessor");
            actualizarInputProcessor();
        }
    }

    @Override
    public void cartaJugada(int jugador, int idCarta) {
        System.out.println("\n📨 ========== CLIENTE RECIBE CARTA JUGADA ==========");
        System.out.println("   Jugador: J" + jugador);
        System.out.println("   Carta ID: " + idCarta);
        System.out.println("   Mi número: J" + miNumeroJugador);

        // ✅ Mover la carta a la mesa
        moverCartaAMesa(jugador, idCarta);

        // ✅ Mostrar mensaje temporal
        String mensaje = (jugador == miNumeroJugador) ? "Jugaste una carta" : "El oponente jugó";
        mostrarMensajeTemporal(mensaje);

        System.out.println("✅ Carta movida a la mesa");
        System.out.println("===================================================\n");
    }


    private CartaSolitario crearCartaPorId(int id) {
        System.out.println("🔍 Intentando crear carta con ID: " + id);

        // Buscar en el enum CartasFinales
        for (CartasFinales carta : CartasFinales.values()) {
            if (carta.getId() == id) {
                System.out.println("✅ Carta encontrada: " + carta.name());
                return carta.crearCarta();
            }
        }

        System.err.println("❌ No se encontró carta con ID: " + id);
        return null;
    }


    private void actualizarInputProcessor() {
        System.out.println("🎮 Actualizando InputProcessor");

        // ✅ Determinar qué cartas son las mías
        List<CartaSolitario> misCartas;
        List<CartaSolitario> cartasOponente;

        if (miNumeroJugador == 1) {
            misCartas = jugador1.getMano();
            cartasOponente = jugador2.getMano();
        } else {
            misCartas = jugador2.getMano();
            cartasOponente = jugador1.getMano();
        }

        Gdx.input.setInputProcessor(new EntradaDosJugadores(
            misCartas,          // ✅ Solo puedo clickear MIS cartas
            cartasOponente,     // Las del oponente (para referencia, pero no clickeables)
            this
        ));
    }


    private void volverAlMenuConMusica() {
        if (Recursos.MUSICA_JUEGO != null) {
            Recursos.MUSICA_JUEGO.stop();
            Recursos.MUSICA_JUEGO.setPosition(0);
        }

        if (Recursos.MUSICA_GENERAL != null) {
            Recursos.MUSICA_GENERAL.play();
        }

        dispose();
        Render.app.setScreen(new PantallaMenu());
    }



    @Override
    public void actualizarPuntos(int puntosJ1, int puntosJ2) {
        this.puntosJ1 = puntosJ1;
        this.puntosJ2 = puntosJ2;
        System.out.println("📊 Puntos actualizados: J1=" + puntosJ1 + ", J2=" + puntosJ2);
    }

    @Override
    public void mostrarVictoria(int ganador) {
        juegoTerminado = true;
        tiempoVictoria = 0f;
        System.out.println("🏆 ¡Jugador " + ganador + " ganó el juego!");
        mostrarMensajeTemporal("¡GANÓ JUGADOR " + ganador + "!");
    }


    @Override
    public void actualizarTurno(int turno) {
        this.turnoActual = turno;
        System.out.println("🔄 Turno actualizado: J" + turno);

        if (turno == miNumeroJugador) {
            mostrarMensajeTemporal("¡Tu turno!");
        } else {
            mostrarMensajeTemporal("Turno del oponente");
        }

        // ❌ NO llamar actualizarEstadoBotones() aquí
        // El servidor enviará "ActualizarBotones" automáticamente
    }


    public void moverCartaAMesa(int jugador, int idCarta) {
        System.out.println("\n🎴 ========== MOVIENDO CARTA A LA MESA ==========");
        System.out.println("   Jugador: J" + jugador);
        System.out.println("   Carta ID: " + idCarta);

        List<CartaSolitario> mano = (jugador == 1) ? jugador1.getMano() : jugador2.getMano();
        List<CartaSolitario> jugadas = (jugador == 1) ? jugadasJ1 : jugadasJ2;
        Vector2[] posiciones = (jugador == 1) ? posicionesJugadasJ1 : posicionesJugadasJ2;

        System.out.println("   Cartas en mano ANTES: " + mano.size());
        System.out.println("   Cartas en mesa ANTES: " + jugadas.size());

        // Buscar la carta en la mano
        CartaSolitario cartaJugada = null;
        for (CartaSolitario c : mano) {
            if (c.getId() == idCarta) {
                cartaJugada = c;
                break;
            }
        }

        if (cartaJugada != null) {
            System.out.println("✅ Carta encontrada en la mano");

            // ✅ Si era una carta del oponente (dorso), mostrar la cara
            if (cartaJugada.estaMostrandoDorso()) {
                System.out.println("   🔄 Cambiando de DORSO a CARA");
                cartaJugada.mostrarCara();
            }

            // ✅ Mover a la posición de la mesa
            int indice = jugadas.size();
            Vector2 posicionMesa = posiciones[indice];
            cartaJugada.setPosicion(posicionMesa);
            cartaJugada.setYaJugadas(true);

            System.out.println("   📍 Carta movida a posición: (" + posicionMesa.x + ", " + posicionMesa.y + ")");

            // ✅ Agregar a la lista de jugadas
            jugadas.add(cartaJugada);

            // ✅ Remover de la mano
            mano.remove(cartaJugada);

            System.out.println("   Cartas en mano DESPUÉS: " + mano.size());
            System.out.println("   Cartas en mesa DESPUÉS: " + jugadas.size());

            // ✅ Reposicionar las cartas restantes en la mano
            if (jugador == miNumeroJugador) {
                System.out.println("   🔄 Reposicionando MIS cartas");
                posicionarCartasJugadorAbajo(mano);
            } else {
                System.out.println("   🔄 Reposicionando cartas del OPONENTE");
                posicionarCartasOponenteArriba(mano);
            }

            System.out.println("✅ CARTA MOVIDA EXITOSAMENTE");
        } else {
            System.err.println("❌ ERROR: No se encontró carta con ID:" + idCarta + " en la mano de J" + jugador);

            // 🐛 DEBUG: Mostrar IDs de todas las cartas en la mano
            System.out.println("   📋 Cartas en la mano de J" + jugador + ":");
            for (CartaSolitario c : mano) {
                System.out.println("      - ID: " + c.getId() + " (dorso: " + c.estaMostrandoDorso() + ")");
            }
        }

        System.out.println("===============================================\n");
    }




    public void limpiarMesa() {
        System.out.println("\n🗑️ ========== LIMPIANDO MESA ==========");
        System.out.println("   Cartas en mesa J1 ANTES: " + jugadasJ1.size());
        System.out.println("   Cartas en mesa J2 ANTES: " + jugadasJ2.size());

        // ✅ Limpiar SOLO las cartas jugadas en la mesa
        jugadasJ1.clear();
        jugadasJ2.clear();

        // ❌ NO limpiar las manos aquí, solo la mesa
        // jugador1.getMano().clear(); <-- NO HACER ESTO
        // jugador2.getMano().clear(); <-- NO HACER ESTO

        System.out.println("   Cartas en mesa J1 DESPUÉS: " + jugadasJ1.size());
        System.out.println("   Cartas en mesa J2 DESPUÉS: " + jugadasJ2.size());
        System.out.println("   Cartas en mano J1: " + jugador1.getMano().size());
        System.out.println("   Cartas en mano J2: " + jugador2.getMano().size());
        System.out.println("✅ Mesa limpiada exitosamente");
        System.out.println("======================================\n");
    }

    public void nuevaMano() {
        System.out.println("\n🔄 ========== NUEVA MANO ==========");
        System.out.println("   Cartas en mesa J1 ANTES: " + jugadasJ1.size());
        System.out.println("   Cartas en mesa J2 ANTES: " + jugadasJ2.size());
        System.out.println("   Cartas en mano J1 ANTES: " + jugador1.getMano().size());
        System.out.println("   Cartas en mano J2 ANTES: " + jugador2.getMano().size());

        // ✅ Limpiar TODO: mesa + manos
        jugadasJ1.clear();
        jugadasJ2.clear();
        jugador1.getMano().clear();
        jugador2.getMano().clear();

        System.out.println("   Cartas en mesa J1 DESPUÉS: " + jugadasJ1.size());
        System.out.println("   Cartas en mesa J2 DESPUÉS: " + jugadasJ2.size());
        System.out.println("   Cartas en mano J1 DESPUÉS: " + jugador1.getMano().size());
        System.out.println("   Cartas en mano J2 DESPUÉS: " + jugador2.getMano().size());
        System.out.println("✅ TODO limpiado - Esperando nuevas cartas");
        System.out.println("==================================\n");
    }

    @Override
    public void cantoRealizado(String tipoCanto, int jugador, String nombreCanto) {
        System.out.println("🎺 Canto realizado: " + tipoCanto + " - J" + jugador + " - " + nombreCanto);

        // ✅ SOLO mostrar mensaje visual
        mostrarMensajeTemporal("J" + jugador + " canta " + nombreCanto.toUpperCase());

        // ❌ NO actualizar estado local
        // El servidor enviará "ActualizarBotones" después
    }

    // 🆕 JUGADOR AL MAZO
    @Override
    public void jugadorAlMazo(int jugador) {
        System.out.println("🃏 J" + jugador + " se fue al mazo");

        // ✅ SOLO UI - NO lógica
        mostrarMensajeTemporal("¡Jugador " + jugador + " se va al mazo!");

        // ✅ El servidor enviará "Puntos" y "NuevaMano"
        // NO hacer lógica aquí
    }

    @Override
    public void actualizarBotones(String botonesVisibles) {
        System.out.println("🎮 Actualizando botones desde servidor: " + botonesVisibles);

        // Primero ocultar todos
        ocultarTodosLosBotones();

        // Si no hay botones que mostrar
        if (botonesVisibles.isEmpty() || botonesVisibles.equals("ninguno")) {
            return;
        }

        // Mostrar solo los que el servidor indica
        String[] botones = botonesVisibles.split(",");
        for (String boton : botones) {
            switch (boton.trim()) {
                case "truco":
                    btnTruco.setVisible(true);
                    btnTruco.setHabilitado(true);
                    break;
                case "retruco":
                    btnRetruco.setVisible(true);
                    btnRetruco.setHabilitado(true);
                    break;
                case "vale4":
                    btnValeCuatro.setVisible(true);
                    btnValeCuatro.setHabilitado(true);
                    break;
                case "envido":
                    btnEnvido.setVisible(true);
                    btnEnvido.setHabilitado(true);
                    break;
                case "real":
                    btnRealEnvido.setVisible(true);
                    btnRealEnvido.setHabilitado(true);
                    break;
                case "falta":
                    btnFaltaEnvido.setVisible(true);
                    btnFaltaEnvido.setHabilitado(true);
                    break;
                case "quiero":
                    btnQuiero.setVisible(true);
                    btnQuiero.setHabilitado(true);
                    break;
                case "noquiero":
                    btnNoQuiero.setVisible(true);
                    btnNoQuiero.setHabilitado(true);
                    break;
                case "mazo":
                    btnIrAlMazo.setVisible(true);
                    btnIrAlMazo.setHabilitado(true);
                    break;
            }
        }
    }

    @Override
    public void respuestaCanto(int jugador, String respuesta, int resultado) {
        System.out.println("💬 Respuesta: J" + jugador + " - " + respuesta + " - resultado=" + resultado);

        // ✅ SOLO mostrar mensaje visual
        String mensaje = respuesta.equalsIgnoreCase("quiero")
            ? "J" + jugador + " dice QUIERO"
            : "J" + jugador + " dice NO QUIERO";
        mostrarMensajeTemporal(mensaje);

        // ❌ NO actualizar estado local
        // El servidor enviará "ActualizarBotones" después
    }

    public int getMiNumeroJugador() {
        return miNumeroJugador;
    }

    // ========== MÉTODOS OBLIGATORIOS DE Screen ==========
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        fondo.dispose();
        btnTruco.dispose();
        btnRetruco.dispose();
        btnValeCuatro.dispose();
        btnEnvido.dispose();
        btnRealEnvido.dispose();
        btnFaltaEnvido.dispose();
        btnQuiero.dispose();
        btnNoQuiero.dispose();
        btnIrAlMazo.dispose();
        fuente.dispose();
        if (fuenteVictoria != null) fuenteVictoria.dispose();
        if (fuenteCanto != null) fuenteCanto.dispose();
    }

}
