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
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant un étudiant.
 */
public class Etudiant {

    private int idEtudiant;
    private String nomEtudiant;
    private String prenom;
    private String classe;
    private int annee;
    private int classement;
    private int INE;
    private String mdp;

    /**
     * Création d'un nouveau Etudiant en mémoire, non existant dans la base de données.
     *
     * @param idEtudiant
     */
    public Etudiant(int idEtudiant) {
        this.idEtudiant = idEtudiant;
    }

    // Nouveau constructeur pour initialiser le score
    public Etudiant(int idEtudiant, String nomEtudiant, String prenom, String classe, int annee, int classement, int INE, String mdp) {
        this.idEtudiant = idEtudiant;
        this.nomEtudiant = nomEtudiant;
        this.prenom = prenom;
        this.classe = classe;
        this.annee = annee;
        this.classement = classement;
        this.INE = INE;
        this.mdp = mdp;
    }

    @Override
    public String toString() {
        return "Etudiant{" +
                "idEtudiant=" + this.getIdEtudiant() +
                ", nom='" + nomEtudiant + '\'' +
                ", prenom='" + prenom + '\'' +
                ", classe='" + classe + '\'' +
                ", annee=" + annee +
                ", classement=" + classement +
                ", INE=" + INE +
                ", mdp='" + mdp + '\'' +
                '}';
    }

    /**
     * Sauvegarde une nouvelle entité et retourne la clé affectée automatiquement par le SGBD.
     * <p>
     * La clé est également sauvegardée dans l'attribut idEtudiant.
     * </p>
     *
     * @param con la connexion à la base de données
     * @return la clé de la nouvelle entité dans la table de la BdD
     * @throws EntiteDejaSauvegardee si l'id de l'entité est différent de -1
     * @throws SQLException si un problème survient avec la BdD
     */
    public int saveInDB(Connection con) throws SQLException {
        // Vérifie si l'étudiant a déjà été sauvegardé
        if (this.getIdEtudiant() != -1) {
            throw new EntiteDejaSauvegardee(); // Lance une exception si l'étudiant a déjà un ID
        }

        try (PreparedStatement insert = con.prepareStatement(
                "INSERT INTO etudiant (nom, prenom, classe, annee, classement, INE, mdp) VALUES (?, ?, ?, ?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {

            // Remplissage des paramètres
            insert.setString(1, this.nomEtudiant);
            insert.setString(2, this.prenom);
            insert.setString(3, this.classe);
            insert.setInt(4, this.annee);
            insert.setInt(5, this.classement);
            insert.setInt(6, this.INE);
            insert.setString(7, this.mdp);

            insert.executeUpdate();

            try (ResultSet rid = insert.getGeneratedKeys()) {
                if (rid.next()) {  // Vérifie s'il y a une clé générée
                    this.idEtudiant = rid.getInt(1);  // Associe la clé générée à l'attribut idEtudiant
                    return this.getIdEtudiant();      // Retourne l'idEtudiant
                } else {
                    throw new SQLException("Aucune clé générée pour l'insertion."); // Gérer le cas d'échec
                }
            }
        }
    }

    public static List<Etudiant> tousLesEtudiants(Connection con) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                "SELECT id, nom, prenom, classe, annee, classement, INE, mdp FROM etudiant")) { // Ajout de score dans la requête
            ResultSet rs = pst.executeQuery();
            List<Etudiant> res = new ArrayList<>();
            while (rs.next()) {
                Etudiant etu = new Etudiant(rs.getInt(1));
                etu.nomEtudiant = rs.getString(2);
                etu.prenom = rs.getString(3);
                etu.classe = rs.getString(4);
                etu.annee = rs.getInt(5);
                etu.classement = rs.getInt(6);
                etu.INE = rs.getInt(7);
                etu.mdp = rs.getString(8);
                res.add(etu);
            }
            return res;
        }
    }

    public static int creeConsole(Connection con) throws SQLException {
        int idEtudiant = ConsoleFdB.entreeInt("refEtudiant : ");
        int score = ConsoleFdB.entreeInt("Score : "); // Demande du score à l'utilisateur
        Etudiant nouveau = new Etudiant(idEtudiant);
        return nouveau.saveInDB(con);
    }

    /**
     * @param idEtudiant the idEtudiant to set
     */
    public void setIdEtudiant(int idEtudiant) {
        this.idEtudiant = idEtudiant;
    }

    /**
     * @return the idEtudiant
     */
    public int getIdEtudiant() {
        return idEtudiant;
    }

    // Getter pour le score
    /*public float getScore() {
        return score;
    }

    // Setter pour le score
    public void setScore(int score) {
        this.score = score;
    }*/
}
