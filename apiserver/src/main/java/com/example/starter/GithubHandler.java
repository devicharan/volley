package com.example.starter;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.oauth2.AccessToken;
import io.vertx.ext.jwt.JWTOptions;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import io.vertx.reactivex.ext.auth.oauth2.OAuth2Auth;
import io.vertx.reactivex.ext.web.Cookie;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.Session;

public class GithubHandler implements Handler<RoutingContext> {

  OAuth2Auth oAuth2Auth;
  JWTAuth m_jwtAuth;
  String devUrl = "http://localhost:3003/api/githubcallback";

  public GithubHandler(JWTAuth jwtAuth, OAuth2Auth authProvider) {
    m_jwtAuth = jwtAuth;
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
          AccessToken tk = (AccessToken) user.getDelegate();

          tk.userInfo(
            res -> {
              if (res.failed()) {
                if (context.session() != null)
                  context.session().destroy();
                context.fail(res.cause());
              } else {

                final JsonObject userInfo = res.result();

                tk.fetch("https://api.github.com/user/emails", res2 -> {
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
                 //   String userEmail = res2.result().body().toJsonObject().getValue("email").toString();
                   Session session = context.session();
                    Session sessionID = session.regenerateId();
                  /* you can look for this sessionid in the store
                      If the sessionid is still present then this jwt is valid otherwise invalid.
                   */
                    String jwtToken = m_jwtAuth.generateToken(new JsonObject().put("userID", "1001")
                      .put("userName", userInfo.getValue("login"))
                      .put("session", sessionID.id().toString()), new JWTOptions().setExpiresInSeconds(6000));

                    Cookie cookie = Cookie.cookie("token", jwtToken);
                    String path = "/"; //give any suitable path
                    cookie.setPath(path);
                    cookie.setMaxAge(6000);
                    context.addCookie(cookie);

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
