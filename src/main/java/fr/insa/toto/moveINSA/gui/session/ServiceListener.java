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
package fr.insa.toto.moveINSA.gui.session;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import java.sql.SQLException;
import org.springframework.stereotype.Component;

/**
 * Permet de couper automatiquement la connection à la BdD à la fin d'une
 * session.
 *
 * @author francois
 */
@Component
public class ServiceListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {

        // on ne fait rien à la création de la session
        event.getSource().addSessionInitListener(
                initEvent -> {
                });

        // on ne fait rien non plus à la fermeture d'une session : 
        // dorénavent, les connexions à la BdD sont gérée par une fr.insa.beuvron.vaadin.utils.ConnectionPool
        event.getSource().addSessionDestroyListener(
                destroyEvent -> {});

    }
}
