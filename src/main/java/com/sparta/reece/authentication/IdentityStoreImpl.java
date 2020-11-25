package com.sparta.reece.authentication;

import com.sparta.reece.entities.UsersEntity;
import com.sparta.reece.services.UsersDAO;

import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;

public class IdentityStoreImpl implements IdentityStore{
    @Override
    public CredentialValidationResult validate(Credential credential) {
        UsernamePasswordCredential usernamePasswordCredential = (UsernamePasswordCredential) credential;
        for (UsersEntity admin: new UsersDAO().getUsersByRole("admin")) {
            try {
                String password = Hashing.getHash(admin, usernamePasswordCredential.getPasswordAsString());
                if (usernamePasswordCredential.getCaller().equals(admin.getUsername())
                        && password.equals(admin.getPassword())) {
                    HashSet<String> roles = new HashSet<>();
                    roles.add("ADMIN");
                    return new CredentialValidationResult(admin.getUsername(), roles);
                }
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                return CredentialValidationResult.NOT_VALIDATED_RESULT;
            }
        }
        for (UsersEntity user: new UsersDAO().getUsersByRole("user")) {
            try {
                String password = Hashing.getHash(user, usernamePasswordCredential.getPasswordAsString());
                if (usernamePasswordCredential.getCaller().equals(user.getUsername())
                        && password.equals(user.getPassword())) {
                    HashSet<String> roles = new HashSet<>();
                    roles.add("USER");
                    return new CredentialValidationResult(user.getUsername(), roles);
                }
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                return CredentialValidationResult.NOT_VALIDATED_RESULT;
            }

        }
        return CredentialValidationResult.NOT_VALIDATED_RESULT;
    }
}
