package trucoarg.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import trucoarg.elementos.Imagen;
import trucoarg.ui.Boton;
import trucoarg.utiles.Configuracion;
import trucoarg.utiles.Recursos;
import trucoarg.utiles.Render;

public class PantallaConfiguraciones implements Screen {

    private Imagen fondo;
    private SpriteBatch batch;
    private BitmapFont tituloFuente;
    private BitmapFont textoFuente;
    private BitmapFont infoFuente;

    // Botones de volumen
    private Boton btnVolumenMenos;
    private Boton btnVolumenMas;

    private Boton btnVolver;

    private float volumenActual = 0.5f; // Volumen inicial (50%)

    @Override
    public void show() {
        fondo = new Imagen(Recursos.FONDOMENU);
        fondo.dimensionarImg(Configuracion.ANCHO, Configuracion.ALTO);
        batch = Render.batch;

        cargarFuentes();
        crearBotones();

        if (Recursos.MUSICA_GENERAL != null) {
            Recursos.MUSICA_GENERAL.setVolume(volumenActual);
        }
        if (Recursos.MUSICA_JUEGO != null) {
            Recursos.MUSICA_JUEGO.setVolume(volumenActual);
        }

        Gdx.input.setInputProcessor(new com.badlogic.gdx.InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                float y = Configuracion.ALTO - screenY;

                if (btnVolumenMenos.fueClickeado(screenX, y)) {
                    bajarVolumen();
                    return true;
                }

                if (btnVolumenMas.fueClickeado(screenX, y)) {
                    subirVolumen();
                    return true;
                }

                if (btnVolver.fueClickeado(screenX, y)) {
                    volverAlMenu();
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

            FreeTypeFontGenerator.FreeTypeFontParameter paramTexto =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
            paramTexto.size = 40;
            paramTexto.color = Color.WHITE;
            textoFuente = generator.generateFont(paramTexto);

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
            textoFuente = new BitmapFont();
            textoFuente.getData().setScale(2.5f);
            infoFuente = new BitmapFont();
            infoFuente.getData().setScale(1.5f);
        }
    }

    private void crearBotones() {
        Color verde = new Color(0.2f, 0.7f, 0.3f, 0.9f);
        Color rojo = new Color(0.8f, 0.2f, 0.2f, 0.9f);
        Color blanco = Color.WHITE;
        Color borde = new Color(0.2f, 0.4f, 0.6f, 1f);

        float centroX = Configuracion.ANCHO / 2f;
        float centroY = Configuracion.ALTO / 2f;

        float btnVolumenAncho = 100;
        float btnVolumenAlto = 80;
        float separacion = 100;

        btnVolumenMenos = new Boton("-",
            centroX - separacion - btnVolumenAncho,
            centroY - btnVolumenAlto/2,
            btnVolumenAncho,
            btnVolumenAlto);
        btnVolumenMenos.setColor(rojo, blanco, borde);

        btnVolumenMas = new Boton("+",
            centroX + separacion,
            centroY - btnVolumenAlto/2,
            btnVolumenAncho,
            btnVolumenAlto);
        btnVolumenMas.setColor(verde, blanco, borde);

        btnVolver = new Boton("VOLVER AL MENÚ",
            centroX - 150, 100, 300, 70);
        btnVolver.setColor(verde, blanco, borde);
    }

    private void bajarVolumen() {
        volumenActual -= 0.1f;
        if (volumenActual < 0) volumenActual = 0;
        aplicarVolumen();
        System.out.println("Volumen: " + (int)(volumenActual * 100) + "%");
    }

    private void subirVolumen() {
        volumenActual += 0.1f;
        if (volumenActual > 1.0f) volumenActual = 1.0f;
        aplicarVolumen();
        System.out.println("Volumen: " + (int)(volumenActual * 100) + "%");
    }

    private void aplicarVolumen() {
        if (Recursos.MUSICA_GENERAL != null) {
            Recursos.MUSICA_GENERAL.setVolume(volumenActual);
        }
        if (Recursos.MUSICA_JUEGO != null) {
            Recursos.MUSICA_JUEGO.setVolume(volumenActual);
        }
    }

    private void volverAlMenu() {
        System.out.println("Volviendo al menú principal...");
        dispose();
        Render.app.setScreen(new PantallaMenu());
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla(0.1f, 0.1f, 0.15f);

        batch.begin();

        fondo.dibujar();

        String titulo = "CONFIGURACIONES";
        float tituloX = Configuracion.ANCHO / 2f - 280;
        float tituloY = Configuracion.ALTO - 100;
        tituloFuente.draw(batch, titulo, tituloX, tituloY);

        String textoVolumen = "VOLUMEN: " + (int)(volumenActual * 100) + "%";
        float volumenX = Configuracion.ANCHO / 2f - 150;
        float volumenY = Configuracion.ALTO / 2f + 100;
        textoFuente.draw(batch, textoVolumen, volumenX, volumenY);

        dibujarBarraVolumen();

        btnVolumenMenos.dibujar(batch);
        btnVolumenMas.dibujar(batch);
        btnVolver.dibujar(batch);

        String mensajeEsc = "ESC para volver al menú";
        infoFuente.draw(batch, mensajeEsc, 50, 650);

        batch.end();
    }

    private void dibujarBarraVolumen() {
        float barraAncho = 400;
        float barraAlto = 30;
        float barraX = Configuracion.ANCHO / 2f - barraAncho / 2f;
        float barraY = Configuracion.ALTO / 2f - 20;

        // Fondo de la barra (gris oscuro)
        Render.shapeRenderer.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);
        Render.shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 0.9f);
        Render.shapeRenderer.rect(barraX, barraY, barraAncho, barraAlto);

        // Barra de volumen (verde)
        Render.shapeRenderer.setColor(0.2f, 0.7f, 0.3f, 0.9f);
        Render.shapeRenderer.rect(barraX, barraY, barraAncho * volumenActual, barraAlto);
        Render.shapeRenderer.end();

        // Borde de la barra
        Render.shapeRenderer.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Line);
        Render.shapeRenderer.setColor(0.2f, 0.4f, 0.6f, 1f);
        Render.shapeRenderer.rect(barraX, barraY, barraAncho, barraAlto);
        Render.shapeRenderer.end();
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
        if (textoFuente != null) textoFuente.dispose();
        if (infoFuente != null) infoFuente.dispose();
        if (btnVolumenMenos != null) btnVolumenMenos.dispose();
        if (btnVolumenMas != null) btnVolumenMas.dispose();
        if (btnVolver != null) btnVolver.dispose();
    }
}
