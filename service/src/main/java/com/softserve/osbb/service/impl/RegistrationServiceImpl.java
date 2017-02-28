package com.softserve.osbb.service.impl;

import java.security.SecureRandom;
import java.util.Objects;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.softserve.osbb.model.Osbb;
import com.softserve.osbb.model.Settings;
import com.softserve.osbb.model.User;
import com.softserve.osbb.service.OsbbService;
import com.softserve.osbb.service.RegistrationService;
import com.softserve.osbb.service.SettingsService;
import com.softserve.osbb.service.UserService;

/**
 * Created by nazar.dovhyy on 29.10.2016.
 */
@Service
public class RegistrationServiceImpl implements RegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationServiceImpl.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private OsbbService osbbService;
    
    @Autowired
    private SettingsService settingsService;
        
    private SecureRandom random = new SecureRandom();

	private static final int MIN_LENGTH = 4;
	private static final int MAX_LENGTH = 16;

	
	private int generatePasswordLenght() {
		return random.nextInt((MAX_LENGTH - MIN_LENGTH) + 1) + MIN_LENGTH;
	}
	
	
	private void checkUserPassword(User user) {
		if(Objects.isNull(user.getPassword())) {
        	user.setPassword(generatePassword());
        }
	}
	
	public String generatePassword() {
		
        int lenght = generatePasswordLenght();
        return RandomStringUtils.random(lenght, 0, 0, true, true, null, random );
        
    }

    @Transactional(readOnly = false,
            isolation = Isolation.SERIALIZABLE,
            propagation = Propagation.REQUIRED)
    @Override
    public User registrate(User user) {
        logger.info("registrating new user " + user);
        checkUserPassword(user);
        User registeredUser = userService.save(user);
        settingsService.save(new Settings(registeredUser));
        return registeredUser;
    }

    @Transactional(readOnly = false,
            isolation = Isolation.SERIALIZABLE,
            propagation = Propagation.REQUIRED)
    @Override
    public Osbb registrate(Osbb newOsbb) {
        logger.info("registering new osbb " + newOsbb);
        if (!osbbService.findByNameContaining(newOsbb.getName()).isEmpty()) {
            throw new IllegalArgumentException("osbb with such name " + newOsbb.getName() + " already exists");
        }
        newOsbb = osbbService.addOsbb(newOsbb);
        return newOsbb;
    }


    
}
