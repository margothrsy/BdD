/*
Copyright 2000- Francois de Bertrand de Beuvron

This file is part of CoursBeuvron.

CoursBeuvron is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CoursBeuvron is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CoursBeuvron.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.insa.toto.moveINSA.gui.testDataGrid;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import fr.insa.beuvron.vaadin.utils.dataGrid.ColumnDescription;
import fr.insa.beuvron.vaadin.utils.dataGrid.DataGrid;
import fr.insa.beuvron.vaadin.utils.dataGrid.GridDescription;
import fr.insa.toto.moveINSA.gui.MainLayout;
import java.util.List;

/**
 *
 * @author francois
 */
@Route(value = "debug/testDataGrid", layout = MainLayout.class)
public class TestDataGrid extends VerticalLayout {

    public static class AvecIcon extends HorizontalLayout {

        public AvecIcon(Icon icon, String text) {
            this.add(icon, new Div(text));
        }
    }
    
    public static class PetitPanel extends VerticalLayout {
        public PetitPanel(List<Object> row) {
            this.add(new HorizontalLayout(new Icon(VaadinIcon.COG),new Div(row.get(0).toString())));
            this.add(new HorizontalLayout(new Icon(VaadinIcon.ACADEMY_CAP),new Div(row.get(1).toString())));
        }
    }

    public TestDataGrid() {
        this.setSizeFull();
        this.add(new H3("Quelques tests sur les DataGrid")); 
        this.add(new Paragraph("Les DataGrid conçues pour afficher facilement n'importe "
                + "quel jeux de données représenté par une List<List<Object>>"));
        DataGrid test1 = DataGrid.simpleGrid(List.of(
                List.of("toto", "truc"),
                List.of("titi", "machin")
        ),
                List.of("c1", "c2"));
        this.add(new Paragraph("Une grille simple en fournissant simplement les entêtes des colonnes"));
        this.add(test1);
        DataGrid test2 = new DataGrid(List.of(
                List.of("toto", "truc"),
                List.of("titi", "machin")
        ),
                new GridDescription(List.of(
                        new ColumnDescription(),
                        new ColumnDescription().headerString("C1").colData(0),
                        new ColumnDescription().headerString("C2").colData(1),
                        new ColumnDescription().colDataObject(0, x -> "C " + (String) x)
                                .headerCompo(new AvecIcon(new Icon(VaadinIcon.ABACUS), "hcompo")),
                        new ColumnDescription().headerString("sub col 1")
                                .colDataObject(0, (t) -> ((String) t).substring(0, 2)),
                        new ColumnDescription().headerString("combo col")
                                .colCalculatedObject((t) -> ((String) t.get(0) + (String) t.get(1))),
                        new ColumnDescription().headerString("render col 2")
                                .colDataCompo(1, (t) -> new AvecIcon(new Icon(VaadinIcon.ABACUS),
                                ((String) t).substring(0, 2))),
                        new ColumnDescription().headerString("compo ligne")
                                .colCalculatedCompo((t) -> new PetitPanel(t))
                ))
                .columnBorders(true)
        );
        this.add(new Paragraph("On peut définir plus finement les colonnes en utilisant "
                + "une GridDescription et des ColumnDescription"));
        this.add(test2);
    }

}
