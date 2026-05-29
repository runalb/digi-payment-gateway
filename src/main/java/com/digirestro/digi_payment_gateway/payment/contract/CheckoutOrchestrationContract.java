package com.digirestro.digi_payment_gateway.payment.contract;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks checkout orchestration code governed by a fixed two-phase flow.
 * Changing annotated types or methods requires updating {@code PaymentOrchestrationServiceContractTest}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface CheckoutOrchestrationContract {}
