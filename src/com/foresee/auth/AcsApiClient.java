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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private static Object _lock = new Object();

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


    private ResponseAbstraction Authenticate(){
        String loginUrl = _configuration.ForeseeAuthServiceUri + _configuration.AccessLogin;

        AccessRequest accessRequest = new AccessRequest(_configuration.ConsumerKey, _configuration.ConsumerSecret,
                _configuration.Username, _configuration.Password);
        PostBodyAbstraction body = _postBodyBuilder.create("application/json", accessRequest.toJson());

        try{
            RequestAbstraction request = _requestBuilder.builder()
                    .withUrl(loginUrl)
                    .withPostBody(body)
                    .build();

            ClientAbstraction client = _httpClientBuilder.create();
            client.setFollowRedirects(false);
            ResponseAbstraction response = client.newCall(request).execute();
            return response;

        }catch(Exception exc){
            _logger.error(exc.getMessage());
        }
        return null;
    }

    private OAuthAccessor ConstructOAuthEngine(boolean callbackActionRequired){
        OAuthServiceProvider provider = new OAuthServiceProvider(
                _configuration.ForeseeServicesUri+_configuration.AccessLogin
        );

        String callbackUrl = null;

        OAuthConsumer consumer = new OAuthConsumer(callbackUrl, _configuration.ConsumerKey, _configuration.ConsumerSecret, provider);
        consumer.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.HMAC_SHA1);
        return new OAuthAccessor(consumer);
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

    public String getSignedUrl(String httpMethod, String url){
        synchronized (_lock) {
            OAuthAccessor auth = ConstructOAuthEngine(false);
            auth.accessToken = _configuration.AccessToken;
            auth.tokenSecret = _configuration.AccessSecret;
            return GetOAuthHeader(auth, httpMethod, url);
        }
    }

    public boolean InitializeOAuth(){
        synchronized (_lock) {
            ResponseAbstraction authTokenResponse = Authenticate();
            if (authTokenResponse == null) {
                _logger.debug("failed get token request");
                return false;
            }

            final String accessTokenResponseString = authTokenResponse.getBody().asString();
            AccessResponse accessResponse = buildAccessResponsefromJson(accessTokenResponseString);

            try {
                _configuration.AccessToken = accessResponse.token;
                _configuration.AccessSecret = accessResponse.secret;

                _logger.debug("Final token: " + _configuration.AccessToken);
                _logger.debug("Final secret: " + _configuration.AccessSecret);
            } catch (Exception exc) {
                _logger.error("Failed to process tokens: " + exc.getMessage());
                return false;
            }

            return true;
        }
    }

    public AccessResponse buildAccessResponsefromJson(String json)  {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);

        try{
            AccessResponse accessResponse = mapper.readValue(json, AccessResponse.class);
            return accessResponse;
        }catch(Exception exc){
            return null;
        }
    }
}
