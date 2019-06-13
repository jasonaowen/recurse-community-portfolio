package com.recurse.portfolio.data;

import lombok.NonNull;
import lombok.Value;

@Value
public class DisplayAuthor {
    int userId;
    String name;
    String imageUrl;

    public static DisplayAuthor fromUserForUser(
        @NonNull User sourceUser,
        User requestingUser
    ) {
        switch(sourceUser.getProfileVisibility()) {
            case PRIVATE:
                if (sourceUser.equals(requestingUser)) {
                    return useInternal(sourceUser);
                } else {
                    return anonymous();
                }
            case INTERNAL:
                if (requestingUser == null) {
                    return anonymous();
                } else {
                    return useInternal(sourceUser);
                }
            case PUBLIC:
                if (requestingUser == null) {
                    return usePublic(sourceUser);
                } else {
                    return useInternal(sourceUser);
                }
            default:
                return anonymous();
        }
    }

    private static DisplayAuthor anonymous() {
        return new DisplayAuthor(
            0,
            "Anonymous",
            "/user-placeholder.png"
        );
    }

    private static DisplayAuthor usePublic(User user) {
        return new DisplayAuthor(
            user.getUserId(),
            user.getPublicName(),
            user.getPublicImageUrl()
        );
    }

    private static DisplayAuthor useInternal(User user) {
        return new DisplayAuthor(
            user.getUserId(),
            user.getInternalName(),
            user.getInternalImageUrl()
        );
    }
}
