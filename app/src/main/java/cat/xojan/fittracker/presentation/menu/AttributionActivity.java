package cat.xojan.fittracker.presentation.menu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

import cat.xojan.fittracker.R;

public class AttributionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_attribution);
        Toolbar toolbar = (Toolbar) findViewById(R.id.fragment_attribution_toolbar);
        toolbar.setTitle(R.string.attributions);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left_white_24dp);


        WebView webView = (WebView) findViewById(R.id.fragment_attribution_webview);
        String firstAttribution = "<h1>App Icon</h1><a href=\"http://www.freepik.com/free-photos-vectors/design\">Design vector designed by Freepik</a>";
        String secondAttribution = "<h1>Sport Icons</h1><div>Icon made by <a href=\"http://www.icons8.com\" title=\"Icons8\">Icons8</a> from <a href=\"http://www.flaticon.com\" title=\"Flaticon\">www.flaticon.com</a> is licensed under <a href=\"http://creativecommons.org/licenses/by/3.0/\" title=\"Creative Commons BY 3.0\">CC BY 3.0</a></div>";
        String html = "<html><body>" + firstAttribution + secondAttribution + "</body></html>";
        String mime = "text/html";
        String encoding = "utf-8";
        webView.loadDataWithBaseURL(null, html, mime, encoding, null);
    }
}
