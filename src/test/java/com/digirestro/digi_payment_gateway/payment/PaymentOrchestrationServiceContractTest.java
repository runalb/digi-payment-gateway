package com.digirestro.digi_payment_gateway.payment;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.digirestro.digi_payment_gateway.merchant.entity.MerchantEntity;
import com.digirestro.digi_payment_gateway.integration.api.dto.PaymentLinkRequest;
import com.digirestro.digi_payment_gateway.payment.enums.PaymentOriginEnum;
import com.digirestro.digi_payment_gateway.payment.contract.PaymentLinkOrchestrationContract;
import com.digirestro.digi_payment_gateway.payment.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.payment.service.PaymentOrchestrationService;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

class PaymentOrchestrationServiceContractTest {

    @Test
    void serviceDeclaresOrchestrationContract() {
        assertTrue(PaymentOrchestrationService.class.isAnnotationPresent(PaymentLinkOrchestrationContract.class));
    }

    @Test
    void generatePaymentLinkMustNotBeTransactional() throws NoSuchMethodException {
        Method method = PaymentOrchestrationService.class.getDeclaredMethod(
                "generatePaymentLink",
                MerchantEntity.class,
                PaymentLinkRequest.class,
                PaymentOriginEnum.class);

        assertFalse(method.isAnnotationPresent(Transactional.class));
    }

    @Test
    void completePaymentLinkGenerationMustRemainPrivatePhaseTwoHook() throws NoSuchMethodException {
        Method method = PaymentOrchestrationService.class.getDeclaredMethod(
                "completePaymentLinkGeneration", PaymentEntity.class);

        assertTrue(Modifier.isPrivate(method.getModifiers()));
        assertTrue(method.isAnnotationPresent(PaymentLinkOrchestrationContract.class));
    }
}
