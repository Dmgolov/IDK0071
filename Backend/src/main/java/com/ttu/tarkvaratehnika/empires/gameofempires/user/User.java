package com.ttu.tarkvaratehnika.empires.gameofempires.user;

import com.ttu.tarkvaratehnika.empires.gameofempires.securityconstants.Constants;

import javax.persistence.*;

//TODO: add all needed values for User table and getters/setters for transferring data from and to the database
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true, nullable = false)
    private String name;
    @Column(nullable = false, length = Constants.PASSWORD_LENGTH)
    private String password;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String salt;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
