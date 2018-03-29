package ca.polymtl.inf3995.oronos.services;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ca.polymtl.inf3995.oronos.widgets.containers.DualWidget;
import ca.polymtl.inf3995.oronos.widgets.containers.Rocket;
import ca.polymtl.inf3995.oronos.widgets.containers.Tab;
import ca.polymtl.inf3995.oronos.widgets.containers.TabContainer;
import ca.polymtl.inf3995.oronos.widgets.containers.UnsupportedContainerWidgetException;
import ca.polymtl.inf3995.oronos.widgets.views.CAN;
import ca.polymtl.inf3995.oronos.widgets.views.DataDisplayer;
import ca.polymtl.inf3995.oronos.widgets.views.DisplayLogWidget;
import ca.polymtl.inf3995.oronos.widgets.views.FindMe;
import ca.polymtl.inf3995.oronos.widgets.views.MapTag;
import ca.polymtl.inf3995.oronos.widgets.views.ModuleStatus;
import ca.polymtl.inf3995.oronos.widgets.views.OronosView;
import ca.polymtl.inf3995.oronos.widgets.views.Plot;
import ca.polymtl.inf3995.oronos.widgets.views.UnsupportedWidget;
import timber.log.Timber;

/**
 * Created by Felix on 15/févr./2018.
 */

public class OronosXmlParser {
    private static final String ns = null;
    private Context context;

    public OronosXmlParser(Context context) {
        this.context = context;
    }

    public Rocket parse(InputStream in) {

        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            Rocket rocket = readRocket(parser);
            if (rocket.getList() == null || rocket.getList().isEmpty()) {
                Timber.e("XML file seems empty. Maybe you're missing a closing tag somewhere?");
            }
            return rocket;
        } catch (XmlPullParserException e) {
            Timber.e("There is an issue with the XML file. Maybe you're missing a closing tag somewhere? Exception message :\n" +
            e.getMessage());

        } catch (IOException e) {
            Timber.e("There was an issue while reading the XML file. Exception message :\n" +
                    e.getMessage());

        } catch (UnsupportedContainerWidgetException e) {
            Timber.e(e.getMessage());
        } finally  {
            try {
                in.close();
            } catch (IOException e) {
                Timber.e("There was an issue while reading the XML file. Exception message :\n" +
                        e.getMessage());
            }
        }
        return null;
    }

    private Rocket readRocket(XmlPullParser parser) throws XmlPullParserException, IOException, UnsupportedContainerWidgetException {
        List<OronosView> entries = new ArrayList<>();
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
                Timber.w(String.format("Skipping unknown or misplaced tag '<%s>'. Did you forget a closing tag somewhere?", tag));
                skip(parser);
            }
        }
        return new Rocket(context, name, id, entries);
    }

    private List<OronosView> readGridContainer(XmlPullParser parser) throws XmlPullParserException, IOException, UnsupportedContainerWidgetException {
        parser.require(XmlPullParser.START_TAG, ns, "GridContainer");
        List<OronosView> list = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("Grid")) {
                list.add(readGrid(parser));
            } else {
                Timber.w(String.format("Skipping unknown or misplaced tag '<%s>'. Did you forget a closing tag somewhere?", name));
                skip(parser);
            }
        }
        return list;
    }

    private OronosView readGrid(XmlPullParser parser) throws XmlPullParserException, IOException, UnsupportedContainerWidgetException {
        parser.require(XmlPullParser.START_TAG, ns, "Grid");
        OronosView contents = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "DataDisplayer":
                    contents = readDataDisplayer(parser, DataDisplayer.DataLayout.FULL);
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
                    Timber.w(String.format("Skipping unknown or misplaced tag '<%s>'. Did you forget a closing tag somewhere?", name));
                    skip(parser);
            }
        }
        return contents;
    }

    private OronosView readTabContainer(XmlPullParser parser) throws XmlPullParserException, IOException, UnsupportedContainerWidgetException {
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
                Timber.w(String.format("Skipping unknown or misplaced tag '<%s>'. Did you forget a closing tag somewhere?", name));
                skip(parser);
            }
        }
        return new TabContainer(context, list).cleanup();
    }

    private Tab readTab(XmlPullParser parser) throws XmlPullParserException, IOException, UnsupportedContainerWidgetException {
        parser.require(XmlPullParser.START_TAG, ns, "Tab");
        OronosView contents = null;
        String tabName = parser.getAttributeValue(null, "name");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "DataDisplayer":
                    contents = readDataDisplayer(parser, DataDisplayer.DataLayout.FULL);
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
                    Timber.w(String.format("Skipping unknown or misplaced tag '<%s>'. Did you forget a closing tag somewhere?", name));
                    skip(parser);
            }
        }
        return new Tab(tabName, contents);
    }

    private DataDisplayer readDataDisplayer(XmlPullParser parser, DataDisplayer.DataLayout layout) throws XmlPullParserException, IOException {
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
                Timber.w(String.format("Skipping unknown or misplaced tag '<%s>'. Did you forget a closing tag somewhere?", name));
                skip(parser);
            }
        }
        return new DataDisplayer(context, list, layout);
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
                Timber.w(String.format("Skipping unknown or misplaced tag '<%s>'. Did you forget a closing tag somewhere?", name));
                skip(parser);
            }
        }
        return new DisplayLogWidget(context, list);
    }

    private OronosView readDualVWidget(XmlPullParser parser) throws XmlPullParserException, IOException, UnsupportedContainerWidgetException {
        parser.require(XmlPullParser.START_TAG, ns, "DualVWidget");
        int startLine = parser.getLineNumber();
        List<OronosView> list = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "TabContainer":
                    list.add(readTabContainer(parser));
                    break;
                case "DualVWidget":
                    list.add(readDualVWidget(parser));
                    break;
                case "DualHWidget":
                    list.add(readDualHWidget(parser));
                    break;
                case "DataDisplayer":
                    list.add(readDataDisplayer(parser, DataDisplayer.DataLayout.HORIZONTAL));
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
                    list.add(new UnsupportedWidget(context));
                    break;
                default:
                    Timber.w(String.format("Skipping unknown or misplaced tag '<%s>'. Did you forget a closing tag somewhere?", name));
                    skip(parser);
            }
        }
        if (list.size() != 2) {
            throw new UnsupportedContainerWidgetException("DualVWidget, à la ligne " + startLine);
        }
        DualWidget dualV = new DualWidget(context, list, DualWidget.DualWidgetOrientation.VERTICAL);
        return dualV.cleanup();
    }

    private OronosView readDualHWidget(XmlPullParser parser) throws XmlPullParserException, IOException, UnsupportedContainerWidgetException {
        parser.require(XmlPullParser.START_TAG, ns, "DualHWidget");
        int startLine = parser.getLineNumber();
        List<OronosView> list = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "TabContainer":
                    list.add(readTabContainer(parser));
                    break;
                case "DualVWidget":
                    list.add(readDualVWidget(parser));
                    break;
                case "DualHWidget":
                    list.add(readDualHWidget(parser));
                    break;
                case "DataDisplayer":
                    list.add(readDataDisplayer(parser, DataDisplayer.DataLayout.VERTICAL));
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
                    Timber.w(String.format("Skipping unknown or misplaced tag '<%s>'. Did you forget a closing tag somewhere?", name));
                    skip(parser);
            }
        }
        if (list.size() != 2) {
            throw new UnsupportedContainerWidgetException("DualHWidget, à la ligne " + startLine);
        }
        DualWidget dualH = new DualWidget(context, list, DualWidget.DualWidgetOrientation.HORIZONTAL);
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
        return new FindMe(context);
    }

    private MapTag readMap(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Map");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            skip(parser);
        }
        return new MapTag(context);
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
        return new ModuleStatus(context, nGrid, nColumns);
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
                Timber.w(String.format("Skipping unknown or misplaced tag '<%s>'. Did you forget a closing tag somewhere?", name));
                skip(parser);
            }
        }
        return new Plot(context, plotName, unit, axis, list);
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
        String customUpdateParam = parser.getAttributeValue(null, "customUpdateParam");
        String updateEach = parser.getAttributeValue(null, "updateEach");
        String customAcceptable = parser.getAttributeValue(null, "customAcceptable");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            skip(parser);
        }
        return new CAN(id, canName, display, minAcceptable, maxAcceptable, chiffresSign,
                specificSource, serialNb, customUpdate, customUpdateParam, updateEach,
                customAcceptable);
    }

    private UnsupportedWidget readUnsupportedWidget(XmlPullParser parser) throws XmlPullParserException, IOException {
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            skip(parser);
        }
        return new UnsupportedWidget(context);
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
