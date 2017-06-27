package oxchains.invoice.rest;

import org.springframework.web.bind.annotation.*;
import oxchains.invoice.rest.domain.RestResp;

import static oxchains.invoice.rest.domain.RestResp.success;

/**
 * @author aiet
 */
@RestController
@RequestMapping("/reimbursement")
public class ReimbursementController {

    @GetMapping
    public RestResp listRI() {
        //get user/company info from token and get list respectively
        return success("TODO");
    }

    @PostMapping
    public RestResp requestRI(@RequestParam String[] invoices) {
        //fetch invoices and create reimbursement in batch
        return success("TODO");
    }

    @PutMapping
    public RestResp updateRI(@RequestParam String[] ids, @RequestParam int action) {
        //update reimbursement of id
        return success("TODO");
    }

}
