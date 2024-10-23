package fr.insa.toto.moveINSA.model;

import fr.insa.beuvron.utils.ConsoleFdB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente une candidature soumise par un étudiant pour une offre de mobilité.
 */
public class Candidature {

    private int idCandidature;
    private int idOffre; // Référence à l'offre de mobilité
    private int idEtudiant; // Référence à l'étudiant
    private int ordre; // Ordre de préférence de la candidature
    private int classementEtudiant; // Classement de l'étudiant pour cette offre
    private String date; // Date de la candidature

    // Constructeur
    public Candidature(int idCandidature, int idOffre, int idEtudiant, int ordre, int classementEtudiant, String date) {
        this.idCandidature = idCandidature;
        this.idOffre = idOffre;
        this.idEtudiant = idEtudiant;
        this.ordre = ordre;
        this.classementEtudiant = classementEtudiant;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Candidature{" +
                "idCandidature=" + idCandidature +
                ", idOffre=" + idOffre +
                ", idEtudiant=" + idEtudiant +
                ", ordre=" + ordre +
                ", classementEtudiant=" + classementEtudiant +
                ", date='" + date + '\'' +
                '}';
    }

    /**
     * Sauvegarde une nouvelle candidature dans la base de données et retourne l'ID généré.
     *
     * @param con la connexion à la base de données
     * @return l'ID généré par la base de données
     * @throws SQLException si un problème survient lors de l'interaction avec la base de données
     */
    public int saveInDB(Connection con) throws SQLException {
        
        try (PreparedStatement insert = con.prepareStatement(
                "insert into candidature (idOffre, idEtudiant, ordre, classementEtudiant, date) values (?,?,?,?,?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            insert.setInt(1, this.idOffre);
            insert.setInt(2, this.idEtudiant);
            insert.setInt(3, this.ordre);
            insert.setInt(4, this.classementEtudiant);
            insert.setString(5, this.date);
            insert.executeUpdate();
            // Récupère la clé générée pour la nouvelle candidature
            try (ResultSet rid = insert.getGeneratedKeys()) {
                if (rid.next()) {
                    this.idCandidature = rid.getInt(1);
                    return this.idCandidature;
                } else {
                    throw new SQLException("Échec de la création de la candidature, aucune clé générée.");
                }
            }
        }
    }
    
    public static int nombreCandidaturesEtudiant(Connection con, int idEtudiant) throws SQLException {
    String sql = "select count(*) from candidature where idEtudiant = ?";
    try (PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setInt(1, idEtudiant);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return rs.getInt(1); // Retourner le nombre de candidatures
        } else {
            return 0; // Aucune candidature trouvée
            }
        }
    }

    /**
     * Récupère toutes les candidatures de la base de données.
     *
     * @param con la connexion à la base de données
     * @return une liste de toutes les candidatures
     * @throws SQLException si un problème survient lors de la récupération des données
     */
    public static List<Candidature> toutesLesCandidatures(Connection con) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                "select idCandidature, idOffre, idEtudiant, ordre, classementEtudiant, date from candidature")) {
            ResultSet rs = pst.executeQuery();
            List<Candidature> res = new ArrayList<>();
            while (rs.next()) {
                res.add(new Candidature(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getInt(5),
                        rs.getString(6)
                ));
            }
            return res;
        }
    }
    
   public static List<Candidature> CandidatureEtudiant(Connection con, int idEtudiant) throws SQLException {
    String sql = "select idCandidature, idOffre, ordre, classementEtudiant, date from candidature where idEtudiant = ?";
    try (PreparedStatement pst = con.prepareStatement(sql)) {
        // Assigner l'idEtudiant passé en paramètre à la requête
        pst.setInt(1, idEtudiant);
        
        // Exécuter la requête
        ResultSet rs = pst.executeQuery();
        
        // Créer une liste pour stocker les résultats
        List<Candidature> res = new ArrayList<>();
        
        // Parcourir les résultats et ajouter chaque candidature à la liste
        while (rs.next()) {
            res.add(new Candidature(
                    rs.getInt("idCandidature"),
                    rs.getInt("idOffre"),
                    idEtudiant, // On connaît déjà l'id de l'étudiant
                    rs.getInt("ordre"),
                    rs.getInt("classementEtudiant"),
                    rs.getString("date")
            ));
        }
        
        return res; // Retourner la liste des candidatures de l'étudiant
    }
}


    /**
     * Crée une nouvelle candidature à partir des informations saisies dans la console.
     *
     * @param con la connexion à la base de données
     * @return l'ID de la nouvelle candidature
     * @throws SQLException si un problème survient lors de l'enregistrement
     */
    public static int creeConsole(Connection con) throws SQLException {
    // Sélectionne l'étudiant via la console
    int idEtudiant = ConsoleFdB.entreeInt("ID de l'étudiant : ");
    
    // Vérifier le nombre de candidatures existantes pour cet étudiant
    int nombreCandidatures = Candidature.nombreCandidaturesEtudiant(con, idEtudiant);
    if (nombreCandidatures >= 5) {
        System.out.println("Cet étudiant a déjà soumis 5 candidatures. Impossible d'en soumettre davantage.");
        return -1; // Indiquer qu'il n'est pas possible de créer une nouvelle candidature
    }
    
    // Sélectionne une offre de mobilité via la console
    int idOffre = ConsoleFdB.entreeInt("ID de l'offre de mobilité : ");
    int ordre = ConsoleFdB.entreeInt("Ordre de préférence : ");
    int classement = ConsoleFdB.entreeInt("Classement de l'étudiant : ");
    String date = ConsoleFdB.entreeString("Date de la candidature (YYYY-MM-DD) : ");

    // Crée une nouvelle candidature avec un ID temporaire de -1
    Candidature nouvelle = new Candidature(-1, idOffre, idEtudiant, ordre, classement, date);
    return nouvelle.saveInDB(con);
    }


    // Getters et setters

    public int getIdCandidature() {
        return idCandidature;
    }

    public int getIdOffre() {
        return idOffre;
    }

    public int getIdEtudiant() {
        return idEtudiant;
    }

    public int getOrdre() {
        return ordre;
    }

    public int getClassementEtudiant() {
        return classementEtudiant;
    }

    public String getDate() {
        return date;
    }
}
