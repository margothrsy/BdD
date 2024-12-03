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
package fr.insa.toto.moveINSA.gui.jeu;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.insa.toto.moveINSA.gui.MainLayout;

/**
 *
 * @author francois
 */
@PageTitle("JeuMinMax")
@Route(value = "jeu", layout = MainLayout.class)
public class TrouveEntier extends VerticalLayout {

    private TextField tfChoix;
    private Button bTest;
    private Button bRejouer;
    private VerticalLayout vlBravo;

    private int nbrAlea;

    public TrouveEntier() {
        this.setWidth("100%");
        this.nbrAlea = (int) (Math.random() * 9 + 1);
        this.add(new H3("trouver entier entre 1 et 9"));
        this.tfChoix = new TextField("choix");
        this.bTest = new Button("test");
        this.bTest.addClickListener((t) -> {
            try {
                int choix = Integer.parseInt(this.tfChoix.getValue());
                if (choix < this.nbrAlea) {
                    Notification.show(choix + " trop petit");
                    this.resetTextField();
                } else if (choix > this.nbrAlea) {
                    Notification.show(choix + " trop grand");
                    this.resetTextField();
                } else {
                    // TODO
                    this.bTest.setEnabled(false);
                    this.bRejouer.setEnabled(true);
                    this.vlBravo.removeAll();
                    H3 bravo = new H3("!!! BRAVO !!! C'était bien "+this.nbrAlea);
                    this.vlBravo.add(bravo);
                }
            } catch (NumberFormatException ex) {
                Notification.show("un entier on a dit !!!");
            }
        });
        this.bTest.addClickShortcut(Key.ENTER);
        this.bRejouer = new Button("Rejouer");
        this.bRejouer.setEnabled(false);
        this.bRejouer.addClickListener((t) -> {
            this.nbrAlea = (int) (Math.random() * 9 + 1);
            this.bTest.setEnabled(true);
            this.bRejouer.setEnabled(false);
            this.vlBravo.removeAll();
            this.resetTextField();
        });
        this.vlBravo = new VerticalLayout();
        this.vlBravo.setWidthFull();
        this.add(this.tfChoix, this.bTest, this.bRejouer, this.vlBravo);
    }

    private void resetTextField() {
        this.tfChoix.setValue("");
        this.tfChoix.focus();
    }

}
