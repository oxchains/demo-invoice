package oxchains.invoice.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import oxchains.invoice.data.CompanyRepo;
import oxchains.invoice.data.CompanyUserRepo;
import oxchains.invoice.domain.CompanyUser;
import oxchains.invoice.rest.domain.RestResp;

import static oxchains.invoice.rest.domain.RestResp.fail;
import static oxchains.invoice.rest.domain.RestResp.success;

/**
 * @author aiet
 */
@RestController
@RequestMapping("/company")
public class CompanyController {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    private CompanyUserRepo companyUserRepo;
    private CompanyRepo companyRepo;


    public CompanyController(@Autowired CompanyUserRepo companyUserRepo, @Autowired CompanyRepo companyRepo) {
        this.companyUserRepo = companyUserRepo;
        this.companyRepo = companyRepo;
    }

    @PostMapping
    public RestResp registerCompany(@RequestBody CompanyUser companyUser) {
        return companyUserRepo
          .findByName(companyUser.getName())
          .map(c -> fail())
          .orElseGet(() -> {
              CompanyUser saved = companyUserRepo.save(companyUser);
              LOG.info("company {} registered", saved);
              return success(null);
          });
    }

    @GetMapping
    public RestResp list() {
        return success(companyRepo.findAll());
    }

}
