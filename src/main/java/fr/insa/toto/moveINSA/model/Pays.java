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
import fr.insa.toto.moveINSA.model.EntiteDejaSauvegardee;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Pays {
    
    private int idPays;  
    private String nomPays;

    
public Pays (String nomPays) {
    this(-1, nomPays);
}

    /**
     * création d'un Pays retrouvé dans la base de donnée.
     
    * @param idPays L'identifiant du pays
    * @param nomPays Le nom du pays
    */
    
    public Pays(int idPays, String nomPays) {
        this.idPays = idPays;
        this.nomPays = nomPays;
    }

    @Override
    public String toString() {
        return "Pays{" 
        + "idPays=" + this.getIdPays()
        + ", nomPays='" + nomPays
        + '}';
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
        if (this.getIdPays() != -1) {
            throw new EntiteDejaSauvegardee();
        }
        try (PreparedStatement insert = con.prepareStatement(
                "insert into partenaire (nomPays) values (?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            // Assigner les valeurs des attributs nomPays
            insert.setString(1, this.getNomPays());
            insert.executeUpdate();
            // récupérer la clé générée (ID)
            try (ResultSet rid = insert.getGeneratedKeys()) {
                rid.next();
                this.idPays = rid.getInt(1); // récupération de l'ID généré
                return this.getIdPays();
            }
        }
    }

    public static List<Pays> tousLesNomsPays(Connection con) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                "select idPays,nomPays from pays")) {
            ResultSet rs = pst.executeQuery();
            List<Pays> res = new ArrayList<>();
            while (rs.next()) {
                res.add(new Pays(rs.getInt(1), rs.getString(2)));
            }
            return res;
        }
    }
   
    public static int creeConsole(Connection con) throws SQLException {
    // Demander à l'utilisateur de saisir le nom du pays
    String nomPays = ConsoleFdB.entreeString("Nom du Pays : ");
    // Créer un nouvel objet Pays avec les informations saisies
    Pays nouveau = new Pays(nomPays);
    // Sauvegarder le nouveau pays dans la base de données
    return nouveau.saveInDB(con);
}

    public static Pays selectInConsole(Connection con) throws SQLException {
        return ListUtils.selectOne("choisissez un pays :",
                tousLesNomsPays(con), (elem) -> elem.getNomPays());
    }
   
    /**
     * @return the nomPays
     */
    public String getNomPays() {
        return nomPays;
    }
    /**
     * @param nomPays the nomPays to set
     */
    public void setNomPays(String nomPays) {
        this.nomPays = nomPays;
    }

    /**
     * @return the id
     */
    public int getIdPays() {
        return idPays;
    }
     
}
