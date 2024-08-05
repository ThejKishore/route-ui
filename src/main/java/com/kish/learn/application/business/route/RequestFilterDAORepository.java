package com.kish.learn.application.business.route;

import com.kish.learn.application.business.route.dao.RequestFilterDAO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface RequestFilterDAORepository extends JpaRepository<RequestFilterDAO, Long> {
    Set<RequestFilterDAO> findAllByRouteId(Long routeId);
}