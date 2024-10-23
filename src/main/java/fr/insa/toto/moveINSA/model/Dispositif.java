package fr.insa.toto.moveINSA.model;

import fr.insa.beuvron.utils.ConsoleFdB;
import fr.insa.beuvron.utils.list.ListUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Dispositif {

    private int idDispositif;
    private final String type;

    // Constructeur
    public Dispositif(int id, String type) {
        this.idDispositif = id;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Dispositif{" +
                "id=" + idDispositif +
                ", type='" + type + '\'' +
                '}';
    }

    /**
     * Sauvegarde une nouvelle entité et retourne la clé affectée automatiquement par le SGBD.
     *
     * @param con la connexion à la base de données
     * @return l'ID généré par la base de données
     * @throws SQLException si un problème avec la base de données survient
     */
    public int saveInDB(Connection con) throws SQLException {
        // Insertion dans la table dispositif
        try (PreparedStatement insert = con.prepareStatement(
                "insert into dispositif (type) values (?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            insert.setString(1, this.type);
            insert.executeUpdate();
            // Récupération de la clé générée
            try (ResultSet rid = insert.getGeneratedKeys()) {
                if (rid.next()) {
                    this.idDispositif = rid.getInt(1);  // Met à jour l'ID avec la valeur générée
                    return this.getId();
                } else {
                    throw new SQLException("Échec de la création du dispositif, aucune clé générée.");
                }
            }
        }
    }

    /**
     * Récupère tous les dispositifs de la base de données.
     *
     * @param con la connexion à la base de données
     * @return une liste de tous les dispositifs
     * @throws SQLException si un problème avec la base de données survient
     */
    public static List<Dispositif> tousLesDispositif(Connection con) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                "select id, type from dispositif")) {
            ResultSet rs = pst.executeQuery();
            List<Dispositif> res = new ArrayList<>();
            while (rs.next()) {
                res.add(new Dispositif(rs.getInt(1), rs.getString(2)));
            }
            return res;
        }
    }

    /**
     * Crée un nouveau dispositif en demandant les informations via la console.
     *
     * @param con la connexion à la base de données
     * @return l'ID du dispositif créé
     * @throws SQLException si un problème avec la base de données survient
     */
    public static int creeConsole(Connection con) throws SQLException {
        // Demande le type du dispositif à l'utilisateur
        String type = ConsoleFdB.entreeString("Type de dispositif : ");
        // Crée un nouveau dispositif avec un ID initial de -1 (non encore enregistré)
        Dispositif nouveau = new Dispositif(-1, type);
        return nouveau.saveInDB(con);  // Enregistre dans la base de données et retourne l'ID généré
    }
    
    public static Dispositif selectInConsole(Connection con) throws SQLException {
        return ListUtils.selectOne("Choisissez un type de dispositif (ER, DD, HE) :",
                tousLesDispositif(con), (elem) -> elem.getType());
    }

    // Getter pour l'ID
    public int getId() {
        return idDispositif;
    }

    private String getType() {
        return type;
    }

}
