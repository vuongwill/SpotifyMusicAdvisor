package advisor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class Auth {
    private String code = "";
    private String clientId = "2e85af4779124ccdbe87bf078a323a84";
    private String clientSecret = "ded4c59363604d1db31b6e654340ed06";
    private String redirectUri = "http://localhost:8888";
    public String server = "https://accounts.spotify.com";
    public String apiUrl = "https://api.spotify.com";
    public String token = "";

    public void requestCode() {
        System.out.println("use this link to request the access code:");
        System.out.println(server + "/authorize?client_id=" + clientId +
                "&redirect_uri=" + redirectUri + "&response_type=code");
        try {
            HttpServer servers = HttpServer.create();
            servers.bind(new InetSocketAddress(8888), 0);
            servers.start();
            servers.createContext("/",
                new HttpHandler() {
                    public void handle(HttpExchange exchange) throws IOException {
                        String query = exchange.getRequestURI().getQuery();
                        String request = "";
                        if (query.contains("code")) {
                            code = query.substring(5);
                            request = "Code received. Go back to application.";
                        } else {
                            request = "Error";
                        }
                        exchange.sendResponseHeaders(200, request.length());
                        exchange.getResponseBody().write(request.getBytes());
                        exchange.getResponseBody().close();

                    }
                }
            );
            System.out.println("waiting for code...");
            while (code.isBlank()) {
                Thread.sleep(10);
            }
            servers.stop(1);
            System.out.println("code received");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public void requestToken() {
        System.out.println("making http request for access_token...");
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create(server + "/api/token"))
                .POST(HttpRequest.BodyPublishers.ofString(
                        "grant_type=authorization_code" +
                        "&code=" + code +
                        "&redirect_uri=" + redirectUri +
                        "&client_id=" + clientId +
                        "&client_secret=" + clientSecret))
                .build();
        //System.out.println("response:");
        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //System.out.println(response.body());
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            token = jsonObject.get("access_token").getAsString();
            Main.auth = true;
            System.out.println("Success!\n");
            //System.out.println(token);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public List<SongInfo> listFeatured() {
        List<SongInfo> list = new LinkedList<>();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .uri(URI.create(apiUrl + "/v1/browse/featured-playlists"))
                    .GET().build();
            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //System.out.println(response.body());
            JsonObject jo = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject categories = jo.getAsJsonObject("playlists");
            for (JsonElement i : categories.getAsJsonArray("items")) {
                SongInfo song = new SongInfo();
                song.setName(i.getAsJsonObject().get("name")
                        .toString().replaceAll("\"", ""));
                song.setLink(i.getAsJsonObject().get("external_urls")
                        .getAsJsonObject().get("spotify")
                        .toString().replaceAll("\"", ""));
                list.add(song);
            }
//            StringBuilder str = new StringBuilder();
//            for (SongInfo i : list) {
//                str.append(i.getAlbum()).append("\n")
//                        .append(i.getLink()).append("\n\n");
//            }
//            System.out.println(str);
            return list;
        } catch (Exception e) {
            System.out.println(e);
            return list;
        }
    }
    public List<SongInfo> listNew() {
        List<SongInfo> list = new LinkedList<>();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .uri(URI.create(apiUrl + "/v1/browse/new-releases"))
                    .GET().build();
            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject jo = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject categories = jo.getAsJsonObject("albums");
            //System.out.println(response.body());
            for (JsonElement i : categories.getAsJsonArray("items")) {
                SongInfo song = new SongInfo();
                song.setName(i.getAsJsonObject().get("name")
                        .toString().replaceAll("\"", ""));
                StringBuilder artist = new StringBuilder();
                for (JsonElement j : i.getAsJsonObject().getAsJsonArray("artists")) {
                    artist.append(j.getAsJsonObject().get("name") + ", ");
                }
                artist.deleteCharAt(artist.length() - 2);
                song.setArtist(artist.toString().trim().replaceAll("\"", ""));
//                song.setArtist(i.getAsJsonObject().getAsJsonArray("artists").get(0)
//                        .getAsJsonObject().get("name")
//                        .toString().replaceAll("\"", ""));
                song.setLink(i.getAsJsonObject().get("external_urls")
                        .getAsJsonObject().get("spotify")
                        .toString().replaceAll("\"", ""));
                list.add(song);
            }
//            StringBuilder str = new StringBuilder();
//            for (SongInfo i : list) {
//                str.append(i.getName()).append("\n")
//                        .append("[")
//                        .append(i.getArtist()).append("]\n")
//                        .append(i.getLink()).append("\n\n");
//            }
//            System.out.println(str);
            return list;
        } catch (Exception e) {
            System.out.println(e);
            return list;
        }
    }
    public List<SongInfo> listCategories() {
        List<SongInfo> list = new LinkedList<>();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .uri(URI.create(apiUrl + "/v1/browse/categories"))
                    .GET().build();
            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject jo = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject categories = jo.getAsJsonObject("categories");
            for (JsonElement i : categories.getAsJsonArray("items")) {
                SongInfo song = new SongInfo();
                song.setName(i.getAsJsonObject().get("name")
                        .toString().replaceAll("\"", ""));
                list.add(song);
            }
//            StringBuilder str = new StringBuilder();
//            for (SongInfo i : list) {
//                str.append(i.getCategory() + "\n");
//            }
//            System.out.println(str);
            return list;
        } catch (Exception e) {
            System.out.println(e);
            return list;
        }
    }
    public List<SongInfo> listPlaylists(String id) {
        List<SongInfo> list = new LinkedList<>();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .uri(URI.create(apiUrl
                            + "/v1/browse/categories/" + browseCategories(id)
                            + "/playlists"))
                    .GET().build();
            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.body().contains("error")) {
                JsonObject jo = JsonParser.parseString(response.body()).getAsJsonObject();
                JsonObject error = jo.getAsJsonObject("error");
                System.out.println(error.get("message").toString().replaceAll("\"", ""));
                return list;
            }
            //System.out.println(response.body());
            JsonObject jo = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject categories = jo.getAsJsonObject("playlists");
            for (JsonElement i : categories.getAsJsonArray("items")) {
                SongInfo song = new SongInfo();
                song.setName(i.getAsJsonObject().get("name")
                        .toString().replaceAll("\"", ""));
                song.setLink(i.getAsJsonObject().get("external_urls")
                        .getAsJsonObject().get("spotify")
                        .toString().replaceAll("\"", ""));
                list.add(song);
            }
//            StringBuilder str = new StringBuilder();
//            for (SongInfo i : list) {
//                str.append(i.getName() + "\n").append(i.getLink() + "\n").append("\n");
//            }
//            System.out.println(str);
            return list;
        } catch (Exception e) {
            System.out.println("Unknown category name.");
            return list;
        }
    }
    public String browseCategories(String string) {
        Map<String, String> map = new LinkedHashMap<>();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .uri(URI.create(apiUrl + "/v1/browse/categories"))
                    .GET().build();
            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject jo = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject categories = jo.getAsJsonObject("categories");
            String name = "";
            String id = "";
            for (JsonElement i : categories.getAsJsonArray("items")) {
                name = i.getAsJsonObject().get("name").toString().replaceAll("\"", "").toLowerCase();
                id = i.getAsJsonObject().get("id").toString().replaceAll("\"", "");
                map.put(name, id);
            }
            return map.get(string.toLowerCase());
        } catch (Exception e) {
            System.out.println(e);
            return "";
        }
    }
}
