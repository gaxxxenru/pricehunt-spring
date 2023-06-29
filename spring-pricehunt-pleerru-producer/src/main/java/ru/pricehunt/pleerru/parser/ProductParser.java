package ru.pricehunt.pleerru.parser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import ru.pricehunt.pleerru.dto.CategoryDTO;
import ru.pricehunt.pleerru.dto.ProductDTO;
import ru.pricehunt.pleerru.dto.ProductFeatureDTO;
import ru.pricehunt.pleerru.dto.ProductImageDTO;
import ru.pricehunt.pleerru.model.Product;
import ru.pricehunt.pleerru.service.ProductService;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductParser {
    @Getter
    private final ProductService productService;
    private final ProxyParser proxyParser;


    public void start(int threadCount) {
        List<Product> products = productService.findProductsNotParsedToday();
        int productsCount = products.size();
        int productsPerThread = productsCount / threadCount;
        int remainingProducts = productsCount % threadCount;


        List<Future<?>> futures = new ArrayList<>();

        try (ExecutorService executorService = Executors.newFixedThreadPool(threadCount)) {
            for (int i = 0; i < threadCount; i++) {
                int start = i * productsPerThread + Math.min(i, remainingProducts);
                int end = start + productsPerThread + (i < remainingProducts ? 1 : 0);
                List<Product> productsThread = products.subList(start, end);
                log.info("Запускаю поток для получения товара: {}", productsThread);
                ProductDetailsRunnable task = new ProductDetailsRunnable(productsThread);
                Future<?> future = executorService.submit(task);
                futures.add(future);
            }

            for (Future<?> future : futures) {
                try {
                    future.get(30, TimeUnit.MINUTES);
                } catch (TimeoutException e) {
                    log.error("Поток завершен по таймауту");
                    executorService.shutdownNow();
                } catch (InterruptedException | ExecutionException e) {
                    log.error("Ошибка при выполнении потока", e);
                    Thread.currentThread().interrupt();
                }
            }
        }
        log.info("Потоки завершены");
    }

    @RequiredArgsConstructor
    private class ProductDetailsRunnable implements Runnable {
        private final List<Product> productsForThread;
        private Proxy currentProxy;

        @Override
        public void run() {
            log.info("ParseProductDetails: thread started");
            currentProxy = proxyParser.getProxiesQueue().poll();
            productsForThread.forEach((product) -> {
                try {
                    getProductsDetails(product);
                } catch (Exception e) {
                    log.error("Error", e);
                }
            });
            log.info("ParseProductDetails: thread finished");
        }

        private void getProductsDetails(Product product) {
            while (true) {
                try {
                    log.info("ParseProductDetails: " + product.getUrl());
                    Connection.Response response = Jsoup.connect(product.getUrl())
                        .headers(Map.of(
                            "accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,/;q=0.8,application/signed-exchange;v=b3;q=0.9",
                            "authority", "https://www.google.com/",
                            "accept-Language", "ru,en;q=0.9"))
                        .userAgent("Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Mobile Safari/537.36")
                        .referrer("https://www.google.com")
                        .timeout((int) (Math.random() * 60 + 15) * 1000)
                        .ignoreContentType(true)
                        .followRedirects(true)
                        .proxy(currentProxy)
                        .execute();
                    if (response.statusCode() != 200) {
                        log.info("ParseProductDetails: status code is not 200 " + product.getUrl());
                        if (!proxyParser.getGoodProxiesQueue().isEmpty()) {
                            currentProxy = proxyParser.getGoodProxiesQueue().poll();
                        } else {
                            currentProxy = proxyParser.getProxiesQueue().poll();
                        }
                        continue;
                    }
                    Document document = response.parse();
                    boolean isPageWithCaptcha = document.selectFirst(".g-recaptcha.flex.flex_center") != null;
                    boolean isPageLoaded = document.selectFirst("title") != null;
                    if (!isPageLoaded || isPageWithCaptcha) {
                        log.info("ParseProductDetails: page not loaded " + product.getUrl());
                        if (!proxyParser.getGoodProxiesQueue().isEmpty()) {
                            currentProxy = proxyParser.getGoodProxiesQueue().poll();
                        } else {
                            currentProxy = proxyParser.getProxiesQueue().poll();
                        }
                        continue;
                    }
                    Element nameElement = document.selectFirst(".product-block__title");
                    if (nameElement == null) {
                        log.info("ParseProductDetails: nameElement is null " + product.getUrl());
                        break;
                    }
                    String name = nameElement.text();
                    Elements imageElements = document.select("img[itemprop], img.mobi_prod_img");
                    if (imageElements.isEmpty()) {
                        log.info("ParseProductDetails: imageElement is null " + product.getUrl());
                        break;
                    }

                    if (name.length() > 255) {
                        log.info("ParseProductDetails: name is too long " + product.getUrl());
                        break;
                    }

                    Element priceElement = document.selectFirst(".categorypage-products__price.big_txt span");
                    if (priceElement == null) {
                        priceElement = document.selectFirst(".categorypage-products__noprice");
                        if (priceElement == null) {
                            log.info("ParseProductDetails: priceElement is null " + product.getUrl());
                            break;
                        }
                    }
                    float price = Float.parseFloat(priceElement.text().replaceAll("[^0-9]", ""));


                    List<ProductImageDTO> productImageDTOS = new ArrayList<>();
                    for (Element imageElement: imageElements) {
                        if (imageElement.attr("src").contains("prestatic.pleer.ru")) {
                            continue;
                        }
                        ProductImageDTO image = ProductImageDTO.builder()
                            .url(imageElement.attr("src").replace("//", "https://"))
                            .name(imageElement.attr("alt"))
                            .build();
                        productImageDTOS.add(image);
                    }


                    Element descriptionElement = document.selectFirst(".product-desc-text-main");
                    if (descriptionElement == null || descriptionElement.text().trim().isEmpty()) {
                        log.info("ParseProductDetails: descriptionElement is null or empty " + product.getUrl());
                        break;
                    }
                    String description = descriptionElement.html();
                    if (description.length() > 5000) {
                        log.info("ParseProductDetails: description is too long " + product.getUrl());
                        break;
                    }

                    Elements featuresElements = document.select(".product-teh-text-main li");
                    if (featuresElements.isEmpty()) {
                        log.info("ParseProductDetails: featuresElements is null " + product.getUrl());
                        break;
                    }
                    List<ProductFeatureDTO> features = new ArrayList<>();
                    for (Element featureElement : featuresElements) {
                        String[] parts = featureElement.text().split(":");
                        String nameFeature = parts[0].trim();
                        String valueFeature = parts[1].trim();
                        if (nameFeature.length() > 255 || valueFeature.length() > 255) {
                            log.info("ParseProductDetails: feature name or value is too long " + product.getUrl());
                            continue;
                        }
                        if (parts.length == 2) {
                            ProductFeatureDTO feature = ProductFeatureDTO.builder()
                                .name(nameFeature)
                                .value(valueFeature)
                                .build();
                            features.add(feature);
                        }
                    }
                        ProductDTO productDTODetails = ProductDTO.builder()
                            .slug(product.getSlug())
                            .category(CategoryDTO.builder().slug(product.getCategorySlug()).build())
                            .url(product.getUrl())
                            .description(description)
                            .features(features)
                            .images(productImageDTOS)
                            .name(name)
                            .price(price)
                            .build();
                        productService.sendProductToKafka(productDTODetails);
                        product.setLastParsingDate(new Date());
                        productService.save(product);
                        proxyParser.addGoodProxyToQueue(currentProxy);
                        log.info("ParseProductDetails: success parsed " + product.getUrl());
                        break;
                } catch (Exception e) {
                    log.error("ParseProductDetails: " + e.getMessage());
                    if (!proxyParser.getGoodProxiesQueue().isEmpty()) {
                        currentProxy = proxyParser.getGoodProxiesQueue().poll();
                    } else {
                        currentProxy = proxyParser.getProxiesQueue().poll();
                    }
                }
            }
        }
}
}
