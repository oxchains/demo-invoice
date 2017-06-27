package oxchains.invoice.rest;

import org.springframework.web.bind.annotation.*;
import oxchains.invoice.domain.Goods;
import oxchains.invoice.rest.domain.InvoiceWithGoods;
import oxchains.invoice.rest.domain.RestResp;

import static oxchains.invoice.rest.domain.RestResp.success;

/**
 * @author aiet
 */
@RestController
@RequestMapping("/invoice")
public class InvoiceController {

    @GetMapping
    public RestResp list() {
        //get user/company info from token and get list respectively
        return success("TODO");
    }

    @GetMapping("/{serial}")
    public RestResp get(@PathVariable String serial){
        return success("TODO");
    }

    @PostMapping
    public RestResp issue(@RequestBody InvoiceWithGoods invoiceWithGoods){
        return success("TODO");
    }

    @PutMapping
    public RestResp transfer(){
        return success("TODO");
    }

    @GetMapping("/{serial}/history")
    public RestResp invoiceHistory(@PathVariable String serial){
        return success("oxchains");
    }

}
