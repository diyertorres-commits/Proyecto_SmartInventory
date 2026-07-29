package unl.edu.cc.rest.jbrew.business;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.cc.rest.jbrew.dao.AjusteDAO;
import unl.edu.cc.rest.jbrew.domain.Ajuste;

import java.util.List;
import java.util.Date;
import java.util.logging.Logger;

@Stateless
public class AjusteService {

    private static final Logger LOGGER = Logger.getLogger(AjusteService.class.getName());

    @Inject
    private AjusteDAO ajusteDAO;

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
        ajusteDAO.save(ajuste);
        return ajuste;
    }

    public void eliminarAjuste(Ajuste ajuste) {
        ajusteDAO.delete(ajuste);
    }

    public List<Ajuste> obtenerAjustes() {
        return ajusteDAO.findAllOrderByDateDesc();
    }

    private int getNextAjusteId() {
        // Obtener el último ID de ajuste de la base de datos
        Ajuste lastAjuste = ajusteDAO.findLastAjuste();
        if (lastAjuste == null) {
            return 1;
        }
        return lastAjuste.getIdAjuste() + 1;
    }
}
