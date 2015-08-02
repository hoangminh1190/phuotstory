package com.m2team.phuotstory.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.m2team.phuotstory.R;
import com.m2team.phuotstory.activity.WriteActivity;
import com.m2team.phuotstory.adapter.RecycleAdapter;
import com.m2team.phuotstory.common.Applog;
import com.m2team.phuotstory.common.Common;
import com.m2team.phuotstory.common.Constant;
import com.m2team.phuotstory.model.Story;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    @Bind(R.id.recycle_view)
    RecyclerView recyclerView;
    @Bind(R.id.floating_add)
    FloatingActionButton floatingActionButton;
    RecycleAdapter adapter;
    List<Story> stories;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        floatingActionButton.setIcon(R.drawable.ic_add_black_24dp);
        floatingActionButton.setColorNormal(Color.WHITE);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WriteActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(intent, Constant.REQ_CODE_ADD_STORY);
            }
        });
        stories = Common.queryStories(getActivity());
        adapter = new RecycleAdapter(getActivity(), stories);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Applog.d("result: " + resultCode);
        if (requestCode == Constant.REQ_CODE_ADD_STORY) {
            if (resultCode == getActivity().RESULT_OK) {
                stories = Common.queryStories(getActivity());
                adapter.updateDataChanged(stories);
                Toast.makeText(getActivity(), getString(R.string.success_add_story), Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(getActivity(), getString(R.string.fail_add_story), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
