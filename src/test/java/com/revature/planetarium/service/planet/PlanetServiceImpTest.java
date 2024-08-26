package com.revature.planetarium.service.planet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.revature.planetarium.entities.Planet;
import com.revature.planetarium.exceptions.PlanetFail;
import com.revature.planetarium.repository.planet.PlanetDaoImp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlanetServiceImpTest<T> {

    private PlanetDaoImp planetDaoImp;
    private PlanetServiceImp planetServiceImp;

    private Planet newPlanetTestData;
    private Planet negPlanetTestNameDataTL;
    private Planet negPlanetTestNameDataNU;
    private Planet negPlanetTestNameDataFail;
    private Planet planetTestData;
    private List<Planet> existingPlanets;



    @Before
    public void setUp() throws Exception {
        planetDaoImp = Mockito.mock(PlanetDaoImp.class);
        planetServiceImp = new PlanetServiceImp<>(planetDaoImp);

        newPlanetTestData = new Planet();
        newPlanetTestData.setOwnerId(1);
        newPlanetTestData.setPlanetId(4);
        newPlanetTestData.setPlanetName("TestDataPlanetName");
        newPlanetTestData.setImageData("null");

        negPlanetTestNameDataTL = new Planet();
        negPlanetTestNameDataTL.setOwnerId(2);
        negPlanetTestNameDataTL.setPlanetId(5);
        negPlanetTestNameDataTL.setPlanetName("TestDataPlanetNameIsTooLongForThisExample");
        negPlanetTestNameDataTL.setImageData("null");

        planetTestData = new Planet();
        planetTestData.setOwnerId(1);
        planetTestData.setPlanetId(5);
        planetTestData.setPlanetName("Earth");
        planetTestData.setImageData("null");

        List<Planet> existingPlanets = new ArrayList<>();
        existingPlanets.add(newPlanetTestData);
        existingPlanets.add(planetTestData);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void createPlanetPositive() {
        Mockito.when(planetDaoImp.createPlanet(newPlanetTestData)).thenReturn(Optional.of(newPlanetTestData));
        Assert.assertEquals("Planet [planetId=4, planetName=TestDataPlanetName, ownerId=1]", planetServiceImp.createPlanet(newPlanetTestData).toString());
    }

    @Test
    public void createPlanetNegNameTooLong() {
        Mockito.when(planetDaoImp.createPlanet(negPlanetTestNameDataTL)).thenReturn(Optional.of(negPlanetTestNameDataTL));
        PlanetFail e = Assert.assertThrows(PlanetFail.class, ()->{
            planetServiceImp.createPlanet(negPlanetTestNameDataTL);
        });
        Assert.assertEquals("Planet name must be between 1 and 30 characters", e.getMessage());
    }

    @Test
    public void createPlanetNegNameNotUnique() {
        negPlanetTestNameDataNU = new Planet();
        negPlanetTestNameDataNU.setOwnerId(3);
        negPlanetTestNameDataNU.setPlanetId(6);
        negPlanetTestNameDataNU.setPlanetName("TestDataPlanetName");
        negPlanetTestNameDataNU.setImageData("null");
        
        Mockito.when(planetDaoImp.readPlanet(negPlanetTestNameDataNU.getPlanetName())).thenReturn(Optional.of(negPlanetTestNameDataNU));
        PlanetFail e = Assert.assertThrows(PlanetFail.class, ()->{
            planetServiceImp.createPlanet(negPlanetTestNameDataNU);
        });
        System.out.println(e.getMessage());
        Assert.assertEquals("Planet name must be unique", e.getMessage());
    }

    @Test
    public void createPlanetNegNameFailure() {
        negPlanetTestNameDataFail = new Planet();
        negPlanetTestNameDataFail.setPlanetName("TestDataPlanetNameFail");
        
        Mockito.when(planetDaoImp.createPlanet(negPlanetTestNameDataFail)).thenReturn(Optional.empty());
        PlanetFail e = Assert.assertThrows(PlanetFail.class, ()->{
            planetServiceImp.createPlanet(negPlanetTestNameDataFail);
        });
        System.out.println(e.getMessage());
        Assert.assertEquals("Planet creation failed, please try again", e.getMessage());
    }

    @Test
    public void selectPlanetPosString() {
        Mockito.when(planetDaoImp.readPlanet("Earth")).thenReturn(Optional.of(planetTestData));
        Assert.assertEquals("Planet [planetId=5, planetName=Earth, ownerId=1]", planetServiceImp.selectPlanet(planetTestData.getPlanetName()).toString());
    }

    @Test
    public void selectPlanetPosInt() {
        Mockito.when(planetDaoImp.readPlanet(5)).thenReturn(Optional.of(planetTestData));
        Assert.assertEquals("Planet [planetId=5, planetName=Earth, ownerId=1]", planetServiceImp.selectPlanet(planetTestData.getPlanetId()).toString());
    }

    @Test
    public void selectPlanetNegNotFound() {
        Mockito.when(planetDaoImp.readPlanet(1)).thenReturn(Optional.empty());
        PlanetFail e = Assert.assertThrows(PlanetFail.class, ()->{
                planetServiceImp.selectPlanet(planetTestData.getPlanetId()).toString();
            }
        );
        Assert.assertEquals("Planet not found", e.getMessage());
    }

    @Test
    public void selectPlanetNegInvalidType() {
        //Mockito.when(planetDaoImp.readPlanet(1.234)).thenReturn(Optional.empty());
        PlanetFail e = Assert.assertThrows(PlanetFail.class, ()->{
                planetServiceImp.selectPlanet(1.234).toString();
            }
        );
        Assert.assertEquals("identifier must be an Integer or String", e.getMessage());
    }

    @Test
    public void selectAllPlanetsPos() {
        Mockito.when(planetDaoImp.readAllPlanets()).thenReturn(existingPlanets);
        Assert.assertEquals(existingPlanets, planetServiceImp.selectAllPlanets());
    }

    @Test
    public void selectAllPlanetsNeg() {
        List<Planet> emptyList = new ArrayList<>();
        Mockito.when(planetDaoImp.readAllPlanets()).thenReturn(emptyList);
        Assert.assertEquals(emptyList, planetServiceImp.selectAllPlanets());
    }

    @Test
    public void selectByOwnerPos() {
        Mockito.when(planetDaoImp.readPlanetsByOwner(1)).thenReturn(existingPlanets);
        Assert.assertEquals(existingPlanets, planetServiceImp.selectByOwner(1));
    }

    @Test
    public void selectByOwnerNeg() {
        List<Planet> emptyList = new ArrayList<>();
        Mockito.when(planetDaoImp.readPlanetsByOwner(0)).thenReturn(emptyList);
        Assert.assertEquals(emptyList, planetServiceImp.selectByOwner(0));
    }

    @Test
    public void updatePlanetPos() {
    }

    @Test
    public void deletePlanetPos() {
    }
}