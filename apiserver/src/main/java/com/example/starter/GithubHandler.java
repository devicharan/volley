package com.example.starter;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.auth.oauth2.AccessToken;

import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import io.vertx.reactivex.ext.auth.oauth2.OAuth2Auth;

import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.Session;
import io.vertx.reactivex.ext.web.client.WebClient;

public class GithubHandler implements Handler<RoutingContext> {

  OAuth2Auth oAuth2Auth;
  Vertx vertx;
  String devUrl = "http://localhost:3003/api/githubcallback";

  public GithubHandler(Vertx vertx, OAuth2Auth authProvider) {
    this.vertx = vertx;
    oAuth2Auth = authProvider;
  }

  @Override
  public void handle(RoutingContext context) {
  }
  public void doAuthenticate(RoutingContext context){
    try {
      String code =context.request().getParam("code");
      oAuth2Auth.rxAuthenticate(
        new JsonObject()
          .put("code", code)
          .put("scope", "user:email")
          .put("redirect_uri",  devUrl)).subscribe(user -> {

        try {


          oAuth2Auth.userInfo(user,
            res -> {
              if (res.failed()) {
                if (context.session() != null)
                  context.session().destroy();
                context.fail(res.cause());
              } else {

                final JsonObject userInfo = res.result();


                WebClient client = WebClient.create(vertx, new WebClientOptions().setLogActivity(true));
                client.get(443,"api.github.com", "/user/emails") .ssl(true).authentication(new TokenCredentials(user.principal().getString("access_token"))).send(res2 -> {

                  if (res2.failed()) {
                    System.out.println("failed retriving ");
                    // request didn't succeed because the token was revoked so we
                    // invalidate the token stored in the session and render the
                    // index page so that the user can start the OAuth flow again
                    context.session().destroy();
                    context.fail(res2.cause());
                  } else {
                    System.out.println("succeeded authenticting");
                    // once authentication is successfull .. store the info..
                    JsonArray emails = res2.result().bodyAsJsonArray();
                    userInfo.put("private_emails", res2.result().bodyAsJsonArray());
                    String userEmail = emails.getJsonObject(0).getValue("email").toString();
                    Session session = context.session();
                    Session sessionID = session.regenerateId();
                  /* you can look for this sessionid in the store
                      If the sessionid is still present then this jwt is valid otherwise invalid.
                   */



                    context.response().putHeader("location", "/").setStatusCode(302).end();

                  }
                });


              }
            });


        } catch (Exception e) {
          System.out.println("ex " + e.toString());
        }

      }, throwable -> {
        System.out.println(" Error while obtaining userinfo" + throwable.toString());
      });
    } catch (Exception e) {
      System.out.println(" Error while obtaining userinfo" + e.toString());
    }
  }
  public void doAuthorize(RoutingContext routingContext) {
    String dev ="http://localhost:3003/api/githubcallback";
    // Authorization oauth2 URI
    String authorization_uri = oAuth2Auth.authorizeURL(new JsonObject()
      .put("redirect_uri",  dev)
      .put("scope", "user:email")
      .put("state", "3(#0/!~"));

    // Redirect example using Vert.x
    routingContext.response().putHeader("Location", authorization_uri)
      .setStatusCode(302)
      .end();
  }
}
