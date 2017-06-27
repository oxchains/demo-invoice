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
@RequestMapping("/user")
public class UserController {

    @PostMapping
    public RestResp registerUser(@RequestBody User user){
        //check user's existence and enroll
        return success("TODO");
    }

}
