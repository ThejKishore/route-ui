package com.kish.learn.application.business.route;

import com.kish.learn.application.business.route.dao.RouteDAO;
import com.kish.learn.application.business.route.enumeration.HTTPMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RouteDAORepository extends JpaRepository<RouteDAO, Long> , JpaSpecificationExecutor<RouteDAO> {
    boolean existsByMethodAndPathAndRouteIdentifier(HTTPMethod method, String path, String routeIdentifier);
    RouteDAO findByMethodAndPathAndRouteIdentifier(HTTPMethod method, String path, String routeIdentifier);
}