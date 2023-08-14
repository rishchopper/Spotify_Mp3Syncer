package main_code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.json.*;

public class Sync_main {
    static final String CLIENT_ID = "3a1996000d4e4f0ea9e91055826878e4";
    static final String CLIENT_SECRET = "1cb5371b398d4cdfa07e342485d925f1";
    static final String REDIRECT_URI = "https://www.practar.com/";
    static final String REDIRECT_URI_ENCODED = "https%3A%2F%2Fwww.practar.com%2F";
    public static void main(String[] args) {
        if (args.length == 0) {
            return;
           }
        String recv_signin = args[0];
        String auth_code = recv_signin.substring(19);
        JSONObject auth_token_json;

        if(recv_signin.contains("?error=")){
            System.out.println("Auth error");
            return;
        }

        System.out.println(auth_code);
        System.out.println("Authentication Code Received! Now we try to get the Access Token...");

        try{
            auth_token_json = getAuthToken(auth_code);
            System.out.println("Access Token: " + auth_token_json.getString("access_token"));
            System.out.println("Token Type: " + auth_token_json.getString("token_type"));
            System.out.println("Expires in: " + auth_token_json.getInt("expires_in"));
            System.out.println("Refresh Token: " + auth_token_json.getString("refresh_token"));
            //System.out.println(getAuthToken(auth_code));
        }catch (Exception e){
            System.out.println("ERROR IN GETTING AUTH TOKEN... STACK TRACE:");
            e.printStackTrace();
        }


        try {
            Thread.sleep(120000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
/*
        System.out.println("Syncing Playlists...");
        //Download each playlist with DownloadPlaylist(playListUrl, playlistName, downloadLocation)
        DownloadPlaylist("https://open.spotify.com/playlist/5GJAfV68qj9L0yfyjqgAiJ", "Call Me Maybe");
        System.out.println("Playlists updated successfully");
*/

    static JSONObject getAuthToken(String authCode) throws IOException {
        String url_str = "https://accounts.spotify.com/api/token?grant_type=authorization_code&code=" + authCode + "&redirect_uri=" + REDIRECT_URI_ENCODED;
        URL url = new URL(url_str);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

        String data = "grant_type=client_credentials&client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "";

        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = http.getOutputStream();
        stream.write(out);

        BufferedReader Lines = new BufferedReader(new InputStreamReader(http.getInputStream()));
        String currentLine = Lines.readLine();
        StringBuilder response = new StringBuilder();
        while (currentLine != null) {
            response.append(currentLine).append("\n");
            currentLine = Lines.readLine();
        }

//        this.accessToken = String.valueOf(JsonParser.parseString(String.valueOf(response)).getAsJsonObject().getAsJsonObject("access_token"));
//        this.expiresIn = String.valueOf(JsonParser.parseString(String.valueOf(response)).getAsJsonObject().getAsJsonObject("expires_in"));
        
        
        JSONObject obj = new JSONObject(String.valueOf(response));

        http.disconnect();
        return obj;
    }

    static void DownloadPlaylist(String playlistUrl, String playlistName)
    {
        try{
            String s = null;
            Process p = Runtime.getRuntime().exec("D:/JavaProjects/Mp3Syncer/playlist_downloader.bat " + playlistUrl + " \"" + playlistName + "\"");
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((s = in.readLine()) != null){
                System.out.println(s);
            }
        }catch(IOException ie){
            ie.printStackTrace();
        }
    }
}
