package com.revature.planetarium.service.moon;

import com.revature.Setup;
import com.revature.planetarium.entities.Moon;
import com.revature.planetarium.exceptions.MoonFail;

import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import com.revature.planetarium.repository.moon.MoonDaoImp;

public class MoonServiceImpTest {

    private MoonDaoImp moonDaoImp;
    private MoonServiceImp<Moon> moonServiceImp;

    private ArrayList<Moon> existingMoonList = new ArrayList<Moon>();
    private Moon newMoon;

    @Before
    public void setUp() throws Exception {
        Setup.resetTestDatabase();
        moonDaoImp = Mockito.mock(MoonDaoImp.class);
        moonServiceImp = new MoonServiceImp<>(moonDaoImp);
        existingMoonList.addAll(Arrays.asList(
                new Moon(1, "moon1", 1),
                new Moon(2, "moon2", 1),
                new Moon(3, "moon3", 2)));
        newMoon = new Moon(4, "moon4", 1);
    }

    @After
    public void tearDown() throws Exception {
    }

    // MOON CREATE POSITIVE
    @Test
    public void createMoonPos() {
        Mockito.when(moonDaoImp.readMoon(newMoon.getMoonName()))
                .thenReturn(Optional.empty());
        Mockito.when(moonDaoImp.createMoon(newMoon))
                .thenReturn(Optional.of(newMoon));

        Moon result = moonServiceImp.createMoon(newMoon);
        Assert.assertEquals(newMoon, result);
        Mockito.verify(moonDaoImp).readMoon(newMoon.getMoonName());
        Mockito.verify(moonDaoImp).createMoon(newMoon);
    }

    // MOON CREATE NEGATIVE
    @Test
    public void createMoonNegNameTooLong() {
        Moon newMoonNameTooLong = new Moon(1, "name exceeds character limit 30", 1);
        MoonFail e = Assert.assertThrows(MoonFail.class, () -> {
            moonServiceImp.createMoon(newMoonNameTooLong);
        });
        Assert.assertEquals("Moon name must be between 1 and 30 characters", e.getMessage());
        Mockito.verifyNoInteractions(moonDaoImp);
    }

    @Test
    public void createMoonNegNameIsNull() {
        Moon newMoonNameIsNull = new Moon(1, "", 1);
        MoonFail e = Assert.assertThrows(MoonFail.class, () -> {
            moonServiceImp.createMoon(newMoonNameIsNull);
        });
        Assert.assertEquals("Moon name must be between 1 and 30 characters", e.getMessage());
        Mockito.verifyNoInteractions(moonDaoImp);
    }

    @Test
    public void createMoonNegNameNotUnique() {
        Moon newMoonNameNotUnique = new Moon(1, "thisNameTaken", 1);
        Mockito.when(moonDaoImp.readMoon(newMoonNameNotUnique.getMoonName())).thenReturn(Optional.of(newMoonNameNotUnique));
        MoonFail e = Assert.assertThrows(MoonFail.class, () -> {
            moonServiceImp.createMoon(newMoonNameNotUnique);
        });
        Assert.assertEquals("Moon name must be unique", e.getMessage());
        Mockito.verify(moonDaoImp).readMoon(newMoonNameNotUnique.getMoonName());
    }

    // MOON SELECT POSITIVE
    @Test
    public void selectMoon() {
    }

    @Test
    public void selectAllMoons() {
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