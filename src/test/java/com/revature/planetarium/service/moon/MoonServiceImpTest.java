package com.revature.planetarium.service.moon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.revature.planetarium.entities.Moon;
import com.revature.planetarium.exceptions.MoonFail;
import com.revature.planetarium.repository.moon.MoonDao;

public class MoonServiceImpTest<T> {

    @Mock
    private MoonDao moonDao;

    @InjectMocks
    private MoonServiceImp<T> moonServiceImp;

    private List<Moon> existingMoonList = new ArrayList<Moon>();
    private Moon newMoon;

    @Before
    public void setUp() throws Exception {
        moonDao = Mockito.mock(MoonDao.class);
        moonServiceImp = new MoonServiceImp<T>(moonDao);
        existingMoonList.addAll(Arrays.asList(
                new Moon(1, "moon1", 1),
                new Moon(2, "moon2", 1),
                new Moon(3, "moon3", 2)));
    }

    @After
    public void tearDown() throws Exception {
        Mockito.reset(moonDao);
    }

    // MOON CREATE POSITIVE
    @Test
    public void createMoonPos() {
        newMoon = new Moon(4, "moon4", 1);
        Mockito.when(moonDao.readMoon(newMoon.getMoonName()))
                .thenReturn(Optional.empty());
        Mockito.when(moonDao.createMoon(newMoon))
                .thenReturn(Optional.of(newMoon));

        Moon result = moonServiceImp.createMoon(newMoon);
        Assert.assertEquals(newMoon, result);
        Mockito.verify(moonDao).readMoon(newMoon.getMoonName());
        Mockito.verify(moonDao).createMoon(newMoon);
    }

    // MOON CREATE NEGATIVE
    @Test
    public void createMoonNegNameTooLong() {
        newMoon = new Moon(1, "name exceeds character limit 30", 1);
        MoonFail e = Assert.assertThrows(MoonFail.class, () -> {
            moonServiceImp.createMoon(newMoon);
        });
        Assert.assertEquals("Moon name must be between 1 and 30 characters", e.getMessage());
        Mockito.verifyNoInteractions(moonDao);
    }

    @Test
    public void createMoonNegNameTooShort() {
        newMoon = new Moon(1, "", 1);
        MoonFail e = Assert.assertThrows(MoonFail.class, () -> {
            moonServiceImp.createMoon(newMoon);
        });
        Assert.assertEquals("Moon name must be between 1 and 30 characters", e.getMessage());
        Mockito.verifyNoInteractions(moonDao);
    }

    @Test
    public void createMoonNegNameNotUnique() {
        newMoon = new Moon(1, "thisNameTaken", 1);
        Mockito.when(moonDao.readMoon(newMoon.getMoonName()))
                .thenReturn(Optional.of(newMoon));
        MoonFail e = Assert.assertThrows(MoonFail.class, () -> {
            moonServiceImp.createMoon(newMoon);
        });
        Assert.assertEquals("Moon name must be unique", e.getMessage());
        Mockito.verify(moonDao).readMoon(newMoon.getMoonName());
    }

    @Test
    public void createMoonNegPlanetDoesNotExist() {
        newMoon = new Moon(4, "moon4", 37);
        Mockito.when(moonDao.readMoon(newMoon.getMoonName()))
                .thenReturn(Optional.empty());
        Mockito.when(moonDao.createMoon(newMoon))
                .thenReturn(Optional.empty());
        MoonFail e = Assert.assertThrows(MoonFail.class, () -> {
            moonServiceImp.createMoon(newMoon);
        });
        Assert.assertEquals("Could not create new moon", e.getMessage());
    }

    // MOON SELECT POSITIVE
    @Test
    public void selectMoonPosId() {
        int Id = 1;
        Moon expectedMoon = new Moon(1, "moon1", 1);
        Mockito.when(moonDao.readMoon(Id)).thenReturn(Optional.of(expectedMoon));
        Moon result = ((MoonServiceImp<Integer>) moonServiceImp).selectMoon(Id);
        // Moon result = moonServiceImp.<Integer>selectMoon(Id);
        Assert.assertEquals(expectedMoon, result);
        Mockito.verify(moonDao).readMoon(Id);
    }

    @Test
    public void selectMoonPosName() {
        String name = "moon1";
        Moon expectedMoon = new Moon(1, "moon1", 1);
        Mockito.when(moonDao.readMoon(name)).thenReturn(Optional.of(expectedMoon));
        Moon result = ((MoonServiceImp<String>) moonServiceImp).selectMoon(name);
        // Moon result = moonServiceImp.<String>selectMoon(name);
        Assert.assertEquals(expectedMoon, result);
        Mockito.verify(moonDao).readMoon(name);
    }

    @Test
    public void selectAllMoons() {
        Mockito.when(moonDao.readAllMoons()).thenReturn(existingMoonList);

        List<Moon> result = moonServiceImp.selectAllMoons();
        Assert.assertEquals(existingMoonList, result);
        Mockito.verify(moonDao).readAllMoons();
    }

    @Test
    public void selectByPlanet() {
    }

    @Test
    public void updateMoon() {
    }

    @Test
    public void deleteMoon() {
    }
}