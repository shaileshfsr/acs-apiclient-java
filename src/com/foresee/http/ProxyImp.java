package com.foresee.http;

import com.foresee.interfaces.http.ProxyAbstraction;

import java.net.Proxy;

/**
 * Created by Bradley.Bax on 11/12/2015.
 */
public class ProxyImp extends ProxyAbstraction<Proxy> {
    private Proxy _proxyValue;
    public ProxyImp(Proxy proxyValue){
        _proxyValue = proxyValue;
    }
    @Override
    public Proxy reference() {
        return _proxyValue;
    }
}
