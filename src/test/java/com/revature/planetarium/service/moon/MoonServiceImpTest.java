package com.revature.planetarium.service.moon;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.revature.planetarium.repository.moon.MoonDaoImp;

import static org.junit.Assert.*;

public class MoonServiceImpTest {

    private MoonDaoImp moonDaoImp;
    private MoonServiceImp moonServiceImp;

    @Before
    public void setUp() throws Exception {
        moonDaoImp = Mockito.mock(MoonDaoImp.class);
        moonServiceImp = new MoonServiceImp<>(moonDaoImp);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void createMoon() {
    }

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