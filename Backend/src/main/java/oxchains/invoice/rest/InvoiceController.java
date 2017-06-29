package oxchains.invoice.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;
import static oxchains.invoice.rest.domain.RestResp.fail;
import static oxchains.invoice.rest.domain.RestResp.success;
import static oxchains.invoice.util.ResponseUtil.txidTail;

/**
 * @author aiet
 */
@RestController
@RequestMapping("/invoice")
public class InvoiceController {

    private Logger LOG = LoggerFactory.getLogger(getClass());

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
            .map(serials -> invoiceRepo.findDistinctBySerialIn(serials))
            .flatMap(invoices -> newArrayList(invoices).stream())
            .collect(toList()))
          .map(RestResp::success)
          .orElse(fail());
    }

    @GetMapping("/{serial}")
    public RestResp get(@PathVariable String serial) {
        return userContext()
          .flatMap(u -> u.isBiz() ? companyUserRepo
            .findByName(u.getName())
            .flatMap(cu -> {
                Optional<Invoice> originInvoice = invoiceRepo.findInvoiceByOriginAndSerial(cu.getCompany(), serial);
                if (originInvoice.isPresent()) {
                    return originInvoice;
                } else {
                    return invoiceRepo.findInvoiceByTargetAndSerial(cu.getCompany(), serial);
                }
            }) : invoiceRepo.findInvoiceByOwnerAndSerial(u.getName(), serial))
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
                  return invoice;
              })
              .flatMap(invoice -> chaincodeData
                .createInvoice(invoice)
                .filter(ChaincodeResp::succeeded)
                .map(resp -> {
                    Iterable<Goods> savedGoods = goodsRepo.save(invoice.getGoods());

                    invoice.setGoods(newArrayList(savedGoods));
                    invoice.setStatus(txidTail("未报销", resp.getTxid()));
                    LOG.info("new invoice issued {}", invoice);
                    return success(transferredInvoice(invoiceRepo.save(invoice), invoiceReq.getTarget()));
                }))))
          .orElse(fail());
    }

    private Invoice transferredInvoice(Invoice invoice, String target) {
        return chaincodeData
          .transfer(invoice, target)
          .filter(ChaincodeResp::succeeded)
          .map(resp -> {
              LOG.info("invoice {} transfered to {}", invoice, target);
              invoice.setHistory(String.format("%s->%s", invoice.getOwner(), target));
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
            .map(invoice -> transferredInvoice(invoice, targetUser.getName()))
            .map(transferredInvoice -> success(null))))
          .orElse(fail());
    }

}
