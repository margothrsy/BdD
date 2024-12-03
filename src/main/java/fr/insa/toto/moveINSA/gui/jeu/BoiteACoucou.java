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

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.insa.toto.moveINSA.gui.MainLayout;

/**
 *
 * @author francois
 */
@PageTitle("BoiteACoucou")
@Route(value = "boite",layout = MainLayout.class)
public class BoiteACoucou extends VerticalLayout{
    
    private final TextField tfNom;
    private TextArea taMessage;
    private Button bCoucou;
    private Button bSalut;
    private HorizontalLayout hlBoutons;
    
    public BoiteACoucou() {
        this.setSizeFull();
        this.tfNom = new TextField("Votre nom");
        this.taMessage = new TextArea();
        this.taMessage.setHeight("10em");
        this.taMessage.setWidthFull();
        this.bCoucou = new Button("Coucou");
        this.bCoucou.addClickListener((t) -> {
            this.taMessage.setValue(this.taMessage.getValue() +
                    "coucou " + this.tfNom.getValue() + "\n");
        });
        this.bSalut = new Button("Salut");
        this.bSalut.addClickListener((t) -> {
            this.taMessage.setValue(this.taMessage.getValue() +
                    "salut " + this.tfNom.getValue() + "\n");
        });
        this.hlBoutons = new HorizontalLayout(this.bCoucou,this.bSalut);
        this.add(this.tfNom,this.taMessage,this.hlBoutons);
        
    }
    
}
