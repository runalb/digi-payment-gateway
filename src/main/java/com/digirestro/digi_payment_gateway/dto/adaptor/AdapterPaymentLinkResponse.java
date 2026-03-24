package com.digirestro.digi_payment_gateway.dto.adaptor;

import com.digirestro.digi_payment_gateway.enums.PaymentStatusEnum;

public record AdapterPaymentLinkResponse(
        String paymentLinkUrl,
        String paymentChannelTxnId,
        PaymentStatusEnum status) {}
