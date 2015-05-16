package cat.xojan.fittracker.main.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.wearable.activity.ConfirmationActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import cat.xojan.fittracker.R;
import cat.xojan.fittracker.service.UtilityService;

public class OpenAppFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.open_app_fragment, container, false);

        ImageButton onAppButton = (ImageButton) view.findViewById(R.id.open_on_app_button);
        onAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityService.startDeviceActivity(getActivity(), UtilityService.LAUNCH_HANDHELD_APP);
                Intent intent = new Intent(getActivity(), ConfirmationActivity.class);
                intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                        ConfirmationActivity.OPEN_ON_PHONE_ANIMATION);
                startActivity(intent);
            }
        });

        return view;
    }
}
