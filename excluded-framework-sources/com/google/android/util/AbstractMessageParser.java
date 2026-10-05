package com.google.android.util;

import android.app.backup.FullBackup;
import android.media.TtmlUtils;
import android.provider.Telephony;
import android.view.ThreadedRenderer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes3.dex */
public abstract class AbstractMessageParser {
    public static final String musicNote = "♫ ";
    private HashMap<Character, Format> formatStart;
    private int nextChar;
    private int nextClass;
    private boolean parseAcronyms;
    private boolean parseFormatting;
    private boolean parseMeText;
    private boolean parseMusic;
    private boolean parseSmilies;
    private boolean parseUrls;
    private ArrayList<Part> parts;
    private String text;
    private ArrayList<Token> tokens;

    public interface Resources {
        TrieNode getAcronyms();

        TrieNode getDomainSuffixes();

        Set<String> getSchemes();

        TrieNode getSmileys();
    }

    private static boolean isFormatChar(char c) {
        return c == '*' || c == '^' || c == '_';
    }

    private static boolean isPunctuation(char c) {
        return c == '!' || c == '\"' || c == '(' || c == ')' || c == ',' || c == '.' || c == '?' || c == ':' || c == ';';
    }

    private static boolean isSmileyBreak(char c, char c2) {
        if (c != '$' && c != '&' && c != '-' && c != '/' && c != '@' && c != '*' && c != '+') {
            switch (c) {
                case '<':
                case '=':
                case '>':
                    break;
                default:
                    switch (c) {
                        case '[':
                        case '\\':
                        case ']':
                        case '^':
                            break;
                        default:
                            switch (c) {
                                case '|':
                                case '}':
                                case '~':
                                    break;
                                default:
                                    return false;
                            }
                            break;
                    }
                    break;
            }
        }
        if (c2 == '*' || c2 == '/' || c2 == '@' || c2 == '^' || c2 == '~' || c2 == '[' || c2 == '\\') {
            return true;
        }
        switch (c2) {
            case '#':
            case '$':
            case '%':
                return true;
            default:
                switch (c2) {
                    case '<':
                    case '=':
                    case '>':
                        return true;
                    default:
                        return false;
                }
        }
    }

    protected abstract Resources getResources();

    public AbstractMessageParser(String str) {
        this(str, true, true, true, true, true, true);
    }

    public AbstractMessageParser(String str, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6) {
        this.text = str;
        this.nextChar = 0;
        this.nextClass = 10;
        this.parts = new ArrayList<>();
        this.tokens = new ArrayList<>();
        this.formatStart = new HashMap<>();
        this.parseSmilies = z;
        this.parseAcronyms = z2;
        this.parseFormatting = z3;
        this.parseUrls = z4;
        this.parseMusic = z5;
        this.parseMeText = z6;
    }

    public final String getRawText() {
        return this.text;
    }

    public final int getPartCount() {
        return this.parts.size();
    }

    public final Part getPart(int i) {
        return this.parts.get(i);
    }

    public final List<Part> getParts() {
        return this.parts;
    }

    public void parse() {
        boolean z;
        String str = null;
        if (parseMusicTrack()) {
            buildParts(null);
            return;
        }
        int i = 0;
        if (this.parseMeText && this.text.startsWith("/me") && this.text.length() > 3 && Character.isWhitespace(this.text.charAt(3))) {
            String strSubstring = this.text.substring(0, 4);
            this.text = this.text.substring(4);
            str = strSubstring;
        }
        while (true) {
            while (true) {
                if (this.nextChar < this.text.length()) {
                    if (!isWordBreak(this.nextChar) && (!z || !isSmileyBreak(this.nextChar))) {
                        throw new AssertionError("last chunk did not end at word break");
                    }
                    z = parseSmiley();
                } else {
                    for (int i2 = 0; i2 < this.tokens.size(); i2++) {
                        if (this.tokens.get(i2).isMedia()) {
                            if (i2 > 0) {
                                int i3 = i2 - 1;
                                if (this.tokens.get(i3) instanceof Html) {
                                    ((Html) this.tokens.get(i3)).trimLeadingWhitespace();
                                }
                            }
                            int i4 = i2 + 1;
                            if (i4 < this.tokens.size() && (this.tokens.get(i4) instanceof Html)) {
                                ((Html) this.tokens.get(i4)).trimTrailingWhitespace();
                            }
                        }
                    }
                    while (i < this.tokens.size()) {
                        if (this.tokens.get(i).isHtml() && this.tokens.get(i).toHtml(true).length() == 0) {
                            this.tokens.remove(i);
                            i--;
                        }
                        i++;
                    }
                    buildParts(str);
                    return;
                }
            }
            if (!parseAcronym() && !parseURL() && !parseFormatting()) {
                parseText();
            }
        }
    }

    public static Token tokenForUrl(String str, String str2) {
        if (str == null) {
            return null;
        }
        Video videoMatchURL = Video.matchURL(str, str2);
        if (videoMatchURL != null) {
            return videoMatchURL;
        }
        YouTubeVideo youTubeVideoMatchURL = YouTubeVideo.matchURL(str, str2);
        if (youTubeVideoMatchURL != null) {
            return youTubeVideoMatchURL;
        }
        Photo photoMatchURL = Photo.matchURL(str, str2);
        if (photoMatchURL != null) {
            return photoMatchURL;
        }
        FlickrPhoto flickrPhotoMatchURL = FlickrPhoto.matchURL(str, str2);
        return flickrPhotoMatchURL != null ? flickrPhotoMatchURL : new Link(str, str2);
    }

    private void buildParts(String str) {
        for (int i = 0; i < this.tokens.size(); i++) {
            Token token = this.tokens.get(i);
            if (token.isMedia() || this.parts.size() == 0 || lastPart().isMedia()) {
                this.parts.add(new Part());
            }
            lastPart().add(token);
        }
        if (this.parts.size() > 0) {
            this.parts.get(0).setMeText(str);
        }
    }

    private Part lastPart() {
        ArrayList<Part> arrayList = this.parts;
        return arrayList.get(arrayList.size() - 1);
    }

    private boolean parseMusicTrack() {
        if (!this.parseMusic || !this.text.startsWith(musicNote)) {
            return false;
        }
        addToken(new MusicTrack(this.text.substring(2)));
        this.nextChar = this.text.length();
        return true;
    }

    private void parseText() {
        StringBuilder sb = new StringBuilder();
        int i = this.nextChar;
        do {
            String str = this.text;
            int i2 = this.nextChar;
            this.nextChar = i2 + 1;
            char cCharAt = str.charAt(i2);
            if (cCharAt == '\n') {
                sb.append("<br>");
            } else if (cCharAt == '\"') {
                sb.append("&quot;");
            } else if (cCharAt == '<') {
                sb.append("&lt;");
            } else if (cCharAt == '>') {
                sb.append("&gt;");
            } else if (cCharAt == '&') {
                sb.append("&amp;");
            } else if (cCharAt == '\'') {
                sb.append("&apos;");
            } else {
                sb.append(cCharAt);
            }
        } while (!isWordBreak(this.nextChar));
        addToken(new Html(this.text.substring(i, this.nextChar), sb.toString()));
    }

    private boolean parseSmiley() {
        TrieNode trieNodeLongestMatch;
        if (!this.parseSmilies || (trieNodeLongestMatch = longestMatch(getResources().getSmileys(), this, this.nextChar, true)) == null) {
            return false;
        }
        int charClass = getCharClass(this.nextChar - 1);
        int charClass2 = getCharClass(this.nextChar + trieNodeLongestMatch.getText().length());
        if ((charClass == 2 || charClass == 3) && (charClass2 == 2 || charClass2 == 3)) {
            return false;
        }
        addToken(new Smiley(trieNodeLongestMatch.getText()));
        this.nextChar += trieNodeLongestMatch.getText().length();
        return true;
    }

    private boolean parseAcronym() {
        TrieNode trieNodeLongestMatch;
        if (!this.parseAcronyms || (trieNodeLongestMatch = longestMatch(getResources().getAcronyms(), this, this.nextChar)) == null) {
            return false;
        }
        addToken(new Acronym(trieNodeLongestMatch.getText(), trieNodeLongestMatch.getValue()));
        this.nextChar += trieNodeLongestMatch.getText().length();
        return true;
    }

    private boolean isDomainChar(char c) {
        return c == '-' || Character.isLetter(c) || Character.isDigit(c);
    }

    private boolean isValidDomain(String str) {
        return matches(getResources().getDomainSuffixes(), reverse(str));
    }

    /* JADX WARN: Code duplicated, block: B:49:0x00c9  */
    private boolean parseURL() {
        String str;
        char cCharAt;
        boolean z = false;
        if (this.parseUrls && isURLBreak(this.nextChar)) {
            int i = this.nextChar;
            int i2 = i;
            while (i2 < this.text.length() && isDomainChar(this.text.charAt(i2))) {
                i2++;
            }
            if (i2 == this.text.length()) {
                return false;
            }
            if (this.text.charAt(i2) == ':') {
                if (!getResources().getSchemes().contains(this.text.substring(this.nextChar, i2))) {
                    return false;
                }
                str = "";
            } else if (this.text.charAt(i2) == '.') {
                while (i2 < this.text.length() && ((cCharAt = this.text.charAt(i2)) == '.' || isDomainChar(cCharAt))) {
                    i2++;
                }
                if (!isValidDomain(this.text.substring(this.nextChar, i2))) {
                    return false;
                }
                int i3 = i2 + 1;
                if (i3 < this.text.length() && this.text.charAt(i2) == ':' && Character.isDigit(this.text.charAt(i3))) {
                    while (i3 < this.text.length() && Character.isDigit(this.text.charAt(i3))) {
                        i3++;
                    }
                    i2 = i3;
                }
                if (i2 != this.text.length()) {
                    char cCharAt2 = this.text.charAt(i2);
                    if (cCharAt2 == '?') {
                        int i4 = i2 + 1;
                        if (i4 == this.text.length()) {
                            z = true;
                        } else {
                            char cCharAt3 = this.text.charAt(i4);
                            if (Character.isWhitespace(cCharAt3) || isPunctuation(cCharAt3)) {
                                z = true;
                            }
                        }
                    } else if (isPunctuation(cCharAt2) || Character.isWhitespace(cCharAt2)) {
                        z = true;
                    } else if (cCharAt2 != '/' && cCharAt2 != '#') {
                        return false;
                    }
                } else {
                    z = true;
                }
                str = "http://";
            }
            if (!z) {
                while (i2 < this.text.length() && !Character.isWhitespace(this.text.charAt(i2))) {
                    i2++;
                }
            }
            String strSubstring = this.text.substring(i, i2);
            addURLToken(str + strSubstring, strSubstring);
            this.nextChar = i2;
            return true;
        }
        return false;
    }

    private void addURLToken(String str, String str2) {
        addToken(tokenForUrl(str, str2));
    }

    private boolean parseFormatting() {
        if (!this.parseFormatting) {
            return false;
        }
        int i = this.nextChar;
        while (i < this.text.length() && isFormatChar(this.text.charAt(i))) {
            i++;
        }
        if (i == this.nextChar || !isWordBreak(i)) {
            return false;
        }
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (int i2 = this.nextChar; i2 < i; i2++) {
            char cCharAt = this.text.charAt(i2);
            Character chValueOf = Character.valueOf(cCharAt);
            if (linkedHashMap.containsKey(chValueOf)) {
                addToken(new Format(cCharAt, false));
            } else {
                Format format = this.formatStart.get(chValueOf);
                if (format != null) {
                    format.setMatched(true);
                    this.formatStart.remove(chValueOf);
                    linkedHashMap.put(chValueOf, Boolean.TRUE);
                } else {
                    Format format2 = new Format(cCharAt, true);
                    this.formatStart.put(chValueOf, format2);
                    addToken(format2);
                    linkedHashMap.put(chValueOf, Boolean.FALSE);
                }
            }
        }
        for (Character ch : linkedHashMap.keySet()) {
            if (linkedHashMap.get(ch) == Boolean.TRUE) {
                Format format3 = new Format(ch.charValue(), false);
                format3.setMatched(true);
                addToken(format3);
            }
        }
        this.nextChar = i;
        return true;
    }

    private boolean isWordBreak(int i) {
        return getCharClass(i + (-1)) != getCharClass(i);
    }

    private boolean isSmileyBreak(int i) {
        return i > 0 && i < this.text.length() && isSmileyBreak(this.text.charAt(i + (-1)), this.text.charAt(i));
    }

    private boolean isURLBreak(int i) {
        int charClass = getCharClass(i - 1);
        return (charClass == 2 || charClass == 3 || charClass == 4) ? false : true;
    }

    private int getCharClass(int i) {
        if (i < 0 || this.text.length() <= i) {
            return 0;
        }
        char cCharAt = this.text.charAt(i);
        if (Character.isWhitespace(cCharAt)) {
            return 1;
        }
        if (Character.isLetter(cCharAt)) {
            return 2;
        }
        if (Character.isDigit(cCharAt)) {
            return 3;
        }
        if (!isPunctuation(cCharAt)) {
            return 4;
        }
        int i2 = this.nextClass + 1;
        this.nextClass = i2;
        return i2;
    }

    public static abstract class Token {
        protected String text;
        protected Type type;

        public boolean controlCaps() {
            return false;
        }

        public abstract boolean isHtml();

        public boolean isMedia() {
            return false;
        }

        public boolean setCaps() {
            return false;
        }

        public enum Type {
            HTML("html"),
            FORMAT("format"),
            LINK("l"),
            SMILEY("e"),
            ACRONYM(FullBackup.APK_TREE_TOKEN),
            MUSIC("m"),
            GOOGLE_VIDEO(Telephony.BaseMmsColumns.MMS_VERSION),
            YOUTUBE_VIDEO("yt"),
            PHOTO(TtmlUtils.TAG_P),
            FLICKR(FullBackup.FILES_TREE_TOKEN);

            private String stringRep;

            Type(String str) {
                this.stringRep = str;
            }

            @Override // java.lang.Enum
            public String toString() {
                return this.stringRep;
            }
        }

        protected Token(Type type, String str) {
            this.type = type;
            this.text = str;
        }

        public Type getType() {
            return this.type;
        }

        public List<String> getInfo() {
            ArrayList arrayList = new ArrayList();
            arrayList.add(getType().toString());
            return arrayList;
        }

        public String getRawText() {
            return this.text;
        }

        public boolean isArray() {
            return !isHtml();
        }

        public String toHtml(boolean z) {
            throw new AssertionError("not html");
        }
    }

    public static class Html extends Token {
        private String html;

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean isHtml() {
            return true;
        }

        public Html(String str, String str2) {
            super(Token.Type.HTML, str);
            this.html = str2;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public String toHtml(boolean z) {
            return z ? this.html.toUpperCase() : this.html;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public List<String> getInfo() {
            throw new UnsupportedOperationException();
        }

        public void trimLeadingWhitespace() {
            this.text = trimLeadingWhitespace(this.text);
            this.html = trimLeadingWhitespace(this.html);
        }

        public void trimTrailingWhitespace() {
            this.text = trimTrailingWhitespace(this.text);
            this.html = trimTrailingWhitespace(this.html);
        }

        private static String trimLeadingWhitespace(String str) {
            int i = 0;
            while (i < str.length() && Character.isWhitespace(str.charAt(i))) {
                i++;
            }
            return str.substring(i);
        }

        public static String trimTrailingWhitespace(String str) {
            int length = str.length();
            while (length > 0 && Character.isWhitespace(str.charAt(length - 1))) {
                length--;
            }
            return str.substring(0, length);
        }
    }

    public static class MusicTrack extends Token {
        private String track;

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean isHtml() {
            return false;
        }

        public MusicTrack(String str) {
            super(Token.Type.MUSIC, str);
            this.track = str;
        }

        public String getTrack() {
            return this.track;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add(getTrack());
            return info;
        }
    }

    public static class Link extends Token {
        private String url;

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean isHtml() {
            return false;
        }

        public Link(String str, String str2) {
            super(Token.Type.LINK, str2);
            this.url = str;
        }

        public String getURL() {
            return this.url;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add(getURL());
            info.add(getRawText());
            return info;
        }
    }

    public static class Video extends Token {
        private static final Pattern URL_PATTERN = Pattern.compile("(?i)http://video\\.google\\.[a-z0-9]+(?:\\.[a-z0-9]+)?/videoplay\\?.*?\\bdocid=(-?\\d+).*");
        private String docid;

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean isHtml() {
            return false;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean isMedia() {
            return true;
        }

        public Video(String str, String str2) {
            super(Token.Type.GOOGLE_VIDEO, str2);
            this.docid = str;
        }

        public String getDocID() {
            return this.docid;
        }

        public static Video matchURL(String str, String str2) {
            Matcher matcher = URL_PATTERN.matcher(str);
            if (matcher.matches()) {
                return new Video(matcher.group(1), str2);
            }
            return null;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add(getRssUrl(this.docid));
            info.add(getURL(this.docid));
            return info;
        }

        public static String getRssUrl(String str) {
            return "http://video.google.com/videofeed?type=docid&output=rss&sourceid=gtalk&docid=" + str;
        }

        public static String getURL(String str) {
            return getURL(str, null);
        }

        public static String getURL(String str, String str2) {
            if (str2 == null) {
                str2 = "";
            } else if (str2.length() > 0) {
                str2 = str2 + "&";
            }
            return "http://video.google.com/videoplay?" + str2 + "docid=" + str;
        }
    }

    public static class YouTubeVideo extends Token {
        private static final Pattern URL_PATTERN = Pattern.compile("(?i)http://(?:[a-z0-9]+\\.)?youtube\\.[a-z0-9]+(?:\\.[a-z0-9]+)?/watch\\?.*\\bv=([-_a-zA-Z0-9=]+).*");
        private String docid;

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean isHtml() {
            return false;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean isMedia() {
            return true;
        }

        public YouTubeVideo(String str, String str2) {
            super(Token.Type.YOUTUBE_VIDEO, str2);
            this.docid = str;
        }

        public String getDocID() {
            return this.docid;
        }

        public static YouTubeVideo matchURL(String str, String str2) {
            Matcher matcher = URL_PATTERN.matcher(str);
            if (matcher.matches()) {
                return new YouTubeVideo(matcher.group(1), str2);
            }
            return null;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add(getRssUrl(this.docid));
            info.add(getURL(this.docid));
            return info;
        }

        public static String getRssUrl(String str) {
            return "http://youtube.com/watch?v=" + str;
        }

        public static String getURL(String str) {
            return getURL(str, null);
        }

        public static String getURL(String str, String str2) {
            if (str2 == null) {
                str2 = "";
            } else if (str2.length() > 0) {
                str2 = str2 + "&";
            }
            return "http://youtube.com/watch?" + str2 + "v=" + str;
        }

        public static String getPrefixedURL(boolean z, String str, String str2, String str3) {
            String str4 = z ? "http://" : "";
            if (str == null) {
                str = "";
            }
            if (str3 == null) {
                str3 = "";
            } else if (str3.length() > 0) {
                str3 = str3 + "&";
            }
            return str4 + str + "youtube.com/watch?" + str3 + "v=" + str2;
        }
    }

    public static class Photo extends Token {
        private static final Pattern URL_PATTERN = Pattern.compile("http://picasaweb.google.com/([^/?#&]+)/+((?!searchbrowse)[^/?#&]+)(?:/|/photo)?(?:\\?[^#]*)?(?:#(.*))?");
        private String album;
        private String photo;
        private String user;

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean isHtml() {
            return false;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean isMedia() {
            return true;
        }

        public Photo(String str, String str2, String str3, String str4) {
            super(Token.Type.PHOTO, str4);
            this.user = str;
            this.album = str2;
            this.photo = str3;
        }

        public String getUser() {
            return this.user;
        }

        public String getAlbum() {
            return this.album;
        }

        public String getPhoto() {
            return this.photo;
        }

        public static Photo matchURL(String str, String str2) {
            Matcher matcher = URL_PATTERN.matcher(str);
            if (matcher.matches()) {
                return new Photo(matcher.group(1), matcher.group(2), matcher.group(3), str2);
            }
            return null;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add(getRssUrl(getUser()));
            info.add(getAlbumURL(getUser(), getAlbum()));
            if (getPhoto() != null) {
                info.add(getPhotoURL(getUser(), getAlbum(), getPhoto()));
            } else {
                info.add((String) null);
            }
            return info;
        }

        public static String getRssUrl(String str) {
            return "http://picasaweb.google.com/data/feed/api/user/" + str + "?category=album&alt=rss";
        }

        public static String getAlbumURL(String str, String str2) {
            return "http://picasaweb.google.com/" + str + "/" + str2;
        }

        public static String getPhotoURL(String str, String str2, String str3) {
            return "http://picasaweb.google.com/" + str + "/" + str2 + "/photo#" + str3;
        }
    }

    public static class FlickrPhoto extends Token {
        private static final String SETS = "sets";
        private static final String TAGS = "tags";
        private String grouping;
        private String groupingId;
        private String photo;
        private String user;
        private static final Pattern URL_PATTERN = Pattern.compile("http://(?:www.)?flickr.com/photos/([^/?#&]+)/?([^/?#&]+)?/?.*");
        private static final Pattern GROUPING_PATTERN = Pattern.compile("http://(?:www.)?flickr.com/photos/([^/?#&]+)/(tags|sets)/([^/?#&]+)/?");

        public static String getRssUrl(String str) {
            return null;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean isHtml() {
            return false;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean isMedia() {
            return true;
        }

        public FlickrPhoto(String str, String str2, String str3, String str4, String str5) {
            super(Token.Type.FLICKR, str5);
            if (!"tags".equals(str)) {
                this.user = str;
                this.photo = ThreadedRenderer.OVERDRAW_PROPERTY_SHOW.equals(str2) ? null : str2;
                this.grouping = str3;
                this.groupingId = str4;
                return;
            }
            this.user = null;
            this.photo = null;
            this.grouping = "tags";
            this.groupingId = str2;
        }

        public String getUser() {
            return this.user;
        }

        public String getPhoto() {
            return this.photo;
        }

        public String getGrouping() {
            return this.grouping;
        }

        public String getGroupingId() {
            return this.groupingId;
        }

        public static FlickrPhoto matchURL(String str, String str2) {
            Matcher matcher = GROUPING_PATTERN.matcher(str);
            if (matcher.matches()) {
                return new FlickrPhoto(matcher.group(1), null, matcher.group(2), matcher.group(3), str2);
            }
            Matcher matcher2 = URL_PATTERN.matcher(str);
            if (matcher2.matches()) {
                return new FlickrPhoto(matcher2.group(1), matcher2.group(2), null, null, str2);
            }
            return null;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add(getUrl());
            info.add(getUser() != null ? getUser() : "");
            info.add(getPhoto() != null ? getPhoto() : "");
            info.add(getGrouping() != null ? getGrouping() : "");
            info.add(getGroupingId() != null ? getGroupingId() : "");
            return info;
        }

        public String getUrl() {
            if (SETS.equals(this.grouping)) {
                return getUserSetsURL(this.user, this.groupingId);
            }
            if ("tags".equals(this.grouping)) {
                String str = this.user;
                if (str != null) {
                    return getUserTagsURL(str, this.groupingId);
                }
                return getTagsURL(this.groupingId);
            }
            String str2 = this.photo;
            if (str2 != null) {
                return getPhotoURL(this.user, str2);
            }
            return getUserURL(this.user);
        }

        public static String getTagsURL(String str) {
            return "http://flickr.com/photos/tags/" + str;
        }

        public static String getUserURL(String str) {
            return "http://flickr.com/photos/" + str;
        }

        public static String getPhotoURL(String str, String str2) {
            return "http://flickr.com/photos/" + str + "/" + str2;
        }

        public static String getUserTagsURL(String str, String str2) {
            return "http://flickr.com/photos/" + str + "/tags/" + str2;
        }

        public static String getUserSetsURL(String str, String str2) {
            return "http://flickr.com/photos/" + str + "/sets/" + str2;
        }
    }

    public static class Smiley extends Token {
        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean isHtml() {
            return false;
        }

        public Smiley(String str) {
            super(Token.Type.SMILEY, str);
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add(getRawText());
            return info;
        }
    }

    public static class Acronym extends Token {
        private String value;

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean isHtml() {
            return false;
        }

        public Acronym(String str, String str2) {
            super(Token.Type.ACRONYM, str);
            this.value = str2;
        }

        public String getValue() {
            return this.value;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public List<String> getInfo() {
            List<String> info = super.getInfo();
            info.add(getRawText());
            info.add(getValue());
            return info;
        }
    }

    public static class Format extends Token {
        private char ch;
        private boolean matched;
        private boolean start;

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean isHtml() {
            return true;
        }

        public Format(char c, boolean z) {
            super(Token.Type.FORMAT, String.valueOf(c));
            this.ch = c;
            this.start = z;
        }

        public void setMatched(boolean z) {
            this.matched = z;
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public String toHtml(boolean z) {
            if (this.matched) {
                return this.start ? getFormatStart(this.ch) : getFormatEnd(this.ch);
            }
            char c = this.ch;
            return c == '\"' ? "&quot;" : String.valueOf(c);
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public List<String> getInfo() {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean controlCaps() {
            return this.ch == '^';
        }

        @Override // com.google.android.util.AbstractMessageParser.Token
        public boolean setCaps() {
            return this.start;
        }

        private String getFormatStart(char c) {
            if (c == '\"') {
                return "<font color=\"#999999\">“";
            }
            if (c == '*') {
                return "<b>";
            }
            if (c == '^') {
                return "<b><font color=\"#005FFF\">";
            }
            if (c == '_') {
                return "<i>";
            }
            throw new AssertionError("unknown format '" + c + "'");
        }

        private String getFormatEnd(char c) {
            if (c == '\"') {
                return "”</font>";
            }
            if (c == '*') {
                return "</b>";
            }
            if (c == '^') {
                return "</font></b>";
            }
            if (c == '_') {
                return "</i>";
            }
            throw new AssertionError("unknown format '" + c + "'");
        }
    }

    private void addToken(Token token) {
        this.tokens.add(token);
    }

    public String toHtml() {
        StringBuilder sb = new StringBuilder();
        for (Part part : this.parts) {
            boolean caps = false;
            sb.append("<p>");
            for (Token token : part.getTokens()) {
                if (token.isHtml()) {
                    sb.append(token.toHtml(caps));
                } else {
                    switch (AnonymousClass1.$SwitchMap$com$google$android$util$AbstractMessageParser$Token$Type[token.getType().ordinal()]) {
                        case 1:
                            sb.append("<a href=\"");
                            sb.append(((Link) token).getURL());
                            sb.append("\">");
                            sb.append(token.getRawText());
                            sb.append("</a>");
                            break;
                        case 2:
                            sb.append(token.getRawText());
                            break;
                        case 3:
                            sb.append(token.getRawText());
                            break;
                        case 4:
                            sb.append(((MusicTrack) token).getTrack());
                            break;
                        case 5:
                            sb.append("<a href=\"");
                            sb.append(Video.getURL(((Video) token).getDocID()));
                            sb.append("\">");
                            sb.append(token.getRawText());
                            sb.append("</a>");
                            break;
                        case 6:
                            sb.append("<a href=\"");
                            sb.append(YouTubeVideo.getURL(((YouTubeVideo) token).getDocID()));
                            sb.append("\">");
                            sb.append(token.getRawText());
                            sb.append("</a>");
                            break;
                        case 7:
                            sb.append("<a href=\"");
                            Photo photo = (Photo) token;
                            sb.append(Photo.getAlbumURL(photo.getUser(), photo.getAlbum()));
                            sb.append("\">");
                            sb.append(token.getRawText());
                            sb.append("</a>");
                            break;
                        case 8:
                            sb.append("<a href=\"");
                            sb.append(((FlickrPhoto) token).getUrl());
                            sb.append("\">");
                            sb.append(token.getRawText());
                            sb.append("</a>");
                            break;
                        default:
                            throw new AssertionError("unknown token type: " + token.getType());
                    }
                }
                if (token.controlCaps()) {
                    caps = token.setCaps();
                }
            }
            sb.append("</p>\n");
        }
        return sb.toString();
    }

    /* JADX INFO: renamed from: com.google.android.util.AbstractMessageParser$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$google$android$util$AbstractMessageParser$Token$Type;

        static {
            int[] iArr = new int[Token.Type.values().length];
            $SwitchMap$com$google$android$util$AbstractMessageParser$Token$Type = iArr;
            try {
                iArr[Token.Type.LINK.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$google$android$util$AbstractMessageParser$Token$Type[Token.Type.SMILEY.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$google$android$util$AbstractMessageParser$Token$Type[Token.Type.ACRONYM.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$google$android$util$AbstractMessageParser$Token$Type[Token.Type.MUSIC.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$google$android$util$AbstractMessageParser$Token$Type[Token.Type.GOOGLE_VIDEO.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$google$android$util$AbstractMessageParser$Token$Type[Token.Type.YOUTUBE_VIDEO.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$google$android$util$AbstractMessageParser$Token$Type[Token.Type.PHOTO.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$google$android$util$AbstractMessageParser$Token$Type[Token.Type.FLICKR.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
        }
    }

    protected static String reverse(String str) {
        StringBuilder sb = new StringBuilder();
        for (int length = str.length() - 1; length >= 0; length--) {
            sb.append(str.charAt(length));
        }
        return sb.toString();
    }

    public static class TrieNode {
        private final HashMap<Character, TrieNode> children;
        private String text;
        private String value;

        public TrieNode() {
            this("");
        }

        public TrieNode(String str) {
            this.children = new HashMap<>();
            this.text = str;
        }

        public final boolean exists() {
            return this.value != null;
        }

        public final String getText() {
            return this.text;
        }

        public final String getValue() {
            return this.value;
        }

        public void setValue(String str) {
            this.value = str;
        }

        public TrieNode getChild(char c) {
            return this.children.get(Character.valueOf(c));
        }

        public TrieNode getOrCreateChild(char c) {
            Character chValueOf = Character.valueOf(c);
            TrieNode trieNode = this.children.get(chValueOf);
            if (trieNode != null) {
                return trieNode;
            }
            TrieNode trieNode2 = new TrieNode(this.text + String.valueOf(c));
            this.children.put(chValueOf, trieNode2);
            return trieNode2;
        }

        public static void addToTrie(TrieNode trieNode, String str, String str2) {
            for (int i = 0; i < str.length(); i++) {
                trieNode = trieNode.getOrCreateChild(str.charAt(i));
            }
            trieNode.setValue(str2);
        }
    }

    private static boolean matches(TrieNode trieNode, String str) {
        int i = 0;
        while (i < str.length()) {
            int i2 = i + 1;
            trieNode = trieNode.getChild(str.charAt(i));
            if (trieNode == null) {
                break;
            }
            if (trieNode.exists()) {
                return true;
            }
            i = i2;
        }
        return false;
    }

    private static TrieNode longestMatch(TrieNode trieNode, AbstractMessageParser abstractMessageParser, int i) {
        return longestMatch(trieNode, abstractMessageParser, i, false);
    }

    private static TrieNode longestMatch(TrieNode trieNode, AbstractMessageParser abstractMessageParser, int i, boolean z) {
        TrieNode trieNode2 = null;
        while (i < abstractMessageParser.getRawText().length()) {
            int i2 = i + 1;
            trieNode = trieNode.getChild(abstractMessageParser.getRawText().charAt(i));
            if (trieNode == null) {
                break;
            }
            if (trieNode.exists() && (abstractMessageParser.isWordBreak(i2) || (z && abstractMessageParser.isSmileyBreak(i2)))) {
                trieNode2 = trieNode;
            }
            i = i2;
        }
        return trieNode2;
    }

    public static class Part {
        private String meText;
        private ArrayList<Token> tokens = new ArrayList<>();

        public String getType(boolean z) {
            StringBuilder sb = new StringBuilder();
            sb.append(z ? "s" : FullBackup.ROOT_TREE_TOKEN);
            sb.append(getPartType());
            return sb.toString();
        }

        private String getPartType() {
            if (isMedia()) {
                return "d";
            }
            return this.meText != null ? "m" : "";
        }

        public boolean isMedia() {
            return this.tokens.size() == 1 && this.tokens.get(0).isMedia();
        }

        public Token getMediaToken() {
            if (isMedia()) {
                return this.tokens.get(0);
            }
            return null;
        }

        public void add(Token token) {
            if (isMedia()) {
                throw new AssertionError("media ");
            }
            this.tokens.add(token);
        }

        public void setMeText(String str) {
            this.meText = str;
        }

        public String getRawText() {
            StringBuilder sb = new StringBuilder();
            String str = this.meText;
            if (str != null) {
                sb.append(str);
            }
            for (int i = 0; i < this.tokens.size(); i++) {
                sb.append(this.tokens.get(i).getRawText());
            }
            return sb.toString();
        }

        public ArrayList<Token> getTokens() {
            return this.tokens;
        }
    }
}
