package org.lutover.app;

import spark.utils.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpClient {

    private final String host;

    public HttpClient(String host) {
        this.host = host;
    }

    public Response get(String path) throws IOException {
        final URL url = new URL(host + path);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoInput(Boolean.TRUE);
        connection.connect();

        final String body = readResponseBody(connection);
        connection.disconnect();

        return Response.build(connection.getResponseCode(), body, "GET");
    }

    public Response post(String path, byte[] entity, Map<String, String> headers) throws IOException {
        final URL url = new URL(host + path);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Length", "" + entity.length);
        headers.forEach(connection::setRequestProperty);

        connection.setDoInput(Boolean.TRUE);
        connection.setDoOutput(Boolean.TRUE);
        connection.getOutputStream().write(entity);

        final String body = readResponseBody(connection);
        connection.disconnect();

        return Response.build(connection.getResponseCode(), body, "POST");
    }

    private String readResponseBody(HttpURLConnection connection) throws IOException {
        return IOUtils.toString(connection.getInputStream());
    }

    static class Response {

        final int status;

        final String body;

        final String method;

        private Response(int status, String body, String method) {
            this.status = status;
            this.body = body;
            this.method = method;
        }

        static Response build(int status, String body, String method) {
            return new Response(status, body, method);
        }
    }
}
