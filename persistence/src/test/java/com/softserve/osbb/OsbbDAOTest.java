package com.softserve.osbb;

import com.softserve.osbb.dao.OsbbDAO;
import com.softserve.osbb.model.OsbbEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Roman on 06.07.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OsbbApplicationRunner.class)
@Transactional
public class OsbbDAOTest {

    private OsbbEntity osbbEntity;

    @Autowired
    OsbbDAO osbbDAO;

    @Before
    public void init() {
        osbbEntity = new OsbbEntity();
        osbbEntity.setName("Lviv_osbb");
        osbbEntity.setDescription("osbb for people");
        osbbEntity.setCreatorId(5);
    }

    @Test
    public void testSave(){
        OsbbEntity savedOsbb = osbbDAO.save(osbbEntity);
        Assert.assertNotNull(savedOsbb);
        Assert.assertNotEquals(0, (long)osbbEntity.getOsbbId());
        Assert.assertEquals("Lviv_osbb",osbbEntity.getName());
    }
}