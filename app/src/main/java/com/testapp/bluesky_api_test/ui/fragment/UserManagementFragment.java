package com.testapp.bluesky_api_test.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.testapp.bluesky_api_test.R;
import com.testapp.bluesky_api_test.repository.AuthRepository;
import com.testapp.bluesky_api_test.ui.LoginActivity;

public class UserManagementFragment extends Fragment {

    private AuthRepository authRepository;
    private TextView tvUserHandle;
    private TextView tvUserDid;
    private Button btnLogout;

    public UserManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authRepository = new AuthRepository(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_management, container, false);

        tvUserHandle = view.findViewById(R.id.tv_user_handle);
        tvUserDid = view.findViewById(R.id.tv_user_did);
        btnLogout = view.findViewById(R.id.btn_logout);

        // ユーザー情報を表示
        tvUserHandle.setText("Handle: " + authRepository.getHandle());
        tvUserDid.setText("DID: " + authRepository.getDid());

        // ログアウトボタンのクリックリスナー
        btnLogout.setOnClickListener(v -> {
            authRepository.logout();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        });

        return view;
    }
}