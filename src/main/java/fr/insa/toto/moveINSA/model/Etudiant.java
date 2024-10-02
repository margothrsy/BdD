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
package fr.insa.toto.moveINSA.model;

import fr.insa.beuvron.utils.ConsoleFdB;
import fr.insa.beuvron.utils.list.ListUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe "miroir" de la table Etudiant.
 * <p>
 * pour interfacer facilement un programme java avec une base de donnée
 * relationnelle, il est souvent pratique de définir des classes correspondant
 * au tables d'entité de la base de données.
 * </p>
 * <p>
 * on pourrait aller plus loin et représenter également les relations et les
 * hiérarchies de classes. Mais ce serait refaire (en moins bien) ce que l'on
 * appelle un ORM : Object Relational Mapper. Il existe un ORM standard en Java
 * : JPA (Java Persistency API).
 * </p>
 * <p>
 * l'utilisation d'un ORM masque les détails de la base de données relationnelle
 * sous-jacente ainsi que le langage SQL. Hors, le but de ce module est de voir
 * l'utilisation de SQL et des bases relationnelles. Nous n'utiliserons donc pas
 * d'ORM.
 * </p>
 * <p>
 * Pour les relations, nous nous contenterons de conserver les identificateurs
 * comme cela est fait dans les tables (voir attribut proposePar de la classe
 * OffreMobilité par exemple.
 * </p>
 *
 * @author francois
 */
public class Etudiant {

    private int id;
    private String refEtudiant;

    /**
     * création d'un nouveau Etudiant en mémoire, non existant dans la Base de
     * donnée.
     *
     * @param refEtudiant
     */
    public Etudiant(String refEtudiant) {
        this(-1,refEtudiant);
    }

    /**
     * création d'un Etudiant retrouvé dans la base de donnée.
     *
     * @param refEtudiant
     */
    public Etudiant(int id, String refEtudiant) {
        this.id = id;
        this.refEtudiant = refEtudiant;
    }

    @Override
    public String toString() {
        return "Etudiant{" + "id =" + this.getId() + " ; refEtudiant=" + refEtudiant + '}';
    }

    /**
     * Sauvegarde une nouvelle entité et retourne la clé affecté automatiquement
     * par le SGBD.
     * <p>
     * la clé est également sauvegardée dans l'attribut id
     * </p>
     *
     * @param con
     * @return la clé de la nouvelle entité dans la table de la BdD
     * @throws EntiteDejaSauvegardee si l'id de l'entité est différent de -1
     * @throws SQLException si autre problème avec la BdD
     */
    public int saveInDB(Connection con) throws SQLException {
        if (this.getId() != -1) {
            throw new EntiteDejaSauvegardee();
        }
        try (PreparedStatement insert = con.prepareStatement(
                "insert into etudiant (refEtudiant) values (?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            insert.setString(1, this.getRefEtudiant());
            insert.executeUpdate();
            try (ResultSet rid = insert.getGeneratedKeys()) {
                rid.next();
                this.id = rid.getInt(1);
                return this.getId();
            }
        }
    }

    public static List<Etudiant> tousLesEtudiant(Connection con) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                "select id,refEtudiant from etudiant")) {
            ResultSet rs = pst.executeQuery();
            List<Etudiant> res = new ArrayList<>();
            while (rs.next()) {
                res.add(new Etudiant(rs.getInt(1), rs.getString(2)));
            }
            return res;
        }
    }

    public static int creeConsole(Connection con) throws SQLException {
        String idP = ConsoleFdB.entreeString("refEtudiant : ");
       Etudiant nouveau = new Etudiant (idP);
        return nouveau.saveInDB(con);
    }

    public static Etudiant selectInConsole(Connection con) throws SQLException {
        return ListUtils.selectOne("choisissez un Etudiant :",
                tousLesEtudiant(con), (elem) -> elem.getRefEtudiant());
    }

    /**
     * @return the refEtudiant
     */
    public String getRefEtudiant() {
        return refEtudiant;
    }

    /**
     * @param refEtudiant the refEtudiant to set
     */
    public void setRefEtudiant(String refEtudiant) {
        this.refEtudiant = refEtudiant;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

}

