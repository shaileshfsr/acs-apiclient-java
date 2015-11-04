/*
The MIT License (MIT)

Copyright (c) 2015 Answers Cloud Services

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */


package com.foresee.auth;

import com.foresee.auth.oauth.*;
import com.foresee.interfaces.http.*;
import com.foresee.interfaces.logging.LoggerAbstraction;
import com.foresee.logging.LogManager;

import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;

/**
 * Created by bradley.bax on 10/7/2015.
 */
public class AcsApiClient {
    private LogManager _logger = null;
    private ClientBuilderAbstraction _httpClientBuilder = null;
    private RequestBuilderAbstraction _requestBuilder = null;
    private PostBodyBuilderAbstraction _postBodyBuilder = null;

    protected AcsApiClientConfig _configuration;

    public AcsApiClient(AcsApiClientConfig configuration,
                        ClientBuilderAbstraction clientBuilder,
                        RequestBuilderAbstraction requestBuilder,
                        PostBodyBuilderAbstraction postBodyBuilder,
                        LoggerAbstraction logger){
        _logger = new LogManager(logger);
        _httpClientBuilder = clientBuilder;
        _configuration = configuration;
        _requestBuilder = requestBuilder;
        _postBodyBuilder = postBodyBuilder;
    }

    private String GetOAuthHeader(OAuthAccessor auth, String type, String url){
        try {
            OAuthMessage msg = auth.newRequestMessage(type, url, null);
            String authHeader = msg.getAuthorizationHeader(null);
            _logger.debug(authHeader);
            return authHeader;
        }catch(Exception exc){
            _logger.error(exc.getMessage());
            return null;
        }
    }

    private ResponseAbstraction GetTokenRequest(CookieManager mng){
        String authHeader = null;
        OAuthAccessor auth = null;
        try{
            auth = ConstructOAuthEngine(true);
            authHeader = GetOAuthHeader(auth, OAuthMessage.GET, auth.consumer.serviceProvider.requestTokenURL);
            _logger.debug("header: " + authHeader);
            if(authHeader == null){
                _logger.error("Invalid authentication headers returned " + authHeader);
                return null;
            }
        }catch(Exception exc){
            _logger.error("GetToken failed to retrieve own authorization header: " + exc.getMessage());
            return null;
        }

        try{
            RequestAbstraction request = _requestBuilder.builder()
                    .withUrl(auth.consumer.serviceProvider.requestTokenURL)
                    .withHeader("Authorization", authHeader).build();

            ClientAbstraction client = _httpClientBuilder.create();
            client.setCookieManager(mng);

            return client.newCall(request).execute();
        }catch(Exception exc){
            _logger.error(exc.getMessage());
            return null;
        }
    }

    private boolean InvokeLogin(String username, String password, CookieManager mng){
        try{
            String postDetails = "j_username=" + username + "&j_password=" + password;
            PostBodyAbstraction body = _postBodyBuilder.create("application/x-www-form-urlencoded", postDetails);

            String loginUrl = _configuration.ForeseeServicesUri + _configuration.LoginAction;
            RequestAbstraction request = _requestBuilder.builder().withUrl(loginUrl).withPostBody(body).build();

            ClientAbstraction client = _httpClientBuilder.create();
            client.setCookieManager(mng);
            client.setFollowRedirects(false);
            ResponseAbstraction response = client.newCall(request).execute();
            return response.isRedirect();

        }catch(Exception exc){
            _logger.error(exc.getMessage());
        }
        return false;
    }

    private HashMap<String, String> getOAuthToken(String response){
        _logger.debug(response);
        HashMap<String, String> settings = new HashMap<String, String>();
        String[] splitStep1 = response.split("&");
        for(String result : splitStep1){
            _logger.debug(result);
            String[] props = result.split("=");
            if(!settings.containsKey(props[0])){
                settings.put(props[0], props[1]);
            }
            //settings.putIfAbsent(props[0], props[1]);
        }
        return settings;
    }

    private String AuthorizeToken(CookieManager mng){
        OAuthAccessor auth = ConstructOAuthEngine(false);

        //bbax: populated by request_token call
        if(_configuration.AccessToken == null){
            _logger.error("oauth token was null");
            return null;
        }

        try{
            RequestAbstraction request = _requestBuilder.builder()
                    .withUrl(auth.consumer.serviceProvider.userAuthorizationURL + "?oauth_token=" + _configuration.AccessToken)
                    .build();

            ClientAbstraction client = _httpClientBuilder.create();
            client.setCookieManager(mng);
            client.setFollowRedirects(false);
            ResponseAbstraction response = client.newCall(request).execute();
            if(response.isRedirect()){
                return response.getHeader("Location");
            }
            return null;

        }catch(Exception exc){
            _logger.error(exc.getMessage());
        }
        return null;
    }

    private OAuthAccessor ConstructOAuthEngine(boolean callbackActionRequired){
        OAuthServiceProvider provider = new OAuthServiceProvider(
                _configuration.ForeseeServicesUri+_configuration.RequestTokenAction,
                _configuration.ForeseeServicesUri+_configuration.UserAuthorizationAction,
                _configuration.ForeseeServicesUri+_configuration.AccessTokenAction
        );

        String callbackUrl = null;
        if(callbackActionRequired){
            callbackUrl = _configuration.ForeseeServicesUri+_configuration.CallbackAction;
            _logger.debug("setting callback url: "+callbackUrl);
        }

        OAuthConsumer consumer = new OAuthConsumer(callbackUrl, _configuration.ConsumerKey, _configuration.ConsumerSecret, provider);
        consumer.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.HMAC_SHA1);
        return new OAuthAccessor(consumer);
    }

    private HashMap<String, String> ParseGetTokenResponse(ResponseAbstraction response){
        HashMap<String, String> tokenDetails;
        try{
            tokenDetails = getOAuthToken(response.getBody().asString());

            String token = "";
            if(tokenDetails.containsKey("oauth_token")){
                token = tokenDetails.get("oauth_token");
            }
            //String token = tokenDetails.getOrDefault("oauth_token", null);
            if(token == null){
                _logger.error("Missing oauth_token object");
                return null;
            }

            String tokenSecret = "";//tokenDetails.getOrDefault("oauth_token_secret", null);
            if(tokenDetails.containsKey("oauth_token_secret")){
                tokenSecret = tokenDetails.get("oauth_token_secret");
            }
            if(tokenSecret == null){
                _logger.error("Missing oauth_token_secret object");
                return null;
            }
        }catch(Exception exc){
            _logger.error("Failed to parse the oauth gettoken response: " + exc.getMessage());
            return null;
        }
        return tokenDetails;
    }

    // bbax: borrowed from http://stackoverflow.com/questions/13592236/parse-the-uri-string-into-name-value-collection-in-java
    private Map<String, List<String>> splitQuery(URL url) throws UnsupportedEncodingException {
        return splitUrlLikeString(url.getQuery());
    }

    private Map<String, List<String>> splitUrlLikeString(String target){
        final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
        final String[] pairs = target.split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            try{
                final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
                if (!query_pairs.containsKey(key)) {
                    query_pairs.put(key, new LinkedList<String>());
                }
                final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
                query_pairs.get(key).add(value);
            }catch(Exception exc){
                _logger.error("failed to item during split: "+exc.getMessage());
            }
        }
        return query_pairs;
    }

    private String getVerifierFromRedirect(String fullRedirect){
        try{
            final String verifierVal = "oauth_verifier";
            Map<String, List<String>> parms = splitQuery(new URL(fullRedirect));
            if(!parms.containsKey(verifierVal) || parms.get(verifierVal).size() < 1){
                _logger.error("Response did not contain a verifier!");
                return null;
            }

            if(parms.get(verifierVal).size() > 1){
                _logger.warn("multiple oauth verifiers are present!");
            }

            return parms.get(verifierVal).get(0);
        }
        catch(Exception exc){
            _logger.error("Failed to find a verifier: "+exc.getMessage());
            return null;
        }
    }

    private Map<String, List<String>> RequestAccessToken(CookieManager mng, String verifier){
        String authHeader = null;
        OAuthAccessor auth = null;
        try{
            auth = ConstructOAuthEngine(false);
            auth.accessToken = _configuration.AccessToken;
            auth.tokenSecret = URLDecoder.decode(_configuration.AccessSecret, OAuth.ENCODING);
            auth.setProperty("oauth_verifier", verifier);
            authHeader = GetOAuthHeader(auth, OAuthMessage.GET, auth.consumer.serviceProvider.accessTokenURL);
            _logger.debug("header: " + authHeader);
            if(authHeader == null){
                _logger.error("Invalid authentication headers returned " + authHeader);
                return null;
            }
        }catch(Exception exc){
            _logger.error("GetToken failed to retrieve own authorization header: " + exc.getMessage());
            return null;
        }

        try{
            RequestAbstraction request = _requestBuilder.builder()
                    .withUrl(auth.consumer.serviceProvider.accessTokenURL)
                    .withHeader("Authorization", authHeader).build();

            ClientAbstraction client = _httpClientBuilder.create();
            client.setCookieManager(mng);
            ResponseAbstraction response = client.newCall(request).execute();
            String responseBody = response.getBody().asString();
            _logger.debug(responseBody);
            return splitUrlLikeString(responseBody);
        }catch(Exception exc){
            _logger.error("Failed request access token: " + exc.getMessage());
            return null;
        }
    }

    public String getSignedUrl(String httpMethod, String url){
        OAuthAccessor auth = ConstructOAuthEngine(false);
        auth.accessToken = _configuration.AccessToken;
        auth.tokenSecret = _configuration.AccessSecret;
        return GetOAuthHeader(auth, httpMethod, url);
    }

    public boolean InitializeOAuth(){
        // bbax: very important... propagating the cookies from call to call has to be
        // managed or the jboss backend gets angry...
        CookieManager mng = new CookieManager();
        CookieHandler.setDefault(mng);
        mng.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        ResponseAbstraction authTokenResponse = GetTokenRequest(mng);
        if(authTokenResponse == null){
            _logger.debug("failed get token request");
            return false;
        }

        HashMap<String, String> getTokenResponse = ParseGetTokenResponse(authTokenResponse);
        if(getTokenResponse == null){
            return false;
        }

        // bbax: foresee divergence.. relies on no data, this isn't really an oAuth thing...
        if(!InvokeLogin(_configuration.Username, _configuration.Password, mng)){
            _logger.error("Failed invoke login end-point.. server error");
            return false;
        }

        String verifier = null;
        try {
            _configuration.AccessToken = getTokenResponse.get("oauth_token");
            _configuration.AccessSecret = getTokenResponse.get("oauth_token_secret");
            String authorizeResponse = AuthorizeToken(mng);
            if(authorizeResponse == null){
                return false;
            }
            _logger.debug(authorizeResponse);
            verifier = getVerifierFromRedirect(authorizeResponse);
            if(verifier==null){
                return false;
            }
        }catch(Exception exc){
            _logger.error(exc.getMessage());
            return false;
        }

        Map<String, List<String>> tokens = RequestAccessToken(mng, verifier);
        if(tokens == null ||
                !tokens.containsKey("oauth_token") || tokens.get("oauth_token").size() < 1 ||
                !tokens.containsKey("oauth_token_secret") || tokens.get("oauth_token_secret").size() < 1){
            return false;
        }

        if(tokens.get("oauth_token").size() > 1 ||
                tokens.get("oauth_token_secret").size() > 1){
            _logger.warn("Too many authorization tokens found.");
        }

        try{
            _configuration.AccessToken = tokens.get("oauth_token").get(0);
            _configuration.AccessSecret = tokens.get("oauth_token_secret").get(0);

            _logger.debug("Final token: " + _configuration.AccessToken);
            _logger.debug("Final secret: " + _configuration.AccessSecret);
        }
        catch(Exception exc){
            _logger.error("Failed to process tokens: " + exc.getMessage());
            return false;
        }
        return true;
    }
}
