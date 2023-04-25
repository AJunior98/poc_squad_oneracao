package org.br.agro.infra.message;

import org.br.agro.application.web.dto.QuotationDTO;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class KafkaEvents {

    private final Logger LOG = LoggerFactory.getLogger(KafkaEvents.class);

    @Channel("quotation-channel")
    Emitter<QuotationDTO> quotationRequestEmitter;

    //O método "toCompletableFuture()" serve obter um CompletableFuture que será concluído quando a operação de envio da mensagem for concluída.
    //O método "join()" do CompletableFuture serve para bloquear a execução do método até que a operação de envio da mensagem seja concluída.
    //Isso garante que o método só retorne após a mensagem ter sido enviada com sucesso para o tópico Kafka.
    public void sendNewKafkaEvent(QuotationDTO quotation){
        LOG.info("-- Enviando Cotação para Tópico Kafka --");
        quotationRequestEmitter.send(quotation).toCompletableFuture().join();
    }

}
