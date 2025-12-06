package trucoarg.utiles;

import trucoarg.personajesSolitario.CartaSolitario;

public enum CartasFinales {
    // BASTO
    C1B(1, PalosCartas.BASTO, Recursos.ANCHO_BASTO, 13),
    C2B(2, PalosCartas.BASTO, Recursos.DOS_BASTO, 9),
    C3B(3, PalosCartas.BASTO, Recursos.TRES_BASTO, 10),
    C4B(4, PalosCartas.BASTO, Recursos.CUATRO_BASTO, 1),
    C5B(5, PalosCartas.BASTO, Recursos.CINCO_BASTO, 2),
    C6B(6, PalosCartas.BASTO, Recursos.SEIS_BASTO, 3),
    C7B(7, PalosCartas.BASTO, Recursos.SIETE_BASTO, 4),
    C10B(10, PalosCartas.BASTO, Recursos.DIEZ_BASTO, 5),
    C11B(11, PalosCartas.BASTO, Recursos.ONCE_BASTO, 6),
    C12B(12, PalosCartas.BASTO, Recursos.DOCE_BASTO, 7),

    // COPA
    C1C(1, PalosCartas.COPA, Recursos.ANCHO_COPA, 8),
    C2C(2, PalosCartas.COPA, Recursos.DOS_COPA, 9),
    C3C(3, PalosCartas.COPA, Recursos.TRES_COPA, 10),
    C4C(4, PalosCartas.COPA, Recursos.CUATRO_COPA, 1),
    C5C(5, PalosCartas.COPA, Recursos.CINCO_COPA, 2),
    C6C(6, PalosCartas.COPA, Recursos.SEIS_COPA, 3),
    C7C(7, PalosCartas.COPA, Recursos.SIETE_COPA, 4),
    C10C(10, PalosCartas.COPA, Recursos.DIEZ_COPA, 5),
    C11C(11, PalosCartas.COPA, Recursos.ONCE_COPA, 6),
    C12C(12, PalosCartas.COPA, Recursos.DOCE_COPA, 7),

    // ESPADA
    C1E(1, PalosCartas.ESPADA, Recursos.ANCHO_ESPADA, 14),
    C2E(2, PalosCartas.ESPADA, Recursos.DOS_ESPADA, 9),
    C3E(3, PalosCartas.ESPADA, Recursos.TRES_ESPADA, 10),
    C4E(4, PalosCartas.ESPADA, Recursos.CUATRO_ESPADA, 1),
    C5E(5, PalosCartas.ESPADA, Recursos.CINCO_ESPADA, 2),
    C6E(6, PalosCartas.ESPADA, Recursos.SEIS_ESPADA, 3),
    C7E(7, PalosCartas.ESPADA, Recursos.SIETE_ESPADA, 12),
    C10E(10, PalosCartas.ESPADA, Recursos.DIEZ_ESPADA, 5),
    C11E(11, PalosCartas.ESPADA, Recursos.ONCE_ESPADA, 6),
    C12E(12, PalosCartas.ESPADA, Recursos.DOCE_ESPADA, 7),

    // ORO
    C1O(1, PalosCartas.ORO, Recursos.ANCHO_ORO, 8),
    C2O(2, PalosCartas.ORO, Recursos.DOS_ORO, 9),
    C3O(3, PalosCartas.ORO, Recursos.TRES_ORO, 10),
    C4O(4, PalosCartas.ORO, Recursos.CUATRO_ORO, 1),
    C5O(5, PalosCartas.ORO, Recursos.CINCO_ORO, 2),
    C6O(6, PalosCartas.ORO, Recursos.SEIS_ORO, 3),
    C7O(7, PalosCartas.ORO, Recursos.SIETE_ORO, 11),
    C10O(10, PalosCartas.ORO, Recursos.DIEZ_ORO, 5),
    C11O(11, PalosCartas.ORO, Recursos.ONCE_ORO, 6),
    C12O(12, PalosCartas.ORO, Recursos.DOCE_ORO, 7);

    private final int NUMERO;
    private final PalosCartas PALOS_CARTAS;
    private final String RUTA;
    private final int NIVEL;
    public int getId() {
        return this.ordinal();
    }
    CartasFinales(int NUMERO, PalosCartas PALOS_CARTAS, String RUTA, int NIVEL) {
        this.NUMERO = NUMERO;
        this.PALOS_CARTAS = PALOS_CARTAS;
        this.RUTA = RUTA;
        this.NIVEL= NIVEL;
    }

    public CartaSolitario crearCarta(){
        return new CartaSolitario(NUMERO, PALOS_CARTAS, RUTA,390, 60, NIVEL, this.ordinal());
    }
}

