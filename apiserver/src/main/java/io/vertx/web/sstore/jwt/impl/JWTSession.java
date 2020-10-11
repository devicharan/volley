package io.vertx.web.sstore.jwt.impl;

import io.vertx.ext.auth.PRNG;
import io.vertx.ext.auth.VertxContextPRNG;
import io.vertx.ext.web.sstore.AbstractSession;

import javax.crypto.Mac;

public class JWTSession extends AbstractSession {

  private final Mac mac;

  public JWTSession(Mac mac, VertxContextPRNG prng, long timeout, int length) {
    super((PRNG) prng, timeout, length);
    this.mac = mac;
  }

  public JWTSession(Mac mac, VertxContextPRNG prng) {
    super((PRNG) prng);
    this.mac = mac;
  }
}
