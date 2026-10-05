package android.filterfw.io;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterFactory;
import android.filterfw.core.FilterGraph;
import android.filterfw.core.KeyValueMap;
import android.filterfw.core.ProtocolException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public class TextGraphReader extends GraphReader {
    private KeyValueMap mBoundReferences;
    private ArrayList<Command> mCommands = new ArrayList<>();
    private Filter mCurrentFilter;
    private FilterGraph mCurrentGraph;
    private FilterFactory mFactory;
    private KeyValueMap mSettings;

    private interface Command {
        void execute(TextGraphReader textGraphReader) throws GraphIOException;
    }

    private class ImportPackageCommand implements Command {
        private String mPackageName;

        public ImportPackageCommand(String str) {
            this.mPackageName = str;
        }

        @Override // android.filterfw.io.TextGraphReader.Command
        public void execute(TextGraphReader textGraphReader) throws GraphIOException {
            try {
                textGraphReader.mFactory.addPackage(this.mPackageName);
            } catch (IllegalArgumentException e) {
                throw new GraphIOException(e.getMessage());
            }
        }
    }

    private class AddLibraryCommand implements Command {
        private String mLibraryName;

        public AddLibraryCommand(String str) {
            this.mLibraryName = str;
        }

        @Override // android.filterfw.io.TextGraphReader.Command
        public void execute(TextGraphReader textGraphReader) {
            FilterFactory unused = textGraphReader.mFactory;
            FilterFactory.addFilterLibrary(this.mLibraryName);
        }
    }

    private class AllocateFilterCommand implements Command {
        private String mClassName;
        private String mFilterName;

        public AllocateFilterCommand(String str, String str2) {
            this.mClassName = str;
            this.mFilterName = str2;
        }

        @Override // android.filterfw.io.TextGraphReader.Command
        public void execute(TextGraphReader textGraphReader) throws GraphIOException {
            try {
                textGraphReader.mCurrentFilter = textGraphReader.mFactory.createFilterByClassName(this.mClassName, this.mFilterName);
            } catch (IllegalArgumentException e) {
                throw new GraphIOException(e.getMessage());
            }
        }
    }

    private class InitFilterCommand implements Command {
        private KeyValueMap mParams;

        public InitFilterCommand(KeyValueMap keyValueMap) {
            this.mParams = keyValueMap;
        }

        @Override // android.filterfw.io.TextGraphReader.Command
        public void execute(TextGraphReader textGraphReader) throws GraphIOException {
            try {
                textGraphReader.mCurrentFilter.initWithValueMap(this.mParams);
                textGraphReader.mCurrentGraph.addFilter(TextGraphReader.this.mCurrentFilter);
            } catch (ProtocolException e) {
                throw new GraphIOException(e.getMessage());
            }
        }
    }

    private class ConnectCommand implements Command {
        private String mSourceFilter;
        private String mSourcePort;
        private String mTargetFilter;
        private String mTargetName;

        public ConnectCommand(String str, String str2, String str3, String str4) {
            this.mSourceFilter = str;
            this.mSourcePort = str2;
            this.mTargetFilter = str3;
            this.mTargetName = str4;
        }

        @Override // android.filterfw.io.TextGraphReader.Command
        public void execute(TextGraphReader textGraphReader) {
            textGraphReader.mCurrentGraph.connect(this.mSourceFilter, this.mSourcePort, this.mTargetFilter, this.mTargetName);
        }
    }

    @Override // android.filterfw.io.GraphReader
    public FilterGraph readGraphString(String str) throws GraphIOException {
        FilterGraph filterGraph = new FilterGraph();
        reset();
        this.mCurrentGraph = filterGraph;
        parseString(str);
        applySettings();
        executeCommands();
        reset();
        return filterGraph;
    }

    private void reset() {
        this.mCurrentGraph = null;
        this.mCurrentFilter = null;
        this.mCommands.clear();
        this.mBoundReferences = new KeyValueMap();
        this.mSettings = new KeyValueMap();
        this.mFactory = new FilterFactory();
    }

    private void parseString(String str) throws GraphIOException {
        String strEat;
        Pattern pattern;
        PatternScanner patternScanner;
        Pattern pattern2;
        char c;
        Pattern patternCompile = Pattern.compile("@[a-zA-Z]+");
        Pattern patternCompile2 = Pattern.compile("\\}");
        Pattern patternCompile3 = Pattern.compile("\\{");
        Pattern patternCompile4 = Pattern.compile("(\\s+|//[^\\n]*\\n)+");
        Pattern patternCompile5 = Pattern.compile("[a-zA-Z\\.]+");
        Pattern patternCompile6 = Pattern.compile("[a-zA-Z\\./:]+");
        Pattern patternCompile7 = Pattern.compile("\\[[a-zA-Z0-9\\-_]+\\]");
        Pattern patternCompile8 = Pattern.compile("=>");
        String str2 = ";";
        Pattern patternCompile9 = Pattern.compile(";");
        Pattern patternCompile10 = Pattern.compile("[a-zA-Z0-9\\-_]+");
        PatternScanner patternScanner2 = new PatternScanner(str, patternCompile4);
        String str3 = null;
        String strEat2 = null;
        String strSubstring = null;
        String strEat3 = null;
        char c2 = 0;
        while (true) {
            Pattern pattern3 = patternCompile;
            if (!patternScanner2.atEnd()) {
                switch (c2) {
                    case 0:
                        patternCompile9 = patternCompile9;
                        str2 = str2;
                        PatternScanner patternScanner3 = patternScanner2;
                        strEat = str3;
                        pattern = patternCompile6;
                        patternScanner = patternScanner3;
                        Pattern pattern4 = patternCompile5;
                        patternCompile10 = patternCompile10;
                        pattern2 = pattern4;
                        pattern3 = pattern3;
                        String strEat4 = patternScanner.eat(pattern3, "<command>");
                        if (strEat4.equals("@import")) {
                            c2 = 1;
                        } else if (strEat4.equals("@library")) {
                            c2 = 2;
                        } else if (strEat4.equals("@filter")) {
                            c2 = 3;
                        } else if (strEat4.equals("@connect")) {
                            c2 = '\b';
                        } else if (strEat4.equals("@set")) {
                            c2 = '\r';
                        } else if (strEat4.equals("@external")) {
                            c2 = 14;
                        } else {
                            if (!strEat4.equals("@setting")) {
                                throw new GraphIOException("Unknown command '" + strEat4 + "'!");
                            }
                            c2 = 15;
                        }
                        patternCompile = pattern3;
                        str2 = str2;
                        patternCompile9 = patternCompile9;
                        PatternScanner patternScanner4 = patternScanner;
                        patternCompile6 = pattern;
                        str3 = strEat;
                        patternScanner2 = patternScanner4;
                        Pattern pattern5 = patternCompile10;
                        patternCompile5 = pattern2;
                        patternCompile10 = pattern5;
                        break;
                    case 1:
                        PatternScanner patternScanner5 = patternScanner2;
                        strEat = str3;
                        pattern = patternCompile6;
                        patternScanner = patternScanner5;
                        pattern2 = patternCompile5;
                        this.mCommands.add(new ImportPackageCommand(patternScanner.eat(pattern2, "<package-name>")));
                        c2 = 16;
                        patternCompile = pattern3;
                        str2 = str2;
                        patternCompile9 = patternCompile9;
                        PatternScanner patternScanner6 = patternScanner;
                        patternCompile6 = pattern;
                        str3 = strEat;
                        patternScanner2 = patternScanner6;
                        Pattern pattern6 = patternCompile10;
                        patternCompile5 = pattern2;
                        patternCompile10 = pattern6;
                        break;
                    case 2:
                        Pattern pattern7 = patternCompile6;
                        patternScanner = patternScanner2;
                        strEat = str3;
                        pattern = pattern7;
                        this.mCommands.add(new AddLibraryCommand(patternScanner.eat(pattern, "<library-name>")));
                        pattern2 = patternCompile5;
                        c2 = 16;
                        patternCompile = pattern3;
                        str2 = str2;
                        patternCompile9 = patternCompile9;
                        PatternScanner patternScanner7 = patternScanner;
                        patternCompile6 = pattern;
                        str3 = strEat;
                        patternScanner2 = patternScanner7;
                        Pattern pattern8 = patternCompile10;
                        patternCompile5 = pattern2;
                        patternCompile10 = pattern8;
                        break;
                    case 3:
                        patternScanner = patternScanner2;
                        patternCompile10 = patternCompile10;
                        strEat = patternScanner.eat(patternCompile10, "<class-name>");
                        c2 = 4;
                        pattern3 = pattern3;
                        pattern2 = patternCompile5;
                        pattern = patternCompile6;
                        patternCompile = pattern3;
                        str2 = str2;
                        patternCompile9 = patternCompile9;
                        PatternScanner patternScanner8 = patternScanner;
                        patternCompile6 = pattern;
                        str3 = strEat;
                        patternScanner2 = patternScanner8;
                        Pattern pattern9 = patternCompile10;
                        patternCompile5 = pattern2;
                        patternCompile10 = pattern9;
                        break;
                    case 4:
                        patternScanner = patternScanner2;
                        patternCompile10 = patternCompile10;
                        strEat = str3;
                        this.mCommands.add(new AllocateFilterCommand(strEat, patternScanner.eat(patternCompile10, "<filter-name>")));
                        c2 = 5;
                        pattern3 = pattern3;
                        pattern2 = patternCompile5;
                        pattern = patternCompile6;
                        patternCompile = pattern3;
                        str2 = str2;
                        patternCompile9 = patternCompile9;
                        PatternScanner patternScanner9 = patternScanner;
                        patternCompile6 = pattern;
                        str3 = strEat;
                        patternScanner2 = patternScanner9;
                        Pattern pattern10 = patternCompile10;
                        patternCompile5 = pattern2;
                        patternCompile10 = pattern10;
                        break;
                    case 5:
                        str3 = str3;
                        patternCompile9 = patternCompile9;
                        patternCompile5 = patternCompile5;
                        patternCompile6 = patternCompile6;
                        str2 = str2;
                        patternScanner = patternScanner2;
                        patternCompile10 = patternCompile10;
                        patternScanner.eat(patternCompile3, "{");
                        c2 = 6;
                        pattern3 = pattern3;
                        pattern2 = patternCompile5;
                        pattern = patternCompile6;
                        strEat = str3;
                        patternCompile = pattern3;
                        str2 = str2;
                        patternCompile9 = patternCompile9;
                        PatternScanner patternScanner10 = patternScanner;
                        patternCompile6 = pattern;
                        str3 = strEat;
                        patternScanner2 = patternScanner10;
                        Pattern pattern11 = patternCompile10;
                        patternCompile5 = pattern2;
                        patternCompile10 = pattern11;
                        break;
                    case 6:
                        str3 = str3;
                        patternCompile9 = patternCompile9;
                        patternCompile5 = patternCompile5;
                        patternCompile6 = patternCompile6;
                        str2 = str2;
                        patternScanner = patternScanner2;
                        patternCompile10 = patternCompile10;
                        this.mCommands.add(new InitFilterCommand(readKeyValueAssignments(patternScanner, patternCompile2)));
                        c2 = 7;
                        pattern3 = pattern3;
                        pattern2 = patternCompile5;
                        pattern = patternCompile6;
                        strEat = str3;
                        patternCompile = pattern3;
                        str2 = str2;
                        patternCompile9 = patternCompile9;
                        PatternScanner patternScanner11 = patternScanner;
                        patternCompile6 = pattern;
                        str3 = strEat;
                        patternScanner2 = patternScanner11;
                        Pattern pattern12 = patternCompile10;
                        patternCompile5 = pattern2;
                        patternCompile10 = pattern12;
                        break;
                    case 7:
                        str3 = str3;
                        patternCompile9 = patternCompile9;
                        patternCompile5 = patternCompile5;
                        patternCompile6 = patternCompile6;
                        str2 = str2;
                        patternScanner = patternScanner2;
                        patternCompile10 = patternCompile10;
                        patternScanner.eat(patternCompile2, "}");
                        c2 = 0;
                        pattern3 = pattern3;
                        pattern2 = patternCompile5;
                        pattern = patternCompile6;
                        strEat = str3;
                        patternCompile = pattern3;
                        str2 = str2;
                        patternCompile9 = patternCompile9;
                        PatternScanner patternScanner12 = patternScanner;
                        patternCompile6 = pattern;
                        str3 = strEat;
                        patternScanner2 = patternScanner12;
                        Pattern pattern13 = patternCompile10;
                        patternCompile5 = pattern2;
                        patternCompile10 = pattern13;
                        break;
                    case '\b':
                        patternScanner = patternScanner2;
                        patternCompile10 = patternCompile10;
                        c = '\t';
                        strEat2 = patternScanner.eat(patternCompile10, "<source-filter-name>");
                        c2 = c;
                        pattern3 = pattern3;
                        pattern2 = patternCompile5;
                        pattern = patternCompile6;
                        strEat = str3;
                        patternCompile = pattern3;
                        str2 = str2;
                        patternCompile9 = patternCompile9;
                        PatternScanner patternScanner13 = patternScanner;
                        patternCompile6 = pattern;
                        str3 = strEat;
                        patternScanner2 = patternScanner13;
                        Pattern pattern14 = patternCompile10;
                        patternCompile5 = pattern2;
                        patternCompile10 = pattern14;
                        break;
                    case '\t':
                        patternScanner = patternScanner2;
                        patternCompile10 = patternCompile10;
                        String strEat5 = patternScanner.eat(patternCompile7, "[<source-port-name>]");
                        c = '\n';
                        strSubstring = strEat5.substring(1, strEat5.length() - 1);
                        c2 = c;
                        pattern3 = pattern3;
                        pattern2 = patternCompile5;
                        pattern = patternCompile6;
                        strEat = str3;
                        patternCompile = pattern3;
                        str2 = str2;
                        patternCompile9 = patternCompile9;
                        PatternScanner patternScanner14 = patternScanner;
                        patternCompile6 = pattern;
                        str3 = strEat;
                        patternScanner2 = patternScanner14;
                        Pattern pattern15 = patternCompile10;
                        patternCompile5 = pattern2;
                        patternCompile10 = pattern15;
                        break;
                    case '\n':
                        str3 = str3;
                        patternCompile9 = patternCompile9;
                        patternCompile5 = patternCompile5;
                        patternCompile6 = patternCompile6;
                        str2 = str2;
                        patternScanner = patternScanner2;
                        patternCompile10 = patternCompile10;
                        patternScanner.eat(patternCompile8, "=>");
                        c2 = 11;
                        pattern3 = pattern3;
                        pattern2 = patternCompile5;
                        pattern = patternCompile6;
                        strEat = str3;
                        patternCompile = pattern3;
                        str2 = str2;
                        patternCompile9 = patternCompile9;
                        PatternScanner patternScanner15 = patternScanner;
                        patternCompile6 = pattern;
                        str3 = strEat;
                        patternScanner2 = patternScanner15;
                        Pattern pattern16 = patternCompile10;
                        patternCompile5 = pattern2;
                        patternCompile10 = pattern16;
                        break;
                    case 11:
                        patternScanner = patternScanner2;
                        patternCompile10 = patternCompile10;
                        c = '\f';
                        strEat3 = patternScanner.eat(patternCompile10, "<target-filter-name>");
                        c2 = c;
                        pattern3 = pattern3;
                        pattern2 = patternCompile5;
                        pattern = patternCompile6;
                        strEat = str3;
                        patternCompile = pattern3;
                        str2 = str2;
                        patternCompile9 = patternCompile9;
                        PatternScanner patternScanner16 = patternScanner;
                        patternCompile6 = pattern;
                        str3 = strEat;
                        patternScanner2 = patternScanner16;
                        Pattern pattern17 = patternCompile10;
                        patternCompile5 = pattern2;
                        patternCompile10 = pattern17;
                        break;
                    case '\f':
                        String strEat6 = patternScanner2.eat(patternCompile7, "[<target-port-name>]");
                        Pattern pattern18 = patternCompile5;
                        String str4 = str3;
                        Pattern pattern19 = patternCompile6;
                        patternScanner = patternScanner2;
                        this.mCommands.add(new ConnectCommand(strEat2, strSubstring, strEat3, strEat6.substring(1, strEat6.length() - 1)));
                        pattern2 = pattern18;
                        pattern = pattern19;
                        strEat = str4;
                        c2 = 16;
                        patternCompile = pattern3;
                        str2 = str2;
                        patternCompile9 = patternCompile9;
                        PatternScanner patternScanner17 = patternScanner;
                        patternCompile6 = pattern;
                        str3 = strEat;
                        patternScanner2 = patternScanner17;
                        Pattern pattern110 = patternCompile10;
                        patternCompile5 = pattern2;
                        patternCompile10 = pattern110;
                        break;
                    case '\r':
                        this.mBoundReferences.putAll(readKeyValueAssignments(patternScanner2, patternCompile9));
                        patternCompile9 = patternCompile9;
                        str2 = str2;
                        c2 = 16;
                        PatternScanner patternScanner18 = patternScanner2;
                        strEat = str3;
                        pattern = patternCompile6;
                        patternScanner = patternScanner18;
                        Pattern pattern20 = patternCompile5;
                        patternCompile10 = patternCompile10;
                        pattern2 = pattern20;
                        patternCompile = pattern3;
                        str2 = str2;
                        patternCompile9 = patternCompile9;
                        PatternScanner patternScanner19 = patternScanner;
                        patternCompile6 = pattern;
                        str3 = strEat;
                        patternScanner2 = patternScanner19;
                        Pattern pattern111 = patternCompile10;
                        patternCompile5 = pattern2;
                        patternCompile10 = pattern111;
                        break;
                    case 14:
                        bindExternal(patternScanner2.eat(patternCompile10, "<external-identifier>"));
                        patternCompile9 = patternCompile9;
                        str2 = str2;
                        c2 = 16;
                        PatternScanner patternScanner110 = patternScanner2;
                        strEat = str3;
                        pattern = patternCompile6;
                        patternScanner = patternScanner110;
                        Pattern pattern21 = patternCompile5;
                        patternCompile10 = patternCompile10;
                        pattern2 = pattern21;
                        patternCompile = pattern3;
                        str2 = str2;
                        patternCompile9 = patternCompile9;
                        PatternScanner patternScanner111 = patternScanner;
                        patternCompile6 = pattern;
                        str3 = strEat;
                        patternScanner2 = patternScanner111;
                        Pattern pattern112 = patternCompile10;
                        patternCompile5 = pattern2;
                        patternCompile10 = pattern112;
                        break;
                    case 15:
                        this.mSettings.putAll(readKeyValueAssignments(patternScanner2, patternCompile9));
                        patternCompile9 = patternCompile9;
                        str2 = str2;
                        c2 = 16;
                        PatternScanner patternScanner112 = patternScanner2;
                        strEat = str3;
                        pattern = patternCompile6;
                        patternScanner = patternScanner112;
                        Pattern pattern22 = patternCompile5;
                        patternCompile10 = patternCompile10;
                        pattern2 = pattern22;
                        patternCompile = pattern3;
                        str2 = str2;
                        patternCompile9 = patternCompile9;
                        PatternScanner patternScanner113 = patternScanner;
                        patternCompile6 = pattern;
                        str3 = strEat;
                        patternScanner2 = patternScanner113;
                        Pattern pattern113 = patternCompile10;
                        patternCompile5 = pattern2;
                        patternCompile10 = pattern113;
                        break;
                    case 16:
                        patternScanner2.eat(patternCompile9, str2);
                        c2 = 0;
                        PatternScanner patternScanner114 = patternScanner2;
                        strEat = str3;
                        pattern = patternCompile6;
                        patternScanner = patternScanner114;
                        Pattern pattern23 = patternCompile5;
                        patternCompile10 = patternCompile10;
                        pattern2 = pattern23;
                        patternCompile = pattern3;
                        str2 = str2;
                        patternCompile9 = patternCompile9;
                        PatternScanner patternScanner115 = patternScanner;
                        patternCompile6 = pattern;
                        str3 = strEat;
                        patternScanner2 = patternScanner115;
                        Pattern pattern114 = patternCompile10;
                        patternCompile5 = pattern2;
                        patternCompile10 = pattern114;
                        break;
                    default:
                        PatternScanner patternScanner116 = patternScanner2;
                        strEat = str3;
                        pattern = patternCompile6;
                        patternScanner = patternScanner116;
                        Pattern pattern24 = patternCompile5;
                        patternCompile10 = patternCompile10;
                        pattern2 = pattern24;
                        patternCompile = pattern3;
                        str2 = str2;
                        patternCompile9 = patternCompile9;
                        PatternScanner patternScanner117 = patternScanner;
                        patternCompile6 = pattern;
                        str3 = strEat;
                        patternScanner2 = patternScanner117;
                        Pattern pattern115 = patternCompile10;
                        patternCompile5 = pattern2;
                        patternCompile10 = pattern115;
                        break;
                }
            } else {
                if (c2 != 16 && c2 != 0) {
                    throw new GraphIOException("Unexpected end of input!");
                }
                return;
            }
        }
    }

    @Override // android.filterfw.io.GraphReader
    public KeyValueMap readKeyValueAssignments(String str) throws GraphIOException {
        return readKeyValueAssignments(new PatternScanner(str, Pattern.compile("\\s+")), null);
    }

    private KeyValueMap readKeyValueAssignments(PatternScanner patternScanner, Pattern pattern) throws GraphIOException {
        Pattern patternCompile = Pattern.compile("=");
        Pattern patternCompile2 = Pattern.compile(";");
        Pattern patternCompile3 = Pattern.compile("[a-zA-Z]+[a-zA-Z0-9]*");
        Pattern patternCompile4 = Pattern.compile("'[^']*'|\\\"[^\\\"]*\\\"");
        Pattern patternCompile5 = Pattern.compile("[0-9]+");
        Pattern patternCompile6 = Pattern.compile("[0-9]*\\.[0-9]+f?");
        Pattern patternCompile7 = Pattern.compile("\\$[a-zA-Z]+[a-zA-Z0-9]");
        Pattern patternCompile8 = Pattern.compile("true|false");
        KeyValueMap keyValueMap = new KeyValueMap();
        char c = 0;
        String strEat = null;
        while (!patternScanner.atEnd() && (pattern == null || !patternScanner.peek(pattern))) {
            char c2 = 2;
            if (c == 0) {
                strEat = patternScanner.eat(patternCompile3, "<identifier>");
                c2 = 1;
            } else if (c == 1) {
                patternScanner.eat(patternCompile, "=");
            } else if (c == 2) {
                String strTryEat = patternScanner.tryEat(patternCompile4);
                if (strTryEat != null) {
                    keyValueMap.put(strEat, strTryEat.substring(1, strTryEat.length() - 1));
                } else {
                    String strTryEat2 = patternScanner.tryEat(patternCompile7);
                    if (strTryEat2 != null) {
                        String strSubstring = strTryEat2.substring(1, strTryEat2.length());
                        KeyValueMap keyValueMap2 = this.mBoundReferences;
                        Object obj = keyValueMap2 != null ? keyValueMap2.get(strSubstring) : null;
                        if (obj == null) {
                            throw new GraphIOException("Unknown object reference to '" + strSubstring + "'!");
                        }
                        keyValueMap.put(strEat, obj);
                    } else {
                        String strTryEat3 = patternScanner.tryEat(patternCompile8);
                        if (strTryEat3 != null) {
                            keyValueMap.put(strEat, Boolean.valueOf(Boolean.parseBoolean(strTryEat3)));
                        } else {
                            String strTryEat4 = patternScanner.tryEat(patternCompile6);
                            if (strTryEat4 != null) {
                                keyValueMap.put(strEat, Float.valueOf(Float.parseFloat(strTryEat4)));
                            } else {
                                String strTryEat5 = patternScanner.tryEat(patternCompile5);
                                if (strTryEat5 != null) {
                                    keyValueMap.put(strEat, Integer.valueOf(Integer.parseInt(strTryEat5)));
                                } else {
                                    throw new GraphIOException(patternScanner.unexpectedTokenMessage("<value>"));
                                }
                            }
                        }
                    }
                }
                c2 = 3;
            } else if (c != 3) {
                c2 = c;
            } else {
                patternScanner.eat(patternCompile2, ";");
                c2 = 0;
            }
            c = c2;
        }
        if (c == 0 || c == 3) {
            return keyValueMap;
        }
        throw new GraphIOException("Unexpected end of assignments on line " + patternScanner.lineNo() + "!");
    }

    private void bindExternal(String str) throws GraphIOException {
        if (this.mReferences.containsKey(str)) {
            this.mBoundReferences.put(str, this.mReferences.get(str));
        } else {
            throw new GraphIOException("Unknown external variable '" + str + "'! You must add a reference to this external in the host program using addReference(...)!");
        }
    }

    private void checkReferences() throws GraphIOException {
        for (String str : this.mReferences.keySet()) {
            if (!this.mBoundReferences.containsKey(str)) {
                throw new GraphIOException("Host program specifies reference to '" + str + "', which is not declared @external in graph file!");
            }
        }
    }

    private void applySettings() throws GraphIOException {
        for (String str : this.mSettings.keySet()) {
            Object obj = this.mSettings.get(str);
            if (str.equals("autoBranch")) {
                expectSettingClass(str, obj, String.class);
                if (obj.equals("synced")) {
                    this.mCurrentGraph.setAutoBranchMode(1);
                } else if (obj.equals("unsynced")) {
                    this.mCurrentGraph.setAutoBranchMode(2);
                } else if (obj.equals("off")) {
                    this.mCurrentGraph.setAutoBranchMode(0);
                } else {
                    throw new GraphIOException("Unknown autobranch setting: " + obj + "!");
                }
            } else if (str.equals("discardUnconnectedOutputs")) {
                expectSettingClass(str, obj, Boolean.class);
                this.mCurrentGraph.setDiscardUnconnectedOutputs(((Boolean) obj).booleanValue());
            } else {
                throw new GraphIOException("Unknown @setting '" + str + "'!");
            }
        }
    }

    private void expectSettingClass(String str, Object obj, Class cls) throws GraphIOException {
        if (obj.getClass() == cls) {
            return;
        }
        throw new GraphIOException("Setting '" + str + "' must have a value of type " + cls.getSimpleName() + ", but found a value of type " + obj.getClass().getSimpleName() + "!");
    }

    private void executeCommands() throws GraphIOException {
        Iterator<Command> it = this.mCommands.iterator();
        while (it.hasNext()) {
            it.next().execute(this);
        }
    }
}
