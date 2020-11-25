package com.sparta.reece.services;

import com.sparta.reece.entities.UsersEntity;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.transaction.Transactional;
import java.util.List;

@Named
public class UsersDAO {
    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
    EntityManager entityManager = entityManagerFactory.createEntityManager();

    public UsersEntity findUserById(int id) {
        return entityManager.find(UsersEntity.class, id);
    }

    public List<UsersEntity> getAllUsers() {
        return entityManager.createNamedQuery("UsersEntity.getAllUsers", UsersEntity.class).getResultList();
    }

    public List<UsersEntity> getUsersByRole(String role) {
        return entityManager.createNamedQuery("UsersEntity.getUsersByRole", UsersEntity.class).setParameter("role", role)
                .getResultList();
    }

    public List<UsersEntity> getUserByName(String name) {
        return entityManager.createNamedQuery("UsersEntity.getUserByName", UsersEntity.class).setParameter("username", name)
                .getResultList();
    }

    public void createUser(UsersEntity user) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(user);
        entityManager.flush();
        entityManager.refresh(user);
        transaction.commit();
    }
}
