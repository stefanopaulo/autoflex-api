package dev.test.projedata.autoflex.api.exceptions;

import java.io.Serial;

public class ResourceNotFoundException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = -2910628588629101301L;

    public ResourceNotFoundException(String msg) {
        super(msg);
    }
}
