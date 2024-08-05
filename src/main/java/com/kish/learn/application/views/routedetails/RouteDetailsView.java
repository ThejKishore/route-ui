package com.kish.learn.application.views.routedetails;

import com.kish.learn.application.business.route.RouteSvc;
import com.kish.learn.application.business.route.dao.RouteDAO;
import com.kish.learn.application.views.MainLayout;
import com.kish.learn.application.views.routeform.RouteFormView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@PageTitle("RouteDetails")
@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@Uses(Icon.class)
public class RouteDetailsView extends Div {

    private Grid<RouteDAO> routeDAOGrid;

    private final Filters filters;
    private final RouteSvc routeSvc;

    public RouteDetailsView(RouteSvc routeSvc) {
        this.routeSvc = routeSvc;
        setSizeFull();
        addClassNames("route-details-view");
        filters = new Filters(this::refreshGrid);
        VerticalLayout layout = new VerticalLayout(createMobileFilters(), filters, createGrid());
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);
    }

    private HorizontalLayout createMobileFilters() {
        // Mobile version
        HorizontalLayout mobileFilters = new HorizontalLayout();
        mobileFilters.setWidthFull();
        mobileFilters.addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.BoxSizing.BORDER,
                LumoUtility.AlignItems.CENTER);
        mobileFilters.addClassName("mobile-filters");

        Icon mobileIcon = new Icon("lumo", "plus");
        Span filtersHeading = new Span("Filters");
        mobileFilters.add(mobileIcon, filtersHeading);
        mobileFilters.setFlexGrow(1, filtersHeading);
        mobileFilters.addClickListener(e -> {
            if (filters.getClassNames().contains("visible")) {
                filters.removeClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:plus");
            } else {
                filters.addClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:minus");
            }
        });
        return mobileFilters;
    }

    public static class Filters extends Div implements Specification<RouteDAO> {

        private final TextField routerId = new TextField("Route Id");
        private final TextField path = new TextField("Path");
        private final TextField blueRouteUri = new TextField("Blue Route Uri");
        private final TextField featureFlagKey = new TextField("Feature Flag Key");
        private final TextField featureFlagValue = new TextField("Feature Flag Key");

        public Filters(Runnable onSearch) {
            setWidthFull();
            addClassName("filter-layout");
            addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM, LumoUtility.BoxSizing.BORDER);
            routerId.setPlaceholder("Route Identifier...");
            path.setPlaceholder("Path...");
            blueRouteUri.setPlaceholder("complete route url ....");
            featureFlagKey.setPlaceholder("feature flag key header name ....");
            featureFlagValue.setPlaceholder("feature flag value header value....");
            // Action buttons
            Button resetBtn = new Button("Reset");
            resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            resetBtn.addClickListener(e -> {
                routerId.clear();
                path.clear();
                blueRouteUri.clear();
                featureFlagKey.clear();
                featureFlagValue.clear();
                onSearch.run();
            });
            Button searchBtn = new Button("Search");
            searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            searchBtn.addClickListener(e -> onSearch.run());

            Div actions = new Div(resetBtn, searchBtn);
            actions.addClassName(LumoUtility.Gap.SMALL);
            actions.addClassName("actions");

            add(routerId, path , blueRouteUri, featureFlagKey ,featureFlagValue,  actions);
        }


        @Override
        public Predicate toPredicate(Root<RouteDAO> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            List<Predicate> predicates = new ArrayList<>();

            if (!routerId.isEmpty()) {
                String lowerCaseFilter = routerId.getValue().toLowerCase();
                if(StringUtils.isNoneBlank(lowerCaseFilter)) {
                    Predicate routeIdPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("routeIdentifier")), "%" + lowerCaseFilter + "%");
                    predicates.add(routeIdPredicate);
                }
            }
            if (path.getValue() != null) {
                String databaseColumn = "path";
                String lowerCaseFilter = path.getValue().toLowerCase();
                if(StringUtils.isNoneBlank(lowerCaseFilter)) {
                    Predicate pathPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get(databaseColumn)), "%" + lowerCaseFilter + "%");
                    predicates.add(pathPredicate);
                }
            }

            if (blueRouteUri.getValue() != null) {
                String databaseColumn = "routeUriBlue";
                String lowerCaseFilter = blueRouteUri.getValue().toLowerCase();
                if(StringUtils.isNoneBlank(lowerCaseFilter)) {
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(databaseColumn)), "%" + lowerCaseFilter + "%"));
                }
            }

            if (featureFlagKey.getValue() != null) {
                String databaseColumn = "featureFlagKey";
                String lowerCaseFilter = featureFlagKey.getValue().toLowerCase();
                if(StringUtils.isNoneBlank(lowerCaseFilter)) {
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(databaseColumn)), "%" + lowerCaseFilter + "%"));
                }
            }

            if (featureFlagValue.getValue() != null) {
                String databaseColumn = "featureFlagValue";
                String lowerCaseFilter = featureFlagValue.getValue().toLowerCase();
                if(StringUtils.isNoneBlank(lowerCaseFilter)) {
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(databaseColumn)), "%" + lowerCaseFilter + "%"));
                }
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }

    }

    private Component createGrid() {
        routeDAOGrid = new Grid<>(RouteDAO.class, false);
        GridContextMenu<RouteDAO> menu = routeDAOGrid.addContextMenu();
        menu.addItem("View", event -> menu.getUI().ifPresent(ui -> ui.navigate(RouteFormView.class).ifPresent(edi -> edi.editRoute(event.getItem().get()))));
        menu.addItem("Edit", event -> menu.getUI().ifPresent(ui -> ui.navigate(RouteFormView.class).ifPresent(edi -> edi.editRoute(event.getItem().get()))));
        menu.addItem("Delete", event -> {});
        routeDAOGrid.addColumn("routeIdentifier").setAutoWidth(true);
        routeDAOGrid.addColumn("method").setAutoWidth(true);
        routeDAOGrid.addColumn("path").setAutoWidth(true);
        routeDAOGrid.addColumn("routeUriBlue").setAutoWidth(true);
        routeDAOGrid.addColumn("isFeatureFlag").setAutoWidth(true);
        routeDAOGrid.addColumn("featureFlagKey").setAutoWidth(true);
        routeDAOGrid.addColumn("featureFlagValue").setAutoWidth(true);

        routeDAOGrid.setItems(query -> routeSvc.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)),
                filters).stream());
        routeDAOGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        routeDAOGrid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

        return routeDAOGrid;
    }

    private void refreshGrid() {
        routeDAOGrid.getDataProvider().refreshAll();
    }

}
