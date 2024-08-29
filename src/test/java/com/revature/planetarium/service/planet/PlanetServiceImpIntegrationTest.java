package com.revature.planetarium.service.planet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.Setup;
import com.revature.planetarium.entities.Planet;
import com.revature.planetarium.exceptions.PlanetFail;
import com.revature.planetarium.repository.planet.PlanetDaoImp;
import com.revature.planetarium.utility.DatabaseConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;

public class PlanetServiceImpTestIntegration<T> {
    private PlanetDaoImp daoImp;

    private PlanetServiceImp<T> serviceImp;

    private Planet newPlanetTestData;
    private Planet negPlanetTestNameDataTL;
    private Planet negPlanetTestNameDataNU;
    private Planet planetTestData;
    private Planet updatedPlanet;
    private Planet planetTestFail;
    private Planet planetTestFailTL;
    private Planet planetTestFailNU;

    @BeforeClass
    public static void testDatabaseSetup() throws SQLException {
        Setup.getConnection();
    }

    @Before
    public void setUp() throws Exception {
        Setup.resetTestDatabase();
        daoImp = new PlanetDaoImp();
        serviceImp = new PlanetServiceImp<>(daoImp);


        newPlanetTestData = new Planet();
        newPlanetTestData.setOwnerId(1);
        newPlanetTestData.setPlanetId(4);
        newPlanetTestData.setPlanetName("TestDataPlanetName");
        newPlanetTestData.setImageData("null");

        negPlanetTestNameDataTL = new Planet();
        negPlanetTestNameDataTL.setOwnerId(2);
        negPlanetTestNameDataTL.setPlanetId(6);
        negPlanetTestNameDataTL.setPlanetName("TestDataPlanetNameIsTooLongForThisExample");
        negPlanetTestNameDataTL.setImageData("null");

        planetTestData = new Planet();
        planetTestData.setOwnerId(1);
        planetTestData.setPlanetId(1);
        planetTestData.setPlanetName("Earth");
        planetTestData.setImageData("null");

    }

    @Test
    public void createPlanetPositiveIntegration() {
        Planet result = serviceImp.createPlanet(newPlanetTestData);
        Assert.assertEquals(newPlanetTestData.toString(), result.toString());
    }

    @Test
    public void createPlanetNegNameTooLongIntegration() {
        PlanetFail e = Assert.assertThrows(PlanetFail.class, ()->{
            serviceImp.createPlanet(negPlanetTestNameDataTL);
        });
        Assert.assertEquals("Planet name must be between 1 and 30 characters", e.getMessage());
    }

    @Test
    public void createPlanetNegNameNotUniqueIntegration() {
        negPlanetTestNameDataNU = new Planet();
        negPlanetTestNameDataNU.setOwnerId(3);
        negPlanetTestNameDataNU.setPlanetId(6);
        negPlanetTestNameDataNU.setPlanetName("ThisNameTakenPlanet");
        negPlanetTestNameDataNU.setImageData("null");
        
        PlanetFail e = Assert.assertThrows(PlanetFail.class, ()->{
            serviceImp.createPlanet(negPlanetTestNameDataNU);
        });
        Assert.assertEquals("Planet name must be unique", e.getMessage());
    }


    @Test
    public void selectPlanetPosStringIntegration() {
        Planet result = ((PlanetServiceImp<String>) serviceImp).selectPlanet(planetTestData.getPlanetName());
        Assert.assertEquals("Planet [planetId=1, planetName=Earth, ownerId=1]",result.toString());
    }

    @Test
    public void selectPlanetPosIntIntegration() {
        Planet result = ((PlanetServiceImp<Integer>) serviceImp).selectPlanet(planetTestData.getPlanetId());
        Assert.assertEquals("Planet [planetId=1, planetName=Earth, ownerId=1]",result.toString());
    }

    @Test
    public void selectPlanetNegNotFoundIntegration() {
        PlanetFail e = Assert.assertThrows(PlanetFail.class, ()->{
            ((PlanetServiceImp<Integer>) serviceImp).selectPlanet(90);
            }
        );
        Assert.assertEquals("Planet not found", e.getMessage());
    }

    @Test
    public void selectPlanetNegInvalidTypeIntegration() {
        PlanetFail e = Assert.assertThrows(PlanetFail.class, ()->{
            ((PlanetServiceImp<Double>) serviceImp).selectPlanet(1.234);
            }
        );
        Assert.assertEquals("identifier must be an Integer or String", e.getMessage());
    }

    @Test
    public void selectAllPlanetsPosIntegration() {
        Assert.assertTrue(!serviceImp.selectAllPlanets().isEmpty());
    }

    @Test
    public void selectAllPlanetsEmptyListIntegration() {
        try (Connection connection = DatabaseConnector.getConnection()){
            String sql = "DELETE FROM planets";
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new AssertionError("Could not delete planets");
        }
        Assert.assertEquals(Collections.emptyList(), serviceImp.selectAllPlanets());
    }

    @Test
    public void selectByOwnerPosIntegration() {
        Assert.assertTrue(!serviceImp.selectByOwner(1).isEmpty());
    }

    @Test
    public void selectByOwnerEmptyListIntegration() {
            try (Connection connection = DatabaseConnector.getConnection()){
                String sql = "DELETE FROM planets";
                Statement statement = connection.createStatement();
                statement.executeUpdate(sql);
            } catch (SQLException e) {
                throw new AssertionError("Could not delete planets");
            }
            Assert.assertEquals(Collections.emptyList(), serviceImp.selectByOwner(1));
    }

    @Test
    public void updatePlanetPosIntegration() {
        updatedPlanet = new Planet();
        updatedPlanet.setPlanetName("updatedPlanetName");
        updatedPlanet.setPlanetId(1);
        updatedPlanet.setOwnerId(1);
        updatedPlanet.setImageData("null");

        Planet result = serviceImp.updatePlanet(updatedPlanet);
        Assert.assertEquals(updatedPlanet.toString(), result.toString());
    }

    @Test
    public void updatePlanetNegNotFoundIntegration() {
        planetTestFail = new Planet();
        planetTestFail.setPlanetId(99);

        PlanetFail e = Assert.assertThrows(PlanetFail.class, ()->{
            serviceImp.updatePlanet(planetTestFail);
        });
        Assert.assertEquals("Planet not found, could not update", e.getMessage());  
    }

    @Test
    public void updatePlanetNegTLIntegration() {
        planetTestFailTL = new Planet();
        planetTestFailTL.setPlanetName("ThisNameIsTooLongForThisExample");
        planetTestFailTL.setPlanetId(2);
        planetTestFailTL.setImageData("null");
        planetTestFailTL.setOwnerId(1);

        PlanetFail e = Assert.assertThrows(PlanetFail.class, ()->{
            serviceImp.updatePlanet(planetTestFailTL);
        });
        Assert.assertEquals("Planet name must be between 1 and 30 characters, could not update", e.getMessage());  
    }

    @Test
    public void updatePlanetNegNUIntegration() {
        planetTestFailNU = new Planet();
        planetTestFailNU.setPlanetName("ThisNameTakenPlanet");
        planetTestFailNU.setPlanetId(2);
        planetTestFailNU.setImageData("null");
        planetTestFailNU.setOwnerId(2);

        PlanetFail e = Assert.assertThrows(PlanetFail.class, ()->{
            serviceImp.updatePlanet(planetTestFailNU);
        });
        Assert.assertEquals("Planet name must be unique, could not update", e.getMessage());  
    }

    @Test
    public void deletePlanetPosIntIntegration() {
        String message = "Planet deleted successfully";
        Boolean deleted = true;
        Assert.assertEquals(message, ((PlanetServiceImp<Integer>) serviceImp).deletePlanet(planetTestData.getPlanetId()));
    }

    @Test
    public void deletePlanetPosStringIntegration() {
        String message = "Planet deleted successfully";
        Boolean deleted = true;
        Assert.assertEquals(message, ((PlanetServiceImp<String>) serviceImp).deletePlanet(planetTestData.getPlanetName()));
    }

    @Test
    public void deletePlanetNegNotValidIntegration() {
        String message = "identifier must be an Integer or String";
        PlanetFail e = Assert.assertThrows(PlanetFail.class, ()->{
            ((PlanetServiceImp<Double>) serviceImp).deletePlanet(1.23);
        });
        Assert.assertEquals(message, e.getMessage());
    }

    @Test
    public void deletePlanetNegIntIntegration() {
        String message = "Planet delete failed, please try again";
        
        ((PlanetServiceImp<Integer>) serviceImp).deletePlanet(planetTestData.getPlanetId());
        PlanetFail e = Assert.assertThrows(PlanetFail.class, ()->{
            ((PlanetServiceImp<Integer>) serviceImp).deletePlanet(planetTestData.getPlanetId());
        });
        Assert.assertEquals(message, e.getMessage());
    }

    @Test
    public void deletePlanetNegStringIntegration() {
        String message = "Planet delete failed, please try again";
        
        ((PlanetServiceImp<String>) serviceImp).deletePlanet(planetTestData.getPlanetName());
        PlanetFail e = Assert.assertThrows(PlanetFail.class, ()->{
            ((PlanetServiceImp<String>) serviceImp).deletePlanet(planetTestData.getPlanetName());
        });
        Assert.assertEquals(message, e.getMessage());
    }
}
