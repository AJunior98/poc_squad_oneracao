package org.br.agro.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.br.agro.entity.OpportunityEntity;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OpportunityRepository implements PanacheRepository<OpportunityEntity> {
}
