package org.br.agro.controller;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.br.agro.dto.ProposalDetailsDTO;
import org.br.agro.entity.ProposalEntity;
import org.br.agro.repository.ProposalRepository;
import org.br.agro.util.AccessTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class ProposalControllerTest extends AccessTokenProvider {

    @Inject
    ProposalRepository proposalRepository;

    @TestHTTPResource("/api/proposal")
    URL apiURL;

    static ProposalDetailsDTO proposalDetailsDTO;
    static ProposalEntity proposalEntity;

    Long proposalID;

    @BeforeEach
    @Transactional
    void config() throws Exception {
        proposalDetailsDTO = new ProposalDetailsDTO(null, "Sinochem", new BigDecimal(430.00), "Arroz", 400, "China", 7);
        proposalEntity = new ProposalEntity(null, "Sinochem", new BigDecimal(430.00), "Arroz", 400, "China", 7, new Date());
        proposalRepository.persist(proposalEntity);
        proposalID = proposalEntity.getId();
    }

    @Test
    @DisplayName("Should create a proposal successfully")
    public void createProposalTest() {
        Response response =
                given()
                        .auth().oauth2(getAccessToken("sinochem", "1234"))
                        .contentType(ContentType.JSON)
                        .body(proposalDetailsDTO)
                        .when()
                        .post(apiURL)
                        .then()
                        .extract().response();
        assertEquals(201, response.statusCode());
    }

}