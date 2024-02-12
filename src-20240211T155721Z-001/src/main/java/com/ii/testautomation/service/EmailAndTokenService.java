package com.ii.testautomation.service;

import com.ii.testautomation.entities.Users;

public interface EmailAndTokenService {

  void sendTokenToEmail(Users user);

  void sendTempPasswordToEmail(String token);

  String verifyToken(String token);

  String generateToken(Users user);

  Users getUserByToken(String token);
}
