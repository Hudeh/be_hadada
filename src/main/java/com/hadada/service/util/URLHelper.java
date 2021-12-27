package com.hadada.service.util;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URI;

public class URLHelper {

    public static boolean isUrlValid(String url) {
        try {
            URI obj = new URI(url);
            obj.toURL();
            if(!obj.getSchemeSpecificPart().startsWith("//")){
                return false;
            }
            return true;
        } catch (MalformedURLException e) {
            return false;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
