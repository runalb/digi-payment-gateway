package com.digirestro.digi_payment_gateway.dto.adaptor;

import com.digirestro.digi_payment_gateway.entity.PaymentEntity;

public record AdapterPaymentLinkResponse(
        PaymentEntity payment) {}
