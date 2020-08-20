package com.teksenz.statemachine.config.actions;

import com.teksenz.statemachine.domain.PaymentEvent;
import com.teksenz.statemachine.domain.PaymentState;
import com.teksenz.statemachine.services.PaymentServiceImpl;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.Random;
@Component
public class PreAuthAction implements Action<PaymentState, PaymentEvent> {
    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {
        System.out.println("Preauth Action called");
        if(new Random().nextInt(10)<8){
            System.out.println("Approve PreAuth");
            context.getStateMachine()
                    .sendEvent(MessageBuilder
                            .withPayload(PaymentEvent.PRE_AUTH_APPROVE)
                            .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER,context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
                            .build());
        }else {
            System.out.println("Decline PreAuth");
            context.getStateMachine()
                    .sendEvent(MessageBuilder
                            .withPayload(PaymentEvent.PRE_AUTH_DECLINE)
                            .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER,context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
                            .build());
        }
    }
}
