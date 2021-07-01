package harshbarash.github.monetaru;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;

public class ChatFragment extends Fragment {

    DatabaseReference profileRef;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    RecyclerView recyclerView;
    EditText searchEt;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_chat, container, false);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        searchEt = requireActivity().findViewById(R.id.search_userch);
        recyclerView = getActivity().findViewById(R.id.rv_ch);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        profileRef = database.getReference("All Users");

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String query = searchEt.getText().toString().toUpperCase();
                Query search = profileRef.orderByChild("name").startAt(query).endAt(query + "\uf0ff");

                FirebaseRecyclerOptions<AllUserMember> options1 =
                        new FirebaseRecyclerOptions.Builder<AllUserMember>()
                                .setQuery(search, AllUserMember.class)
                                .build();

                FirebaseRecyclerAdapter<AllUserMember, ProfileViewholder> firebaseRecyclerAdapter1 =
                        new FirebaseRecyclerAdapter<AllUserMember, ProfileViewholder>(options1) {
                            @Override
                            protected void onBindViewHolder(@NonNull ProfileViewholder holder, int position, @NonNull AllUserMember model) {


                                final String postkey = getRef(position).getKey();

                                holder.setProfileInchat(requireActivity().getApplication(), model.getName(), model.getUid(), model.getBio(), model.getUrl());


                                String name = getItem(position).getName();
                                String url = getItem(position).getUrl();
                                String uid = getItem(position).getUid();


                                holder.sendmessagebtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getActivity(), MessageActivity.class);
                                        intent.putExtra("n", name);
                                        intent.putExtra("u", url);
                                        intent.putExtra("uid", uid);
                                        startActivity(intent);
                                    }
                                });

                            }

                            @NonNull
                            @Override
                            public ProfileViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.chat_profile_item, parent, false);

                                return new ProfileViewholder(view);
                            }
                        };

                firebaseRecyclerAdapter1.startListening();
                recyclerView.setAdapter(firebaseRecyclerAdapter1);

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();


        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {


            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);

            }
        };
        TedPermission.with(requireActivity())
                .setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();


        FirebaseRecyclerOptions<AllUserMember> options1 =
                new FirebaseRecyclerOptions.Builder<AllUserMember>()
                        .setQuery(profileRef, AllUserMember.class)
                        .build();

        FirebaseRecyclerAdapter<AllUserMember, ProfileViewholder> firebaseRecyclerAdapter1 =
                new FirebaseRecyclerAdapter<AllUserMember, ProfileViewholder>(options1) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProfileViewholder holder, int position, @NonNull AllUserMember model) {


                        final String postkey = getRef(position).getKey();

                        holder.setProfileInchat(requireActivity().getApplication(), model.getName(), model.getUid(), model.getBio(), model.getUrl());


                        String name = getItem(position).getName();
                        String url = getItem(position).getUrl();
                        String uid = getItem(position).getUid();


                        holder.sendmessagebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(), MessageActivity.class);
                                intent.putExtra("n", name);
                                intent.putExtra("u", url);
                                intent.putExtra("uid", uid);
                                startActivity(intent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ProfileViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.chat_profile_item, parent, false);

                        return new ProfileViewholder(view);
                    }
                };

        firebaseRecyclerAdapter1.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter1);


    }

}