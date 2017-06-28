package oxchains.invoice.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import oxchains.invoice.domain.Invoice;
import oxchains.invoice.rest.domain.ChaincodeResp;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpMethod.GET;
import static oxchains.invoice.util.ResponseUtil.extract;
import static oxchains.invoice.util.ResponseUtil.resolve;

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

    public Optional<ChaincodeResp> createInvoice(Invoice invoice) {
        return extract(restTemplate.postForObject(txUri + "create," + invoice.createArgs(), entity, String.class), "/data").map(data -> resolve(data, ChaincodeResp.class));
    }

    public List<ChaincodeResp> invoiceHistory(String user) {
        return Stream
          .of(getHistoryOf(user, "0"), getHistoryOf(user, "1"))
          .flatMap(respOptional -> respOptional
            .map(Stream::of)
            .orElseGet(Stream::empty))
          .filter(resp -> isNotBlank(resp.getPayload()))
          .map(resp -> {
              resp.setPayload(resp
                .getPayload()
                .replace("\n", ","));
              return resp;
          })
          .collect(toList());
    }

    private Optional<ChaincodeResp> getHistoryOf(String user, String type) {
        return extract(restTemplate
          .exchange(String.format("%s%s,%s,%s", txUri, "myHistory", user, type), GET, entity, String.class)
          .getBody(), "/data").map(data -> resolve(data, ChaincodeResp.class));

    }

    public Optional<ChaincodeResp> transfer(Invoice invoice, String target) {
        return extract(restTemplate.postForObject(txUri + "transfer," + invoice.transferArgs(target), entity, String.class), "/data").map(data -> resolve(data, ChaincodeResp.class));
    }
}
