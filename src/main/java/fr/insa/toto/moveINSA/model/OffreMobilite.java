package fr.insa.toto.moveINSA.model;

import fr.insa.beuvron.utils.ConsoleFdB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe "miroir" de la table offremobilite.
 * <p>
 * pour un commentaire plus détaillé sur ces classes "miroir", voir dans la
 * classe Partenaire
 * </p>
 *
 * @author francois
 */
public class OffreMobilite {

    private int idOffre;
    private int nbrPlaces;
    private String proposePar;
    private int semestre;
    private int niveauScolaire;
    private String dispositif;
    private String nomOffre;
    private String specialiteAssocie;

    /**
     * Création d'une nouvelle Offre en mémoire, non existante dans la base de
     * données.
     * @param nbrPlaces
     * @param proposePar
     * @param semestre
     * @param niveauScolaire
     * @param dispositif
     * @param nomOffre
     * @param specialiteAssocie
     */
    public OffreMobilite(int nbrPlaces, String proposePar, int semestre, int niveauScolaire, String dispositif, String nomOffre, String specialiteAssocie) {
        this(-1, nbrPlaces, proposePar, semestre, niveauScolaire, dispositif, nomOffre, specialiteAssocie);
    }

    /**
     * Création d'une Offre retrouvée dans la base de données.
     * @param id
     * @param nbrPlaces
     * @param proposePar
     * @param semestre
     * @param niveauScolaire
     * @param dispositif
     * @param nomOffre
     * @param specialiteAssocie
     */
    public OffreMobilite(int id, int nbrPlaces, String proposePar, int semestre, int niveauScolaire, String dispositif, String nomOffre, String specialiteAssocie) {
        this.idOffre = id;
        this.nbrPlaces = nbrPlaces;
        this.proposePar = proposePar;
        this.semestre = semestre;
        this.niveauScolaire = niveauScolaire;
        this.dispositif = dispositif;
        this.nomOffre = nomOffre;
        this.specialiteAssocie = specialiteAssocie;
    }

    @Override
    public String toString() {
        return "OffreMobilite{" +
                "id=" + this.getId() +
                ", nbrPlaces=" + nbrPlaces +
                ", proposePar=" + proposePar +
                ", semestre=" + semestre +
                ", niveauScolaire=" + niveauScolaire +
                ", dispositif='" + dispositif + '\'' +
                ", nomOffre='" + nomOffre + '\'' +
                ", specialiteAssocie='" + specialiteAssocie + '\'' +
                '}';
    }

    /**
     * Sauvegarde une nouvelle entité et retourne la clé affectée automatiquement
     * par le SGBD.
     * <p>
     * La clé est également sauvegardée dans l'attribut id.
     * </p>
     *
     * @param con
     * @return la clé de la nouvelle entité dans la table de la BdD
     * @throws SQLException si autre problème avec la BdD
     */
    public int saveInDB(Connection con) throws SQLException {
        if (this.getId() != -1) {
            throw new fr.insa.toto.moveINSA.model.EntiteDejaSauvegardee();
        }
        try (PreparedStatement insert = con.prepareStatement(
                "INSERT INTO offremobilite (nbrplaces, proposepar, semestre, niveauScolaire, dispositif, nomOffre, specialiteAssocie) VALUES (?,?,?,?,?,?,?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            insert.setInt(1, this.nbrPlaces);
            insert.setString(2, this.proposePar);
            insert.setInt(3, this.semestre);
            insert.setInt(4, this.niveauScolaire);
            insert.setString(5, this.dispositif);
            insert.setString(6, this.nomOffre);
            insert.setString(7, this.specialiteAssocie);
            insert.executeUpdate();
            
            try (ResultSet rid = insert.getGeneratedKeys()) {
                if (rid.next()) {
                    this.idOffre = rid.getInt(1);
                    return this.getId();
                } else {
                    throw new SQLException("Échec de la création de l'offre, aucune clé générée.");
                }
            }
        }
    }

    /**
     * Récupère toutes les offres de mobilité de la base de données.
     *
     * @param con la connexion à la base de données
     * @return une liste de toutes les offres de mobilité
     * @throws SQLException si un problème survient lors de la récupération
     */
    public static List<OffreMobilite> toutesLesOffres(Connection con) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                "SELECT id, nbrplaces, proposepar, semestre, niveauScolaire, dispositif, nomOffre, specialiteAssocie FROM offremobilite")) {
            ResultSet rs = pst.executeQuery();
            List<OffreMobilite> res = new ArrayList<>();
            while (rs.next()) {
                res.add(new OffreMobilite(
                        rs.getInt("id"), 
                        rs.getInt("nbrplaces"), 
                        rs.getString("proposepar"), 
                        rs.getInt("semestre"), 
                        rs.getInt("niveauScolaire"), 
                        rs.getString("dispositif"), 
                        rs.getString("nomOffre"), 
                        rs.getString("specialiteAssocie")
                ));
            }
            return res;
        }
    }

    /**
     * Crée une offre de mobilité en demandant les informations via la console.
     *
     * @param con la connexion à la base de données
     * @return l'ID de l'offre créée
     * @throws SQLException si un problème survient lors de l'enregistrement
     */
    public static int creeConsole(Connection con) throws SQLException {
        int nbr = ConsoleFdB.entreeInt("Nombre de places : ");
        String par = ConsoleFdB.entreeString("Proposé par : ");
        int s = ConsoleFdB.entreeInt("Semestre proposé : ");
        int niv = ConsoleFdB.entreeInt("Niveau scolaire : ");
        String dispositif = ConsoleFdB.entreeString("Type de dispositif : ");
        String nom = ConsoleFdB.entreeString("Nom de l'offre : ");
        String spe = ConsoleFdB.entreeString("Pour quelle spécialité : ");
        
        OffreMobilite nouveau = new OffreMobilite(nbr, par, s, niv, dispositif, nom, spe);
        return nouveau.saveInDB(con);
    }

    /**
     * @return the id
     */
    public int getId() {
        return idOffre;
    }

}
