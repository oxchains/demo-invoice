package oxchains.invoice.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import oxchains.invoice.data.UserRepo;
import oxchains.invoice.domain.User;
import oxchains.invoice.rest.domain.RestResp;

import static oxchains.invoice.rest.domain.RestResp.fail;
import static oxchains.invoice.rest.domain.RestResp.success;

/**
 * @author aiet
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    private UserRepo userRepo;

    @Autowired
    public UserController(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @PostMapping
    public RestResp registerUser(@RequestBody User user) {
        return userRepo
          .findByName(user.getName())
          .map(u -> fail())
          .orElseGet(() -> {
              User u = userRepo.save(user);
              LOG.info("{} registered", u);
              return success(null);
          });
    }

}
