package com.kish.learn.application.views.routeform;

import com.kish.learn.application.business.route.RouteSvc;
import com.kish.learn.application.business.route.dao.RequestFilterDAO;
import com.kish.learn.application.business.route.dao.RequestPredicateDAO;
import com.kish.learn.application.business.route.dao.RouteDAO;
import com.kish.learn.application.business.route.enumeration.FilterType;
import com.kish.learn.application.business.route.enumeration.HTTPMethod;
import com.kish.learn.application.business.route.enumeration.RequestPredicateType;
import com.kish.learn.application.business.route.enumeration.SelectedFilter;
import com.kish.learn.application.views.MainLayout;
import com.kish.learn.application.views.routedetails.RouteDetailsView;
import com.kish.learn.application.views.routeform.filterform.FilterFormView;
import com.kish.learn.application.views.routeform.predicateform.PredicateFormView;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@PageTitle("Route Form")
@Route(value = "route-form", layout = MainLayout.class)
public class RouteFormView extends Composite<VerticalLayout> {

    private final PredicateFormView predicateFormView;
    private final FilterFormView filterFormView;
    private final RouteSvc routeSvc;
    RouteDAO routeDAO;
    RequestFilterDAO selectedFilterDAO;
    RequestPredicateDAO selectedPredicateDAO;

    Binder<Route> binder = new BeanValidationBinder<>(Route.class);

    VerticalLayout layoutColumn2 = new VerticalLayout();
    H3 h3 = new H3();
    FormLayout formLayout2Col = new FormLayout();
    TextField routeIdentifier = new TextField();
    // Shorthand for cases without extra configuration
    TextField path = new TextField();
    Select<HTTPMethod> method = new Select<>();
    TextField blueUri = new TextField();
    TextField blueWeight = new TextField();
    TextField greenUri = new TextField();
    TextField greenWeight = new TextField();
    TextField featureKey = new TextField();
    TextField featureValue = new TextField();
    Select<TrafficSplit> trafficSplitSelect = new Select();

    HorizontalLayout layoutRow = new HorizontalLayout();

    Grid<RequestPredicateDAO> requestPredictedGrid = new Grid<>(RequestPredicateDAO.class);
    Grid<RequestFilterDAO> requestFilterGrid = new Grid<>(RequestFilterDAO.class);

    Details predicateDetails;
    Details filterDetails;

    Button save = new Button();
    Button clear = new Button();

    Dialog predicateDialog = new Dialog();
    Dialog filterDialog = new Dialog();


    public RouteFormView(RouteSvc routeSvc) {
        method.setLabel("HTTP Method");
        method.setItems(HTTPMethod.values());
        method.setItemLabelGenerator(HTTPMethod::name);
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);
        layoutColumn2.setWidth("100%");
        layoutColumn2.setMaxWidth("800px");
        layoutColumn2.setHeight("min-content");
        h3.setText("Route Information");
        h3.setWidth("100%");
        formLayout2Col.setWidth("100%");
        routeIdentifier.setLabel("Route Identifier");
        path.setLabel("Path");
        blueUri.setLabel("Blue-Uri");
        blueWeight.setLabel("Blue-Weight");
        greenUri.setLabel("Green-Uri");
        greenWeight.setLabel("Green-Weight");
        featureKey.setLabel("Feature Key");
        featureValue.setLabel("Feature Value");
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        save.setText("Save");
        save.setWidth("min-content");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        clear.setText("Cancel");
        clear.setWidth("min-content");
        getContent().add(layoutColumn2);
        layoutColumn2.add(h3);
        layoutColumn2.add(formLayout2Col);
        formLayout2Col.add(routeIdentifier,2);
        formLayout2Col.add(path,2);
        formLayout2Col.add(method,2);
        formLayout2Col.add(trafficSplitSelect,2);
        trafficSplitSelect.setItems(TrafficSplit.values());
        trafficSplitSelect.setItemLabelGenerator(TrafficSplit::name);
        trafficSplitSelect.setValue(TrafficSplit.NONE);
        trafficSplitSelect.setLabel("Select Traffic Split Strategy");
        formLayout2Col.add(blueWeight,2);
        formLayout2Col.add(greenWeight,2);
        formLayout2Col.add(blueUri,2);
        formLayout2Col.add(greenUri,2);
        formLayout2Col.add(featureKey);
        formLayout2Col.add(featureValue);

        {

            predicateDetails = new Details("Predicate",getRequestPredictedGrid());
            predicateDetails.setWidthFull();
            predicateDetails.setHeight("25%");
            layoutColumn2.add(predicateDetails);
        }

        {

            filterDetails = new Details("Filter",getFilterComponenet());
            filterDetails.setWidthFull();
            filterDetails.setHeight("25%");
            layoutColumn2.add(filterDetails);
        }
        {
            predicateFormView = new PredicateFormView(predicateDialog);
            predicateDialog.add(predicateFormView);
            filterFormView = new FilterFormView(filterDialog);
            filterDialog.add(filterFormView);
        }

        layoutColumn2.add(layoutRow);
        layoutRow.add(save);
        layoutRow.add(clear);
        setComponents(this.trafficSplitSelect.getValue());
        trafficSplitSelect.addValueChangeListener(l -> setComponents(l.getValue()));
        this.routeSvc = routeSvc;
        setContent();

        clear.addClickListener(buttonClickEvent -> {
            clear.getUI().ifPresent( ui -> ui.navigate(RouteDetailsView.class));
        });
    }

    private void setContent() {
        if(routeDAO !=null){
            routeIdentifier.setValue(routeDAO.getRouteIdentifier());
            path.setValue(routeDAO.getPath());
            method.setValue(routeDAO.getMethod());
            blueWeight.setValue(Optional.ofNullable(routeDAO.getBlueWeight()).map(i -> i+"").orElseGet( () -> "100"));
            greenWeight.setValue(Optional.ofNullable(routeDAO.getGreenWeight()).map(i -> i+"").orElseGet( () -> "0"));
            blueUri.setValue(Optional.ofNullable(routeDAO.getRouteUriBlue()).orElseGet(() -> StringUtils.EMPTY));
            greenUri.setValue(Optional.ofNullable(routeDAO.getRouteUriGreen()).orElseGet(() -> StringUtils.EMPTY));
            featureKey.setValue(Optional.ofNullable(routeDAO.getFeatureFlagKey()).orElseGet(() -> StringUtils.EMPTY));
            featureValue.setValue(Optional.ofNullable(routeDAO.getFeatureFlagValue()).orElseGet(() -> StringUtils.EMPTY));
            requestFilterGrid.setItems(Optional.ofNullable(routeDAO.getRequestFilterDAOS()).orElseGet(Collections::emptySet));
            requestPredictedGrid.setItems(Optional.ofNullable(routeDAO.getRequestPredicateDAOS()).orElseGet(Collections::emptySet));
//            trafficSplitSelect.

        }else{
            routeIdentifier.clear();
            path.clear();
            method.clear();
            trafficSplitSelect.setValue(TrafficSplit.NONE);
            blueWeight.clear();
            greenWeight.clear();
            blueUri.clear();
            greenUri.clear();
            featureKey.clear();
            featureValue.clear();
            requestFilterGrid.setItems();
            requestPredictedGrid.setItems();
        }
    }

    private VerticalLayout getRequestPredictedGrid() {
        VerticalLayout requestPredictedGridVL = new VerticalLayout();
        HorizontalLayout headerHL = new HorizontalLayout();
        Button addPredicate = new Button("Add");
        addPredicate.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button editPredicate = new Button("Edit");
        Button deletePredicate = new Button("Delete");
        deletePredicate.addThemeVariants(ButtonVariant.LUMO_ERROR);
        addPredicate.addClickListener(l -> {
            predicateFormView.setRequestPredicateDAO(null);
            predicateFormView.setRouteSvc(routeSvc);
            predicateDialog.open();
        });
        editPredicate.addClickListener(l -> {
            if(selectedPredicateDAO !=null) {
                predicateFormView.setRequestPredicateDAO(selectedPredicateDAO);
                predicateFormView.setRouteSvc(routeSvc);
                predicateDialog.open();
            }
        });
        deletePredicate.addClickListener(l -> {
            if(selectedPredicateDAO !=null) {
                predicateFormView.setRequestPredicateDAO(selectedPredicateDAO);
                predicateFormView.setRouteSvc(routeSvc);
                predicateDialog.open();
            }
        });

        headerHL.add(addPredicate);
        headerHL.add(editPredicate);
        headerHL.add(deletePredicate);

        headerHL.setWidth("100%");
        requestPredictedGridVL.add(headerHL);
        requestPredictedGridVL.setSizeFull();
        requestPredictedGrid = new Grid<>(RequestPredicateDAO.class , false);
        requestPredictedGrid.addColumn("predicateName").setHeader("Predicate Name").setAutoWidth(true);
        requestPredictedGrid.addColumn("predicateKey").setHeader("Predicate Key").setAutoWidth(true);
        requestPredictedGrid.addColumn("predicateValue").setHeader("Predicate Value").setAutoWidth(true);
        requestPredictedGrid.addColumn("requestPredicateType").setHeader("Predicate Type").setAutoWidth(true);
        requestPredictedGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        requestPredictedGrid.addSelectionListener(l ->  l.getFirstSelectedItem()
                .ifPresentOrElse(sel -> selectedPredicateDAO=sel, () ->selectedPredicateDAO = null));
        RequestPredicateDAO dummy = new RequestPredicateDAO();
        dummy.setPredicateName("header predicate name");
        dummy.setPredicateKey("header key ");
        dummy.setPredicateValue("header value");
        dummy.setRequestPredicateType(RequestPredicateType.HEADER);
        requestPredictedGrid.setItems(List.of(dummy));
        requestPredictedGridVL.add(requestPredictedGrid);
        return requestPredictedGridVL;
    }




    private VerticalLayout getFilterComponenet() {
        VerticalLayout filterComponentVL = new VerticalLayout();
        HorizontalLayout filterHeaderHL = new HorizontalLayout();
        Button addFilter = new Button("Add");
        addFilter.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button editFilter = new Button("Edit");
        Button deleteFilter = new Button("Delete");
        deleteFilter.addThemeVariants(ButtonVariant.LUMO_ERROR);
        addFilter.addClickListener(l -> {
            filterFormView.setRequestFilterDAO(null);
            filterFormView.setRouteSvc(routeSvc);
            filterDialog.open();
        });
        editFilter.addClickListener(l -> {
            if(selectedFilterDAO !=null) {
                filterFormView.setRequestFilterDAO(selectedFilterDAO);
                filterFormView.setRouteSvc(routeSvc);
                filterDialog.open();
            }
        });
        deleteFilter.addClickListener(l -> {
            if(selectedFilterDAO !=null) {
                filterFormView.setRequestFilterDAO(selectedFilterDAO);
                filterFormView.setRouteSvc(routeSvc);
                filterDialog.open();
            }
        });
        filterHeaderHL.add(addFilter);
        filterHeaderHL.add(editFilter);
        filterHeaderHL.add(deleteFilter);
        filterHeaderHL.setWidth("100%");
        filterComponentVL.add(filterHeaderHL);
        filterComponentVL.setSizeFull();
        requestFilterGrid = new Grid<>(RequestFilterDAO.class , false);
        requestFilterGrid.addColumn(RequestFilterDAO::getFilterName).setHeader("Filter Name").setAutoWidth(true);
        requestFilterGrid.addColumn(RequestFilterDAO::getFilterType).setHeader("Filter Type").setAutoWidth(true);
        requestFilterGrid.addColumn(RequestFilterDAO::getSelectKey).setHeader("Filter Key").setAutoWidth(true);
        requestFilterGrid.addColumn(RequestFilterDAO::getSelectValue).setHeader("Filter Value").setAutoWidth(true);
        requestFilterGrid.addColumn(RequestFilterDAO::getSelectedFilter).setHeader("Selected Filter").setAutoWidth(true);
        requestFilterGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        requestFilterGrid.addSelectionListener(l ->  l.getFirstSelectedItem()
                .ifPresentOrElse(sel -> selectedFilterDAO=sel, () -> selectedFilterDAO = null));
        RequestFilterDAO dummy = new RequestFilterDAO();
        dummy.setFilterName("strip path");
        dummy.setFilterType(FilterType.BEFORE);
        dummy.setSelectKey("<empty>");
        dummy.setSelectValue("1");
        dummy.setSelectedFilter(SelectedFilter.STRIP_PREFIX);
        requestFilterGrid.setItems(List.of(dummy));
        filterComponentVL.add(requestFilterGrid);
        return filterComponentVL;

    }

    private void setComponents(TrafficSplit trafficSplit) {
        switch (trafficSplit) {
            case NONE:
                blueUri.setVisible(true);
                blueWeight.setVisible(false);
                greenUri.setVisible(false);
                greenWeight.setVisible(false);
                featureKey.setVisible(false);
                featureValue.setVisible(false);
                break;
            case WEIGH_BASED:
                blueUri.setVisible(true);
                blueWeight.setVisible(true);
                greenUri.setVisible(true);
                greenWeight.setVisible(true);
                featureKey.setVisible(false);
                featureValue.setVisible(false);
                break;
            case FEATURE_BASED:
                blueUri.setVisible(true);
                blueWeight.setVisible(false);
                greenUri.setVisible(true);
                greenWeight.setVisible(false);
                featureKey.setVisible(true);
                featureValue.setVisible(true);
                break;
        }
    }


    enum TrafficSplit{
        NONE,
        WEIGH_BASED,
        FEATURE_BASED
    }

    public void editRoute(RouteDAO routeDAO){
        this.routeDAO = routeDAO;
        setContent();
    }
}
