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
 * <h1>Oronos Xml Parser</h1>
 * This parser is specifically targeting Oronos rocket xml files. Each tag in the application will
 * be created only if it exists in the xml, and according to the specifications of the xml.
 *
 * @author Félix Boulet, Patrick Richer St-Onge
 * @version 0.0
 * @since 2018-04-12
 **/
public class OronosXmlParser {
    private static final String ns = null;
    private Context context;

    /**
     * Constructor requesting the context
     *
     * @param context to which all the tags will be added.
     */
    public OronosXmlParser(Context context) {
        this.context = context;
    }

    /**
     * This method parse an InputStream that must correspond to an Oronos xml file and returns a
     * Rocket that has a name, a number and a list of widgets.
     *
     * @param in Oronos xml file.
     * @return Rocket
     */
    public Rocket parse(InputStream in) {

        XmlPullParser parser = Xml.newPullParser();
        Rocket rocket = null;
        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            rocket = readRocket(parser);
            if (rocket.getList() == null || rocket.getList().isEmpty()) {
                Timber.e("XML file seems empty. Maybe you're missing a closing tag somewhere?");
            }
        } catch (XmlPullParserException e) {
            Timber.e("There is an issue with the XML file. Maybe you're missing a closing tag somewhere? Exception message :\n" +
                    e.getMessage());

        } catch (IOException e) {
            Timber.e("There was an issue while reading the XML file. Exception message :\n" +
                    e.getMessage());

        } catch (UnsupportedContainerWidgetException e) {
            Timber.e(e.getMessage());
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                Timber.e("There was an issue while reading the XML file. Exception message :\n" +
                        e.getMessage());
            }
        }
        return rocket;
    }

    /**
     * This method is reading the information related to the tag Rocket.
     *
     * @param parser a correctly configured XmlPullParser
     */
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

    /**
     * This method is reading the information related to the tag GridContainer.
     *
     * @param parser a correctly configured XmlPullParser
     */
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

    /**
     * This method is reading the information related to the tag Grid.
     *
     * @param parser a correctly configured XmlPullParser
     */
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

    /**
     * This method is reading the information related to the tag TabContainer.
     *
     * @param parser a correctly configured XmlPullParser
     */
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

    /**
     * This method is reading the information related to the tag Tab.
     *
     * @param parser a correctly configured XmlPullParser
     */
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

    /**
     * This method is reading the information related to the tag DataDisplayer.
     *
     * @param parser a correctly configured XmlPullParser
     */
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

    /**
     * This method is reading the information related to the tag DisplayLogWidget.
     *
     * @param parser a correctly configured XmlPullParser
     */
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

    /**
     * This method is reading the information related to the tag DualVWidget.
     *
     * @param parser a correctly configured XmlPullParser
     */
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

    /**
     * This method is reading the information related to the tag DualHWidget.
     *
     * @param parser a correctly configured XmlPullParser
     */
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

    /**
     * This method is reading the information related to the tag FindMe.
     *
     * @param parser a correctly configured XmlPullParser
     */
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

    /**
     * This method is reading the information related to the tag Map.
     *
     * @param parser a correctly configured XmlPullParser
     */
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

    /**
     * This method is reading the information related to the tag ModuleStatus.
     *
     * @param parser a correctly configured XmlPullParser
     */
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

    /**
     * This method is reading the information related to the tag Plot.
     *
     * @param parser a correctly configured XmlPullParser
     */
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

    /**
     * This method is reading the information related to the tag CAN.
     *
     * @param parser a correctly configured XmlPullParser
     */
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

    /**
     * This method is skipping an unknown tag.
     *
     * @param parser a correctly configured XmlPullParser
     */
    private UnsupportedWidget readUnsupportedWidget(XmlPullParser parser) throws XmlPullParserException, IOException {
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            skip(parser);
        }
        return new UnsupportedWidget(context);
    }

    /**
     * This method skips an unknown tag.
     *
     * @param parser a correctly configured XmlPullParser
     */
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
