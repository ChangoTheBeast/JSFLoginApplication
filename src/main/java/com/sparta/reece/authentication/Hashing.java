package com.sparta.reece.authentication;

import com.sparta.reece.entities.UserSecurityEntity;
import com.sparta.reece.entities.UsersEntity;
import com.sparta.reece.services.UserSecurityDAO;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.HashMap;

public class Hashing {

    public static HashMap<UserSecurityEntity, byte[]> setHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int its = 10000;
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, its, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        UserSecurityEntity userSecurityEntity = new UserSecurityEntity();
        userSecurityEntity.setSalt(salt);
        userSecurityEntity.setIterations(its);
        HashMap<UserSecurityEntity, byte[]> results = new HashMap<>();
        results.put(userSecurityEntity, factory.generateSecret(keySpec).getEncoded());

        return results;
    }

    public static String getHash(UsersEntity user, String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        UserSecurityDAO securityDAO = new UserSecurityDAO();
        UserSecurityEntity userSecurity = securityDAO.getUserSecurityByID(user.getUserId());
        int its = userSecurity.getIterations();
        byte[] salt = userSecurity.getSalt();
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, its, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hashedPassword = factory.generateSecret(keySpec).getEncoded();
        return Base64.getEncoder().encodeToString(hashedPassword);
    }

}
