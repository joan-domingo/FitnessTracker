package cat.xojan.fittracker.ui.menu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import cat.xojan.fittracker.R;

public class AttributionFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attribution, container, false);

        setHasOptionsMenu(true);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.fragment_attribution_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        WebView webView = (WebView) view.findViewById(R.id.fragment_attribution_webview);
        String firstAttribution = "<h1>App Icon</h1><a href=\"http://www.freepik.com/free-photos-vectors/design\">Design vector designed by Freepik</a>";
        String secondAttribution = "<h1>Sport Icons</h1><div>Icon made by <a href=\"http://www.icons8.com\" title=\"Icons8\">Icons8</a> from <a href=\"http://www.flaticon.com\" title=\"Flaticon\">www.flaticon.com</a> is licensed under <a href=\"http://creativecommons.org/licenses/by/3.0/\" title=\"Creative Commons BY 3.0\">CC BY 3.0</a></div>";
        String html = "<html><body>" + firstAttribution + secondAttribution + "</body></html>";
        String mime = "text/html";
        String encoding = "utf-8";
        webView.loadDataWithBaseURL(null, html, mime, encoding, null);

        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem settings = menu.findItem(R.id.action_settings);
        settings.setVisible(false);
        MenuItem delete = menu.findItem(R.id.action_delete);
        delete.setVisible(false);
        MenuItem attributions = menu.findItem(R.id.action_attributions);
        attributions.setVisible(false);
    }
}
