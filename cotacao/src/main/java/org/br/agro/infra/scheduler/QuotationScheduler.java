package org.br.agro.infra.scheduler;

import io.quarkus.scheduler.Scheduled;
import org.br.agro.domain.service.impl.QuotationServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
public class QuotationScheduler {

    private final Logger LOG = LoggerFactory.getLogger(QuotationScheduler.class);

    @Inject
    QuotationServiceImpl quotationServiceImpl;

    //Podemos usar o identificador para poder cancelar facilmente o scheduled, por exemplo
    //boolean cancelled = scheduledExecutorService.cancel("task-job");

    @Transactional
    @Scheduled(every = "35s", identity="task-job")
    void schedule(){
        LOG.info("-- Executando scheduler --");
        quotationServiceImpl.getCurrencyPrice();
    }

    //A expressão cron "0 0 22 * * ?" é composta por seis campos que representam, respectivamente:
    //segundos (0-59), minutos (0-59), horas (0-23), dias do mês (1-31), meses (1-12) e dias da semana (1-7, onde tanto 1 quanto 7 representam Domingo).
    // Neste exemplo, estamos agendando o job para executar todos os dias às 22:00,
    // definindo "0" para segundos e minutos, "22" para horas, e o caractere "*" para os outros campos, indicando que não há restrições.

    @Transactional
    @Scheduled(cron = "0 0 22 * * ?")
    void scheduleDeleteAll(){
        LOG.info("-- Executando scheduler para limpeza do banco Quotation --");
        quotationServiceImpl.cleanDataBase();
    }

}
