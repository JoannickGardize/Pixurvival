package com.pixurvival.core;

import lombok.Getter;

import java.util.Arrays;

@Getter
public class LoadGameException extends Exception {

    private static final long serialVersionUID = 1L;

    public enum Reason {
        PARSE_EXCEPTION,
        INCOMPATIBLE_CONTENT_PACK_VERSION,
        INCOMPATIBLE_VERSION,
        NOT_PLAYABLE_IN_SOLO,
        NOT_SAME_CONTENT_PACK,
        CONTENT_PACK_FILE_NOT_FOUND,
        CONTAINS_ERRORS,
        OTHER
    }

    private Reason reason;
    private Object[] args;

    public LoadGameException(Reason reason, Object... args) {
        super(reason.name() + Arrays.toString(args));
        this.reason = reason;
        this.args = args;
    }
}
