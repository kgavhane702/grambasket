// File: auth-service/src/main/java/com/grambasket/authservice/model/User.java
package com.grambasket.authservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User implements UserDetails {
    @Id
    private String id;
    private String username;
    private String password;

    /**
     * MODIFIED: Added @Builder.Default to prevent NullPointerExceptions.
     * Every new user will now automatically be assigned the USER role.
     */
    @Builder.Default
    private Set<Role> roles = Set.of(Role.USER);

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Using toSet() is slightly more idiomatic here since roles is a Set.
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());
    }

    // ... other UserDetails methods are fine ...
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}