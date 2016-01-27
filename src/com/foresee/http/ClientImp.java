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

import com.foresee.interfaces.http.ClientAbstraction;
import com.foresee.interfaces.http.ProxyAbstraction;
import com.foresee.interfaces.http.RequestAbstraction;
import com.foresee.interfaces.http.ResponseAbstraction;

import java.io.OutputStream;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bradley.bax on 10/29/2015.
 */
public class ClientImp extends ClientAbstraction<ClientImp> {
    private RequestImp _stagedRequest = null;
    private ProxyImp _stagedProxy = null;
    private static Object _lock = new Object();
    @Override
    public void setCookieManager(CookieManager mng) {
        CookieHandler.setDefault(mng);
    }

    @Override
    public void setFollowRedirects(boolean bFollowRedirects) {
        HttpURLConnection.setFollowRedirects(bFollowRedirects);
    }

    @Override
    public ClientAbstraction withProxy(ProxyAbstraction proxy) {
        _stagedProxy = (ProxyImp)proxy;
        return this;
    }

    @Override
    public ClientAbstraction newCall(RequestAbstraction request) {
        _stagedRequest = (RequestImp)request.reference();
        return this;
    }

    @Override
    public ResponseAbstraction execute() {
        synchronized (_lock) {
            try {
                URL request_url = _stagedRequest.getUrl();
                URLConnection connection = null;
                if (_stagedProxy != null) {
                    connection = request_url.openConnection(_stagedProxy.reference());
                } else {
                    connection = request_url.openConnection();
                }

                HashMap<String, String> extraHeaders = _stagedRequest.getAdditionalHeaders();
                for (Map.Entry<String, String> entry : extraHeaders.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();

                    connection.setRequestProperty(key, value);
                }

                connection.setRequestProperty("Accept-Charset", _stagedRequest.defaultCharSet);

                if (_stagedRequest.IsPost()) {
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", _stagedRequest.ContentType());

                    OutputStream output = connection.getOutputStream();
                    try {
                        output.write(_stagedRequest.GetBodyBytes());
                    } catch (Exception exc) {
                        return null;
                    } finally {
                        output.flush();
                        output.close();
                    }
                }

                ResponseImp resultForReturn = new ResponseImp((HttpURLConnection)connection);
                return resultForReturn;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public ClientImp reference() {
        return this;
    }
}
