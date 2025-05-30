package com.viettel.spring.cloud.server.helper;

import java.util.List;

import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;

@Component
public class CriteriaQueryHelper {
    public <T> List<T> findByJoinFilters(EntityManager em, Class<T> entityClass, String joinField, String joinColumn, Object value) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(entityClass);
        Root<T> root = query.from(entityClass);

        Join<Object, Object> join = root.join(joinField);
        Predicate predicate = cb.equal(join.get(joinColumn), value);
        query.where(predicate);

        return em.createQuery(query).getResultList();
    }

}
