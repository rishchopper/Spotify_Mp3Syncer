package com.rishabh_chopra.spotify_syncer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;

public class AuthCodeServer {

    private static String headerData;
    private static String auth_code = "nuthin";

    public String getAuth_code() {
        new AuthCodeServer().startServer();
        System.out.println("auth code received, now sending back to Sync_main.java");
        return auth_code;
    }

    public void startServer() {

        try (ServerSocket serverSocket = new ServerSocket(8080)) {

            boolean isClosed = false;

            System.out.println("Server started: http://localhost:8080/");

            int count = 0;

            while (count < 1) {
                Socket socket = serverSocket.accept();

                try {
                    try (InputStream raw = socket.getInputStream()) { // ARM

                        //System.out.println("=================BEFORE STARTING READING HEADER =======================");

                        //System.out.println("Collecting data to string array...");

                        headerData = getHeaderToArray(raw);
                        //System.out.println(headerData);

                        if (headerData.substring(0, 11).equals("GET /?code=")){
                            auth_code = headerData.substring(11);
                        //    System.out.println(auth_code);
                            auth_code = auth_code.substring(0, (auth_code.length()-5));
                        }else if(headerData.contains("?error=")) {
                            auth_code = "ERROR";
                        }else{
                            auth_code = "tf? nether of those?!!??!";
                        }
                        //System.out.println(auth_code);
//
                        //System.out.println("+++++++++++++++++ AFTER ENDING READING HEADER +++++++++++++++++++++++");
                    }
                } catch (MalformedURLException ex) {
                    System.err.println(socket.getLocalAddress() + " is not a parseable URL");

                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }
                count++;
            }

        } catch (Exception ex) {
            System.out.println("error# " + ex.getMessage());
        }
    }

    public String getHeaderToArray(InputStream inputStream) {

        String headerTempData = "";

        // chain the InputStream to a Reader
        Reader reader = new InputStreamReader(inputStream);
        try {
            int c;
            int count = 0;
            while ((c = reader.read()) != -1) {
                if(count == 4){
                    return headerTempData;
                }
                //System.out.print((char) c);
                headerTempData += (char) c;
                if ((char)c == 'H' || (char)c == 'T' || (char)c == 'P'){
                    count++;
                }else{
                    count = 0;
                }

                if (headerTempData.contains("\r\n\r\n"))
                    break;
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        headerData = headerTempData;

        return headerTempData;
    }
}