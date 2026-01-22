package rest.service.fne;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;

/**
 *
 * @author koben
 */
public class FneInvoice {

    // ✅ plus final : valeurs par défaut, mais modifiables selon B2B/B2C
    private String invoiceType = "sale";
    private String paymentMethod = "check";
    private String template = "B2B";

    // ✅ optionnel (obligatoire uniquement en B2B => contrôlé dans le service)
    private String clientNcc;

    @NotNull
    private String clientCompanyName;

    @NotNull
    private String clientPhone;

    @NotNull
    private String clientEmail;

    @NotNull
    private String pointOfSale;

    @NotNull
    private String establishment;

    private String commercialMessage;

    private String footer;

    private List<FneInvoiceItem> items = new ArrayList<>();

    // ======= Getters/Setters =======

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getClientNcc() {
        return clientNcc;
    }

    public void setClientNcc(String clientNcc) {
        this.clientNcc = clientNcc;
    }

    public String getClientCompanyName() {
        return clientCompanyName;
    }

    public void setClientCompanyName(String clientCompanyName) {
        this.clientCompanyName = clientCompanyName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getPointOfSale() {
        return pointOfSale;
    }

    public void setPointOfSale(String pointOfSale) {
        this.pointOfSale = pointOfSale;
    }

    public String getEstablishment() {
        return establishment;
    }

    public void setEstablishment(String establishment) {
        this.establishment = establishment;
    }

    public String getCommercialMessage() {
        return commercialMessage;
    }

    public void setCommercialMessage(String commercialMessage) {
        this.commercialMessage = commercialMessage;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public List<FneInvoiceItem> getItems() {
        return items;
    }

    public void setItems(List<FneInvoiceItem> items) {
        this.items = items;
    }

}
