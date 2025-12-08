package trucoarg.personajesDosJugadores;

import trucoarg.personajesSolitario.CartaSolitario;
import java.util.List;


public class Envido extends Canto {

    private static final int PUNTOS_ENVIDO = 2;
    private static final int PUNTOS_REAL_ENVIDO = 3;
    private static final int PUNTOS_FALTA_ENVIDO_BASE = 15; // Por ahora fijo, luego calcular

    // Control de secuencia de cantos
    private int puntosAcumulados = 0; // Puntos totales en juego
    private boolean huboEnvido = false;
    private boolean huboRealEnvido = false;
    private boolean huboFaltaEnvido = false;
    private int contadorEnvidos = 0; // Cuántos "envido" se cantaron

    @Override
    public void reset() {
        super.reset();
        puntosAcumulados = 0;
        huboEnvido = false;
        huboRealEnvido = false;
        huboFaltaEnvido = false;
        contadorEnvidos = 0;
    }

    @Override
    public boolean cantar(int jugador, String tipoCanto) {
        String cantoLower = tipoCanto.toLowerCase().trim();

        System.out.println("DEBUG Envido.cantar() - J" + jugador + " intenta cantar: " + cantoLower);
        System.out.println("  Estado actual: cantoActual=" + cantoActual + ", esperandoRespuesta=" + esperandoRespuesta);
        System.out.println("  Puntos acumulados: " + puntosAcumulados);

        // CLAVE: Si hay canto pendiente, solo el jugador que debe responder puede "subir"
        if (esperandoRespuesta) {
            int jugadorQueDebeResponder = getJugadorQueDebeResponder();

            if (jugador != jugadorQueDebeResponder) {
                System.out.println("  RECHAZADO: No es el jugador que debe responder");
                return false;
            }

            System.out.println("  J" + jugador + " está SUBIENDO la apuesta de " + cantoActual + " a " + cantoLower);
        }

        if (!validarCanto(cantoLower, jugador)) {
            return false;
        }

        // ACUMULAR PUNTOS según el nuevo canto
        acumularPuntos(cantoLower);

        // Actualizar estado
        cantoActual = cantoLower;
        jugadorQueCanto = jugador;
        esperandoRespuesta = true;
        cantoAceptado = false;

        System.out.println("  ÉXITO: Canto registrado. Puntos en juego: " + puntosAcumulados);
        System.out.println("  Esperando respuesta del J" + getJugadorQueDebeResponder());
        return true;
    }

    private void acumularPuntos(String tipoCanto) {
        switch (tipoCanto) {
            case "envido":
                puntosAcumulados += PUNTOS_ENVIDO;
                huboEnvido = true;
                contadorEnvidos++;
                break;
            case "real envido":
                puntosAcumulados += PUNTOS_REAL_ENVIDO;
                huboRealEnvido = true;
                break;
            case "falta envido":
                // Falta envido se calcula después
                huboFaltaEnvido = true;
                break;
        }
    }

    @Override
    protected boolean validarCanto(String tipoCanto, int jugador) {
        switch (tipoCanto) {
            case "envido":
                // Si no hay canto activo, se puede cantar libremente
                if (cantoActual == null) {
                    if (contadorEnvidos >= 2) {
                        System.out.println("  RECHAZADO: Ya se cantó envido 2 veces");
                        return false;
                    }
                    return true;
                }

                // Si hay canto activo, solo se puede "subir" si el canto actual es envido
                if (!cantoActual.equals("envido")) {
                    System.out.println("  RECHAZADO: No se puede cantar envido después de " + cantoActual);
                    return false;
                }

                // Máximo 2 envidos acumulados
                if (contadorEnvidos >= 2) {
                    System.out.println("  RECHAZADO: Ya se cantó envido 2 veces");
                    return false;
                }
                return true;

            case "real envido":
                // Real envido se puede cantar:
                // 1. Al inicio (sin canto previo)
                // 2. Como respuesta a envido (subiendo)
                if (cantoActual == null) {
                    return true;
                }

                if (cantoActual.equals("envido")) {
                    return true; // Subiendo desde envido
                }

                System.out.println("  RECHAZADO: Real envido solo al inicio o después de envido");
                return false;

            case "falta envido":
                // Falta envido se puede cantar:
                // 1. Al inicio (sin canto previo)
                // 2. Como respuesta a envido o real envido (subiendo)
                if (cantoActual == null) {
                    return true;
                }

                if (cantoActual.equals("envido") || cantoActual.equals("real envido")) {
                    return true; // Subiendo
                }

                System.out.println("  RECHAZADO: Falta envido solo al inicio o en secuencia de envido");
                return false;

            default:
                System.out.println("  RECHAZADO: Canto desconocido");
                return false;
        }
    }

    @Override
    public int responder(int jugador, boolean quiero) {
        System.out.println("DEBUG Envido.responder() - J" + jugador + " responde: " + (quiero ? "QUIERO" : "NO QUIERO"));
        System.out.println("  Estado: cantoActual=" + cantoActual + ", puntosAcumulados=" + puntosAcumulados);

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
            cantoAceptado = true;
            int pts = getPuntos();
            System.out.println("  Canto aceptado. Se juega por " + pts + " puntos");
            return 0; // Continúa el juego, se deben mostrar cartas
        } else {
            // NO QUIERO: gana quien cantó, pero solo gana 1 punto
            System.out.println("  Canto rechazado. J" + jugadorQueCanto + " gana 1 punto");
            return jugadorQueCanto;
        }
    }

    @Override
    public int getPuntos() {
        if (huboFaltaEnvido) {
            // TODO: Implementar cálculo según puntos de cada jugador
            // Por ahora retorna los puntos acumulados hasta ahora + falta envido
            return puntosAcumulados + PUNTOS_FALTA_ENVIDO_BASE;
        }

        return puntosAcumulados;
    }

    /**
     * Retorna cuántos puntos gana quien rechaza (siempre 1)
     */
    public int getPuntosRechazo() {
        return 1;
    }

    /**
     * Verifica qué cantos de envido están disponibles como respuesta
     * al canto actual (para "subir" la apuesta)
     */
    public boolean puedeSubirConEnvido() {
        if (!esperandoRespuesta) return false;
        if (cantoActual == null) return false;

        // Se puede subir con envido solo si el canto actual es envido y no se llegó al máximo
        return cantoActual.equals("envido") && contadorEnvidos < 2;
    }

    public boolean puedeSubirConRealEnvido() {
        if (!esperandoRespuesta) return false;
        if (cantoActual == null) return false;

        // Se puede subir con real envido desde envido (y no se cantó real antes)
        return cantoActual.equals("envido") && !huboRealEnvido;
    }

    public boolean puedeSubirConFaltaEnvido() {
        if (!esperandoRespuesta) return false;
        if (cantoActual == null) return false;

        // Se puede subir con falta envido desde envido o real envido (y no se cantó falta antes)
        return (cantoActual.equals("envido") || cantoActual.equals("real envido")) && !huboFaltaEnvido;
    }

    public int calcularEnvido(List<CartaSolitario> mano) {
        if (mano == null || mano.size() != 3) return 0;

        int[] valores = new int[3];
        String[] palos = new String[3];

        for (int i = 0; i < 3; i++) {
            CartaSolitario carta = mano.get(i);
            valores[i] = obtenerValorEnvido(carta);
            palos[i] = String.valueOf(carta.getPALOS_CARTAS());
        }

        int maxEnvido = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = i + 1; j < 3; j++) {
                if (palos[i].equals(palos[j])) {
                    int envido = 20 + valores[i] + valores[j];
                    maxEnvido = Math.max(maxEnvido, envido);
                }
            }
        }

        if (maxEnvido == 0) {
            maxEnvido = Math.max(valores[0], Math.max(valores[1], valores[2]));
        }

        return maxEnvido;
    }

    private int obtenerValorEnvido(CartaSolitario carta) {
        int numero = carta.getNUMERO();

        if (numero >= 1 && numero <= 7) {
            return numero;
        }
        if (numero == 10 || numero == 11 || numero == 12) {
            return 0;
        }
        return 0;
    }
}
