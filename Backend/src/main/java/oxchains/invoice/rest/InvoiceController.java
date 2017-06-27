package oxchains.invoice.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import oxchains.invoice.auth.JwtAuthentication;
import oxchains.invoice.data.ChaincodeData;
import oxchains.invoice.data.InvoiceRepo;
import oxchains.invoice.domain.User;
import oxchains.invoice.rest.domain.InvoiceWithGoods;
import oxchains.invoice.rest.domain.RestResp;

import java.util.ArrayList;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;
import static oxchains.invoice.rest.domain.RestResp.success;

/**
 * @author aiet
 */
@RestController
@RequestMapping("/invoice")
public class InvoiceController {

    private ChaincodeData chaincodeData;
    private InvoiceRepo invoiceRepo;

    private Optional<User> userContext() {
        return ((JwtAuthentication) getContext().getAuthentication()).user();
    }

    public InvoiceController(@Autowired InvoiceRepo invoiceRepo, @Autowired ChaincodeData chaincodeData) {
        this.invoiceRepo = invoiceRepo;
        this.chaincodeData = chaincodeData;
    }

    @GetMapping
    public RestResp list() {
        return success(userContext()
          .map(u -> newArrayList(invoiceRepo.findInvoicesByOwnerOrOriginOrTarget(u.getName(), u.getId(), u.getId())))
          .orElse(new ArrayList<>()));
    }

    @GetMapping("/{serial}")
    public RestResp get(@PathVariable String serial) {
        return success("TODO");
    }

    @PostMapping
    public RestResp issue(@RequestBody InvoiceWithGoods invoiceWithGoods) {
        return success("TODO");
    }

    @PutMapping
    public RestResp transfer() {
        return success("TODO");
    }

    @GetMapping("/{serial}/history")
    public RestResp invoiceHistory(@PathVariable String serial) {
        return success("oxchains");
    }

}
