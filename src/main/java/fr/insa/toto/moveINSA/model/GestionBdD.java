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
import fr.insa.beuvron.utils.exceptions.ExceptionsUtils;
import fr.insa.beuvron.utils.list.ListUtils;
import fr.insa.beuvron.utils.database.ResultSetUtils;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.h2.jdbc.meta.DatabaseMetaServer;

/**
 * Opération générales sur la base de donnée de gestion des tournois.
 * <p>
 * Les opérations plus spécifiques aux diverses tables sont réparties dans les
 * classes correspondantes.
 * </p>
 *
 * @author francois
 */
public class GestionBdD {

    /**
     * création complète du schéma de la BdD.
     *
     * @param con
     * @throws SQLException
     */
    public static void creeSchema(Connection con)
            throws SQLException {
        con.setAutoCommit(false);
        try (Statement st = con.createStatement()) {
            // creation des tables
            st.executeUpdate(
                    "create table partenaire ( \n"
                    + ConnectionSimpleSGBD.sqlForGeneratedKeys(con, "id") + ",\n"
                    + " refPartenaire varchar(50) not null unique,\n"
                    + " ville varchar(50), \n"
                    + " pays varchar(50) \n"
                    + ")");
            st.executeUpdate(
                    "create table offremobilite ( \n"
                    + ConnectionSimpleSGBD.sqlForGeneratedKeys(con, "id") + ",\n"
                    + " nbrplaces int not null,\n"
                    + " proposePar int not null,\n"
                    + " semestre int not null,\n"
                    + " niveauScolaire int not null,\n"
                    + " dispositif varchar(2) not null,\n"
                    + " nomOffre varchar(100) not null,\n"
                    + " specialiteAssocie varchar(50) not null\n"
                    + ")");
            st.executeUpdate(
                    "CREATE TABLE etudiant ( \n"
                    + " INE INT PRIMARY KEY, \n"
                    + " nomEtudiant VARCHAR(50) NOT NULL, \n"
                    + " prenom VARCHAR(50) NOT NULL, \n"
                    + " classe VARCHAR(50) NOT NULL, \n"
                    + " annee INT NOT NULL, \n"
                    + " classement INT NOT NULL, \n"
                    + " mdp VARCHAR(50) NOT NULL \n"
                    + ")");
            /*st.executeUpdate(
                    "create table classe ( \n"
                    + ConnectionSimpleSGBD.sqlForGeneratedKeys(con, "idClasse") + ",\n"
                    + " nom varchar(50) not null,\n"
                    + " effectif int not null\n"
                    + ")");

            st.executeUpdate(
                    "create table specialite ( \n"
                    + ConnectionSimpleSGBD.sqlForGeneratedKeys(con, "idSpecialite") + ",\n"
                    + " nomSpecialite varchar(50) not null,\n"
                    + " effectifSpecialite int not null\n"
                    + ")");
             st.executeUpdate(
                    "create table departement ( \n"
                    + ConnectionSimpleSGBD.sqlForGeneratedKeys(con, "idDepartement") + ",\n"
                    + " idDepartement int not null unique,\n"
                    + " nomDepartement varchar(50) not null\n"
                
                    + ")");*/
            // création des liens
            //hcvzaidvzcv   bkjbzkcbkabkj
            st.executeUpdate(
                    """
                    alter table offremobilite
                        add constraint fk_offremobilite_proposepar
                        foreign key (proposepar) references partenaire(id)
                        on delete restrict on update restrict
                    """);
            con.commit();
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        } finally {
            con.setAutoCommit(true);
        }
    }

    /**
     * suppression complete de toute la BdD.
     *
     * @param con
     * @throws SQLException
     */
    public static void deleteSchema(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            
            try {
                st.executeUpdate(
                        "alter table offremobilite drop constraint fk_offremobilite_proposepar");
            } catch (SQLException ex) {
                
            }
          
            try {
                st.executeUpdate("drop table offremobilite");
            } catch (SQLException ex) {
                
            }
            try {
                st.executeUpdate("drop table partenaire");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table etudiant");
            } catch (SQLException ex) {
            }
             /*try {
                st.executeUpdate("drop table classe");
            } catch (SQLException ex) {
            }
              try {
                st.executeUpdate("drop table specialite");
            } catch (SQLException ex) {
            }
              try {
                st.executeUpdate("drop table departement");
            } catch (SQLException ex) {
            }*/
        }
    }

    /**
     * crée un jeu de test dans la BdD.
     *
     * @param con
     * @throws SQLException
     **/
    public static void initBdDTest(Connection con) throws SQLException {
    // Initialisation des partenaires
    List<Partenaire> partenaires = List.of(
        new Partenaire("MIT", "Cambridge", "USA"),
        new Partenaire("Oxford", "Oxford", "UK")
    );
    for (var partenaire : partenaires) {
        partenaire.saveInDB(con);
    }

    // Initialisation des offres de mobilité
    List<OffreMobilite> offres = List.of(
        new OffreMobilite(1, partenaires.get(0).getIdPartenaire(), 5, 3, "HE", "bmlanhgjhve", "GT2E"),
        new OffreMobilite(2, partenaires.get(0).getIdPartenaire(), 8, 4, "HE", "hgkiuzgeiuzgu", "MIQ"),
        new OffreMobilite(5, partenaires.get(1).getIdPartenaire(), 9, 5, "ER", "fhoiefoihzi", "GM")
    );
    for (var offre : offres) {
        offre.saveInDB(con);
    }

    // Initialisation des étudiants
    List<Etudiant> etudiants = List.of(
        new Etudiant("362356701RE", "Emilie", "Matieu", "GT2E", 3, 5, "zeiurgozfgiz"),
        new Etudiant("362356701TT", "Emma", "Martin", "GT2E", 3, 7, "dvhiyavyvyif")
    );
    for (var etudiant : etudiants) {
        etudiant.saveInDB(con);
    }
}

    public static void razBDD(Connection con) throws SQLException {
        deleteSchema(con);
        creeSchema(con);
        initBdDTest(con);
    }

    public static void menuPartenaire(Connection con) {
        int rep = -1;
        while (rep != 0) {
            int i = 1;
            System.out.println("Menu partenaires");
            System.out.println("==================");
            System.out.println((i++) + ") liste de tous les partenaires");
            System.out.println((i++) + ") créer un nouveau partenaire");
            System.out.println("0) Retour");
            rep = ConsoleFdB.entreeEntier("Votre choix : ");
            try {
                int j = 1;
                if (rep == j++) {
                    List<Partenaire> users = Partenaire.tousLesPartenaires(con);
                    System.out.println(users.size() + " utilisateurs : ");
                    System.out.println(ListUtils.enumerateList(users, (elem) -> elem.toString()));
                } else if (rep == j++) {
                    Partenaire.creeConsole(con);
                }
            } catch (Exception ex) {
                System.out.println(ExceptionsUtils.messageEtPremiersAppelsDansPackage(ex, "fr.insa", 3));
            }
        }
    }

    public static void menuOffre(Connection con) {
        int rep = -1;
        while (rep != 0) {
            int i = 1;
            System.out.println("Menu offres mobilité");
            System.out.println("==================");
            System.out.println((i++) + ") liste de toutes les offres");
            System.out.println((i++) + ") créer une nouvelle offre");
            System.out.println("0) Retour");
            rep = ConsoleFdB.entreeEntier("Votre choix : ");
            try {
                int j = 1;
                if (rep == j++) {
                    List<OffreMobilite> offres = OffreMobilite.toutesLesOffres(con);
                    System.out.println(offres.size() + " offres : ");
                    System.out.println(ListUtils.enumerateList(offres, (elem) -> elem.toString()));
                } else if (rep == j++) {
                    OffreMobilite.creeConsole(con);
                }
            } catch (Exception ex) {
                System.out.println(ExceptionsUtils.messageEtPremiersAppelsDansPackage(ex, "fr.insa", 3));
            }
        }
    }
     public static void menuEtudiant(Connection con) {
        int rep = -1;
        while (rep != 0) {
            int i = 1;
            System.out.println("Menu etudiants");
            System.out.println("==================");
            System.out.println((i++) + ") liste de tous les etudiants");
            System.out.println((i++) + ") créer un nouvel etudiant");
            System.out.println("0) Retour");
            rep = ConsoleFdB.entreeEntier("Votre choix : ");
            try {
                int j = 1;
                if (rep == j++) {
                    List<Etudiant> offres = Etudiant.tousLesEtudiants(con);
                    System.out.println(offres.size() + " offres : ");
                    System.out.println(ListUtils.enumerateList(offres, (elem) -> elem.toString()));
                } else if (rep == j++) {
                   Etudiant.creeConsole(con);
                }
            } catch (Exception ex) {
                System.out.println(ExceptionsUtils.messageEtPremiersAppelsDansPackage(ex, "fr.insa", 3));
            }
        }
    }
    
     public static void menuClasse(Connection con) {
        int rep = -1;
        while (rep != 0) {
            int i = 1;
            System.out.println("Menu classes");
            System.out.println("==================");
            System.out.println((i++) + ") liste de toutes les classes");
            System.out.println((i++) + ") créer une nouvelle classe");
            System.out.println("0) Retour");
            rep = ConsoleFdB.entreeEntier("Votre choix : ");
            try {
                int j = 1;
                if (rep == j++) {
                    List<Classe> users = Classe.toutesLesClasses(con);
                    System.out.println(users.size() + " utilisateurs : ");
                    System.out.println(ListUtils.enumerateList(users, (elem) -> elem.toString()));
                } else if (rep == j++) {
                    Classe.creeConsole(con);
                }
            } catch (Exception ex) {
                System.out.println(ExceptionsUtils.messageEtPremiersAppelsDansPackage(ex, "fr.insa", 3));
            }
        }
    }
    
    public static void menuSpecialite(Connection con) {
        int rep = -1;
        while (rep != 0) {
            int i = 1;
            System.out.println("Menu specialite");
            System.out.println("==================");
            System.out.println((i++) + ") liste de toutes les specialite");
            System.out.println((i++) + ") créer une nouvelle specialite");
            System.out.println("0) Retour");
            rep = ConsoleFdB.entreeEntier("Votre choix : ");
            try {
                int j = 1;
                if (rep == j++) {
                    List<Specialite> users = Specialite.toutesLesSpecialites(con);
                    System.out.println(users.size() + " utilisateurs : ");
                    System.out.println(ListUtils.enumerateList(users, (elem) -> elem.toString()));
                } else if (rep == j++) {
                    Classe.creeConsole(con);
                }
            } catch (Exception ex) {
                System.out.println(ExceptionsUtils.messageEtPremiersAppelsDansPackage(ex, "fr.insa", 3));
            }
        }
    }
    public static void menuDepartement(Connection con) {
        int rep = -1;
        while (rep != 0) {
            int i = 1;
            System.out.println("Menu departement");
            System.out.println("==================");
            System.out.println((i++) + ") liste de tous les departements");
            System.out.println((i++) + ") créer un nouveau departement");
            System.out.println("0) Retour");
            rep = ConsoleFdB.entreeEntier("Votre choix : ");
            try {
                int j = 1;
                if (rep == j++) {
                    List<Departement> users = Departement.tousLesDepartements(con);
                    System.out.println(users.size() + " utilisateurs : ");
                    System.out.println(ListUtils.enumerateList(users, (elem) -> elem.toString()));
                } else if (rep == j++) {
                    Departement.creeConsole(con);
                }
            } catch (Exception ex) {
                System.out.println(ExceptionsUtils.messageEtPremiersAppelsDansPackage(ex, "fr.insa", 3));
            }
        }
    }
    public static void menuBdD(Connection con) {
        int rep = -1;
        while (rep != 0) {
            int i = 1;
            System.out.println("Menu gestion base de données");
            System.out.println("============================");
            System.out.println((i++) + ") RAZ BdD = delete + create + initialize");
            System.out.println((i++) + ") donner un ordre SQL update quelconque");
            System.out.println((i++) + ") donner un ordre SQL query quelconque");
            System.out.println("0) Retour");
            rep = ConsoleFdB.entreeEntier("Votre choix : ");
            try {
                int j = 1;
                if (rep == j++) {
                    razBDD(con);
                } else if (rep == j++) {
                    String ordre = ConsoleFdB.entreeString("ordre SQL : ");
                    try (PreparedStatement pst = con.prepareStatement(ordre)) {
                        pst.executeUpdate();
                    }
                } else if (rep == j++) {
                    String ordre = ConsoleFdB.entreeString("requete SQL : ");
                    try (PreparedStatement pst = con.prepareStatement(ordre)) {
                        try (ResultSet rst = pst.executeQuery()) {
                            System.out.println(ResultSetUtils.formatResultSetAsTxt(rst));
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println(ExceptionsUtils.messageEtPremiersAppelsDansPackage(ex, "fr.insa.beuvron", 3));
            }
        }
    }

    public static void menuPrincipal() {
        int rep = -1;
        Connection con = null;
        try {
            con = ConnectionSimpleSGBD.defaultCon();
            System.out.println("Connection OK");
        } catch (SQLException ex) {
            System.out.println("Problème de connection : " + ex.getLocalizedMessage());
            throw new Error(ex);
        }
        while (rep != 0) {
            int i = 1;
            System.out.println("Menu principal");
            System.out.println("==================");
            System.out.println((i++) + ") test driver mysql");
            System.out.println((i++) + ") menu gestion BdD");
            System.out.println((i++) + ") menu partenaires");
            System.out.println((i++) + ") menu offres");
            System.out.println((i++) + ") menu etudiant");
            System.out.println((i++) + ") menu classe");
            System.out.println((i++) + ") menu specialite");
            System.out.println((i++) + ") menu departement");
            System.out.println("0) Fin");
            rep = ConsoleFdB.entreeEntier("Votre choix : ");
            try {
                int j = 1;
                if (rep == j++) {
                    try {
                        Class<Driver> mysqlDriver = (Class<Driver>) Class.forName("com.mysql.cj.jdbc.Driver");
                    } catch (ClassNotFoundException ex) {
                        System.out.println("com.mysql.cj.jdbc.Driver not found");
                    }
                    DatabaseMetaData meta = con.getMetaData();
                    System.out.println("jdbc driver version : " + meta.getDriverName() + " ; " + meta.getDriverVersion());
                } else if (rep == j++) {
                    menuBdD(con);
                } else if (rep == j++) {
                    menuPartenaire(con);
                } else if (rep == j++) {
                    menuOffre(con);
                }
            } catch (Exception ex) {
                System.out.println(ExceptionsUtils.messageEtPremiersAppelsDansPackage(ex, "fr.insa", 3));
            }
        }
    }

    public static boolean EntiteDejaSauvegardee(Connection con, String tableName, String columnName, Object value) 
        throws SQLException {
    String query = "SELECT 1 FROM " + tableName + " WHERE " + columnName + " = ?";
    try (PreparedStatement pst = con.prepareStatement(query)) {
        pst.setObject(1, value);
        try (ResultSet rst = pst.executeQuery()) {
            // Si un enregistrement est trouvé, l'entité existe déjà
            return rst.next();
        }
    }
}
    public static void main(String[] args) {
        menuPrincipal();
    }
}
