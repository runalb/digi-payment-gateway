package com.digirestro.digi_payment_gateway.paymentchannelstrategy.resolver;

import com.digirestro.digi_payment_gateway.paymentchannel.enums.PaymentChannelNameEnum;
import com.digirestro.digi_payment_gateway.paymentchannelstrategy.interfaces.PaymentChannelStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PaymentChannelStrategyResolver {

    private final Map<PaymentChannelNameEnum, PaymentChannelStrategy> strategyByChannel;

    public PaymentChannelStrategyResolver(List<PaymentChannelStrategy> paymentChannelStrategies) {
        Map<PaymentChannelNameEnum, PaymentChannelStrategy> strategies = new HashMap<>();
        for (PaymentChannelStrategy strategy : paymentChannelStrategies) {
            PaymentChannelNameEnum channelName = strategy.getChannelName();
            if (strategies.containsKey(channelName)) {
                throw new IllegalStateException(
                        "Duplicate PaymentChannelStrategy registered for channel: " + channelName);
            }
            strategies.put(channelName, strategy);
        }
        this.strategyByChannel = Map.copyOf(strategies);
    }

    public PaymentChannelStrategy getRequiredStrategy(PaymentChannelNameEnum channelName) {
        PaymentChannelStrategy strategy = strategyByChannel.get(channelName);
        if (strategy == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No strategy registered for channel: " + channelName);
        }
        return strategy;
    }
}
