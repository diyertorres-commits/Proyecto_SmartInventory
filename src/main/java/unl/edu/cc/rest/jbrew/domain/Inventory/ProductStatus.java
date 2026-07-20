package unl.edu.cc.rest.jbrew.domain.Inventory;

public enum ProductStatus {
    DISPONIBLE("Disponible"),
    STOCK_BAJO("Stock Bajo"),
    AGOTADO("Agotado");

    private final String texto;

    ProductStatus(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }

    public String getCssClass() {
        switch (this) {
            case DISPONIBLE:
                return "status-available";
            case STOCK_BAJO:
                return "status-low";
            case AGOTADO:
                return "status-out";
            default:
                return "";
        }
    }
}
