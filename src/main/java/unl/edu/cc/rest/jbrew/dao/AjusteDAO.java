package unl.edu.cc.rest.jbrew.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import unl.edu.cc.rest.jbrew.domain.Ajuste;

import java.util.List;

@Stateless
public class AjusteDAO extends BaseDAO<Ajuste> {

    @PersistenceContext
    private EntityManager em;

    public AjusteDAO() {
        super(Ajuste.class);
    }

    @jakarta.annotation.PostConstruct
    public void init() {
        super.setEntityManager(em);
    }

    public List<Ajuste> findAllOrderByDateDesc() {
        return em.createQuery(
            "SELECT a FROM Ajuste a ORDER BY a.fecha DESC", Ajuste.class)
            .getResultList();
    }

    public List<Ajuste> findByProducto(String productoNombre) {
        return findList(
            "SELECT a FROM Ajuste a WHERE a.productoNombre = :productoNombre",
            "productoNombre", productoNombre);
    }

    public List<Ajuste> findByTipoAjuste(String tipoAjuste) {
        return findList(
            "SELECT a FROM Ajuste a WHERE a.tipoAjuste = :tipoAjuste",
            "tipoAjuste", tipoAjuste);
    }

    public Ajuste findLastAjuste() {
        List<Ajuste> ajustes = em.createQuery(
            "SELECT a FROM Ajuste a ORDER BY a.id DESC", Ajuste.class)
            .setMaxResults(1)
            .getResultList();
        return ajustes.isEmpty() ? null : ajustes.get(0);
    }
}
