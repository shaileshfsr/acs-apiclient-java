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
import com.foresee.interfaces.http.RequestBuilderAbstraction;

import java.security.InvalidKeyException;

/**
 * Created by bradley.bax on 10/29/2015.
 */
public class RequestBuilderImp extends RequestBuilderAbstraction {
    private RequestImp _stagedRequest = null;
    @Override
    public RequestBuilderAbstraction builder() {
        _stagedRequest = new RequestImp();
        return this;
    }

    @Override
    public RequestBuilderAbstraction withUrl(String url) {
        _stagedRequest.setUrl(url);
        return this;
    }

    @Override
    public RequestBuilderAbstraction withHeader(String key, String value) {
        try {
            _stagedRequest.addHeader(key, value);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public RequestBuilderAbstraction withPostBody(PostBodyAbstraction postBody) {
        _stagedRequest.setPostBody(postBody);
        return this;
    }

    @Override
    public RequestAbstraction build() {
        return _stagedRequest;
    }
}
