package com.laioffer.job.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laioffer.job.entity.Item;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GitHubClient {
    private static final String URL_TEMPLATE = "https://jobs.github.com/positions.json?description=%s&lat=%s&long=%s";
    private static final String DEAFAULT_KEYWORD = "developer";

    //public String search(double lat, double lon, String keyword) {
    public List<Item> search(double lat, double lon, String keyword) {
        if (keyword == null) {
            keyword = DEAFAULT_KEYWORD;
        }

        // "hello world" => "hello%20word"
        // 把空格替换为%20
        try {
            keyword = URLEncoder.encode(keyword, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = String.format(URL_TEMPLATE, keyword, lat, lon);

        CloseableHttpClient httpClient = HttpClients.createDefault();

        //create a custom response handler
        //ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
        ResponseHandler<List<Item>> responseHandler = new ResponseHandler<List<Item>>() {
            @Override
            public List<Item> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                if (response.getStatusLine().getStatusCode() != 200) {
                    //return "";
                    return Collections.emptyList();
                }
                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    //return "";
                    return Collections.emptyList();
                }
                //return EntityUtils.toString(entity);
                ObjectMapper mapper = new ObjectMapper();
                return Arrays.asList(mapper.readValue(entity.getContent(), Item[].class));
            }
        };

        try {
            return httpClient.execute(new HttpGet(url), responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //return "";
        return Collections.emptyList();
    }
}
