package com.stegnin.miecosystem.vaadin.ui;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

@Route("")
@Theme(value = Material.class, variant = Material.LIGHT)
@PageTitle("ГЛАВНАЯ")
public class HomeView extends VerticalLayout {

    public HomeView() {
        init();
    }

    private void init() {
        Anchor anchor = new Anchor("device/info", "ИНФОРМАЦИЯ ОБ УСТРОЙСТВЕ");
        add(anchor);
    }

}
