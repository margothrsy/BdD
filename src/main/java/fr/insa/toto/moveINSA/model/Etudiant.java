package fr.insa.toto.moveINSA.model;

import fr.insa.beuvron.utils.ConsoleFdB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe "miroir" de la table Etudiant.
 * <p>
 * Pour interfacer facilement un programme Java avec une base de données relationnelle,
 * il est souvent pratique de définir des classes correspondant aux tables d'entités.
 * </p>
 * <p>
 * Nous éviterons l'utilisation d'un ORM pour rester dans l'esprit pédagogique.
 * </p>
 *
 * @author francois
 */
public class Etudiant {

    private String INE;
    private String nomEtudiant;
    private String prenom;
    private String classe;
    private int annee;
    private int classement;
    private String mdp;

    /**
     * Constructeur minimaliste.
     *
     * @param INE Identifiant de l'étudiant
     */
    public Etudiant(String INE) {
        this.INE = INE;
    }

    public int getClassement() {
        return classement;
    }
    
    
    public double score() {
        if (classe == null) {
            throw new IllegalStateException("L'étudiant n'est associé à aucune classe.");
        }
        int effectif = classe.getEffectifClasse();
        if (effectif <= 0) {
            throw new IllegalStateException("L'effectif de la classe doit être strictement positif.");
        }
        return (double) classement / effectif;
    }

    /**
     * Constructeur complet.
     */
    public Etudiant(String INE, String nomEtudiant, String prenom, String classe, int annee, int classement, String mdp) {
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
                "INE='" + INE + '\'' +
                ", nom='" + nomEtudiant + '\'' +
                ", prenom='" + prenom + '\'' +
                ", classe='" + classe + '\'' +
                ", annee=" + annee +
                ", classement=" + classement +
                ", mdp='" + mdp + '\'' +
                '}';
    }

    /**
     * Sauvegarde une nouvelle entité dans la base de données.
     *
     * @param con Connexion à la base de données
     * @return L'identifiant (INE) généré
     * @throws EntiteDejaSauvegardee Si l'entité est déjà sauvegardée
     * @throws SQLException En cas de problème avec la base de données
     */
    public String saveInDB(Connection con) throws SQLException, EntiteDejaSauvegardee {
        if (this.INE != null) {
            throw new EntiteDejaSauvegardee(); // Exception si l'INE existe déjà
        }

        try (PreparedStatement insert = con.prepareStatement(
                "INSERT INTO etudiant (nom, prenom, classe, annee, classement, mdp) VALUES (?, ?, ?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {

            insert.setString(1, this.nomEtudiant);
            insert.setString(2, this.prenom);
            insert.setString(3, this.classe);
            insert.setInt(4, this.annee);
            insert.setInt(5, this.classement);
            insert.setString(6, this.mdp);

            insert.executeUpdate();

            try (ResultSet rid = insert.getGeneratedKeys()) {
                if (rid.next()) {
                    this.INE = rid.getString(1);
                    return this.INE;
                } else {
                    throw new SQLException("Aucune clé générée pour l'insertion.");
                }
            }
        }
    }

    /**
     * Retourne tous les étudiants de la base de données.
     */
    public static List<Etudiant> tousLesEtudiants(Connection con) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                "SELECT INE, nom, prenom, classe, annee, classement, mdp FROM etudiant")) {
            ResultSet rs = pst.executeQuery();
            List<Etudiant> res = new ArrayList<>();
            while (rs.next()) {
                Etudiant etu = new Etudiant(
                        rs.getString("INE"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("classe"),
                        rs.getInt("annee"),
                        rs.getInt("classement"),
                        rs.getString("mdp")
                );
                res.add(etu);
            }
            return res;
        }
    }

    /**
     * Création d'un étudiant via console.
     */
    public static String creeConsole(Connection con) throws SQLException, EntiteDejaSauvegardee {
        String nom = ConsoleFdB.entreeString("Nom : ");
        String prenom = ConsoleFdB.entreeString("Prénom : ");
        String classe = ConsoleFdB.entreeString("Classe : ");
        int annee = ConsoleFdB.entreeInt("Année : ");
        int classement = ConsoleFdB.entreeInt("Classement : ");
        String mdp = ConsoleFdB.entreeString("Mot de passe : ");

        Etudiant nouveau = new Etudiant(null, nom, prenom, classe, annee, classement, mdp);
        return nouveau.saveInDB(con);
    }

    public void setINE(String INE) {
        this.INE = INE;
    }

    public String getINE() {
        return INE;
    }
}

/**
 * Exception levée lorsqu'une entité est déjà sauvegardée.
 */
class EntiteDejaSauvegardee extends Exception {
    public EntiteDejaSauvegardee() {
        super("L'entité est déjà sauvegardée dans la base de données.");
    }
}