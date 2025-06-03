package com.viettel.spring.cloud.server.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import jakarta.persistence.criteria.Predicate;

@Component
public class CriteriaQueryUtil {
    public <T> List<T> findByJoinFilters(EntityManager em, Class<T> entityClass, String joinField, String joinColumn, Object value) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(entityClass);
        Root<T> root = query.from(entityClass);

        Join<Object, Object> join = root.join(joinField);
        Predicate predicate = cb.equal(join.get(joinColumn), value);
        query.where(predicate);

        return em.createQuery(query).getResultList();
    }

    @Transactional
    public <T> int deleteByJoinFilter(EntityManager em, Class<T> entityClass, String joinField, String joinColumn, Object value) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<T> delete = cb.createCriteriaDelete(entityClass);
        Root<T> root = delete.from(entityClass);

        Join<Object, Object> join = root.join(joinField);
        Predicate predicate = cb.equal(join.get(joinColumn), value);
        delete.where(predicate);

        return em.createQuery(delete).executeUpdate();
    }

    public <T> List<T> findByNestedJoinsWithConditions(
            EntityManager em,
            Class<T> entityClass,
            List<JoinSpec> joinSpecs,
            List<PredicateSpec> predicateSpecs) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(entityClass);
        Root<T> root = query.from(entityClass);

        // Quản lý các alias và join
        Map<String, From<?, ?>> joins = new HashMap<>();
        joins.put("root", root);

        for (JoinSpec spec : joinSpecs) {
            From<?, ?> parent = joins.get(spec.parentAlias());
            if (parent == null) {
                throw new IllegalArgumentException("Alias không tồn tại: " + spec.parentAlias());
            }
            Join<?, ?> join = parent.join(spec.field());
            joins.put(spec.alias(), join);
        }

        // Gán các điều kiện
        List<Predicate> predicates = new ArrayList<>();
        for (PredicateSpec ps : predicateSpecs) {
            Path<?> path = joins.get(ps.alias()).get(ps.field());
            predicates.add(cb.equal(path, ps.value()));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        return em.createQuery(query).getResultList();
    }

    // Record để mô tả các bước join
    public record JoinSpec(String parentAlias, String field, String alias) {}

    // Record để mô tả các điều kiện lọc
    public record PredicateSpec(String alias, String field, Object value) {}
}
