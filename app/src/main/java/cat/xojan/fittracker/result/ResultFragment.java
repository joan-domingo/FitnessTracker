package cat.xojan.fittracker.result;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import cat.xojan.fittracker.R;
import cat.xojan.fittracker.googlefit.FitnessController;
import cat.xojan.fittracker.session.SessionListFragment;

public class ResultFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        Button save = (Button) view.findViewById(R.id.result_button_save);
        Button exit = (Button) view.findViewById(R.id.result_button_exit);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FitnessController.getInstance().saveSession(getActivity().getSupportFragmentManager());
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMain();
            }
        });

        return view;
    }

    private void goToMain() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new SessionListFragment())
                .commit();
    }
}
