package com.recurse.portfolio.security;

public enum Visibility {
    /**
     * Things with private visibility are only shown to the author.
     * This allows for preparing prerelease projects, personal notes,
     * and so on.
     */
    PRIVATE,

    /**
     * Things with internal visibility are only shown to logged-in users.
     */
    INTERNAL,

    /**
     * Things with public visibility are shown to everyone.
     */
    PUBLIC
}
