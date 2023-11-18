package guru.springframework.spring6resttemplate.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6resttemplate.config.RestTemplateBuilderConfig;
import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerDTOPageImpl;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.web.client.MockServerRestTemplateCustomizer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest
@Import(RestTemplateBuilderConfig.class)
class BeerClientMockTest {

    private static final String URL = "http://localhost:8080";
    @Autowired
    RestTemplateBuilder restTemplateBuilder;
    @Autowired
    ObjectMapper objectMapper;
    @Mock
    RestTemplateBuilder mockRestTempleteBuilder = new RestTemplateBuilder(new MockServerRestTemplateCustomizer());

    BeerDTO dto;
    String dtoJson;
    BeerClientImpl beerClient;
    MockRestServiceServer server;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        RestTemplate restTemplate = restTemplateBuilder.build();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        when(mockRestTempleteBuilder.build()).thenReturn(restTemplate);
        beerClient = new BeerClientImpl(mockRestTempleteBuilder);
        dto = getBeerDto();
        dtoJson = objectMapper.writeValueAsString(dto);
    }

    @Test
    void deleteBeerNotFound() {
        server.expect(method(HttpMethod.DELETE))
                .andExpect(requestToUriTemplate(URL + BeerClientImpl.GET_BEER_BY_ID_PATH, dto.getId()))
                .andRespond(withResourceNotFound());

        assertThrows(HttpClientErrorException.class , () -> {
            beerClient.deleteBeer(dto.getId());
        });
    }

    @Test
    void deleteBeer() {
        server.expect(method(HttpMethod.DELETE))
                .andExpect(requestToUriTemplate(URL + BeerClientImpl.GET_BEER_BY_ID_PATH, dto.getId()))
                .andRespond(withNoContent());

        beerClient.deleteBeer(dto.getId());

        server.verify();
    }

    @Test
    void updateBeer() {
        server.expect(method(HttpMethod.PUT))
                .andExpect(requestToUriTemplate(URL + BeerClientImpl.GET_BEER_BY_ID_PATH, dto.getId()))
                .andRespond(withNoContent());

        mockGetBeerById();

        BeerDTO responseDto = beerClient.updateBeer(dto);

        assertThat(responseDto.getBeerName()).isEqualTo(dto.getBeerName());
    }

    @Test
    void createBeer() {
        URI uri = UriComponentsBuilder.fromPath(BeerClientImpl.GET_BEER_BY_ID_PATH)
                .build(dto.getId());

        server.expect(method(HttpMethod.POST))
                .andExpect(requestTo(URL + BeerClientImpl.GET_BEER_PATH))
                .andRespond(withAccepted().location(uri));

        mockGetBeerById();

        BeerDTO responseDto = beerClient.createBeer(dto);

        assertThat(responseDto.getBeerName()).isEqualTo(dto.getBeerName());
    }

    @Test
    void getBeerById() {
        mockGetBeerById();

        BeerDTO responseDto = beerClient.getBeerById(dto.getId());

        assertThat(responseDto.getBeerName()).isEqualTo(dto.getBeerName());
    }

    @Test
    void listBeers() throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(getPage());

        server.expect(method(HttpMethod.GET))
                .andExpect(requestTo(URL + BeerClientImpl.GET_BEER_PATH))
                .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));

        Page<BeerDTO> dtos = beerClient.listBeers();

        assertThat(dtos.getContent().size()).isGreaterThan(0);
    }

    private void mockGetBeerById() {
        server.expect(method(HttpMethod.GET))
                .andExpect(requestToUriTemplate(URL + BeerClientImpl.GET_BEER_BY_ID_PATH, dto.getId()))
                .andRespond(withSuccess(dtoJson, MediaType.APPLICATION_JSON));
    }

    private BeerDTOPageImpl getPage() {
        return new BeerDTOPageImpl(Arrays.asList(getBeerDto()), 1, 25, 1);
    }

    private BeerDTO getBeerDto() {
        return BeerDTO.builder()
                .id(UUID.randomUUID())
                .price(new BigDecimal("10.99"))
                .beerName("Mango Bobs")
                .beerStyle(BeerStyle.IPA)
                .quantityOnHand(500)
                .upc("123245")
                .build();
    }
}
