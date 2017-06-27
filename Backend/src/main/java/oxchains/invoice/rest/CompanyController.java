package oxchains.invoice.rest;

import org.springframework.web.bind.annotation.*;
import oxchains.invoice.domain.CompanyUser;
import oxchains.invoice.rest.domain.RestResp;

import static oxchains.invoice.rest.domain.RestResp.success;

/**
 * @author aiet
 */
@RestController
@RequestMapping("/company")
public class CompanyController {

    @PostMapping
    public RestResp registerCompany(@RequestBody CompanyUser companyUser) {
        //save company
        return success(companyUser.getCompany());
    }

    @GetMapping
    public RestResp list(){
        return success(null);
    }

}
