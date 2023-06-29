package ru.pricehunt.pleerru.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.pricehunt.pleerru.parser.ProxyParser;
import ru.pricehunt.pleerru.parser.ProductParser;
import ru.pricehunt.pleerru.parser.ProductUrlParser;

@Slf4j
@Component
@RequiredArgsConstructor
public class ParseTask {
    private final ProductUrlParser productUrlParser;
    private final ProductParser productParser;

    @EventListener(ApplicationReadyEvent.class)
    public void run() {
//        log.info("GrabAndCheckProxies started");
//        productUrlParser.start(9);
        log.info("ParseProductsSummary finished");
        productParser.start(300);
        log.info("ParseProductsDetails finished");
    }
}
