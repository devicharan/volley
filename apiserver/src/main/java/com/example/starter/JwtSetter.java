package com.example.starter;

import io.vertx.core.Handler;

import io.vertx.reactivex.core.http.Cookie;
import io.vertx.reactivex.ext.web.RoutingContext;

public class JwtSetter  implements Handler<RoutingContext> {

  @Override
  public void handle(RoutingContext routingContext) {
    Cookie cookieValue =routingContext.getCookie("token");

    if(cookieValue !=null) {
      String token = routingContext.getCookie("token").getValue();
      routingContext.request().headers().add("Authorization", "Bearer " + token);
      routingContext.next();
    } else {
      routingContext.next();
    }
  }
}
