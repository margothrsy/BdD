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
package fr.insa.toto.moveINSA.gui.vues;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.insa.beuvron.vaadin.utils.ConnectionPool;
import fr.insa.beuvron.vaadin.utils.dataGrid.ColumnDescription;
import fr.insa.beuvron.vaadin.utils.dataGrid.GridDescription;
import fr.insa.beuvron.vaadin.utils.dataGrid.ResultSetGrid;
import fr.insa.toto.moveINSA.gui.MainLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 *
 * @author francois
 */
@PageTitle("MoveINSA")
@Route(value = "offres/liste", layout = MainLayout.class)
public class OffresPanel extends VerticalLayout {

    /**
     * Un petit composant qui affiche un entier sous forme d'un ensemble d'icones.
     */
    public static class IntAsIcon extends HorizontalLayout {

        public IntAsIcon(int nbr) {
            for (int i = 0; i < nbr; i++) {
                this.add(new Icon(VaadinIcon.EXIT));
            }
        }
    }

    private ResultSetGrid gOffres;
    private Button bPostule;

    public OffresPanel() {
        try (Connection con = ConnectionPool.getConnection()) {
            this.add(new H2("Affichage de tables (ResultSet) quelconques à l'aide de ResultSetGrid"));
            PreparedStatement offresAvecPart = con.prepareStatement(
                    "select offremobilite.id as idOffre,partenaire.refPartenaire,offremobilite.nbrplaces,partenaire.id as idPartenaire \n"
                    + "  from offremobilite \n"
                    + "    join partenaire on offremobilite.proposepar = partenaire.id");
            this.add(new H3("affichage direct (sans mise en forme) du ResultSet"));
            this.add(new ResultSetGrid(offresAvecPart));
            this.gOffres = new ResultSetGrid(offresAvecPart, new GridDescription(List.of(
                    new ColumnDescription().colData(0).visible(false), // on veut pouvoir accéder à l'id de l'offre mais non l'afficher
                    new ColumnDescription().colData(1).headerString("Partenaire"),
                    new ColumnDescription().colData(2).headerString("nbr places"),
                    // pour montrer l'utilisation d'un composant dans une colonne
                    new ColumnDescription().colDataCompo(2, (t) -> new IntAsIcon((Integer) t)).headerString("places"),
                    // pour montrer une colonne calculée à partir de plusieurs colonnes de données
                    // rappelez vous que le paramètre t de la lambda ci-dessous est de type List<Object>
                    // on peut donc accéder à la valeur des colonnes simplement par un get
                    // comme ici, on concatene, la méthode toString définie pour tout objet sera
                    // appelée, et on n'a donc pas besoin de faire des "cast" en fonction du type réel des colonnes
                    new ColumnDescription().colCalculatedObject((t) -> t.get(1) + "/" + t.get(3) + " : " + t.get(2)).headerString("resumé"),
                    new ColumnDescription().colData(3).visible(false) // même chose pour l'id du partenaire
            )));
            this.add(new H3("la même table mais mise en forme"));
            this.add(new Paragraph("le petit bouton \"Postuler\" n'est pas vraiment opérationel : "
                    + "je n'ai même pas la notion d'étudiant dans ma base de donnée. Il est là pour que vous puissiez voir dans "
                    + "le code qu'il est facile d'interagir avec une Grid pour par exemple récupérer la ligne sélectionnée. "
                    + "Cela montre aussi l'utilité des colonnes non affichées"));
            this.add(this.gOffres);
            this.bPostule = new Button("Postuler");
            this.bPostule.addClickListener((t) -> {
                // comme la grille est générique, chaque ligne contient une List<Object> : un Object par colonne
                // par défaut une grille est en mono-selection
                // mais comme on peut fixer en multi-selection, on a potentiellement un ensemble d'item selectionnés
                Set<List<Object>> lignesSelected = this.gOffres.getSelectedItems();
                // dans notre cas 0 ou 1 item selectionné
                if (lignesSelected.isEmpty()) {
                    Notification.show("selectionnez une offre");
                } else {
                    List<Object> ligne = lignesSelected.iterator().next();
                    // normalement, on ne montre pas les ID à l'utilisateur
                    // ici c'est pour montrer que l'on a bien accès à la colonne 0 même si elle n'est pas visible
                    Notification.show("vous postulez sur l'offre N° "+ligne.get(0) + " proposée par " + ligne.get(1));
                }
            });
            this.add(this.bPostule);
            this.add(new H3("offres groupées par partenaires"));
            PreparedStatement offresParPartenaire = con.prepareStatement(
            "select partenaire.id,partenaire.refPartenaire,sum(offremobilite.nbrplaces) as placesPartenaire, ( \n"
                    + "   select sum(nbrplaces) from offremobilite \n"
                    + "  ) as totplaces \n"
                    + "  from offremobilite \n"
                    + "    join partenaire on offremobilite.proposepar = partenaire.id \n"
                    + "  group by partenaire.id");
            this.add(new ResultSetGrid(offresParPartenaire));
            this.add(new H3("le même, mais avec mise en forme"));
            ResultSetGrid parPart = new ResultSetGrid(offresParPartenaire, new GridDescription(List.of(
                    new ColumnDescription().colData(0).visible(false), // on veut pouvoir accéder à l'id du partenaire mais non l'afficher
                    new ColumnDescription().colData(1).headerString("partenaire"),
                    new ColumnDescription().colData(2).headerString("total places offertes"),
                    // calcul du pourcentage
                    // Note : le type précis de donnée retourné par un opérateur sum n'est pas forcément un Integer
                    // c'est pourquoi on n'utilise pas un simple cast : (Integer) t.get(2)
                    // mais on passe plutôt par la forme textuelle : Integer.parseInt(""+t.get(2))
                    new ColumnDescription().colCalculatedObject((t) -> {
                        int nbrPart = Integer.parseInt(""+t.get(2));
                        int nbrTot = Integer.parseInt(""+t.get(3));
                        double percent = ((double) nbrPart) / nbrTot * 100;
                        return String.format("%.0f%%", percent);
                    }).headerString("pourcentage")
            )));
            this.add(parPart);
        } catch (SQLException ex) {
            System.out.println("Probleme : " + ex.getLocalizedMessage());
            Notification.show("Probleme : " + ex.getLocalizedMessage());
        }
    }

}
