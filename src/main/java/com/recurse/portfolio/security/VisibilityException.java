package com.recurse.portfolio.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class VisibilityException extends RuntimeException {

    public VisibilityException(Visibility requiredVisibility) {
        super(
            String.format("Access denied; required visibility %s",
                requiredVisibility.toString())
        );
    }
}
