package com.romcharm.util.dynamoDBConverters;

import com.romcharm.defaults.ProjectButtonTypes;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Romesh Selvan
 */
public class ProjectButtonTypeConverterTest {
    private ProjectButtonTypeConverter projectButtonTypeConverter = new ProjectButtonTypeConverter();

    @Test
    public void whenConvertingAListOfButtonsContainingGITHUBAndGalleryToListOfStringsThenObtainTheStringList() {
        List<String> expected = Arrays.asList("GITHUB", "GALLERY");
        List<String> result = projectButtonTypeConverter.convert(Arrays.asList(ProjectButtonTypes.GITHUB, ProjectButtonTypes.GALLERY));
        assertThat(result, is(expected));
    }

    @Test
    public void whenConvertingBackToProjectButtonTypeFromGITHUBStringObtainTheGithubEnum() {
        List<ProjectButtonTypes> expected = Arrays.asList(ProjectButtonTypes.GITHUB, ProjectButtonTypes.GALLERY);
        List<ProjectButtonTypes> result = projectButtonTypeConverter.unconvert(Arrays.asList("GITHUB", "GALLERY"));
        assertThat(result, is(expected));
    }
}