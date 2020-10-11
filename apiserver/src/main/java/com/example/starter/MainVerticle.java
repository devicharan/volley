package com.example.starter;

import com.example.starter.Auth.CustomJwtAuthHandler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.auth.oauth2.OAuth2ClientOptions;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;

import io.vertx.reactivex.ext.auth.oauth2.OAuth2Auth;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.OAuth2AuthHandler;
import io.vertx.reactivex.ext.web.handler.SessionHandler;
import io.vertx.reactivex.ext.web.sstore.LocalSessionStore;

public class MainVerticle extends AbstractVerticle {
  /*
     Nginix listens on 3003 , serves static html ,react files
     Nginix forwards all "/api" requests to vertx.

   */

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    Router rxRouter = Router.router(vertx);
    rxRouter.route().handler(this::recordTime);
    SessionHandler sessionHandler = SessionHandler.create(LocalSessionStore.create(vertx));
    JwtSetter jwtSetter = new JwtSetter();
    rxRouter.route().handler(jwtSetter::handle);
    rxRouter.route().handler(sessionHandler);



    JWTAuth jwtAuth = JWTAuth.create( vertx, new JWTAuthOptions()
      .addPubSecKey(new PubSecKeyOptions()
          .setAlgorithm("HS256")
          .setPublicKey("keyboard cat")
          .setSymmetric(true)));

    CustomJwtAuthHandler jwtAuthHandler = (CustomJwtAuthHandler) CustomJwtAuthHandler.create(jwtAuth.getDelegate());
    String gitClientID = "";
    String gitClientSecret = "";


    OAuth2Auth oauth2 = OAuth2Auth.create(vertx, new OAuth2ClientOptions()
      .setFlow(OAuth2FlowType.AUTH_CODE)
      .setClientID(gitClientID)
      .setClientSecret(gitClientSecret)
      .setSite("https://github.com/login")
      .setTokenPath("/oauth/access_token")
      .setAuthorizationPath("/oauth/authorize")
      .setUserInfoPath("https://api.github.com/user")
      .setScopeSeparator(" ")
      .setHeaders(new JsonObject()
        .put("User-Agent", "vertx-auth-oauth2"))
    );
    GithubHandler socialAuthHandler = new GithubHandler((io.vertx.reactivex.ext.auth.jwt.JWTAuth) jwtAuth, oauth2);

    rxRouter.route("/githubauth").handler(socialAuthHandler::doAuthorize);
    rxRouter.route("/githubcallback").handler(socialAuthHandler::doAuthenticate);
    rxRouter.get("/protected").handler(jwtAuthHandler::handle);
    rxRouter.get("/protected").handler(routingContext -> {
      routingContext.response().setStatusCode(200).end();
    });


    System.out.println("starting");
    HttpServerOptions options = new HttpServerOptions().setLogActivity(true);
    options.setCompressionSupported(true);
    vertx.createHttpServer(options).requestHandler(rxRouter::accept).listen(3020);

  }

  private  void recordTime(RoutingContext routingContext){
    long start = System.nanoTime();
    routingContext.put("start-"+routingContext.request().path(), start);
    System.out.println("hit the path"+routingContext.request().path());
    routingContext.next();

  }


  }

