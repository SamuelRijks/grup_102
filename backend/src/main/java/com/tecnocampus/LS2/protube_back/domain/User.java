package com.tecnocampus.LS2.protube_back.domain;

import jakarta.persistence.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "uploader", cascade = CascadeType.REMOVE)
    private List<Video> videos;

    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE)
    private List<Comment> comments;


    // Getters and Setters

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // Return roles or authorities if you have them
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Modify if you need custom logic
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Modify if you need custom logic
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Modify if you need custom logic
    }

    @Override
    public boolean isEnabled() {
        return true; // Modify if you need custom logic
    }
}