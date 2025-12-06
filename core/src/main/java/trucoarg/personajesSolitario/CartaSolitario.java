package trucoarg.personajesSolitario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import trucoarg.ui.EntradasSolitario;
import trucoarg.utiles.Configuracion;
import trucoarg.utiles.PalosCartas;
import trucoarg.utiles.Recursos;

public class CartaSolitario extends Sprite {
    private final int NUMERO;
    private final int NIVEL;
    private final PalosCartas PALOS_CARTAS;
    private Vector2 posicion;
    private float velocidad = 300f;
    private boolean yaJugadas;

    private Rectangle qcyo;

    public int id;


    public CartaSolitario(int NUMERO, PalosCartas PALOS_CARTAS, String rutaImagen, float y, float x, int NIVEL, int id) {
        super(new Texture(rutaImagen));
        this.NUMERO = NUMERO;
        this.NIVEL= NIVEL;
        this.PALOS_CARTAS = PALOS_CARTAS;
        this.id = id;
        posicion = new Vector2(x, y);
        setPosition(posicion.x, posicion.y);

        qcyo= new Rectangle(posicion.x, posicion.y, getWidth(), getHeight());
    }

    public PalosCartas getPALOS_CARTAS() {
        return PALOS_CARTAS;
    }

    public int getNUMERO() {
        return NUMERO;
    }
    public int getNIVEL(){return NIVEL;}
    public boolean getYaJugadas() { return yaJugadas; }
    public int getId() {return id;}
    public void setYaJugadas(boolean y) { this.yaJugadas = y; }


    public void dibujar(SpriteBatch b) {
        super.draw(b);
    }

    public void mover(EntradasSolitario entradas) {
        float delta = Gdx.graphics.getDeltaTime();
        boolean seMovio = false;

        if (entradas.isArriba()) {
            posicion.y += velocidad * delta;
            seMovio = true;
        }
        if (entradas.isAbajo()) {
            posicion.y -= velocidad * delta;
            seMovio = true;
        }
        if (entradas.isDerecha()) {
            posicion.x += velocidad * delta;
            seMovio = true;
        }
        if (entradas.isIzquierda()) {
            posicion.x -= velocidad * delta;
            seMovio = true;
        }

        if (seMovio) {
            posicion.x = MathUtils.clamp(posicion.x, 0, Configuracion.ANCHO - getWidth());
            posicion.y = MathUtils.clamp(posicion.y, 0, Configuracion.ALTO - getHeight());


            setPosition(posicion.x, posicion.y);
            qcyo.setPosition(posicion);

        }
    }

    public Rectangle getQcyo(){
        return qcyo;
    }


    public void setPosicion(Vector2 nuevaPosicion) {
        posicion.set(nuevaPosicion);
        setPosition(posicion.x, posicion.y);
        qcyo.setPosition(posicion);
        qcyo.setSize(getWidth(), getHeight());
    }

    public boolean fueClickeada(float x, float y) {
        return qcyo.contains(x, y);
    }

@Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        if (qcyo != null) {
            qcyo.setSize(width, height);
        }
    }

    public void moverAlCentro(float deltaTime) {
        float objetivoX = Configuracion.ANCHO / 2f - getWidth() / 2f;
        float objetivoY = Configuracion.ALTO / 2f - getHeight() / 2f;

        posicion.set(objetivoX, objetivoY);
        setPosition(posicion.x, posicion.y);
        qcyo.setPosition(posicion);
    }




    public Vector2 getPosicion() {
        return new Vector2(posicion);
    }

    public void setVelocidad(float nuevaVelocidad) {
        this.velocidad = nuevaVelocidad;
    }


    public float getVelocidad() {
        return velocidad;
    }


    // En CartaSolitario.java
    public void mostrarDorso() {
        setTexture(new Texture(Recursos.IMAGEN_DORSO));
        }
    }
