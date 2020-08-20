package com.teksenz.statemachine.services;

import com.teksenz.statemachine.domain.Payment;
import com.teksenz.statemachine.domain.PaymentEvent;
import com.teksenz.statemachine.domain.PaymentState;
import org.springframework.statemachine.StateMachine;

public interface PaymentService {
    Payment newPayment(Payment payment);
    StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId);
    StateMachine<PaymentState, PaymentEvent> preAuthApprove(Long paymentId);
    StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId);
    StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId);

}
