package androidsamples.java.tictactoe;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import androidsamples.java.tictactoe.model.UserViewModel;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private UserViewModel userVM;

    private EditText emailEditText;
    private EditText passwdEditText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userVM = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // Handle the back press by adding a confirmation dialog
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d(TAG, "Back pressed");
                Navigation.findNavController(requireActivity(),R.id.nav_host_fragment).popBackStack();
                Navigation.findNavController(requireActivity(),R.id.nav_host_fragment).popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        emailEditText = view.findViewById(R.id.edit_email);
        passwdEditText = view.findViewById(R.id.edit_password);

        userVM.getCurrentUserId().observe(getViewLifecycleOwner(), userId -> {
            Toast.makeText(requireActivity(), userVM.getMessage(), Toast.LENGTH_SHORT).show();
            if(!"".equals(userId)) {
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).popBackStack();
            }
        });

        view.findViewById(R.id.btn_log_in).setOnClickListener(this::onClickRegisterOrSignIn);

        return view;
    }

    public void onClickRegisterOrSignIn(View view) {

        String emailId = emailEditText.getText().toString();
        String passwd = passwdEditText.getText().toString();

        if(!"".equals(emailId) && !"".equals(passwd)) {
            userVM.signInOrRegister(emailId, passwd);
        }
    }

    // No options menu in login fragment.
}