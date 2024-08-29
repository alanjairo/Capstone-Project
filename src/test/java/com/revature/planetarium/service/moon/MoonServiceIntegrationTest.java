package com.revature.planetarium.service.moon;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.sql.*;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.Setup;
import com.revature.planetarium.entities.Moon;
import com.revature.planetarium.exceptions.MoonFail;
import com.revature.planetarium.repository.moon.MoonDao;
import com.revature.planetarium.repository.moon.MoonDaoImp;
import com.revature.planetarium.utility.DatabaseConnector;

public class MoonServiceIntegrationTest<T> {

    private MoonDao moonDao;

    private MoonService<T> moonService;

    private Moon newMoon;
    private Moon existingMoon;
    private Moon notExistingMoon;

    @BeforeClass
    public static void testDatabaseSetup() throws SQLException {
        Setup.getConnection();
    }

    @Before
    public void setUp() throws Exception {
        Setup.resetTestDatabase();
        moonDao = new MoonDaoImp();
        moonService = new MoonServiceImp<>(moonDao);
        existingMoon = new Moon(1, "existingMoon", 1);
        notExistingMoon = new Moon(99, "notExistingMoon", 1);
        newMoon = new Moon(1, "newMoon", 1);
    }

    @After
    public void tearDown() throws Exception {
    }

    // MOON CREATE
    @Test
    public void createMoonPos() {
        newMoon = new Moon();
        newMoon.setMoonName("newMoon");
        newMoon.setOwnerId(1);

        Moon result = moonService.createMoon(newMoon);

        Assert.assertNotNull(result);
        Assert.assertSame(newMoon, result);
    }

    @Test
    public void createMoonNegNameTooLong() {
        newMoon = new Moon(1, "name exceeds character limit 30", 1);
        MoonFail e = Assert.assertThrows(MoonFail.class, () -> {
            moonService.createMoon(newMoon);
        });
        Assert.assertEquals("Moon name must be between 1 and 30 characters", e.getMessage());
    }

    @Test
    public void createMoonNegNameTooShort() {
        newMoon = new Moon(1, "", 1);
        MoonFail e = Assert.assertThrows(MoonFail.class, () -> {
            moonService.createMoon(newMoon);
        });
        Assert.assertEquals("Moon name must be between 1 and 30 characters", e.getMessage());
    }

    @Test
    public void createMoonNegNameNotUnique() {
        newMoon = new Moon(1, "ThisNameTakenMoon", 1);
        MoonFail e = Assert.assertThrows(MoonFail.class, () -> {
            moonService.createMoon(newMoon);
        });
        Assert.assertEquals("Moon name must be unique", e.getMessage());
    }

    // MOON SELECT
    @SuppressWarnings("unchecked")
    @Test
    public void selectMoonPosId() {
        int Id = 1;
        Moon expectedMoon = new Moon(1, "Luna", 1);
        Moon result = ((MoonServiceImp<Integer>) moonService).selectMoon(Id);
        Assert.assertEquals(expectedMoon, result);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void selectMoonPosName() {
        String name = "Luna";
        Moon expectedMoon = new Moon(1, "Luna", 1);
        Moon result = ((MoonServiceImp<String>) moonService).selectMoon(name);
        Assert.assertEquals(expectedMoon, result);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void selectMoonNegNotFound() {
        newMoon = new Moon(1, "moonNotFound", 1);
        MoonFail e = Assert.assertThrows(MoonFail.class, () -> {
            ((MoonServiceImp<Integer>) moonService).selectMoon(99);
        });
        Assert.assertEquals("Moon not found", e.getMessage());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void SelectMoonNegInvalidType() {
        MoonFail e = Assert.assertThrows(MoonFail.class, () -> {
            ((MoonServiceImp<Double>) moonService).selectMoon(1.234);
        });
        Assert.assertEquals("Identifier must be an Integer or String", e.getMessage());
    }

    @Test
    public void selectAllMoonsPos() {
        int moonCount = 0;
        try (Connection connection = DatabaseConnector.getConnection()) {
            String sql = "SELECT count(*) FROM moons";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            if (rs.next()) {
                moonCount = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new AssertionError("Could not retrieve moons");
        }
        List<Moon> result = moonService.selectAllMoons();
        Assert.assertEquals(moonCount, result.size());
    }

    @Test
    public void selectAllMoonsNegEmptyList() {
        try (Connection connection = DatabaseConnector.getConnection()) {
            String sql = "DELETE FROM moons";
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new AssertionError("Could not delete moons");
        }
        Assert.assertEquals(Collections.emptyList(), moonService.selectAllMoons());
    }

    @Test
    public void selectMoonByPlanetPos() {
        int moonCount = 0;
        int planetId = 2;
        try (Connection connection = DatabaseConnector.getConnection()) {
            String sql = "SELECT count(*) FROM moons WHERE myPlanetId = " + planetId;
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            if (rs.next()) {
                moonCount = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new AssertionError("Could not retrieve moons with planet id " + planetId);
        }
        Assert.assertEquals(moonCount, moonService.selectByPlanet(planetId).size());
    }

    @Test
    public void selectMoonByPlanetNegNoPlanet() {
        Assert.assertEquals(Collections.emptyList(), moonService.selectByPlanet(0));
    }

    // MOON UPDATE
    @Test
    public void updateMoon() {
        Moon updatedMoon = new Moon();
        updatedMoon.setMoonId(2);
        updatedMoon.setMoonName("UpdatedTitan");
        updatedMoon.setOwnerId(2);
        try {
            Moon result = moonService.updateMoon(updatedMoon);
            Assert.assertEquals("UpdatedTitan", result.getMoonName());
            Optional<Moon> retrievedMoon = moonDao.readMoon(2);
            Assert.assertTrue(retrievedMoon.isPresent());
            Assert.assertEquals("UpdatedTitan", retrievedMoon.get().getMoonName());

        } catch (MoonFail e) {
            Assert.fail("Update failed: " + e.getMessage());
        }
        Assert.assertEquals(updatedMoon, moonService.updateMoon(moonDao.readMoon(2).get()));
    }

    @Test
    public void updateMoonNegNotFound() {
        MoonFail e = Assert.assertThrows(MoonFail.class, () -> {
            moonService.updateMoon(notExistingMoon);
        });
        Assert.assertEquals("Moon not found, could not update", e.getMessage());
    }

    @Test
    public void updateMoonNegNameTooLong() {
        existingMoon = new Moon(1, "this name exceeds the 30 character limit for a new moonn name", 1);
        MoonFail e = Assert.assertThrows(MoonFail.class, () -> {
            moonService.updateMoon(existingMoon);
        });
        Assert.assertEquals("Moon name must be between 1 and 30 characters, could not update", e.getMessage());
    }

    @Test
    public void updateMoonNegativeNameNotUnique() {
        existingMoon = new Moon(1, "ThisNameTakenMoon", 1);
        MoonFail e = Assert.assertThrows(MoonFail.class, () -> {
            moonService.updateMoon(existingMoon);
        });
        Assert.assertEquals("Moon name must be unique, could not update", e.getMessage());
    }

    // MOON DELETE
    @SuppressWarnings("unchecked")
    @Test
    public void deleteMoonPos() {
        String message = "Moon deleted successfully";
        Assert.assertEquals(message,
                ((MoonServiceImp<Integer>) moonService).deleteMoon(1));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void deleteMoonPosString() {
        String message = "Moon deleted successfully";
        Assert.assertEquals(message,
                ((MoonServiceImp<String>) moonService).deleteMoon("ThisNameTakenMoon"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void deleteMoonNegInt() {
        String message = "Moon delete failed, please try again";
        MoonFail e = Assert.assertThrows(MoonFail.class, () -> {
            ((MoonServiceImp<Integer>) moonService).deleteMoon(99);
        });
        Assert.assertEquals(message, e.getMessage());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void deleteMoonNegString() {
        String message = "Moon delete failed, please try again";
        MoonFail e = Assert.assertThrows(MoonFail.class, () -> {
            ((MoonServiceImp<String>) moonService).deleteMoon("notExistingMoon");
        });
        Assert.assertEquals(message, e.getMessage());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void deleteMoonNegNotValid() {
        String message = "Identifier must be an Integer or String";
        MoonFail e = Assert.assertThrows(MoonFail.class, () -> {
            ((MoonServiceImp<Double>) moonService).deleteMoon(1.234);
        });
        Assert.assertEquals(message, e.getMessage());
    }
}