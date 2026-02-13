package dev.test.projedata.autoflex.api.exceptions;

import java.io.Serial;

public class DatabaseException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 9200566300960689483L;

    public DatabaseException(String msg) {
        super(msg);
    }
}
