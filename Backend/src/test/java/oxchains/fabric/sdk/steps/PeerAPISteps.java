package oxchains.fabric.sdk.steps;

import net.thucydides.core.annotations.Step;
import org.hyperledger.fabric.sdk.Peer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import oxchains.MgtConsoleApplication;
import oxchains.fabric.sdk.FabricSDK;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @author aiet
 */
@ContextConfiguration(classes = MgtConsoleApplication.class)
@TestPropertySource(locations = "classpath:test.properties")
public class PeerAPISteps {

    @Autowired FabricSDK fabricSDK;

    @Step("with peer {0} at {1}")
    public void peerCreated(String peerId, String peerEndpoint) {
        Optional<Peer> peerOptional = fabricSDK.withPeer(peerId, peerEndpoint);
        assertTrue(peerOptional.isPresent());
    }

    @Step("peer {0} joins chain {1}")
    public void peerJoinChain(String peerId, String chainName) {
        fabricSDK.joinChain(peerId, chainName);
    }

    @Step("peer {0} joined chain {1}")
    public void peerHasJoinedChain(String peerId, String chainName) {
        assertNotNull("peer id needed", peerId);
        assertNotNull("chain needed", chainName);
        List<Peer> peers = fabricSDK.chainPeers(chainName);
        Optional<Peer> peerOptional = peers
          .stream()
          .filter(p -> peerId.equals(p.getName()))
          .findFirst();
        assertTrue(peerOptional.isPresent());
    }

    @Step("peer {0} did not join chain {1}")
    public void peerShoudNotJoinChain(String peerId, String chainName) {
        assertNotNull("chain needed", chainName);
        List<Peer> peers = fabricSDK.chainPeers(chainName);
        Optional<Peer> peerOptional = peers
          .stream()
          .filter(p -> peerId.equals(p.getName()))
          .findFirst();
        assertFalse(peerOptional.isPresent());
    }
}
