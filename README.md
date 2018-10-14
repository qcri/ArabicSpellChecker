Arabic Spell Checker
=============

ABOUT
--------------------------
QCRI Arabic Spell Checker.


Download
---------

Arabic Spell Checker. Latest release
You may check-out the latest version of the Arabic Spell Checker from the demo page http://farasa.qcri.org/


CONTENTS
--------------------------
Arabic Spell Checker

How to run the software
------------------------
1 - Command line:


	java -jar <PackageSubDir>/dist/SpellChecker.jar -i InputFile -o OutputFile
	or 
	java -jar <PackageSubDir>/dist/SpellChecker.jar < InputFile > OutputFile


How to compile the software
----------------------------
Build the jar:

    cd <PackageSubDir> 
	ant jar
	
Deploy the package to other direcotory:

	ant deploy -Do=<Dest Dir>

Using the SpellCheker API
-------------------------
You may call the SpellChecker in your code 

	import spellchecker;
	....
	SpellChecker speller = new SpellChecker();
	....
	formatInput = false; //
    formatOutput = false; // true for site display
	boolean considerContext = true; // Use the context or not
	nofCorrectionEntries = spell.init(considerContext);
	String line="ان الحياه دقائق وثوانى"; // utf-8 string
	String output = spell.spellCheck(input, formatInput, formatOutput, considerContext);


RELATED PAPERS
-------------------------


CONTACT
--------------------------
If you have any problem, question please contact hmubarak@hbku.edu.qa 

WEB SITE
---------------------------
URL for the project  and the latest news  and downloads
	http://farasa.qcri.org/

GITHUB
---------------------------
Where to download the latest version, 
	https://github.com/qcri/ASpellChecker

LICENSE
------------
    Arabic Spell Checker is being made 
    public for research purpose only. 
    For non-research use, please contact:
    
        Hamdy Mubarak <hmubarak@hbku.edu.qa>
	Kareem Darwish <kdarwish@hbku.edu.qa>
        Ahmed Abdelali <aabdelali@hbku.edu.qa>
    
    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  


COPYRIGHT
----------------------------
Copyright 2015-2018 QCRI
