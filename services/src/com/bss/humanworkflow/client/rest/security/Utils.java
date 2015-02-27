package com.bss.humanworkflow.client.rest.security;

import java.io.ByteArrayOutputStream;
import com.bss.security.JWTokens;

import java.net.URLEncoder;

import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.Cookie;

import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.core.Request;

public class Utils {
  
  public static Cookie createCookie(String cookieName, String payload) {
    System.out.println("createCookie cookieName -> " + cookieName +
                       ", payload -> " + payload);

    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      GZIPOutputStream gzip = new GZIPOutputStream(out);

      gzip.write(payload.getBytes("UTF8"));
      gzip.close();

      payload =
          JWTokens.encodeBase64(new String(out.toByteArray()));
    } catch (Exception e) {
      e.printStackTrace();
    }

    Cookie cookie = new Cookie(cookieName, payload);
    cookie.setMaxAge(60 * 60 * 24 * 365);
    cookie.setPath("/");

    return cookie;
  }
}
