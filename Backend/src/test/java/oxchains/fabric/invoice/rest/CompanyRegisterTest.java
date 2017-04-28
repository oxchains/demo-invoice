package oxchains.fabric.invoice.rest;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import net.serenitybdd.jbehave.SerenityStory;
import org.jbehave.core.annotations.BeforeStory;
import oxchains.invoice.rest.CompanyController;

/**
 * @author aiet
 */
public class CompanyRegisterTest extends SerenityStory {

    @BeforeStory
    public void init() {
        RestAssuredMockMvc.standaloneSetup(new CompanyController());
    }

}
