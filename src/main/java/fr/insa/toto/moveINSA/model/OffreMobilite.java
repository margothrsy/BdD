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
import fr.insa.toto.moveINSA.model.Partenaire;
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
     * création d'une nouvelle Offre en mémoire, non existant dans la Base de
     * donnée.
     * @param nbrPlaces
     * @param proposePar
     * @param semestre
     * @param niveauScolaire
     * @param dispositif
     * @param nomOffre
     * @param specialiteAssocie
     */
    public OffreMobilite(int nbrPlaces, String proposePar, int semestre,int niveauScolaire, String dispositif, String nomOffre, String specialiteAssocie) {
        this(-1, nbrPlaces, proposePar, semestre, niveauScolaire, dispositif, nomOffre, specialiteAssocie);
}

    /**
     * création d'une Offre retrouvée dans la base de donnée.
     * @param id
     * @param nbrPlaces
     * @param proposePar
     * @param semestre
     * @param niveauScolaire
     * @param dispositif
     * @param nomOffre
     * @param specialiteAssocie
     */
    public OffreMobilite(int id, int nbrPlaces, String proposePar, int semestre,int niveauScolaire, String dispositif, String nomOffre, String specialiteAssocie) {
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
     * Sauvegarde une nouvelle entité et retourne la clé affecté automatiquement
     * par le SGBD.
     * <p>
     * la clé est également sauvegardée dans l'attribut id
     * </p>
     *
     * @param con
     * @return la clé de la nouvelle entité dans la table de la BdD
     * @throws EntiteDejaSauvegardee si l'id de l'entité est différent de -1
     * @throws SQLException si autre problème avec la BdD
     */
    public int saveInDB(Connection con) throws SQLException {
        if (this.getId() != -1) {
            throw new fr.insa.toto.moveINSA.model.EntiteDejaSauvegardee();
        }
        try (PreparedStatement insert = con.prepareStatement(
                "insert into offremobilite (nbrplaces,proposepar,semestre, niveauScolaire, dispositif, nomOffre, specialiteAssocie) values (?,?,?,?,?,?,?)",
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
                rid.next();
                this.idOffre = rid.getInt(1);
                return this.getId();
            }
        }
    }

    public static List<OffreMobilite> toutesLesOffres(Connection con) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                "select id,nbrplaces,proposepar,semestre, niveauScolaire, refpartenaire, nomOffre, specialiteAssocie from offremobilite")) {
            ResultSet rs = pst.executeQuery();
            List<OffreMobilite> res = new ArrayList<>();
            while (rs.next()) {
                res.add(new OffreMobilite(rs.getInt(1), rs.getString(2), rs.getInt(3),  rs.getInt(4), rs.getString(5), rs.getString(6), rs.getString(7)));
            }
            return res;
        }
    }

    public static int creeConsole(Connection con) throws SQLException {
        Partenaire p = Partenaire.selectInConsole(con);
        int nbr = ConsoleFdB.entreeInt("nombre de places : ");
        String par = ConsoleFdB.entreeString("proposé par: ");
        int s = ConsoleFdB.entreeInt("semestre proposé : ");
        int niv = ConsoleFdB.entreeInt("niveau scolaire : ");
        String dispositif = ConsoleFdB.entreeString("type de dispositif : ");
        String nom = ConsoleFdB.entreeString("nom de l'offre : ");
        String spe = ConsoleFdB.entreeString("pour quel specialité : ");
        OffreMobilite nouveau = new OffreMobilite(p.getId(), nbr, par , s, niv, dispositif, nom, spe);
        return nouveau.saveInDB(con);
    }

    /**
     * @return the id
     */
    public int getId() {
        return idOffre;
    }

}
