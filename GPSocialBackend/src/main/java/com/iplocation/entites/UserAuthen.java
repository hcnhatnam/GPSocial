/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iplocation.entites;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author namhcn
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserAuthen {

    private String email;
    private String password;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserAuthen)) {
            return false;
        }
        UserAuthen other = (UserAuthen) o;
        return this.email.equals(other.email) && this.password.equals(other.password);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.email);
        hash = 73 * hash + Objects.hashCode(this.password);
        return hash;
    }

}
