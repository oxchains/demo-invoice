package oxchains.invoice.domain;

import com.fasterxml.jackson.annotation.JsonGetter;

/**
 * @author aiet
 */
public interface ICompany {

    String getName();

    String getTaxIdentifier();

    String getAddress();

    String getBankName();

    String getBankAccount();

}
