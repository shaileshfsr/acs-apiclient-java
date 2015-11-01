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
import java.util.Scanner;

/**
 * Created by bradley.bax on 10/29/2015.
 */
public class ResponseImp extends ResponseAbstraction<ResponseImp> {
    private HttpURLConnection _connection;

    public ResponseImp(HttpURLConnection connection){
        _connection = connection;
    }
    @Override
    public boolean isRedirect() {
        try {
            int response = _connection.getResponseCode();
            if(response >=300 && response <=399){
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getHeader(String key) {
        return _connection.getHeaderField(key);
    }

    @Override
    public ResponseBodyAbstraction getBody() {
        try {
            if(_connection.getResponseCode() != 200){
                return null;
            }

            InputStream inputStream = _connection.getInputStream();

            Scanner scan = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
            String loadString = scan.next();

            inputStream.close();
            scan.close();

            return new ResponseBodyImp(loadString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseImp reference() {
        return this;
    }

    @Override
    public int responseCode() {
        try {
            return _connection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
