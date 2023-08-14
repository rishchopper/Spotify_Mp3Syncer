package main_code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;
import java.awt.Desktop;

/*
    1) https://developer.spotify.com/dashboard/applications
*/

public class SpotifyAuth {
    static final String CLIENT_ID = "3a1996000d4e4f0ea9e91055826878e4";
    static final String CLIENT_SECRET = "1cb5371b398d4cdfa07e342485d925f1";
    static final String REDIRECT_URI = "https://www.practar.com/";
    static final String REDIRECT_URI_ENCODED = "https%3A%2F%2Fwww.practar.com%2F";

    public static void main(String[] args){
        final String url = "https://accounts.spotify.com/authorize?client_id=" + CLIENT_ID + "&scopes=playlist-read-private%20user-library-read&response_type=code&redirect_uri=" + REDIRECT_URI_ENCODED;
        URL auth_code_url = null;;
        try {
            auth_code_url = new URL(url);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(openWebpage(auth_code_url)){
            System.out.println("URL opened in browser");
        }else {
            System.out.println("openWebpage failed");
        }
    }

    public static boolean openWebpage(URL url) {
        try {
            return openWebpage(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
