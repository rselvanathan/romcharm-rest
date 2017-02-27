package com.romcharm.controllers;

import com.amazonaws.services.sns.model.PublishResult;
import com.romcharm.domain.romcharm.Family;
import com.romcharm.exceptions.NotFoundException;
import com.romcharm.notification.NotificationService;
import com.romcharm.repositories.Repository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.CompletableFuture;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FamiliesControllerTest {

    private static final String email = "family@email.com";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private Repository<Family> familiesRepositoryMock;

    @Mock
    private NotificationService notificationServiceMock;

    @InjectMocks
    private FamiliesController familiesController;

    @Test
    public void whenEmailIsFoundReturnTheFamily() {
        Family family = new Family();

        when(familiesRepositoryMock.findOne(email)).thenReturn(family);

        Family result = familiesController.getFamily(email);

        assertThat(result, is(family));
    }

    @Test
    public void whenEmailIsNotFoundExpectNotFoundException() {
        expectedException.expect(NotFoundException.class);

        when(familiesRepositoryMock.findOne(email)).thenReturn(null);

        familiesController.getFamily(email);
    }

    @Test
    public void whenAddingFamilyAndFamilyIsNewThenAddFamily() {
        when(familiesRepositoryMock.findOne(email)).thenReturn(null);

        Family family = new Family(email, "firstName", "lastName", true, 2);
        Family emailMessage = new Family(email, "firstName", "lastName", true, 2);
        CompletableFuture<PublishResult> completableFuture = new CompletableFuture<>();
        completableFuture.complete(Mockito.mock(PublishResult.class));

        when(familiesRepositoryMock.save(family)).thenReturn(family);

        familiesController.saveFamily(family);

        verify(notificationServiceMock).sendEmailNotification(emailMessage);
        verify(familiesRepositoryMock).save(family);
    }

    @Test
    public void whenNotificationServiceFutureThrowsAnExceptionExpectAValueToBeStillReturned() {
        when(familiesRepositoryMock.findOne(email)).thenReturn(null);

        Family family = new Family(email, "firstName", "lastName", true, 2);
        Family emailMessage = new Family(email, "firstName", "lastName", true, 2);
        CompletableFuture<PublishResult> completableFuture = new CompletableFuture<>();
        completableFuture.completeExceptionally(new RuntimeException("Fake Error"));

        when(familiesRepositoryMock.save(family)).thenReturn(family);

        Family result = familiesController.saveFamily(family);

        verify(notificationServiceMock).sendEmailNotification(emailMessage);
        verify(familiesRepositoryMock).save(family);

        assertThat(result, is(family));
    }

    @Test
    public void whenAddingFamilyAndFamilyIsFoundThenExpectIllegalArgumentException() {
        expectedException.expect(IllegalArgumentException.class);

        Family family = new Family(email, null, null, false, 0);
        when(familiesRepositoryMock.findOne(email)).thenReturn(family);

        familiesController.saveFamily(family);

        verify(familiesRepositoryMock, never()).save(family);
    }
}
