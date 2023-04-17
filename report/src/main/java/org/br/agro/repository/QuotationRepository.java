package org.br.agro.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.br.agro.entity.QuotationEntity;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class QuotationRepository implements PanacheRepository<QuotationEntity> {
}
