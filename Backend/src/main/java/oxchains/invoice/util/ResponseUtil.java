package oxchains.invoice.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oxchains.invoice.domain.Invoice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static oxchains.invoice.domain.Invoice.fromPayload;

/**
 * @author aiet
 */
public class ResponseUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseUtil.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static Optional<String> extract(String json, String path) {
        try {
            JsonNode root = OBJECT_MAPPER.readTree(json);
            JsonNode data = root.at(path);
            return Optional.ofNullable(data.isObject() ? data.toString() : data.textValue());
        } catch (Exception e) {
            LOG.error("failed to extract value under path {} out of {}: {}", path, json, e.getMessage());
        }
        return empty();
    }

    public static <T> T resolve(String json, Class<T> tClass) {
        try {
            return OBJECT_MAPPER.readValue(json, tClass);
        } catch (IOException e) {
            LOG.error("failed to resolve data to {}: {}, cause: {}", tClass, json, e.getMessage());
        }
        return null;
    }

    public static List<Invoice> parseInvoicePayload(String payload){
        if(isBlank(payload)) return emptyList();
        String[] invoiceStrs = payload.split(";");
        List<Invoice> invoices = new ArrayList<>(invoiceStrs.length);
        for(String invoiceStr : invoiceStrs){
            String[] invoiceAttrs = invoiceStr.split(",");
            invoices.add(fromPayload(invoiceAttrs[2], invoiceAttrs[1]));
        }
        return invoices;
    }
}
