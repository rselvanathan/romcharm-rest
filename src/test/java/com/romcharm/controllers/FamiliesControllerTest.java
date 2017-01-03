package com.romcharm.controllers;

import com.romcharm.domain.Family;
import com.romcharm.exceptions.NotFoundException;
import com.romcharm.repositories.FamiliesRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FamiliesControllerTest {

    private static final String email = "family@email.com";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private FamiliesRepository familiesRepositoryMock;

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

        Family family = Family.builder().email(email).build();
        familiesController.saveFamily(family);

        verify(familiesRepositoryMock).save(family);
    }

    @Test
    public void whenAddingFamilyAndFamilyIsFoundThenExpectIllegalArgumentException() {
        expectedException.expect(IllegalArgumentException.class);

        Family family = Family.builder().email(email).build();
        when(familiesRepositoryMock.findOne(email)).thenReturn(family);

        familiesController.saveFamily(family);

        verify(familiesRepositoryMock, never()).save(family);
    }
}
