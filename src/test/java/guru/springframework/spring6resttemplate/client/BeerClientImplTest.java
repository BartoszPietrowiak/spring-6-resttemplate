package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BeerClientImplTest {

    @Autowired
    BeerClientImpl beerClient;

    @Test
    void deleteBeer() {
        BeerDTO dto = BeerDTO.builder()
                .price(new BigDecimal("10.99"))
                .beerName("Tyskie")
                .beerStyle(BeerStyle.LAGER)
                .quantityOnHand(500)
                .upc("123132123")
                .build();

        BeerDTO beerDTO = beerClient.createBeer(dto);

        beerClient.deleteBeer(beerDTO.getId());

        assertThrows(HttpClientErrorException.class, () -> {
            beerClient.getBeerById(beerDTO.getId());
        });
    }

    @Test
    void updateBeer() {
        BeerDTO dto = BeerDTO.builder()
                .price(new BigDecimal("10.99"))
                .beerName("Tyskie")
                .beerStyle(BeerStyle.LAGER)
                .quantityOnHand(500)
                .upc("123132123")
                .build();

        BeerDTO beerDTO = beerClient.createBeer(dto);

        final String newBeerName = "ZUBR";

        beerDTO.setBeerName(newBeerName);
        BeerDTO updatedBeer = beerClient.updateBeer(beerDTO);

        assertEquals(newBeerName, updatedBeer.getBeerName());
    }

    @Test
    void createBeer() {
        BeerDTO dto = BeerDTO.builder()
                .price(new BigDecimal("10.99"))
                .beerName("Tyskie")
                .beerStyle(BeerStyle.LAGER)
                .quantityOnHand(500)
                .upc("123132123")
                .build();

        BeerDTO savedDto = beerClient.createBeer(dto);

        assertNotNull(savedDto);
    }

    @Test
    void getBeerById() {
        Page<BeerDTO> beerDTOS = beerClient.listBeers();

        BeerDTO dto = beerDTOS.getContent().get(0);

        BeerDTO byId = beerClient.getBeerById(dto.getId());

        assertNotNull(byId);
    }

    @Test
    void listBeers() {
        beerClient.listBeers();
    }

}
