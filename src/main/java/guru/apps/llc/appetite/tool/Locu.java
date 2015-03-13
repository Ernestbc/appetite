package guru.apps.llc.appetite.tool;

import android.os.Looper;
import android.os.Handler;
import android.util.Log;
import android.util.JsonReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Streams venue objects.
 *
 * For more responsive loading, the following maintains a connection to the server
 * and returns venues upon request. Note this should be run in a thread.
 *
 * Created by jrpotter on 1/2/15.
 */
public class Locu extends Thread {

    // Private Instance Members
    // ==================================================

    // Tells whether more venues are left to read
    private JsonReader reader;

    // Used to make repeated requests to the server
    private String request;
    private HashMap<String, String> data;

    // Allow paging responses
    private String results_key;
    private boolean allVenuesLoaded;

    
    // Public Instance Members
    // ==================================================

    public Handler mHandler;
    public ArrayList<Venue> venues;


    // Public Static Members
    // ==================================================

    public static final int VENUE_LOAD_COUNT = 15;


    // Static JSON Fields
    // ==================================================

    public static final String VENUE_FIELD_LOCU_ID = "locu_id";
    public static final String VENUE_FIELD_NAME = "name";
    public static final String VENUE_FIELD_SHORT_NAME = "short_name";
    public static final String VENUE_FIELD_WEBSITE_URL = "website_url";
    public static final String VENUE_FIELD_DESCRIPTION = "description";
    public static final String VENUE_FIELD_EXTENDED = "extended";
    public static final String VENUE_FIELD_CONTACT = "contact";
    public static final String VENUE_FIELD_LOCATION = "location";
    public static final String VENUE_FIELD_MENUS = "menus";
    public static final String VENUE_FIELD_OPEN_HOURS = "open_hours";
    public static final String VENUE_FIELD_CATEGORIES = "categories";

    public static final String VENUE_EXTENDED_FIELD_PAYMENT_METHODS = "payment_methods";

    public static final String VENUE_CONTACT_FIELD_PHONE = "phone";
    public static final String VENUE_CONTACT_FIELD_EMAIL = "email";

    public static final String VENUE_LOCATION_FIELD_ADDRESS1 = "address1";
    public static final String VENUE_LOCATION_FIELD_ADDRESS2 = "address2";
    public static final String VENUE_LOCATION_FIELD_ADDRESS3 = "address3";
    public static final String VENUE_LOCATION_FIELD_LOCALITY = "locality";
    public static final String VENUE_LOCATION_FIELD_REGION = "region";
    public static final String VENUE_LOCATION_FIELD_POSTAL_CODE = "postal_code";
    public static final String VENUE_LOCATION_FIELD_COUNTRY = "country";
    public static final String VENUE_LOCATION_FIELD_GEO = "geo";
    public static final String VENUE_LOCATION_FIELD_COORDINATES = "coordinates";

    public static final String VENUE_MENU_FIELD_MENU_NAME = "menu_name";
    public static final String VENUE_MENU_FIELD_SECTIONS = "sections";
    public static final String VENUE_MENU_FIELD_CURRENCY_SYMBOL = "currency_symbol";

    public static final String VENUE_SECTION_FIELD_SECTION_NAME = "section_name";
    public static final String VENUE_SECTION_FIELD_SUBSECTIONS = "subsections";

    public static final String VENUE_SUBSECTION_FIELD_SUBSECTION_NAME = "subsection_name";
    public static final String VENUE_SUBSECTION_FIELD_CONTENTS = "contents";

    public static final String VENUE_CONTENT_FIELD_TYPE = "type";
    public static final String VENUE_SECTION_TEXT_FIELD_TEXT = "text";
    public static final String VENUE_ITEM_FIELD_NAME = "name";
    public static final String VENUE_ITEM_FIELD_DESCRIPTION = "description";
    public static final String VENUE_ITEM_FIELD_PRICE = "price";
    public static final String VENUE_ITEM_FIELD_OPTION_GROUPS = "option_groups";

    public static final String VENUE_OPTION_GROUP_FIELD_TYPE = "type";
    public static final String VENUE_OPTION_GROUP_FIELD_TEXT = "text";
    public static final String VENUE_OPTION_GROUP_FIELD_DESCRIPTION = "description";

    public static final String VENUE_OPTION_FIELD_NAME = "name";
    public static final String VENUE_OPTION_FIELD_PRICE = "price";

    public static final String VENUE_CATEGORY_FIELD_NAME = "name";
    public static final String VENUE_CATEGORY_FIELD_STR_ID = "str_id";


    // Constructor
    // ==================================================

    /**
     * Generates the thread object (though it must be started separately).
     * Note all venues are loaded when a search is conducted, so paging is
     * unnecessary.
     *
     * @param request ""
     * @param data ""
     */
    public Locu(String request, HashMap<String, String> data) {
        this.venues = new ArrayList<>();
        this.allVenuesLoaded = false;
        this.request = request;
        this.results_key = "";
        this.data = data;
    }


    // Looper Methods
    // ==================================================

    /**
     * We synchronize the start of the thread in case any calls are made
     * requesting data immediately.
     */
    @Override
    public void run() {
        Looper.prepare();
        synchronized (this) {
            mHandler = new Handler();
            notify();
        }

        Looper.loop();
    }

    /**
     * Simply close the JSON stream.
     */
    public void closeReader() {
        if(reader != null) {
            try {
                reader.close();
                reader = null;
            } catch (IOException e) {
                e.printStackTrace();
                // Log.e("LOCU", "Could not close connection (Locu)");
            }
        }
    }

    /**
     * Closes connection and stops thread.
     */
    public void terminate() {
        closeReader();
        mHandler.getLooper().quit();
    }


    // Batch Adapter Methods
    // ==================================================

    /**
     * Check whether pagination is complete.
     *
     * @return ""
     */
    public boolean loadedAllPages() {

        return allVenuesLoaded;
    }

    /**
     * Similar to the batch process, but queries another page if necessary.
     */
    public void loadNextPage() {
        if(!allVenuesLoaded) {
            data.put("results_key", results_key);
            paginateResponse();
        }
    }

    /**
     * Make a request to the server, initializing the JsonReader with
     * the URLConnection. Note this should be called before any notifications
     * outward in case any calls using the reader are made.
     *
     * Note if the venues count is empty, we assume pagination is complete.
     */
    private void paginateResponse() {
        BufferedReader br = SiteConnection.getBufferedResponse(request, data);
        try {
            if(br != null) {
                reader = new JsonReader(br);
                reader.beginObject();
                while(reader.hasNext()) {
                    String name = reader.nextName();
                    switch(name) {
                        case "next_results_key":
                            results_key = reader.nextString();
                            break;
                        case "venues":
                            loadVenues();
                            break;
                        default:
                            reader.skipValue();
                            break;
                    }
                }
                reader.endObject();
            } else {
                throw new IOException();
            }
        } catch (IOException e) {
            allVenuesLoaded = true;
            // Log.e("LOCU", "Invalid Response Returned");
            e.printStackTrace();
        }

        closeReader();
    }


    // Plural Reader Methods
    // ==================================================

    /**
     *
     * @return
     * @throws IOException
     */
    private ArrayList<VenueMenu> getVenueMenus() throws IOException {

        ArrayList<VenueMenu> menus = new ArrayList<>();

        reader.beginArray();
        while(reader.hasNext()) {
            menus.add(getVenueMenu());
        }
        reader.endArray();

        return menus;
    }

    /**
     *
     * @return
     * @throws IOException
     */
    private HashMap<String, ArrayList<String[]>> getVenueOpenHours() throws IOException {

        HashMap<String, ArrayList<String[]>> openHours = new HashMap<>();

        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();

            reader.beginArray();
            while(reader.hasNext()) {
                reader.beginArray();
                String opening = reader.nextString();
                String closing = reader.nextString();
                openHours.put(name, new ArrayList<String[]>());
                openHours.get(name).add(new String[]{opening, closing});
                reader.endArray();
            }
            reader.endArray();
        }
        reader.endObject();

        return openHours;
    }

    /**
     *
     * @return
     * @throws IOException
     */
    private ArrayList<VenueSection> getVenueSections() throws IOException {

        ArrayList<VenueSection> sections = new ArrayList<>();

        reader.beginArray();
        while(reader.hasNext()) {
            sections.add(getVenueSection());
        }
        reader.endArray();

        return sections;
    }

    /**
     *
     * @return
     * @throws IOException
     */
    private ArrayList<VenueSubsection> getVenueSubsections() throws IOException {

        ArrayList<VenueSubsection> subsections = new ArrayList<>();

        reader.beginArray();
        while(reader.hasNext()) {
            subsections.add(getVenueSubsection());
        }
        reader.endArray();

        return subsections;
    }

    /**
     *
     * @return
     * @throws IOException
     */
    private ArrayList<VenueContent> getVenueContents() throws IOException {

        ArrayList<VenueContent> contents = new ArrayList<>();

        reader.beginArray();
        while(reader.hasNext()) {
            contents.add(getVenueContent());
        }
        reader.endArray();

        return contents;
    }

    /**
     *
     * @return
     * @throws IOException
     */
    private ArrayList<VenueOptionGroup> getVenueOptionGroups() throws IOException {

        ArrayList<VenueOptionGroup> optionGroups = new ArrayList<>();

        reader.beginArray();
        while(reader.hasNext()) {
            optionGroups.add(getVenueOptionGroup());
        }
        reader.endArray();

        return optionGroups;

    }

    /**
     *
     * @return
     * @throws IOException
     */
    private ArrayList<VenueCategory> getVenueCategories() throws IOException {

        ArrayList<VenueCategory> categories = new ArrayList<>();

        reader.beginArray();
        while(reader.hasNext()) {
            categories.add(getVenueCategory());
        }
        reader.endArray();

        return categories;
    }


    // Reader Methods
    // ==================================================

    /**
     * Try to load a total all venues in current page, closing out when finished.
     */
    private void loadVenues() throws IOException {
        reader.beginArray();
        if(!reader.hasNext()) {
            allVenuesLoaded = true;
        } else {
            while (reader.hasNext()) {
                venues.add(getVenue());
            }
        }
        reader.endArray();
    }

    /**
     * Recursively builds up the Venue Object.
     * https://dev.locu.com/documentation/#venue
     *
     * @return "The venue object in question."
     */
    private Venue getVenue() {
        try {
            // Note we are currently in the list of venues as setup by the run method
            if (reader.hasNext()) {
                Venue venue = new Venue();
                reader.beginObject();
                while(reader.hasNext()) {
                    String name = reader.nextName();
                    switch(name) {
                        case VENUE_FIELD_LOCU_ID:
                            venue.locu_id = reader.nextString();
                            break;
                        case VENUE_FIELD_NAME:
                            venue.name = reader.nextString();
                            break;
                        case VENUE_FIELD_SHORT_NAME:
                            venue.short_name = reader.nextString();
                            break;
                        case VENUE_FIELD_DESCRIPTION:
                            venue.description = reader.nextString();
                            break;
                        case VENUE_FIELD_WEBSITE_URL:
                            venue.website_url = reader.nextString();
                            break;
                        case VENUE_FIELD_EXTENDED:
                            venue.extended = getVenueExtended();
                            break;
                        case VENUE_FIELD_CONTACT:
                            venue.contact = getVenueContact();
                            break;
                        case VENUE_FIELD_LOCATION:
                            venue.location = getVenueLocation();
                            break;
                        case VENUE_FIELD_MENUS:
                            venue.menus = getVenueMenus();
                            break;
                        case VENUE_FIELD_OPEN_HOURS:
                            venue.open_hours = getVenueOpenHours();
                            break;
                        case VENUE_FIELD_CATEGORIES:
                            venue.categories = getVenueCategories();
                            break;
                        default:
                            reader.skipValue();
                            break;
                    }
                }
                reader.endObject();
                return venue;
            } else {
                terminate();
            }
        } catch (IOException e) {
            // Log.e("JSON", "IO Exception (Locu)");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Contains information on how to contact a venue.
     * https://dev.locu.com/documentation/#contact
     *
     * @return "The VenueContact in question."
     * @throws IOException
     */
    private VenueContact getVenueContact() throws IOException {

        VenueContact contact = new VenueContact();

        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            switch(name) {
                case VENUE_CONTACT_FIELD_PHONE:
                    contact.phone = reader.nextString();
                    break;
                case VENUE_CONTACT_FIELD_EMAIL:
                    contact.email = reader.nextString();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();

        return contact;
    }

    /**
     * Contains information on how to get to the venue.
     * https://dev.locu.com/documentation/#location
     *
     * @return "The VenueLocation in question."
     * @throws IOException
     */
    private VenueLocation getVenueLocation() throws IOException {

        VenueLocation location = new VenueLocation();

        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            switch(name) {
                case VENUE_LOCATION_FIELD_ADDRESS1:
                    location.address1 = reader.nextString();
                    break;
                case VENUE_LOCATION_FIELD_ADDRESS2:
                    location.address2 = reader.nextString();
                    break;
                case VENUE_LOCATION_FIELD_ADDRESS3:
                    location.address3 = reader.nextString();
                    break;
                case VENUE_LOCATION_FIELD_LOCALITY:
                    location.locality = reader.nextString();
                    break;
                case VENUE_LOCATION_FIELD_POSTAL_CODE:
                    location.postal_code = reader.nextString();
                    break;
                case VENUE_LOCATION_FIELD_REGION:
                    location.region = reader.nextString();
                    break;
                case VENUE_LOCATION_FIELD_COUNTRY:
                    location.country = reader.nextString();
                    break;
                case VENUE_LOCATION_FIELD_GEO:
                    reader.beginObject();
                    while(reader.hasNext()) {
                        String name2 = reader.nextName();
                        switch(name2) {
                            case VENUE_LOCATION_FIELD_COORDINATES:
                                reader.beginArray();
                                location.longitude = reader.nextDouble();
                                location.latitude = reader.nextDouble();
                                reader.endArray();
                                break;
                            default:
                                reader.skipValue();
                                break;
                        }
                    }
                    reader.endObject();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();

        return location;
    }

    /**
     * Returns information on the menu of the venue.
     * https://dev.locu.com/documentation/#menu
     *
     * @return "The VenueMenu in question."
     * @throws IOException
     */
    private VenueMenu getVenueMenu() throws IOException {

        VenueMenu menu = new VenueMenu();

        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            switch(name) {
                case VENUE_MENU_FIELD_MENU_NAME:
                    menu.menu_name = reader.nextString();
                    break;
                case VENUE_MENU_FIELD_CURRENCY_SYMBOL:
                    menu.currency_symbol = reader.nextString();
                    break;
                case VENUE_MENU_FIELD_SECTIONS:
                    menu.sections = getVenueSections();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();

        return menu;
    }

    /**
     * Contains information on a part of the menu.
     * https://dev.locu.com/documentation/#section
     *
     * @return "The VenueSection" in question."
     * @throws IOException
     */
    private VenueSection getVenueSection() throws IOException {

        VenueSection section = new VenueSection();

        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            switch(name) {
                case VENUE_SECTION_FIELD_SECTION_NAME:
                    section.section_name = reader.nextString();
                    break;
                case VENUE_SECTION_FIELD_SUBSECTIONS:
                    section.subsections = getVenueSubsections();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();

        return section;
    }

    /**
     * Contains information on a part of a section.
     * https://dev.locu.com/documentation/#subsection
     *
     * @return "The VenueSubsection in question."
     * @throws IOException
     */
    private VenueSubsection getVenueSubsection() throws IOException {

        VenueSubsection subsection = new VenueSubsection();

        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            switch(name) {
                case VENUE_SUBSECTION_FIELD_SUBSECTION_NAME:
                    subsection.subsection_name = reader.nextString();
                    break;
                case VENUE_SUBSECTION_FIELD_CONTENTS:
                    subsection.contents = getVenueContents();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();

        return subsection;
    }

    /**
     * Contains information on the dishes provided by the venue.
     * https://dev.locu.com/documentation/#sectiontext
     * https://dev.locu.com/documentation/#item
     *
     * Since TYPE isn't necessarily first, we preload the items and then create the proper object.
     *
     * @return "The VenueContent in question."
     * @throws IOException
     */
    private VenueContent getVenueContent() throws IOException {

        String type = "";
        HashMap<String, String> data = new HashMap<>();
        ArrayList<VenueOptionGroup> option_groups = null;

        // Load in object
        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            switch(name) {
                case VENUE_CONTENT_FIELD_TYPE:
                    type = reader.nextString();
                    break;
                case VENUE_SECTION_TEXT_FIELD_TEXT:
                case VENUE_ITEM_FIELD_NAME:
                case VENUE_ITEM_FIELD_DESCRIPTION:
                case VENUE_ITEM_FIELD_PRICE:
                    data.put(name, reader.nextString());
                    break;
                case VENUE_ITEM_FIELD_OPTION_GROUPS:
                    option_groups = getVenueOptionGroups();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();

        // Create correct object after iterating data map
        VenueContent content;

        // Load in SECTION_TEXT values
        if(type.equals(VenueContent.SECTION_TEXT)) {
            VenueSectionText sectionText = new VenueSectionText();
            sectionText.text = data.get(VENUE_SECTION_TEXT_FIELD_TEXT);
            content = sectionText;

        // Load in ITEM values
        } else if(type.equals(VenueContent.ITEM)) {
            VenueItem item = new VenueItem();
            item.name = data.get(VENUE_ITEM_FIELD_NAME);
            item.description = data.get(VENUE_ITEM_FIELD_DESCRIPTION);
            item.price = data.get(VENUE_ITEM_FIELD_PRICE);
            item.option_groups = option_groups;
            content = item;

        // There are only two types possible
        } else {
            throw new IOException("Invalid VenueContent Type (Locu)");
        }

        content.type = type;
        return content;
    }

    /**
     * Contains information about options provided by the venue (a certain element).
     * This will either be choosing an item, or adding an item.
     *
     * https://dev.locu.com/documentation/#optiongroup
     *
     * @return "The VenueOptionGroup in question."
     * @throws IOException
     */
    private VenueOptionGroup getVenueOptionGroup() throws IOException {

        VenueOptionGroup optionGroup = new VenueOptionGroup();

        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            switch(name) {
                case VENUE_OPTION_GROUP_FIELD_TYPE:
                    optionGroup.type = reader.nextString();
                    break;
                case VENUE_OPTION_GROUP_FIELD_TEXT:
                    optionGroup.text = reader.nextString();
                    break;
                case VENUE_OPTION_GROUP_FIELD_DESCRIPTION:
                    optionGroup.description = new ArrayList<>();
                    reader.beginArray();
                    while(reader.hasNext()) {
                        optionGroup.description.add(getVenueOption());
                    }
                    reader.endArray();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();

        return optionGroup;
    }

    /**
     * Contains information about an option the venue provides.
     * https://dev.locu.com/documentation/#option
     *
     * @return "The VenueOption in question."
     * @throws IOException
     */
    private VenueOption getVenueOption() throws IOException {

        VenueOption option = new VenueOption();

        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            switch(name) {
                case VENUE_OPTION_FIELD_NAME:
                    option.name = reader.nextString();
                    break;
                case VENUE_OPTION_FIELD_PRICE:
                    option.price = reader.nextString();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();

        return option;
    }

    /**
     * Contains information on how the venue is categorized.
     * https://dev.locu.com/documentation/#category
     *
     * @return "The VenueCategory in question."
     * @throws IOException
     */
    private VenueCategory getVenueCategory() throws IOException {

        VenueCategory category = new VenueCategory();

        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            switch(name) {
                case VENUE_CATEGORY_FIELD_NAME:
                    category.name = reader.nextString();
                    break;
                case VENUE_CATEGORY_FIELD_STR_ID:
                    category.str_id = reader.nextString();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();

        return category;
    }

    /**
     * Contains additional information about the venue.
     * https://dev.locu.com/documentation/#extended
     *
     * @return "Extended information in question."
     * @throws IOException
     */
    private VenueExtended getVenueExtended() throws IOException {
        VenueExtended extended = new VenueExtended();

        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            switch(name) {
                case VENUE_EXTENDED_FIELD_PAYMENT_METHODS:
                    extended.payment_methods = new HashMap<>();
                    reader.beginObject();
                    while(reader.hasNext()) {
                        String name2 = reader.nextName();
                        extended.payment_methods.put(name2, reader.nextBoolean());
                    }
                    reader.endObject();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();

        return extended;
    }


    // Venue Objects
    // ==================================================
    // These objects match the Locu API, and have fields corresponding to said docs

    /**
     * POD of venue.
     *
     * https://dev.locu.com/documentation/#venue
     */
    public class Venue {
        public String locu_id;
        public String name;
        public String short_name;
        public String description;
        public String website_url;
        public VenueContact contact;
        public VenueLocation location;
        public ArrayList<VenueMenu> menus;
        public HashMap<String, ArrayList<String[]>> open_hours;
        public ArrayList<VenueCategory> categories;
        public VenueExtended extended;
    }

    /**
     * Basic contact information.
     * We skip fields like fax and multiple phones/emails for now.
     *
     * https://dev.locu.com/documentation/#contact
     */
    public class VenueContact {
        public String phone;
        public String email;
    }

    /**
     * We abstract away from the GeoJSON object as specified in the docs.
     * Instead, we directly set the latitude and longitude here.
     *
     * https://dev.locu.com/documentation/#location
     */
    public class VenueLocation {
        public String address1;
        public String address2;
        public String address3;
        public String locality;
        public String region;
        public String postal_code;
        public String country;
        public double latitude;
        public double longitude;
    }

    /**
     * POD of menu (there can be more than one).
     *
     * https://dev.locu.com/documentation/#menu
     */
    public class VenueMenu {
        public String menu_name;
        public String currency_symbol;
        public ArrayList<VenueSection> sections;
    }

    /**
     * POD of part of menu.
     *
     * https://dev.locu.com/documentation/#section
     */
    public class VenueSection {
        public String section_name;
        public ArrayList<VenueSubsection> subsections;
    }

    /**
     * POD of part of section.
     *
     * https://dev.locu.com/documentation/#subsection
     */
    public class VenueSubsection {
        public String subsection_name;
        public ArrayList<VenueContent> contents;
    }

    /**
     * The contents in a subsection can be an item or a section text.
     * This class allows for this ambiguity.
     *
     * https://dev.locu.com/documentation/#sectiontext
     * https://dev.locu.com/documentation/#item
     */
    public class VenueContent {
        public static final String SECTION_TEXT = "SECTION_TEXT";
        public static final String ITEM = "ITEM";
        public String type;
    }

    /**
     * Contains description of a section.
     *
     * https://dev.locu.com/documentation/#sectiontext
     */
    public class VenueSectionText extends VenueContent {
        public String text;
    }

    /**
     * Contains information of a specific item.
     *
     * https://dev.locu.com/documentation/#item
     */
    public class VenueItem extends VenueContent {
        public String name;
        public String description;
        public String price;
        public ArrayList<VenueOptionGroup> option_groups;
    }

    /**
     * Contains information regarding a group of options.
     *
     * https://dev.locu.com/documentation/#optiongroup
     */
    public class VenueOptionGroup {

        // Possible values for type
        public static final String OPTION_ADD = "OPTION_ADD";
        public static final String OPTION_CHOOSE = "OPTION_CHOOSE";

        public String type;
        public String text;
        public ArrayList<VenueOption> description;

    }

    /**
     * POD of an option.
     *
     * https://dev.locu.com/documentation/#option
     */
    public class VenueOption {
        public String name;
        public String price;
    }

    /**
     * POD of a category.
     *
     * https://dev.locu.com/documentation/#category
     */
    public class VenueCategory {
        public String name;
        public String str_id;
    }

    /**
     * Additional members describing a venue.
     * Most of these are expected to be null;
     *
     * https://dev.locu.com/documentation/#extended
     */
    public class VenueExtended {
        public HashMap<String, Boolean> payment_methods;
    }

}