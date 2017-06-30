package oxchains.invoice.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Logger LOG = LoggerFactory.getLogger(getClass());

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
                List<Invoice> invoiceList = newArrayList(invoiceRepo.findDistinctBySerialInAndOwner(Arrays.asList(invoices), owner.getName()));
                if (invoiceList.size() == invoices.length) {
                    Reimbursement reimbursement = new Reimbursement();
                    reimbursement.setCustomer(owner.getName());
                    reimbursement.setCompany(c);
                    reimbursement.setDescription(remark);
                    return chaincodeData
                      .reimburse(owner.getName(), invoices, reimbursement)
                      .filter(ChaincodeResp::succeeded)
                      .map(resp -> {

                          invoiceList.forEach(invoice -> {
                              invoice.setCode(1);
                              invoice.setStatus(txidTail("报销中", resp.getTxid()));
                          });
                          invoiceRepo.save(invoiceList);
                          LOG.info("updated status of invoices {}", company, invoiceList);

                          reimbursement.setStatus(txidTail("审核中", resp.getTxid()));
                          reimbursement.setInvoices(invoiceList);
                          LOG.info("requesting {} to reimburse invoices {}", company, invoiceList);
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
                    List<Invoice> invoices = reimbursement.getInvoices();
                    if (action > 0) {
                        invoices.forEach(invoice -> {
                            invoice.setStatus(txidTail("已报销", resp.getTxid()));
                            invoice.setCode(2);
                            invoice.setHistory(String.format("%s->%s", invoice.getOwner(), u.getName()));
                            invoice.setOwner(u.getName());
                        });
                        reimbursement.setStatus(txidTail("已确认", resp.getTxid()));
                        reimbursement.setCode(1);
                        LOG.info("{} confirmed reimbursement {}", cu, id);
                    } else {
                        invoices.forEach(invoice -> {
                            invoice.setStatus(txidTail("未报销", resp.getTxid()));
                            invoice.setCode(0);
                        });
                        reimbursement.setCode(-1);
                        reimbursement.setStatus(txidTail("已拒绝:" + remark, resp.getTxid()));
                        LOG.info("{} denied reimbursement {}", cu, id);
                    }
                    invoiceRepo.save(invoices);
                    return success(reimbursementRepo.save(reimbursement));
                }))))
          .orElse(fail());
    }

}
