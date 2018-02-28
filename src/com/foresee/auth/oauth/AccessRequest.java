package com.foresee.auth.oauth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;

public class AccessRequest {
    @JsonProperty(value = "consumerKey")
    public String consumerKey;

    @JsonProperty(value = "consumerSecret")
    public String consumerSecret;

    @JsonProperty(value = "username")
    public String username;

    @JsonProperty(value = "password")
    public String password;

    public AccessRequest() {

    }

    public AccessRequest(String consumerKey, String consumerSecret, String username, String password) {
        super();
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.username = username;
        this.password = password;
    }

    public String toJson()  {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try{
            mapper.writeValue(baos, this);
        }catch(Exception exc){
            return null;
        }
        return baos.toString();
    }
}
