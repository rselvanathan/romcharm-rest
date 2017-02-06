package com.romcharm.util.dynamoDBConverters;

import com.romcharm.domain.mypage.GalleryLink;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Romesh Selvan
 */
public class GalleryLinkListConverterTest {
    private GalleryLinkListConverter galleryLinkListConverter = new GalleryLinkListConverter();

    @Test
    public void mapGalleryLinkListToAStringJsonContainingObjects() {
        String expected = "[{\"url\":\"url1\"},{\"url\":\"url2\"}]";
        List<GalleryLink> galleryLinks = Arrays.asList(new GalleryLink("url1"), new GalleryLink("url2"));
        String result = galleryLinkListConverter.convert(galleryLinks);
        assertThat(result, is(expected));
    }

    @Test
    public void mapJsonStringToAListOfGalleryLinks() {
        List<GalleryLink> expectedList = Arrays.asList(new GalleryLink("url1"), new GalleryLink("url2"));
        String jsonString = "[{\"url\":\"url1\"},{\"url\":\"url2\"}]";
        List<GalleryLink> result = galleryLinkListConverter.unconvert(jsonString);
        assertThat(result, is(expectedList));
    }
}