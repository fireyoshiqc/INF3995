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

    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List entries = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "Rocket");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("GridContainer")) {
                entries.add(readGridContainer(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private GridContainer readGridContainer(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "GridContainer");
        List<Grid> list = new ArrayList<>();
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
        return new GridContainer(list);
    }

    private Grid readGrid(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Grid");
        TabContainer tabContainer = null;
        int row = Integer.parseInt(parser.getAttributeValue(null, "row"));
        int col = Integer.parseInt(parser.getAttributeValue(null, "col"));
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("TabContainer")) {
                tabContainer = readTabContainer(parser);
            } else {
                skip(parser);
            }
        }
        return new Grid(row, col, tabContainer);
    }

    private TabContainer readTabContainer(XmlPullParser parser) throws XmlPullParserException, IOException {
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
        return new TabContainer(list);
    }

    private Tab readTab(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Tab");
        TabbableWidget contents = null;
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
            if (name.equals("Tab")) {
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
            if (name.equals("Tab")) {
                list.add(readCAN(parser));
            } else {
                skip(parser);
            }
        }
        return new DisplayLogWidget(list);
    }

    private DualVWidget readDualVWidget(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "DualVWidget");
        List<TabbableWidget> list = new ArrayList<>();
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
                default:
                    skip(parser);
            }

            if (list.size() != 2) {
                //TODO: throw exceptions
            }

        }
        return new DualVWidget(list);
    }

    private DualHWidget readDualHWidget(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "DualHWidget");
        List<TabbableWidget> list = new ArrayList<>();
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
                default:
                    skip(parser);
            }

            if (list.size() != 2) {
                //TODO: throw exceptions
            }

        }
        return new DualHWidget(list);
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
        String plot_name = parser.getAttributeValue(null, "name");
        String unit = parser.getAttributeValue(null, "unit");
        String axis = parser.getAttributeValue(null, "axis");
        List<CAN> list = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("Tab")) {
                list.add(readCAN(parser));
            } else {
                skip(parser);
            }
        }
        return new Plot(plot_name, unit, axis, list);
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
