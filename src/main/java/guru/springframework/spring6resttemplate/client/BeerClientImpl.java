package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerDTOPageImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BeerClientImpl implements BeerClient {
    public static final String GET_BEER_PATH = "/api/v1/beer";
    public static final String GET_BEER_BY_ID_PATH = "/api/v1/beer/{beerId}";
    private final RestTemplateBuilder restTemplateBuilder;

    @Override
    public Page<BeerDTO> listBeers() {
        RestTemplate restTemplate = restTemplateBuilder.build();

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath(GET_BEER_PATH);

        ResponseEntity<BeerDTOPageImpl> response = restTemplate
                .getForEntity(uriComponentsBuilder.toUriString(), BeerDTOPageImpl.class);

        return response.getBody();

    }

    @Override
    public BeerDTO getBeerById(UUID beerId) {
        RestTemplate restTemplate = restTemplateBuilder.build();

        return restTemplate.getForObject(GET_BEER_BY_ID_PATH, BeerDTO.class, beerId);
    }

    @Override
    public BeerDTO createBeer(BeerDTO beerDTO) {
        RestTemplate restTemplate = restTemplateBuilder.build();

        URI uri = restTemplate.postForLocation(GET_BEER_PATH, beerDTO);

        return restTemplate.getForObject(uri.getPath(), BeerDTO.class);
    }

    @Override
    public BeerDTO updateBeer(BeerDTO beerDTO) {
        RestTemplate restTemplate = restTemplateBuilder.build();

        restTemplate.put(GET_BEER_BY_ID_PATH, beerDTO, beerDTO.getId());

        return getBeerById(beerDTO.getId());
    }

    @Override
    public void deleteBeer(UUID beerId) {
        RestTemplate restTemplate = restTemplateBuilder.build();

        restTemplate.delete(GET_BEER_BY_ID_PATH, beerId);
    }
}
