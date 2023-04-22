package org.br.agro.infra.repository.impl;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import org.br.agro.domain.entity.Quotation;
import org.br.agro.infra.entity.QuotationEntity;
import org.br.agro.infra.repository.QuotationRepository;
import org.br.agro.infra.repository.mapper.QuotationDynamoRepositoryMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class QuotationRepositoryImpl implements QuotationRepository {

    private final DynamoDBMapper dynamoDBMapper;
    private final QuotationDynamoRepositoryMapper mapper;

    @Inject
    public QuotationRepositoryImpl(DynamoDBMapper dynamoDBMapper, QuotationDynamoRepositoryMapper mapper) {
        this.dynamoDBMapper = dynamoDBMapper;
        this.mapper = mapper;
    }

    @Override
    public Quotation save(Quotation quotation) {
        var quotationEntity = this.mapper.mapToEntity(quotation);
        dynamoDBMapper.save(quotationEntity);
        quotation.setId(quotationEntity.getId());
        return mapper.mapToDomain(quotationEntity);
    }

    @Override
    public void delete(UUID id) {
        var movieEntity = this.dynamoDBMapper.load(QuotationEntity.class, id);
        Optional.ofNullable(movieEntity)
                .ifPresent(this.dynamoDBMapper::delete);
    }

    @Override
    public List<Quotation> findAll() {
        var scan = new DynamoDBScanExpression();
        var listScan = dynamoDBMapper.scan(QuotationEntity.class, scan);
        return listScan.stream().map(this.mapper::mapToDomain).collect(Collectors.toList());
    }
}
