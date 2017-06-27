package oxchains.invoice.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import oxchains.invoice.auth.JwtService;
import oxchains.invoice.data.CompanyUserRepo;
import oxchains.invoice.data.UserRepo;
import oxchains.invoice.data.UserTokenRepo;
import oxchains.invoice.domain.CompanyUser;
import oxchains.invoice.domain.UserToken;
import oxchains.invoice.rest.domain.NameAndPass;
import oxchains.invoice.rest.domain.RestResp;

import static oxchains.invoice.rest.domain.RestResp.fail;
import static oxchains.invoice.rest.domain.RestResp.success;

/**
 * @author aiet
 */
@RestController
@RequestMapping("/token")
public class TokenController {

    private UserRepo userRepo;
    private CompanyUserRepo companyUserRepo;
    private UserTokenRepo userTokenRepo;
    private JwtService jwtService;

    public TokenController(@Autowired CompanyUserRepo companyUserRepo, @Autowired UserRepo userRepo, @Autowired UserTokenRepo userTokenRepo, @Autowired JwtService jwtService) {
        this.userRepo = userRepo;
        this.companyUserRepo = companyUserRepo;
        this.userTokenRepo = userTokenRepo;
        this.jwtService = jwtService;
    }

    @PostMapping
    public RestResp enroll(@RequestBody NameAndPass user) {
        return (user.isBiz() ? companyUserRepo
          .findByNameAndPassword(user.getUsername(), user.getPassword())
          .map(CompanyUser::toUser) : userRepo.findByNameAndPassword(user.getUsername(), user.getPassword()))
          .map(u -> {
              UserToken userToken = new UserToken(jwtService.generate(u, user.isBiz()));
              return success(userTokenRepo.save(userToken));
          })
          .orElse(fail());
    }
}
