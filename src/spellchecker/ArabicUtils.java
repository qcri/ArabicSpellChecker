/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spellchecker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author kareemdarwish
 */
public class ArabicUtils implements java.io.Serializable
{

    // ALL Arabic letters \U0621-\U063A\U0641-\U064A

    public static final String AllArabicLetters = "\u0621\u0622\u0623\u0624\u0625\u0626\u0627\u0628\u0629\u062A\u062B\u062C\u062D\u062E\u062F"
	    + "\u0630\u0631\u0632\u0633\u0634\u0635\u0636\u0637\u0638\u0639\u063A\u0641\u0642\u0643\u0644\u0645\u0646\u0647\u0648\u0649\u064A";
    public static final String AllArabicDiacritics = "\u064B\u064C\u064D\u064E\u064F\u0650\u0651\u0652";
    // ALL Hindi digits \U0660-\U0669
    public static final String AllHindiDigits = "\u0660\u0661\u0662\u0663\u0664\u0665\u0666\u0667\u0668\u0669";
    // ALL Arabic letters and Hindi digits \U0621-\U063A\U0641-\U064A\U0660-\U0669
    public static final String AllArabicLettersAndHindiDigits = "\u0621\u0622\u0623\u0624\u0625\u0626\u0627\u0628\u0629\u062A\u062B\u062C\u062D\u062E\u062F"
	    + "\u0630\u0631\u0632\u0633\u0634\u0635\u0636\u0637\u0638\u0639\u063A\u0641\u0642\u0643\u0644\u0645\u0646\u0647\u0648\u0649\u064A\u0660\u0661\u0662\u0663\u0664\u0665\u0666\u0667\u0668\u0669";
    public static final String AllDigits = "0123456789";
    public static final String ALLDelimiters = "\u0020\u0000-\u002F\u003A-\u0040\u007B-\u00BB\u005B-\u005D\u005F-\u0060\\^\u0600-\u060C\u06D4-\u06ED\ufeff";

    public static ArrayList<String> ArabicStopWords = new ArrayList<String>();

    public static final char ALEF = '\u0627';
    public static final char ALEF_MADDA = '\u0622';
    public static final char ALEF_HAMZA_ABOVE = '\u0623';
    public static final char ALEF_HAMZA_BELOW = '\u0625';

    public static final char HAMZA = '\u0621';
    public static final char HAMZA_ON_NABRA = '\u0624';
    public static final char HAMZA_ON_WAW = '\u0626';

    public static final char YEH = '\u064A';
    public static final char DOTLESS_YEH = '\u0649';

    public static final char TEH_MARBUTA = '\u0629';
    public static final char HEH = '\u0647';

    public static final Pattern emailRegex = Pattern.compile("[a-zA-Z0-9\\-\\._]+@[a-zA-Z0-9\\-_]+\\.[a-zA-Z0-9\\-_]+");
    public static final Pattern dateRegex = Pattern.compile("[0-9]+[\\-/][0-9]+([\\-/][0-9]+)*");
    public static final Pattern timeRegex = Pattern.compile("[0-9][0-9][:،][0-9][0-9]([:،][0-9][0-9](.[0-9]+)*)*");
    public static final Pattern pAllDiacritics = Pattern.compile("[\u0640\u064b\u064c\u064d\u064e\u064f\u0650\u0651\u0652\u0670]");
    public static final Pattern pAllNonCharacters = Pattern.compile("[\u0020\u2000-\u200F\u2028-\u202F\u205F-\u206F\uFEFF]+");
    public static final Pattern pAllDelimiters = Pattern.compile("[" + ALLDelimiters + "]+");

    private final static String FARSI_ORIGINAL_CHARACHTERS = "٠١٢۲٣۳٤٥Ƽ٦٧۷٨۸٩۹ﺀٴٱﺂﭑﺎﺈﺄιٲﺍٳίﺃٵﺇﺁﺑپﮨﺒٻﺐﺏﭘﭒﭗﭖﭚٮﭛٺﺗﭠﺘټﺖﺕﭡٹﭞٿﭟﭤﮢﭥﭨﭢﭣﮣﭧﺛﺜﮆﺚﺙٽﮇچﺟﭴﺠﭼڄڇﭸﺝڃﺞﭽﮀﭵﭹﭻﭾﭿﭺﺣﺤﺡﺢځﺧﺨڅڂﺦﺥڿډﺩڍﺪڊڈﮃﮂڋﮈڌﮉڐﮄﺫﺬڎڏۮڕړﺮﺭڒڔږڑژﮌڗﮍڙﺯﺰﮊﺳڛﺴﺲﺱښﺷڜﺸﺶﺵۺﺻﺼڝﺺﺹﺿﻀﺽڞﺾۻﻃﻁﻄﻂﻈﻇﻅڟﻆﻋ۶ﻌﻊﻉﻏﻐڠۼﻍﻎﻓڤﻔﭬڣﭰﻒﻑڦڢڡﭫڥﭪﭭﭯﭮﻗﻘڨﻖﻕڧﭱگڳکڪڱﮔﻛﮘڰﮐﮖﻜﮜڲﻚڴﮗڭﻙﮓﮙګڮﮕﮛڬﮎﮝﮚﮑﮒﮏﯖﯕﻟڵڷﻠڶﻞﻝڸﻣﻤﻢﻡﻧﻥڼﻨﻦڻڽﮠڹﮞںטּﮡﮟھہۃﮬﮪﮧۂﻫﮫﺔﻪﻬﮭﺓۿﻩەۀﮤﮥﮦۆۈۅﯙۉﻭﻮۄۋۇۊﯚٷٶﯛﯠﺆﯜۏﺅﯡﯝﯘﯢﯞﯣﯗﯟﯾےﻳۓېێﮱﻴﮯﭔﻲۑۍﯿﻱﻰﭜڀﺋﻯﭕﮮﺌﭓﯼﭝ༦ﺊﯽﮰﭙﯥﺉﯦﯧﯤیٸ";
    private final static String ARABIC_NORMALIZED_CHARACHTERS = "0122334556778899ءءاااااااااااااااببببببببببببببتتتتتتتتتتتتتتتتتتتتثثثثثثثجججججججججججججججججججحححححخخخخخخخددددددددددددددذذذذذرررررررررررررزززسسسسسسششششششصصصصصضضضضضضططططظظظظظعععععغغغغغغفففففففففففففففففقققققققككككككككككككككككككككككككككككككككككللللللللممممننننننننننننننهههههههههههههههههههههوووووووووووووووووووووووووووويييييييييييييييييييييييييييييييييييييي";
    private final static String FARSI_LA_ORIGINAL = "[ﻻﻵﻷעﻹﻼﻶﻸﬠ]";
    private final static String ARABIC_LA_NORMALIZED = "لا";

    public static final String prefixes[] =
    {
	// "ال", "و", "ف", "ب", "ك", "ل", "لل"
	"\u0627\u0644", "\u0648", "\u0641", "\u0628", "\u0643", "\u0644", "\u0644\u0644", "س"
    };

    public static final String suffixes[] =
    {
        // "ه", "ها", "ك", "ي", "هما", "كما", "نا", "كم", "هم", "هن", "كن",
	// "ا", "ان", "ين", "ون", "وا", "ات", "ت", "ن", "ة"
	"\u0647", "\u0647\u0627", "\u0643", "\u064a", "\u0647\u0645\u0627", "\u0643\u0645\u0627", "\u0646\u0627", "\u0643\u0645", "\u0647\u0645", "\u0647\u0646", "\u0643\u0646",
	"\u0627", "\u0627\u0646", "\u064a\u0646", "\u0648\u0646", "\u0648\u0627", "\u0627\u062a", "\u062a", "\u0646", "\u0629",
	"تم", "ما"
    };

    
    public ArabicUtils()
    {
	String[] stop =
	{
	    "و", "ما", "هي", "هو", "هم", "هما", "هن", "هذا", "هذه", "هذان", "هؤلاء", "هل", "في", "هنا", "هناك", "مع", "من", "علي", "كيف", "كان"
	};
	ArabicStopWords = new ArrayList<String>();
	for (String s : stop)
	{
	    ArabicStopWords.add(s);
	}
    }

    public static String buck2morph(String input)
    {
	String buck = "$Y\'|&}*<>&}";
	String morph = "PyAAAAOAAAA";
	input = replaceChars(input, buck, morph);
        // input = input.replace('$', 'P').replace('Y', 'y').replace('\'', 'A').replace('|', 'A').replace('&', 'A').replace('}', 'A').replace('*', 'O');
	// input = input.replace("<", "A").replace(">", "A").replace("&", "A").replace("'", "A").replace("}", "A");
	return input;
    }

    public static String utf82buck(String input)
    {
	String ar = "\u0627\u0625\u0622\u0623\u0621\u0628\u062a\u062b\u062c\u062d\u062e\u062f\u0630\u0631\u0632\u0633\u0634\u0635\u0636\u0637\u0638\u0639\u063a\u0641\u0642\u0643\u0644\u0645\u0646\u0647\u0648\u064a\u0649\u0629\u0624\u0626\u064e\u064b\u064f\u064c\u0650\u064d\u0652\u0651";
	String buck = "A<|>'btvjHxd*rzs$SDTZEgfqklmnhwyYp&}aFuNiKo~";
	input = replaceChars(input, ar, buck);
	return input;
    }

    public static String utf82buckWithoutDiacritics(String input)
    {
	input = input.replace("\u0627", "A").replace("\u0625", "<").replace("\u0622", "|").replace("\u0623", ">").replace("\u0621", "'");
	input = input.replace("\u0628", "b").replace("\u062a", "t").replace("\u062b", "v").replace("\u062c", "j").replace("\u062d", "H");
	input = input.replace("\u062e", "x").replace("\u062f", "d").replace("\u0630", "*").replace("\u0631", "r").replace("\u0632", "z");
	input = input.replace("\u0633", "s").replace("\u0634", "$").replace("\u0635", "S").replace("\u0636", "D").replace("\u0637", "T");
	input = input.replace("\u0638", "Z").replace("\u0639", "E").replace("\u063a", "g").replace("\u0641", "f").replace("\u0642", "q");
	input = input.replace("\u0643", "k").replace("\u0644", "l").replace("\u0645", "m").replace("\u0646", "n").replace("\u0647", "h");
	input = input.replace("\u0648", "w").replace("\u064a", "y").replace("\u0649", "Y").replace("\u0629", "p").replace("\u0624", "&");
	input = input.replace("\u0626", "}");
	return input;
    }

    public static String buck2utf8(String input)
    {
	String ar = "\u0627\u0625\u0622\u0623\u0621\u0628\u062a\u062b\u062c\u062d\u062e\u062f\u0630\u0631\u0632\u0633\u0634\u0635\u0636\u0637\u0638\u0639\u063a\u0641\u0642\u0643\u0644\u0645\u0646\u0647\u0648\u064a\u0649\u0629\u0624\u0626\u064e\u064b\u064f\u064c\u0650\u064d\u0652\u0651";
	String buck = "A<|>'btvjHxd*rzs$SDTZEgfqklmnhwyYp&}aFuNiKo~";
	input = replaceChars(input, buck, ar);
//        input = input.replace("A", "\u0627").replace("<", "\u0625").replace("|", "\u0622").replace(">", "\u0623").replace("'", "\u0621");
//        input = input.replace("b", "\u0628").replace("t", "\u062a").replace("v", "\u062b").replace("j", "\u062c").replace("H", "\u062d");
//        input = input.replace("x", "\u062e").replace("d", "\u062f").replace("*", "\u0630").replace("r", "\u0631").replace("z", "\u0632");
//        input = input.replace("s", "\u0633").replace("$", "\u0634").replace("S", "\u0635").replace("D", "\u0636").replace("T", "\u0637");
//        input = input.replace("Z", "\u0638").replace("E", "\u0639").replace("g", "\u063a").replace("f", "\u0641").replace("q", "\u0642");
//        input = input.replace("k", "\u0643").replace("l", "\u0644").replace("m", "\u0645").replace("n", "\u0646").replace("h", "\u0647");
//        input = input.replace("w", "\u0648").replace("y", "\u064a").replace("Y", "\u0649").replace("p", "\u0629").replace("&", "\u0624");
//        input = input.replace("}", "\u0626").replace("{", "إ");
//        input = input.replace("a", "\u064e").replace("F", "\u064b").replace("u", "\u064f").replace("N", "\u064c");
//        input = input.replace("i", "\u0650").replace("K", "\u064d").replace("o", "\u0652").replace("~", "\u0651");
	return input;
    }

    public static String buck2utf8WithoutDiacritics(String input)
    {
	input = input.replace("A", "\u0627").replace("<", "\u0625").replace("|", "\u0622").replace(">", "\u0623").replace("'", "\u0621");
	input = input.replace("b", "\u0628").replace("t", "\u062a").replace("v", "\u062b").replace("j", "\u062c").replace("H", "\u062d");
	input = input.replace("x", "\u062e").replace("d", "\u062f").replace("*", "\u0630").replace("r", "\u0631").replace("z", "\u0632");
	input = input.replace("s", "\u0633").replace("$", "\u0634").replace("S", "\u0635").replace("D", "\u0636").replace("T", "\u0637");
	input = input.replace("Z", "\u0638").replace("E", "\u0639").replace("g", "\u063a").replace("f", "\u0641").replace("q", "\u0642");
	input = input.replace("k", "\u0643").replace("l", "\u0644").replace("m", "\u0645").replace("n", "\u0646").replace("h", "\u0647");
	input = input.replace("w", "\u0648").replace("y", "\u064a").replace("Y", "\u0649").replace("p", "\u0629").replace("&", "\u0624");
	input = input.replace("}", "\u0626");
	return input;
    }

    public static ArrayList<String> tokenizeText(String input)
    {

	char[] charInput = input.toCharArray();
	input = "";
	for (int i = 0; i < charInput.length; i++)
	{
	    int c = charInput[i];
	    if (c <= 32 || c == 127 || (c >= 194128 && c <= 194160))
	    {
		input += " ";
	    }
	    else
	    {
		input += charInput[i];
	    }
	}

	input = input.replaceAll("[\u200B\ufeff]+", " ").replace("    ", "");
	ArrayList<String> output = new ArrayList<String>();
	String[] words = input.split("[\\\u061f \t\n\r,\\-<>\"\\?\\:;\\&]+");
	for (int i = 0; i < words.length; i++)
	{
	    if (words[i].startsWith("#")
		    || words[i].startsWith("@")
		    // || words[i].startsWith(":")
		    || words[i].startsWith(";")
		    || words[i].startsWith("http://")
		    || words[i].matches("[a-zA-Z0-9\\-\\._]+@[a-zA-Z0-9\\-\\._]+")
                    || words[i].matches("[0-9][0-9]:[0-9][0-9](:[0-9][0-9]){0,1}"))
	    {
		if (words[i].endsWith(":") || words[i].endsWith("\'"))
		{
		    words[i] = words[i].substring(0, words[i].length() - 1);
		}
		output.add(normalize(words[i].trim()));
	    }
	    else
	    {
		// String[] tmp = words[i].split("[~<>_\"\\-,\\.،\\!\\#\\$\\%\\?\\^\\&\\*\\(\\)\\[\\]\\{\\}\\/\\|\\\\]+");
		String[] tmp = words[i].split("[" + ArabicUtils.ALLDelimiters + "]+");
		for (int j = 0; j < tmp.length; j++)
		{
		    while (tmp[j].startsWith("\'"))
		    {
			tmp[j] = tmp[j].substring(1);
		    }
		    while (tmp[j].endsWith("\'") || tmp[j].endsWith("\"") || tmp[j].endsWith(":"))
		    {
			tmp[j] = tmp[j].substring(0, tmp[j].length() - 1);
		    }
		    if (!tmp[j].isEmpty() && tmp[j].length() > 0) //
		    {
			output.add(normalize(tmp[j].trim()));
		    }
		}
	    }
	}
	return output;
    }

    public static ArrayList<String> tokenizeWithoutProcessing(String s)
    {
	s = removeNonCharacters(s);
	// s = removeDiacritics(s);
	s = s.replaceAll("[\t\n\r]", " ");

	ArrayList<String> output = new ArrayList<String>();

	String[] words = s.split(" ");
	for (int i = 0; i < words.length; i++)
	{
	    if (words[i].startsWith("#")
		    || words[i].startsWith("@")
		    || words[i].startsWith(":")
		    || words[i].startsWith(";")
		    || words[i].startsWith("http://")
		    || emailRegex.matcher(words[i]).matches())
	    {
		output.add(words[i]);
	    }
	    else
	    {
		for (String ss : charBasedTonkenizer(words[i]).split(" "))
		{
		    if (ss.trim().length() > 0)
		    {
//			if (ss.startsWith("لل"))
//			{
//			    output.add("لال" + ss.substring(2));
//			}
//			else if (ss.trim().length() > 0)
			{
			    output.add(ss);
			}
		    }
		}
	    }
	}
	return output;
    }

    public static String tokenizePatterns(Pattern p, String str) {
        
        Matcher m = p.matcher(str);
        StringBuffer sb = new StringBuffer();
        int lastIndex = 0;
        while(m.find()) {
            m.appendReplacement(sb, " "+m.group(0)+" ");
            lastIndex = m.end();
        }
        sb.append(str.substring(lastIndex));
        return sb.toString();
    }

    public static ArrayList<String> tokenize(String s) {

    	s = removeNonCharacters(s);
    	s = tokenizePatterns(emailRegex,s);
    	s = tokenizePatterns(dateRegex,s);
    	s = s.replaceAll("http", " http");
        s = s.replaceAll("[\t\n\r ]+", " ");
        String[] ss;
        int j;

        ArrayList<String> output = new ArrayList<String>();

        String[] words = s.split(" ");
        for (int i = 0; i < words.length; i++) {
            if ( words[i].startsWith("#")
                    || words[i].startsWith("@")
                    || words[i].startsWith(":")
                    || words[i].startsWith(";")
                    || words[i].startsWith("http://")
                    || words[i].startsWith("https://")
                    || emailRegex.matcher(words[i]).matches()
                    || words[i].equals("ـ") 
                    || dateRegex.matcher(words[i]).matches()
                    || timeRegex.matcher(words[i]).matches()
                    ) {
            		// Remove Kashida.
	                output.add(words[i].replace("ـ","_"));
            } else {
                ss  = charBasedTonkenizer(removeDiacritics(words[i])).split(" ");
                for (j= 0; j<ss.length; j++) {
                    if (ss[j].trim().length() > 0) {
//                        if (ss.startsWith("لل"))
//                            output.add("لال" + ss.substring(2));
//                        else 
                        if (ss[j].trim().length() > 0)
                            output.add(ss[j]);
                    }
                }
            }
        }
        words = null;
        return output;
    }

    private static String charBasedTonkenizer(String s)
    {
	String sFinal = "";

	for (int i = 0; i < s.length(); i++)
	{
	    if (pAllDelimiters.matcher(s.substring(i, i + 1)).matches() && !(s.substring(i, i + 1).equals(".") || s.substring(i, i + 1).equals(",") || s.substring(i, i + 1).equals(".") || s.substring(i, i + 1).equals("%")))
	    {
		sFinal += " " + s.substring(i, i + 1) + " ";
	    }
	    else if (s.substring(i, i + 1).equals("%"))
	    {
		if (i > 0 && AllDigits.contains(s.substring(i - 1, i)))
		{
		    sFinal += s.substring(i, i + 1) + " ";
		}
		else
		{
		    sFinal += " " + s.substring(i, i + 1) + " ";
		}
	    }
	    else if (s.substring(i, i + 1).equals(".") || s.substring(i, i + 1).equals(",") || s.substring(i, i + 1).equals("."))
	    {
		if (i > 0 && i < s.length() - 1 && AllDigits.contains(s.substring(i - 1, i)) && AllDigits.contains(s.substring(i + 1, i + 2)))
		{
		    sFinal += s.substring(i, i + 1);
		}
		else if (i > 0 && s.substring(i, i + 1).equals(".") && s.substring(i - 1, i).equals("."))
		{
		    sFinal += s.substring(i, i + 1);
		}
		else if (i == 0)
		{
		    sFinal += s.substring(i, i + 1) + " ";
		}
		else if (i == s.length() - 1)
		{
		    sFinal += " " + s.substring(i, i + 1);
		    // } else if (s.substring(i - 1, i).matches("[0-9]") && s.substring(i + 1, i + 2).matches("[0-9]")) {
		}
		else if (AllDigits.contains(s.substring(i - 1, i)) && AllDigits.contains(s.substring(i + 1, i + 2)))
		{
		    sFinal += s.substring(i, i + 1);
		}
		else
		{
		    sFinal += " " + s.substring(i, i + 1) + " ";
		}
		// } else if (!s.substring(i, i + 1).matches("[" + ArabicUtils.AllArabicLettersAndHindiDigits + "\u0640\u064b\u064c\u064d\u064e\u064f\u0650\u0651\u0652\u0670" + "a-zA-Z0-9]")) {
	    }
	    else if (!(ArabicUtils.AllArabicLettersAndHindiDigits + "\u0640\u064b\u064c\u064d\u064e\u064f\u0650\u0651\u0652\u0670"
		    + "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789ÀÁÂÃÄÅÆÇÈÉËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿ").contains(s.substring(i, i + 1)))
	    {
		sFinal += " " + s.substring(i, i + 1) + " ";
		// sFinal += s.substring(i, i + 1);
	    }
	    else
	    {
		if (i == 0)
		{
		    sFinal += s.substring(i, i + 1);
		}
		else
		{
                    // if ((s.substring(i, i + 1).matches("[0-9]") && s.substring(i - 1, i).matches("[" + ArabicUtils.AllArabicLetters + "]"))
		    //        || (s.substring(i - 1, i).matches("[0-9]") && s.substring(i, i + 1).matches("[" + ArabicUtils.AllArabicLetters + "]"))) {
		    if ((AllDigits.contains(s.substring(i, i + 1))
			    && (AllArabicLetters + AllArabicDiacritics).contains(s.substring(i - 1, i)))
			    || (AllDigits.contains(s.substring(i - 1, i))
			    && (AllArabicLetters + AllArabicDiacritics).contains(s.substring(i, i + 1))))
		    {
			sFinal += " " + s.substring(i, i + 1);
		    }
		    else
		    {
			sFinal += s.substring(i, i + 1);
		    }

		}
	    }
	}
	return sFinal.trim();
    }

    public static String normalizeatb(String s) {
        
         s = s.replace(ALEF_MADDA, ALEF).replace(ALEF_HAMZA_ABOVE, ALEF).replace(ALEF_HAMZA_BELOW, ALEF);
         s = s.replace(DOTLESS_YEH, YEH);
         //s = s.replace(HAMZA_ON_NABRA, HAMZA).replace(HAMZA_ON_WAW, HAMZA);
         //s = s.replace(TEH_MARBUTA, HEH);
         s = s.replace("ٱ", "ا");
         
         s = pAllDiacritics.matcher(s).replaceAll("");
        return s;
    }
    public static String normalize(String s)
    {
//	// IF Starts with lam-lam
//	if (s.startsWith("\u0644\u0644")) //
//	{
//	    // need to insert an ALEF into the word
//	    s = "\u0644\u0627\u0644" + s.substring(2);
//	}
//	// If starts with waw-lam-lam
//	if (s.startsWith("\u0648\u0644\u0644")) //
//	{
//	    // need to insert an ALEF into the word
//	    s = "\u0648\u0644\u0627\u0644" + s.substring(3);
//	}
		// If starts with fa-lam-lam
		/*
	 * // Until fix the CRFPP training model if
	 * (s.startsWith("\u0641\u0644\u0644")) // { // need to insert an ALEF
	 * into the word s = "\u0641\u0644\u0627\u0644" + s.substring(3); }
	 */
	/*
	 * skip normalization of hamza, ta marbouta and alef maqsoura s =
	 * s.replace(ALEF_MADDA, ALEF).replace(ALEF_HAMZA_ABOVE,
	 * ALEF).replace(ALEF_HAMZA_BELOW, ALEF); s = s.replace(DOTLESS_YEH,
	 * YEH); s = s.replace(HAMZA_ON_NABRA, HAMZA).replace(HAMZA_ON_WAW,
	 * HAMZA); s = s.replace(TEH_MARBUTA, HEH);
	 */
	s = pAllDiacritics.matcher(s).replaceAll("");
	// s = s.replaceAll("[\u0640\u064b\u064c\u064d\u064e\u064f\u0650~\u0651\u0652\u0670]+", ""); // .replace(KASRATAN, EMPTY).replace(DAMMATAN, EMPTY).replace(FATHATAN, EMPTY).replace(FATHA, EMPTY).replace(DAMMA, EMPTY).replace(KASRA, EMPTY).replace(SHADDA, EMPTY).replace(SUKUN, EMPTY);
	return s;
    }

    public static String normalizeFull(String s) {
//        // IF Starts with lam-lam
//        if (s.startsWith("\u0644\u0644")) //
//        {
//            // need to insert an ALEF into the word
//            s = "\u0644\u0627\u0644" + s.substring(2);
//        }
//        // If starts with waw-lam-lam
//        if (s.startsWith("\u0648\u0644\u0644")) //
//        {
//            // need to insert an ALEF into the word
//            s = "\u0648\u0644\u0627\u0644" + s.substring(3);
//        }
		// If starts with fa-lam-lam
		/* // Until fix the CRFPP training model
         if (s.startsWith("\u0641\u0644\u0644")) //
         {
         // need to insert an ALEF into the word
         s = "\u0641\u0644\u0627\u0644" + s.substring(3);
         }
         */
        
         s = s.replace(ALEF_MADDA, ALEF).replace(ALEF_HAMZA_ABOVE, ALEF).replace(ALEF_HAMZA_BELOW, ALEF);
         s = s.replace(DOTLESS_YEH, YEH);
         s = s.replace(HAMZA_ON_NABRA, HAMZA).replace(HAMZA_ON_WAW, HAMZA);
         s = s.replace(TEH_MARBUTA, HEH);
         s = s.replace("ٱ", "ا");
         
         s = pAllDiacritics.matcher(s).replaceAll("");
        // s = s.replaceAll("[\u0640\u064b\u064c\u064d\u064e\u064f\u0650~\u0651\u0652\u0670]+", ""); // .replace(KASRATAN, EMPTY).replace(DAMMATAN, EMPTY).replace(FATHATAN, EMPTY).replace(FATHA, EMPTY).replace(DAMMA, EMPTY).replace(KASRA, EMPTY).replace(SHADDA, EMPTY).replace(SUKUN, EMPTY);
        return s;
    }


    public static String removeDiacritics(String s)
    {
	s = pAllDiacritics.matcher(s).replaceAll("");
	// s = s.replaceAll("[\u0640\u064b\u064c\u064d\u064e\u064f\u0650\u0651\u0652\u0670]", "");
	return s;
    }

    private static String removeNonCharacters(String s)
    {
	s = pAllNonCharacters.matcher(s).replaceAll(" ");
	// s = s.replaceAll("[\u0020\u2000-\u200F\u2028-\u202F\u205F-\u206F\uFEFF]+", " ");
	return s;
    }

    public static BufferedReader openFileForReading(String filename) throws FileNotFoundException, IOException {
        if (filename.endsWith(".gz"))
        {
            BufferedReader sr = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(new File(filename)))));
            return sr;
        }
        else
        {
            BufferedReader sr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename))));
            return sr;
        }
    }

    public static BufferedWriter openFileForWriting(String filename) throws FileNotFoundException, IOException {
        if (filename.endsWith(".gz"))
        {
            BufferedWriter sw = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(new File(filename)))));
            return sw;
        }
        else
        {
            BufferedWriter sw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filename))));
            return sw;
        }
    }

    public static String standardizeDiacritics(String word)
    {
	String diacrtics = "[\u064e\u064b\u064f\u064c\u0650\u064d\u0652\u0651]";
	String sokun = "\u0652";
	String fatha = "\u064e";
	word = word.replaceFirst("^" + diacrtics + "+", "");
	// word = word.replace("َا", "ا").replace("ُو", "و").replace("ِي", "ي").replace(".", "").replace("آَ", "آ");
	word = word.replace("َا", "ا").replace("ُو", "و").replace("ِي", "ي").replace("آَ", "آ");
	int pos = word.indexOf("و");
	while (pos > 0 && pos < word.length() - 1)
	{
	    if (!word.substring(pos - 1, pos).matches(diacrtics) && word.substring(pos + 1, pos + 2).equals(sokun))
	    {
		word = word.substring(0, pos + 1) + word.substring(pos + 2);
	    }
	    pos = word.indexOf("و", pos + 1);
	}
	pos = word.indexOf("ي");
	while (pos > 0 && pos < word.length() - 1)
	{
	    if (!word.substring(pos - 1, pos).matches(diacrtics) && word.substring(pos + 1, pos + 2).equals(sokun))
	    {
		word = word.substring(0, pos + 1) + word.substring(pos + 2);
	    }
	    pos = word.indexOf("ي", pos + 1);
	}
	pos = word.indexOf("ا");
	while (pos > 0 && pos < word.length() - 1)
	{
	    if (!word.substring(pos - 1, pos).matches(diacrtics)
		    && (word.substring(pos + 1, pos + 2).equals(sokun) || word.substring(pos + 1, pos + 2).equals(fatha)))
	    {
		word = word.substring(0, pos + 1) + word.substring(pos + 2);
	    }
	    pos = word.indexOf("ا", pos + 1);
	}
	if (word.startsWith("الْ"))
	{
	    word = word.replaceFirst("الْ", "ال");
	}
	// word = word.replaceFirst(diacrtics + "+$", "");
	return word;
    }

    public static String transferDiacriticsFromWordToSegmentedVersion(String diacritizedWord, String stemmedWord)
    {
	boolean startsWithLamLam = false;
	boolean startsWithWaLamLam = false;
	boolean startsWithFaLamLam = false;
	if ((stemmedWord.startsWith("ل+ال") && ArabicUtils.removeDiacritics(diacritizedWord).startsWith("لل"))
		|| (stemmedWord.startsWith("و+ل+ال") && ArabicUtils.removeDiacritics(diacritizedWord).startsWith("ولل"))
		|| (stemmedWord.startsWith("ف+ل+ال") && ArabicUtils.removeDiacritics(diacritizedWord).startsWith("فلل")))
	{
	    int posFirstLam = diacritizedWord.indexOf("ل", 0);
	    int posSecondLam = diacritizedWord.indexOf("ل", posFirstLam + 1);
	    diacritizedWord = diacritizedWord.substring(0, posSecondLam) + "ا" + diacritizedWord.substring(posSecondLam);
	}

	String output = "";
	stemmedWord = stemmedWord.replace(" ", "");
	stemmedWord = stemmedWord.replaceFirst("\\+$", "");
	if (diacritizedWord.equals(stemmedWord) || !stemmedWord.contains("+"))
	{
	    return diacritizedWord;
	}

	int pos = 0;
	for (int i = 0; i < stemmedWord.length(); i++)
	{
	    if (stemmedWord.substring(i, i + 1).equals("+") || stemmedWord.substring(i, i + 1).equals(";"))
	    {
		{
		    output += stemmedWord.substring(i, i + 1);
		}
	    }
	    else
	    {
		int loc = diacritizedWord.indexOf(stemmedWord.substring(i, i + 1), pos);
		if (loc >= 0)
		{
		    String diacritics = diacritizedWord.substring(pos, loc);
		    output += diacritics + stemmedWord.substring(i, i + 1);
		    // add trailing diacritics
		    loc++;
		    while (loc < diacritizedWord.length() && diacritizedWord.substring(loc, loc + 1).matches("["
			    + ArabicUtils.buck2utf8("aiouNKF~") + "]"))
		    {
			output += diacritizedWord.substring(loc, loc + 1);
			loc++;
		    }
		    pos = loc;
		}
		else
		{
		    // System.err.println(diacritizedWord + "\t" + stemmedWord);
		}
	    }
	}
	return output;
    }

    public static String getDiacritizedStem(String stemmed, String diacritized)
    {
	String output = "";
	String[] parts = (" " + stemmed + " ").split(";");
	if (parts.length != 3)
	{
	    return diacritized;
	}
	String suffixes = parts[2].trim();
	String stem = parts[1].trim();

	int i = diacritized.length();
	while (i > 0 && !ArabicUtils.removeDiacritics(diacritized.substring(i, diacritized.length())).equals(suffixes))
	{
	    i--;
	}
	// head would be the word without the suffix
	String head = diacritized.substring(0, i);
	String tail = diacritized.substring(i);
	// next get the stem
	i = head.length();
	while (i > 0 && !ArabicUtils.removeDiacritics(head.substring(i, head.length())).equals(stem))
	{
	    i--;
	}
	String diacritizedStem = head.substring(i);

	return diacritizedStem;
    }

    /**
     * The following is copied directly from StringUtils.java which is part of
     * the "lang" part of the Apache Commons Utils. It comes with the following
     * license : Licensed to the Apache Software Foundation (ASF) under one or
     * more contributor license agreements. See the NOTICE file distributed with
     * this work for additional information regarding copyright ownership. The
     * ASF licenses this file to You under the Apache License, Version 2.0 (the
     * "License"); you may not use this file except in compliance with the
     * License. You may obtain a copy of the License at
     *
     * http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
     * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
     * License for the specific language governing permissions and limitations
     * under the License.
     */
    public static final String EMPTY = "";

    public static String replaceFarsiCharacters(String input)
    {
        input = input.replace("ﻻ","لا");
        input = input.replace("ﻵ","لآ");
        input = input.replace("ﻷ","لأ");
        input = input.replace("ע","لا");
        input = input.replace("ﻹ","لإ");
        input = input.replace("ﻼ","لا");
        input = input.replace("ﻶ","لآ");
        input = input.replace("ﻸ","لأ");
        input = input.replace("ﬠ","لا");
	// input = input.replaceAll(FARSI_LA_ORIGINAL, ARABIC_LA_NORMALIZED);
	input = replaceChars(input, FARSI_ORIGINAL_CHARACHTERS, ARABIC_NORMALIZED_CHARACHTERS);
	return input;
    }
    
    // Replace, character based
    //-----------------------------------------------------------------------
    /**
     * <p>
     * Replaces all occurrences of a character in a String with another. This is
     * a null-safe version of {@link String#replace(char, char)}.</p>
     *
     * <p>
     * A {@code null} string input returns {@code null}. An empty ("") string
     * input returns an empty string.</p>
     *
     * <pre>
     * StringUtils.replaceChars(null, *, *)        = null
     * StringUtils.replaceChars("", *, *)          = ""
     * StringUtils.replaceChars("abcba", 'b', 'y') = "aycya"
     * StringUtils.replaceChars("abcba", 'z', 'y') = "abcba"
     * </pre>
     *
     * @param str String to replace characters in, may be null
     * @param searchChar the character to search for, may be null
     * @param replaceChar the character to replace, may be null
     * @return modified String, {@code null} if null string input
     * @since 2.0
     */
    public static String replaceChars(final String str, final char searchChar, final char replaceChar)
    {
	if (str == null)
	{
	    return null;
	}
	return str.replace(searchChar, replaceChar);
    }

    /**
     * <p>
     * Replaces multiple characters in a String in one go. This method can also
     * be used to delete characters.</p>
     *
     * <p>
     * For example:<br>
     * <code>replaceChars(&quot;hello&quot;, &quot;ho&quot;, &quot;jy&quot;) = jelly</code>.</p>
     *
     * <p>
     * A {@code null} string input returns {@code null}. An empty ("") string
     * input returns an empty string. A null or empty set of search characters
     * returns the input string.</p>
     *
     * <p>
     * The length of the search characters should normally equal the length of
     * the replace characters. If the search characters is longer, then the
     * extra search characters are deleted. If the search characters is shorter,
     * then the extra replace characters are ignored.</p>
     *
     * <pre>
     * StringUtils.replaceChars(null, *, *)           = null
     * StringUtils.replaceChars("", *, *)             = ""
     * StringUtils.replaceChars("abc", null, *)       = "abc"
     * StringUtils.replaceChars("abc", "", *)         = "abc"
     * StringUtils.replaceChars("abc", "b", null)     = "ac"
     * StringUtils.replaceChars("abc", "b", "")       = "ac"
     * StringUtils.replaceChars("abcba", "bc", "yz")  = "ayzya"
     * StringUtils.replaceChars("abcba", "bc", "y")   = "ayya"
     * StringUtils.replaceChars("abcba", "bc", "yzx") = "ayzya"
     * </pre>
     *
     * @param str String to replace characters in, may be null
     * @param searchChars a set of characters to search for, may be null
     * @param replaceChars a set of characters to replace, may be null
     * @return modified String, {@code null} if null string input
     * @since 2.0
     */
    public static String replaceChars(final String str, final String searchChars, String replaceChars)
    {
	if (isEmpty(str) || isEmpty(searchChars))
	{
	    return str;
	}
	if (replaceChars == null)
	{
	    replaceChars = EMPTY;
	}
	boolean modified = false;
	final int replaceCharsLength = replaceChars.length();
	final int strLength = str.length();
	final StringBuilder buf = new StringBuilder(strLength);
	for (int i = 0; i < strLength; i++)
	{
	    final char ch = str.charAt(i);
	    final int index = searchChars.indexOf(ch);
	    if (index >= 0)
	    {
		modified = true;
		if (index < replaceCharsLength)
		{
		    buf.append(replaceChars.charAt(index));
		}
	    }
	    else
	    {
		buf.append(ch);
	    }
	}
	if (modified)
	{
	    return buf.toString();
	}
	return str;
    }

    // Empty checks
    //-----------------------------------------------------------------------
    /**
     * <p>
     * Checks if a CharSequence is empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     *
     * <p>
     * NOTE: This method changed in Lang version 2.0. It no longer trims the
     * CharSequence. That functionality is available in isBlank().</p>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is empty or null
     * @since 3.0 Changed signature from isEmpty(String) to
     * isEmpty(CharSequence)
     */
    public static boolean isEmpty(final CharSequence cs)
    {
	return cs == null || cs.length() == 0;
    }
}
