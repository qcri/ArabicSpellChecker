/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spellchecker;

import java.io.*;
import java.util.*;

import static spellchecker.ArabicUtils.*;

/**
 *
 * @author hmubarak
 */

public class SpellChecker {   
    private final int FREQ_PERCENT_THRESHOLD = 2; // 5 %
    private final int MAX_WINDOW_SIZE = 3; // w0 w1 w2

    private HashMap<String, String> candidatesUnigram = new HashMap<String, String>();
    private HashMap<String, String> corrections = new HashMap<String, String>();
    private HashMap<String, Integer> unigrams = new HashMap<String, Integer>();
    private HashMap<String, Integer> bigrams = new HashMap<String, Integer>();
    private HashMap<String, String> punctFreq = new HashMap<String, String>();
    private HashMap<String, String> mgbAljazeeraBW = new HashMap<String, String>();
    //private HashMap<String, Double> kenLM = new HashMap<String, Double>();
    private HashMap<String, Integer> ngramFreq = new HashMap<String, Integer>();
    
    boolean applyReplaceWordsOnly = true;
    boolean tryAllEdits = false;
    
    int totalUnigramFreq = 0;
    int totalBigramFreq = 0;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        int i = 0;
        String arg;
        String infile = "";
        String outfile = "";
        BufferedReader br; 
        BufferedWriter bw; 
        
        boolean formatInput, formatOutput, considerContext;
        int nofCorrectionEntries, nofLines, nofWords;
        long startTime, endTime, elapsedTime;
        String input = null, output, msg;
        //List<String> fileLines = new ArrayList<String>();
        
        while (i < args.length)
        {
            arg = args[i++];
            // 
            if (arg.equals("--help") || arg.equals("-h") || (args.length != 0 && args.length != 2 && args.length != 4 && args.length != 6 && args.length != 8))
            {
                System.out.println("Usage: SpellChecker <--help|-h> <[-i|--input] [in-filename]> <[-o|--output] [out-filename]>");
                System.exit(-1);
            }

            if (arg.equals("--input") || arg.equals("-i"))
            {
                infile = args[i];   
            }
            if (arg.equals("--output") || arg.equals("-o"))
            {
                outfile = args[i];
            }
        }
        
        if (!infile.equals(""))
        {
            br = openFileForReading(infile);
        }
        else
        {
            br = new BufferedReader(new InputStreamReader(System.in));
        }

        if (!outfile.equals(""))
        {
            bw = openFileForWriting(outfile);
        }
        else
        {
            bw = new BufferedWriter(new OutputStreamWriter(System.out));
        }
            
        System.err.print("Initializing the system ....");
        String[] words;
        SpellChecker spell = new SpellChecker();
        
        
        formatInput = false;
        formatOutput = false;//true for site display
        considerContext = true;

        //nofCorrectionEntries = spell.init("D:/SpellCheck/", considerContext);
        nofCorrectionEntries = spell.init(considerContext);
        
        //input = "ان الحياه دقائق وثوانى";
        //input = "وبحضاراته الشسيعة فقط,";// "يتحدثهاأكثر  بالحوارفعليك  وضعتهاايران";//  "الارهاربيةتنتظر"; //"مليوننسمة ثلاتة";
        //input = "النصر آت لا محال انشاء الله من يؤمن بالله واليوم الآخر . والمطلع على قصص الانبياء يرى ان النصر من الله وعلامات ألقيامه آتيه في تسلسل عجيب . إن تنصر الله ينصركم لا رجوع انما التغيير آت ليس من الرءساء أنما من فوق سابع سماء";
                
        //output = spell.spellCheck(input, formatInput, formatOutput, considerContext);
        
        //msg = String.format("%s\r\n%s", input, output);
        //System.out.println(msg);
        

        //nofLines = spell.LoadFile(infile, fileLines);
        //bw = new BufferedWriter(new FileWriter(outfile));
        
        System.err.print("\r");
        System.err.println("System ready!               ");
            
        nofWords = 0;
        nofLines = 0;
        startTime = System.currentTimeMillis();
        
        while ((input = br.readLine()) != null)
        {
            nofLines++;
            output = spell.spellCheck(input, formatInput, formatOutput, considerContext);
            words = output.split("\\s+");
            nofWords += words.length;
            bw.write(output);
        }

            
       // for (i = 0; i < nofLines; i++)
       // {
       //     msg = String.format("Correcting line:%d/%d", i, nofLines);
       //     System.out.println(msg);

       //     input = fileLines.get(i);
       //     output = spell.spellCheck(input, formatInput, formatOutput, considerContext);
           
       //     words = output.split("\\s+");
       //     nofWords += words.length;

       //     msg = String.format("%s\r\n%s\r\n\r\n", input, output);
       //     System.out.println(msg);
           
       //     bw.write(output);
       // }
        endTime = System.currentTimeMillis();
        
        elapsedTime = endTime - startTime;
        
        bw.close();
        msg = String.format("Lines:%d Words:%d Time(ms):%d", nofLines, nofWords, elapsedTime);
        System.err.println(msg);                            
    }
    

    public int LoadFile(String filename, List<String> list)
    {
        int nofLines, dbgLineNo, breakpoint, errors, i, maxNofLines;
        String strLine, msg;

        nofLines = 0;
        dbgLineNo = 1226;
        errors = 0;
        maxNofLines = -1;

        try
        {
            list.clear();
            java.io.File file = new java.io.File(filename);
            if (file.exists())
            {
                FileInputStream fstream = new FileInputStream(filename);
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

                //Read File Line By Line
                while ((strLine = br.readLine()) != null)
                {
                    if (nofLines == dbgLineNo)
                    {
                        breakpoint = 1;
                    }

                    if (nofLines == maxNofLines)
                    {
                        break;
                    }

                    if ((nofLines > 0) && (nofLines % 10000) == 0)
                    {
                        //msg = String.format("LoadFile(). Reading: %s, Line:%d", filename, nofLines);
                        //System.out.println (msg);
                    }

                    list.add(strLine);

                    nofLines++;
                }
                //Close the input stream
                br.close();
            }
            else
            {
                nofLines = -1;
            }
        }
    catch (Exception e)
        {
            e.printStackTrace();
    }
               
        return nofLines;
    }
    
    public HashMap deserializeMap(String MapName) throws FileNotFoundException, IOException, ClassNotFoundException
    {
        //System.err.println("Deserialize :"+"SpellCheckData/" + MapName + ".ser");
        ObjectInputStream ois = new ObjectInputStream(resolveName("SpellCheckData/" + MapName + ".ser"));
        HashMap map = (HashMap) ois.readObject();
        ois.close();
        return map;
    }
    
    //public int init(String folderName, boolean considerContext)
    public int init(boolean considerContext)
    {
        boolean readFromSerFiles = true;
        String dir, msg;

        //msg = String.format("init() start.");
        //System.out.println(msg);

        dir = "";
        if (!readFromSerFiles)
        {
            //dir = "D:/SpellCheck/";
            dir = "../SpellCheckData/";
            if (!dir.endsWith("/"))
            {
                dir += "/"; //For Windows, use \\
            }
        }

        candidatesUnigram.clear();
        corrections.clear();
        unigrams.clear();
        bigrams.clear();
        punctFreq.clear();
        mgbAljazeeraBW.clear();
        //kenLM.clear();
        ngramFreq.clear();

        totalUnigramFreq = 0;
        totalBigramFreq = 0;

        try
        {
            if (!readFromSerFiles)
            {
                //candidatesUnigram = loadCandidates(dir + "aj.lm.words.uni.trim2");
                candidatesUnigram = loadCandidates(dir + "raw-aljazeera-text.txt.spellDict");
                //candidatesUnigram = loadCandidates(dir + "mgb.arabic.unnormalized.spellDict"); //aj.lm.words.uni.trim


                corrections = loadCorrections(dir + "corrections.txt");
                unigrams = loadNGrams(dir + "raw-aljazeera-text.nonorm.lm", 1);
                //bigrams = loadNGrams(dir + "raw-aljazeera-text.nonorm.bi", 2);
                bigrams = loadNGrams(dir + "raw-aljazeera-text.nonorm.freq3.bi", 2);

                mgbAljazeeraBW =  loadAljazeeraBW(dir + "mgb-Aljazeera.bw");

                punctFreq = loadPunctFreq(dir + "punctFreq.txt");

                //kenLM = loadKenLM(dir + "articles.txt.tokenized.arpa");

                // FIXME: Hamdy, save totalUnigramFreq, and totalBigramFreq
                ngramFreq.put("totalUnigramFreq", totalUnigramFreq);
                ngramFreq.put("totalBigramFreq", totalBigramFreq);

                File file = new File(dir + "ngramFreq.txt.ser");
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
                oos.writeObject(ngramFreq);
                oos.close();            
            }
            else
            {
                //candidatesUnigram = deserializeMap("aj.lm.words.uni.trim2");
                corrections = deserializeMap("corrections.txt");
                candidatesUnigram = deserializeMap("raw-aljazeera-text.txt.spellDict");                
                unigrams = deserializeMap("raw-aljazeera-text.nonorm.lm");
                //bigrams = deserializeMap("raw-aljazeera-text.nonorm.bi");
                bigrams = deserializeMap("raw-aljazeera-text.nonorm.freq3.bi");
                mgbAljazeeraBW = deserializeMap("mgb-Aljazeera.bw");
                punctFreq = deserializeMap("punctFreq.txt");
                ngramFreq = deserializeMap("ngramFreq.txt");
                totalUnigramFreq = ngramFreq.get("totalUnigramFreq");
                totalBigramFreq = ngramFreq.get("totalBigramFreq");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        msg = String.format("init() end. candidatesUnigram.size()=%d", candidatesUnigram.size());
        System.out.println(msg);

        return candidatesUnigram.size();
    }
    
    
    public int clean()
    {
        try
        {
            candidatesUnigram.clear();
            corrections.clear();
            unigrams.clear();
            bigrams.clear();
            punctFreq.clear();
            mgbAljazeeraBW.clear();
            //kenLM.clear();
            ngramFreq.clear();
        }
    catch (Exception e)
        {
            e.printStackTrace();
    }
        
        return 0;
    }


    private InputStream resolveName(String name) {
        if (name == null) {
            return null;
        }
        if (!name.startsWith("/")) {
            String baseName = this.getClass().getName();
            int index = baseName.lastIndexOf('.');
            if (index != -1) {
                name = baseName.substring(0, index).replace('.', '/') + "/" + name;
            }
        } else {
            name = name.substring(1);
        }
    // return name;
    ClassLoader cl = this.getClass().getClassLoader();
    return cl.getResourceAsStream(name);
    }
    
    
    private HashMap<String, String> loadCandidates(String filePath) throws FileNotFoundException, IOException, ClassNotFoundException
    {
        int i, nofLines;
        String msg;
        List<String> fileLines = new ArrayList<String>();
        HashMap<String, String> candidates = new HashMap<String, String>();
        // char[] tab = {'\t'};

        msg = String.format("loadCandidates() start. filePath=%s", filePath);
        System.out.println(msg);

        String line = "";
        // if (wikipediaArEn.isEmpty())
        //File file = new File(filePath + ".ser");
        //if (file.exists())
        if(resolveName(filePath + ".ser") != null )
        {
            // System.out.println(df.format(cal.getTime()));
            ObjectInputStream ios = new ObjectInputStream(resolveName(filePath + ".ser"));
            candidates = (HashMap) ios.readObject();
            // System.out.println(df.format(cal.getTime()));
        }
        else
        {
            //nofLines = 0;
            //BufferedReader sr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath))));
            //while ((line = sr.readLine()) != null)
            //{
            
            nofLines = LoadFile(filePath, fileLines);
            for (i = 0; i < nofLines; i++)
            {
                if ((i % 10000) == 0)
                {
                    msg = String.format("loadCandidates(). lineNo=%d/%d", i, nofLines);
                    System.out.println(msg);
                }
                
                line = fileLines.get(i);

                if (line.length() > 0)
                {
                    String[] lineParts = line.split("\t");
                    if (line.length() > 0 && lineParts.length > 1) // && Regex.IsMatch("^[0-9\\.\\-]$"))
                    {
                        candidates.put(lineParts[0], lineParts[1]);
                        /*
                         String norm = normalizeFull(line);
                         if (!candidates.containsKey(norm)) {
                         candidates.put(norm, line);
                         }
                         else
                         {
                         String temp = candidates.get(norm) + " " + line;
                         candidates.put(norm, temp);
                         }
                         */
                    }
                }
            }
            // FIXME: Hamdy, 2014-06-26. No write permission
            File file = new File(filePath + ".ser");
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(candidates);
            oos.close();
        }

        fileLines.clear();

        msg = String.format("loadCandidates() end. candidates.size()=%d", candidates.size());
        System.out.println(msg);
        
        return candidates;
    }

    
    private HashMap<String, String> loadCorrections(String filePath) throws FileNotFoundException, IOException, ClassNotFoundException
    {
        int i, nofLines;
        String msg, line = "";
        List<String> fileLines = new ArrayList<String>();
        HashMap<String, String> corrections = new HashMap<String, String>();
        // char[] tab = {'\t'};

        msg = String.format("loadCorrections() start. filePath=%s", filePath);
        System.out.println(msg);

        // if (wikipediaArEn.isEmpty())
        //File file = new File(filePath + ".ser");
        //if (file.exists())
        if(resolveName(filePath + ".ser") != null)
        {
            // System.out.println(df.format(cal.getTime()));
            ObjectInputStream ios = new ObjectInputStream(resolveName(filePath + ".ser"));
            corrections = (HashMap) ios.readObject();
            // System.out.println(df.format(cal.getTime()));
        }
        else
        {
            //nofLines = 0;
            //BufferedReader sr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath))));
            //while ((line = sr.readLine()) != null)
            //{
            //    nofLines++;
            nofLines = LoadFile(filePath, fileLines);
            for (i = 0; i < nofLines; i++)
            {
                if ((i % 10000) == 0)
                {
                    msg = String.format("loadCorrections(). lineNo=%d/%d", i, nofLines);
                    System.out.println(msg);
                }
                
                line = fileLines.get(i);

                if (line.length() > 0)
                {
                    String[] lineParts = line.split("\t");
                    if (line.length() > 0 && lineParts.length > 0) // && Regex.IsMatch("^[0-9\\.\\-]$"))
                    {
                        corrections.put(lineParts[0], lineParts[1]);
                    }
                }
            }
            // FIXME: Hamdy, 2014-06-26. No write permission
            File file = new File(filePath + ".ser");
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(corrections);
            oos.close();
        }

        fileLines.clear();
        
        msg = String.format("loadCorrections() end. corrections.size()=%d", corrections.size());
        System.out.println(msg);
        
        return corrections;
    }
    
    
    private HashMap<String, String> loadPunctFreq(String filePath) throws FileNotFoundException, IOException, ClassNotFoundException
    {
        int i, nofLines;
        String msg, line = "";
        List<String> fileLines = new ArrayList<String>();
        HashMap<String, String> punctFreq = new HashMap<String, String>();
        // char[] tab = {'\t'};

        msg = String.format("loadPunctFreq() start. filePath=%s", filePath);
        System.out.println(msg);
        
        // if (wikipediaArEn.isEmpty())
        //File file = new File(filePath + ".ser");
        //if (file.exists())
        if(resolveName(filePath + ".ser") != null)
        {
            // System.out.println(df.format(cal.getTime()));
            ObjectInputStream ios = new ObjectInputStream(resolveName(filePath + ".ser"));
            punctFreq = (HashMap) ios.readObject();
            // System.out.println(df.format(cal.getTime()));
        }
        else
        {
            //nofLines = 0;
            //BufferedReader sr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath))));
            //while ((line = sr.readLine()) != null)
            nofLines = LoadFile(filePath, fileLines);
            for (i = 0; i < nofLines; i++)
            {
                if ((i % 10000) == 0)
                {
                    msg = String.format("loadPunctFreq(). nofLines=%d", nofLines);
                    System.out.println(msg);
                }

                line = fileLines.get(i);
                
                if (line.length() > 0)
                {
                    String[] lineParts = line.split("\t");
                    if (line.length() > 0 && lineParts.length > 0) // && Regex.IsMatch("^[0-9\\.\\-]$"))
                    {
                        punctFreq.put(lineParts[0], lineParts[1]);
                    }
                }
            }
            // FIXME: Hamdy, 2014-06-26. No write permission
            File file = new File(filePath + ".ser");
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(punctFreq);
            oos.close();
        }

        fileLines.clear();
        
        msg = String.format("loadPunctFreq() end. punctFreq.size()=%d", punctFreq.size());
        System.out.println(msg);
        
        return punctFreq;
    }
        
        
    private HashMap<String, Integer> loadNGrams(String filePath, int ngram) throws FileNotFoundException, IOException, ClassNotFoundException
    {
        int i, nofLines, freq;
        String msg, line = "";
        List<String> fileLines = new ArrayList<String>();
        HashMap<String, Integer> uniBiGrams = new HashMap<String, Integer>();
        // char[] tab = {'\t'};

        msg = String.format("loadNGrams() start. filePath=%s ngram=%d", filePath, ngram);
        System.out.println(msg);
        
        if (ngram == 1)
        {
            totalUnigramFreq = 0;
        }
        else if (ngram == 2)
        {
            totalBigramFreq = 0;            
        }

        // if (wikipediaArEn.isEmpty())
        //File file = new File(filePath + ".ser");
        //if (file.exists())
        if(resolveName(filePath + ".ser") != null)
        {
            // System.out.println(df.format(cal.getTime()));
            ObjectInputStream ios = new ObjectInputStream(resolveName(filePath + ".ser"));
            uniBiGrams = (HashMap) ios.readObject();
            // System.out.println(df.format(cal.getTime()));
        }
        else
        {
            //nofLines = 0;
            //BufferedReader sr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath))));
            //while ((line = sr.readLine()) != null)
            nofLines = LoadFile(filePath, fileLines);
            for (i = 0; i < nofLines; i++)
            {
                if ((i % 10000) == 0)
                {
                    msg = String.format("loadNGrams(). lineNo=%d/%d", i, nofLines);
                    System.out.println(msg);
                }
                
                line = fileLines.get(i);

                if (/*(nofLines >= 3) &&*/ (line.length() > 0))
                {
                    String[] lineParts = line.split("\t");
                    if (line.length() > 0 && lineParts.length > 1) // && Regex.IsMatch("^[0-9\\.\\-]$"))
                    {
                        freq = Integer.parseInt(lineParts[1]);
                        uniBiGrams.put(lineParts[0], freq);

                        if (ngram == 1)
                        {
                            totalUnigramFreq += freq;
                        }
                        else if (ngram == 2)
                        {
                            totalBigramFreq += freq;            
                        }
                        
                        /*
                         String norm = normalizeFull(line);
                         if (!candidates.containsKey(norm)) {
                         candidates.put(norm, line);
                         }
                         else
                         {
                         String temp = candidates.get(norm) + " " + line;
                         candidates.put(norm, temp);
                         }
                         */
                    }
                }
            }
            // FIXME: Hamdy, 2014-06-26. No write permission
            File file = new File(filePath + ".ser");
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(uniBiGrams);
            oos.close();
        }

        fileLines.clear();
        
        msg = String.format("loadNGrams() end. uniBiGrams.size()=%d, ngram=%d", uniBiGrams.size(), ngram);
        System.out.println(msg);
        
        return uniBiGrams;
    }


    private HashMap<String, String> loadAljazeeraBW(String filePath) throws FileNotFoundException, IOException, ClassNotFoundException
    {
        int i, nofLines;
        String msg, value;
        List<String> fileLines = new ArrayList<String>();
        HashMap<String, String> candidates = new HashMap<String, String>();
    
        msg = String.format("loadAljazeeraBW() start. filePath=%s", filePath);
        System.out.println(msg);

        String line = "";
        //File file = new File(filePath + ".ser");
        //if (file.exists())
        if(resolveName(filePath + ".ser") != null)
        {
            // System.out.println(df.format(cal.getTime()));
            ObjectInputStream ios = new ObjectInputStream(resolveName(filePath + ".ser"));
            candidates = (HashMap) ios.readObject();
            // System.out.println(df.format(cal.getTime()));
        }
        else
        {
            //nofLines = 0;
            //BufferedReader sr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath))));
            //while ((line = sr.readLine()) != null)
            nofLines = LoadFile(filePath, fileLines);
            for (i = 0; i < nofLines; i++)
            {
                if ((i % 10000) == 0)
                {
                    msg = String.format("loadAljazeeraBW(). lineNo=%d/%d", i, nofLines);
                    System.out.println(msg);
                }

                line = fileLines.get(i);
                
                if (line.length() > 0)
                {
                    String[] lineParts = line.split("\t");
                    //if (line.length() > 0) // && Regex.IsMatch("^[0-9\\.\\-]$"))
                    {
                        value = "";
                        if (lineParts.length > 1)
                        {
                            value = lineParts[1];
                        }
                        candidates.put(lineParts[0], value);
                        /*
                         String norm = normalizeFull(line);
                         if (!candidates.containsKey(norm)) {
                         candidates.put(norm, line);
                         }
                         else
                         {
                         String temp = candidates.get(norm) + " " + line;
                         candidates.put(norm, temp);
                         }
                         */
                    }
                }
            }
            // FIXME: Hamdy, 2014-06-26. No write permission
            File file = new File(filePath + ".ser");
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(candidates);
            oos.close();
        }

        fileLines.clear();
        
        msg = String.format("loadAljazeeraBW() end. candidates.size()=%d", candidates.size());
        System.out.println(msg);
        
        return candidates;
    }

    
    /*private static HashMap<String, Double> loadKenLM(String filePath) throws FileNotFoundException, IOException, ClassNotFoundException
    {
        int nofLines, ngram;
        String msg, line = "";
        HashMap<String, Double> lm = new HashMap<String, Double>();
        // char[] tab = {'\t'};
    
        msg = String.format("loadKenLM() start. filePath=%s", filePath);
        System.out.println(msg);

        // if (wikipediaArEn.isEmpty())
        File file = new File(filePath + ".ser");
        if (file.exists())
        {
            // System.out.println(df.format(cal.getTime()));
            ObjectInputStream ios = new ObjectInputStream(new FileInputStream(file));
            unigrams = (HashMap) ios.readObject();
            // System.out.println(df.format(cal.getTime()));
        }
        else
        {
            nofLines = 0;
            ngram = 0;
            BufferedReader sr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath))));
            while ((line = sr.readLine()) != null)
            {
                nofLines++;
                if ((nofLines % 10000) == 0)
                {
                    msg = String.format("loadKenLM(). nofLines=%d", nofLines);
                    System.out.println(msg);
                }

                if (line.contains("\\1-grams:"))
                {
                    ngram = 1;
                }
                else if (line.contains("\\2-grams:"))
                {
                    ngram = 2;
                }
                else if (line.contains("\\3-grams:"))
                {
                    ngram = 3;
                }

                if ((ngram == 2) && (line.length() > 0))
                {
                    String[] lineParts = line.split("\t");
                    if (line.length() > 0 && lineParts.length > 1) // && Regex.IsMatch("^[0-9\\.\\-]$"))
                    {
                        lm.put(lineParts[1], Double.parseDouble(lineParts[0]));
                    }
                }
            }
            // FIXME: Hamdy, 2014-06-26. No write permission
            //ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            //oos.writeObject(unigrams);
            //oos.close();
        }

        msg = String.format("loadKenLM() end. lm.size()=%d", lm.size());
        System.out.println(msg);
        
        return lm;
    }*/

    
    public String spellCheck(String input, boolean formatInput, boolean formatOutput, boolean considerContext)
    {
        int i, nofLines;
        String correction, correction2, line;
        String[] lines;
        ArrayList<String> words;
        ArrayList<Integer> latticeIndexes = new ArrayList<Integer>();
        HashMap<Integer, ArrayList<String>> lattice;

        // For site display, show formatted output and try all edits: insertion, deletion, and substitution.
        tryAllEdits = formatOutput;
        correction = "";
        try
        {
            boolean restorePunctOnly = false;
            if (restorePunctOnly) {
                return  restorePunct(input);
            }

            lines = input.trim().split("\n");
            nofLines = lines.length;

            for (i = 0; i < nofLines; i++)
            {
                line = lines[i].trim();
                
                words = ArabicUtils.tokenize(line);

                lattice = buildLattice(words, latticeIndexes);
                
                correction2 = findBestPath(lattice, words, latticeIndexes, formatOutput);
                correction += String.format("%s\n", correction2);
                
                lattice.clear();
            }
        }
    catch (Exception e)
        {
            correction = input;
            e.printStackTrace();
    }
        
        return correction;
    }

    
    private HashMap<Integer, ArrayList<String>> buildLattice(ArrayList<String> words, ArrayList<Integer>latticeIndexes) {
        int i = 0, j, k, m, len, orgWordIndex, nofWords, freq0, freq1, maxFreq, orgWordFreq, splitAction, tmpFreq, breakpoint, oov, latticeIndex;
        int[] nofCandidates = new int[1];
        //double score0, score1, score2;
        int score0, score1, score2;
        int[] offset = new int[2]; // Used for call by reference
        int[] bestFreqCAM = new int[2];
        char ch;
        String w, norm, corr, curr, error, prev, next, split, merge, candidates, orgCAM, w1, orgWord;
        HashMap<Integer, ArrayList<String>> lattice = new HashMap<Integer, ArrayList<String>>();

        ArrayList<String> temp = new ArrayList<String>();

        temp.add("<s>");
        lattice.put(i, temp);
        
        latticeIndexes.clear();
        latticeIndex = -1;
        latticeIndexes.add(latticeIndex);

        i++;
        //lattice.put(i, temp); FIXME: shifted up before incrementing i

        //try {
            prev = "<s>";
            next = "</s>";
            nofWords = words.size();
            for (j = 0; j < nofWords; j++) {
                orgWordIndex = j;
                if (j > 0) {
                    prev = words.get(j - 1);
                }
                else {
                    prev = "<s>";
                }

                if (j < nofWords - 1) {
                    next = words.get(j + 1);
                }
                else {
                    next = "</s>";
                }

                w = words.get(j);
                orgWord = w;

                if (!isArabicWord(w)) {
                    temp = new ArrayList<String>();
                    
                    // Correct Arabic punctuation marks
                    if (w.equals("?")) {
                        w = "؟";
                    }
                    else if (w.equals(",")) {
                        w = "،";
                    }
                    
                    temp.add(w);
                    lattice.put(i, temp);
                    
                    latticeIndex = orgWordIndex;
                    latticeIndexes.add(latticeIndex);

                    i++;
                    continue;
                }
 
                w = correctLeadingLamAlefLam(w);
                
                // ليه -> لماذا
                corr = getOfflineCorrections(w, j, nofWords, words, offset);

                if (corr.length() > 0) {
                    String[] suggestions = corr.split("/");
                    if (suggestions.length == 1) {
                        String[] lineParts = suggestions[0].split("_");
                        for (m = 0; m < lineParts.length; m++) {
                            temp = new ArrayList<String>();

                            temp.add(lineParts[m].trim());
                            lattice.put(i, temp);

                            latticeIndex = orgWordIndex;
                            latticeIndexes.add(latticeIndex);

                            i++;
                        }
                    }
                    else {
                        // اللي\tالذي/التي/الذين
                        temp = new ArrayList<String>();
                        for (m = 0; m < suggestions.length; m++) {

                            temp.add(suggestions[m].trim());
                        }
                        lattice.put(i, temp);

                        latticeIndex = orgWordIndex;
                        latticeIndexes.add(latticeIndex);
                        
                        i++;
                    }

                    j += offset[0];
                    continue;
                }

                len = w.length();
                if ((j < nofWords - 1) && (len == 1)) {
                    if (w.equals("و")) {
                        w1 = correctLeadingLamAlefLam(words.get(j + 1));
                        curr = w + w1;

                        ch = curr.charAt(1);
                        if ((ch >= 'ء') && (ch <= 'ي') && (curr.length() > 2)) {
                            w = curr;
                            j++;
                        }
                    }
                }

                orgCAM = "";
                candidates = "";
                nofCandidates[0] = 0;
                oov = 1;

                corr = correctSurfaceErrors(w);

                curr = w;
                if (corr.length() > 0) {
                    curr = corr;
                    oov = 0;

                    // Search for offline corrections again, ex: لييييييه
                    corr = getOfflineCorrections(curr, j, nofWords, words, offset);

                    if (corr.length() > 0) {
                        String[] suggestions = corr.split("/");
                        if (suggestions.length == 1) {
                            String[] lineParts = suggestions[0].split("_");
                            for (m = 0; m < lineParts.length; m++) {
                                temp = new ArrayList<String>();

                                temp.add(lineParts[m].trim());
                                lattice.put(i, temp);

                                latticeIndex = orgWordIndex;
                                latticeIndexes.add(latticeIndex);
                                
                                i++;
                            }
                        }
                        else {
                            // اللي\tالذي/التي/الذين
                            temp = new ArrayList<String>();
                            for (m = 0; m < suggestions.length; m++) {
                                temp.add(suggestions[m].trim());
                            }
                            lattice.put(i, temp);

                            latticeIndex = orgWordIndex;
                            latticeIndexes.add(latticeIndex);
                            
                            i++;
                        }
                        j += offset[0];
                        continue;
                    }
                }
                else {
                    //corr = correctCAM(w, 1, bestFreqCAM);
                    //if (corr.length() > 0) {
                    //    curr = corr;
                    //}
                    //else {

                        candidates = getCandidates(w, nofCandidates);
                        if (nofCandidates[0] == 0) {
                            norm = correctLeadingLamAlefLam(normalizeFull(w));

                            candidates = getCandidates(norm, nofCandidates);
                            if (nofCandidates[0] > 0) {
                                // Try to correct word before full normalization, ex: شئ ->شيء before converting it to شء
                                orgCAM = correctCAM(w, 0, bestFreqCAM);
                                
                                curr = norm;
                                oov = 0;
                                
                                // If the word has only one form at unigram file, and has no error, then keep the original word, ex: إعتقاد
                                if (nofCandidates[0] == 1){
                                    String[] wordFreq = candidates.split("_");
                                    
                                    if (orgCAM.length() > 0) {
                                        // Word الطائفه  has only one correct form "الظائفة" and this is the same like CAM correction. So, take any one of them
                                        if (orgCAM.equals(wordFreq[0])) {
                                            orgCAM = "";
                                        }
                                    }

                                    if (orgCAM.length() == 0) {
                                        //curr = w;
                                        curr = wordFreq[0];
                                        oov = 0;
                                    }
                                    else
                                    {
                                        breakpoint = 1;
                                    }
                                }
                            }
                            else {
                                if (!w.equals(norm)){
                                    freq0 = getNGramFreq(w, false, 1);
                                    freq1 = getNGramFreq(norm, false, 1);

                                    if ((freq0 * 100) < (freq1 * FREQ_PERCENT_THRESHOLD)) {
                                        curr = norm;
                                    }
                                }
                                // Word doesn't appear in unigrams (OOV: may be a NE or a normal word that is not exist in LM)
                                breakpoint = 1;
                            }
                        }
                    //}
                }

                candidates = getCandidates(curr, nofCandidates);
                if (nofCandidates[0] > 1) {
                    temp = new ArrayList<String>();

                    // Solve conflict between candidate unigrams and splitting errors: ماهذا    ماهذا ماهذأ
                    // Get best frequency for candidate unigrams and splitting, then select the correct action
                    splitAction = 0;
                    
                    split = splitWord(curr, "", "");

                    if (split.length() > 0) {
                        //score2 = getLMScore(split);
                        score2 = getNGramFreq(split, false, 2);
                   
                        score1 = -1000;
                        for (String s : candidates.split(" +")) {
                            if (s.length() > 0) {
                                String[] wordFreq = s.split("_");
                                //score0 = getLMScore(wordFreq[0]);
                                score0 = getNGramFreq(wordFreq[0], false, 1);
                                tmpFreq = Integer.parseInt(wordFreq[1]);
                                
                                //score0 = getLMScore(s);
                                if (score1 < score0) {
                                    score1 = score0;
                                }
                            }
                        }
                        if (score2 > score1) {
                            splitAction = 1;
                        }
                    }

                    if (splitAction == 0) {
                        // Hamdy, 2014-06-26. Filter candidates according to aljazeear unigrams frequency. ايضا    أيضا ايضا إيضا آيضا أيضأ ايضأ أىضا إيضأ
                        k = 0;
                        freq0 = 0;
                        maxFreq = 0;
                        orgWordFreq = 0;
                        for (String s : candidates.split(" +")) {
                            if (s.length() > 0) {
                                String[] wordFreq = s.split("_");
                                tmpFreq = Integer.parseInt(wordFreq[1]);
                                
                                if (maxFreq < tmpFreq)
                                {
                                    maxFreq = tmpFreq;
                                }
                                if (orgWord.equals(wordFreq[0]))
                                {
                                    orgWordFreq = tmpFreq;
                                }

                                if (k == 0) {
                                    //freq0 = getNGramFreq(wordFreq[0], false, 1);
                                    freq0 = tmpFreq;
                                }
                                else {
                                    //freq1 = getNGramFreq(wordFreq[0], false, 1);
                                    freq1 = tmpFreq;

                                    if ((freq1 * 100) < (freq0 * FREQ_PERCENT_THRESHOLD)) {
                                        continue;
                                    }
                                }
                                temp.add(wordFreq[0]);
                            }
                            k++;
                        }
                        
                        if (orgCAM.length() > 0) {
                            temp.add(orgCAM);
                        }

                        if (temp.size() > 0) {
                            // Solve errors in candidate unigrams, الفاءده    الفاءدة الفاءده, قاريء    قارىء قاريء
                            if (freq0 <= 1 ) {
                                corr = correctCAM(w, 0, bestFreqCAM);
                                if (corr.length() > 0) {
                                    temp.clear();
                                    temp.add(corr);
                                }
                            }
                            
                            if ((orgWordFreq > 0) && (orgWordFreq > maxFreq / 2))
                            {
                                // FIXME: Keep user input and don't correct it!
                                temp.clear();
                                temp.add(orgWord);
                            }
                            
                            lattice.put(i, temp);

                            latticeIndex = orgWordIndex;
                            latticeIndexes.add(latticeIndex);                        
                        }
                    }
                    else {
                        String[] lineParts = split.split(" ");
                        for (m = 0; m < lineParts.length; m++) {
                            temp = new ArrayList<String>();

                            temp.add(lineParts[m].trim());
                            lattice.put(i, temp);

                            latticeIndex = orgWordIndex;
                            latticeIndexes.add(latticeIndex);
                            
                            i++;
                        }
                        continue;
                    }
                } else {
                    if (nofCandidates[0] > 0) {
                        oov = 0;
                    }
                    // Correct words that are found in aljazeera but not found in candidate unigrams, ex: اباحت، اباحوا
                    corr = correctCAM(curr, 0, bestFreqCAM);

                    if (corr.length() > 0) {
                        curr = corr;
                        oov = 0;
                    }
                    else {
                        merge = mergeWords(curr, prev, next);
                        if (merge.length() > 0) {
                            temp = new ArrayList<String>();

                            temp.add(merge);
                            lattice.put(i, temp);

                            latticeIndex = orgWordIndex;
                            latticeIndexes.add(latticeIndex);
                            
                            i++;
                            j++;
                            continue;
                        }

                        split = splitWord(curr, prev, next);

                        if (split.length() > 0) {
                            String[] lineParts = split.split(" ");
                            for (m = 0; m < lineParts.length; m++) {
                                temp = new ArrayList<String>();

                                temp.add(lineParts[m].trim());
                                lattice.put(i, temp);

                                latticeIndex = orgWordIndex;
                                latticeIndexes.add(latticeIndex);
                                
                                i++;
                            }
                            continue;
                        }
                    }

                    if (oov == 1) {
                        corr = correctUnknownWords(curr);
                        
                        if (corr.length() > 0) {
                            String[] lineParts = corr.split("_");
                            for (m = 0; m < lineParts.length; m++) {
                                temp = new ArrayList<String>();

                                temp.add(lineParts[m].trim());
                                lattice.put(i, temp);

                                latticeIndex = orgWordIndex;
                                latticeIndexes.add(latticeIndex);
                                
                                i++;
                            }
                            continue;
                        }
                        else if (tryAllEdits)
                        {
                            temp = edits(curr, 10);
                            
                            if (!temp.isEmpty())
                            {
                                lattice.put(i, temp);

                                latticeIndex = orgWordIndex;
                                latticeIndexes.add(latticeIndex);
                                i++;
                                
                                continue;
                            }
                        }
                    }

                    temp = new ArrayList<String>();
                    temp.add(curr);

                    if ((orgCAM.length() > 0) && (!orgCAM.equals(curr))) {
                        temp.add(orgCAM);
                    }

                    lattice.put(i, temp);

                    latticeIndex = orgWordIndex;
                    latticeIndexes.add(latticeIndex);                
                }
                i++;
            }
            temp = new ArrayList<String>();
            temp.add("</s>");
            lattice.put(i, temp);

            latticeIndex = -1;
            latticeIndexes.add(latticeIndex);
            
        //} catch(IOException ex) {
        //    System.out.println("HashMap(), Exception");
        //}

        return lattice;
    }
    
    
    private boolean isArabicWord(String s) {
        boolean arabic = false;
        char ch;
        
        ch = s.charAt(0);
        if ((ch >= 'ء') && (ch <= 'ي')) {
            arabic = true;
        }
        
        return arabic;
    }
    
    
    private String getOfflineCorrections(String w, int j, int nofWords, ArrayList<String> words, int[] offset) {
        String corr, error, w1, w2;

        if (false && applyReplaceWordsOnly) { //FIXME
            return "";
        }

        corr = "";
        offset[0] = -1;
        error = "";
        // Handle offline corrections with diferent formats:
        // ليه\tلماذا
        // انشاء_الله\tإن_شاء_الله
        // اللي\tالذي/التي/الذين
        // Try 3-gram errors
        if (j < nofWords - 2) {
            w1 = correctLeadingLamAlefLam(words.get(j + 1));
            w2 = correctLeadingLamAlefLam(words.get(j + 2));
            error = String.format("%s_%s_%s", w, w1, w2);
            if (corrections.containsKey(error)) {
                corr = corrections.get(error);
                offset[0] = 2;
            }
        }
        // Try 2-gram errors
        if ((offset[0] == -1) && (j < nofWords - 1)) {
            w1 = correctLeadingLamAlefLam(words.get(j + 1));
            error = String.format("%s_%s", w, w1);
            if (corrections.containsKey(error)) {
                corr = corrections.get(error);
                offset[0] = 1;
            }
        }
        // Try 1-gram errors
        if (offset[0] == -1) {
            error = String.format("%s", w);
            if (corrections.containsKey(error)) {
                corr = corrections.get(error);
                offset[0] = 0;
            }
        }
        
        return corr;
    }

    
    //private static String getPunct(String w, int j, int nofWords, ArrayList<String> words, int[] offset) {
    private String getPunct(String w, int j, int nofWords, HashMap<Integer, String> finalAnswer, int[] offset) {
        String punct, s, w1, w2;

        if (applyReplaceWordsOnly) {
            return "";
        }

        punct = "";
        offset[0] = -1;
        s = "";
        // Try 3-gram punctuation strings
        if (j < nofWords - 2) {
            w1 = finalAnswer.get(j + 1);
            w2 = finalAnswer.get(j + 2);
            s = String.format("%s_%s_%s", w, w1, w2);
            if (punctFreq.containsKey(s)) {
                punct = punctFreq.get(s);
                offset[0] = 2;
            }
        }
        // Try 2-gram punctuation strings
        if ((offset[0] == -1) && (j < nofWords - 1)) {
            w1 = finalAnswer.get(j + 1);
            s = String.format("%s_%s", w, w1);
            if (punctFreq.containsKey(s)) {
                punct = punctFreq.get(s);
                offset[0] = 1;
            }
        }
        // Try 1-gram punctuation strings
        if (offset[0] == -1) {
            s = String.format("%s", w);
            if (punctFreq.containsKey(s)) {
                punct = punctFreq.get(s);
                offset[0] = 0;
            }
        }
        
        return punct;
    }

                
    private String correctSurfaceErrors(String s) {
        int i, freq0 = 0, freq1 = 0, corrected = 0, len, breakpoint;
        int[] bestFreqCAM = new int[2];
        char ch, ch2;
        boolean searchInCandidateUnigrams = true;
        String corr = "", tmp, tmp2, tmp3;
        
        len = s.length();
        if (len > 2) {
            if ((s.indexOf("اا") >= 0) || (s.indexOf("يي") >= 0) || (s.indexOf("وو") >= 0)){
                // أخييييييراااااا  -> أخيرا
                tmp = s.replaceAll("[اا]+", "ا");
                tmp = tmp.replaceAll("[يي]+", "ي");
                tmp = tmp.replaceAll("[وو]+", "و");
                freq1 = getNGramFreq(tmp, searchInCandidateUnigrams, 1);
                
                if (freq1 == 0) {
                    tmp = correctCAM(tmp, 0, bestFreqCAM);
                    if (tmp.length() > 0) {
                        freq1 = getNGramFreq(tmp, searchInCandidateUnigrams, 1);
                    }
                }
                if (freq1 != 0) {
                    corr = tmp;
                    corrected = 1;
                }
                else if (s.startsWith("اا")) {
                    // ااحضارة - > الحضارة
                    tmp = "ال" + s.substring(2);

                    freq1 = getNGramFreq(tmp, searchInCandidateUnigrams, 1);

                    if (freq1 == 0) {
                        tmp = correctCAM(tmp, 0, bestFreqCAM);
                        if (tmp.length() > 0) {
                            freq1 = getNGramFreq(tmp, searchInCandidateUnigrams, 1);
                        }
                    }
                    if (freq1 != 0) {
                        corr = tmp;
                        corrected = 1;
                    }
                }
            }                
            
            // سماءا  -> سماء
            if (corrected == 0) {
                if (s.endsWith("اءا")) {
                    tmp = s.substring(0, len -1);
                    freq1 = getNGramFreq(tmp, searchInCandidateUnigrams, 1);

                    if (freq1 == 0) {
                        tmp = correctCAM(tmp, 0, bestFreqCAM);
                        if (tmp.length() > 0) {
                            freq1 = getNGramFreq(tmp, searchInCandidateUnigrams, 1);
                        }
                    }
                    if (freq1 != 0) {
                        corr = tmp;
                        corrected = 1;
                    }
                }                
            }

            // افعلو -> افعلوا
            if ((corrected == 0) && (len > 2)) {
                if (s.endsWith("و")) {
                    tmp = s + "ا";
                    freq1 = getNGramFreq(tmp, searchInCandidateUnigrams, 1);

                    if (freq1 == 0) {
                        tmp = correctCAM(tmp, 0, bestFreqCAM);
                        if (tmp.length() > 0) {
                            freq1 = getNGramFreq(tmp, searchInCandidateUnigrams, 1);
                        }
                    }
                    if (freq1 != 0) {
                        corr = tmp;
                        corrected = 1;
                    }
                }                
            }

            if ((corrected == 0) && (len > 3)) {
                freq0 = getNGramFreq(s, searchInCandidateUnigrams, 1);
                if (freq0 == 0) {
                    // بكثيرررررررر-> بكثير
                    for (i = 0; i < len - 1; i++) {
                        ch = s.charAt(i);
                        ch2 = s.charAt(i + 1);

                        if (ch == ch2) {
                            tmp2 = String.format("[%c%c]+", ch, ch2);
                            tmp3 = String.format("%c", ch);
                            tmp = s.replaceAll(tmp2, tmp3);
                            freq1 = getNGramFreq(tmp, searchInCandidateUnigrams, 1);

                            //if (freq1 == 0) {
                            //    tmp = correctCAM(tmp, 0, bestFreqCAM);
                            //    if (tmp.length() > 0) {
                            //        freq1 = getNGramFreq(tmp, searchInCandidateUnigrams, 1);
                            //    }
                            //}
                            if (freq1 != 0) {
                                corr = tmp;
                                corrected = 1;
                                break;
                            }
                        }
                    }
                    
                    if (corrected == 0) {
                        // أردنين-> أردنيين
                        if (s.endsWith("ين") && !s.endsWith("يين")) {
                            tmp = s.substring(0, len - 1);
                            tmp = tmp + "ين";
                            freq1 = getNGramFreq(tmp, searchInCandidateUnigrams, 1);

                            if (freq1 != 0) {
                                corr = tmp;
                                corrected = 1;
                            }                            
                        }
                    }
                }
            }        
        }
    
        // Sometimes, words at aljazeera corpus contain errors, ex: اللذي. So, give chance for correction
        if (unigrams.containsKey(s)) {
            freq0 = unigrams.get(s);
        }
        
        if ((freq0 == 0) && (freq1 == -1)) {
            // Orgiginal word is not found in the aljazeera words and  it's found in candidate unigrams for corrections
            breakpoint  = 1;
        }
        else if ((freq0 == 0) && (freq1 == 1)) {
            // Ignore correction with very low fequency وللتقطيع ->ولتقطيع
            corr = "";
        }
        else if ((freq0 * 100) >= (freq1 * FREQ_PERCENT_THRESHOLD)) {
            if ((freq0 == 1) && (freq1 >= 10)) {
                // Word has very low frequency = 1 (تقفو) and its correction (تقفوا) has a better frequency = 40
                breakpoint = 1;
            }
            else {
                corr = "";
            }
        }

        return corr;
    }
    
    
    private String correctCAM(String s, int maxNofSugg, int[] bestFreqCAM) {
        int i, len, orgFreq, freq, bestFreq, nofSugg;
        boolean searchInCandidateUnigrams = false;
        String corr = "", tmp;
        char ch;
        
        orgFreq = 0;
        bestFreq = 0;
        bestFreqCAM[0] = -1;
        nofSugg = 0;
        len = s.length();

        if (len > 1) {
            for (i = 0; i < len; i++) {
                ch = s.charAt(i);
                
                if (ch == 'ا') {
                    // احمال -> احمال
                    tmp = s.substring(0, i) + "أ" + s.substring(i + 1);
                    freq = getNGramFreq(tmp, searchInCandidateUnigrams, 1);
                    
                    if (freq > 0) {
                        nofSugg++;
                    }
                                           
                    if (bestFreq < freq) {
                        bestFreq = freq;
                        corr = tmp;
                    }

                    // اعطاء -> إعطاء
                    tmp = s.substring(0, i) + "إ" + s.substring(i + 1);
                    freq = getNGramFreq(tmp, searchInCandidateUnigrams, 1);
                    
                    if (freq > 0) {
                        nofSugg++;
                    }

                    if (bestFreq < freq) {
                        bestFreq = freq;
                        corr = tmp;
                    }

                    // الية -> آلية
                    tmp = s.substring(0, i) + "آ" + s.substring(i + 1);
                    freq = getNGramFreq(tmp, searchInCandidateUnigrams, 1);
                    
                    if (freq > 0) {
                        nofSugg++;
                    }

                    if (bestFreq < freq) {
                        bestFreq = freq;
                        corr = tmp;
                    }
                    bestFreqCAM[0] = bestFreq;
                }

                if (ch == 'ء') {
                    // فاءدة -> فائدة
                    tmp = s.substring(0, i) + "ئ" + s.substring(i + 1);
                    freq = getNGramFreq(tmp, searchInCandidateUnigrams, 1);
                    
                    if (freq > 0) {
                        nofSugg++;
                    }
                                           
                    if (bestFreq < freq) {
                        bestFreq = freq;
                        corr = tmp;
                    }

                    // 
                    tmp = s.substring(0, i) + "ؤ" + s.substring(i + 1);
                    freq = getNGramFreq(tmp, searchInCandidateUnigrams, 1);
                    
                    if (freq > 0) {
                        nofSugg++;
                    }

                    if (bestFreq < freq) {
                        bestFreq = freq;
                        corr = tmp;
                    }
                    bestFreqCAM[0] = bestFreq;
                }

                if (ch == 'ؤ') {
                    tmp = s.substring(0, i) + "ئ" + s.substring(i + 1);
                    freq = getNGramFreq(tmp, searchInCandidateUnigrams, 1);
                    
                    if (freq > 0) {
                        nofSugg++;
                    }
                                           
                    if (bestFreq < freq) {
                        bestFreq = freq;
                        corr = tmp;
                    }

                    // مرؤس -> مرؤوس
                    tmp = s.substring(0, i) + "ؤو" + s.substring(i + 1);
                    freq = getNGramFreq(tmp, searchInCandidateUnigrams, 1);
                    
                    if (freq > 0) {
                        nofSugg++;
                    }

                    if (bestFreq < freq) {
                        bestFreq = freq;
                        corr = tmp;
                    }
                    bestFreqCAM[0] = bestFreq;
                }
                
                if ((i == len - 1) && (ch == 'ي')) {
                    // الأقصي -> الأقصى
                    tmp = s.substring(0, i) + "ى";
                    freq = getNGramFreq(tmp, searchInCandidateUnigrams, 1);
                    
                    if (freq > 0) {
                        nofSugg++;
                    }

                    if (bestFreq < freq) {
                        bestFreq = freq;
                        corr = tmp;
                    }
                    bestFreqCAM[0] = bestFreq;
                }

                if ((i == len - 1) && (ch == 'ه')) {
                    // إنسانيه-> إنسانية
                    tmp = s.substring(0, i) + "ة";
                    freq = getNGramFreq(tmp, searchInCandidateUnigrams, 1);
                    
                    if (freq > 0) {
                        nofSugg++;
                    }

                    if (bestFreq < freq) {
                        bestFreq = freq;
                        corr = tmp;
                    }
                    bestFreqCAM[0] = bestFreq;
                }

                if ((i == len - 1) && (ch == 'ئ')) {
                    // شئ   -> شيء
                    tmp = s.substring(0, i) + "يء";
                    freq = getNGramFreq(tmp, searchInCandidateUnigrams, 1);
                    
                    if (freq > 0) {
                        nofSugg++;
                    }

                    if (bestFreq < freq) {
                        bestFreq = freq;
                        corr = tmp;
                    }
                    bestFreqCAM[0] = bestFreq;
                }

                if ((i == len - 3) && ((ch == 'ي') || (ch == 'ى')) && (s.charAt(i + 1) == 'ى') && (s.charAt(i + 2) == 'ء')) {
                    // شيىء -> شيء
                    tmp = s.substring(0, i + 1) + "ء";
                    freq = getNGramFreq(tmp, searchInCandidateUnigrams, 1);
                    
                    if (freq > 0) {
                        nofSugg++;
                    }

                    if (bestFreq < freq) {
                        bestFreq = freq;
                        corr = tmp;
                        bestFreqCAM[0] = bestFreq;
                        i++;
                        continue;
                    }
                    bestFreqCAM[0] = bestFreq;
                }
                
                if ((i == len - 2) && ((ch == 'ي') || (ch == 'ى')) && (s.charAt(i + 1) == 'ء')) {
                    // قاريء -> قارئ
                    tmp = s.substring(0, i) + "ئ";
                    freq = getNGramFreq(tmp, searchInCandidateUnigrams, 1);
                    
                    if (freq > 0) {
                        nofSugg++;
                    }

                    if (bestFreq < freq) {
                        bestFreq = freq;
                        corr = tmp;
                        bestFreqCAM[0] = bestFreq;
                        i++;
                        continue;
                    }
                    bestFreqCAM[0] = bestFreq;
                }

                if ((i == len - 2) && (ch == 'ي') && (s.charAt(i + 1) == 'ئ')) {
                    // شيئ -> شيء
                    tmp = s.substring(0, i + 1) + "ء";
                    freq = getNGramFreq(tmp, searchInCandidateUnigrams, 1);
                    
                    if (freq > 0) {
                        nofSugg++;
                    }

                    if (bestFreq < freq) {
                        bestFreq = freq;
                        corr = tmp;
                        bestFreqCAM[0] = bestFreq;
                        i++;
                        continue;
                    }
                    bestFreqCAM[0] = bestFreq;
                }
                
                if ((i == len - 1) && (ch == 'آ')) {
                    // دائمآ -> دائما
                    tmp = s.substring(0, i) + "ا";
                    freq = getNGramFreq(tmp, searchInCandidateUnigrams, 1);
                    
                    if (freq > 0) {
                        nofSugg++;
                    }

                    if (bestFreq < freq) {
                        bestFreq = freq;
                        corr = tmp;
                    }
                    bestFreqCAM[0] = bestFreq;
                }
            }
        }
    
        if (corr.length() > 0) {
            orgFreq = getNGramFreq(s, searchInCandidateUnigrams, 1);

            if ((maxNofSugg == 1) && (nofSugg > 1)) {
                corr = "";
            }
            else if (bestFreq < orgFreq) {
                corr = "";
            }
        }

        return corr;
    }
    
    
    private String correctUnknownWords(String w) {
        int nofPrefixes, nofSuffixes, i, j, len, len2, freq, freq2, freq3, affixExpansion, breakpoint;
        int[] bestFreqCAM = new int[2];
        String s = "", tmp, tmp2, tmp3, corr;
        boolean searchInCandidateUnigrams = true;
        //String[] prefixes = {"و", "ف", "ال", "ب", "ل", "ك", "وال", "فال", "وب", "ول", "فب", "فل", "وبال", "ولل", "فبال", "فلل", "س"};
        String[] prefixes = {"و", "ف", "ب", "ل", "ك", "س"};
        String[] anit = {"أ", "ن", "ي", "ت"};
        //String[] suffixes = {"ي", "نا", "ك", "كما", "كم", "كن", "ه", "ها", "هما", "هم", "هن", "ت", "تما", "تم", "تن", "ا", "تا", "وا", "ن", "ين", "ي", "ون", "ان",
        //    "ته", "تها", "تهما", "تهم", "ناه", "ناها", "ناهما", "ناهم", "تينه", "", "", "", ""};
        
        len = w.length();
        affixExpansion = 0;

        if (affixExpansion == 0) {
            // Check simple prefixes
            nofPrefixes = prefixes.length;
            affixExpansion = 0;

            for (i = 0; i < nofPrefixes; i++) {
                if (w.startsWith(prefixes[i])) {
                    len2 = prefixes[i].length();
                    tmp = w.substring(len2);

                    //corr = correctCAM(tmp, 1, bestFreqCAM);
                    freq = getNGramFreq(tmp, searchInCandidateUnigrams, 1);

                    if (freq > 0) {
                        affixExpansion = 1;
                        break;
                    }
                }
            }
        }

        // Check dialectal prefixes, بيقول، ديقول
        if ((affixExpansion == 0) && (w.startsWith("ب") || w.startsWith("د"))) {
            tmp = w.substring(1);
            j = anit.length;
            for (i = 0; i < j; i++) {
                if (tmp.startsWith(anit[i])) {
                    freq = getNGramFreq(tmp, searchInCandidateUnigrams, 1);

                    if (freq > 0) {
                        s = tmp;
                        affixExpansion = 1;
                        break;
                    }
                }
            }
        }
                
        // Check dialectal prefixes, حيقول، هيقول
        if ((affixExpansion == 0) && (w.startsWith("ه") || w.startsWith("ح"))) {
            tmp = w.substring(1);
            j = anit.length;
            for (i = 0; i < j; i++) {
                if (tmp.startsWith(anit[i])) {
                    tmp = "س" + tmp;
                    freq = getNGramFreq(tmp, searchInCandidateUnigrams, 1);

                    if (freq > 0) {
                        s = tmp;
                        affixExpansion = 1;
                        break;
                    }
                }
            }
        }
        
        if (!applyReplaceWordsOnly) {        
            // Check dialectal prefixes, هالموضوع
            if ((affixExpansion == 0) && (len > 5) && w.startsWith("هال")) {
                tmp = w.substring(1);
                freq = getNGramFreq(tmp, searchInCandidateUnigrams, 1);

                if (freq > 0) {
                    tmp2 = "هذا " + tmp;
                    freq2 = getNGramFreq(tmp2, searchInCandidateUnigrams, 2);

                    tmp3 = "هذه " + tmp;
                    freq3 = getNGramFreq(tmp3, searchInCandidateUnigrams, 2);

                    if (freq2 >= freq3) {
                        s = "هذا_" + tmp;
                    } else {
                        s = "هذه_" + tmp;
                    }

                    affixExpansion = 1;
                }
            }

            // Check dialectal prefixes, عالأرض
            if ((affixExpansion == 0) && (len > 5) && w.startsWith("عال")) {
                tmp = w.substring(1);
                freq = getNGramFreq(tmp, searchInCandidateUnigrams, 1);

                if (freq > 0) {
                    s = "على_" + tmp;
                    affixExpansion = 1;
                }
            }
        }
        
        if (affixExpansion == 0) {
            // Solve soundex errors, المحافضة، نمازج، أخدوا، مظبوط
            tmp = w.replaceAll("ض", "ظ");
            freq = getNGramFreq(tmp, searchInCandidateUnigrams, 1);

            if (freq > 0) {
                s = tmp;
                affixExpansion = 1;
            }
            if (affixExpansion == 0) {
                tmp = w.replaceAll("ز", "ذ");
                freq = getNGramFreq(tmp, searchInCandidateUnigrams, 1);

                if (freq > 0) {
                    s = tmp;
                    affixExpansion = 1;
                }
            }
            if (affixExpansion == 0) {
                tmp = w.replaceAll("د", "ذ");
                freq = getNGramFreq(tmp, searchInCandidateUnigrams, 1);

                if (freq > 0) {
                    s = tmp;
                    affixExpansion = 1;
                }
            }
            if (affixExpansion == 0) {
                tmp = w.replaceAll("ظ", "ض");
                freq = getNGramFreq(tmp, searchInCandidateUnigrams, 1);

                if (freq > 0) {
                    s = tmp;
                    affixExpansion = 1;
                }
            }            
        }
        
        if ((s.length() > 0) && s.equals(w)) {
            s = "";
        }

        return s;
    }
    
    
    private String splitWord(String s, String prev, String next) {
        //double score0, score1, bestScore;
        boolean commonSplit, trySplit;
        int score0, score1, bestScore, breakpoint, f1, f2;
        int i, len, freq1, freq2;
        int[] bestFreqCAM = new int[2];
        String corr = "", s1, s2, path0, path1, corr1, corr2;
        char ch;
        
        if (false && applyReplaceWordsOnly) { //FIXME
            return "";
        }

        //try {
            score0 = -1000;
            score1 = -1000;
            bestScore = -1000;
            len = s.length();
            if (len > 4) {
                for (i = 1; i < len - 2; i++) {
                    ch = s.charAt(i);

                    trySplit = false;
                    commonSplit = false;
                    if ((ch == 'ا') || (ch == 'ء') || (ch == 'ة') || (ch == 'د') || (ch == 'ذ') || (ch == 'ر') || (ch == 'ز') || (ch == 'و') || (ch == 'ى')) {
                        commonSplit = true;
                    }
                    else if ((len > 8) && (i >= 3) && (i < len - 4))
                    {
                        // Add more restrictions on splitting. Try splitting to 2 parts with length >= 4 characters
                        trySplit = true;
                    }
                    
                    if (commonSplit || trySplit) //FIXME: true
                    {
                        if (score0 == -1000) {
                            //path0 = String.format("%s %s %s", prev, s, next);
                            path0 = String.format("%s %s %s", "", s, "");
                            //score0 = getLMScore(path0.trim());
                            score0 = getNGramFreq(path0.trim(), false, 1);
                        }

                        s1 = s.substring(0, i + 1);
                        s2 = s.substring(i + 1);

                        // Correct spelling mistakes for splitted words
                        corr1 = correctCAM(s1, 0, bestFreqCAM);
                        corr2 = correctCAM(s2, 0, bestFreqCAM);

                        if (corr1.length() > 0) {
                            breakpoint = 1;
                        } else {
                            corr1 = s1;
                        }

                        if (corr2.length() > 0) {
                            breakpoint = 1;
                        } else {
                            corr2 = s2;
                        }

                        //path1 = String.format("%s %s %s %s", prev, s1, s2, next);
                        path1 = String.format("%s %s %s %s", "", corr1, corr2, "");
                        //score1 = getLMScore(path1.trim());
                        score1 = getNGramFreq(path1.trim(), false, 2);

                        if (commonSplit && (score0 == 0) && (score1 == 0)) {
                            // Handle some common splitting cases
                            if (corr1.equals("لا") || corr1.equals("يا") || corr1.equals("أو") || corr1.endsWith("ة") || corr1.endsWith("اء") || corr2.startsWith("ال")) {
                                
                                f1 = getNGramFreq(corr1, false, 1);
                                f2 = getNGramFreq(corr2, false, 1);
                                
                                if ((f1 > 0) && (f2 > 0))
                                {
                                    score1 = 1;
                                }
                            }
                            else if (true && (len >= 10))
                            {
                                f1 = getNGramFreq(corr1, false, 1);
                                f2 = getNGramFreq(corr2, false, 1);
                                
                                if ((f1 > 0) && (f2 > 0))
                                {
                                    score1 = 1;
                                }                                
                            }
                            //if (corr1.equals("او")) {
                            //    corr1 = "أو";
                            //    score1 = 1;
                            //}
                        }

                        // sometimes, scores are close to each others (قادرون/قاد رون), so use percentage instead of comparison
                        if (score1 > score0) {
                            if (score1 > bestScore)
                            {
                                bestScore = score1;
                                corr = String.format("%s %s", corr1, corr2);
                            }
                        }
                    }
                }
            }
        //} catch(IOException ex){
        //    System.out.println("splitWord(), Exception");
        //}
        
        return corr;
    }
    
    
    private String mergeWords(String s, String prev, String next) {
        boolean searchInCandidateUnigrams = true;
        double score0, score1, bestScore;
        int i, len, len2, freq, breakpoint;
        String corr = "", path0, path1, tmp;
        
        if (false && applyReplaceWordsOnly) { //FIXME
            return "";
        }

        try {
            score0 = -1000;
            score1 = -1000;
            len = s.length();
            len2 = next.length();

            if ((len > 1) && (len2 == 1) && !next.equals("و")) {
                if (isArabicWord(s) && isArabicWord(next)) {
                    tmp = s + next;
                    freq = getNGramFreq(tmp, searchInCandidateUnigrams, 1);
                    
                    if (freq > 0) {
                        path0 = String.format("%s %s %s", prev, s, next);
                        score0 = getLMScore(path0);

                        path1 = String.format("%s %s%s", prev, s, next);
                        score1 = getLMScore(path1);

                        if (score1 > score0) {
                            corr = String.format("%s%s", s, next);
                        }
                    }
                    else {
                        breakpoint = 1;
                    }
                }
            }
        } catch(IOException ex){
            System.out.println("splitWord(), Exception");
        }
        
        return corr;
    }
    
    
    private int getNGramFreq(String s, boolean searchInCandidateUnigrams, int ngram) {
        int freq = 0;

        if (ngram == 1) {
            if (unigrams.containsKey(s)) {
                freq = unigrams.get(s);
            }
            //else if (searchInCandidateUnigrams) {
            //    if (candidatesUnigram.containsKey(s)) {
            //        freq = -1;
            //    }
            //}
            //if (freq == 1) {
            //    freq = 0;
            //}
        } else if (ngram == 2) {
            String tmp;
            
            tmp = s.replaceAll(" ", "_");
            if (bigrams.containsKey(tmp)) {
                freq = bigrams.get(tmp);
            }
        }
        
        return freq;
    }


    private String getCandidates(String s, int[] nofCandidates) {
        String candidates = "";
        nofCandidates[0] = 0;

        if (candidatesUnigram.containsKey(s)) {
            ArrayList<String> temp = new ArrayList<String>();

            candidates = candidatesUnigram.get(s);

            for (String c : candidates.split(" +")) {
                nofCandidates[0]++;
            }
        }
        
        return candidates;
    }
            
    
    private String findBestPath(HashMap<Integer, ArrayList<String>> lattice, ArrayList<String> words, ArrayList<Integer>latticeIndexes, boolean formatOutput) throws IOException {
        boolean breakpoint;
        int i, nofTokens, len, punctTotafFreq, punctBeforeFreq, punctAfterFreq, j, m, n, c, latticeSize, wordIndex, wordIndex2, nofInputWords, freq, freq2, maxFreq;
        int[] nofCandidates = new int[1];
        String space = " +", sBase, tmp, w, w2, punct, punctBefore, punctAfter, correction, undiacWord, undiacWord2, aljazeeraBW, candidates, norm;
        int[] punctOffset = new int[2]; // Used for call by reference
        String[] fields, fields2;
        HashMap<Integer, String> finalAnswer = new HashMap<Integer, String>();
       
        nofInputWords = words.size();

        finalAnswer.put(0, "<s>");

        latticeSize = lattice.keySet().size();
        for (i = 1; i < latticeSize - 1; i++) {
            sBase = finalAnswer.get(0);
            for (j = 1; j < i; j++) {
                sBase += " " + finalAnswer.get(j);
            }

            ArrayList<String> paths = new ArrayList<String>();
            // add options for current node
            for (String sol : lattice.get(i)) {
                paths.add(sBase + " " + correctLeadingLamAlefLam(sol));
            }

            ArrayList<String> pathsNext = new ArrayList<String>();
            // add options for next node
            for (String s : paths) {
                for (String sol : lattice.get(i + 1)) {
                    pathsNext.add(s + " " + correctLeadingLamAlefLam(sol));
                }
            }

            // determine best option for current word
            // this would be done using the language model
            String[] bestPath = findBestPathLMLastNWords(pathsNext, MAX_WINDOW_SIZE).trim().split(space);
            if (bestPath.length == i + 2)
            {
                if (formatOutput)
                {
                    wordIndex = latticeIndexes.get(i);

                    undiacWord = removeDiacritics(bestPath[i]);
                    undiacWord2 = removeDiacritics(words.get(wordIndex));
                    
                    if (!undiacWord2.equals(undiacWord))
                    {
                        correction = String.format("%s/%s", bestPath[i], words.get(wordIndex));
                        
                        wordIndex2 = latticeIndexes.get(i + 1);
                        if ((wordIndex2 > 0) && (wordIndex2 != wordIndex + 1))
                        {
                            for (n = wordIndex + 1; n < wordIndex2; n++)
                            {
                                correction += String.format("_%s", words.get(n));
                            }
                        }
                        else if ((wordIndex2 < 0) && (wordIndex < nofInputWords))
                        {
                            // Merge action at the end of input text, ex: input is و ارجوا
                            for (n = wordIndex + 1; n < nofInputWords; n++)
                            {
                                correction += String.format("_%s", words.get(n));
                            }
                        }
                        
                        // FIXME: favor use input if it's a high frequent word, ex:اليأس is corrected to إلياس
                        /*if (!correction.contains("_"))
                        {
                            w = words.get(wordIndex);
                            //norm = correctLeadingLamAlefLam(normalizeFull(w));
                            norm = normalizeFull(w);
                            candidates = getCandidates(norm, nofCandidates);
                            if (nofCandidates[0] > 0)
                            {
                                fields = candidates.split("\\s+");
                                freq2 = 0;
                                maxFreq = 0;
                                for (c = 0; c < fields.length; c++)
                                {
                                    fields2 = fields[c].split("_");
                                    w2 = fields2[0];
                                    freq = Integer.parseInt(fields2[1]);
                                    
                                    if (maxFreq < freq)
                                    {
                                        maxFreq = freq;
                                    }
                                    if (w2.equals(w))
                                    {
                                        freq2 = freq;
                                    }
                                }
                                
                                if (freq2 > maxFreq / 2)
                                {
                                    // Keep user input
                                    correction = words.get(wordIndex);
                                }
                            }
                        }*/
                    }
                    else
                    {
                        correction = bestPath[i];

                        // Add "/" at the end if the word didn't appear in LM (highlight as warning)
                        if (isArabicWord(correction))
                        {
                            freq = getNGramFreq(correction, false, 1);
                            if (freq < 1)
                            {
                                // Check if a valid word or not
                                aljazeeraBW = mgbAljazeeraBW.get(correction);
                                
                                if (aljazeeraBW == null)
                                {
                                    correction += "/";
                                }
                                else
                                {
                                    if (aljazeeraBW.isEmpty())
                                    {
                                        correction += "/";
                                    }
                                    else
                                    {
                                        //correction += "/#"; // If you want to mark such words.
                                        breakpoint = true;
                                    }
                                }
                            }
                        }
                    }
                }
                else
                {
                    correction = bestPath[i];
                }
                finalAnswer.put(i, correction);
            }
            else
            {
                System.err.println("ERROR");
            }
        }
        finalAnswer.put(latticeSize - 1, "</s>");
       
        boolean restorePunct = true;
        if (!applyReplaceWordsOnly && restorePunct) {
            // Insert punctuation marks بحيث -> ، بحيث
            nofTokens = finalAnswer.keySet().size();
            for (j = 1; j < nofTokens; j++) {
                w = finalAnswer.get(j);

                punct = getPunct(w, j, nofTokens, finalAnswer, punctOffset);

                if (punct.length() > 0) {
                    punctTotafFreq = 0;
                    punctBeforeFreq = 0;
                    punctAfterFreq = 0;
                    punctBefore = "";
                    punctAfter = "";

                    String[] punctParts = punct.split("_");
                    for (m = 0; m < punctParts.length; m++) {
                        switch (m) {
                            case 0:
                                punctTotafFreq = Integer.parseInt(punctParts[m].trim());
                            break;
                            case 1:
                                punctBefore = punctParts[m].trim();
                            break;
                            case 2:
                                punctBeforeFreq = Integer.parseInt(punctParts[m].trim());
                            break;
                            case 3:
                                punctAfter = punctParts[m].trim();
                            break;
                            case 4:
                                punctAfterFreq = Integer.parseInt(punctParts[m].trim());
                            break;
                        }
                    }

                    if (punctBefore.length() > 0) {
                        w = String.format("%s %s", punctBefore, finalAnswer.get(j));
                        w2 = finalAnswer.get(j - 1);

                        // Ignore punctuation before first word or after another punctuation
                        if ((j == 1) || wordHasPunct(w2)) {
                            breakpoint = true;
                        }
                        else {
                            finalAnswer.put(j, w);
                        }
                    }

                    if (punctAfter.length() > 0) {
                        if (j < nofTokens - punctOffset[0] - 1) {
                            w2 = finalAnswer.get(j + punctOffset[0] + 1);

                            if (wordHasPunct(w2)) {
                                breakpoint = true;
                            }
                            else {
                                if (punctOffset[0] == 0){
                                    w = String.format("%s %s", finalAnswer.get(j), punctAfter);
                                    finalAnswer.put(j, w);
                                }
                                else {
                                    w = String.format("%s %s", finalAnswer.get(j + 1), punctAfter);
                                    finalAnswer.put(j + 1, w);
                                    j += punctOffset[0];
                                }
                            }
                        }
                        else {
                            breakpoint = true;
                        }
                    }
                }
            }
        }

        nofTokens = finalAnswer.keySet().size();
        String sBest = "";
        
        if (nofTokens > 1)
        {
            sBest = "";
            for (int k = 1; k < nofTokens - 1; k++) {
                sBest += finalAnswer.get(k) + " ";
            }

            // Add punctuation marks at the end of sentence
            if (!applyReplaceWordsOnly) {
                len = sBest.length();
                tmp = finalAnswer.get(nofTokens - 1);
                if (tmp.length() == 1) {
                    if (tmp.equals(".") || tmp.equals("؟") || tmp.equals("!")) {
                        // Valid EOS
                        breakpoint = true;
                    }else {
                        if (tmp.equals("،")) {
                            sBest = sBest.substring(0, len - 1);
                            sBest += ".";
                        }else {
                            breakpoint = true;
                        }
                    }
                }else {
                    sBest += " .";
                }
            }
        }

        sBest = sBest.trim();
        return sBest;
    }

    
    private boolean wordHasPunct(String w) {
        boolean punct = false;
        
        if (w.length() == 1) {
            punct = true;
        }
        else if (w.contains("،") || w.contains(".") || w.contains("-") || w.contains("!") || w.contains("؟") || w.contains("؛") || w.contains(":")) {
            punct = true;
        }
        else if (w.equals("<s>") || w.equals("</s>")) {
            punct = true;
        }

        return punct;
    }

    
    public String correctLeadingLamAlefLam(String s) {
        if (s.startsWith("لال")) {
            s = "لل" + s.substring(3);
        }
        return s;
    }

    
    private String findBestPathLM(ArrayList<String> paths) throws IOException {
        if (paths.size() == 1)
        {
            return paths.get(0);
        }
        else
        {
            double bestScore = -1000000, finalScore;
            String bestPath = "";
            for (String s : paths) {
                finalScore = getLMScore(s);
                if (bestScore < finalScore) {
                    bestScore = finalScore;
                    bestPath = s;
                }
            }
            return bestPath;
        }
    }

    private String findBestPathLMLastNWords(ArrayList<String> paths, int numberOfWords) throws IOException {
        if (paths.size() == 1)
        {
            return paths.get(0);
        }
        else
        {
            double bestScore = -1000000, finalScore;
            String bestPath = "";
            for (String s : paths) {
                String[] words = s.split(" +");
                String lastNWords = "";
                if (words.length <= numberOfWords)
                {
                    // do nothing
                    lastNWords = s;
                }
                else
                {
                    // get last n words
                    
                    for (int i = words.length - numberOfWords; i < words.length; i++)
                    {
                        lastNWords += words[i] + " ";
                    }
                    lastNWords = lastNWords.trim();
                }
                finalScore = getLMScore(lastNWords);
                if (bestScore < finalScore) {
                    bestScore = finalScore;
                    bestPath = s;
                }
            }
            return bestPath;
        }
    }
    
    private double getLMScore(String s) throws IOException
    {
        int i, nofWords, freq;
        double d, finalScore;
        String w;
        String[] words;
        
        /* finalScore = -1000;
        bwLM.write(s + "\n");
        bwLM.flush();
        String stemp = brLM.readLine();
        if (stemp.contains("Total:")) {
            stemp = stemp.replaceFirst(".*Total\\:", "").trim();
            stemp = stemp.replaceFirst("OOV.*", "").trim();
        } else {
            stemp = "-1000";
        }
        finalScore = Double.parseDouble(stemp);*/
        
        /*d = kenLM.get(s);
        if (d != null)
        {
            finalScore = d;
        }*/
        
        // Use unigram and Bigram frequencies instead of KenLM
        finalScore = 0;
        words = s.split("\\s+");
        for (i = 0; i < words.length; i++)
        {
            s = words[i];
            if (s.equals("<s>") || s.equals("</s>"))
            {
                // <s> and </s> and not generated in unigrams LM
                continue;
            }
            freq = getNGramFreq(s, false, 1);
            
            if (freq > 0)
            {
                d = Math.log10((double)freq / (double)totalUnigramFreq);
            }
            else
            {
                d = -1000;
            }
            
            finalScore += d;
        }
        for (i = 0; i < words.length - 1; i++)
        {
            s = String.format("%s %s", words[i], words[i + 1]);
            freq = getNGramFreq(s, false, 2);
            
            if (s.equals("<s> أن"))
            {
                // Error in Aljazeera LM
                freq = 0;
            }
            if (freq > 0)
            {
                d = Math.log10((double)freq / (double)totalBigramFreq);
            }
            else
            {
                d = -1000;
            }
            
            finalScore += d;
        }
        
        return finalScore;
    }


    private String restorePunct(String s) throws IOException {
        int j, k, m, len, nofTokens, punctTotafFreq, punctBeforeFreq, punctAfterFreq, breakpoint;
        int[] punctOffset = new int[2]; // Used for call by reference
        String sBest, w, w2, tmp, punct, punctBefore, punctAfter;
        ArrayList<String> words;
        HashMap<Integer, String> finalAnswer = new HashMap<Integer, String>();

        words = ArabicUtils.tokenize(s);
      
        finalAnswer.put(0, "<s>");
        
        nofTokens = words.size();
        for (j = 0; j < nofTokens; j++) {
            finalAnswer.put(j + 1, correctLeadingLamAlefLam(words.get(j)));
        }
        finalAnswer.put(j + 1, "</s>");
      
        sBest = "";
        // Insert punctuation marks بحيث -> ، بحيث
        nofTokens = finalAnswer.keySet().size();
        for (j = 1; j < nofTokens; j++) {
            w = finalAnswer.get(j);

            punct = getPunct(w, j, nofTokens, finalAnswer, punctOffset);

            if (punct.length() > 0) {
                punctTotafFreq = 0;
                punctBeforeFreq = 0;
                punctAfterFreq = 0;
                punctBefore = "";
                punctAfter = "";

                String[] punctParts = punct.split("_");
                for (m = 0; m < punctParts.length; m++) {
                    switch (m) {
                        case 0:
                            punctTotafFreq = Integer.parseInt(punctParts[m].trim());
                        break;
                        case 1:
                            punctBefore = punctParts[m].trim();
                        break;
                        case 2:
                            punctBeforeFreq = Integer.parseInt(punctParts[m].trim());
                        break;
                        case 3:
                            punctAfter = punctParts[m].trim();
                        break;
                        case 4:
                            punctAfterFreq = Integer.parseInt(punctParts[m].trim());
                        break;
                    }
                }

                if (punctBefore.length() > 0) {
                    w = String.format("%s %s", punctBefore, finalAnswer.get(j));
                    w2 = finalAnswer.get(j - 1);

                    // Ignore punctuation before first word or after another punctuation
                    if ((j == 1) || wordHasPunct(w2)) {
                        breakpoint = 1;
                    }
                    else {
                        finalAnswer.put(j, w);
                    }
                }

                if (punctAfter.length() > 0) {
                    if (j < nofTokens - punctOffset[0] - 1) {
                        w2 = finalAnswer.get(j + punctOffset[0] + 1);

                        if (wordHasPunct(w2)) {
                            breakpoint = 1;
                        }
                        else {
                            if (punctOffset[0] == 0){
                                w = String.format("%s %s", finalAnswer.get(j), punctAfter);
                                finalAnswer.put(j, w);
                            }
                            else {
                                w = String.format("%s %s", finalAnswer.get(j + 1), punctAfter);
                                finalAnswer.put(j + 1, w);
                                j += punctOffset[0];
                            }
                        }
                    }
                    else {
                        breakpoint = 1;
                    }
                }
            }
        }

        nofTokens = finalAnswer.keySet().size();
        sBest = finalAnswer.get(1);
        for (k = 2; k < nofTokens - 1; k++) {
            sBest += " " + finalAnswer.get(k);
        }
                
        // Add punctuation marks at the end of sentence
        len = sBest.length();
        tmp = finalAnswer.get(nofTokens - 2);
        if (tmp.length() == 1) {
            if (tmp.equals(".") || tmp.equals("؟") || tmp.equals("!")) {
                // Valid EOS
                breakpoint = 1;
            }else {
                if (tmp.equals("،")) {
                    sBest = sBest.substring(0, len - 1);
                    sBest += ".";
                }else {
                    breakpoint = 1;
                }
            }
        }else {
            sBest += " .";
        }

        return sBest;
    }
    
    
    //http://norvig.com/spell-correct.html
    public ArrayList<String> edits(String word, int maxNofSugg)
    {
        final int MIN_SUGG_FREQ = 5;
        boolean foundWithDiffAffixes;
        int i, len, freq, n, prefixLen, suffixLen;
        char c;
        String s, diffAffix;
        ArrayList<String> result = new ArrayList<String>();
        final String[] PREFIXES = {"و", "ف", "ب", "لل",  "ل", "ك", "ال", "س"};
        //final String[] SUFFIXES = {"ة", "ه", "ها", "هما", "هم", "هن", "ك", "كما", "كم", "كن", "ون", "ين", "وا", "و", "ي", "ان", "ات", "ا"};
        final String[] SUFFIXES = {"ا", "ة", "ت", "ك", "ن", "و", "ي", "ات", "ان", "ون", "وا", "ين", "كما", "كم", "كن", "ه", "ها", "هما", "هم", "هن", "نا", "تما", "تم", "تن"};

        
        n = 0;
        len = word.length();
        
        if (len >= 5)
        {
            diffAffix = "";
            prefixLen = 0;
            suffixLen = 0;
            for (i = 0; i < PREFIXES.length; i++)
            {
                if (word.startsWith(PREFIXES[i]))
                {
                    prefixLen = PREFIXES[i].length();
                }
            }
            for (i = 0; i < SUFFIXES.length; i++)
            {
                if (word.endsWith(SUFFIXES[i]))
                {
                    suffixLen = SUFFIXES[i].length();
                }
            }
            
            foundWithDiffAffixes = false;
            //FIXME: 20170305
            if (prefixLen > 0)
            {
                diffAffix = word.substring(prefixLen);
                freq = getNGramFreq(diffAffix, false, 1);
                
                if (freq > 0)
                {
                    foundWithDiffAffixes = true;
                }
            }
            if ((suffixLen > 0) && !foundWithDiffAffixes)
            {
                diffAffix = word.substring(0, len - suffixLen);
                freq = getNGramFreq(diffAffix, false, 1);
                
                if (freq > 0)
                {
                    foundWithDiffAffixes = true;
                }                
            }
            if (false && (prefixLen > 0) && (suffixLen > 0) && !foundWithDiffAffixes)
            {
                diffAffix = word.substring(prefixLen, len - suffixLen);
                if (diffAffix.length() > 3)
                {
                    freq = getNGramFreq(diffAffix, false, 1);

                    if (freq > 0)
                    {
                        foundWithDiffAffixes = true;
                    }                
                }
            }

            if (!foundWithDiffAffixes)
            {
                for (i = 0; i < len; ++i)
                {
                    s = word.substring(0, i) + word.substring(i+1);
                    freq = getNGramFreq(s, false, 1);
                    if (freq >= MIN_SUGG_FREQ)
                    {
                        n++;
                        result.add(s);
                    }
                }
                for (i = 0; i < len - 1; ++i)
                {
                    s = word.substring(0, i) + word.substring(i+1, i+2) + word.substring(i, i+1) + word.substring(i+2);
                    freq = getNGramFreq(s, false, 1);
                    if (freq >= MIN_SUGG_FREQ)
                    {
                        n++;
                        result.add(s);
                    }
                }
                for (i = 0; i < len; ++i)
                {
                    for ( c = 'ء'; c <= 'ي'; ++c)
                    {
                        s = word.substring(0, i) + String.valueOf(c) + word.substring(i+1);
                        freq = getNGramFreq(s, false, 1);
                        if (freq >= MIN_SUGG_FREQ)
                        {
                            n++;
                            result.add(s);
                        }
                    }
                }
                for ( i = 0; i <= len; ++i)
                {
                    for (c = 'ء'; c <= 'ي'; ++c)
                    {
                        s = word.substring(0, i) + String.valueOf(c) + word.substring(i);
                        freq = getNGramFreq(s, false, 1);
                        if (freq >= MIN_SUGG_FREQ)
                        {
                            n++;
                            result.add(s);
                        }
                    }
                }
            }
            else
            {
                foundWithDiffAffixes = true;
                //String msg = String.format("foundWithDiffAffixes: %s:%s", word, diffAffix);
                //System.out.println(msg);                
            }
               
        }
        
        //if (result.isEmpty())
        //{
        //    result.add(word);
        //}

        return result;
    }
}
