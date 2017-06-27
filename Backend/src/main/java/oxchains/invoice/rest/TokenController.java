package oxchains.invoice.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import oxchains.invoice.domain.User;
import oxchains.invoice.rest.domain.RestResp;

import static oxchains.invoice.rest.domain.RestResp.success;

/**
 * @author aiet
 */
@RestController
@RequestMapping("/token")
public class TokenController {

    @PostMapping
    public RestResp enroll(@RequestBody User user) {
        //find user, create token and enroll
        return success("TODO");
    }
}
