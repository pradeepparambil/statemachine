package com.teksenz.statemachine.config;

import com.teksenz.statemachine.domain.PaymentEvent;
import com.teksenz.statemachine.domain.PaymentState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import java.util.UUID;

@Slf4j
@SpringBootTest
class StateMachineConfigTest {
    @Autowired
    StateMachineFactory<PaymentState, PaymentEvent> factory;

    @Test
    public void testStateMachine(){
        StateMachine<PaymentState,PaymentEvent> sm = factory.getStateMachine(UUID.randomUUID());
        sm.start();
        log.debug("State : " + sm.getState());
        sm.sendEvent(PaymentEvent.PRE_AUTHORIZE);
        log.debug("State : " + sm.getState());
        sm.sendEvent(PaymentEvent.PRE_AUTH_APPROVE);
        log.debug("State : " + sm.getState());

    }

}