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

    private int INE;
    private String nomEtudiant;
    private String prenom;
    private String classe;
    private int annee;
    private int classement;
    private String mdp;

    /**
     * Création d'un nouveau Etudiant en mémoire, non existant dans la base de données.
     *
     * @param INE
     */
    public Etudiant(int INE) {
        this.INE = INE;
    }

    // Nouveau constructeur pour initialiser le score
    public Etudiant(int INE, String nomEtudiant, String prenom, String classe, int annee, int classement, String mdp) {
        this.INE = INE;
        this.nomEtudiant = nomEtudiant;
        this.prenom = prenom;
        this.classe = classe;
        this.annee = annee;
        this.classement = classement;
        this.mdp = mdp;
    }

    @Override
    public String toString() {
        return "Etudiant{" +
                "INE=" + this.getINE() +
                ", nom='" + nomEtudiant + '\'' +
                ", prenom='" + prenom + '\'' +
                ", classe='" + classe + '\'' +
                ", annee=" + annee +
                ", classement=" + classement +
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
    public int saveInDB(Connection con) throws SQLException, EntiteDejaSauvegardee {
        // Vérifie si l'étudiant a déjà été sauvegardé
        if (this.getINE() != -1) {
            throw new EntiteDejaSauvegardee(); // Lance une exception si l'étudiant a déjà un ID
        }

        try (PreparedStatement insert = con.prepareStatement(
                "INSERT INTO etudiant (nom, prenom, classe, annee, classement, mdp) VALUES ( ?, ?, ?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {

            // Remplissage des paramètres
            insert.setString(1, this.nomEtudiant);
            insert.setString(2, this.prenom);
            insert.setString(3, this.classe);
            insert.setInt(4, this.annee);
            insert.setInt(5, this.classement);
            insert.setString(6, this.mdp);

            insert.executeUpdate();

            try (ResultSet rid = insert.getGeneratedKeys()) {
                if (rid.next()) {  // Vérifie s'il y a une clé générée
                    this.INE = rid.getInt(1);  // Associe la clé générée à l'attribut idEtudiant
                    return this.getINE();      // Retourne l'idEtudiant
                } else {
                    throw new SQLException("Aucune clé générée pour l'insertion."); // Gérer le cas d'échec
                }
            }
        }
    }

    public static List<Etudiant> tousLesEtudiants(Connection con) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                "SELECT INE, nom, prenom, classe, annee, classement, mdp FROM etudiant")) { // Ajout de score dans la requête
            ResultSet rs = pst.executeQuery();
            List<Etudiant> res = new ArrayList<>();
            while (rs.next()) {
                Etudiant etu = new Etudiant(rs.getInt("INE"));
                etu.nomEtudiant = rs.getString("nom");
                etu.prenom = rs.getString("prenom");
                etu.classe = rs.getString("classe");
                etu.annee = rs.getInt("annee");
                etu.classement = rs.getInt("classement");
                etu.mdp = rs.getString("mdp");
                res.add(etu);
            }
            return res;
        }
    }

    public static int creeConsole(Connection con) throws SQLException, EntiteDejaSauvegardee {
        String nom = ConsoleFdB.entreeString("Nom : ");
        String prenom = ConsoleFdB.entreeString("Prénom : ");
        String classe = ConsoleFdB.entreeString("Classe : ");
        int annee = ConsoleFdB.entreeInt("Année : ");
        int classement = ConsoleFdB.entreeInt("Classement : ");
        String mdp = ConsoleFdB.entreeString("Mot de passe : ");

        Etudiant nouveau = new Etudiant(-1, nom, prenom, classe, annee, classement, mdp);
        return nouveau.saveInDB(con);
    }


    /**
     * @param idEtudiant the idEtudiant to set
     */
    public void setIdEtudiant(int idEtudiant) {
        this.INE = idEtudiant;
    }

    /**
     * @return the idEtudiant
     */
    public int getINE() {
        return INE;
    }

    // Getter pour le score
    /*public float getScore() {
        return score;
    }

    // Setter pour le score
    public void setScore(int score) {
        this.score = score;
    }*/
    
    /**
 * Exception levée lorsqu'une entité est déjà sauvegardée dans la base.
 */
    class EntiteDejaSauvegardee extends Exception {
    public EntiteDejaSauvegardee() {
        super("L'entité est déjà sauvegardée dans la base de données.");
    }
}
}
