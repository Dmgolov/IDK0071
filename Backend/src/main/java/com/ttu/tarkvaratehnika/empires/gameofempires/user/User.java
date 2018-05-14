package com.ttu.tarkvaratehnika.empires.gameofempires.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ttu.tarkvaratehnika.empires.gameofempires.security.Constants;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;

@Entity
public class User implements UserDetails {

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

    @JsonIgnore
    @Override
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Transient
    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Transient
    @JsonIgnore
    @Override
    public String getUsername() {
        return getName();
    }

    @Transient
    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Transient
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Transient
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Transient
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}
