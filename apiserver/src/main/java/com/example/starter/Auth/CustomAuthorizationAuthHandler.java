package com.example.starter.Auth;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.impl.AuthHandlerImpl;
import io.vertx.ext.web.handler.impl.HttpStatusException;


abstract class CustomAuthorizationAuthHandler extends AuthHandlerImpl {
  protected final  CustomAuthorizationAuthHandler.Type type;
  static final HttpStatusException FORBIDDEN = new HttpStatusException(403);
  static final HttpStatusException UNAUTHORIZED = new HttpStatusException(401);
  static final HttpStatusException BAD_REQUEST = new HttpStatusException(400);

  CustomAuthorizationAuthHandler(AuthProvider authProvider, CustomAuthorizationAuthHandler.Type type) {
    super(authProvider);
    this.type = type;
  }

  CustomAuthorizationAuthHandler(AuthProvider authProvider, String realm, CustomAuthorizationAuthHandler.Type type) {
    super(authProvider, realm);
    this.type = type;
  }
  public void handle(RoutingContext ctx) {
    super.handle(ctx);
  }

  protected final void parseAuthorization(RoutingContext ctx, boolean optional, Handler<AsyncResult<String>> handler) {
    HttpServerRequest request = ctx.request();
    String authorization = request.headers().get(HttpHeaders.AUTHORIZATION);
    if (authorization == null) {
      if (optional) {
        handler.handle(Future.succeededFuture());
      } else {
        handler.handle(Future.failedFuture(UNAUTHORIZED));
      }

    } else {
      try {
        int idx = authorization.indexOf(32);
        if (idx <= 0) {
          handler.handle(Future.failedFuture(BAD_REQUEST));
          return;
        }

        if (!this.type.is(authorization.substring(0, idx))) {
          handler.handle(Future.failedFuture(UNAUTHORIZED));
          return;
        }

        handler.handle(Future.succeededFuture(authorization.substring(idx + 1)));
      } catch (RuntimeException var7) {
        handler.handle(Future.failedFuture(var7));
      }

    }
  }

  static enum Type {
    BASIC("Basic"),
    DIGEST("Digest"),
    BEARER("Bearer"),
    HOBA("HOBA"),
    MUTUAL("Mutual"),
    NEGOTIATE("Negotiate"),
    OAUTH("OAuth"),
    SCRAM_SHA_1("SCRAM-SHA-1"),
    SCRAM_SHA_256("SCRAM-SHA-256");

    private final String label;

    private Type(String label) {
      this.label = label;
    }

    public boolean is(String other) {
      return this.label.equalsIgnoreCase(other);
    }
  }
}
