package com.kish.learn.application.business.route;

import com.kish.learn.application.business.route.dao.RequestPredicateDAO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface RequestPredicateDAORepository extends JpaRepository<RequestPredicateDAO, Long> {
     Set<RequestPredicateDAO> findAllByRouteId(Long routeId);
}