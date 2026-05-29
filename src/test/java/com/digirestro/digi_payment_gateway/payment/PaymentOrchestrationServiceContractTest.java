package com.digirestro.digi_payment_gateway.payment;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.digirestro.digi_payment_gateway.integration.api.dto.CheckoutRequest;
import com.digirestro.digi_payment_gateway.merchant.entity.MerchantEntity;
import com.digirestro.digi_payment_gateway.payment.contract.CheckoutOrchestrationContract;
import com.digirestro.digi_payment_gateway.payment.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.payment.enums.PaymentOriginEnum;
import com.digirestro.digi_payment_gateway.payment.service.PaymentOrchestrationService;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

class PaymentOrchestrationServiceContractTest {

    @Test
    void serviceDeclaresOrchestrationContract() {
        assertTrue(PaymentOrchestrationService.class.isAnnotationPresent(CheckoutOrchestrationContract.class));
    }

    @Test
    void generateCheckoutMustNotBeTransactional() throws NoSuchMethodException {
        Method method = PaymentOrchestrationService.class.getDeclaredMethod(
                "generateCheckout",
                MerchantEntity.class,
                CheckoutRequest.class,
                PaymentOriginEnum.class);

        assertFalse(method.isAnnotationPresent(Transactional.class));
    }

    @Test
    void completeCheckoutGenerationMustRemainPrivatePhaseTwoHook() throws NoSuchMethodException {
        Method method = PaymentOrchestrationService.class.getDeclaredMethod(
                "completeCheckoutGeneration", PaymentEntity.class);

        assertTrue(Modifier.isPrivate(method.getModifiers()));
        assertTrue(method.isAnnotationPresent(CheckoutOrchestrationContract.class));
    }
}
