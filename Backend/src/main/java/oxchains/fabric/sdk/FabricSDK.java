package oxchains.fabric.sdk;

import com.google.common.collect.Lists;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.hyperledger.fabric_ca.sdk.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import oxchains.fabric.sdk.domain.CAAdmin;
import oxchains.fabric.sdk.domain.FabricUser;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static java.util.Collections.singletonList;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.hyperledger.fabric.sdk.ChainCodeResponse.Status.SUCCESS;
import static org.hyperledger.fabric.sdk.TransactionRequest.Type.GO_LANG;
import static org.hyperledger.fabric.sdk.security.CryptoSuite.Factory.getCryptoSuite;

/**
 * @author aiet
 */
@Component
public class FabricSDK {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final String caServerUrl;
    private final Properties properties = new Properties();
    private final String[] EMPTY_ARGS = new String[]{};

    public FabricSDK(@Value("${fabric.ca.server.url}") String caServerUrl, Properties properties) {
        this.caServerUrl = caServerUrl;
        this.properties.putAll(properties);
    }

    private static final WeakHashMap<String, Peer> PEER_CACHE = new WeakHashMap<>(8);
    private static final WeakHashMap<String, ChainCodeID> CHAINCODE_CACHE = new WeakHashMap<>(8);

    private final HFClient fabricClient = HFClient.createNewInstance();
    private HFCAClient caClient;
    private CAAdmin caServerAdminUser;

    @Value("${fabric.ca.server.admin}") private String caServerAdmin;
    @Value("${fabric.ca.server.admin.pass}") private String caServerAdminPass;
    @Value("${fabric.ca.server.admin.mspid}") private String caServerAdminMSPId;
    @Value("${fabric.ca.server.admin.affiliation}") private String caServerAdminAffiliation;

    @PostConstruct
    private void init() {
        try {
            caClient = new HFCAClient(caServerUrl, properties);
            caClient.setCryptoSuite(getCryptoSuite());
            fabricClient.setCryptoSuite(getCryptoSuite());
            this.caServerAdminUser = new CAAdmin(caServerAdmin, caServerAdminAffiliation, caServerAdminMSPId);
            enroll(caServerAdminUser);
            fabricClient.setUserContext(caServerAdminUser);
        } catch (MalformedURLException e) {
            LOG.error("failed to create CA client with url {} and properties {}", caServerUrl, properties, e);
        } catch (InvalidArgumentException | CryptoException e) {
            LOG.error("failed to enable encryption for fabric client", e);
        } catch (BaseException e) {
            LOG.error("failed to enroll admin user: ", e);
        }
    }

    /**
     * the user will be enrolled if hasn't
     */
    private void enroll(CAAdmin userToEnroll) throws BaseException {
        //TODO check if enrolled yet
        userToEnroll.setEnrollment(caClient.enroll(userToEnroll.getName(), caServerAdminPass));
    }

    public void startPeer(String peerId) {
        //TODO
    }

    public void stopPeer(String peerId) {
        //TODO
    }

    public Optional<Peer> withPeer(String peerId, String peerUrl) {
        Peer peer = null;
        try {
            peer = fabricClient.newPeer(peerId, peerUrl);
            PEER_CACHE.putIfAbsent(peerId, peer);
        } catch (Exception e) {
            LOG.error("failed to create peer {} on {}: ", peerId, peerUrl, e);
        }
        return Optional.ofNullable(peer);
    }

    public Optional<FabricUser> createUser(String username, String affiliation) {
        FabricUser user = null;
        try {
            user = new FabricUser(username, affiliation);
            RegistrationRequest registrationRequest = new RegistrationRequest(username, affiliation);
            user.setEnrollmentSecret(caClient.register(registrationRequest, caServerAdminUser));
            //TODO when to enroll? difference to register?
            caClient.enroll(username, user.getEnrollmentSecret());
            user.setMspId(caServerAdminMSPId);
        } catch (Exception e) {
            LOG.error("failed to register fabric user {} from {}", username, affiliation);
        }
        return Optional.ofNullable(user);
    }

    public Optional<Orderer> withOrderer(String ordererName, String ordererUrl) {
        Orderer orderer = null;
        try {
            orderer = fabricClient.newOrderer(ordererName, ordererUrl);
        } catch (Exception e) {
            LOG.error("failed to create orderer {} on {}", ordererName, ordererUrl, e);
        }
        return Optional.ofNullable(orderer);
    }

    public Optional<EventHub> withEventHub(String eventName, String eventEndpoint) {
        EventHub eventHub = null;
        try {
            eventHub = fabricClient.newEventHub(eventName, eventEndpoint);
        } catch (Exception e) {
            LOG.error("failed to create event hub at {}", eventEndpoint, e);
        }
        return Optional.ofNullable(eventHub);
    }

    public Optional<Chain> constructChain(String chainName, Orderer orderer, String chainConfigurationFilePath) {
        Optional<Chain> chain = null;
        try {
            chain = constructChain(chainName, orderer, new ChainConfiguration(new File(chainConfigurationFilePath)));
        } catch (IOException e) {
            LOG.error("failed to read chain configuration file {}", chainConfigurationFilePath, e);
        }
        return chain;
    }

    public Optional<Chain> constructChain(String chainName, Orderer orderer, ChainConfiguration chainConfiguration) {
        Chain chain = null;
        try {
            chain = fabricClient.newChain(chainName, orderer, chainConfiguration);
        } catch (TransactionException | InvalidArgumentException e) {
            LOG.error("failed to construct new chain {} with orderer {} and configuration {}", chainName, orderer.getName(), e);
        }
        return Optional.ofNullable(chain);
    }

    public Optional<Chain> getChain(String chainName) {
        final Chain cachedChain = fabricClient.getChain(chainName);
        return Optional.ofNullable(cachedChain);
    }

    public Optional<Peer> getPeer(String peerId) {
        return Optional.ofNullable(PEER_CACHE.get(peerId));
    }

    public void joinChain(String peerId, String chainName) {
        try {
            Chain chain = fabricClient.getChain(chainName);
            chain.joinPeer(PEER_CACHE.get(peerId));
            chain.initialize();
        } catch (Exception e) {
            LOG.error("{} failed to join chain {}: ", peerId, chainName, e);
        }
    }

    public List<Peer> chainPeers(String channelName) {
        return Lists.newCopyOnWriteArrayList(fabricClient
          .getChain(channelName)
          .getPeers());
    }

    public ProposalResponse installChaincodeOnPeer(ChainCodeID chaincodeId, Chain chain, Peer peer, String sourceLocation) {
        InstallProposalRequest installProposalRequest = fabricClient.newInstallProposalRequest();
        installProposalRequest.setChaincodeID(chaincodeId);
        try {
            installProposalRequest.setChaincodeSourceLocation(new File(sourceLocation));
            installProposalRequest.setChaincodeLanguage(GO_LANG);
            Collection<ProposalResponse> responses = chain.sendInstallProposal(installProposalRequest, singletonList(peer));
            if (responses.isEmpty()) {
                LOG.warn("no response while installing chaincode {}", chaincodeId.getName());
            } else {
                CHAINCODE_CACHE.putIfAbsent(chaincodeId.getName(), chaincodeId);
                return responses
                  .iterator()
                  .next();
            }
        } catch (Exception e) {
            LOG.error("failed to install chaincode {} on peer {} for chain {}", chaincodeId.getName(), peer.getName(), chain.getName(), e);
        }
        return null;
    }


    public CompletableFuture<ProposalResponse> instantiateChaincode(ChainCodeID chaincode, Chain chain, Peer peer, final String... args) {
        return instantiateChaincode(chaincode, chain, peer, null, args);
    }

    public CompletableFuture<ProposalResponse> instantiateChaincode(ChainCodeID chaincode, Chain chain, Peer peer, ChaincodeEndorsementPolicy policy, final String... args) {
        InstantiateProposalRequest instantiateProposalRequest = fabricClient.newInstantiationProposalRequest();
        instantiateProposalRequest.setChaincodeID(chaincode);
        if(args.length>0) instantiateProposalRequest.setFcn(args[0]);
        if(args.length>1) instantiateProposalRequest.setArgs(Arrays.copyOfRange(args, 1, args.length));
        else instantiateProposalRequest.setArgs(EMPTY_ARGS);
        instantiateProposalRequest.setChaincodeEndorsementPolicy(policy);
        try {
            Collection<ProposalResponse> responses = chain.sendInstantiationProposal(instantiateProposalRequest, singletonList(peer));
            if (responses.isEmpty()) {
                LOG.warn("no responses while instantiating chaincode {}", chaincode.getName());
            } else {
                final ProposalResponse response = responses.iterator().next();
                if (response.getStatus() == SUCCESS) {
                    return chain
                      .sendTransaction(responses, chain.getOrderers())
                      .thenApply(transactionEvent -> {
                          LOG.info("instantiation {} : transaction {} finished", Arrays.toString(args), transactionEvent.getTransactionID());
                          return response;
                      });
                } else supplyAsync(() -> response);
            }
        } catch (Exception e) {
            LOG.error("failed to instantiate chaincode {} with arg {}", chaincode.getName(), Arrays.toString(args), e);
        }
        return null;
    }

    public Optional<ChainCodeID> getChaincode(String chaincodeName) {
        return Optional.ofNullable(CHAINCODE_CACHE.get(chaincodeName));
    }

    public CompletableFuture<ProposalResponse> invokeChaincode(ChainCodeID chaincode, Chain chain, Peer peer, String... args) {
        TransactionProposalRequest transactionProposalRequest = fabricClient.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(chaincode);
        if(args.length>0) transactionProposalRequest.setFcn(args[0]);
        if(args.length>1) transactionProposalRequest.setArgs(Arrays.copyOfRange(args, 1, args.length));
        else transactionProposalRequest.setArgs(EMPTY_ARGS);

        transactionProposalRequest.setProposalWaitTime(30000);
        try {
            Collection<ProposalResponse> responses = chain.sendTransactionProposal(transactionProposalRequest, singletonList(peer));
            if (responses.isEmpty()) {
                LOG.warn("no responses while invoking chaincode {}", chaincode.getName());
            } else {
                final ProposalResponse response = responses
                  .iterator()
                  .next();
                if (response.getStatus() == SUCCESS) {
                    return chain
                      .sendTransaction(responses)
                      .thenApply(transactionEvent -> {
                          LOG.info("invoking {} : transaction {} finished", Arrays.toString(args), transactionEvent.getTransactionID());
                          return response;
                      });
                } else return supplyAsync(() -> response);
            }
        } catch (Exception e) {
            LOG.error("failed to invoke chaincode {} with arg {}", chaincode.getName(), Arrays.toString(args));
        }
        return null;
    }


    public ProposalResponse queryChaincode(ChainCodeID chaincode, Chain chain, Peer peer, String... args) {
        QueryByChaincodeRequest queryByChaincodeRequest = fabricClient.newQueryProposalRequest();
        queryByChaincodeRequest.setChaincodeID(chaincode);
        if(args.length>0) queryByChaincodeRequest.setFcn(args[0]);
        if(args.length>1) queryByChaincodeRequest.setArgs(Arrays.copyOfRange(args, 1, args.length));
        else queryByChaincodeRequest.setArgs(EMPTY_ARGS);

        try{
            Collection<ProposalResponse> responses = chain.queryByChaincode(queryByChaincodeRequest, singletonList(peer));
            if(responses.isEmpty()){
                LOG.warn("no response while querying chaincode {}", chaincode.getName());
            }else return responses.iterator().next();
        } catch (Exception e) {
            LOG.error("failed to query chaincode {} with arg {}", chaincode.getName(), Arrays.toString(args));
        }
        return null;
    }
}
