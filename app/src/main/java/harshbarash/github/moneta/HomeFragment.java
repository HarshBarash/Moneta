package harshbarash.github.moneta;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import harshbarash.github.monetaandroid.R;

//раздел главная
public class HomeFragment extends Fragment {
    ImageButton define;
    ImageButton collection;
    ImageButton search;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);



        define = v.findViewById(R.id.define);
        collection = v.findViewById(R.id.collection);
        search = v.findViewById(R.id.search);


        define.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireContext(), DefineActivity.class));

//                startActivity(Intent(requireContext(), DefineActivity::class.java));

            }
        });

        collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(getActivity(), CollectionActivity.class));
            }
        });
//
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CoinsActivity.class));
            }
        });

        return v;
    }
}
