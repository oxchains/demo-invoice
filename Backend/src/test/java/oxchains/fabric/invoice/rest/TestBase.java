package oxchains.fabric.invoice.rest;

import net.serenitybdd.jbehave.SerenityStory;
import org.jbehave.core.annotations.BeforeStories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;
import oxchains.EInvoiceApplication;

import javax.servlet.Filter;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.mockMvc;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author aiet
 */
@ContextConfiguration(classes = EInvoiceApplication.class)
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test.properties")
public class TestBase extends SerenityStory {

    @Autowired private WebApplicationContext context;

    @Autowired private Filter springSecurityFilterChain;

    @BeforeStories
    public void init() {
        mockMvc(webAppContextSetup(context)
          .addFilters(springSecurityFilterChain)
          .build());
    }

}
