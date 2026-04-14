package com.digirestro.digi_payment_gateway.integration.channel.service;

import com.digirestro.digi_payment_gateway.enums.PaymentChannelNameEnum;
import com.digirestro.digi_payment_gateway.integration.channel.adapter.PaymentChannelAdapter;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PaymentChannelAdapterResolver {

    private final List<PaymentChannelAdapter> adapters;

    public PaymentChannelAdapterResolver(List<PaymentChannelAdapter> adapters) {
        this.adapters = adapters;
    }

    public PaymentChannelAdapter requireByChannelName(PaymentChannelNameEnum channelName) {
        return adapters.stream()
                .filter(a -> Objects.equals(a.getChannel().getName(), channelName))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No adapter registered for channel: " + channelName));
    }
}
