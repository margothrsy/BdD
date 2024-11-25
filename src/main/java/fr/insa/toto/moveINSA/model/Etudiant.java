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
 * Pour interfacer facilement un programme Java avec une base de données relationnelle,
 * il est souvent pratique de définir des classes correspondant aux tables d'entités.
 * Nous éviterons l'utilisation d'un ORM pour rester dans l'esprit pédagogique.
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

    // Getters et setters
    public String getINE() {
        return INE;
    }

    public void setINE(String INE) {
        this.INE = INE;
    }

    public String getNomEtudiant() {
        return nomEtudiant;
    }

    
    public String getPrenom() {
        return prenom;
    }

    

    public String getClasse() {
        return classe;
    }

    

    public int getAnnee() {
        return annee;
    }

    
    public int getClassement() {
        return classement;
    }

    

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    /**
     * Calcule un score basé sur le classement et l'effectif de la classe.
     *
     * @param effectif Effectif total de la classe
     * @return Score de l'étudiant
     * @throws IllegalStateException Si l'étudiant n'est associé à aucune classe
     */
   public double score(Connection con) {
    if (classe == null) {
        throw new IllegalStateException("La classe n'est pas définie.");
    }

    try {
        // Récupère l'objet Classe à partir du nom de la classe
        Classe classeObjet = Classe.recupererParNomClasse(con, classe);
        if (classeObjet == null || classeObjet.getEffectifClasse() <= 0) {
            throw new IllegalStateException("Classe introuvable ou effectif invalide.");
        }

        // Calcul du score
        return (double) classement / classeObjet.getEffectifClasse();
    } catch (SQLException e) {
        throw new IllegalStateException("Erreur lors de la récupération de la classe.", e);
    }
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
            throw new EntiteDejaSauvegardee();
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
}


