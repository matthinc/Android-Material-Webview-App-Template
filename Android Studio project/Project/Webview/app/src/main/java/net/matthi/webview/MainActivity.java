package net.matthi.webview;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.matthias.webview.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    //The application toolbar
    Toolbar toolbar;

    //The drawer list
    ListView list;

    //The main content display
    WebView webView;

    //The drawer menu
    DrawerLayout drawerLayout;

    //The refresh button
    MenuItem refreshButton;

    //Tabs
    TabLayout tabs;

    //The actual displayed page
    static Pages actual = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
         Views
         */
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        list = (ListView)findViewById(R.id.listView);
        webView = (WebView) findViewById(R.id.webView);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        tabs = (TabLayout) findViewById(R.id.tabs);

        /*
         Tabs
         */
        //Tabs are invisible
        hideTabs();

        //Listener to receive tab changes
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            //User selected new tab
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                //Find page
                for (Pages p: Pages.values()){
                    if (p.title.equals(actual.title)){

                        //Load URL in webview
                        if (actual.url != null)
                            actual.actualURL = p.url.split("\\|\\|")[tab.getPosition()];
                        if (actual.localPage != null)
                            actual.actualAlternativeURL = actual.localPage.split("\\|\\|")[tab.getPosition()];
                    }

                }
                if (actual.url != null) {
                    webView.loadUrl(actual.actualURL);
                }else{
                    if (actual.localPage !=null)
                        webView.loadUrl("file:///android_asset/" + actual.actualAlternativeURL);
                }
            }

            //We don't need this methods
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        /*
         Drawer items
         */

        //Create list of all items for the drawer
        final ArrayList<Pages> pages = new ArrayList<Pages>();
        for (Pages p: Pages.values()){
            if (p.menu == false)
                pages.add(p);
        }

        //Set actual page to the first item
        if (actual == null){
            actual = pages.get(0);
        }

         /*
         White design
         */
        if (actual.useWhiteFont){
            toolbar.setTitleTextColor(0xFFFFFFFF);
            tabs.setTabTextColors(0xFFFFFFFF, 0xFFFFFFFF);
        }

        /*
         Toolbar
         */
        toolbar.setTitle(actual.title);
        setSupportActionBar(toolbar);

        /*
         Admob
         */
        AdView mAdView = (AdView) findViewById(R.id.adView);

        //Hide AdView when not needed
        if (!actual.useAds){
            mAdView.setVisibility(View.INVISIBLE);
        }else{
            //Init AdView when needed
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }

        /*
         Drawer
         */
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,  drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.setDrawerListener(mDrawerToggle);
        drawerLayout.setDrawerShadow(R.drawable.shadow, Gravity.LEFT);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();

        /*
         Pages to drawer
         */

        //Add pages to the drawer
        ListAdapter adapter = new ListAdapter(this, pages );
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        /*
         Webview
         */
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {


            //Handle URL loading
            //Handle Intents (e.g. mailto:)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if ((url.contains("http:") || url.contains("https:")) && actual.homeDomains.contains(URI.create(url).getHost())) {
                    view.loadUrl(url);
                    return false;
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url));
                    PackageManager manager = getPackageManager();
                    List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
                    if (infos.size() > 0) {
                        startActivity(intent);
                    }
                    return true;
                }
            }

            //Needed for correct back-button functionality
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.clearHistory();
                super.onPageFinished(view, url);
            }

            //Handle loading errors
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (actual.actualAlternativeURL !=null)
                    webView.loadUrl("file:///android_asset/"+actual.actualAlternativeURL);
                else
                     view.loadData(actual.error, "text/html", actual.localEncoding);
            }
        });

        /*
         Site listener
         */

        //Change page via drawer
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changePage(pages.get(position));
            }
        });

        /*
         Update
         */

        //Load actual (first) page
        changePage(actual);
    }

    /*
     Tabs
     */
    //Show the TabView
    void showTabs(){
        tabs.getLayoutParams().height = TabLayout.LayoutParams.WRAP_CONTENT;
    }

    //Hide the TabView
    void hideTabs(){
        tabs.removeAllTabs();
        tabs.getLayoutParams().height = 0;
    }


    //Change the page
    //Update Webview, Toolbar, Refresh button...
    void changePage(Pages newPage){
        actual = newPage;

        //Handle tabs
        tabs.removeAllTabs();
        if (newPage.tabs != null && newPage.tabs.contains("||")){
            showTabs();
            for (String s : newPage.tabs.split("\\|\\|")){
               tabs.addTab(tabs.newTab().setText(s));
            }
           if (newPage.url != null) {
               newPage.actualURL = newPage.url.split("\\|\\|")[0];
           }
            if (newPage.localPage != null) {
                newPage.actualAlternativeURL = newPage.localPage.split("\\|\\|")[0];
            }

            //There are no tabs...
        }else{
            hideTabs();
            newPage.actualURL = newPage.url;
            newPage.actualAlternativeURL = newPage.localPage;
        }


        //Enable JS for the webview
        webView.getSettings().setJavaScriptEnabled(true);

        //Load URL to the webview...
        if (newPage.url != null) {
            webView.loadUrl(newPage.actualURL);
        }else{
            if (newPage.localPage !=null) //...Or use local page
                webView.loadUrl("file:///android_asset/" + newPage.actualAlternativeURL);
        }

        //Update Toolbar and Statusbar
        toolbar.setBackgroundColor(newPage.actionBarColor.value);
        drawerLayout.setBackgroundColor(newPage.actionBarColor.value);
        drawerLayout.closeDrawers();
        toolbar.setTitle(newPage.title);

        //Show refresh button
        if (refreshButton!= null) {
            if (newPage.refreshButton) {
                refreshButton.setVisible(true);
            } else {
                refreshButton.setVisible(false);
            }
        }

        actual = newPage;
    }

    //Create OptionsMenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Create the menu
        getMenuInflater().inflate(R.menu.main, menu);

        //Add pages to the menu
        for (Pages p: Pages.values()){
            if (p.menu != false)
                menu.add(p.title);
        }

        //Refresh button
        refreshButton = menu.add(R.string.refresh);
        if (actual.useWhiteFont) {
            refreshButton.setIcon(R.drawable.ic_autorenew_white_36dp);
        }else {
            refreshButton.setIcon(R.drawable.ic_autorenew_black_36dp);
        }
        refreshButton.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        //Share button
        if (Pages.values()[0].share){
            MenuItem shareItem = menu.add(getString(R.string.share));
            if (actual.shareAsAction){
                if (actual.useWhiteFont)
                    shareItem.setIcon(R.drawable.ic_share_white_36dp);
                else
                    shareItem.setIcon(R.drawable.ic_share_black_36dp);
                shareItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            }
        }

        //Show/hide refresh button for the first page
        if (actual != null) {
            if (actual.refreshButton) {
                refreshButton.setVisible(true);
            } else {
                refreshButton.setVisible(false);
            }
        }
        return true;
    }


    //Handle back-button
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        }else {
            //super.onBackPressed();    //Don't close app via back-button
        }
    }


    //Handle menu-clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Share
        if (item.getTitle().toString().equals(getString(R.string.share))){
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Pages.values()[0].shareText);
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
            startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_using)));
            return super.onOptionsItemSelected(item);
        }

        //Refresh
        if (item.getTitle().toString().equals(getString(R.string.refresh ))){
            if (actual.url != null){
                System.out.println(actual.url);
                webView.reload();
                webView.loadUrl(actual.url);
            }
            return super.onOptionsItemSelected(item);
        }

        //Page
        for (Pages p: Pages.values()){
            if (p.title.equals(item.getTitle().toString())){
               if (p.url.contains("http:")||p.url.contains("http:")) {
                   changePage(p);
               }else{

                   //Handle special Intents (e.g. for mailto: in the menu)

                   Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(p.url));
                   PackageManager manager = getPackageManager();
                   List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
                   if (infos.size() > 0) {
                       startActivity(intent);
                   }


               }

            }
        }
        return super.onOptionsItemSelected(item);
    }

}
