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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PlanetServiceImpTest<T> {

    private PlanetDaoImp planetDaoImp;
    
    private PlanetServiceImp<T> planetServiceImp;

    private Planet newPlanetTestData;
    private Planet negPlanetTestNameDataTL;
    private Planet negPlanetTestNameDataNU;
    private Planet negPlanetTestNameDataFail;
    private Planet planetTestData;
    private Planet updatedPlanet;
    private Planet planetTestFail;
    private Planet planetTestFailTL;
    private Planet planetTestFailNU;
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
        Mockito.when(planetDaoImp.readPlanet(newPlanetTestData.getPlanetName())).thenReturn(Optional.empty());
        Mockito.when(planetDaoImp.createPlanet(newPlanetTestData)).thenReturn(Optional.of(newPlanetTestData));
        Assert.assertEquals("Planet [planetId=4, planetName=TestDataPlanetName, ownerId=1]", planetServiceImp.createPlanet(newPlanetTestData).toString());
        Mockito.verify(planetDaoImp).readPlanet(newPlanetTestData.getPlanetName());
        Mockito.verify(planetDaoImp).createPlanet(newPlanetTestData);
    }

    @Test
    public void createPlanetNegNameTooLong() {
        Mockito.when(planetDaoImp.readPlanet(negPlanetTestNameDataTL.getPlanetName())).thenReturn(Optional.empty());
        Mockito.when(planetDaoImp.createPlanet(negPlanetTestNameDataTL)).thenReturn(Optional.of(negPlanetTestNameDataTL));
        PlanetFail e = Assert.assertThrows(PlanetFail.class, ()->{
            planetServiceImp.createPlanet(negPlanetTestNameDataTL);
        });
        Assert.assertEquals("Planet name must be between 1 and 30 characters", e.getMessage());
        Mockito.verifyNoInteractions(planetDaoImp);
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
        Mockito.verify(planetDaoImp).readPlanet(negPlanetTestNameDataNU.getPlanetName());
        Mockito.verifyNoMoreInteractions(planetDaoImp);
    }

    @Test
    public void createPlanetNegNameFailure() {
        negPlanetTestNameDataFail = new Planet();
        negPlanetTestNameDataFail.setPlanetName("TestDataPlanetNameFail");
        
        Mockito.when(planetDaoImp.readPlanet(negPlanetTestNameDataFail.getPlanetName())).thenReturn(Optional.empty());
        Mockito.when(planetDaoImp.createPlanet(negPlanetTestNameDataFail)).thenReturn(Optional.empty());
        PlanetFail e = Assert.assertThrows(PlanetFail.class, ()->{
            planetServiceImp.createPlanet(negPlanetTestNameDataFail);
        });
        System.out.println(e.getMessage());
        Assert.assertEquals("Planet creation failed, please try again", e.getMessage());
        Mockito.verify(planetDaoImp).readPlanet(negPlanetTestNameDataFail.getPlanetName());
        Mockito.verify(planetDaoImp).createPlanet(negPlanetTestNameDataFail);
    }

    @Test
    public void selectPlanetPosString() {
        Mockito.when(planetDaoImp.readPlanet("Earth")).thenReturn(Optional.of(planetTestData));
        Assert.assertEquals("Planet [planetId=5, planetName=Earth, ownerId=1]", ((PlanetServiceImp<String>) planetServiceImp).selectPlanet(planetTestData.getPlanetName()).toString());
        Mockito.verify(planetDaoImp).readPlanet(planetTestData.getPlanetName());
        Mockito.verifyNoMoreInteractions(planetDaoImp);
    }

    @Test
    public void selectPlanetPosInt() {
        Mockito.when(planetDaoImp.readPlanet(5)).thenReturn(Optional.of(planetTestData));
        Assert.assertEquals("Planet [planetId=5, planetName=Earth, ownerId=1]", ((PlanetServiceImp<Integer>) planetServiceImp).selectPlanet(planetTestData.getPlanetId()).toString());
        Mockito.verify(planetDaoImp).readPlanet(planetTestData.getPlanetId());
        Mockito.verifyNoMoreInteractions(planetDaoImp);
    }

    @Test
    public void selectPlanetNegNotFound() {
        Mockito.when(planetDaoImp.readPlanet(1)).thenReturn(Optional.empty());
        PlanetFail e = Assert.assertThrows(PlanetFail.class, ()->{
            ((PlanetServiceImp<Integer>) planetServiceImp).selectPlanet(planetTestData.getPlanetId());
            }
        );
        Assert.assertEquals("Planet not found", e.getMessage());
        Mockito.verify(planetDaoImp).readPlanet(planetTestData.getPlanetId());
        Mockito.verifyNoMoreInteractions(planetDaoImp);
    }

    @Test
    public void selectPlanetNegInvalidType() {
        //Mockito.when(planetDaoImp.readPlanet(1.234)).thenReturn(Optional.empty());
        PlanetFail e = Assert.assertThrows(PlanetFail.class, ()->{
            ((PlanetServiceImp<Double>) planetServiceImp).selectPlanet(1.234);
            }
        );
        Assert.assertEquals("identifier must be an Integer or String", e.getMessage());
        Mockito.verifyNoInteractions(planetDaoImp);
    }

    @Test
    public void selectAllPlanetsPos() {
        Mockito.when(planetDaoImp.readAllPlanets()).thenReturn(existingPlanets);
        Assert.assertEquals(existingPlanets, planetServiceImp.selectAllPlanets());
        Mockito.verify(planetDaoImp).readAllPlanets();
        Mockito.verifyNoMoreInteractions(planetDaoImp);
    }

    @Test
    public void selectAllPlanetsEmptyList() {
        Mockito.when(planetDaoImp.readAllPlanets()).thenReturn(Collections.emptyList());
        Assert.assertEquals(Collections.emptyList(), planetServiceImp.selectAllPlanets());
        Mockito.verify(planetDaoImp).readAllPlanets();
        Mockito.verifyNoMoreInteractions(planetDaoImp);
    }

    @Test
    public void selectByOwnerPos() {
        Mockito.when(planetDaoImp.readPlanetsByOwner(1)).thenReturn(existingPlanets);
        Assert.assertEquals(existingPlanets, planetServiceImp.selectByOwner(1));
        Mockito.verify(planetDaoImp).readPlanetsByOwner(1);
        Mockito.verifyNoMoreInteractions(planetDaoImp);
    }

    @Test
    public void selectByOwnerEmptyList() {
        Mockito.when(planetDaoImp.readPlanetsByOwner(0)).thenReturn(Collections.emptyList());
        Assert.assertEquals(Collections.emptyList(), planetServiceImp.selectByOwner(0));
        Mockito.verify(planetDaoImp).readPlanetsByOwner(0);
        Mockito.verifyNoMoreInteractions(planetDaoImp);
    }

    @Test
    public void updatePlanetPos() {
        updatedPlanet = new Planet();
        updatedPlanet.setPlanetName("updatedPlanetName");
        updatedPlanet.setPlanetId(1);
        updatedPlanet.setOwnerId(1);
        updatedPlanet.setImageData("null");
        
        Mockito.when(planetDaoImp.readPlanet(planetTestData.getPlanetId())).thenReturn(Optional.of(planetTestData));
        Mockito.when(planetDaoImp.updatePlanet(planetTestData)).thenReturn(Optional.of(updatedPlanet));
        Assert.assertEquals(updatedPlanet, planetServiceImp.updatePlanet(planetTestData));
        Mockito.verify(planetDaoImp).readPlanet(planetTestData.getPlanetId());
        Mockito.verify(planetDaoImp).updatePlanet(planetTestData);
        Mockito.verifyNoMoreInteractions(planetDaoImp);
    }

    @Test
    public void updatePlanetNegNotFound() {
        planetTestFail = new Planet();
        planetTestFail.setPlanetId(99);

        Mockito.when(planetDaoImp.readPlanet(planetTestFail.getPlanetId())).thenReturn(Optional.empty());
        PlanetFail e = Assert.assertThrows(PlanetFail.class, ()->{
            planetServiceImp.updatePlanet(planetTestFail);
        });
        Assert.assertEquals("Planet not found, could not update", e.getMessage());  
        Mockito.verify(planetDaoImp).readPlanet(planetTestFail.getPlanetId());
        Mockito.verifyNoMoreInteractions(planetDaoImp);
    }

    @Test
    public void updatePlanetNegTL() {
        planetTestFailTL = new Planet();
        planetTestFailTL.setPlanetName("ThisNameIsTooLongForThisExample");
        planetTestFailTL.setPlanetId(6);
        planetTestFailTL.setImageData("null");

        Mockito.when(planetDaoImp.readPlanet(planetTestFailTL.getPlanetId())).thenReturn(Optional.of(planetTestFailTL));
        PlanetFail e = Assert.assertThrows(PlanetFail.class, ()->{
            planetServiceImp.updatePlanet(planetTestFailTL);
        });
        Assert.assertEquals("Planet name must be between 1 and 30 characters, could not update", e.getMessage());  
        Mockito.verify(planetDaoImp).readPlanet(planetTestFailTL.getPlanetId());
        Mockito.verifyNoMoreInteractions(planetDaoImp);
    }

    @Test
    public void updatePlanetNegNU() {
        planetTestFailNU = new Planet();
        planetTestFailNU.setPlanetName("ThisNameIsTaken");
        planetTestFailNU.setPlanetId(6);
        planetTestFailNU.setImageData("null");

        Mockito.when(planetDaoImp.readPlanet(planetTestFailNU.getPlanetId())).thenReturn(Optional.of(planetTestData));
        Mockito.when(planetDaoImp.readPlanet(planetTestFailNU.getPlanetName())).thenReturn(Optional.of(planetTestData));
        PlanetFail e = Assert.assertThrows(PlanetFail.class, ()->{
            planetServiceImp.updatePlanet(planetTestFailNU);
        });
        Assert.assertEquals("Planet name must be unique, could not update", e.getMessage());  
        Mockito.verify(planetDaoImp).readPlanet(planetTestFailNU.getPlanetId());
        Mockito.verify(planetDaoImp).readPlanet(planetTestFailNU.getPlanetName());
        Mockito.verifyNoMoreInteractions(planetDaoImp);
    }

    @Test
    public void updatePlanetNegFail() {
        planetTestFail = new Planet();
        planetTestFail.setPlanetName("ThisNameFails");
        planetTestFail.setPlanetId(6);
        planetTestFail.setImageData("null");

        Mockito.when(planetDaoImp.readPlanet(planetTestFail.getPlanetId())).thenReturn(Optional.of(planetTestFail));
        Mockito.when(planetDaoImp.updatePlanet(planetTestFail)).thenReturn(Optional.empty());
        PlanetFail e = Assert.assertThrows(PlanetFail.class, ()->{
            planetServiceImp.updatePlanet(planetTestFail);
        });
        Assert.assertEquals("Planet update failed, please try again", e.getMessage());  
        Mockito.verify(planetDaoImp).readPlanet(planetTestFail.getPlanetId());
        Mockito.verify(planetDaoImp).updatePlanet(planetTestFail);
        Mockito.verifyNoMoreInteractions(planetDaoImp);
    }

    @Test
    public void deletePlanetPosInt() {
        String message = "Planet deleted successfully";
        Boolean deleted = true;
        Mockito.when(planetDaoImp.deletePlanet(planetTestData.getPlanetId())).thenReturn(deleted);
        Assert.assertEquals(message, ((PlanetServiceImp<Integer>) planetServiceImp).deletePlanet(planetTestData.getPlanetId()));
        Mockito.verify(planetDaoImp).deletePlanet(planetTestData.getPlanetId());
        Mockito.verifyNoMoreInteractions(planetDaoImp);
    }

    @Test
    public void deletePlanetPosString() {
        String message = "Planet deleted successfully";
        Boolean deleted = true;
        Mockito.when(planetDaoImp.deletePlanet(planetTestData.getPlanetName())).thenReturn(deleted);
        Assert.assertEquals(message, ((PlanetServiceImp<String>) planetServiceImp).deletePlanet(planetTestData.getPlanetName()));
        Mockito.verify(planetDaoImp).deletePlanet(planetTestData.getPlanetName());
        Mockito.verifyNoMoreInteractions(planetDaoImp);
    }

    @Test
    public void deletePlanetNegNotValid() {
        String message = "identifier must be an Integer or String";
        PlanetFail e = Assert.assertThrows(PlanetFail.class, ()->{
            ((PlanetServiceImp<Double>) planetServiceImp).deletePlanet(1.23);
        });
        Assert.assertEquals(message, e.getMessage());
        Mockito.verifyNoInteractions(planetDaoImp);
    }

    @Test
    public void deletePlanetNegInt() {
        String message = "Planet delete failed, please try again";
        Boolean deleted = false;
        Mockito.when(planetDaoImp.deletePlanet(planetTestData.getPlanetId())).thenReturn(deleted);
        PlanetFail e = Assert.assertThrows(PlanetFail.class, ()->{
            ((PlanetServiceImp<Integer>) planetServiceImp).deletePlanet(planetTestData.getPlanetId());
        });
        Assert.assertEquals(message, e.getMessage());
        Mockito.verify(planetDaoImp).deletePlanet(planetTestData.getPlanetId());
        Mockito.verifyNoMoreInteractions(planetDaoImp);
    }

    @Test
    public void deletePlanetNegString() {
        String message = "Planet delete failed, please try again";
        Boolean deleted = false;
        Mockito.when(planetDaoImp.deletePlanet(planetTestData.getPlanetName())).thenReturn(deleted);
        PlanetFail e = Assert.assertThrows(PlanetFail.class, ()->{
            ((PlanetServiceImp<String>) planetServiceImp).deletePlanet(planetTestData.getPlanetName());
        });
        Assert.assertEquals(message, e.getMessage());
        Mockito.verify(planetDaoImp).deletePlanet(planetTestData.getPlanetName());
        Mockito.verifyNoMoreInteractions(planetDaoImp);
    }

}