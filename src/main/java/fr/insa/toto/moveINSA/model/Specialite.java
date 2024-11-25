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
 * Classe "miroir" de la table Specialite.
 */
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Specialite {

    private int idSpecialite;
    private String nomSpecialite;
    private String offreSpecialite; // Ajout de l'attribut 'specialite' manquant

    /**
     * Création d'une Classe retrouvée dans la base de données.
     */
    public Specialite(int idSpecialite) {
        this.idSpecialite = idSpecialite;
    }

    @Override
    public String toString() {
        return "Specialite{" +
                "IdSpecialite=" + this.getIdSpecialite() +
                " ; NomSpecialite=" + this.nomSpecialite +
                " ; Specialite=" + this.offreSpecialite + // Ajout du '+' pour la concaténation
                '}';
    }

    /**
     * Sauvegarde une nouvelle entité et retourne la clé affectée automatiquement
     * par le SGBD.
     * <p>
     * La clé est également sauvegardée dans l'attribut idClasse.
     * </p>
     *
     * @param con la connexion à la base de données
     * @return la clé de la nouvelle entité dans la table de la BdD
     * @throws EntiteDejaSauvegardee si l'id de l'entité est différent de -1
     * @throws SQLException si un autre problème survient avec la BdD
     */
    public int saveInDB(Connection con) throws SQLException {
        if (this.getIdSpecialite() != -1) {
            throw new fr.insa.toto.moveINSA.model.EntiteDejaSauvegardee();
        }

        try (PreparedStatement insert = con.prepareStatement(
                "INSERT INTO specialite (nomSpecialite, specialite) VALUES (?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) { // Correction du nom de la table

            insert.setString(1, this.nomSpecialite);
            insert.setString(2, this.offreSpecialite); // Correction de l'index et de la variable

            insert.executeUpdate();

            try (ResultSet rid = insert.getGeneratedKeys()) {
                if (rid.next()) {
                    this.idSpecialite = rid.getInt(1);
                    return this.getIdSpecialite();
                } else {
                    throw new SQLException("Aucune clé générée pour l'insertion.");
                }
            }
        }
    }

    /**
     * Retourne toutes les spécialités enregistrées dans la base de données.
     */
    public static List<Specialite> toutesLesSpecialites(Connection con) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                "SELECT idSpecialite, nomSpecialite, specialite FROM Specialite")) { // Correction du nom de la table
            ResultSet rs = pst.executeQuery();
            List<Specialite> res = new ArrayList<>(); // Correction de la liste à utiliser
            while (rs.next()) {
                Specialite specialite = new Specialite(rs.getInt(1)); // Utilisation de Specialite
                specialite.nomSpecialite = rs.getString(2);
                specialite.offreSpecialite = rs.getString(3); // Ajout de l'attribut 'specialite'
                res.add(specialite); // Correction de la variable à ajouter
            }
            return res;
        }
    }

    /**
     * Création d'une nouvelle spécialité via la console et sauvegarde dans la base de données.
     */
    public static int creeConsole(Connection con) throws SQLException {
        String nomSpecialite = ConsoleFdB.entreeString("Nom de la specialite : "); // Correction du nom de la variable
        String specialite = ConsoleFdB.entreeString("Spécialité : ");
        
        Specialite nouveau = new Specialite(-1); // Utilisation de Specialite avec un ID par défaut
        nouveau.nomSpecialite = nomSpecialite;
        nouveau.offreSpecialite = specialite; // Assignation à l'attribut

        return nouveau.saveInDB(con);
    }

    /**
     * @return l'identifiant de la spécialité
     */
    public int getIdSpecialite() {
        return idSpecialite;
    }

    // Getters pour nomSpecialite et specialite si nécessaire
    public String getNomSpecialite() {
        return nomSpecialite;
    }

    public String getSpecialite() {
        return offreSpecialite;
    }
}
