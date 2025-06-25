package com.exemplo.reembolso.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("notificarRejeicao")
public class NotificarRejeicaoDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        // Aqui você pode simular o envio de e-mail, log, etc.
        System.out.println("📨 Notificando solicitante: reembolso foi rejeitado.");
    }
}
