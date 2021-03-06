/**
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and EvoSuite
 * contributors
 *
 * This file is part of EvoSuite.
 *
 * EvoSuite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * EvoSuite is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with EvoSuite. If not, see <http://www.gnu.org/licenses/>.
 */
package com.examples.with.different.packagename.jee.injection;

import javax.inject.Inject;

/**
 * Created by Andrea Arcuri on 20/08/15.
 */
public class InjectionInInputParameter {

    public static class Bar{
        public int get(){return 42;}
    }


    public static class Foo{
        @Inject
        private Bar bar;

        public  int getInt(){
            return bar.get();
        }
    }


    @Inject
    private Foo foo;

    public void exe(){
        if(foo.getInt() == 42){
            System.out.println("Got it");
        }
    }
}
