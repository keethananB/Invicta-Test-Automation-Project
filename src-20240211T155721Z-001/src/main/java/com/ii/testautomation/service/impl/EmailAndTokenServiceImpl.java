package com.ii.testautomation.service.impl;

import com.ii.testautomation.config.EmailConfiguration;
import com.ii.testautomation.entities.CompanyUser;
import com.ii.testautomation.entities.Project;
import com.ii.testautomation.entities.Users;
import com.ii.testautomation.enums.LoginStatus;
import com.ii.testautomation.repositories.CompanyUserRepository;
import com.ii.testautomation.repositories.DesignationRepository;
import com.ii.testautomation.repositories.LicensesRepository;
import com.ii.testautomation.repositories.ProjectRepository;
import com.ii.testautomation.repositories.UserRepository;
import com.ii.testautomation.service.EmailAndTokenService;
import com.ii.testautomation.utils.Constants;
import com.ii.testautomation.utils.EmailBody;
import com.ii.testautomation.utils.StatusCodeBundle;
import io.jsonwebtoken.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@PropertySource("classpath:MessagesAndCodes.properties")
@Service
public class EmailAndTokenServiceImpl implements EmailAndTokenService {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private CompanyUserRepository companyUserRepository;
  @Autowired
  private LicensesRepository licensesRepository;
  @Autowired
  private EmailConfiguration emailConfiguration;
  @Autowired
  private JavaMailSender javaMailSender;
  @Autowired
  private ResourceLoader resourceLoader;
  @Autowired
  private EmailBody emailBody;
  @Autowired
  ProjectRepository projectRepository;
  @Autowired
  private StatusCodeBundle statusCodeBundle;
  @Autowired
  private DesignationRepository designationRepository;

  @Value("${user.verification.email.subject}")
  private String userVerificationMailSubject;
  @Value("${user.verification.email.body}")
  private String userVerificationMailBody;
  @Value("${reset.password.email.subject}")
  private String passwordResetMailSubject;
  @Value("${reset.password.email.body}")
  private String passwordResetMailBody;
  @Value("${email.send.temporaryPassword.subject}")
  private String temporaryPasswordSendMailSubject;
  @Value("${email.send.temporaryPassword.body}")
  private String temporaryPasswordSendMailBody;

  @Override
  public String generateToken(Users user) {
    UUID uuid = UUID.randomUUID();
    String uniqueIdentifier = uuid.toString().substring(0,8);
    user.setUniqueIdentification(uniqueIdentifier);
    userRepository.save(user);

    Claims claims = Jwts.claims().setIssuer(user.getId().toString());
    claims.put("Roll",user.getDesignation().getName());
    claims.put("companyUserId",user.getCompanyUser().getId());
    return Jwts.builder()
          .setClaims(claims)
          .signWith(SignatureAlgorithm.HS256, uniqueIdentifier).compact();
  }

  @Override
  public void sendTokenToEmail(Users user) {
    Resource resource = resourceLoader.getResource("classpath:Templates/button.html");
    try {
      String token = generateToken(user);
      MimeMessage mimeMessage = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
      helper.setTo(user.getEmail());
      if (user.getStatus() == LoginStatus.NEW.getStatus()) {
        helper.setSubject(userVerificationMailSubject);
        helper.setText(emailBody.getEmailBody1()+token+emailBody.getEmailBody2(), true);
      }
      else
      {
        helper.setSubject(passwordResetMailSubject);
        helper.setText(emailBody.getEmailBody1()+token+emailBody.getEmailBody2(), true);
      }
      javaMailSender.send(mimeMessage);
      user.setStatus(LoginStatus.MAILED.getStatus());
      userRepository.save(user);
    }  catch (MessagingException e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  public void sendTempPasswordToEmail(String token) {
      BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
      Users user = getUserByToken(token);
      user.setStatus(LoginStatus.PENDING.getStatus());
      UUID uuid = UUID.randomUUID();
      String tempPassword = uuid.toString().substring(0,8);
      user.setPassword(bCryptPasswordEncoder.encode(tempPassword));
      userRepository.save(user);
      if (user.getDesignation().getName().equals(Constants.COMPANY_ADMIN.toString()))
      {
        CompanyUser companyAdmin = companyUserRepository.findById(user.getCompanyUser().getId()).get();
        companyAdmin.setStatus(true);
        companyUserRepository.save(companyAdmin);
      }
      SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
      simpleMailMessage.setTo(user.getEmail());
      simpleMailMessage.setSubject(temporaryPasswordSendMailSubject);
      simpleMailMessage.setText(temporaryPasswordSendMailBody+""+tempPassword);
      javaMailSender.send(simpleMailMessage);
    }

  @Override
  public String verifyToken(String token) {
    try {
      Users user = getUserByToken(token);
      Jwts.parser().setSigningKey(user.getUniqueIdentification()).parseClaimsJws(token);
      return Constants.TOKEN_VERIFIED;
    } catch (ExpiredJwtException e) {

      return statusCodeBundle.getTokenExpiredMessage();
    }
    catch (Exception e) {
      return e.getMessage();
    }
  }

  @Override
  public Users getUserByToken(String token) {
    String[] parts = token.split("\\.");
    String payload = new String(Base64.getDecoder().decode(parts[1]));
    String issuer = payload.split(",")[0].split(":")[1].replaceAll("\"", "");
    Long userId = Long.parseLong(issuer);
    Users user = userRepository.findById(userId).get();
    return user;
  }
}
