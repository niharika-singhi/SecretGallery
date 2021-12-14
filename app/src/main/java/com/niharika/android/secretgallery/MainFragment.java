package com.niharika.android.secretgallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    private String mEmailId, mCode;
    View view;
    private TextInputEditText mEmailView, mCodeView;
    private MaterialButton mNextButton, mForgetButton;
    private int layout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!readUserInfo())
            layout = R.layout.first_time_user;
        else
            layout = R.layout.fragment_main;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(layout, container, false);
        hideActionBar();
        addButtonListener();
        return view;
    }

    private void hideActionBar() {
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }
    }

    private void showActionBar() {
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }
    }

    private void addButtonListener() {
        mNextButton = view.findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                if (layout == R.layout.first_time_user)
                    readNewUserInput();
                else
                    checkUser();
            }
        });
        if (layout == R.layout.fragment_main) {
            mForgetButton = view.findViewById(R.id.forget_button);
            mForgetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mailMsg = getString(R.string.mailBody_1) + mCode + getString(R.string.mailBody_2);
                    new SendEmailTask().execute(mEmailId, mailMsg, getString(R.string.subject));
                    showSnackBarMsg(getString(R.string.email_sent_msg));
                }
            });
        }
    }

    private boolean checkError(String email, String code) {
        if (email != null)
            if (TextUtils.isEmpty(email)) {
                ((TextInputLayout) view.findViewById(R.id.email_input)).setError("Enter Email id");
                showSnackBarMsg(getString(R.string.email_empty));
                return false;
            }
        if (TextUtils.isEmpty(code)) {
            ((TextInputEditText) view.findViewById(R.id.code)).setError("Enter Code");
            showSnackBarMsg(getString(R.string.code_empty));
            return false;
        }
        if (email != null)
            if (Patterns.EMAIL_ADDRESS.matcher(code).matches()) {
                ((TextInputLayout) view.findViewById(R.id.email_input)).setError("Email address is invalid");
                showSnackBarMsg(getString(R.string.invalid_email));
                return false;
            }
        return true;
    }

    private void hideKeyboard(View v) {
        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void showSnackBarMsg(String msg) {
        Snackbar snackbar = Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG);
        snackbar.setTextColor(getResources().getColor((R.color.SG_textColor_primary)));
        snackbar.setBackgroundTint(getResources().getColor(R.color.SG_light_yellow));
        snackbar.show();
    }

    private void checkUser() {
        mCodeView = view.findViewById(R.id.code);
        String code = mCodeView.getText().toString();
        checkError(null, code);
        if (TextUtils.equals(mCode, code)) {
            Navigation.findNavController(view).navigate(R.id.pagerFragment);
            showActionBar();
        }
    }

    private void readNewUserInput() {
        mEmailView = view.findViewById(R.id.email);
        String emailId = mEmailView.getText().toString();
        mCodeView = view.findViewById(R.id.code);
        String code = mCodeView.getText().toString();
        if (!checkError(emailId, code))
            return;
        writeUserInfo(emailId, code);
        Navigation.findNavController(view).navigate(R.id.pagerFragment);
        showActionBar();
    }

    private boolean readUserInfo() {
        SharedPreferences sharedPref = getActivity().getPreferences(getContext().MODE_PRIVATE);
        mEmailId = sharedPref.getString(getString(R.string.EmailId), "");
        mCode = sharedPref.getString(getString(R.string.code), "");
        if (TextUtils.isEmpty(mEmailId) && TextUtils.isEmpty(mCode))
            return false;
        return true;
    }

    private void writeUserInfo(String email, String code) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.EmailId), email);
        editor.putString(getString(R.string.code), code);
        editor.apply();
    }
}
