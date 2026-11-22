package com.cafeteria.extras;

import com.cafeteria.modelo.CafeteriaException;

/** La personalización pedida no existe o no se puede aplicar al producto. */
public class PersonalizacionInvalidaException extends CafeteriaException {

    private static final long serialVersionUID = 1L;

    public PersonalizacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
