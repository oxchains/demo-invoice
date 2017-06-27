package oxchains.invoice.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import oxchains.invoice.domain.Invoice;
import oxchains.invoice.rest.domain.RestResp;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;

/**
 * chaincode operations
 * @author aiet
 */
@Service
public class ChaincodeData {

    @Value("${fabric.uri.tx}") private String txUri;

    private HttpEntity<String> entity;
    private RestTemplate restTemplate = new RestTemplate();

    public ChaincodeData(@Autowired @Qualifier("token") String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, token);
        this.entity = new HttpEntity<>(httpHeaders);
    }

    public Object createInvoice(Invoice invoice) {
        return restTemplate.postForObject(txUri, entity, RestResp.class).data;
    }

    public List<Object> invoiceHistory(String user) {
        return Arrays.asList(restTemplate
          .exchange(txUri + "myHistory," + user + ",0", GET, entity, RestResp.class)
          .getBody().data, restTemplate
          .exchange(txUri + "myHistory," + user + ",0", GET, entity, RestResp.class)
          .getBody().data);
    }

}
