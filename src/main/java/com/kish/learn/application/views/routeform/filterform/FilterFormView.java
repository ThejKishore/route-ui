package com.kish.learn.application.views.routeform.filterform;

import com.kish.learn.application.business.route.RouteSvc;
import com.kish.learn.application.business.route.dao.RequestFilterDAO;
import com.kish.learn.application.business.route.enumeration.FilterType;
import com.kish.learn.application.business.route.enumeration.SelectedFilter;
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

public class FilterFormView extends Composite<VerticalLayout> {

    private Dialog parentDialog;

    private RequestFilterDAO requestFilterDAO;

    private RouteSvc routeSvc;

    VerticalLayout layoutColumn2 = new VerticalLayout();
    H3 h3 = new H3();
    FormLayout formLayout2Col = new FormLayout();
    TextField filterName = new TextField();
    Select<SelectedFilter> selectedFilter = new Select();
    Select<FilterType> filterType = new Select();
    TextField selectKey = new TextField();
    TextField selectValue = new TextField();
    HorizontalLayout layoutRow = new HorizontalLayout();
    Button save = new Button();
    Button cancel = new Button();


    public FilterFormView(Dialog parentDialog) {
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
        h3.setText("Filter Form");
        h3.setWidthFull();
        filterName.setLabel("Filter Name");
        filterName.setWidth("100%");
        formLayout2Col.setWidth("100%");
        selectValue.setLabel("Select Value");
        selectValue.setWidth("min-content");
        selectKey.setLabel("Select Key");
        selectKey.setWidth("min-content");
        selectedFilter.setLabel("Selected Filter");
        selectedFilter.setWidth("min-content");
        selectedFilter.setItems(SelectedFilter.values());
        selectedFilter.setItemLabelGenerator(SelectedFilter::name);

        filterType.setLabel("Filter Type");
        filterType.setEnabled(false);
        filterType.setItems(FilterType.values());
        filterType.setItemLabelGenerator(FilterType::name);
        filterType.setWidth("min-content");
        layoutRow.setWidthFull();
        layoutColumn2.setFlexGrow(1.0, layoutRow);
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.setHeight("min-content");
        save.setText("Save");
        save.setWidth("min-content");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.setText("Cancel");
        cancel.setWidth("min-content");
        getContent().add(layoutColumn2);
        layoutColumn2.add(h3);
        layoutColumn2.add(formLayout2Col);
        formLayout2Col.add(filterName,2);
        formLayout2Col.add(selectedFilter,2);
        formLayout2Col.add(filterType,2);
        formLayout2Col.add(selectValue);
        formLayout2Col.add(selectKey);
        layoutColumn2.add(layoutRow);
        layoutRow.add(save);
        layoutRow.add(cancel);
        cancel.addClickListener(closeEvent -> this.parentDialog.close());
        selectedFilter.addValueChangeListener( v -> {
            SelectedFilter value = v.getValue();
            filterType.setValue(value.getFilterType());
        });
        setContent();
    }

    private void setContent() {
        if(getRequestFilterDAO() != null) {
            filterName.setValue(getRequestFilterDAO().getFilterName());
            selectedFilter.setValue(getRequestFilterDAO().getSelectedFilter());
            filterType.setValue(getRequestFilterDAO().getSelectedFilter().getFilterType());
            selectKey.setValue(getRequestFilterDAO().getSelectKey());
            selectValue.setValue(getRequestFilterDAO().getSelectValue());
        }else{
            filterName.clear();
            selectedFilter.clear();
            filterType.clear();
            selectKey.clear();
            selectValue.clear();
        }
    }


    public RequestFilterDAO getRequestFilterDAO() {
        return requestFilterDAO;
    }

    public void setRequestFilterDAO(RequestFilterDAO requestFilterDAO) {
        this.requestFilterDAO = requestFilterDAO;
        setContent();
    }

    public RouteSvc getRouteSvc() {
        return routeSvc;
    }

    public void setRouteSvc(RouteSvc routeSvc) {
        this.routeSvc = routeSvc;
    }

}
