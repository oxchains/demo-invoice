package oxchains.invoice.data;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import oxchains.invoice.domain.Invoice;
import oxchains.invoice.domain.Reimbursement;
import oxchains.invoice.rest.domain.ChaincodeResp;
import oxchains.invoice.rest.domain.FabricAccount;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.lang.System.currentTimeMillis;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
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

    private HttpHeaders httpHeaders;
    private RestTemplate restTemplate;

    private Logger LOG = LoggerFactory.getLogger(getClass());

    public ChaincodeData() {
        this.httpHeaders = new HttpHeaders();
        this.restTemplate = new RestTemplate();
    }

    @Value("${fabric.uri}") private String uri;
    @Value("${fabric.username}") private String username;
    @Value("${fabric.password}") private String password;
    @Value("${fabric.affiliation}") private String affiliation;

    @Scheduled(fixedRate = 1000 * 3600 * 72)
    void token() {
        String resp = new RestTemplate().postForObject(uri + "/user/token", new FabricAccount(username, password, affiliation), String.class);
        Optional<String> tokenOptional = extract(resp, "/data/token");
        if (tokenOptional.isPresent()) {
            String token = "Bearer " + tokenOptional.get();
            LOG.info("refreshed access token for fabric manager: {}", token);
            this.httpHeaders.set(AUTHORIZATION, token);
        } else throw new IllegalStateException("system failed to init: cannot get token from fabric manager!");
    }

    public Optional<ChaincodeResp> createInvoice(Invoice invoice) {
        return extract(restTemplate.postForObject(txUri + "create," + invoice.createArgs(), new HttpEntity<>(this.httpHeaders), String.class), "/data").map(data -> resolve(data, ChaincodeResp.class));
    }

    public List<ChaincodeResp> invoiceHistory(String user) {
        return Stream
          .of(getHistoryOf(user, "0"), getHistoryOf(user, "1"))
          .flatMap(respOptional -> respOptional
            .map(Stream::of)
            .orElseGet(Stream::empty))
          .filter(resp -> isNotBlank(resp.getPayload()))
          .peek(resp -> resp.setPayload(resp
            .getPayload()
            .replace("\n", ",")))
          .collect(toList());
    }

    private Optional<ChaincodeResp> getHistoryOf(String user, String type) {
        return extract(restTemplate
          .exchange(String.format("%s%s,%s,%s", txUri, "myHistory", user, type), GET, new HttpEntity<>(this.httpHeaders), String.class)
          .getBody(), "/data").map(data -> resolve(data, ChaincodeResp.class));

    }

    public Optional<ChaincodeResp> transfer(Invoice invoice, String target) {
        return extract(restTemplate.postForObject(txUri + "transfer," + invoice.transferArgs(target), new HttpEntity<>(this.httpHeaders), String.class), "/data").map(data -> resolve(data, ChaincodeResp.class));
    }

    public Optional<ChaincodeResp> reimbursementOf(String name, String rid) {
        return extract(restTemplate
          .exchange(String.format("%s%s,%s,%s", txUri, "getbx", rid, name), GET, new HttpEntity<>(this.httpHeaders), String.class)
          .getBody(), "/data").map(data -> resolve(data, ChaincodeResp.class));
    }

    public Optional<ChaincodeResp> reimburse(String[] invoices, Reimbursement reimbursement) {
        return extract(restTemplate.postForObject(txUri + "createbx," + reimbursement.reimburseArgs(StringUtils.join(invoices, "-")), new HttpEntity<>(this.httpHeaders), String.class), "/data").map(data -> resolve(data, ChaincodeResp.class));
    }

    public Optional<ChaincodeResp> handleReimbursement(String serial, String name, boolean reject, String remark) {
        if (reject) {
            return extract(restTemplate.postForObject(String.format("%s%s,%s,%s,%s,%s", txUri, "rejectbx", serial, name, remark, currentTimeMillis() / 1000), new HttpEntity<>(this.httpHeaders), String.class), "/data").map(
              data -> resolve(data, ChaincodeResp.class));
        } else {
            return extract(restTemplate.postForObject(String.format("%s%s,%s,%s,%s", txUri, "confirmbx", serial, name, currentTimeMillis() / 1000), new HttpEntity<>(this.httpHeaders), String.class), "/data").map(data -> resolve(data, ChaincodeResp.class));

        }
    }
}
