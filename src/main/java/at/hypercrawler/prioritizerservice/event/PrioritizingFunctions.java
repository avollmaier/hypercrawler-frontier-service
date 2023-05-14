package at.hypercrawler.prioritizerservice.event;

import at.hypercrawler.prioritizerservice.domain.service.PrioritizerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@Slf4j
@Configuration
public class PrioritizingFunctions {

    private final PrioritizerService prioritizerService;

    public PrioritizingFunctions(PrioritizerService prioritizerService) {
        this.prioritizerService = prioritizerService;
    }

    @Bean
    public Function<Flux<AddressSuppliedMessage>, Flux<Message<AddressPrioritizedMessage>>> prioritize() {
        return addressSupplyMessageFlux -> addressSupplyMessageFlux.map(addressSuppliedMessage -> {
            log.info("Prioritizing address {}", addressSuppliedMessage.address());

            final int priority = prioritizerService.evaluatePriority(addressSuppliedMessage.address());
            return MessageBuilder.withPayload(new AddressPrioritizedMessage(
                    addressSuppliedMessage.crawlerId(),
                    addressSuppliedMessage.address()
            )).setHeader("priority", priority).build();
        });
    }


}
