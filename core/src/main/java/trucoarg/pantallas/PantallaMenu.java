package trucoarg.pantallas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import trucoarg.elementos.Imagen;
import trucoarg.elementos.Texto;
import trucoarg.network.GameController;
import trucoarg.ui.EntradasMenu;
import trucoarg.utiles.Configuracion;
import trucoarg.utiles.Recursos;
import trucoarg.utiles.Render;
import trucoarg.network.ClientThread;

public class PantallaMenu implements Screen, GameController{

    private Imagen fondo;
    private SpriteBatch b;
    private final Texto[] OPCIONES = new Texto[5];
    private final String[] TEXTOS = {"Un jugador", "Dos jugadores", "Opciones", "Tutorial", "Salir..."};
    private EntradasMenu entradas;
    private int opc = 1;
    private float tiempo = 0;
    public GameController gameController;

    @Override
    public void show() {
        fondo = new Imagen(Recursos.FONDOMENU);
        fondo.dimensionarImg(Configuracion.ANCHO, Configuracion.ALTO);
        b = Render.batch;
        cargarOpcionesCentradas();
        entradas = new EntradasMenu();
        Gdx.input.setInputProcessor(entradas);
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla(1,1,1);
        tiempo += delta;

        manejarEntradas();
        actualizarColores();

        b.begin();
        fondo.dibujar();
        for (Texto t : OPCIONES) {
            t.dibujar();
        }
        b.end();
    }

    private void manejarEntradas() {
        if (tiempo > 0.15f) {
            if (entradas.abajo()) {
                opc++;
                if (opc > OPCIONES.length) opc = 1;
                tiempo = 0;
            }

            if (entradas.arriba()) {
                opc--;
                if (opc < 1) opc = OPCIONES.length;
                tiempo = 0;
            }

            if (entradas.enter()) {
                ejecutarOpcion();
                tiempo = 0;
            }
        }
    }

    private void actualizarColores() {
        for (int i = 0; i < OPCIONES.length; i++) {
            if (i == opc - 1) {
                OPCIONES[i].setColor(Color.BLUE);
            } else {
                OPCIONES[i].setColor(Color.WHITE);
            }
        }
    }
    private void cargarOpcionesCentradas() {
        int avance = 60;
        int yInicial = 500;

        for (int i = 0; i < OPCIONES.length; i++) {
            OPCIONES[i] = new Texto(Recursos.FUENTE_MENU, 60, Color.WHITE, true);
            OPCIONES[i].setTexto(TEXTOS[i]);


            float xCentrado = (Configuracion.ANCHO - OPCIONES[i].getAncho()) / 2;

            OPCIONES[i].setPosicion(xCentrado, yInicial - (i * avance));
        }
    }

    private void ejecutarOpcion() {
        switch (opc) {
            case 1:
                if (Recursos.MUSICA_GENERAL != null) {
                    Recursos.MUSICA_GENERAL.stop();
                    Recursos.MUSICA_GENERAL.setPosition(0);
                    Recursos.MUSICA_JUEGO.play();
                }
            Render.app.setScreen(new PantallaUnJugador());
                break;
            case 2:
                ClientThread clientThread = new ClientThread(this);
                clientThread.start();
                clientThread.sendMessage("Connect");
                Render.app.setScreen(new PantallaSeleccionPuntos(this));
                break;
            case 3:
                Render.app.setScreen(new PantallaConfiguraciones());
                break;
            case 4:
                Render.app.setScreen(new PantallaTutorial());
                break;
            case 5:
                Gdx.app.exit();
                break;
        }
    }

    @Override public void resize(int width, int height) {

    }
    @Override public void pause() {

    }
    @Override public void resume() {

    }
    @Override public void hide() {

    }
    @Override public void dispose() {
        b.dispose();
        fondo.dispose();
    }

    @Override
    public void connect(int numPlayer) {

    }

    @Override
    public void start() {

    }
}
