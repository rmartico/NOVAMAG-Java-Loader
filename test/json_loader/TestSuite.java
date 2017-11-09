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

