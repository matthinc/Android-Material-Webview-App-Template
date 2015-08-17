package net.matthi.webview;

import com.example.matthias.webview.R;

import java.net.URI;

/**
 * Created by Matthias on 11.08.2015.
 */
public enum Pages {

    //---------------- Edit here ----------------

    //ID            menu    title               tabs        URL(s)                                          offline URL(s)              Color               Icon            Refresh Button

    Home        (   false, "Home",              null,       "http://www.matthi.net/wv_test/home.html",       null,                      Color.CYAN_DARK,     Icon.HOME,     false),
    Newsfeed    (   false, "News",              null,       "http://www.matthi.net/wv_test/newsfeed.html",   null,                      Color.CYAN_DARK,     Icon.LIST,     true),
    Team        (   false, "Company",           "Team||CEO","http://www.matthi.net/wv_test/team.html||http://www.matthi.net/wv_test/ceo.html" ,null,Color.CYAN_DARK,Icon.PEOPLE,   false),
    Offline     (   false, "Offline page",      null,       null,                                            "offline.html",            Color.PURPLE_DARK,   Icon.DOWNLOAD, false),
    Contact     (   false, "Contact",           null,       "http://www.matthi.net/wv_test/contact.html",    "contact.html",            Color.CYAN_DARK,     Icon.PERSON,   false),
    Imprint     (   false, "Imprint",           null,       "http://www.matthi.net/wv_test/imprint.html",     null,                     Color.GRAY_LIGHT,    Icon.TEXT,     false),
    Imprint2    (   true,  "Imprint",           null,       "http://www.matthi.net/wv_test/imprint.html",     null,                     Color.GRAY_LIGHT,    null,          false),
    Mail        (   true,  "Send me an E-mail", null,       "mailto:test@fakemail.com",                       null,                     Color.CYAN_DARK,     null,          false);



    //White Design
    public boolean useWhiteFont = false;

    //URLs included in your app
    public String homeDomains = "";

    //Error message
    public String error = "<center> The page couldn't be loaded </center>";

    //Share
    public boolean share = true;
    public boolean shareAsAction = false;
    public String shareText = "Look at this! http://www.example.org/";

    //Misc
    public String localEncoding = "UTF-8";

    //Ads
    public boolean useAds = false;
    // Enter your unit id at /res/values/strings.xml -> banner_ad_unit_id


    //---------------- Do not edit ----------------
    //
    //
    //
    //


    Pages(boolean menu, String title, String tabs, String url, String localPage,  Color actionBarColor, Icon icon, boolean refreshButton) {
        this.menu = menu;
        this.title = title;
        this.url = url;
        this.actionBarColor = actionBarColor;
        this.icon = icon;

        //Domain to homeDomains
        if (url != null){
           if (!url.contains("||")){
               homeDomains += ","+ URI.create(url).getHost();
           }else{
               for (String s:url.split("\\|\\|")){
                   System.out.println(s);
                   homeDomains += ","+ URI.create(s).getHost();
               }
           }
        }

        this.tabs = tabs;
        this.refreshButton = refreshButton;
        this.localPage = localPage;
    }


    public boolean menu;
    public String title;
    public String url;
    public Color actionBarColor;
    public Icon icon;
    public boolean refreshButton;
    public String localPage;
    public String tabs;


    public String actualURL;
    public String actualAlternativeURL;


    //All Colors

    // NAME     (0xARGB);

    public static enum Color {
        RED             (0xFFDD3333),
        GREEN           (0xFF33DD33),
        BLUE            (0xFF4444DD),
        YELLOW          (0xFFDDDD11),
        CYAN_DARK       (0xFF0088FF),
        CYAN_LIGHT      (0xFF00DDFF),
        GRAY_LIGHT      (0xFFBBBBBB),
        GRAY_DARK       (0xFF555555),
        PURPLE_DARK     (0xFFBB00BB),
        PURPLE_LIGHT    (0xFFFF11FF);

        public int value;

        Color(int value){
            this.value = value;
        }
    }

    //All Icons

    //NAME      (R.drawable.<imageName>)

    public static enum Icon {
        HOME        (R.drawable.ic_home_black_48dp),
        DOWNLOAD    (R.drawable.ic_get_app_black_48dp),
        STAR        (R.drawable.ic_grade_black_48dp),
        QUESTION    (R.drawable.ic_help_black_48dp),
        INTERNET    (R.drawable.ic_language_black_48dp),
        LIST        (R.drawable.ic_list_black_48dp),
        PERSON      (R.drawable.ic_perm_identity_black_48dp),
        MAP         (R.drawable.ic_room_black_48dp),
        SETTINGS    (R.drawable.ic_settings_black_48dp),
        SHOP        (R.drawable.ic_shop_black_48dp),
        TEXT        (R.drawable.ic_subject_black_48dp),
        MOVIE       (R.drawable.ic_theaters_black_48dp),
        CALENDAR    (R.drawable.ic_today_black_48dp),
        GALLERY     (R.drawable.ic_view_module_black_48dp),
        EYE         (R.drawable.ic_visibility_black_48dp),
        WORK        (R.drawable.ic_work_black_48dp),
        PEOPLE      (R.drawable.ic_people_outline_black_36dp);


        public int value;

        Icon(int value){
            this.value = value;
        }
    }
}
