package com.ens.timezer0.ui.today;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ens.timezer0.NavigActivity;
import com.ens.timezer0.R;
import com.ens.timezer0.chat.Contact;
import com.ens.timezer0.chat.setting;
import com.ens.timezer0.models.modelutilisateur;
import com.ens.timezer0.utils.SharedPref;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TodayFragment extends Fragment {

    private TodayViewModel todayViewModel;
    TextView emailtxt , usernametxt ;
    CircleImageView image;
    MaterialButton btnsetting, btncontact, btngroupe;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        todayViewModel =
                ViewModelProviders.of(this).get(TodayViewModel.class);
        View view = inflater.inflate(R.layout.fragment_today, container, false);
        usernametxt =view.findViewById(R.id.utilisateur_acceuil);
        emailtxt = view.findViewById(R.id.emailacceuil);
        btncontact = view.findViewById(R.id.contactacceuil);
        btncontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Contact.class);
              //  intent.putExtra("client_id",NavigActivity.clientID);
                startActivity(intent);
            }
        });
        btnsetting =view.findViewById(R.id.btnsettingacceuil);
        btnsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), setting.class);
                //  intent.putExtra("client_id",NavigActivity.clientID);
                startActivity(intent);
            }
        });

        image = view.findViewById(R.id.imageacceuil);

        final TextView textView = view.findViewById(R.id.bnjour);
        todayViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        final String userid = SharedPref.readSharedSetting(getActivity(), "userid", "0");
        final String token = SharedPref.readSharedSetting(getActivity(), "token", "0");
        todayViewModel.getUser(userid,token).observe(getViewLifecycleOwner(), new Observer<modelutilisateur>() {
            @Override
            public void onChanged(modelutilisateur utilisateurModels) {
             //  adapter = new CustomAdapterRec(reclamationModels);

                usernametxt.setText(utilisateurModels.getUsername());
                emailtxt.setText(utilisateurModels.getEmail());
                Glide.with(getActivity().getApplicationContext()).load(utilisateurModels.getImageurl()).into(image);

                SharedPref.saveSharedSetting(getActivity(), "username",utilisateurModels.getUsername());
                SharedPref.saveSharedSetting(getActivity(), "email",utilisateurModels.getEmail());
                SharedPref.saveSharedSetting(getActivity(), "image_profile",utilisateurModels.getImageurl());

                SharedPref.saveSharedSetting(getActivity(), "firstName",utilisateurModels.getFirstName());
                SharedPref.saveSharedSetting(getActivity(), "lastName",utilisateurModels.getLastName());
                SharedPref.saveSharedSetting(getActivity(), "telephone",utilisateurModels.getTelephone());
             //   ((NavigActivity) getActivity()).updateDrawer();
                NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                View h = navigationView.getHeaderView(0);
                final String prof = SharedPref.readSharedSetting(getActivity(), "image_profile", "0");
                ImageView img = h.findViewById(R.id.imageViewpro);
                TextView us = h.findViewById(R.id.nomnav);
                TextView emailtxt = h.findViewById(R.id.emailnav);
                Glide.with(getActivity().getApplicationContext()).load(prof)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true).into(img);
                final String email = SharedPref.readSharedSetting(getActivity(), "email", "saad@gmail.com");
                final String username = SharedPref.readSharedSetting(getActivity(), "username", "application");

                us.setText(username);
                emailtxt.setText(email);
             //   listView.setAdapter(adapter);

            }


        });

        return view;
    }
}
