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
package fr.insa.toto.moveINSA.gui;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.List;

@PageTitle("MoveINSA")
@Route(value = "", layout = MainLayout.class)
public class VuePrincipale extends VerticalLayout {

    public VuePrincipale() {
        this.add(new H3("Petit programme pour démarrer le projet M3 2024"));
        List<Paragraph> attention = List.of(
                new Paragraph("Attention : la base de donnée utilisée par défaut "
                        + "est créée en mémoire."),
                new Paragraph("Vous devez la réinitialiser après chaque démarrage."
                        + "Pour cela, allez dans le menu debug;RAZ BdD et cliquez sur le bouton"),
                new Paragraph("Dans cette version, les connexions à la base de donnée sont gérées par un pool de connexion"),
                new Paragraph("Pour utiliser une autre base de donnée, vous devez modifier la classe "
                        + "fr.insa.beuvron.vaadin.utils.ConnectionPool"),
                new Paragraph("Si vous utilisez le serveur MySQL fourni pour M3, "
                        + "il vous suffit de commenter la def pour H2 et de dé-commenter "
                        + "la def pour le serveur mysql de M3. Pensez évidemment à modifier pour donner VOS login/pass")
        );
        attention.get(0).getStyle().set("color", "red");
        attention.forEach((p) -> this.add(p));

    }
}
