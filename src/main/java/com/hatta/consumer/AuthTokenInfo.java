/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hatta.consumer;

/**
 *
 * @author Hatta NR
 */
public class AuthTokenInfo {
    
    private String Access_token;
    private String Token_type;
    private String Refresh_token;
    private int Expires_in;
    private String Scope;

    public String getAccess_token() {
        return Access_token;
    }

    public void setAccess_token(String Access_token) {
        this.Access_token = Access_token;
    }

    public String getToken_type() {
        return Token_type;
    }

    public void setToken_type(String Token_type) {
        this.Token_type = Token_type;
    }

    public String getRefresh_token() {
        return Refresh_token;
    }

    public void setRefresh_token(String Refresh_token) {
        this.Refresh_token = Refresh_token;
    }

    public int getExpires_in() {
        return Expires_in;
    }

    public void setExpires_in(int Expires_in) {
        this.Expires_in = Expires_in;
    }

    public String getScope() {
        return Scope;
    }

    public void setScope(String Scope) {
        this.Scope = Scope;
    }
   
}
