package com.recurse.portfolio.security;

import com.recurse.portfolio.data.User;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class VisibilityPolicyTest {
    private User author = mock(User.class);
    private User peer = mock(User.class);

    @Test
    public void authorPrivateGrants() {
        var policy = createPolicy(Visibility.PRIVATE);
        assertThat(policy.evaluate(author, author), is("private"));
    }

    @Test
    public void authorInternalGrants() {
        var policy = createPolicy(Visibility.INTERNAL);
        assertThat(policy.evaluate(author, author), is("private"));
    }

    @Test
    public void authorPublicGrants() {
        var policy = createPolicy(Visibility.PUBLIC);
        assertThat(policy.evaluate(author, author), is("private"));
    }


    @Test(expected = VisibilityException.class)
    public void peerPrivateDenies() {
        var policy = createPolicy(Visibility.PRIVATE);
        policy.evaluate(author, peer);
    }

    @Test
    public void peerInternalGrants() {
        var policy = createPolicy(Visibility.INTERNAL);
        assertThat(policy.evaluate(author, peer), is("internal"));
    }

    @Test
    public void peerPublicGrants() {
        var policy = createPolicy(Visibility.PUBLIC);
        assertThat(policy.evaluate(author, peer), is("internal"));
    }

    @Test(expected = VisibilityException.class)
    public void anonPrivateDenies() {
        var policy = createPolicy(Visibility.PRIVATE);
        policy.evaluate(author, null);
    }

    @Test(expected = VisibilityException.class)
    public void anonInternalDenies() {
        var policy = createPolicy(Visibility.INTERNAL);
        policy.evaluate(author, null);
    }

    @Test
    public void anonPublicGrants() {
        var policy = createPolicy(Visibility.PUBLIC);
        assertThat(policy.evaluate(author, null), is("public"));
    }

    @Test(expected = VisibilityException.class)
    public void emptySetPrivateDenies() {
        var policy = createPolicy(Visibility.PRIVATE);
        policy.evaluate(Lists.emptyList(), peer);
    }

    @Test
    public void singleAuthorPrivateAllows() {
        var policy = createPolicy(Visibility.PRIVATE);
        assertThat(policy.evaluate(List.of(author), author), is("private"));
    }

    @Test
    public void multipleAuthorsPrivateAllows() {
        var policy = createPolicy(Visibility.PRIVATE);
        assertThat(
            policy.evaluate(List.of(author, peer), author),
            is("private")
        );
    }

    private VisibilityPolicy<String> createPolicy(Visibility visibility) {
        return new VisibilityPolicy<>(
            visibility,
            "private",
            "internal",
            "public"
        );
    }
}
