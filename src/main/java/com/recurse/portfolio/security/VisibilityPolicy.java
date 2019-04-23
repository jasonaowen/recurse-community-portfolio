package com.recurse.portfolio.security;

import com.recurse.portfolio.data.User;
import lombok.NonNull;
import lombok.Value;

import java.util.Collection;
import java.util.List;

@Value
public class VisibilityPolicy<T> {
    Visibility visibility;
    T privateValue;
    T internalValue;
    T publicValue;

    public T evaluate(@NonNull User author, User requester) {
        return evaluate(List.of(author), requester);
    }

    public T evaluate(@NonNull Collection<User> authors, User requester) {
        if (requester != null && authors.contains(requester)) {
            return privateValue;
        } else if (visibility == Visibility.PRIVATE) {
            throw new VisibilityException(visibility);
        }

        if (requester != null) {
            return internalValue;
        } else if (visibility == Visibility.INTERNAL){
            throw new VisibilityException(visibility);
        }

        return publicValue;
    }
}
