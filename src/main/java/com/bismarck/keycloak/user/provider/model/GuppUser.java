package com.bismarck.keycloak.user.provider.model;

import jakarta.persistence.*;

import java.math.BigInteger;

/**
 * Model representation of a Gupp-User
 *
 * @author n.bismarck
 * @since 0.1.0
 */
@NamedQueries({
        @NamedQuery(name="getUserByUsername", query="select u from GuppUser u where u.username = :username"),
        @NamedQuery(name="getUserByEmail", query="select u from GuppUser u where u.email = :email"),
        @NamedQuery(name="getUserCount", query="select count(u) from GuppUser u"),
        @NamedQuery(name="getAllUsers", query="select u from GuppUser u"),
        @NamedQuery(name="searchForUser", query="select u from GuppUser u where " +
                "( lower(u.username) like :search or u.email like :search ) order by u.username"),
})
@Entity
@Table(name="user")
public class GuppUser {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(columnDefinition = "BIGINT", unique = true)
    private BigInteger id;

    private String username;
    private String email;
    private String password;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private String apps;
    private String tenant;

    public String getId() {
        return String.valueOf(id);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getTenant() {
        return this.tenant;
    }
}
