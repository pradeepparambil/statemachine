package com.teksenz.statemachine.config.guards;

import com.teksenz.statemachine.domain.PaymentEvent;
import com.teksenz.statemachine.domain.PaymentState;
import com.teksenz.statemachine.services.PaymentServiceImpl;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

@Component
public class PaymentIdGuard implements Guard<PaymentState, PaymentEvent> {
    @Override
    public boolean evaluate(StateContext<PaymentState, PaymentEvent> stateContext) {
        return stateContext.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER) != null;
    }
}
