package fr.insa.toto.moveINSA.model;

import fr.insa.beuvron.utils.list.ListUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Semestre {
    
    private int idSemestre;
    private String nomSemestre; // Par exemple, "Semestre 1", "Semestre 2", etc.

    // Constructeur
    public Semestre(int idSemestre, String nomSemestre) {
        this.idSemestre = idSemestre;
        this.nomSemestre = nomSemestre;
    }

    @Override
    public String toString() {
        return "Semestre{" +
                "idSemestre=" + idSemestre +
                ", nomSemestre='" + nomSemestre + '\'' +
                '}';
    }
    
     public static List<Semestre> tousLesSemestre(Connection con) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                "select idSemestre, nomSemestre from dispositif")) {
            ResultSet rs = pst.executeQuery();
            List<Semestre> res = new ArrayList<>();
            while (rs.next()) {
                res.add(new Semestre(rs.getInt(1), rs.getString(2)));
            }
            return res;
        }
    }

    /**
     * Sauvegarde le semestre dans la base de données et retourne l'ID généré par le SGBD.
     *
     * @param con Connexion à la base de données
     * @return L'ID du semestre sauvegardé
     * @throws SQLException en cas d'erreur avec la base de données
     */
    public int saveInDB(Connection con) throws SQLException {
        // Insertion dans la table semestre
        try (PreparedStatement insert = con.prepareStatement(
                "insert into semestre (nomSemestre) values (?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            insert.setString(1, this.nomSemestre);  // Insère le nom du semestre
            insert.executeUpdate();
            // Récupération de la clé générée
            try (ResultSet rid = insert.getGeneratedKeys()) {
                if (rid.next()) {
                    this.idSemestre = rid.getInt(1);  // Met à jour l'ID avec la valeur générée
                    return this.idSemestre;
                } else {
                    throw new SQLException("Échec de la création du semestre, aucune clé générée.");
                }
            }
        }
    }
    
    public static Semestre selectInConsole(Connection con) throws SQLException {
        return ListUtils.selectOne("Choisissez un semestre (format: Semestre 1...) :",
                tousLesSemestre(con), (elem) -> elem.getNomSemestre());
    }

    // Getter pour l'ID du semestre
    public int getIdSemestre() {
        return idSemestre;
    }

    // Getter pour le nom du semestre
    public String getNomSemestre() {
        return nomSemestre;
    }

}
