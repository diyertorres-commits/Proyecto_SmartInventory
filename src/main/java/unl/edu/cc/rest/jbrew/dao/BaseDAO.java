package unl.edu.cc.rest.jbrew.dao;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public abstract class BaseDAO<T> {

    protected EntityManager em;

    private final Class<T> entityClass;

    public BaseDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected void setEntityManager(EntityManager em) {
        this.em = em;
    }

    protected Class<T> getEntityClass() {
        return entityClass;
    }

    public T findById(Long id) {
        return em.find(entityClass, id);
    }

    public Optional<T> findByIdOptional(Long id) {
        return Optional.ofNullable(findById(id));
    }

    public List<T> findAll() {
        String entityName = entityClass.getSimpleName();
        return em.createQuery("SELECT e FROM " + entityName + " e", entityClass).getResultList();
    }

    public T save(T entity) {
        try {
            // Verificar si la entidad ya tiene ID usando reflexión (buscando en jerarquía de clases)
            java.lang.reflect.Field idField = findIdField(entity.getClass());
            if (idField != null) {
                idField.setAccessible(true);
                Object idValue = idField.get(entity);
                
                if (idValue != null) {
                    // Si tiene ID, usar merge (actualización)
                    return em.merge(entity);
                }
            }
            
            // Si no tiene ID, usar persist (nueva entidad)
            em.persist(entity);
            em.flush();
            em.refresh(entity);
            return entity;
        } catch (jakarta.persistence.EntityExistsException e) {
            return em.merge(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar entidad", e);
        }
    }

    private java.lang.reflect.Field findIdField(Class<?> clazz) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField("id");
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    public void delete(T entity) {
        try {
            java.lang.reflect.Field idField = findIdField(entity.getClass());
            if (idField != null) {
                idField.setAccessible(true);
                Object idValue = idField.get(entity);
                
                T managed = em.contains(entity) ? entity : em.find(entityClass, idValue);
                if (managed != null) {
                    em.remove(managed);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar entidad", e);
        }
    }

    public void deleteById(Long id) {
        T entity = findById(id);
        if (entity != null) {
            delete(entity);
        }
    }

    public long count() {
        String entityName = entityClass.getSimpleName();
        return em.createQuery("SELECT COUNT(e) FROM " + entityName + " e", Long.class).getSingleResult();
    }

    protected Optional<T> findSingleResult(String query, String paramName, Object paramValue) {
        List<T> results = em.createQuery(query, entityClass)
                .setParameter(paramName, paramValue)
                .getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    protected List<T> findList(String query, String paramName, Object paramValue) {
        return em.createQuery(query, entityClass)
                .setParameter(paramName, paramValue)
                .getResultList();
    }
}
