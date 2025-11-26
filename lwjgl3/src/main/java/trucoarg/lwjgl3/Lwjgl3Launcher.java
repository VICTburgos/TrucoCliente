package trucoarg.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import trucoarg.principal.Principal;
import trucoarg.utiles.Configuracion;

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("TRUCO ARGENTINO... CLIENTE");
        config.setWindowedMode(Configuracion.ANCHO, Configuracion.ALTO);
        new Lwjgl3Application(new Principal(), config);
    }
}
