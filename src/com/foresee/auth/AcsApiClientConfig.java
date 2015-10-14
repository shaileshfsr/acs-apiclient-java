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
    public String ConsumerKey;
    public String ConsumerSecret;
    public String ForeseeServicesUri = "https://portal2.foreseeresults.com/services/";

    public String Username;
    public String Password;

    public String AccessToken = null;
    public String AccessSecret = null;

    //"https://portal2.foreseeresults.com/services/client"

    // bbax: override these at your own risk.  Im pretty sure they don't change for Foresee...
    public String RequestTokenAction = "oauth/request_token";
    public String LoginAction = "login";
    public String UserAuthorizationAction = "oauth/user_authorization";
    public String AccessTokenAction = "oauth/access_token";
    public String CallbackAction = "client";

    public AcsApiClientConfig(String consumerKey, String consumerSecret, String username, String password){
        ConsumerKey = consumerKey;
        ConsumerSecret = consumerSecret;
        Username = username;
        Password = password;
    }
}
