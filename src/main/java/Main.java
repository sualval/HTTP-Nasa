import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;

public class Main {
    private static final String url = "https://api.nasa.gov/planetary/apod?api_key=vXekMYoKLRpwDiyZB789FjDbgP8XxNhK9Xo93Rig";
    private static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        String urlFile;
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(5000)
                .setSocketTimeout(30000)
                .setRedirectsEnabled(false)
                .build()).build()) {
            HttpGet request = new HttpGet(url);
            request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                //  urlFile= mapper.readTree(response.getEntity().getContent().readAllBytes()).get("url").asText();
                Nasa nasa = mapper.readValue(response.getEntity().getContent().readAllBytes(), new TypeReference<>() {
                });
                urlFile = nasa.getUrl();
                //  urlFile = nasa.getHdurl();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            HttpGet request1 = new HttpGet(urlFile);
            BufferedInputStream inputStream = new BufferedInputStream(httpClient.execute(request1).getEntity().getContent());
            String fileName = urlFile.substring(urlFile.lastIndexOf("/") + 1);
            FileOutputStream fos = new FileOutputStream(fileName);
            int i;
            while ((i = inputStream.read()) != -1) {
                fos.write(i);
            }
            fos.close();
            inputStream.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
