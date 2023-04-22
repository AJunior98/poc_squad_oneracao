package org.br.agro.infra.repository.mapper;

import org.br.agro.application.web.dto.QuotationDTO;
import org.br.agro.domain.entity.Quotation;
import org.br.agro.infra.entity.QuotationEntity;
import org.modelmapper.ModelMapper;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class QuotationDynamoRepositoryMapper {

    public QuotationEntity mapToEntity(Quotation quotation){
        var mapper = new ModelMapper();
        return mapper.map(quotation, QuotationEntity.class);
    }

    public Quotation mapToDomain(QuotationEntity quotationEntity){
        var mapper = new ModelMapper();
        return mapper.map(quotationEntity, Quotation.class);
    }

}
