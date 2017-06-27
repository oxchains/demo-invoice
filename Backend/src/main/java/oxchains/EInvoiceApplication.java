package oxchains;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import oxchains.invoice.rest.domain.FabricAccount;
import static oxchains.invoice.util.ResponseUtil.extract;

import java.util.Optional;

@SpringBootApplication
public class EInvoiceApplication {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    @Value("${fabric.uri}") private String uri;

    @Value("${fabric.username}") private String username;
    @Value("${fabric.password}") private String password;
    @Value("${fabric.affiliation}") private String affiliation;

    @Bean({ "token" })
    String token() {
        String resp = new RestTemplate().postForObject(uri + "/user/token", new FabricAccount(username, password, affiliation), String.class);
        Optional<String> tokenOptional = extract(resp, "/data/token");
        if (tokenOptional.isPresent()) {
            String token = "Bearer " + tokenOptional.get();
            LOG.info("access token for fabric manager: {}", token);
            return token;
        } else throw new IllegalStateException("system failed to init: cannot get token from fabric manager!");
    }

    public static void main(String[] args) {
        SpringApplication.run(EInvoiceApplication.class, args);
    }
}
