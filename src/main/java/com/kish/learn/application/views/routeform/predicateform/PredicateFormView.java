package com.kish.learn.application.views.routeform.predicateform;

import com.kish.learn.application.business.route.RouteSvc;
import com.kish.learn.application.business.route.dao.RequestPredicateDAO;
import com.kish.learn.application.business.route.enumeration.RequestPredicateType;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PredicateFormView extends Composite<VerticalLayout> {

    private Dialog parentDialog;



    private RequestPredicateDAO requestPredicateDAO;

    private RouteSvc routeSvc;

    VerticalLayout layoutColumn2 = new VerticalLayout();
    H3 h3 = new H3();
    FormLayout formLayout2Col = new FormLayout();
    TextField predicateName = new TextField();
    TextField predicateKey = new TextField();
    TextField predicateValue = new TextField();
    Select<RequestPredicateType> requestPredicateType = new Select();
    HorizontalLayout buttonGrpLayout = new HorizontalLayout();
    Button buttonPrimary = new Button();
    Button buttonSecondary = new Button();

    public PredicateFormView(Dialog parentDialog) {
        this.parentDialog = parentDialog;
        setLayout();
    }


    private void setLayout() {
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);
        layoutColumn2.setWidthFull();
        getContent().setFlexGrow(1.0, layoutColumn2);
        layoutColumn2.setWidth("100%");
        layoutColumn2.setMaxWidth("800px");
        layoutColumn2.setHeight("min-content");
        h3.setText("Request Predicate");
        h3.setWidthFull();
        predicateName.setLabel("Predicate Name");
        predicateName.setWidth("100%");
        formLayout2Col.setWidth("100%");
        predicateKey.setLabel("Predicate Key");
        predicateKey.setWidth("min-content");
        predicateValue.setLabel("Predicate Value");
        predicateValue.setWidth("min-content");
        requestPredicateType.setLabel("Predicate Type");
        requestPredicateType.setItems(RequestPredicateType.values());
        requestPredicateType.setItemLabelGenerator(RequestPredicateType::name);
        requestPredicateType.setWidth("min-content");
        buttonGrpLayout.setWidthFull();
        layoutColumn2.setFlexGrow(1.0, buttonGrpLayout);
        buttonGrpLayout.addClassName(Gap.MEDIUM);
        buttonGrpLayout.setWidth("100%");
        buttonGrpLayout.setHeight("min-content");
        buttonPrimary.setText("Save");
        buttonPrimary.setWidth("min-content");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonSecondary.setText("Cancel");
        buttonSecondary.setWidth("min-content");
        getContent().add(layoutColumn2);
        layoutColumn2.add(h3);
        layoutColumn2.add(formLayout2Col);
        formLayout2Col.add(predicateName,2);
        formLayout2Col.add(requestPredicateType,2);
        formLayout2Col.add(predicateKey);
        formLayout2Col.add(predicateValue);
        layoutColumn2.add(buttonGrpLayout);
        buttonGrpLayout.add(buttonPrimary);
        buttonGrpLayout.add(buttonSecondary);
        buttonSecondary.addClickListener(close -> parentDialog.close());
        setContent();
    }

    private void setContent() {
        if(getRequestPredicateDAO() != null) {
            predicateName.setValue(getRequestPredicateDAO().getPredicateName());
            predicateKey.setValue(getRequestPredicateDAO().getPredicateKey());
            predicateValue.setValue(getRequestPredicateDAO().getPredicateValue());
            requestPredicateType.setValue(getRequestPredicateDAO().getRequestPredicateType());
        }else{
            predicateName.clear();
            predicateKey.clear();
            predicateValue.clear();
            requestPredicateType.clear();
        }
    }

    public RequestPredicateDAO getRequestPredicateDAO() {
        return requestPredicateDAO;
    }

    public RouteSvc getRouteSvc() {
        return routeSvc;
    }

    public void setRequestPredicateDAO(RequestPredicateDAO requestPredicateDAO) {
        this.requestPredicateDAO = requestPredicateDAO;
        setContent();
    }


    public void setRouteSvc(RouteSvc routeSvc) {
        this.routeSvc = routeSvc;
    }

}
