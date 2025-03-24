package com.quick_task.security.security_config;

import com.quick_task.entity.WebUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class MyUserDetails implements UserDetails {
    private Long userId;
    private String email;
    private String password;
    private Collection<GrantedAuthority> authorities;


    public MyUserDetails(Long userId, String email, String password, Collection<GrantedAuthority> authorities) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }


    public static MyUserDetails buildUserDetails(WebUser user) {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("USER"));

        return new MyUserDetails(
                user.getIdWebUser(),
                user.getMailUser(),
                user.getPasswordUser(),
                authorities);

    }
        @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }
//стоит работать с этим
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
