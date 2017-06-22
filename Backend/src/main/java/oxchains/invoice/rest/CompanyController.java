package oxchains.invoice.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import oxchains.invoice.domain.CompanyUser;
import oxchains.invoice.rest.domain.RestResp;

/**
 * @author aiet
 */
@RestController("/company")
public class CompanyController {

    @PostMapping
    public RestResp registerCompany(@RequestBody CompanyUser companyUser) {
        return RestResp.success(companyUser.getCompany());
    }

}
