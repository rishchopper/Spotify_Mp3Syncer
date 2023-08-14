package com.rishabh_chopra.spotify_syncer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

import org.json.*;

public class Sync_main {
    static final String CLIENT_ID = "3a1996000d4e4f0ea9e91055826878e4";
    static final String CLIENT_SECRET = "1cb5371b398d4cdfa07e342485d925f1";
    static final String REDIRECT_URI = "http://localhost:8080/";
    static final String REDIRECT_URI_ENCODED = "http%3A%2F%2Flocalhost%3A8080%2F";

    static String auth_code;
    static String user_id = "gtbq0x5hcdf8li23meazpl0eg";
    static String token;
    static JSONObject access_token_json;

    static String contentToAppend = "@echo off\n" +
            "echo Download Location: D:\\Music\\Spotify_Synced\n" +
            "\n" +
            "cd /d \"D:\\Music\\Spotify_Synced\"\n" +
            "\n";

    public static void main(String[] args) {
        JSONArray liked_songs;
        JSONArray playlists;
        ArrayList<String> liked_songs_array = new ArrayList<>();

        AuthCodeServer authCodeServer = new AuthCodeServer();
        SpotifyAuth spotifyAuth = new SpotifyAuth();
        spotifyAuth.main();

        auth_code = authCodeServer.getAuth_code();

        System.out.println("Authentication Code: " + auth_code);
        System.out.println("Authentication Code Received! Now we try to get the Access Token...");

        try{
            access_token_json = getAccessToken(auth_code);
            token = "Bearer " + access_token_json.getString("access_token");
            System.out.println("Access Token: " + access_token_json.getString("access_token"));
            System.out.println("Token Type: " + access_token_json.getString("token_type"));
            System.out.println("Expires in: " + access_token_json.getInt("expires_in"));
            //System.out.println(getAuthToken(auth_code));
        }catch (Exception e){
            e.printStackTrace();
        }

        System.out.println("Clearing old playlists...");
        try{
            NukePlaylists();
        }catch (Exception e){
            e.printStackTrace();
        }

        System.out.println("Getting Playlists...");
        try {
            JSONObject playlists_list = getPlaylists();
            //System.out.println(playlists_list.toString());
            playlists = playlists_list.getJSONArray("items");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        System.out.println("Downloading " + playlists.length() + "  Playlists...");

        for (int i = 0; i< playlists.length(); i++){
            try {
                /*while (t - System.currentTimeMillis()/1000 <= 300){

                }
                if (t-System.currentTimeMillis()/1000 <= 300){
                    System.out.println("ERROR: SYSTEM TIMEOUT REQUEST INCOMPLETE");
                }*/

                JSONObject playlist = playlists.getJSONObject(i);
                JSONObject external_links = playlist.getJSONObject("external_urls");
                String playlist_url = external_links.getString("spotify");
                String playlist_name = playlist.getString("name");
                System.out.println("Attempting to download playlist: " + playlist_name);
                contentToAppend += "echo Now Downloading: " + playlist_name.trim() + "\n";
                contentToAppend += "spotdl " + playlist_url.trim() + " --m3u \"" + playlist_name.trim() + "\" --user-auth --auth-token " + token.substring(7) + "\n";
                contentToAppend += "echo =================================================\n";
                //playlistUrl.trim() + " \"" + playlistName.trim() + "\" " + token.substring(7)
                //DownloadPlaylist(playlist_url, playlist_name);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        writeBatchScript();


        System.out.println("Playlists Downloaded! Now Getting Liked Songs...");

        try {
            JSONObject liked_songs_object = getLikedSongs();
            liked_songs = liked_songs_object.getJSONArray("items");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Downloading Liked Songs...");
        for (int i = 0; i< liked_songs.length(); i++){
            try {
                JSONObject current_song = (JSONObject) liked_songs.get(i);
                JSONObject song = current_song.getJSONObject("track");
                JSONObject external_urls = song.getJSONObject("external_urls");
                String song_url = external_urls.getString("spotify");
                liked_songs_array.add(i, song_url);
                DownloadLikedSong(song_url);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        System.out.println("Transferring Liked Songs...");
        TransferLikedSongs();
    }

    static JSONObject getPlaylists() throws IOException {
        String url_str = "https://api.spotify.com/v1/me/playlists?limit=50";
        URL url = new URL(url_str);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("GET");
        http.setRequestProperty("Authorization", token);
        http.setRequestProperty("Content-Type", "application/json");
        http.setDoInput(true);

        BufferedReader Lines = new BufferedReader(new InputStreamReader(http.getInputStream()));
        String currentLine = Lines.readLine();
        StringBuilder response = new StringBuilder();
        while (currentLine != null) {
            response.append(currentLine).append("\n");
            currentLine = Lines.readLine();
        }

        JSONObject obj = new JSONObject(String.valueOf(response));

        http.disconnect();
        return obj;
    }

    static JSONObject getLikedSongs() throws IOException{
        String url_str = "https://api.spotify.com/v1/me/tracks?market=IN&limit=50";
        URL url = new URL(url_str);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("GET");
        http.setRequestProperty("Authorization", token);
        http.setRequestProperty("Content-Type", "application/json");
        http.setDoInput(true);

        BufferedReader Lines = new BufferedReader(new InputStreamReader(http.getInputStream()));
        String currentLine = Lines.readLine();
        StringBuilder response = new StringBuilder();
        while (currentLine != null) {
            response.append(currentLine).append("\n");
            currentLine = Lines.readLine();
        }

        JSONObject obj = new JSONObject(String.valueOf(response));

        http.disconnect();
        return obj;
    }

    static void DownloadPlaylist(String playlistUrl, String playlistName)
    {
        try{
            String s = null;
            Process p = Runtime.getRuntime().exec("D:/Documents/IntelliJ_IDEA_Projects/Spotify_Playlist_Syncer/src/main/java/com/rishabh_chopra/spotify_syncer/playlist_downloader.bat " + playlistUrl.trim() + " \"" + playlistName.trim() + "\" " + token.substring(7));
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((s = in.readLine()) != null){
                System.out.println("Stuck in while loop: line 176, s = '"+ s + "'");
                System.out.println(s);
            }
        }catch(IOException ie){
            ie.printStackTrace();
        }
    }

    static JSONObject getAccessToken(String authCode) throws IOException {
        String url_str = "https://accounts.spotify.com/api/token";
        URL url = new URL(url_str);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        http.setRequestProperty("Authorization", "Basic M2ExOTk2MDAwZDRlNGYwZWE5ZTkxMDU1ODI2ODc4ZTQ6MWNiNTM3MWIzOThkNGNkZmEwN2UzNDI0ODVkOTI1ZjE=");

        String data = "grant_type=authorization_code&code=" + authCode + "&redirect_uri=" + REDIRECT_URI_ENCODED;

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

        JSONObject obj = new JSONObject(String.valueOf(response));

        http.disconnect();
        return obj;
    }

    static void DownloadLikedSong(String songUrl){
        try{
            String s = null;
            Process p = Runtime.getRuntime().exec("D:/Documents/IntelliJ_IDEA_Projects/Spotify_Playlist_Syncer/src/main/java/com/rishabh_chopra/spotify_syncer/liked_song_downloader.bat " + songUrl.trim());
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((s = in.readLine()) != null){
                System.out.println(s);
            }
        }catch(IOException ie){
            ie.printStackTrace();
        }
    }

    static void TransferLikedSongs(){
        try{
            String s = null;
            Process p = Runtime.getRuntime().exec("D:/Documents/IntelliJ_IDEA_Projects/Spotify_Playlist_Syncer/src/main/java/com/rishabh_chopra/spotify_syncer/liked_songs_transfer.bat");
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((s = in.readLine()) != null){
                System.out.println(s);
            }
        }catch(IOException ie){
            ie.printStackTrace();
        }
    }

    static void NukePlaylists(){
        try{
            String s = null;
            Process p = Runtime.getRuntime().exec("D:/Documents/IntelliJ_IDEA_Projects/Spotify_Playlist_Syncer/src/main/java/com/rishabh_chopra/spotify_syncer/playlists_nuke.bat");
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((s = in.readLine()) != null){
                System.out.println(s);
            }
        }catch(IOException ie){
            ie.printStackTrace();
        }
    }

    static void writeBatchScript(){
        String fileName = "download_songs_script.bat";
        // The following code will write the contents of contentToAppend to a .bat file by the name of fileName

        try {
            // Open the file
            FileWriter fileWriter = new FileWriter(fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // Write the content to the file
            bufferedWriter.write(contentToAppend);
            bufferedWriter.newLine(); // Add a new line after appending the content

            // Close the writers
            bufferedWriter.close();
            fileWriter.close();

            System.out.println("String appended to the file successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while appending to the file: " + e.getMessage());
        }
    }
}
