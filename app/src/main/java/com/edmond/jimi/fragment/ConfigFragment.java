package com.edmond.jimi.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.edmond.jimi.activity.MainActivity;
import com.edmond.jimi.util.PrefrenceTool;
import com.edmond.kangmei.R;

/**
 * Created by apple on 15/9/5.
 */
public class ConfigFragment extends PlaceholderFragment  {
    EditText txtTitle =null;
    EditText txtSalesman=null;
    EditText txtPhone=null;
    EditText txtDevPhone=null;

    public static ConfigFragment newInstance(int sectionNumber) {
        ConfigFragment fragment = new ConfigFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_config, container, false);


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        txtTitle = (EditText) getActivity().findViewById(R.id.txtCompany);
        txtTitle.setText(PrefrenceTool.getStringValue("kangmei", "apptitle",
                getActivity().getApplicationContext()));
        txtSalesman = (EditText) getActivity().findViewById(R.id.txtSalesMan);
        txtSalesman.setText(PrefrenceTool.getStringValue("kangmei", "salesman",
                getActivity().getApplicationContext()));
        txtPhone = (EditText) getActivity().findViewById(R.id.txtPhone);
        txtPhone.setText(PrefrenceTool.getStringValue("kangmei", "phone",
                getActivity().getApplicationContext()));
        txtDevPhone = (EditText) getActivity().findViewById(R.id.txtDevPhone);
        txtDevPhone.setText(PrefrenceTool.getStringValue("kangmei",
                "deliveryphone", getActivity().getApplicationContext()));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onDestroyView() {

        PrefrenceTool.saveValue("kangmei", "apptitle", txtTitle
                .getText().toString(), getActivity().getApplication());
        getActivity().setTitle(txtTitle.getText());

        PrefrenceTool.saveValue("kangmei", "salesman", txtSalesman.getText()
                .toString(), getActivity().getApplicationContext());
        PrefrenceTool.saveValue("kangmei", "phone", txtPhone.getText()
                .toString(), getActivity().getApplicationContext());
        PrefrenceTool.saveValue("kangmei", "deliveryphone", txtDevPhone
                .getText().toString(), getActivity().getApplicationContext());

        super.onDestroyView();
    }
}
