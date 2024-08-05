package com.kish.learn.application.business.route;

import com.kish.learn.application.business.route.dao.RouteDAO;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RouteSvc {

    private final RouteDAORepository routeDAORepository;
    private final RequestFilterDAORepository requestFilterDAORepository;
    private final RequestPredicateDAORepository requestPredicateDAORepository;

    public RouteDAO getRoute(Long routeId) {
        return routeDAORepository.findById(routeId)
                .orElseThrow(() -> new DataRetrievalFailureException("Route not found"));
    }

    public List<RouteDAO> getAllRoutes() {
        return routeDAORepository.findAll();
    }

    public Page<RouteDAO> list(Pageable pageable) {
        return routeDAORepository.findAll(pageable);
    }

    public Page<RouteDAO> list(Pageable pageable, Specification<RouteDAO> filter) {
        return routeDAORepository.findAll(filter,pageable);
    }

    public NotificationResponse updateRoute(RouteDAO routeDAO) {
        if(routeDAORepository.existsById(routeDAO.getId())){
            RouteDAO savedRoute = routeDAORepository.save(routeDAO);
            return new NotificationResponse(HttpStatus.OK,"Update Route Success",savedRoute);
        }else{
            return new NotificationResponse(HttpStatus.NOT_FOUND,"Route not found" , null);
        }
    }

    public NotificationResponse addRoute(RouteDAO routeDAO) {
        if(!routeDAORepository.existsByMethodAndPathAndRouteIdentifier(routeDAO.getMethod(),routeDAO.getPath(),routeDAO.getRouteIdentifier())) {
            RouteDAO newRoute = routeDAORepository.save(routeDAO);
            return new NotificationResponse(HttpStatus.OK,"New Route added Successfully",newRoute);
        }else{
            return new NotificationResponse(HttpStatus.BAD_REQUEST,"Duplicate Route not allowed found" , null);
        }
    }

    public NotificationResponse deleteRoute(Long routeId) {
        if(routeDAORepository.existsById(routeId)) {
            routeDAORepository.deleteById(routeId);
            return new NotificationResponse(HttpStatus.OK , "Successfully deleted route" , null);
        }else{
            return new NotificationResponse(HttpStatus.NOT_FOUND , "Route not found so unable to delete" , null);
        }
    }

    private record NotificationResponse<T>(HttpStatus status, String notificationMsg , T t ) {}
}
