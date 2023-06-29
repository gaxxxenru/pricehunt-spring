package ru.pricehunt.pleerru.parser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import ru.pricehunt.pleerru.model.Category;
import ru.pricehunt.pleerru.model.Product;
import ru.pricehunt.pleerru.service.CategoryService;
import ru.pricehunt.pleerru.service.ProductService;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductUrlParser {
    private final CategoryService categoryService;
    private final ProductService productService;
    private final ProxyParser proxyParser;

    public void start(int threadCount) {
        List<Category> categories = categoryService.findAll();
        int categoriesCount = categories.size();
        int categoriesPerThread = categoriesCount / threadCount;
        int remainingCategories = categoriesCount % threadCount;

        List<Future<?>> futures = new ArrayList<>();

        try (ExecutorService executorService = Executors.newFixedThreadPool(threadCount)) {
            for (int i = 0; i < threadCount; i++) {
                int start = i * categoriesPerThread + Math.min(i, remainingCategories);
                int end = start + categoriesPerThread + (i < remainingCategories ? 1 : 0);
                List<Category> categoriesThread = categories.subList(start, end);
                log.info("Запускаю поток для проверки категорий: {}", categoriesThread);
                ProductUrlsRunnable task = new ProductUrlsRunnable(categoriesThread);
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
    private class ProductUrlsRunnable implements Runnable {
        private final List<Category> categoriesListThread;

        @Override
        public void run() {
            categoriesListThread.forEach((category) -> {
                try {
                    getProductsFromCategory(category);
                } catch (Exception e) {
                    log.error("Error while parsing products from category: " + category.getSlug(), e);
                }
            });
        }
        private void getProductsFromCategory(Category category) {
            Proxy currentProxy = proxyParser.getProxiesQueue().poll();
            int currentPage = 1;
            int lastPage = currentPage + 1;

            while (currentPage <= lastPage) {
                try {
                    String pageUrl = category.getUrl() + currentPage + ".html";
                    log.info("ParseProductsSummary: " + pageUrl + " " + category.getSlug());
                    Connection.Response response = Jsoup.connect(pageUrl)
                        .headers(Map.of(
                            "accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,/;q=0.8,application/signed-exchange;v=b3;q=0.9",
                            "authority", "https://www.google.com/",
                            "accept-Language", "ru,en;q=0.9"))
                        .userAgent("Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Mobile Safari/537.36")
                        .referrer("https://www.google.com")
                        .timeout((int) (Math.random() * 15 + 15) * 1000)
                        .ignoreContentType(true)
                        .followRedirects(true)
                        .proxy(currentProxy)
                        .execute();
                    if (response.statusCode() != 200) {
                        log.info("ParseProductsSummary: status code " + response.statusCode() + " " + pageUrl + " " + category.getSlug());
                        currentProxy = proxyParser.getProxiesQueue().poll();
                        continue;
                    }
                    Document document = response.parse();
                    boolean isPageWithCaptcha = document.selectFirst(".product-block__title") != null;
                    boolean isPageLoaded = document.selectFirst(".header-phone") != null;
                    if (isPageWithCaptcha) {
                        log.info("ParseProductsSummary: captcha found " + pageUrl + " " + category.getSlug());
                        if (proxyParser.getProxiesQueue().isEmpty()) {
                            log.info("ParseProductsSummary: goodProxiesQueue is empty " + pageUrl + " " + category.getSlug());
                            break;
                        }
                        currentProxy = proxyParser.getProxiesQueue().poll();
                        continue;
                    }
                    if (!isPageLoaded) {
                        log.info("ParseProductsSummary: page not loaded " + pageUrl + " " + category.getSlug());
                        currentProxy = proxyParser.getProxiesQueue().poll();
                        log.info("ParseProductsSummary: proxy left " + proxyParser.getProxiesQueue().size());
                        continue;
                    }
                    Element activePageButtonElement = document.selectFirst("ul.inlineb a.categorypage-paging__link.active span");
                    Element lastPageButtonElement = document.select("ul.inlineb a.categorypage-paging__link span").last();
                    Elements productElements = document.select(".categorypage-products-info");
                    if (activePageButtonElement == null && productElements.isEmpty()) {
                        log.info("ParseProductsSummary: products not found for " + pageUrl + " " + category.getSlug());
                        break;
                    }
                    for (Element productElement : productElements) {
                        Element productNameElement = productElement.selectFirst(".categorypage-products__title");
                        Element productUrlElement = productElement.selectFirst(".categorypage-products__title[href]");
                        if (productUrlElement == null || productNameElement == null) {
                            log.info("ParseProductsSummary: productUrlElement or productPriceElement or productNameElement not found " + pageUrl + " " + category.getSlug());
                            continue;
                        }
                        String productName = productNameElement.text();
                        String slug = productName.toLowerCase().replaceAll("[^a-z0-9\\s]", "")
                            .replaceAll("\\s+", "-");



                        String productUrl = "https://pleer.ru/" + productUrlElement.getElementsByTag("a").attr("href");

                        if (productService.findBySlug(productUrl) != null) {
                            log.info("ParseProductsSummary: product already exists " + slug + " " + category.getSlug());
                            continue;
                        }
                        productService.save(Product.builder()
                            .slug(slug)
                            .url(productUrl)
                            .categorySlug(category.getSlug())
                            .build());
                        log.info("Product: " + slug + " has been parsed");
                    }

                    if (activePageButtonElement == null) {
                        log.info("ParseProductsSummary: category have only 1 page " + pageUrl + " " + category.getSlug());
                        break;
                    }

                    if (lastPageButtonElement == null) {
                        log.info("ParseProductsSummary: last page not found " + pageUrl + " " + category.getSlug());
                        break;
                    }

                    lastPage = Integer.parseInt(lastPageButtonElement.text());
                    currentPage = Integer.parseInt(activePageButtonElement.text());

                    if (currentPage == lastPage) {
                        log.info("ParseProductsSummary: " + category.getSlug() + " has been parsed");
                        break;
                    }
                    currentPage++;
                } catch (Exception e) {
                    log.error("ParseProductsSummary: " + e.getMessage());
                    if (proxyParser.getProxiesQueue().isEmpty()) {
                        log.info("ParseProductsSummary: goodProxiesQueue is empty " + category.getSlug());
                        break;
                    }
                    currentProxy = proxyParser.getProxiesQueue().poll();
                }
            }
            log.info("ParseProductsSummary: " + category.getSlug() + " has been parsed");
        }
    }
}
