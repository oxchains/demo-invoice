package oxchains.invoice.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import oxchains.invoice.auth.JwtAuthentication;
import oxchains.invoice.data.*;
import oxchains.invoice.domain.CompanyUser;
import oxchains.invoice.domain.Goods;
import oxchains.invoice.domain.Invoice;
import oxchains.invoice.domain.User;
import oxchains.invoice.rest.domain.ChaincodeResp;
import oxchains.invoice.rest.domain.InvoiceReq;
import oxchains.invoice.rest.domain.RestResp;
import oxchains.invoice.util.ResponseUtil;

import java.util.Collection;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;
import static oxchains.invoice.rest.domain.RestResp.fail;
import static oxchains.invoice.rest.domain.RestResp.success;

/**
 * @author aiet
 */
@RestController
@RequestMapping("/invoice")
public class InvoiceController {

    private ChaincodeData chaincodeData;
    private InvoiceRepo invoiceRepo;
    private GoodsRepo goodsRepo;
    private CompanyUserRepo companyUserRepo;
    private CompanyRepo companyRepo;
    private UserRepo userRepo;

    private Optional<User> userContext() {
        return ((JwtAuthentication) getContext().getAuthentication()).user();
    }

    public InvoiceController(@Autowired UserRepo userRepo, @Autowired CompanyRepo companyRepo, @Autowired CompanyUserRepo companyUserRepo, @Autowired GoodsRepo goodsRepo, @Autowired InvoiceRepo invoiceRepo, @Autowired ChaincodeData chaincodeData) {
        this.userRepo = userRepo;
        this.companyRepo = companyRepo;
        this.companyUserRepo = companyUserRepo;
        this.invoiceRepo = invoiceRepo;
        this.chaincodeData = chaincodeData;
        this.goodsRepo = goodsRepo;
    }

    @GetMapping
    public RestResp list() {
        return userContext()
          .map(u -> chaincodeData
            .invoiceHistory(u.getName())
            .stream()
            .map(ChaincodeResp::getPayload)
            .map(ResponseUtil::parseInvoicePayload)
            .flatMap(Collection::stream)
            .collect(toList()))
          .map(RestResp::success)
          .orElse(fail());
    }

    @GetMapping("/{serial}")
    public RestResp get(@PathVariable String serial) {
        return userContext()
          .flatMap(u -> invoiceRepo.findInvoiceByOwnerAndSerial(u.getName(), serial))
          .map(RestResp::success)
          .orElse(fail());
    }

    @PostMapping
    public RestResp issue(@RequestBody InvoiceReq invoiceReq) {
        return userContext()
          .flatMap(u -> companyUserRepo
            .findByName(u.getName())
            .flatMap(cu -> companyRepo
              .findByName(invoiceReq.getTitle())
              .map(target -> {
                  Invoice invoice = new Invoice();
                  invoice.setTarget(target);
                  invoice.setOrigin(cu.getCompany());
                  invoice.setOwner(cu.getName());
                  invoice.setGoods(invoiceReq.getGoods());
                  invoice.setStatus("未报销");
                  return invoice;
              })
              .flatMap(invoice -> chaincodeData
                .createInvoice(invoice)
                .filter(ChaincodeResp::succeeded)
                .map(resp -> {
                    Iterable<Goods> savedGoods = goodsRepo.save(invoice.getGoods());
                    invoice.setGoods(newArrayList(savedGoods));
                    return success(transferedInvoice(invoiceRepo.save(invoice), invoiceReq.getTarget()));
                }))))
          .orElse(fail());
    }

    private Invoice transferedInvoice(Invoice invoice, String target) {
        return chaincodeData
          .transfer(invoice, target)
          .filter(ChaincodeResp::succeeded)
          .map(resp -> {
              invoice.setOwner(target);
              return invoiceRepo.save(invoice);
          })
          .orElse(null);
    }

    @PutMapping
    public RestResp transfer(@RequestParam("invoice") String serial, @RequestParam String target, @RequestParam(required = false) boolean biz) {
        return userContext()
          .flatMap(owner -> (biz ? companyUserRepo
            .findByName(target)
            .map(CompanyUser::toUser) : userRepo.findByName(target)).flatMap(targetUser -> invoiceRepo
            .findInvoiceByOwnerAndSerial(owner.getName(), serial)
            .map(invoice -> transferedInvoice(invoice, targetUser.getName()))
            .map(transferredInvoice -> success(null))))
          .orElse(fail());
    }

}
