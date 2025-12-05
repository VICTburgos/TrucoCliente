package trucoarg.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import trucoarg.elementos.Imagen;
import trucoarg.network.ClientThread;
import trucoarg.network.GameController;
import trucoarg.ui.Boton;
import trucoarg.utiles.Configuracion;
import trucoarg.utiles.Recursos;
import trucoarg.utiles.Render;

public class PantallaSeleccionPuntos implements Screen, GameController {

    public ClientThread clientThread;
    private Imagen fondo;
    private SpriteBatch batch;
    private BitmapFont tituloFuente;
    private BitmapFont subtituloFuente;
    private BitmapFont infoFuente;

    private Boton btn15Puntos;
    private Boton btn30Puntos;

    private final Object gameInstance;

    // ✅ Variables para almacenar las cartas recibidas
    private int numPlayer = -1;
    private boolean esperandoCartas = false;

    public PantallaSeleccionPuntos(Object game, ClientThread clientThread) {
        this.clientThread = clientThread;
        this.gameInstance = game;
    }

    @Override
    public void show() {
        fondo = new Imagen(Recursos.FONDODOSJUGADORES);
        fondo.dimensionarImg(Configuracion.ANCHO, Configuracion.ALTO);
        batch = Render.batch;

        cargarFuentes();
        crearBotones();

        Gdx.input.setInputProcessor(new com.badlogic.gdx.InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                float y = Configuracion.ALTO - screenY;

                if (btn15Puntos.fueClickeado(screenX, y)) {
                    clientThread.sendMessage("Setearpuntos:15");
                    return true;
                }

                if (btn30Puntos.fueClickeado(screenX, y)) {
                    clientThread.sendMessage("Setearpuntos:30");
                    return true;
                }

                return false;
            }

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    volverAlMenu();
                    return true;
                }
                return false;
            }
        });
    }

    private void cargarFuentes() {
        try {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal(Recursos.FUENTE_MENU)
            );

            FreeTypeFontGenerator.FreeTypeFontParameter paramTitulo =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
            paramTitulo.size = 60;
            paramTitulo.color = Color.WHITE;
            paramTitulo.borderWidth = 3;
            paramTitulo.borderColor = new Color(0.2f, 0.4f, 0.6f, 1f);
            tituloFuente = generator.generateFont(paramTitulo);

            FreeTypeFontGenerator.FreeTypeFontParameter paramSubtitulo =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
            paramSubtitulo.size = 30;
            paramSubtitulo.color = new Color(0.9f, 0.9f, 0.9f, 1f);
            subtituloFuente = generator.generateFont(paramSubtitulo);

            FreeTypeFontGenerator.FreeTypeFontParameter paramInfo =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
            paramInfo.size = 24;
            paramInfo.color = Color.WHITE;
            infoFuente = generator.generateFont(paramInfo);

            generator.dispose();
        } catch (Exception e) {
            System.out.println("Error cargando fuentes: " + e.getMessage());
            tituloFuente = new BitmapFont();
            tituloFuente.getData().setScale(4f);
            subtituloFuente = new BitmapFont();
            subtituloFuente.getData().setScale(2f);
            infoFuente = new BitmapFont();
            infoFuente.getData().setScale(1.5f);
        }
    }

    private void crearBotones() {
        float btnAncho = 300;
        float btnAlto = 100;
        float separacion = 30;
        float centroY = Configuracion.ALTO / 2f - 50;

        float totalAncho = (btnAncho * 2) + separacion;
        float inicioX = (Configuracion.ANCHO / 2f) - (totalAncho / 2f);

        Color azulArg = new Color(0.4f, 0.6f, 0.85f, 0.9f);
        Color amarillo = new Color(1f, 0.8f, 0.2f, 0.9f);
        Color blanco = Color.WHITE;
        Color borde = new Color(0.2f, 0.4f, 0.6f, 1f);

        btn15Puntos = new Boton("15 PUNTOS",
            inicioX,
            centroY,
            btnAncho,
            btnAlto);
        btn15Puntos.setColor(azulArg, blanco, borde);

        btn30Puntos = new Boton("30 PUNTOS",
            inicioX + btnAncho + separacion,
            centroY,
            btnAncho,
            btnAlto);
        btn30Puntos.setColor(amarillo, new Color(0.2f, 0.2f, 0.2f, 1f), borde);
    }

    // En PantallaSeleccionPuntos
    private void iniciarJuego(int puntosParaGanar) {
        System.out.println("Iniciando juego a " + puntosParaGanar + " puntos");
        PantallaDosJugadores pantallaJuego = new PantallaDosJugadores(puntosParaGanar, this);


        clientThread.gameController = pantallaJuego;

        dispose();
        Render.app.setScreen(pantallaJuego);
    }

    private void volverAlMenu() {
        System.out.println("Volviendo al menú principal...");
        if (clientThread != null) {
            clientThread.terminate();
        }
        dispose();
        Render.app.setScreen(new PantallaMenu());
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla(0.1f, 0.1f, 0.15f);

        batch.begin();

        fondo.dibujar();

        String titulo = "TRUCO ARGENTINO";
        float tituloX = Configuracion.ANCHO / 2f - 250;
        float tituloY = Configuracion.ALTO - 150;
        tituloFuente.draw(batch, titulo, tituloX, tituloY);

        String subtitulo = "Elegí los puntos para ganar";
        float subtituloX = Configuracion.ANCHO / 2f - 200;
        float subtituloY = Configuracion.ALTO - 250;
        subtituloFuente.draw(batch, subtitulo, subtituloX, subtituloY);

        String mensajeEsc = "ESC para volver al menú principal...";
        infoFuente.draw(batch, mensajeEsc, 50, 650);

        // ✅ Mostrar estado de conexión
        if (numPlayer >= 0) {
            String mensajeJugador = "Jugador " + numPlayer + " conectado";
            infoFuente.draw(batch, mensajeJugador, 50, 600);
        }

        btn15Puntos.dibujar(batch);
        btn30Puntos.dibujar(batch);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (fondo != null) fondo.dispose();
        if (tituloFuente != null) tituloFuente.dispose();
        if (subtituloFuente != null) subtituloFuente.dispose();
        if (infoFuente != null) infoFuente.dispose();
        if (btn15Puntos != null) btn15Puntos.dispose();
        if (btn30Puntos != null) btn30Puntos.dispose();
    }

    @Override
    public void connect(int numPlayer) {
        this.numPlayer = numPlayer;
        System.out.println("Cliente conectado como jugador: " + numPlayer);
    }

    @Override
    public void start() {
        System.out.println("Partida iniciada desde servidor");
    }

    @Override
    public void iniciarPartida(int puntos) {
        System.out.println("Iniciando partida con " + puntos + " puntos");
        iniciarJuego(puntos);
    }

    @Override
    public void repartir(int jugador, int carta) {
        System.out.println("Repartiendo carta " + carta + " al jugador " + jugador);

    }
}
