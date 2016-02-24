package com.bss.humanworkflow.client.rest.types;

public class AuthenticateInput {
  
  protected String login;
  
  protected String password;

  public void setLogin(String login) {
    this.login = login;
  }

  public String getLogin() {
    return login;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPassword() {
    return password;
  }
}
