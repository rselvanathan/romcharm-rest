package com.romcharm.controllers;

import com.romcharm.authorization.JWTUtil;
import com.romcharm.defaults.APIErrorCode;
import com.romcharm.domain.Login;
import com.romcharm.domain.Token;
import com.romcharm.domain.User;
import com.romcharm.exceptions.NotFoundException;
import com.romcharm.repositories.UserRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UsersControllerTest {

    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final String ROLE = "ROLE";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private JWTUtil jwtUtilMock;

    @Mock
    private UserRepository userRepositoryMock;

    @InjectMocks
    private UsersController usersController;

    @Test
    public void whenGettingUserAndUserDoesNotExistExpectNotFoundExceptionForNotFoundUser() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(APIErrorCode.USER_NOT_FOUND.getReason());

        when(userRepositoryMock.findOne(USERNAME)).thenReturn(null);

        usersController.getUser(new Login(USERNAME, PASSWORD));
    }

    @Test
    public void whenGettingUserAndPasswordDoesNotMatchThenExpectNotFoundExceptionForMismatchPassword() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(APIErrorCode.PASSWORD_INCORRECT.getReason());

        String someOtherPassword = "someOtherPassword";
        User userResponse = new User(USERNAME, someOtherPassword, ROLE);
        when(userRepositoryMock.findOne(USERNAME)).thenReturn(userResponse);

        usersController.getUser(new Login(USERNAME, PASSWORD));
    }

    @Test
    public void whenGettingUserExpectAToken() {
        User userResponse = getUser();
        when(userRepositoryMock.findOne(USERNAME)).thenReturn(userResponse);

        String token = "token";
        when(jwtUtilMock.generateToken(userResponse)).thenReturn(token);

        Token result = usersController.getUser(new Login(USERNAME, PASSWORD));

        assertThat(result.getToken(), is(token));
    }

    @Test
    public void whenSavingUserAndUserAlreadyExistsExpectIllegalArgumentException() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(APIErrorCode.USER_EXISTS.getReason());

        User user = getUser();
        when(userRepositoryMock.findOne(USERNAME)).thenReturn(user);

        usersController.saveUser(user);
    }


    @Test
    public void whenSavingUserAndUserIsNewItShouldBeSaved() {
        User user = getUser();
        when(userRepositoryMock.findOne(USERNAME)).thenReturn(null);

        usersController.saveUser(user);

       verify(userRepositoryMock).save(user);
    }

    private User getUser() {
        return new User(USERNAME, PASSWORD, ROLE);
    }
}