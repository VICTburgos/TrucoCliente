package trucoarg.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import trucoarg.pantallas.PantallaDosJugadores;
import trucoarg.personajesSolitario.CartaSolitario;
import java.util.List;

public class EntradaDosJugadores implements InputProcessor {

    private final List<CartaSolitario> misCartas;          // ✅ MIS cartas (abajo)
    private final List<CartaSolitario> cartasOponente;     // ✅ Cartas del oponente (arriba)
    private final PantallaDosJugadores pantalla;

    private boolean escape = false;

    /**
     * Constructor
     * @param misCartas Las cartas del jugador ACTUAL (se muestran abajo)
     * @param cartasOponente Las cartas del OPONENTE (se muestran arriba)
     * @param pantalla Referencia a la pantalla del juego
     */
    public EntradaDosJugadores(List<CartaSolitario> misCartas,
                               List<CartaSolitario> cartasOponente,
                               PantallaDosJugadores pantalla) {
        this.misCartas = misCartas;
        this.cartasOponente = cartasOponente;
        this.pantalla = pantalla;

        System.out.println("🎮 EntradaDosJugadores creado:");
        System.out.println("   Mis cartas: " + misCartas.size());
        System.out.println("   Cartas oponente: " + cartasOponente.size());
    }

    public boolean escape() {
        boolean fuePresionado = escape;
        escape = false;
        return fuePresionado;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Convertir coordenadas de pantalla a coordenadas de juego
        float x = screenX;
        float y = Gdx.graphics.getHeight() - screenY;

        System.out.println("\n🖱️ ===== CLICK DETECTADO =====");
        System.out.println("   Posición: (" + x + ", " + y + ")");

        // Primero verificar clicks en botones
        Boton[] botones = pantalla.getBotones();
        if (botones != null) {
            for (Boton boton : botones) {
                if (boton != null && boton.fueClickeado(x, y)) {
                    System.out.println("✅ Botón clickeado: " + boton.getTexto());
                    pantalla.procesarClickBoton(boton);
                    return true;
                }
            }
        }

        // ✅ SOLO PERMITIR CLICKEAR MIS CARTAS (las de abajo)
        for (int i = 0; i < misCartas.size(); i++) {
            CartaSolitario carta = misCartas.get(i);

            if (carta.fueClickeada(x, y)) {
                System.out.println("✅ MI carta clickeada:");
                System.out.println("   Índice: " + i);
                System.out.println("   ID: " + carta.getId());
                System.out.println("   Ya jugada: " + carta.getYaJugadas());

                if (carta.getYaJugadas()) {
                    System.out.println("   ❌ Carta ya fue jugada");
                    return true;
                }

                // ✅ CRÍTICO: Obtener MI número de jugador
                int miNumero = pantalla.getMiNumeroJugador();
                System.out.println("   🎮 Mi número de jugador: " + miNumero);

                // ✅ Llamar a jugarCarta con MI número
                pantalla.jugarCarta(carta, miNumero);
                return true;
            }
        }

        // ⚠️ Si clickean una carta del oponente, mostrar mensaje
        for (int i = 0; i < cartasOponente.size(); i++) {
            CartaSolitario carta = cartasOponente.get(i);

            if (carta.fueClickeada(x, y)) {
                System.out.println("❌ Click en carta del OPONENTE (bloqueado)");
                return true;
            }
        }

        System.out.println("   ℹ️ Click no procesado");
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            escape = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            escape = false;
            return true;
        }
        return false;
    }

    // Métodos requeridos por InputProcessor
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
}
