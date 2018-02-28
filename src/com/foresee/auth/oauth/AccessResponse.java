package com.foresee.auth.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessResponse {
    @JsonProperty(value = "token")
    public String token;

    @JsonProperty(value = "secret")
    public String secret;

    public AccessResponse(){};

}
