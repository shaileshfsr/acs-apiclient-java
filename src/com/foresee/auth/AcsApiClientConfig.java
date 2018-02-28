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

/**
 * Created by bradley.bax on 10/8/2015.
 */
public class AcsApiClientConfig {
    /* Consumer Credentials */
    public String ConsumerKey;
    public String ConsumerSecret;

    /* Legacy Foresee Services URI - To be retired*/
    public String ForeseeServicesUri = "https://portal2.foreseeresults.com/services/";

    /* Auth URI: For the purpose of this library the services URI is still the above.
    Only the authentication URI is changing */
    public String ForeseeAuthServiceUri = "https://services-edge.foresee.com/";

    public String Username;
    public String Password;

    public String AccessToken = null;
    public String AccessSecret = null;

    /* URI for the Login Call */
    public String AccessLogin = "access";

    /**
     * Default constructor that initializes the services URI and auth URI
     * @param consumerKey The consumer key
     * @param consumerSecret The consumer secret
     * @param username The username of the user
     * @param password The password associated with the user
     */
    @Deprecated
    public AcsApiClientConfig(String consumerKey, String consumerSecret,
                              String username, String password){
        ConsumerKey = consumerKey;
        ConsumerSecret = consumerSecret;
        Username = username;
        Password = password;
    }

    /**
     * Additional constructor that accepts the
     * @param consumerKey The consumer key
     * @param consumerSecret The consumer secret
     * @param username The username of the user
     * @param password The password associated with the user
     * @param foreseeAuthServiceUri
     * @param servicesUri
     */
    public AcsApiClientConfig(String consumerKey, String consumerSecret,
                              String username, String password, String foreseeAuthServiceUri, String servicesUri){
        this(consumerKey, consumerSecret, username, password);
        ForeseeAuthServiceUri = foreseeAuthServiceUri;
        ForeseeServicesUri = servicesUri;
    }
}
