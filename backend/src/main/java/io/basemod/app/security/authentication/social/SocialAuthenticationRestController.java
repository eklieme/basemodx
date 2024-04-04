package io.basemod.app.security.authentication.social;

import io.basemod.app.security.authentication.domain.*;
import io.basemod.app.security.authentication.social.flow.AccessCodeResponse;
import io.basemod.app.security.authentication.social.flow.AccessTokenRequest;
import io.basemod.app.security.authentication.social.flow.AccessTokenResponse;
import io.basemod.app.security.authentication.social.github.GitHubAccessTokenResponse;
import io.basemod.app.security.authentication.social.github.GitHubUserInformation;
import io.basemod.app.security.authentication.social.flow.ExtendedAccessTokenRequest;
import io.basemod.app.security.authentication.social.google.GoogleUserInformation;
import io.basemod.app.security.authentication.utils.JwtUtils;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;


@RestController
@RequestMapping("/auth")
public class SocialAuthenticationRestController {

    private static final String GITHUB_CLIENT_ID_ENV_VAR = "GITHUB_CLIENT_ID";
    private static final String GITHUB_CLIENT_SECRET_ENV_VAR = "GITHUB_CLIENT_SECRET";
    private static final String GOOGLE_CLIENT_ID_ENV_VAR = "GOOGLE_CLIENT_ID";
    private static final String GOOGLE_CLIENT_SECRET_ENV_VAR = "GOOGLE_CLIENT_SECRET";
    private static final String REDIRECT_URI_ENV_VAR = "REDIRECT_URI";

    private static final String SOCIAL_PROVIDER_ID_GITHUB = "github";
    private static final String SOCIAL_PROVIDER_ID_GOOGLE = "google";
    private static final String DEPLOYMENT_MODE_DEMO = "demo";


    private RestTemplate restTemplate;
    private MongoTemplate mongoTemplate;

    JwtUtils jwtUtils;

    private BaseUserController baseUserController;

    @Value("${deployment.mode:unknown}")
    private String deploymentMode;


    @Autowired
    public SocialAuthenticationRestController(MongoTemplate mongoTemplate, BaseUserController baseUserController,
                                              JwtUtils jwtUtils) {

        restTemplate = new RestTemplate();
        this.mongoTemplate = mongoTemplate;
        this.baseUserController = baseUserController;
        this.jwtUtils = jwtUtils;

    }

    Logger logger = LoggerFactory.getLogger(SocialAuthenticationRestController.class);

    @RequestMapping(value = "/github", method = RequestMethod.POST)
    public BaseSessionInformation socialLoginWithGithub(@RequestBody AccessCodeResponse gitHubAccessCodeResponse) {

        logger.debug("Got access code '{}' result from github, prepare request for access token",
                    gitHubAccessCodeResponse.getCode());

        AccessTokenRequest gitHubAccessTokenRequest = new AccessTokenRequest(
                System.getenv(GITHUB_CLIENT_ID_ENV_VAR),
                System.getenv(GITHUB_CLIENT_SECRET_ENV_VAR),
                gitHubAccessCodeResponse.getCode());

        logger.trace("Prepared access token request {}, query github api", gitHubAccessTokenRequest);

        ResponseEntity<GitHubAccessTokenResponse> accessTokenResponse
                = restTemplate.postForEntity("https://github.com/login/oauth/access_token",
                    gitHubAccessTokenRequest, GitHubAccessTokenResponse.class);

        logger.trace("Got access token response {}", accessTokenResponse.getBody());
        logger.trace("Request user Id information from github api");

        ResponseEntity<GitHubUserInformation> githubUserInformation = restTemplate.exchange("https://api.github.com/user",
                HttpMethod.GET,
                new HttpEntity(createGitHubAuthHeaders(accessTokenResponse.getBody().getAccess_token())),
                GitHubUserInformation.class);

        logger.trace("Got github user information {}", githubUserInformation.getBody());

        String user_id = githubUserInformation.getBody().getLogin();

        logger.debug("Extract user id '{}' for front end", user_id);

        SocialLoginUserInformation socialLoginUserInformation =
                new SocialLoginUserInformation(SOCIAL_PROVIDER_ID_GITHUB, user_id, accessTokenResponse.getBody().getAccess_token());

        return loginUser(socialLoginUserInformation);

    }

    @RequestMapping(value = "/google", method = RequestMethod.POST)
    public BaseSessionInformation socialLoginWithGoogle(@RequestBody AccessCodeResponse accessCodeResponse) {

        logger.debug("Got access code '{}' result from google, prepare request for access token",
                accessCodeResponse.getCode());

        ExtendedAccessTokenRequest extendedAccessTokenRequest = new ExtendedAccessTokenRequest(
                System.getenv(GOOGLE_CLIENT_ID_ENV_VAR),
                System.getenv(GOOGLE_CLIENT_SECRET_ENV_VAR),
                accessCodeResponse.getCode(),
                "authorization_code",
                System.getenv(REDIRECT_URI_ENV_VAR));

        logger.trace("Prepared access token request {}, query google api", extendedAccessTokenRequest);

        ResponseEntity<AccessTokenResponse> accessTokenResponse
                = restTemplate.postForEntity("https://oauth2.googleapis.com/token",
                extendedAccessTokenRequest, AccessTokenResponse.class);

        logger.trace("Got google access token response {}", accessTokenResponse.getBody());
        logger.trace("Request user Id information from google api");

        ResponseEntity<GoogleUserInformation> googleUserInformation = restTemplate.exchange("https://www.googleapis.com/oauth2/v1/userinfo?alt=json",
                HttpMethod.GET,
                new HttpEntity(createGoogleAuthHeaders(accessTokenResponse.getBody().getAccess_token())),
                GoogleUserInformation.class);

        logger.trace("Got github user information {}", googleUserInformation.getBody());

        String user_id = googleUserInformation.getBody().getEmail();

        logger.debug("Extract google user id '{}' for front end", user_id);

        SocialLoginUserInformation socialLoginUserInformation =
                new SocialLoginUserInformation(SOCIAL_PROVIDER_ID_GOOGLE, user_id, accessTokenResponse.getBody().getAccess_token());

        return loginUser(socialLoginUserInformation);

    }

    public BaseSessionInformation loginUser(SocialLoginUserInformation socialLoginUserInformation) {


        BaseUser baseUser = baseUserController.getBaseUserUsingSocialLoginInformation(socialLoginUserInformation);


        Authentication authentication = new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return baseUser.getRolesForSecurityContext();
            }

            @Override
            public Object getCredentials() {
                return "";
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return baseUser;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
                setAuthenticated(isAuthenticated);
            }

            @Override
            public String getName() {
                return null;
            }
        };

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        return new BaseSessionInformation(baseUser,jwt);


    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public Response getLoggedInUserInformation() {

        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String) {
            String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            logger.debug("\t...security context does NOT hold baseUSER, instead {}", principal);
            return Response.status(Response.Status.NOT_FOUND).build();
        } else if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof BaseUser) {
            BaseUser principal = (BaseUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            logger.debug("\t...found user Information request for user {}", principal.getUniqueId());

            return Response.status(Response.Status.FOUND).entity(new BaseSessionInformation(principal, "")).build();
        }

        logger.debug("\t...unknown security context!");
        return Response.status(Response.Status.NOT_FOUND).build();

    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public Response updateUserInformation(@RequestBody BaseUser updatedBaseUser) {

        if(deploymentMode.equals(DEPLOYMENT_MODE_DEMO)) {
            String uniqueUserId = updatedBaseUser.getSocialLoginProvider()+"_"+ updatedBaseUser.getSocialLoginUserId();
            logger.debug("requesting change of user '{}'", uniqueUserId);
            BaseUser baseUserToChange = baseUserController.getBaseUserUsingUniqueId(uniqueUserId);
            if(baseUserToChange!=null) {
                logger.debug("\t...base user's original roles: {}", baseUserToChange.getBaseUserRoles().size());
                logger.debug("\t...base user's NEW roles: {}", updatedBaseUser.getBaseUserRoles().size());
                baseUserToChange.setBaseUserRoles(updatedBaseUser.getBaseUserRoles());
                baseUserController.saveBaseUser(baseUserToChange);
                logger.debug("\t...changed base user");
                return Response.ok().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }

        logger.debug("\t...deployment mode does not allow change of base User");
        return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();

    }


    private HttpHeaders createGitHubAuthHeaders(String accessToken){
        return new HttpHeaders() {{
            set( "Authorization", "token "+accessToken );
        }};
    }

    private HttpHeaders createGoogleAuthHeaders(String accessToken){
        return new HttpHeaders() {{
            set( "Authorization", "Bearer "+accessToken );
        }};
    }

}

