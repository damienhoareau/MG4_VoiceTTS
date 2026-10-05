package com.alibaba.fastjson.support.geo;

import android.app.Instrumentation;
import com.alibaba.fastjson.annotation.JSONType;
import java.util.LinkedHashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes3.dex */
@JSONType(orders = {"type", Instrumentation.REPORT_KEY_IDENTIFIER, "bbox", "coordinates", "properties"}, typeName = "Feature")
public class Feature extends Geometry {
    private Geometry geometry;
    private String id;
    private Map<String, String> properties;

    public Feature() {
        super("Feature");
        this.properties = new LinkedHashMap();
    }

    public Geometry getGeometry() {
        return this.geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String, String> map) {
        this.properties = map;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String str) {
        this.id = str;
    }
}
