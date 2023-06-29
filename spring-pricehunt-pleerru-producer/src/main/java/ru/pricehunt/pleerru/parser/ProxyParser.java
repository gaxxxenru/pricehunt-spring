package ru.pricehunt.pleerru.parser;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ProxyParser {
    @Getter
    private final ConcurrentLinkedQueue<Proxy> proxiesQueue = new ConcurrentLinkedQueue<>();
    @Getter
    private final ConcurrentLinkedQueue<Proxy> goodProxiesQueue = new ConcurrentLinkedQueue<>();

    private final String proxyHttpsUrl;
    private final String proxySocksUrl;
    private Proxy.Type proxyType = Proxy.Type.SOCKS;

    public ProxyParser(@Value("${proxy.https}") String proxyHttpsUrl, @Value("${proxy.socks}") String proxySocksUrl) {
        this.proxyHttpsUrl = proxyHttpsUrl;
        this.proxySocksUrl = proxySocksUrl;
        parse(proxySocksUrl, proxyType);
        log.info("Socks Proxies grabbed: " + getProxiesQueue().size());
        parse(proxyHttpsUrl, proxyType);
        log.info("Https Proxies grabbed: " + getProxiesQueue().size());
    }

    public void addGoodProxyToQueue(Proxy proxy) {
        goodProxiesQueue.add(proxy);
    }

    @Scheduled(fixedDelay = 1000 * 60)
    public void startParsing() {
        log.info("Proxies in queue: " + proxiesQueue.size());
        if (proxiesQueue.size() > 1500) {
            log.info("Proxies queue is full");
        } else {
            String proxyUrl = proxyType == Proxy.Type.SOCKS ? proxySocksUrl : proxyHttpsUrl;
            parse(proxyUrl, proxyType);
            log.info("Proxies in queue: " + proxiesQueue.size());
            proxyType = proxyType == Proxy.Type.SOCKS ? Proxy.Type.HTTP : Proxy.Type.SOCKS;
        }
    }



    public void parse(String proxyUrl, Proxy.Type type) {
        log.info("Parsing proxies from " + proxyUrl);
        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .build();

        Request request = new Request.Builder()
            .url(proxyUrl)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    try (BufferedReader reader = new BufferedReader(responseBody.charStream())) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            proxiesQueue.add(createProxyFromAddress(line, type));
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error while getting proxy", e);
        }
    }

    private java.net.Proxy createProxyFromAddress(String address, Proxy.Type type) {
        String[] addressParts = address.split(":");
        return new Proxy(type, new InetSocketAddress(addressParts[0], Integer.parseInt(addressParts[1])));
    }
}
