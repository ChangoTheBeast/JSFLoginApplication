package com.sparta.reece.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "users", schema = "login")
@NamedQueries({
        @NamedQuery(name = "UsersEntity.getAllUsers",
                query = "SELECT u FROM UsersEntity u"),
        @NamedQuery(name = "UsersEntity.getUsersByRole",
                query = "SELECT u FROM UsersEntity u WHERE u.role = :role"),
        @NamedQuery(name = "UsersEntity.getUserByName",
                query = "SELECT u FROM UsersEntity u WHERE u.username = :username")
})
public class UsersEntity {
    private Integer userId;
    private String username;
    private String password;
    private String role;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Basic
    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "role")
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsersEntity that = (UsersEntity) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(username, that.username) &&
                Objects.equals(password, that.password) &&
                Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        return result;
    }
}
