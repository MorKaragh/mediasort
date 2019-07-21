package ru.mewory.photohost.service.socnet;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class InstagramLoaderTest {


    @Test
    public void extractIdFromPath() {
        assertEquals("B0JH0VWHf5A",
                InstagramLoader.extractIdFromPath("https://www.instagram.com/p/B0JH0VWHf5A/"));
    }
}