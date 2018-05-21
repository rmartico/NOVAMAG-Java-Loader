/*
 *      Copyright (C) 2017-2018 UBU-ICCRAM-ADMIRABLE-NOVAMAG-GA686056
 *
 *  This Program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2, or (at your option)
 *  any later version.
 *
 *  This Program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with UBU-ICCRAM-ADMIRABLE-NOVAMAG-GA686056  see the file COPYING.  If not, see
 *  <http://www.gnu.org/licenses/>.
 *
 */

package json_loader;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import json_loader.formulaparser.TestArrayFormula;
import json_loader.formulaparser.TestFormulaParser;
import json_loader.formulaparser.TestFractFormula;
import json_loader.formulaparser.TestFraction;
import json_loader.formulaparser.TestFractionSelector;
import json_loader.dao.TestMolecule;
import json_loader.dao.TestAttachedFiles;
import json_loader.dao.TestAuthors;
import json_loader.dao.TestDBItem;
import json_loader.dao.TestFileTypes;
import json_loader.dao.TestItems;


@RunWith(Suite.class)
@Suite.SuiteClasses({
	//package json_loader
		TestJSONparser.class,
		TestLoader.class,
	//package formulaparser 
		TestFormulaParser.class,
		TestArrayFormula.class,
		TestFractFormula.class,
		TestFraction.class,
		TestFractionSelector.class,
	//package dao
		TestMolecule.class,
		TestItems.class,
		TestAuthors.class,
		TestFileTypes.class,
		TestAttachedFiles.class,
		TestDBItem.class
	})
public class TestSuite {
  //nothing
}

