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
package fr.insa.toto.moveINSA.gui.vues;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import fr.insa.beuvron.vaadin.utils.ConnectionPool;
import fr.insa.toto.moveINSA.gui.MainLayout;
import fr.insa.toto.moveINSA.gui.session.SessionInfo;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.SQLException;

/**
 *
 * @author francois
 */
@Route(value = "debug/testDriver", layout = MainLayout.class)
public class TestDriverPanel extends VerticalLayout {

    public TestDriverPanel() {
        this.add(new H3("test du driver"));
        try (Connection con = ConnectionPool.getConnection()){
            Class<Driver> mysqlDriver = (Class<Driver>) Class.forName("com.mysql.cj.jdbc.Driver");
            this.add(new Paragraph("com.mysql.cj.jdbc.Driver OK"));
            DatabaseMetaData meta = con.getMetaData();
            this.add(new Paragraph("jdbc driver de la connection : " + meta.getDriverName() + " ; " + meta.getDriverVersion()));
        } catch (ClassNotFoundException ex) {
            this.add(new Paragraph("com.mysql.cj.jdbc.Driver not found"));
        } catch (SQLException ex) {
            this.add(new H3("problème sql : "));
            StringWriter mess = new StringWriter();
            PrintWriter out = new PrintWriter(mess);
            ex.printStackTrace(out);
            this.add(new Paragraph(mess.toString()));
        }

    }

}
