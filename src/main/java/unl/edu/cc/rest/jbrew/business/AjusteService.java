package unl.edu.cc.rest.jbrew.business;

import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Singleton
public class AjusteService {

    private final List<Ajuste> ajustes = new ArrayList<>();
    private int contadorAjustes = 1;

    @Lock(LockType.WRITE)
    public Ajuste registrarAjuste(String productoNombre, String tipoAjuste, String operacion,
                                  int cantidad, int stockAnterior, int stockNuevo,
                                  String motivo, String responsable) {
        Ajuste ajuste = new Ajuste(
                contadorAjustes++,
                new Date(),
                productoNombre,
                tipoAjuste,
                operacion,
                cantidad,
                stockAnterior,
                stockNuevo,
                motivo,
                responsable
        );
        ajustes.add(ajuste);
        return ajuste;
    }

    @Lock(LockType.WRITE)
    public void eliminarAjuste(Ajuste ajuste) {
        ajustes.remove(ajuste);
    }

    @Lock(LockType.READ)
    public List<Ajuste> obtenerAjustes() {
        return new ArrayList<>(ajustes);
    }

    public static class Ajuste implements Serializable {
        private final int id;
        private final Date fecha;
        private final String productoNombre;
        private final String tipoAjuste;
        private final String operacion;
        private final int cantidad;
        private final int stockAnterior;
        private final int stockNuevo;
        private final String motivo;
        private final String responsable;

        private final transient SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        public Ajuste(int id, Date fecha, String productoNombre, String tipoAjuste, String operacion,
                      int cantidad, int stockAnterior, int stockNuevo, String motivo, String responsable) {
            this.id = id;
            this.fecha = fecha;
            this.productoNombre = productoNombre;
            this.tipoAjuste = tipoAjuste;
            this.operacion = operacion;
            this.cantidad = cantidad;
            this.stockAnterior = stockAnterior;
            this.stockNuevo = stockNuevo;
            this.motivo = motivo;
            this.responsable = responsable;
        }

        public String getFechaTexto() {
            return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(fecha);
        }

        public String getTipoCss() {
            switch (tipoAjuste) {
                case "merma":
                case "robo":
                case "dano":
                case "vencimiento":
                    return "status-agotado";
                case "error":
                case "manual":
                    return "status-bajo";
                default:
                    return "status-disponible";
            }
        }

        // Getters
        public int getId() { return id; }
        public Date getFecha() { return fecha; }
        public String getProductoNombre() { return productoNombre; }
        public String getTipoAjuste() { return tipoAjuste; }
        public String getOperacion() { return operacion; }
        public int getCantidad() { return cantidad; }
        public int getStockAnterior() { return stockAnterior; }
        public int getStockNuevo() { return stockNuevo; }
        public String getMotivo() { return motivo; }
        public String getResponsable() { return responsable; }
    }
}
