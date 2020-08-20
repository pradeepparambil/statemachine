package com.teksenz.statemachine.services;

import com.teksenz.statemachine.domain.Payment;
import com.teksenz.statemachine.domain.PaymentEvent;
import com.teksenz.statemachine.domain.PaymentState;
import com.teksenz.statemachine.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    public static final String PAYMENT_ID_HEADER = "payment_id";
    private final PaymentRepository paymentRepository;
    private final StateMachineFactory<PaymentState,PaymentEvent> factory;
    private final PaymentStateChangeInterceptor paymentStateChangeInterceptor;
    @Override
    public Payment newPayment(Payment payment) {
        payment.setState(PaymentState.NEW);
        return paymentRepository.save(payment);
    }

//    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId) {
        StateMachine<PaymentState,PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId,sm,PaymentEvent.PRE_AUTHORIZE);
        return sm;
    }
//    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> preAuthApprove(Long paymentId) {
        StateMachine<PaymentState,PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId,sm,PaymentEvent.PRE_AUTH_APPROVE);
        return sm;
    }

//    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId) {
        StateMachine<PaymentState,PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId,sm,PaymentEvent.AUTHORIZE);
        return sm;
    }

//    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId) {
        StateMachine<PaymentState,PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId,sm,PaymentEvent.AUTH_DECLINE);
        return sm;
    }
    private void sendEvent(Long paymentId, StateMachine<PaymentState, PaymentEvent> sm, PaymentEvent event){
        Message message = MessageBuilder.withPayload(event)
                .setHeader(PAYMENT_ID_HEADER,paymentId)
                .build();
        sm.sendEvent(message);
    }
    private StateMachine<PaymentState,PaymentEvent> build(Long paymentId){
        Payment payment = paymentRepository.getOne(paymentId);
        StateMachine<PaymentState,PaymentEvent> sm = factory.getStateMachine(payment.getId().toString());
        sm.stop();
        sm.getStateMachineAccessor()
                .doWithAllRegions(sma->{
                    sma.addStateMachineInterceptor(paymentStateChangeInterceptor);
                    sma.resetStateMachine(new DefaultStateMachineContext<>(payment.getState(),null,null,null));
                });
        sm.start();
        return sm;
    }
}
