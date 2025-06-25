package com.exemplo.reembolso.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("acionarPagamento")
public class AcionaPagamentoDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("⏳ Executando pagamento (aguardando 10 segundos)...");

        try {
            Thread.sleep(10000); // 💤 Espera 10 segundos
        } catch (InterruptedException e) {
            System.err.println("Erro na simulação de pagamento: " + e.getMessage());
        }

        System.out.println("✅ Pagamento simulado com sucesso.");
    }
}
