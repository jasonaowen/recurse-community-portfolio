package com.recurse.portfolio.security;

import com.recurse.portfolio.data.User;
import lombok.NonNull;
import lombok.Value;

@Value
public class VisibilityPolicy<T> {
    Visibility visibility;
    T privateValue;
    T internalValue;
    T publicValue;

    public T evaluate(@NonNull User author, User requestor) {
        if (author.equals(requestor)) {
            return privateValue;
        } else if (visibility == Visibility.PRIVATE) {
            throw new VisibilityException(visibility);
        }

        if (requestor != null) {
            return internalValue;
        } else if (visibility == Visibility.INTERNAL){
            throw new VisibilityException(visibility);
        }

        return publicValue;
    }
}
