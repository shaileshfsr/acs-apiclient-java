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

import com.foresee.interfaces.http.ResponseAbstraction;
import com.foresee.interfaces.http.ResponseBodyAbstraction;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by bradley.bax on 10/29/2015.
 */
public class ResponseImp extends ResponseAbstraction<ResponseImp> {
    private ResponseBodyAbstraction _body;
    private int _responseStatusCode;
    private Map<String, List<String>> _headers;

    public ResponseImp(HttpURLConnection connection) throws java.io.IOException{
        _responseStatusCode = connection.getResponseCode();
        _body = loadBody(connection);
        _headers = connection.getHeaderFields();
    }
    @Override
    public boolean isRedirect() {
        if(_responseStatusCode >=300 && _responseStatusCode <=399){
            return true;
        }
        return false;
    }

    @Override
    public String getHeader(String key) throws IllegalStateException {
        if(_headers != null && _headers.containsKey(key)) {
            if(_headers.get(key).size() != 1){
                throw new IllegalStateException("Header should contain a single value [" + key + "]");
            }
            return _headers.get(key).get(0);
        }

        throw new IllegalStateException("Null pointers in header... [" + key + "]");
    }

    private ResponseBodyAbstraction loadBody(HttpURLConnection connection) {
        try {
            String loadString = "";
            if(connection.getResponseCode() < 200 || connection.getResponseCode() > 299){
                InputStream inputStream = connection.getErrorStream();
                if(inputStream == null){
                    return null;
                }
                Scanner scan = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
                loadString = scan.next();
                inputStream.close();
                scan.close();
            }else {
                InputStream inputStream = connection.getInputStream();
                if(inputStream == null){
                    return null;
                }
                Scanner scan = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
                loadString = scan.next();
                inputStream.close();
                scan.close();
            }

            return new ResponseBodyImp(loadString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseBodyAbstraction getBody(){
        return _body;
    }

    @Override
    public ResponseImp reference() {
        return this;
    }

    @Override
    public int responseCode() {
        return _responseStatusCode;
    }
}
