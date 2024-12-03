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

import com.vaadin.flow.server.VaadinSession;
import fr.insa.toto.moveINSA.model.Partenaire;
import java.io.Serializable;

/**
 * Conserve les informations partagées par diverses pages dans une même session.
 * <p>
 * les sessions sont gérées par vaadin. Normalement tout affichage d'une page
 * dans vaadin se fait dans une session.
 * </p>
 * <p>
 * Cette classe rassemble dans ses attributs toutes les informations utiles qui
 * doivent être passées d'une page à l'autre dans une même session. Exemple
 * classique : l'utilisateur se connecte sur une page, et l'identité de
 * l'utilisateur connecté doit être conservé pour savoir par exemple les actions
 * qu'il a droit de faire, ou pour afficher des informations spécifique à cet
 * utilisateur.
 * </p>
 * <p>
 * V2 : la connexion à la base de donnée est dorénavent gérée au travers d'une
 * "connection pool" : voir {@link fr.insa.beuvron.vaadin.utils.ConnectionPool}
 * </p>
 * <p>
 * Les informations de sessions doivent être Serializables. La classe
 * SessionInfo implemente donc l'interface générale de java
 * {@link java.io.Serializable} et nous nous limitons à des attributs de type
 * basique (String,int ...)
 * </p>
 *
 * @author francois
 */
public class SessionInfo implements Serializable {

    /**
     * permet de s'assurer qu'une version sauvegardée est compatible avec la
     * version actuelle de la classe.
     * <pre>
     * <p> Si le serialVersionUID d'une instance sauvegardée ne correspond pas à la
     * version courante, une erreur {@link java.io.InvalidClassException} sera signalée
     * </p> <p>
     * Note : dans de nombreux exemples en ligne, la valeur du serialVersionUID est
     * fixée à une valeur "aléatoire". Je ne comprends pas pourquoi. Nous fixons
     * ci-dessous que la nous sommes à la version 1. Il suffit de fixer cette
     * valeur à 2 (ou n'importe quelle valeur hors 1) pour signaler que la classe
     * a une nouvelle version.
     * </p>
     * </pre>
     */
    private static final long serialVersionUID = 1L;

    /**
     * identificateur du partenaire actuellement connecté, null si aucun
     * partenaire connecté.
     */
    private Integer loggedPart;

    /**
     * le refPartenaire du partenaire connecté; null si non connecté.
     */
    private String partRef;

    public SessionInfo() {
        this.loggedPart = null;
        this.partRef = null;
    }

    /**
     * get the SessionInfo associated with the current session or create a new
     * SessionInfo and associate it with the current session.
     *
     * @return the SessionInfo associated with current session
     */
    public static SessionInfo getOrCreateCurSessionInfo() {
        VaadinSession curS = VaadinSession.getCurrent();
        SessionInfo res = curS.getAttribute(SessionInfo.class);
        if (res == null) {
            res = new SessionInfo();
            curS.setAttribute(SessionInfo.class, res);
        }
        return res;
    }

    public static void doLogin(Partenaire p) {
        SessionInfo cur = getOrCreateCurSessionInfo();
        cur.loggedPart = p.getId();
        cur.partRef = p.getRefPartenaire();
    }

    public static boolean connected() {
        SessionInfo cur = getOrCreateCurSessionInfo();
        return cur.loggedPart != null;
    }

    public static void doLogout() {
        VaadinSession.getCurrent().close();
    }

    public static Integer getLoggedPartId() {
        SessionInfo cur = getOrCreateCurSessionInfo();
        return cur.loggedPart;
    }

    /**
     * @return the partRef
     */
    public static String getLoggedPartRef() {
        SessionInfo cur = getOrCreateCurSessionInfo();
        return cur.partRef;
    }

}
