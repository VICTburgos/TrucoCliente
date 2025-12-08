package trucoarg.personajesDosJugadores;

public class Truco extends Canto {

    private static final int PUNTOS_TRUCO = 2;
    private static final int PUNTOS_RETRUCO = 3;
    private static final int PUNTOS_VALE_CUATRO = 4;
    private static final int PUNTOS_SIN_CANTO = 1;

    @Override
    public boolean cantar(int jugador, String tipoCanto) {
        String cantoLower = tipoCanto.toLowerCase().trim();

        System.out.println("DEBUG Truco.cantar() - J" + jugador + " intenta cantar: " + cantoLower);
        System.out.println("  Estado actual: cantoActual=" + cantoActual +
            ", esperandoRespuesta=" + esperandoRespuesta +
            ", cantoAceptado=" + cantoAceptado);

        if (esperandoRespuesta) {
            System.out.println("  RECHAZADO: Hay canto pendiente");
            return false;
        }

        if (!validarCanto(cantoLower, jugador)) {
            return false;
        }

        cantoActual = cantoLower;
        jugadorQueCanto = jugador;
        esperandoRespuesta = true;
        cantoAceptado = false;

        System.out.println("  Canto registrado. Esperando respuesta del J" + getJugadorQueDebeResponder());
        return true;
    }

    @Override
    protected boolean validarCanto(String tipoCanto, int jugador) {
        switch (tipoCanto) {
            case "truco":
                if (cantoActual != null) {
                    System.out.println("  RECHAZADO: Ya hay canto activo (" + cantoActual + ")");
                    return false;
                }
                return true;

            case "retruco":
                if (cantoActual == null || !cantoActual.equals("truco") || !cantoAceptado) {
                    System.out.println("  RECHAZADO: No hay truco aceptado para retruco");
                    return false;
                }
                if (jugador == jugadorQueCanto) {
                    System.out.println("  RECHAZADO: El mismo jugador no puede retrucarse a sí mismo");
                    return false;
                }
                return true;

            case "vale cuatro":
            case "vale 4":
                if (cantoActual == null || !cantoActual.equals("retruco") || !cantoAceptado) {
                    System.out.println("  RECHAZADO: No hay retruco aceptado para vale cuatro");
                    return false;
                }
                if (jugador == jugadorQueCanto) {
                    System.out.println("  RECHAZADO: El mismo jugador no puede hacer vale cuatro después de su retruco");
                    return false;
                }
                return true;

            default:
                System.out.println("  RECHAZADO: Canto desconocido");
                return false;
        }
    }

    @Override
    public int getPuntos() {
        if (cantoActual == null) {
            return PUNTOS_SIN_CANTO;
        }

        switch (cantoActual) {
            case "truco":
                return PUNTOS_TRUCO;
            case "retruco":
                return PUNTOS_RETRUCO;
            case "vale cuatro":
            case "vale 4":
                return PUNTOS_VALE_CUATRO;
            default:
                return PUNTOS_SIN_CANTO;
        }
    }


    public int puntosDeLaMano() {
        return getPuntos();
    }
}
