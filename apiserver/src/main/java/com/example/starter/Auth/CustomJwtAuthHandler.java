package com.example.starter.Auth;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.impl.JWTUser;
import io.vertx.ext.web.RoutingContext;
//import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.ext.web.handler.impl.HttpStatusException;
import io.vertx.ext.web.handler.impl.JWTAuthHandlerImpl;


import java.util.List;

public class CustomJwtAuthHandler  extends CustomAuthorizationAuthHandler implements JWTAuthHandler {

  private final String skip;
  private final JsonObject options;

  public static JWTAuthHandler create(JWTAuth authProvider) {
    return new CustomJwtAuthHandler(authProvider, (String)null);
  }

  public static JWTAuthHandler create(JWTAuth authProvider, String skip) {
    return new CustomJwtAuthHandler(authProvider, skip);
  }

  public CustomJwtAuthHandler(JWTAuth authProvider, String skip) {
    super(authProvider, CustomAuthorizationAuthHandler.Type.BEARER);
    this.skip = skip;
    this.options = new JsonObject();
  }

  CustomJwtAuthHandler(AuthProvider authProvider, Type type, String skip, JsonObject options) {
    super(authProvider, type);
    this.skip = skip;
    this.options = options;
  }

  public JWTAuthHandler setAudience(List<String> audience) {
    this.options.put("audience", new JsonArray(audience));
    return this;
  }

  public JWTAuthHandler setIssuer(String issuer) {
    this.options.put("issuer", issuer);
    return this;
  }

  public JWTAuthHandler setIgnoreExpiration(boolean ignoreExpiration) {
    this.options.put("ignoreExpiration", ignoreExpiration);
    return this;
  }
  public void handle(io.vertx.reactivex.ext.web.RoutingContext ctx) {
    super.handle(ctx.getDelegate());
  }
  public void parseCredentials(RoutingContext context, Handler<AsyncResult<JsonObject>> handler) {
    if (this.skip != null && context.normalisedPath().startsWith(this.skip)) {
      context.next();
    } else {
      this.parseAuthorization(context, false, (parseAuthorization) -> {
        if (parseAuthorization.failed()) {
          handler.handle(Future.failedFuture(parseAuthorization.cause()));
        } else {
          JsonObject jsonObject = new JsonObject().put("jwt", (String)parseAuthorization.result()).put("options", this.options);
          ((JWTAuth)this.authProvider).authenticate(jsonObject,userAsyncResult -> {
            if (userAsyncResult.failed()){
              handler.handle(Future.failedFuture(new HttpStatusException(401, userAsyncResult.cause().getMessage())));
            } else {
              context.setUser((User) userAsyncResult.result());
              handler.handle(Future.succeededFuture((new JsonObject()).put("jwt", (String)parseAuthorization.result()).put("options", this.options)));
            }
          });
        }
      });
    }
  }

  protected String authenticateHeader(RoutingContext context) {
    return "Bearer";
  }
}
