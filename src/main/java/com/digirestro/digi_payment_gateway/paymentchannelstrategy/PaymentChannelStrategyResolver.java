package com.digirestro.digi_payment_gateway.paymentchannelstrategy;

import com.digirestro.digi_payment_gateway.paymentchannel.enums.PaymentChannelNameEnum;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PaymentChannelStrategyResolver {

    private final List<PaymentChannelStrategy> paymentChannelStrategies;

    public PaymentChannelStrategyResolver(List<PaymentChannelStrategy> paymentChannelStrategies) {
        this.paymentChannelStrategies = paymentChannelStrategies;
    }

    public PaymentChannelStrategy requireByChannelName(PaymentChannelNameEnum channelName) {
        return paymentChannelStrategies.stream()
                .filter(strategy -> Objects.equals(strategy.getChannelName(), channelName))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No strategy registered for channel: " + channelName));
    }
}
