package com.sparta.reece.entities;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "user_security", schema = "login")
public class UserSecurityEntity {
    private Integer userId;
    private byte[] salt;
    private Integer iterations;

    @Id
    @Column(name = "user_id")
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "salt")
    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    @Basic
    @Column(name = "iterations")
    public Integer getIterations() {
        return iterations;
    }

    public void setIterations(Integer iterations) {
        this.iterations = iterations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSecurityEntity that = (UserSecurityEntity) o;
        return Objects.equals(userId, that.userId) &&
                Arrays.equals(salt, that.salt) &&
                Objects.equals(iterations, that.iterations);
    }

    @Override
    public int hashCode() {
        int result = (iterations != null ? iterations.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(salt);
        return result;
    }
}
