package com.oxchains.billing.rest.common;

/**
 * @author aiet
 */
public class EndorseAction extends PromptAction{

    private String endorser;
    private String endorsee;

    public String getEndorser() {
        return endorser;
    }

    public void setEndorser(String endorser) {
        this.endorser = endorser;
    }

    public String getEndorsee() {
        return endorsee;
    }

    public void setEndorsee(String endorsee) {
        this.endorsee = endorsee;
    }


}
