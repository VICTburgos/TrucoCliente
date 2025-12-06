package trucoarg.utiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class Recursos {

    public static final String FONDOMENU= "imagenes/fondoMenu.png";
    public static final String FONDOUNJUGADOR= "imagenes/fondoUnJugador.png";
    public static final String FONDODOSJUGADORES= "imagenes/fondoDosJugadores.png";
    public static final String IMAGEN_TUTORIAL= "imagenes/pantallaTutorial.png";
    public static final String IMAGEN_DORSO= "imagenes/dorso.png";


    public static final String FUENTE_MENU= "fuentes/Argentina.ttf";

    public static Music MUSICA_GENERAL;
    public static Music MUSICA_JUEGO;

    public static void cargarCanciones() {
        MUSICA_GENERAL = Gdx.audio.newMusic(Gdx.files.internal("musica/musicaGeneral.mp3"));
        MUSICA_JUEGO = Gdx.audio.newMusic(Gdx.files.internal("musica/musicaJuego.mp3"));

    }

    public static void liberar() {
        if (MUSICA_GENERAL != null) MUSICA_GENERAL.dispose();
        if (MUSICA_JUEGO != null) MUSICA_JUEGO.dispose();


  }
    //SOLO BASTO
    public static final String ANCHO_BASTO= "cartasBasto/AnchoBasto.png";
    public static final String CINCO_BASTO= "cartasBasto/CincoBasto.png";
    public static final String CUATRO_BASTO= "cartasBasto/CuatroBasto.png";
    public static final String DIEZ_BASTO= "cartasBasto/DiezBasto.png";
    public static final String DOCE_BASTO= "cartasBasto/DoceBasto.png";
    public static final String DOS_BASTO= "cartasBasto/DosBasto.png";
    public static final String ONCE_BASTO= "cartasBasto/OnceBasto.png";
    public static final String SEIS_BASTO= "cartasBasto/SeisBasto.png";
    public static final String SIETE_BASTO= "cartasBasto/SieteBasto.png";
    public static final String TRES_BASTO= "cartasBasto/TresBasto.png";

    //SOLO COPA
    public static final String ANCHO_COPA= "cartasCopa/AnchoCopa.png";
    public static final String CINCO_COPA= "cartasCopa/CincoCopa.png";
    public static final String CUATRO_COPA= "cartasCopa/CuatroCopa.png";
    public static final String DIEZ_COPA= "cartasCopa/DiezCopa.png";
    public static final String DOCE_COPA= "cartasCopa/DoceCopa.png";
    public static final String DOS_COPA= "cartasCopa/DosCopa.png";
    public static final String ONCE_COPA= "cartasCopa/OnceCopa.png";
    public static final String SEIS_COPA= "cartasCopa/SeisCopa.png";
    public static final String SIETE_COPA= "cartasCopa/SieteCopa.png";
    public static final String TRES_COPA= "cartasCopa/TresCopa.png";

    //SOLO ESPADA
    public static final String ANCHO_ESPADA= "cartasEspada/AnchoEspada.png";
    public static final String CINCO_ESPADA= "cartasEspada/CincoEspada.png";
    public static final String CUATRO_ESPADA= "cartasEspada/CuatroEspada.png";
    public static final String DIEZ_ESPADA= "cartasEspada/DiezEspada.png";
    public static final String DOCE_ESPADA= "cartasEspada/DoceEspada.png";
    public static final String DOS_ESPADA= "cartasEspada/DosEspada.png";
    public static final String ONCE_ESPADA= "cartasEspada/OnceEspada.png";
    public static final String SEIS_ESPADA= "cartasEspada/SeisEspada.png";
    public static final String SIETE_ESPADA= "cartasEspada/SieteEspada.png";
    public static final String TRES_ESPADA= "cartasEspada/TresEspada.png";

    //SOLO ORO
    public static final String ANCHO_ORO= "cartasOro/AnchoOro.png";
    public static final String CINCO_ORO= "cartasOro/CincoOro.png";
    public static final String CUATRO_ORO= "cartasOro/CuatroOro.png";
    public static final String DIEZ_ORO= "cartasOro/DiezOro.png";
    public static final String DOCE_ORO= "cartasOro/DoceOro.png";
    public static final String DOS_ORO= "cartasOro/DosOro.png";
    public static final String ONCE_ORO= "cartasOro/OnceOro.png";
    public static final String SEIS_ORO= "cartasOro/SeisOro.png";
    public static final String SIETE_ORO= "cartasOro/SieteOro.png";
    public static final String TRES_ORO= "cartasOro/TresOro.png";



    //ZONA COLISIONES
    public static final String ZONA_BASTO= "zonas/zonaBasto.png";
    public static final String ZONA_COPA= "zonas/zonaCopa.png";
    public static final String ZONA_ESPADA= "zonas/zonaEspada.png";
    public static final String ZONA_ORO= "zonas/zonaOro.png";
}
