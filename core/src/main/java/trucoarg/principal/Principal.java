package trucoarg.principal;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import trucoarg.pantallas.PantallaMenu;
import trucoarg.pantallas.PantallaSeleccionPuntos;
import trucoarg.utiles.Recursos;
import trucoarg.utiles.Render;

public class Principal extends Game {
    public static SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        Render.batch = batch;
        Render.app = this;
        Render.shapeRenderer = new ShapeRenderer();
        Recursos.cargarCanciones();
        ponerMusica();
        this.setScreen(new PantallaMenu());
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        Recursos.liberar();
        batch.dispose();
        if (Render.shapeRenderer != null) {
            Render.shapeRenderer.dispose();
        }
    }

    private void ponerMusica() {
        if (!Recursos.MUSICA_GENERAL.isPlaying()) {
            Recursos.MUSICA_GENERAL.setLooping(true);
            Recursos.MUSICA_GENERAL.setVolume(0.4f);
         //   Recursos.MUSICA_GENERAL.play();
        }
    }
}
