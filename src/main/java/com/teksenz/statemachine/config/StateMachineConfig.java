package com.teksenz.statemachine.config;

import com.teksenz.statemachine.domain.PaymentEvent;
import com.teksenz.statemachine.domain.PaymentState;
import com.teksenz.statemachine.services.PaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;
import java.util.Random;

@Slf4j
@EnableStateMachineFactory
@Configuration
@RequiredArgsConstructor
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {
    private final Action<PaymentState,PaymentEvent> preAuthAction;
    private final Action<PaymentState,PaymentEvent> authAction;
    private final Guard<PaymentState,PaymentEvent> paymentIdGuard;

    @Override
    public void configure(StateMachineStateConfigurer states) throws Exception {
        states.withStates()
                .initial(PaymentState.NEW)
                .states(EnumSet.allOf(PaymentState.class))
                .end(PaymentState.AUTH)
                .end(PaymentState.AUTH_ERROR)
                .end(PaymentState.PRE_AUTH_ERROR);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {
        transitions.withExternal().source(PaymentState.NEW).target(PaymentState.NEW).event(PaymentEvent.PRE_AUTHORIZE)
                .action(preAuthAction).guard(paymentIdGuard)
                .and()
                .withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH).event(PaymentEvent.PRE_AUTH_APPROVE)
                .and()
                .withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH_ERROR).event(PaymentEvent.PRE_AUTH_DECLINE)
                .and()
                .withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.PRE_AUTH).event(PaymentEvent.AUTHORIZE)
                .action(authAction)
                .and()
                .withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.AUTH).event(PaymentEvent.AUTH_APPROVE)
                .and()
                .withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.AUTH_ERROR).event(PaymentEvent.AUTH_DECLINE);

    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception {
        StateMachineListenerAdapter<PaymentState,PaymentEvent> adapter = new StateMachineListenerAdapter<>(){
            @Override
            public void stateChanged(State<PaymentState, PaymentEvent> from, State<PaymentState, PaymentEvent> to) {
                log.info(String.format("State changed(from : %s to : %s)",from,to));
            }
        };
        config.withConfiguration().listener(adapter);
    }
//    public Guard<PaymentState,PaymentEvent> paymentIdGuard(){
//        return context-> context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER) != null;
//    }
//
//    public Action<PaymentState,PaymentEvent> preAuthAction(){
//        return context->{
//            System.out.println("Preauth Action called");
//            if(new Random().nextInt(10)<8){
//                System.out.println("Approve PreAuth");
//                context.getStateMachine()
//                        .sendEvent(MessageBuilder
//                        .withPayload(PaymentEvent.PRE_AUTH_APPROVE)
//                                .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER,context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
//                                .build());
//            }else {
//                System.out.println("Decline PreAuth");
//                context.getStateMachine()
//                        .sendEvent(MessageBuilder
//                                .withPayload(PaymentEvent.PRE_AUTH_DECLINE)
//                                .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER,context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
//                                .build());
//            }
//
//        };
//    }
//
//    public Action<PaymentState,PaymentEvent> authAction(){
//        return context->{
//            System.out.println("Auth Action called");
//            if(new Random().nextInt(10)<8){
//                System.out.println("Approve Auth");
//                context.getStateMachine()
//                        .sendEvent(MessageBuilder
//                                .withPayload(PaymentEvent.AUTH_APPROVE)
//                                .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER,context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
//                                .build());
//            }else {
//                System.out.println("Decline Auth");
//                context.getStateMachine()
//                        .sendEvent(MessageBuilder
//                                .withPayload(PaymentEvent.AUTH_DECLINE)
//                                .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER,context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
//                                .build());
//            }
//
//        };
//    }


}
