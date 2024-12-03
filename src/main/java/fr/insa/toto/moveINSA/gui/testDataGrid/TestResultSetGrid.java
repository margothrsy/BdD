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

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import fr.insa.beuvron.vaadin.utils.dataGrid.ColumnDescription;
import fr.insa.beuvron.vaadin.utils.dataGrid.GridDescription;
import fr.insa.beuvron.vaadin.utils.dataGrid.ResultSetGrid;
import fr.insa.toto.moveINSA.gui.MainLayout;
import fr.insa.toto.moveINSA.model.ConnectionSimpleSGBD;
import fr.insa.toto.moveINSA.model.GestionBdD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author francois
 */
@Route(value = "debug/testResultSetGrid", layout = MainLayout.class)
public class TestResultSetGrid extends VerticalLayout {
    
    public static class OffrePanel extends VerticalLayout {
        public OffrePanel(String refPartenaire,int nbrPlaces) {
            this.add(new Span(refPartenaire));
            this.add(new Span("total places offertes : " + nbrPlaces));
        }
    }
    
    public TestResultSetGrid() {
        try {
            Connection con = CONN;
            this.setSizeFull();
            this.add(new H3("Tests sur les ResultSetGrid"));
            this.add(new Paragraph("Les ResultSetGrid sont des cas particulier de DataGrid "
                    + "où les données sont retrouvées à l'aide d'un PreparedStatement"));
            PreparedStatement pst1 = con.prepareStatement(
                    "select partenaire.refPartenaire,offremobilite.nbrplaces \n"
                    + " from offremobilite join partenaire on offremobilite.proposepar = partenaire.id "
                            + " order by partenaire.refPartenaire asc");
            this.add(new Paragraph("Affichage simple d'un PreparedStatement avec jointure"));
            this.add(new ResultSetGrid(pst1));
            PreparedStatement pst2 = con.prepareStatement(
                    "select id,refPartenaire, \n"
                    + "  ( \n"
                    + "  select coalesce(sum(nbrplaces),0) \n"
                    + "    from offremobilite where proposepar = partenaire.id \n"
                    + "  ) as totplaces \n"
                    + " from partenaire");
            this.add(new Paragraph("Affichage d'un PreparedStatement avec requêtes imbriquées. "
                    + "On a masqué la première colonne contenant l'identificateur du partenaire"));
            this.add(new ResultSetGrid(pst2, new GridDescription(List.of(
                    new ColumnDescription().colData(0).visible(false),
                    new ColumnDescription().colData(1).headerString("ref"),
                    new ColumnDescription().colData(2).headerString("total places")
            ))));
            PreparedStatement pst3 = con.prepareStatement(
                    "select id,refPartenaire, \n"
                    + "  ( \n"
                    + "  select coalesce(sum(nbrplaces),0) \n"
                    + "    from offremobilite where proposepar = partenaire.id \n"
                    + "  ) as totplaces \n"
                    + " from partenaire");
            this.add(new Paragraph("Le même, mais cette fois on n'affiche qu'une seule colonne "
                    + "contenant un composant qui regroupe les informations"));
            this.add(new ResultSetGrid(pst3, new GridDescription(List.of(
                    new ColumnDescription().colData(0).visible(false),
                    new ColumnDescription().headerString("Partenaires").colCalculatedCompo(
                            (List<Object> ligne) -> new OffrePanel((String) ligne.get(1),
                                    // !! la colonne sum renvoie un java.math.BigDecimal
                                    // au moins avec mysql
                                    // et non un Integer
                                    Integer.parseInt(ligne.get(2).toString())))
            ))));
        } catch (SQLException ex) {
            Notification.show("Internal Problem : " + ex.getLocalizedMessage());
            ex.printStackTrace();
        }
        
    }
    
        // ---------- gestion de la connection à la BdD
    // ici une connection globale pour BdD M2 en mémoire
    private static Connection CONN = creeEtInit();

    private static Connection creeEtInit() {
        try {
            Connection con = ConnectionSimpleSGBD.defaultCon();
            GestionBdD.razBDD(con);
            return con;
        } catch (SQLException ex) {
            Notification.show("Impossible créer BdD"
                    + ex.getLocalizedMessage());
            throw new Error(ex);
        }
    }

    
}
