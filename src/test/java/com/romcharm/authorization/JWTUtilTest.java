package com.romcharm.authorization;

import com.romcharm.domain.User;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

public class JWTUtilTest {

    private static final String USERNAME = "USERNAME";
    private static final String ROLE = "ROLE";

    private JWTUtil jwtUtil = new JWTUtil();

    @Before
    public void setup() {
        jwtUtil.setJwtSecret("secret");
    }

    @Test
    public void generateTokenShouldGenerateANonEmptyToken() {
        User build = User.builder().username(USERNAME).role(ROLE).build();
        String result = jwtUtil.generateToken(build);
        assertThat(result, is(not(nullValue())));
    }

    @Test
    public void whenRetrievingTokenCorrectRoleShouldBeReturned() {
        User build = User.builder().username(USERNAME).role(ROLE).build();
        String token = jwtUtil.generateToken(build);
        Optional<String> tokenRole = jwtUtil.getTokenRole(token);
        assertThat(tokenRole.get(), is(ROLE));
    }

    @Test
    public void whenRetrievingTokenCorrectUsernameShouldBeReturned() {
        User build = User.builder().username(USERNAME).role(ROLE).build();
        String token = jwtUtil.generateToken(build);
        Optional<String> tokenUsername = jwtUtil.getTokenUsername(token);
        assertThat(tokenUsername.get(), is(USERNAME));
    }

    @Test
    public void whenRetrievingTokenUsernameAndTokenIsInvalidExpectEmptyOptional() {
        String badToken = "bad";
        Optional<String> tokenUsername = jwtUtil.getTokenUsername(badToken);
        assertThat(tokenUsername, is(Optional.empty()));
    }

    @Test
    public void whenRetrievingTokeRoleAndTokenIsInvalidExpectEmptyOptional() {
        String badToken = "bad";
        Optional<String> tokenUsername = jwtUtil.getTokenRole(badToken);
        assertThat(tokenUsername, is(Optional.empty()));
    }
}