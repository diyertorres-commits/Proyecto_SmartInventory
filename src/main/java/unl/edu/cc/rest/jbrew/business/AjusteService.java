package unl.edu.cc.rest.jbrew.business;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.cc.rest.jbrew.business.service.CrudGenericService;
import unl.edu.cc.rest.jbrew.domain.Ajuste;

import java.util.List;
import java.util.Date;
import java.util.logging.Logger;

@Stateless
public class AjusteService {

    private static final Logger LOGGER = Logger.getLogger(AjusteService.class.getName());

    @Inject
    private CrudGenericService crudGenericService;

    public Ajuste registrarAjuste(String productoNombre, String tipoAjuste, String operacion,
                                  int cantidad, int stockAnterior, int stockNuevo,
                                  String motivo, String responsable) {
        Ajuste ajuste = new Ajuste(
                getNextAjusteId(),
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
        crudGenericService.create(ajuste);
        return ajuste;
    }

    public void eliminarAjuste(Ajuste ajuste) {
        crudGenericService.delete(Ajuste.class, ajuste.getId());
    }

    public List<Ajuste> obtenerAjustes() {
        return crudGenericService.findWithQuery("SELECT a FROM Ajuste a ORDER BY a.fecha DESC");
    }

    private int getNextAjusteId() {
        // Obtener el último ID de ajuste de la base de datos
        List<Ajuste> ajustes = crudGenericService.findWithQuery("SELECT a FROM Ajuste a ORDER BY a.id DESC");
        if (ajustes.isEmpty()) {
            return 1;
        }
        return ajustes.get(0).getIdAjuste() + 1;
    }
}
