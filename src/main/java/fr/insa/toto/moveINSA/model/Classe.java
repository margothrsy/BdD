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
 * Classe "miroir" de la table classe.
 */
public class Classe {

    private int idClasse;
    private String nomClasse;
    private int effectifClasse;
    private String specialite;
    private int annee;

    /**
     * Création d'une Classe retrouvée dans la base de données.
     */
    public Classe(int idClasse) {
        this.idClasse = idClasse;
    }
    
    public Classe(int idClasse, String nomClasse, int effectifClasse, String specialite, int annee) {
        this.idClasse = idClasse;
        this.nomClasse = nomClasse;
        this.effectifClasse = effectifClasse;
        this.specialite = specialite;
        this.annee = annee;
        
    }

    @Override
    public String toString() {
        return "Classe{" +
                "IdClasse=" + this.getIdClasse() +
                " ; NomClasse=" + this.nomClasse +
                " ; EffectifClasse=" + effectifClasse +   // Correction ici
                " ; Spécialité=" + this.specialite +
                " ; Année=" + this.annee +
                '}';
    }

    /**
     * Sauvegarde une nouvelle entité et retourne la clé affectée automatiquement
     * par le SGBD.
     * <p>
     * La clé est également sauvegardée dans l'attribut idClasse.
     * </p>
     *
     * @param con
     * @return la clé de la nouvelle entité dans la table de la BdD
     * @throws EntiteDejaSauvegardee si l'id de l'entité est différent de -1
     * @throws SQLException si un autre problème survient avec la BdD
     */
    public int saveInDB(Connection con) throws SQLException, EntiteDejaSauvegardee {
        if (this.getIdClasse() != -1) {
            throw new fr.insa.toto.moveINSA.model.EntiteDejaSauvegardee();
        }

        try (PreparedStatement insert = con.prepareStatement(
                "INSERT INTO classe (nomClasse, effectifClasse, specialite, annee) VALUES (?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            insert.setString(1, this.nomClasse);
            insert.setInt(2, this.effectifClasse);  // Utilisation de effectifClasse
            insert.setString(3, this.specialite);
            insert.setInt(4, this.annee);
            
            insert.executeUpdate();
            
            try (ResultSet rid = insert.getGeneratedKeys()) {
                if (rid.next()) {
                    this.idClasse = rid.getInt(1);
                    return this.getIdClasse();
                } else {
                    throw new SQLException("Aucune clé générée pour l'insertion.");
                }
            }
        }
    }

    /**
     * Retourne toutes les classes enregistrées dans la base de données.
     * @param con
     * @return 
     * @throws java.sql.SQLException
     */
    public static List<Classe> toutesLesClasses(Connection con) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                "SELECT idClasse, nomClasse, effectifClasse, specialite, annee FROM classe")) {
            ResultSet rs = pst.executeQuery();
            List<Classe> res = new ArrayList<>();
            while (rs.next()) {
                res.add(new Classe(
                        rs.getInt("idclasse"), 
                        rs.getString("nomClasse"), 
                        rs.getInt("effectifClasse"), 
                        rs.getString("specialite"), 
                        rs.getInt("annee")
                ));
            }
            return res;
        }
    }

    /**
     * Création d'une nouvelle classe via la console et sauvegarde dans la base de données.
     */
    public static int creeConsole(Connection con) throws SQLException {
        int idClasse = ConsoleFdB.entreeInt("IdClasse : ");
        String nomClasse = ConsoleFdB.entreeString("Nom de la classe : ");
        int effectifClasse = ConsoleFdB.entreeInt("Effectif : ");
        String specialite = ConsoleFdB.entreeString("Spécialité : ");
        int annee = ConsoleFdB.entreeInt("Année : ");

        Classe nouveau = new Classe(idClasse);
        nouveau.nomClasse = nomClasse;
        nouveau.effectifClasse = effectifClasse;  // Utilisation de effectifClasse
        nouveau.specialite = specialite;
        nouveau.annee = annee;

        return nouveau.saveInDB(con);
    }

    /**
     * Sélectionne une classe via la console.
     */
    /**
    public static Classe selectInConsole(Connection con) throws SQLException {
        return ListUtils.selectOne("Choisissez une classe :",
                toutesLesClasses(con), Classe::getIdClasse);
    }
*/
    /**
     * @return l'identifiant de la classe
     */
    public int getIdClasse() {
        return idClasse;
    }

    public int getEffectifClasse() {  // Renommé pour effectifClasse
        return effectifClasse;
    }

    public void setEffectifClasse(int effectifClasse) {  // Méthode de setter pour effectifClasse
        this.effectifClasse = effectifClasse;
    }
    
    public static Classe recupererParNomClasse(Connection con, String nomClasse) throws SQLException {
    String requete = "SELECT idClasse, nomClasse, effectifClasse, specialite, annee FROM Classe WHERE nomClasse = ?";
    try (PreparedStatement pst = con.prepareStatement(requete)) {
        pst.setString(1, nomClasse);
        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                Classe classe = new Classe(rs.getInt("idClasse"));
                classe.nomClasse = rs.getString("nomClasse");
                classe.effectifClasse = rs.getInt("effectifClasse");
                classe.specialite = rs.getString("specialite");
                classe.annee = rs.getInt("annee");
                return classe;
            } else {
                return null; // Aucune classe trouvée pour ce nom
            }
        }
    }
    }
}

