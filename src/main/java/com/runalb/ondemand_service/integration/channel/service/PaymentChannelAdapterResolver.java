package com.runalb.ondemand_service.integration.channel.service;

import com.runalb.ondemand_service.integration.channel.adapter.PaymentChannelAdapter;
import com.runalb.ondemand_service.payment.enums.PaymentChannelNameEnum;

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
