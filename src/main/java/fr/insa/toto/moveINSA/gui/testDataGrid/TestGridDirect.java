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
package fr.insa.toto.moveINSA.gui.testDataGrid;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import fr.insa.toto.moveINSA.gui.MainLayout;
import java.util.List;

/**
 * Juste quelques test sur les Grid vaadin.
 * @author francois
 */
@Route(value = "debug/testGridDirect", layout = MainLayout.class)
public class TestGridDirect extends VerticalLayout{
    
    /** pour une grid direct avec des Integer et des String 
     * {@code ==> ok : 9 < 10 ; "9" > "10"}
     * 
     */
    private static class IntAndString{
        private Integer ival;
        private String sval;

        public IntAndString(Integer ival, String sval) {
            this.ival = ival;
            this.sval = sval;
        }
        
        public static List<IntAndString> testData() {
            return List.of(
                    new IntAndString(9, "9"),
                    new IntAndString(10, "10")
            );
        }

        @Override
        public String toString() {
            return "IES{" + ival + "," + sval + '}';
        }
        
        

        /**
         * @return the ival
         */
        public Integer getIval() {
            return ival;
        }

        /**
         * @return the sval
         */
        public String getSval() {
            return sval;
        }
    }

    /** meme chose, mais en déclarant tout comme Object
     * si je met que des Integer en première col et des String en seconde, cela fonctionne encore
     * si je mixte, il semble qu'il revienne à la comparaison de String
     * {@code ==> difficile de voir comment cela fonctionne}
     */
    private static class ObjAndObj{
        private Object ival;
        private Object sval;

        public ObjAndObj(Object ival, Object sval) {
            this.ival = ival;
            this.sval = sval;
        }
        
        public static List<ObjAndObj> testData() {
            return List.of(
                    new ObjAndObj(9, "9"),
                    new ObjAndObj(10, "10")
            );
        }

        public static List<ObjAndObj> testDataMix() {
            return List.of(
                    new ObjAndObj("9", "9"),
                    new ObjAndObj(10, "10"),
                    new ObjAndObj(new IntAndString(13, "coucou"), "10"),
                    new ObjAndObj(null, "10")
            );
        }

        /**
         * @return the ival
         */
        public Object getIval() {
            return ival;
        }

        /**
         * @return the sval
         */
        public Object getSval() {
            return sval;
        }
        
    }
    
    public TestGridDirect() {
        this.add(new H3("Quelques tests sur les Grid vaadin"));
        Grid<IntAndString> grid1 = new Grid<>(IntAndString.testData());
        grid1.addColumn(IntAndString::getIval)
                .setHeader("as Integer")
                .setSortable(true);
        grid1.addColumn(IntAndString::getSval)
                .setHeader("as String")
                .setSortable(true);
        this.add(grid1);
        Grid<ObjAndObj> grid2 = new Grid<>(ObjAndObj.testData());
        grid2.addColumn(ObjAndObj::getIval)
                .setHeader("as Integer")
                .setSortable(true);
        grid2.addColumn(ObjAndObj::getSval)
                .setHeader("as String")
                .setSortable(true);
        Grid<ObjAndObj> grid3 = new Grid<>(ObjAndObj.testDataMix());
        grid3.addColumn(ObjAndObj::getIval)
                .setHeader("as Integer")
                .setSortable(true);
        grid3.addColumn(ObjAndObj::getSval)
                .setHeader("as String")
                .setSortable(true);
        this.add(grid1,grid2,grid3);
    }
    
}
