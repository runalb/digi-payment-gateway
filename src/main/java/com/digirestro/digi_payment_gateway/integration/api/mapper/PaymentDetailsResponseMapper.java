package com.digirestro.digi_payment_gateway.integration.api.mapper;

import com.digirestro.digi_payment_gateway.integration.api.dto.PaymentDetailsResponse;
import com.digirestro.digi_payment_gateway.payment.entity.PaymentEntity;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PaymentDetailsResponseMapper {

    public PaymentDetailsResponse toResponse(PaymentEntity payment) {
        return new PaymentDetailsResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getMerchantReferenceId(),
                payment.getPaymentChannel().getName(),
                payment.getPaymentChannelTxnId(),
                payment.getCreatedDateTime(),
                payment.getUpdatedDateTime());
    }

    public List<PaymentDetailsResponse> toResponseList(List<PaymentEntity> payments) {
        return payments.stream().map(this::toResponse).toList();
    }
}
