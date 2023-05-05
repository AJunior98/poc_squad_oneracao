package org.br.agro.service.impl;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import org.br.agro.dto.ProposalDTO;
import org.br.agro.dto.ProposalDetailsDTO;
import org.br.agro.entity.ProposalEntity;
import org.br.agro.repository.ProposalRepository;
import org.br.agro.service.ProposalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class ProposalServiceImplTest {

    @Inject
    ProposalService proposalService;

    @Inject
    ProposalRepository proposalRepository;

    ProposalEntity proposalEntity;
    ProposalDetailsDTO proposalDetailsDTO;
    ProposalDTO proposalDTO;

    @BeforeEach
    void config() {
        proposalEntity = new ProposalEntity(null, "Sinochem", new BigDecimal(430.00), "Arroz", 400, "China", 7, new Date());
        proposalDetailsDTO = new ProposalDetailsDTO(null, "Sinochem", new BigDecimal(430.00), "Arroz", 400, "China", 7);
        proposalDTO = new ProposalDTO(1L, "Sinochem", new BigDecimal(430), "Arroz");
    }

    @Test
    @DisplayName("Should find an proposal successfully")
    @TestTransaction
    @Order(1)
    public void testFindFullProposal() {
        proposalRepository.persist(proposalEntity);

        ProposalDetailsDTO proposalDetailsDTO = proposalService.findFullProposal(proposalEntity.getId());

        assertNotNull(proposalDetailsDTO.getProposalId());
        assertEquals("Sinochem", proposalDetailsDTO.getCustomer());
        assertEquals(new BigDecimal(430.00), proposalDetailsDTO.getPriceTonne());
        assertEquals("Arroz", proposalDetailsDTO.getProduct());
        assertEquals(400, proposalDetailsDTO.getTonnes());
        assertEquals("China", proposalDetailsDTO.getCountry());
        assertEquals(7, proposalDetailsDTO.getProposalValidityDays());
    }

    @Test
    @DisplayName("Should find an proposal successfully")
    @TestTransaction
    @Order(2)
    public void testRemoveProposal() {
        proposalRepository.persist(proposalEntity);

        assertDoesNotThrow(() -> proposalService.removeProposal(proposalEntity.getId()));
    }

    @Test
    @DisplayName("Should find an proposal successfully")
    @TestTransaction
    @Order(3)
    public void testCreateNewProposal() {
        assertDoesNotThrow(() -> proposalService.createNewProposal(proposalDetailsDTO));
    }

}