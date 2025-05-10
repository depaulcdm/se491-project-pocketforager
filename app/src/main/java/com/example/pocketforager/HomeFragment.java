package com.example.pocketforager;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class HomeFragment extends Fragment {
    public HomeFragment() { super(R.layout.fragment_home); }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        view.findViewById(R.id.toDetailsBtn).setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_home_to_details)
        );
    }
}
