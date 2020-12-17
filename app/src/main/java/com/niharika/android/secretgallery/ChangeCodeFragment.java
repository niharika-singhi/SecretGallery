package com.niharika.android.secretgallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChangeCodeFragment extends Fragment {
    private EditText mOldCode,mNewCode,mConfirmCode,mSaveCode;
    private TextView mEmailView;
    private MaterialButton mSaveButton;
    private String mCode;
    private String mEmailId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_change_code, container, false);
        mOldCode=view.findViewById(R.id.oldCode);
        mNewCode=view.findViewById(R.id.newCode);
        mConfirmCode=view.findViewById(R.id.confirmCode);
        mSaveButton=view.findViewById(R.id.save_button);
        mEmailView=view.findViewById(R.id.email_label);
        readUserInfo();
        mEmailView.setText("Email Id:  "+mEmailId);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                saveCode();
            }
        });
        return view;
    }

    private void saveCode() {
        String oldCode=mOldCode.getText().toString();
        String newCode=mNewCode.getText().toString();
        String confirmCode=mConfirmCode.getText().toString();
        readUserInfo();
        if(TextUtils.equals(newCode,confirmCode)) {
            if (check(oldCode)) {
                writeUserInfo(newCode);
                Navigation.findNavController(getView()).navigate(R.id.pagerFragment);
            } else
                showSnackBarMsg(getString(R.string.wrongCodeMsg));
        }
        else
            showSnackBarMsg(getString(R.string.codesNotMatch));
    }

    private void showSnackBarMsg(String msg) {
        Snackbar snackbar=Snackbar.make(getView(),msg,Snackbar.LENGTH_LONG);
        snackbar.setTextColor(getResources().getColor((R.color.SG_textColor_primary)));
        snackbar.setBackgroundTint(getResources().getColor(R.color.SG_light_purple));
        snackbar.show();
    }

    private boolean check(String oldCode) {
        if(TextUtils.equals(oldCode,mCode))
            return true;
        else
            return false;
    }
    private void readUserInfo() {
        SharedPreferences sharedPref = getActivity().getPreferences(getContext().MODE_PRIVATE);
        mEmailId  = sharedPref.getString(getString(R.string.EmailId),"");
        mCode= sharedPref.getString(getString(R.string.code), "");
    }

    private void writeUserInfo(String newCode){
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.code), newCode);
        editor.apply();
    }

    private void hideKeyboard(View v) {
        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
