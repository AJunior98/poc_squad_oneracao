package org.br.agro.infra.repository;

import org.br.agro.domain.entity.Quotation;

import java.util.List;
import java.util.UUID;

public interface QuotationRepository {

    Quotation save(Quotation quotation);
    void delete(UUID id);
    List<Quotation> findAll();

}
