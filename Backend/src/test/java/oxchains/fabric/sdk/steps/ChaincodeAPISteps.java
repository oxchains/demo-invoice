package oxchains.fabric.sdk.steps;

import net.thucydides.core.annotations.Step;
import org.hyperledger.fabric.sdk.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import oxchains.MgtConsoleApplication;
import oxchains.fabric.sdk.FabricSDK;

import java.io.File;
import java.util.Optional;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hyperledger.fabric.sdk.ChainCodeResponse.Status.FAILURE;
import static org.hyperledger.fabric.sdk.ChainCodeResponse.Status.SUCCESS;
import static org.junit.Assert.*;

/**
 * @author aiet
 */
@ContextConfiguration(classes = MgtConsoleApplication.class)
@TestPropertySource(locations = "classpath:test.properties")
public class ChaincodeAPISteps {

    @Autowired private FabricSDK fabricSDK;
    private ProposalResponse installResponse;
    private ProposalResponse instantiateResponse;
    private ProposalResponse invokeResponse;
    private ProposalResponse queryResponse;
    private ChainCodeID chaincode;
    private Chain chain;

    @Step("load chaincode from path {0}")
    public void loadChaincode(String chaincodeName, String chaincodeVersion, String chaincodeLocation) {
        chaincode = ChainCodeID
          .newBuilder()
          .setName(chaincodeName)
          .setVersion(chaincodeVersion)
          .setPath(chaincodeLocation)
          .build();
    }

    @Step("install chaincode on chain {0}")
    public void installChaincodeOnPeer(String chainName) {
        checkChain(chainName);
        installResponse = fabricSDK.installChaincodeOnPeer(chaincode, chain, chain
          .getPeers()
          .iterator()
          .next(), "src/test/resources/chaincode");
        assertNotNull(installResponse);
    }

    @Step("chaincode installed successfully")
    public void installationSucceeded() {
        assertEquals(SUCCESS, installResponse.getStatus());
    }

    @Step("instantiate chaincode on chain {0} with {1}")
    public void instantiateWith(String chaincodeName, String arg) throws Exception {
        Optional<ChainCodeID> chaincodeIdOptional = fabricSDK.getChaincode(chaincodeName);
        assertTrue("chaincode should have been created", chaincodeIdOptional.isPresent());
        chaincode = chaincodeIdOptional.get();

        ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
        chaincodeEndorsementPolicy.fromYamlFile(new File("src/test/resources/chain_configuration/" + chaincodeName + "_endorsement_policy.yaml"));

        instantiateResponse = fabricSDK
          .instantiateChaincode(chaincode, chain, chain
            .getPeers()
            .iterator()
            .next(), chaincodeEndorsementPolicy, arg.split(" "))
          .get(60, SECONDS);
        assertNotNull(instantiateResponse);
    }

    @Step("chaincode instantiated successfully")
    public void instantiationSucceeded() {
        assertEquals(SUCCESS, instantiateResponse.getStatus());
    }

    @Step("query on chain with {0}")
    public void queryChainWith(String arg) {
        assertNotNull("chain should have been created", chain);
        assertNotNull("chaincode should have been created", chaincode);
        queryResponse = fabricSDK.queryChaincode(chaincode, chain, chain
          .getPeers()
          .iterator()
          .next(), arg.split(" "));
        assertNotNull(queryResponse);
    }

    @Step("query result: {0}")
    public void resultIs(int result) {
        assertEquals(SUCCESS, queryResponse.getStatus());
        String responseResult = queryResponse
          .getProposalResponse()
          .getResponse()
          .getPayload()
          .toStringUtf8();
        assertEquals("" + result, responseResult);
    }

    @Step("query result contains: {0}")
    public void resultContains(String invoiceId) {
        assertEquals(SUCCESS, queryResponse.getStatus());
        String responseResult = queryResponse
          .getProposalResponse()
          .getResponse()
          .getPayload()
          .toStringUtf8();
        assertThat(responseResult, containsString(invoiceId));
    }

    @Step("query result does not contain: {0}")
    public void resultContainsNo(String invoiceId) {
        assertEquals(SUCCESS, queryResponse.getStatus());
        String responseResult = queryResponse
          .getProposalResponse()
          .getResponse()
          .getPayload()
          .toStringUtf8();
        assertThat(responseResult, not(containsString(invoiceId)));
    }

    @Step("invoke chain with arg {0}")
    public void invokeChainWith(String arg) throws Exception {
        assertNotNull("chain should have been created", chain);
        assertNotNull("chaincode should have been created", chaincode);
        invokeResponse = fabricSDK
          .invokeChaincode(chaincode, chain, chain
            .getPeers()
            .iterator()
            .next(), arg.split(" "))
          .get(60, SECONDS);
        assertNotNull(invokeResponse);
    }

    @Step("chain invocation succceeded")
    public void invokeSucceeded() {
        assertEquals(SUCCESS, invokeResponse.getStatus());
    }

    @Step("chain {0} listens on event hub at {2}")
    public void chainWithEventHub(String eventName, String eventEndpoint) throws Exception {
        Optional<EventHub> eventHubOptional = fabricSDK.withEventHub(eventName, eventEndpoint);
        assertTrue(eventHubOptional.isPresent());

        /* append event hub and re-initialize */
        chain.addEventHub(eventHubOptional.get());
        chain.initialize();
    }

    @Step("check chain {0} existence")
    public void checkChain(String chainName) {
        Optional<Chain> chainOptional = fabricSDK.getChain(chainName);
        assertTrue("chain should have been created", chainOptional.isPresent());
        /* append event hub and re-initialize */
        chain = chainOptional.get();
    }

    @Step("check chain {0} existence")
    public void checkChaincode(String chaincodeName) {
        Optional<ChainCodeID> chaincodeOptional = fabricSDK.getChaincode(chaincodeName);
        assertTrue("chaincode should have been created", chaincodeOptional.isPresent());
        /* append event hub and re-initialize */
        chaincode = chaincodeOptional.get();
    }

    @Step("chain invocation failed")
    public void invokeFailed() {
        assertEquals(FAILURE, invokeResponse.getStatus());
    }

    @Step("chain query failed")
    public void queryFail() {
        assertEquals(FAILURE, queryResponse.getStatus());
    }

}
