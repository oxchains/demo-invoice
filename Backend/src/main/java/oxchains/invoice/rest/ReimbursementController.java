package oxchains.invoice.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import oxchains.invoice.auth.JwtAuthentication;
import oxchains.invoice.data.*;
import oxchains.invoice.domain.Invoice;
import oxchains.invoice.domain.Reimbursement;
import oxchains.invoice.domain.User;
import oxchains.invoice.rest.domain.ChaincodeResp;
import oxchains.invoice.rest.domain.RestResp;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;
import static oxchains.invoice.rest.domain.RestResp.fail;
import static oxchains.invoice.rest.domain.RestResp.success;
import static oxchains.invoice.util.ResponseUtil.txidTail;

/**
 * @author aiet
 */
@RestController
@RequestMapping("/reimbursement")
public class ReimbursementController {

    private ChaincodeData chaincodeData;
    private ReimbursementRepo reimbursementRepo;
    private CompanyUserRepo companyUserRepo;
    private CompanyRepo companyRepo;
    private InvoiceRepo invoiceRepo;

    public ReimbursementController(@Autowired InvoiceRepo invoiceRepo, @Autowired CompanyRepo companyRepo, @Autowired CompanyUserRepo companyUserRepo, @Autowired ChaincodeData chaincodeData, @Autowired ReimbursementRepo reimbursementRepo) {
        this.invoiceRepo = invoiceRepo;
        this.companyRepo = companyRepo;
        this.chaincodeData = chaincodeData;
        this.reimbursementRepo = reimbursementRepo;
        this.companyUserRepo = companyUserRepo;
    }

    private Optional<User> userContext() {
        return ((JwtAuthentication) getContext().getAuthentication()).user();
    }

    @GetMapping
    public RestResp listRI() {
        return userContext()
          .map(u -> u.isBiz() ? companyUserRepo
            .findByName(u.getName())
            .map(cu -> reimbursementRepo.findAllByCompany(cu.getCompany()))
            .orElse(null) : reimbursementRepo.findAllByCustomer(u.getName()))
          .map(iter -> success(newArrayList(iter)))
          .orElse(fail());
    }

    @GetMapping("/{rid}")
    public RestResp getRI(@PathVariable String rid) {
        return userContext()
          .flatMap(u -> chaincodeData
            .reimbursementOf(u.getName(), rid)
            .filter(resp -> resp.succeeded() && resp
              .getPayload()
              .contains(u.getName()))
            .flatMap(resp -> u.isBiz() ? companyUserRepo
              .findByName(u.getName())
              .flatMap(cu -> reimbursementRepo.findBySerialAndCompany(rid, cu.getCompany())) : reimbursementRepo.findBySerialAndCustomer(rid, u.getName())))
          .map(RestResp::success)
          .orElse(fail());
    }

    @PostMapping
    public RestResp requestRI(@RequestParam(required = false, defaultValue = "") String remark, @RequestParam String company, @RequestParam String[] invoices) {
        return userContext()
          .flatMap(owner -> companyRepo
            .findByName(company)
            .flatMap(c -> {
                List<Invoice> invoiceList = newArrayList(invoiceRepo.findDistinctBySerialIn(Arrays.asList(invoices)));
                if (invoiceList.size() == invoices.length) {
                    Reimbursement reimbursement = new Reimbursement();
                    reimbursement.setCustomer(owner.getName());
                    reimbursement.setCompany(c);
                    reimbursement.setDescription(remark);
                    return chaincodeData
                      .reimburse(owner.getName(), invoices, reimbursement)
                      .filter(ChaincodeResp::succeeded)
                      .map(resp -> {
                          reimbursement.setStatus(txidTail("审核中", resp.getTxid()));
                          reimbursement.setInvoices(invoiceList);
                          return reimbursementRepo.save(reimbursement);
                      });
                }
                return Optional.empty();
            }))
          .map(RestResp::success)
          .orElse(fail());
    }

    @PutMapping
    public RestResp updateRI(@RequestParam String id, @RequestParam(required = false, defaultValue = "") String remark, @RequestParam int action) {
        return userContext()
          .filter(User::isBiz)
          .flatMap(u -> companyUserRepo
            .findByName(u.getName())
            .flatMap(cu -> reimbursementRepo
              .findBySerialAndCompany(id, cu.getCompany())
              .flatMap(reimbursement -> chaincodeData
                .handleReimbursement(reimbursement.getSerial(), reimbursement
                  .getCompany()
                  .getName(), action <= 0, remark)
                .filter(ChaincodeResp::succeeded)
                .map(resp -> {
                    if (action > 0) {
                        reimbursement
                          .getInvoices()
                          .forEach(invoice -> {
                              invoice.setStatus(txidTail("已报销", resp.getTxid()));
                              invoice.setHistory(String.format("%s->%s", invoice.getOwner(), u.getName()));
                              invoice.setOwner(u.getName());
                              invoiceRepo.save(invoice);
                          });
                        reimbursement.setStatus(txidTail("已确认", resp.getTxid()));
                    } else {
                        reimbursement.setStatus(txidTail("已拒绝:" + remark, resp.getTxid()));
                    }
                    return success(reimbursementRepo.save(reimbursement));
                }))))
          .orElse(fail());
    }

}
