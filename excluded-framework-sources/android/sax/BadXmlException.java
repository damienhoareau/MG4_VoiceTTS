package android.sax;

import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

/* JADX INFO: loaded from: classes2.dex */
class BadXmlException extends SAXParseException {
    public BadXmlException(String str, Locator locator) {
        super(str, locator);
    }

    @Override // org.xml.sax.SAXException, java.lang.Throwable
    public String getMessage() {
        return "Line " + getLineNumber() + ": " + super.getMessage();
    }
}
