package net.kamradtfamily;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import static org.junit.Assert.*;

/**
 * Hello world!
 *
 */
public class Steps
{

    public static class a_new_purchase_order implements Function<Map<String, Object>, Map<String, Object>> {
        @Override
        public Map<String, Object> apply(Map<String, Object> map) {
            Map<String, Object> ret = new HashMap<>(map);
            ret.put("po", "{\"price\":1234.56,\"type\":\"Truck\"}");
            System.out.println("created random purchase order " + ret.get("po"));
            return ret;
        }
    }
    public static class you_post_the_new_purchase_order implements Function<Map<String, Object>, Map<String, Object>> {
        @Override
        public Map<String, Object> apply(Map<String, Object> map) {
            Map<String, Object> ret = new HashMap<>(map);
            HttpClient client = HttpClient.newBuilder()
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/po"))
                    .timeout(Duration.ofSeconds(20))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString((String)map.get("po")))
                    .build();
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                ret.put("id", response.body());
                System.out.println("got purchase order id " + ret.get("id"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ret;
        }
    }
    public static class you_should_get_an_id implements Function<Map<String, Object>, Map<String, Object>> {
        @Override
        public Map<String, Object> apply(Map<String, Object> map) {
            assertTrue(map.containsKey("id"));
            return map;
        }
    }
    public static class you_should_be_able_to_get_the_purchase_order_by_id implements Function<Map<String, Object>, Map<String, Object>> {
        @Override
        public Map<String, Object> apply(Map<String, Object> map) {
            Map<String, Object> ret = new HashMap<>(map);
            HttpClient client = HttpClient.newBuilder()
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/po/"+map.get("id")))
                    .timeout(Duration.ofSeconds(20))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                ret.put("newPo", response.body());
                System.out.println("got purchase order " + ret.get("newPo"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ret;
        }
    }
    public static class it_should_be_the_same_as_the_original_purchase_order implements Function<Map<String, Object>, Map<String, Object>> {
        @Override
        public Map<String, Object> apply(Map<String, Object> map) {
            assertEquals(map.get("po"),map.get("newPo"));
            System.out.println("matches!");
            return map;
        }
    }
}
