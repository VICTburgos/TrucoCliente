package trucoarg.personajesDosJugadores;


public abstract class Canto {

    protected String cantoActual = null;
    protected int jugadorQueCanto = -1;
    protected boolean esperandoRespuesta = false;
    protected boolean cantoAceptado = false;


    public void reset() {
        cantoActual = null;
        jugadorQueCanto = -1;
        esperandoRespuesta = false;
        cantoAceptado = false;
    }


    public abstract boolean cantar(int jugador, String tipoCanto);


    public int responder(int jugador, boolean quiero) {
        System.out.println("DEBUG " + this.getClass().getSimpleName() + ".responder() - J" + jugador +
            " responde: " + (quiero ? "QUIERO" : "NO QUIERO"));
        System.out.println("  Estado: cantoActual=" + cantoActual + ", jugadorQueCanto=" + jugadorQueCanto);

        if (!esperandoRespuesta) {
            System.out.println("  ERROR: No hay canto pendiente");
            return -1;
        }

        if (jugador == jugadorQueCanto) {
            System.out.println("  ERROR: El jugador que cantó no puede responder");
            return -1;
        }

        esperandoRespuesta = false;

        if (quiero) {
            // Acepta el canto, se sigue jugando
            cantoAceptado = true;
            System.out.println("  Canto aceptado. Se juega por " + getPuntos() + " puntos");
            return 0;
        } else {
            // Rechaza el canto, gana el que cantó
            System.out.println("  Canto rechazado. Gana J" + jugadorQueCanto);
            return jugadorQueCanto;
        }
    }


    public abstract int getPuntos();


    protected abstract boolean validarCanto(String tipoCanto, int jugador);

    // Getters comunes
    public boolean estaEsperandoRespuesta() {
        return esperandoRespuesta;
    }

    public int getJugadorQueDebeResponder() {
        if (!esperandoRespuesta) return -1;
        return jugadorQueCanto == 1 ? 2 : 1;
    }

    public String getCantoActual() {
        return cantoActual;
    }

    public int getJugadorQueCanto() {
        return jugadorQueCanto;
    }

    public boolean isCantoAceptado() {
        return cantoAceptado;
    }
}
