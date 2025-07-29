package com.testapp.bluesky_api_test.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.Tag;
import com.testapp.bluesky_api_test.viewmodel.GroupEditViewModel;
import java.util.ArrayList;
import java.util.List;

public class TagSelectionDialogFragment extends DialogFragment {

    private static final String ARG_POST_ID = "postId";
    private int postId;
    private GroupEditViewModel groupEditViewModel;

    public interface TagSelectionListener {
        void onTagsSelected(int postId, List<Integer> selectedTagIds);
    }

    private TagSelectionListener listener;

    public static TagSelectionDialogFragment newInstance(int postId) {
        TagSelectionDialogFragment fragment = new TagSelectionDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POST_ID, postId);
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnAddNewTagRequestListener {
        void onAddNewTagRequest();
    }

    private OnAddNewTagRequestListener addNewTagRequestListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof TagSelectionListener) {
            listener = (TagSelectionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement TagSelectionListener");
        }
        if (context instanceof OnAddNewTagRequestListener) {
            addNewTagRequestListener = (OnAddNewTagRequestListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnAddNewTagRequestListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            postId = getArguments().getInt(ARG_POST_ID);
        }

        groupEditViewModel = new ViewModelProvider(requireActivity()).get(GroupEditViewModel.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Tags");

        // "Add New Tag" button
        builder.setNeutralButton("Add New Tag", (dialog, which) -> {
            if (addNewTagRequestListener != null) {
                addNewTagRequestListener.onAddNewTagRequest();
            }
        });

        groupEditViewModel.getAllTags().observe(this, tags -> {
            if (tags == null || tags.isEmpty()) {
                builder.setMessage("No tags available. Add one!")
                       .setPositiveButton("Close", (dialog, id) -> dismiss());
            } else {
                List<String> tagNames = new ArrayList<>();
                for (Tag tag : tags) {
                    tagNames.add(tag.getName());
                }
                boolean[] checkedItems = new boolean[tags.size()];
                ArrayList<Integer> selectedTagIds = new ArrayList<>();

                builder.setMultiChoiceItems(tagNames.toArray(new String[0]), checkedItems, (dialog, which, isChecked) -> {
                    if (isChecked) {
                        selectedTagIds.add(tags.get(which).getId());
                    } else {
                        selectedTagIds.remove(Integer.valueOf(tags.get(which).getId()));
                    }
                });

                builder.setPositiveButton("OK", (dialog, id) -> {
                    if (listener != null) {
                        listener.onTagsSelected(postId, selectedTagIds);
                    }
                });
                builder.setNegativeButton("Cancel", (dialog, id) -> dismiss());
            }
            // Re-create the dialog to reflect new changes
            if (getDialog() != null) {
                getDialog().dismiss();
            }
            builder.create().show();
        });

        return builder.create();
    }
}
