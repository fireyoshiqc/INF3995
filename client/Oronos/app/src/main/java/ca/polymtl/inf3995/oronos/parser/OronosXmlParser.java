package ca.polymtl.inf3995.oronos.parser;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felix on 15/févr./2018.
 */

public class OronosXmlParser {
    private static final String ns = null;

    public Rocket parse(InputStream in) throws XmlPullParserException, IOException, UnsupportedContainerWidgetException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readRocket(parser);
        } finally {
            in.close();
        }
    }

    private Rocket readRocket(XmlPullParser parser) throws XmlPullParserException, IOException, UnsupportedContainerWidgetException {
        List<ContainableWidget> entries = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, ns, "Rocket");
        String name = parser.getAttributeValue(null, "name");
        String id = parser.getAttributeValue(null, "id");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tag = parser.getName();
            // Starts by looking for the entry tag
            if (tag.equals("GridContainer")) {
                entries.addAll(readGridContainer(parser));
            } else {
                skip(parser);
            }
        }
        return new Rocket(name, id, entries);
    }

    private List<ContainableWidget> readGridContainer(XmlPullParser parser) throws XmlPullParserException, IOException, UnsupportedContainerWidgetException {
        parser.require(XmlPullParser.START_TAG, ns, "GridContainer");
        List<ContainableWidget> list = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("Grid")) {
                list.add(readGrid(parser));
            } else {
                skip(parser);
            }
        }
        return list;
    }

    private ContainableWidget readGrid(XmlPullParser parser) throws XmlPullParserException, IOException, UnsupportedContainerWidgetException {
        parser.require(XmlPullParser.START_TAG, ns, "Grid");
        ContainableWidget contents = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("TabContainer")) {
                contents = readTabContainer(parser);
            } else {
                skip(parser);
            }
        }
        return contents;
    }

    private ContainableWidget readTabContainer(XmlPullParser parser) throws XmlPullParserException, IOException, UnsupportedContainerWidgetException {
        parser.require(XmlPullParser.START_TAG, ns, "TabContainer");
        List<Tab> list = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("Tab")) {
                list.add(readTab(parser));
            } else {
                skip(parser);
            }
        }
        TabContainer tabContainer = new TabContainer(list);
        return tabContainer.cleanup();
    }

    private Tab readTab(XmlPullParser parser) throws XmlPullParserException, IOException, UnsupportedContainerWidgetException {
        parser.require(XmlPullParser.START_TAG, ns, "Tab");
        ContainableWidget contents = null;
        String tabName = parser.getAttributeValue(null, "name");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "DataDisplayer":
                    contents = readDataDisplayer(parser);
                    break;
                case "DisplayLogWidget":
                    contents = readDisplayLogWidget(parser);
                    break;
                case "DualVWidget":
                    contents = readDualVWidget(parser);
                    break;
                case "DualHWidget":
                    contents = readDualHWidget(parser);
                    break;
                case "FindMe":
                    contents = readFindMe(parser);
                    break;
                case "Map":
                    contents = readMap(parser);
                    break;
                case "Modulestatus":
                    contents = readModuleStatus(parser);
                    break;
                case "Plot":
                    contents = readPlot(parser);
                    break;
                case "TabContainer":
                    contents = readTabContainer(parser);
                    break;
                default:
                    skip(parser);
            }
        }
        return new Tab(tabName, contents);
    }

    private DataDisplayer readDataDisplayer(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "DataDisplayer");
        List<CAN> list = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("CAN")) {
                list.add(readCAN(parser));
            } else {
                skip(parser);
            }
        }
        return new DataDisplayer(list);
    }

    private DisplayLogWidget readDisplayLogWidget(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "DisplayLogWidget");
        List<CAN> list = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("CAN")) {
                list.add(readCAN(parser));
            } else {
                skip(parser);
            }
        }
        return new DisplayLogWidget(list);
    }

    private ContainableWidget readDualVWidget(XmlPullParser parser) throws XmlPullParserException, IOException, UnsupportedContainerWidgetException {
        parser.require(XmlPullParser.START_TAG, ns, "DualVWidget");
        int startLine = parser.getLineNumber();
        List<ContainableWidget> list = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "DataDisplayer":
                    list.add(readDataDisplayer(parser));
                    break;
                case "DisplayLogWidget":
                    list.add(readDisplayLogWidget(parser));
                    break;
                case "FindMe":
                    list.add(readFindMe(parser));
                    break;
                case "Map":
                    list.add(readMap(parser));
                    break;
                case "Modulestatus":
                    list.add(readModuleStatus(parser));
                    break;
                case "Plot":
                    list.add(readPlot(parser));
                    break;
                case "ButtonArray":
                case "RadioStatus":
                case "CustomCANSender":
                    list.add(new UnsupportedWidget());
                    break;
                default:
                    skip(parser);
            }
        }
        if (list.size() != 2) {
            throw new UnsupportedContainerWidgetException("DualVWidget, à la ligne " + startLine);
        }
        DualVWidget dualV = new DualVWidget(list);
        return dualV.cleanup();
    }

    private ContainableWidget readDualHWidget(XmlPullParser parser) throws XmlPullParserException, IOException, UnsupportedContainerWidgetException {
        parser.require(XmlPullParser.START_TAG, ns, "DualHWidget");
        int startLine = parser.getLineNumber();
        List<ContainableWidget> list = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "DataDisplayer":
                    list.add(readDataDisplayer(parser));
                    break;
                case "DisplayLogWidget":
                    list.add(readDisplayLogWidget(parser));
                    break;
                case "FindMe":
                    list.add(readFindMe(parser));
                    break;
                case "Map":
                    list.add(readMap(parser));
                    break;
                case "Modulestatus":
                    list.add(readModuleStatus(parser));
                    break;
                case "Plot":
                    list.add(readPlot(parser));
                    break;
                case "ButtonArray":
                case "RadioStatus":
                case "CustomCANSender":
                    list.add(readUnsupportedWidget(parser));
                    break;
                default:
                    skip(parser);
            }
        }
        if (list.size() != 2) {
            throw new UnsupportedContainerWidgetException("DualHWidget, à la ligne " + startLine);
        }
        DualHWidget dualH = new DualHWidget(list);
        return dualH.cleanup();
    }

    private FindMe readFindMe(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "FindMe");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            skip(parser);
        }
        return new FindMe();
    }

    private Map readMap(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Map");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            skip(parser);
        }
        return new Map();
    }

    private ModuleStatus readModuleStatus(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Modulestatus");
        int nGrid = Integer.parseInt(parser.getAttributeValue(null, "nGrid"));
        int nColumns = Integer.parseInt(parser.getAttributeValue(null, "nColumns"));
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            skip(parser);
        }
        return new ModuleStatus(nGrid, nColumns);
    }

    private Plot readPlot(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Plot");
        String plotName = parser.getAttributeValue(null, "name");
        String unit = parser.getAttributeValue(null, "unit");
        String axis = parser.getAttributeValue(null, "axis");
        List<CAN> list = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("CAN")) {
                list.add(readCAN(parser));
            } else {
                skip(parser);
            }
        }
        return new Plot(plotName, unit, axis, list);
    }

    private CAN readCAN(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "CAN");
        String id = parser.getAttributeValue(null, "id");
        String canName = parser.getAttributeValue(null, "name");
        String display = parser.getAttributeValue(null, "display");
        String minAcceptable = parser.getAttributeValue(null, "minAcceptable");
        String maxAcceptable = parser.getAttributeValue(null, "maxAcceptable");
        String chiffresSign = parser.getAttributeValue(null, "chiffresSign");
        String specificSource = parser.getAttributeValue(null, "specificSource");
        String serialNb = parser.getAttributeValue(null, "serialNb");
        String customUpdate = parser.getAttributeValue(null, "customUpdate");
        String updateEach = parser.getAttributeValue(null, "updateEach");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            skip(parser);
        }
        return new CAN(id, canName, display, minAcceptable, maxAcceptable, chiffresSign, specificSource, serialNb, customUpdate, updateEach);
    }

    private UnsupportedWidget readUnsupportedWidget(XmlPullParser parser) throws XmlPullParserException, IOException {
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            skip(parser);
        }
        return new UnsupportedWidget();
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
