package com.revature.planetarium.service.moon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

public class MoonServiceImpUnitTest<T> {

    @Mock
    private MoonDao moonDao;

    @InjectMocks
    private MoonServiceImp<T> moonServiceImp;

    private List<Moon> existingMoonList = new ArrayList<Moon>();
    private Moon newMoon;
    private Moon existingMoon;
    private Moon notExistingMoon;
    private Moon updatedMoon;

    @Before
    public void setUp() throws Exception {
        moonDao = Mockito.mock(MoonDao.class);
        moonServiceImp = new MoonServiceImp<T>(moonDao);
        existingMoonList.addAll(Arrays.asList(
                new Moon(1, "moon1", 1),
                new Moon(2, "moon2", 1),
                new Moon(3, "moon3", 2)));
        existingMoon = new Moon(1, "existingMoon", 1);
        notExistingMoon = new Moon(99, "notExistingMoon", 1);
        newMoon = new Moon(1, "newMoon", 1);
        updatedMoon = new Moon(1, "updatedMoon", 1);

    }

    @After
    public void tearDown() throws Exception {
        Mockito.reset(moonDao);
    }

    // MOON CREATE
    @Test
    public void createMoonPos() {
        newMoon = new Moon(1, "createMoonPos", 1);
        Mockito.when(moonDao.readMoon(newMoon.getMoonName()))
                .thenReturn(Optional.empty());
        Mockito.when(moonDao.createMoon(newMoon))
                .thenReturn(Optional.of(newMoon));

        Moon result = moonServiceImp.createMoon(newMoon);
        Assert.assertEquals(newMoon, result);
        Mockito.verify(moonDao).readMoon(newMoon.getMoonName());
        Mockito.verify(moonDao).createMoon(newMoon);
    }

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
        newMoon = new Moon(1, "moonWithNoPlanet", 37);
        Mockito.when(moonDao.readMoon(newMoon.getMoonName()))
                .thenReturn(Optional.empty());
        Mockito.when(moonDao.createMoon(newMoon))
                .thenReturn(Optional.empty());
        MoonFail e = Assert.assertThrows(MoonFail.class, () -> {
            moonServiceImp.createMoon(newMoon);
        });
        Assert.assertEquals("Could not create new moon", e.getMessage());
    }

    // MOON SELECT
    @SuppressWarnings("unchecked")
    @Test
    public void selectMoonPosId() {
        int Id = 1;
        Moon expectedMoon = new Moon(1, "moon1", 1);
        Mockito.when(moonDao.readMoon(Id)).thenReturn(Optional.of(expectedMoon));
        Moon result = ((MoonServiceImp<Integer>) moonServiceImp).selectMoon(Id);
        Assert.assertEquals(expectedMoon, result);
        Mockito.verify(moonDao).readMoon(Id);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void selectMoonPosName() {
        String name = "moon1";
        Moon expectedMoon = new Moon(1, "moon1", 1);
        Mockito.when(moonDao.readMoon(name)).thenReturn(Optional.of(expectedMoon));
        Moon result = ((MoonServiceImp<String>) moonServiceImp).selectMoon(name);
        Assert.assertEquals(expectedMoon, result);
        Mockito.verify(moonDao).readMoon(name);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void selectMoonNegNotFound() {
        newMoon = new Moon(1, "moonNotFound", 1);
        Mockito.when(moonDao.readMoon(1)).thenReturn(Optional.empty());
        MoonFail e = Assert.assertThrows(MoonFail.class, () -> {
            ((MoonServiceImp<Integer>) moonServiceImp).selectMoon(newMoon.getMoonId());
        });
        Assert.assertEquals("Moon not found", e.getMessage());
        Mockito.verify(moonDao).readMoon(newMoon.getMoonId());
        Mockito.verifyNoMoreInteractions(moonDao);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void SelectMoonNegInvalidType() {
        MoonFail e = Assert.assertThrows(MoonFail.class, () -> {
            ((MoonServiceImp<Double>) moonServiceImp).selectMoon(1.234);
        });
        Assert.assertEquals("Identifier must be an Integer or String", e.getMessage());
        Mockito.verifyNoMoreInteractions(moonDao);
    }

    @Test
    public void selectAllMoonsPos() {
        Mockito.when(moonDao.readAllMoons()).thenReturn(existingMoonList);

        List<Moon> result = moonServiceImp.selectAllMoons();
        Assert.assertEquals(existingMoonList, result);
        Mockito.verify(moonDao).readAllMoons();
    }

    @Test
    public void selectAllMoonsNegEmptyList() {
        Mockito.when(moonDao.readAllMoons()).thenReturn(Collections.emptyList());
        Assert.assertEquals(Collections.emptyList(), moonServiceImp.selectAllMoons());
        Mockito.verify(moonDao).readAllMoons();
        Mockito.verifyNoMoreInteractions(moonDao);
    }

    @Test
    public void selectMoonByPlanetPos() {
        Mockito.when(moonDao.readMoonsByPlanet(1)).thenReturn(existingMoonList);
        Assert.assertEquals(existingMoonList, moonServiceImp.selectByPlanet(1));
        Mockito.verify(moonDao).readMoonsByPlanet(1);
        Mockito.verifyNoMoreInteractions(moonDao);
    }

    @Test
    public void selectMoonByPlanetNegNoPlanet() {
        Mockito.when(moonDao.readMoonsByPlanet(0)).thenReturn(Collections.emptyList());
        Assert.assertEquals(Collections.emptyList(), moonServiceImp.selectByPlanet(0));
        Mockito.verify(moonDao).readMoonsByPlanet(0);
        Mockito.verifyNoMoreInteractions(moonDao);
    }

    // MOON UPDATE
    @Test
    public void updateMoon() {
        Mockito.when(moonDao.readMoon(existingMoon.getMoonId())).thenReturn(Optional.of(existingMoon));
        Mockito.when(moonDao.readMoon(updatedMoon.getMoonName())).thenReturn(Optional.empty());
        Mockito.when(moonDao.updateMoon(existingMoon)).thenReturn(Optional.of(updatedMoon));

        Assert.assertEquals(updatedMoon, moonServiceImp.updateMoon(existingMoon));

        Mockito.verify(moonDao).readMoon(existingMoon.getMoonId());
        Mockito.verify(moonDao).readMoon(existingMoon.getMoonName());
        Mockito.verify(moonDao).updateMoon(existingMoon);
        Mockito.verifyNoMoreInteractions(moonDao);
    }

    @Test
    public void updateMoonNegNotFound() {
        Mockito.when(moonDao.readMoon(notExistingMoon.getMoonId())).thenReturn(Optional.empty());
        MoonFail e = Assert.assertThrows(MoonFail.class, () -> {
            moonServiceImp.updateMoon(notExistingMoon);
        });
        Assert.assertEquals("Moon not found, could not update", e.getMessage());
        Mockito.verify(moonDao).readMoon(notExistingMoon.getMoonId());
        Mockito.verifyNoMoreInteractions(moonDao);
    }

    @Test
    public void updateMoonNegNameTooLong() {
        existingMoon = new Moon(1, "this name exceeds the 30 character limit for a new moonn name", 1);
        Mockito.when(moonDao.readMoon(existingMoon.getMoonId())).thenReturn(Optional.of(existingMoon));
        MoonFail e = Assert.assertThrows(MoonFail.class, () -> {
            moonServiceImp.updateMoon(existingMoon);
        });
        Assert.assertEquals("Moon name must be between 1 and 30 characters, could not update", e.getMessage());
        Mockito.verify(moonDao).readMoon(existingMoon.getMoonId());
        Mockito.verifyNoMoreInteractions(moonDao);
    }

    @Test
    public void updateMoonNegativeNameNotUnique() {
        existingMoon = new Moon(1, "this name is not unique", 1);
        Moon moonWithSameName = new Moon(2, "this name is not unique", 1);
        Mockito.when(moonDao.readMoon(existingMoon.getMoonId())).thenReturn(Optional.of(existingMoon));
        Mockito.when(moonDao.readMoon(moonWithSameName.getMoonName())).thenReturn(Optional.of(moonWithSameName));
        MoonFail e = Assert.assertThrows(MoonFail.class, () -> {
            moonServiceImp.updateMoon(existingMoon);
        });
        Assert.assertEquals("Moon name must be unique, could not update", e.getMessage());
        Mockito.verify(moonDao).readMoon(existingMoon.getMoonId());
        Mockito.verify(moonDao).readMoon(moonWithSameName.getMoonName());
        Mockito.verifyNoMoreInteractions(moonDao);
    }

    @Test
    public void updateMoonNegativeGenericFail() {
        existingMoon = new Moon(1, "this name is not unique", 1);
        Mockito.when(moonDao.readMoon(existingMoon.getMoonId())).thenReturn(Optional.of(existingMoon));
        Mockito.when(moonDao.readMoon(existingMoon.getMoonName())).thenReturn(Optional.of(existingMoon));
        Mockito.when(moonDao.updateMoon(existingMoon)).thenReturn(Optional.empty());
        MoonFail e = Assert.assertThrows(MoonFail.class, () -> {
            moonServiceImp.updateMoon(existingMoon);
        });
        Assert.assertEquals("Moon update failed, please try again", e.getMessage());
        Mockito.verify(moonDao).readMoon(existingMoon.getMoonId());
        Mockito.verify(moonDao).readMoon(existingMoon.getMoonName());
        Mockito.verify(moonDao).updateMoon(existingMoon);
        Mockito.verifyNoMoreInteractions(moonDao);
    }

    // MOON DELETE
    @SuppressWarnings("unchecked")
    @Test
    public void deleteMoonPos() {
        String message = "Moon deleted successfully";
        Boolean deleted = true;
        Mockito.when(moonDao.deleteMoon(existingMoon.getMoonId())).thenReturn(deleted);
        Assert.assertEquals(message,
                ((MoonServiceImp<Integer>) moonServiceImp).deleteMoon(existingMoon.getMoonId()));
        Mockito.verify(moonDao).deleteMoon(existingMoon.getMoonId());
        Mockito.verifyNoMoreInteractions(moonDao);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void deleteMoonPosString() {
        String message = "Moon deleted successfully";
        Boolean deleted = true;
        Mockito.when(moonDao.deleteMoon(existingMoon.getMoonName())).thenReturn(deleted);
        Assert.assertEquals(message,
                ((MoonServiceImp<String>) moonServiceImp).deleteMoon(existingMoon.getMoonName()));
        Mockito.verify(moonDao).deleteMoon(existingMoon.getMoonName());
        Mockito.verifyNoMoreInteractions(moonDao);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void deleteMoonNegInt() {
        String message = "Moon delete failed, please try again";
        Boolean deleted = false;
        Mockito.when(moonDao.deleteMoon(existingMoon.getMoonId())).thenReturn(deleted);
        MoonFail e = Assert.assertThrows(MoonFail.class,() -> {
            ((MoonServiceImp<Integer>) moonServiceImp).deleteMoon(existingMoon.getMoonId());
        });
        Assert.assertEquals(message, e.getMessage());
        Mockito.verify(moonDao).deleteMoon(existingMoon.getMoonId());
        Mockito.verifyNoMoreInteractions(moonDao);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void deleteMoonNegString() {
        String message = "Moon delete failed, please try again";
        Boolean deleted = false;
        Mockito.when(moonDao.deleteMoon(existingMoon.getMoonName())).thenReturn(deleted);
        MoonFail e = Assert.assertThrows(MoonFail.class,() -> {
            ((MoonServiceImp<String>) moonServiceImp).deleteMoon(existingMoon.getMoonName());
        });
        Assert.assertEquals(message, e.getMessage());
        Mockito.verify(moonDao).deleteMoon(existingMoon.getMoonName());
        Mockito.verifyNoMoreInteractions(moonDao);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void deleteMoonNegNotValid() {
        String message = "Identifier must be an Integer or String";
        MoonFail e = Assert.assertThrows(MoonFail.class,() -> {
            ((MoonServiceImp<Double>) moonServiceImp).deleteMoon(1.234);
        });
        Assert.assertEquals(message, e.getMessage());
        Mockito.verifyNoMoreInteractions(moonDao);
    }
}