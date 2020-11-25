package com.sparta.reece.services;

import com.sparta.reece.entities.UserSecurityEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class UserSecurityDAO {
    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
    EntityManager entityManager = entityManagerFactory.createEntityManager();

    public void createUserSecurity(UserSecurityEntity user) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(user);
        entityManager.flush();
        entityManager.refresh(user);
        transaction.commit();
    }

    public UserSecurityEntity getUserSecurityByID(int id) {
        return entityManager.find(UserSecurityEntity.class, id);
    }
}
