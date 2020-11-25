package com.sparta.reece.beans;

import com.sparta.reece.authentication.Hashing;
import com.sparta.reece.entities.User;
import com.sparta.reece.entities.UserSecurityEntity;
import com.sparta.reece.entities.UsersEntity;
import com.sparta.reece.services.UserSecurityDAO;
import com.sparta.reece.services.UsersDAO;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

@Named
@RequestScoped
public class UserRegistrationBean {
    @Inject
    User user;

    @Inject
    UsersDAO usersDAO;

    @Inject
    FacesContext facesContext;

    @Inject
    ExternalContext externalContext;

    private String role;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void submit() {
        List<UsersEntity> userList = usersDAO.getUserByName(user.getUsername());
        if (userList.size() > 0) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "User already exists in the database.", null));
        } else {
            try {
                HashMap<UserSecurityEntity, byte[]> securityEntityHashMap = Hashing.setHash(user.getPassword());
                String password = null;
                for (byte[] hashedPassword : securityEntityHashMap.values()) {
                    password = Base64.getEncoder().encodeToString(hashedPassword);
                }

                UsersEntity userEntity = new UsersEntity();
                userEntity.setUsername(user.getUsername());
                userEntity.setPassword(password);
                userEntity.setRole(this.role);
                usersDAO.createUser(userEntity);
                userEntity = usersDAO.getUserByName(user.getUsername()).get(0);
                for (UserSecurityEntity entity : securityEntityHashMap.keySet()) {
                    entity.setUserId(userEntity.getUserId());
                    new UserSecurityDAO().createUserSecurity(entity);
                }
                facesContext.addMessage(null, new FacesMessage("User was successfully added.", null));
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "User was unable to be added.", null));
            }
        }
    }
}
