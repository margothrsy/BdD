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
import fr.insa.toto.moveINSA.model.EntiteDejaSauvegardee;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe "miroir" de la table partenaire.
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
public class Partenaire {

    private int idPartenaire;
    private String refPartenaire;
    private String ville;
    private String pays;

    /*
     * Création d'un nouveau Partenaire en mémoire, non existant dans la base de données.
     * Par défaut, l'ID sera -1 car l'objet n'est pas encore sauvegardé.
    *
    * @param refPartenaire La référence du partenaire
    * @param ville La ville du partenaire (peut être null)
    * @param pays Le pays du partenaire (peut être null)
    */
public Partenaire(String refPartenaire, String ville, String pays) {
    this(-1, refPartenaire, ville, pays);
}

    /**
     * création d'un Partenaire retrouvé dans la base de donnée.
     
    * @param idPartenaire L'identifiant du partenaire
    * @param refPartenaire La référence du partenaire
    * @param ville La ville du partenaire
    * @param pays Le pays du partenaire
    */
    
    public Partenaire(int idPartenaire, String refPartenaire, String ville, String pays) {
        this.idPartenaire = idPartenaire;
        this.refPartenaire = refPartenaire;
        this.ville = null; // Si la ville n'est pas disponible, tu peux la mettre à null
        this.pays = null; // idem pour pays
    }

    @Override
    public String toString() {
        return "Partenaire{" 
        + "idPartenaire=" + this.getIdPartenaire()
        + ", refPartenaire='" + refPartenaire + '\'' 
        + ", ville='" + ville + '\'' 
        + ", pays='" + pays + '\'' 
        + '}';
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
        if (this.getIdPartenaire() != -1) {
            throw new EntiteDejaSauvegardee();
        }
        try (PreparedStatement insert = con.prepareStatement(
                "insert into partenaire (refPartenaire, ville, pays) values (?,?,?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            // Assigner les valeurs des attributs refPartenaire, ville et pays
            insert.setString(1, this.getRefPartenaire());
            insert.setString(2, this.getVille()!= null ? this.getVille() : null);
            insert.setString(3, this.getPays()!= null ? this.getPays() : null);
            insert.executeUpdate();
            // récupérer la clé générée (ID)
            try (ResultSet rid = insert.getGeneratedKeys()) {
                rid.next();
                this.idPartenaire = rid.getInt(1); // récupération de l'ID généré
                return this.getIdPartenaire();
            }
        }
    }

    public static List<Partenaire> tousLesPartenaires(Connection con) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                "select idPartenaire,refPartenaire from partenaire")) {
            ResultSet rs = pst.executeQuery();
            List<Partenaire> res = new ArrayList<>();
            while (rs.next()) {
                res.add(new Partenaire(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)));
            }
            return res;
        }
    }
    public static List<String> toutesLesVilles(Connection con) throws SQLException {
    try (PreparedStatement pst = con.prepareStatement(
            "select distinct ville from partenaire")) {
        ResultSet rs = pst.executeQuery();
        List<String> villes = new ArrayList<>();
        while (rs.next()) {
            villes.add(rs.getString(1)); // Récupère la colonne 'ville'
        }
        return villes;
    }
}
    public static List<String> tousLesPays(Connection con) throws SQLException {
    try (PreparedStatement pst = con.prepareStatement(
            "SELECT DISTINCT pays FROM partenaire")) {
        ResultSet rs = pst.executeQuery();
        List<String> pays = new ArrayList<>();
        while (rs.next()) {
            pays.add(rs.getString(1)); // Récupère la colonne 'pays'
        }
        return pays;
    }
}

    public static int creeConsole(Connection con) throws SQLException {
    // Demander à l'utilisateur de saisir la référence du partenaire
    String refPartenaire = ConsoleFdB.entreeString("refPartenaire : ");
    // Demander à l'utilisateur de saisir la ville
    String ville = ConsoleFdB.entreeString("Ville : ");
    // Demander à l'utilisateur de saisir le pays
    String pays = ConsoleFdB.entreeString("Pays : ");
    // Créer un nouvel objet Partenaire avec les informations saisies
    Partenaire nouveau = new Partenaire(refPartenaire, ville, pays);
    // Sauvegarder le nouveau partenaire dans la base de données
    return nouveau.saveInDB(con);
}

    public static Partenaire selectInConsole(Connection con) throws SQLException {
        return ListUtils.selectOne("choisissez un partenaire :",
                tousLesPartenaires(con), (elem) -> elem.getRefPartenaire());
    }
    public static Partenaire selectVilleInConsoleFromPartenaire(Connection con) throws SQLException {
        return ListUtils.selectOne("Choisissez une ville parmi les partenaires :", 
                tousLesPartenaires(con), (elem) -> elem.getVille());
    }
    public static Partenaire selectPaysInConsole(Connection con) throws SQLException {
    return ListUtils.selectOne("Choisissez un pays :", 
                tousLesPartenaires(con), (elem) -> elem.getPays());
    }
    /**
     * @return the refPartenaire
     */
    public String getRefPartenaire() {
        return refPartenaire;
    }
    public String getVille() {
        return ville;
    }
    public String getPays() {
        return pays;
    }

    /**
     * @param refPartenaire the refPartenaire to set
     */
    public void setRefPartenaire(String refPartenaire) {
        this.refPartenaire = refPartenaire;
    }
    public void setVille(String ville) {
        this.ville = ville;
    }
    public void setPays(String pays) {
        this.pays = pays;
    }

    /**
     * @return the id
     */
    public int getIdPartenaire() {
        return idPartenaire;
    }
    
}