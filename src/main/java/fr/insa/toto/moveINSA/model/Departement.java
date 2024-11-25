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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe "miroir" de la table Departement.
 */
public class Departement {

    private int idDepartement;
    private String nomDepartement;
    private String specialite; // Renommer l'attribut pour éviter la confusion

    /**
     * Création d'une Classe retrouvée dans la base de données.
     */
    public Departement(int idDepartement) {
        this.idDepartement = idDepartement;
    }

    @Override
    public String toString() {
        return "Departement{" +
                "IdDepartement=" + this.getIdDepartement() +
                " ; NomDepartement=" + this.nomDepartement +
                " ; Specialite=" + this.specialite + // Correction du nom de la variable
                '}';
    }

    /**
     * Sauvegarde une nouvelle entité et retourne la clé affectée automatiquement
     * par le SGBD.
     * <p>
     * La clé est également sauvegardée dans l'attribut idDepartement.
     * </p>
     *
     * @param con la connexion à la base de données
     * @return la clé de la nouvelle entité dans la table de la BdD
     * @throws EntiteDejaSauvegardee si l'id de l'entité est différent de -1
     * @throws SQLException si un autre problème survient avec la BdD
     */
    public int saveInDB(Connection con) throws SQLException, EntiteDejaSauvegardee {
        if (this.getIdDepartement() != -1) {
            throw new fr.insa.toto.moveINSA.model.EntiteDejaSauvegardee();
        }

        try (PreparedStatement insert = con.prepareStatement(
                "INSERT INTO departement (nomDepartement, specialite) VALUES (?, ?)", // Correction du nom de la table
                PreparedStatement.RETURN_GENERATED_KEYS)) {

            insert.setString(1, this.nomDepartement);
            insert.setString(2, this.specialite); // Utilisation de la variable correcte

            insert.executeUpdate();

            try (ResultSet rid = insert.getGeneratedKeys()) {
                if (rid.next()) {
                    this.idDepartement = rid.getInt(1);
                    return this.getIdDepartement();
                } else {
                    throw new SQLException("Aucune clé générée pour l'insertion.");
                }
            }
        }
    }

    /**
     * Retourne tous les départements enregistrés dans la base de données.
     */
    public static List<Departement> tousLesDepartements(Connection con) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                "SELECT idDepartement, nomDepartement, specialite FROM departement")) { // Correction du nom de la table
            ResultSet rs = pst.executeQuery();
            List<Departement> res = new ArrayList<>(); // Utilisation de la bonne liste
            while (rs.next()) {
                Departement departement = new Departement(rs.getInt(1)); // Création d'un objet Departement
                departement.nomDepartement = rs.getString(2);
                departement.specialite = rs.getString(3); // Utilisation de l'attribut correct
                res.add(departement);
            }
            return res;
        }
    }

    /**
     * Création d'un nouveau département via la console et sauvegarde dans la base de données.
     */
    public static int creeConsole(Connection con) throws SQLException {
        String nomDepartement = ConsoleFdB.entreeString("Nom du département : "); // Correction du nom de la variable
        String specialite = ConsoleFdB.entreeString("Spécialité : ");
        
        Departement nouveau = new Departement(-1); // Utilisation de Departement avec un ID par défaut
        nouveau.nomDepartement = nomDepartement; // Assignation à l'attribut
        nouveau.specialite = specialite; // Assignation à l'attribut

        return nouveau.saveInDB(con);
    }

    /**
     * @return l'identifiant du département
     */
    public int getIdDepartement() {
        return idDepartement;
    }

    // Getters pour nomDepartement et specialite
    public String getNomDepartement() {
        return nomDepartement;
    }

    public String getSpecialite() {
        return specialite; // Correction du nom de la méthode pour correspondre à l'attribut
    }
}
