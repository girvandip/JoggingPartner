package com.example.batere3a.joggingpartner.order;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.batere3a.joggingpartner.R;

import java.util.LinkedList;


/**
 * A simple {@link Fragment} subclass.
 */
public class History extends Fragment {
    protected LinkedList<String> mWordList;
    protected RecyclerView recyclerView;
    protected HistoryAdapter historyAdapter;
    protected RecyclerView.LayoutManager layoutManager;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected History.LayoutManagerType mCurrentLayoutManagerType;

    public History() {
        mWordList = new LinkedList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_orders, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);

        layoutManager = new LinearLayoutManager(getActivity());

        // set layout manager
        mCurrentLayoutManagerType = History.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        // initialize the adapter with data from bundle
        historyAdapter = new HistoryAdapter(getArguments().getString("data"),
                getArguments().getString("username"));
        recyclerView.setAdapter(historyAdapter);

        // Inflate the layout for this fragment
        return rootView;
    }

    public void setRecyclerViewLayoutManager(History.LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                layoutManager = new GridLayoutManager(getActivity(), 2);
                mCurrentLayoutManagerType = History.LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                layoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = History.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                layoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = History.LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }
}
