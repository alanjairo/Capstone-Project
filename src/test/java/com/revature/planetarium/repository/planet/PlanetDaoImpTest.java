package com.revature.planetarium.repository.planet;

import com.revature.Setup;
import com.revature.planetarium.entities.Planet;
import com.revature.planetarium.utility.DatabaseConnector;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Optional;

public class PlanetDaoImpTest {

    private Planet createdPlanet;
    private Planet existingPlanet;

    private PlanetDao dao;

    @BeforeClass
    public static void testDatabaseSetup() throws SQLException {
        Setup.getConnection();
        Setup.resetTestDatabase();
    }

    @Before
    public void setUp() throws Exception {
        createdPlanet = new Planet();
        createdPlanet.setPlanetName("TestingPlanet");
        createdPlanet.setOwnerId(1);
        existingPlanet = new Planet();
        existingPlanet.setPlanetName("Earth");
        existingPlanet.setPlanetId(1);
        existingPlanet.setOwnerId(1);

        dao = new PlanetDaoImp();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void createPlanetPositiveNoImage() {
        //createdPlanet.setImageData("src/test/resources/Celestial-Images/planet-1.jpg");
        Optional<Planet> returnedPlanet = dao.createPlanet(createdPlanet);
        System.out.println(returnedPlanet.isPresent());

        Assert.assertSame(createdPlanet, returnedPlanet.get());
    }

    @Test
    public void createPlanetPositiveWithImage() throws IOException {
        File imageFile = new File("src/test/resources/Celestial-Images/planet-1.jpg");
        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
        String imageDataBase64 = Base64.getEncoder().encodeToString(imageBytes);

        createdPlanet.setImageData(imageDataBase64);
        Optional<Planet> returnedPlanet = dao.createPlanet(createdPlanet);

        Assert.assertSame(createdPlanet, dao.createPlanet(createdPlanet).get());
    }

    @Test
    public void readPlanetByIdPositive() {
        Assert.assertEquals(Optional.of(existingPlanet), dao.readPlanet(1));

    }

    @Test
    public void readPlanetByNamePositive() {
        Assert.assertEquals(Optional.of(existingPlanet), dao.readPlanet("Earth"));

    }

    @Test
    public void readPlanetByIdNegative() {
        //Assuming there are fewer than 50 planets
        Assert.assertEquals(Optional.empty(), dao.readPlanet(50));

    }

    @Test
    public void readPlanetByNameNegative() {
        Assert.assertEquals(Optional.empty(), dao.readPlanet("thisPlanetDoesNotExist"));
    }

    @Test
    public void readAllPlanetsPositive() {
        Assert.assertTrue(dao.readAllPlanets().size() > 1);
    }

    @Test
    public void readAllPlanetsNegative() {
        deletePlanetsForNegativeReadAllPlanetsTest();
        Assert.assertEquals(0, dao.readAllPlanets().size());
    }

    public void deletePlanetsForNegativeReadAllPlanetsTest() {
        try (Connection connection = DatabaseConnector.getConnection()){
            String sql = "DELETE FROM planets";
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new AssertionError("Could not delete planets");
        }
    }

    @Test
    public void readPlanetsByOwnerPositive() {
        //Assumption user 1 only has the database setup planets added
        Assert.assertEquals(2, dao.readPlanetsByOwner(1).size());
    }

    @Test
    public void readPlanetsByOwnerNegative() {
        //Assumption no user with id=3
        Assert.assertEquals(0, dao.readPlanetsByOwner(3).size());
    }

    @Test
    public void updatePlanetPositive() {
        existingPlanet.setPlanetName("Tatooine");
        Assert.assertEquals(Optional.of(existingPlanet), dao.updatePlanet(existingPlanet));
    }

    @Test
    public void updatePlanetNegative() {
        createdPlanet.setPlanetName("Tatooine");
        Assert.assertEquals(Optional.empty(), dao.updatePlanet(createdPlanet));
    }

    @Test
    public void deletePlanetByIdPositive() {
        Assert.assertTrue(dao.deletePlanet(1));
    }

    @Test
    public void deletePlanetByNamePositive() {
        Assert.assertTrue(dao.deletePlanet("Earth"));
    }

    @Test
    public void deletePlanetByIdNegative() {
        Assert.assertFalse(dao.deletePlanet(50));
    }

    @Test
    public void deletePlanetByNameNegative() {
        Assert.assertFalse(dao.deletePlanet("thisPlanetDoesNotExist"));
    }


}