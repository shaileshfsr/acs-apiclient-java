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

package com.foresee.http;

import com.foresee.interfaces.http.PostBodyAbstraction;
import com.foresee.interfaces.http.RequestAbstraction;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.util.HashMap;

/**
 * Created by bradley.bax on 10/29/2015.
 */
public class RequestImp extends RequestAbstraction<RequestImp> {
    private PostBodyAbstraction _postBody = null;
    private String _url;
    private HashMap<String, String> _headers = new HashMap<String, String>();
    //private boolean _isPost;

    final String defaultCharSet = "UTF-8";

    public URL getUrl() throws MalformedURLException {
        return new URL(_url);
    }

    public void setUrl(String url){
        _url = url;
    }

    public void addHeader(String key, String value) throws InvalidKeyException {
        if(_headers.containsKey(key)){
            throw new InvalidKeyException("Already contains the key requested");
        }
        _headers.put(key, value);
    }

    public HashMap<String, String> getAdditionalHeaders(){
        return _headers;
    }

    public String ContentType(){
        return ((PostBodyImp)_postBody.reference()).RawContentType()+ ";charset="+defaultCharSet;
    }

    public void setPostBody(PostBodyAbstraction postBody){
        _postBody = postBody;
    }

    public byte[] GetBodyBytes(){
        return ((PostBodyImp)_postBody.reference()).RawBody().getBytes(Charset.forName(defaultCharSet));
    }

    public boolean IsPost(){
        return _postBody != null;
    }

    @Override
    public RequestImp reference() {
        return this;
    }
}
