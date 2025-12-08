package trucoarg.personajesDosJugadores;

import com.badlogic.gdx.graphics.g2d.Sprite;
import trucoarg.personajesSolitario.CartaSolitario;

import java.util.ArrayList;
import java.util.List;

public class JugadorBase extends Sprite {
    private int id; // 1 o 2
    private List<CartaSolitario> mano;
    private boolean esMano;

    public JugadorBase(int id, boolean esMano) {
        this.id = id;
        this.esMano = esMano;
        this.mano = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public boolean esMano() {
        return esMano;
    }

    public void setEsMano(boolean esMano) {
        this.esMano = esMano;
    }

    public List<CartaSolitario> getMano() {
        return mano;
    }
}
