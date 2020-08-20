package com.teksenz.statemachine.services;

import com.teksenz.statemachine.domain.Payment;
import com.teksenz.statemachine.domain.PaymentEvent;
import com.teksenz.statemachine.domain.PaymentState;
import com.teksenz.statemachine.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentServiceImplTest {
    @Autowired
    PaymentService paymentService;
    @Autowired
    PaymentRepository paymentRepository;
    Payment payment;
    @BeforeEach
    void setUp() {
        payment = Payment.builder()
                .amount(new BigDecimal(100))
                .build();
    }


    @Transactional
    @Test
    void preAuthorize() {
        Payment savedPayment = paymentService.newPayment(payment);
        assertEquals(PaymentState.NEW,savedPayment.getState());

        StateMachine<PaymentState, PaymentEvent> sm = paymentService.preAuth(savedPayment.getId());
        savedPayment = paymentRepository.getOne(savedPayment.getId());

        System.out.println(sm.getState().getId());
        System.out.println(savedPayment);
    }

    @Transactional
    @RepeatedTest(10)
    @Test
    void authorize() {
        Payment savedPayment = paymentService.newPayment(payment);
        assertEquals(PaymentState.NEW,savedPayment.getState());

        StateMachine<PaymentState, PaymentEvent> sm = paymentService.preAuth(savedPayment.getId());
        savedPayment = paymentRepository.getOne(savedPayment.getId());

        if(savedPayment.getState()==PaymentState.PRE_AUTH){
            System.out.println("Pre-authorization complete...");
            sm=paymentService.authorizePayment(savedPayment.getId());
        }else {
            System.out.println("Pre-authorization failed...");
        }

        savedPayment = paymentRepository.getOne(savedPayment.getId());
        System.out.println(sm.getState().getId());
        System.out.println(savedPayment);
    }

}